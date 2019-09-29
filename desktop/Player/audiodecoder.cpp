#include <QDebug>

#include "audiodecoder.h"

/* Minimum SDL audio buffer size, in samples. */
#define SDL_AUDIO_MIN_BUFFER_SIZE 512
/* Calculate actual buffer size keeping in mind not cause too frequent audio callbacks */
#define SDL_AUDIO_MAX_CALLBACKS_PER_SEC 30

AudioDecoder::AudioDecoder(QObject *parent):
    QObject(parent), isStop(false), isPause(false), isReadFinished(false),
    totalTime(0), clock(0), volume(SDL_MIX_MAXVOLUME), audioDeviceFormat(AUDIO_F32SYS),
    aCovertCtx(NULL), sendReturn(0)
{

}

int AudioDecoder::openAudio(AVFormatContext *pFormatCtx, int index)
{
    AVCodec *codec;
    SDL_AudioSpec wantedSpec;
    int wantedNbChannels;
    const char *env;

    /*  soundtrack array use to adjust */
    int nextNbChannels[]={0,0,1,6,2,6,4,6};
    int nextSampleRates[]={0, 44100, 48000, 96000, 192000};
    int nextSampleRateIndex= FF_ARRAY_ELEMS(nextSampleRates)-1;

    isStop=false;
    isPause=false;
    isReadFinished=false;

    audioSrcFmt=AV_SAMPLE_FMT_NONE;
    audioSrcChannelLayout=0;
    audioSrcFreq=0;

    pFormatCtx->streams[index]->discard=AVDISCARD_DEFAULT;

    stream=pFormatCtx->streams[index];

    codecCtx=avcodec_alloc_context3(NULL);
    avcodec_parameters_to_context(codecCtx, pFormatCtx->streams[index]->codecpar);

    /* find audio decoder */
    if((codec=avcodec_find_decoder(codecCtx->codec_id))==NULL)
    {
        avcodec_free_context(&codecCtx);
        qDebug()<<"Audio decoder not found.";
        return -1;
    }

    if(avcodec_open2(codecCtx, codec, NULL)<0)
    {
        avcodec_free_context(&codecCtx);
        qDebug()<<"Could not open audio decoder.";
        return -1;
    }

    totalTime=pFormatCtx->duration;

    env=SDL_getenv("SDL_AUDIO_CHANNELS");
    if(env)
    {
        qDebug()<<"SDL audio channels";
        wantedNbChannels=atoi(env);
        audioDstChannelLayout=av_get_default_channel_layout(wantedNbChannels);
    }

    wantedNbChannels=codecCtx->channels;
    if(!audioDstChannelLayout||
            (wantedNbChannels!=av_get_channel_layout_nb_channels(audioDstChannelLayout)))
    {
        audioDstChannelLayout=av_get_default_channel_layout(wantedNbChannels);
        audioDstChannelLayout &= ~AV_CH_LAYOUT_STEREO_DOWNMIX;
    }

    wantedSpec.channels=av_get_channel_layout_nb_channels(audioDstChannelLayout);
    wantedSpec.freq=codecCtx->sample_rate;

    if(wantedSpec.freq<=0||wantedSpec.channels<=0)
    {
        avcodec_free_context(&codecCtx);
        qDebug()<< "Invalid sample rate or channel count, freq: " << wantedSpec.freq << " channels: " << wantedSpec.channels;
        return -1;
    }

    while(nextSampleRateIndex&&nextSampleRates[nextSampleRateIndex]>=wantedSpec.freq)
    {
        nextSampleRateIndex--;
    }

    wantedSpec.format=audioDeviceFormat;
    wantedSpec.silence=0;
    wantedSpec.samples=FFMAX(SDL_AUDIO_MIN_BUFFER_SIZE, 2<<av_log2(wantedSpec.freq/SDL_AUDIO_MAX_CALLBACKS_PER_SEC));
    wantedSpec.callback=&AudioDecoder::audioCallback;
    wantedSpec.userdata=this;

    while(1)
    {
        while(SDL_OpenAudio(&wantedSpec, &spec)<0)
        {
            qDebug()<<QString("SDL_OpenAudio (%1 channels, %2 Hz): %3").arg(
                          wantedSpec.channels).arg(wantedSpec.freq).arg(SDL_GetError());
            wantedSpec.channels=nextNbChannels[FFMIN(7, wantedSpec.channels)];
            if(!wantedSpec.channels)
            {
                wantedSpec.freq=nextSampleRates[nextSampleRateIndex--];
                wantedSpec.channels=wantedNbChannels;
                if(!wantedSpec.freq)
                {
                    avcodec_free_context(&codecCtx);
                    qDebug()<<"No more combinations to try, audio open failed";
                    return -1;
                }
            }
            audioDstChannelLayout=av_get_default_channel_layout(wantedSpec.channels);
        }

        if(spec.format!=audioDeviceFormat)
        {
            qDebug()<<"SDL audio format: "<<wantedSpec.format<<" is not supported"
                   <<", set to advised audio format: "<< spec.format;
            wantedSpec.format=spec.format;
            audioDeviceFormat=spec.format;
            SDL_CloseAudio();
        }
        else
        {
            break;
        }
    }

    if(spec.channels!=wantedSpec.channels)
    {
        audioDstChannelLayout=av_get_default_channel_layout(spec.channels);
        if(!audioDstChannelLayout)
        {
            avcodec_free_context(&codecCtx);
            qDebug()<<"SDL advised channel count "<<spec.channels<<" is not supported!";
            return -1;
        }
    }

    switch(audioDeviceFormat)
    {
    case AUDIO_U8:
        audioDstFmt=AV_SAMPLE_FMT_U8;
        audioDepth=1;
        break;
    case AUDIO_S16SYS:
        audioDstFmt=AV_SAMPLE_FMT_S16;
        audioDepth=2;
        break;
    case AUDIO_S32SYS:
        audioDstFmt=AV_SAMPLE_FMT_S32;
        audioDepth=4;
        break;
    case AUDIO_F32SYS:
        audioDstFmt=AV_SAMPLE_FMT_FLT;
        audioDepth=4;
    default:
        audioDstFmt=AV_SAMPLE_FMT_S16;
        audioDepth=2;
        break;
    }

    /* open sound */
    SDL_PauseAudio(0);

    return 0;
}

void AudioDecoder::closeAudio()
{
    emptyAudioData();

    SDL_LockAudio();
    SDL_CloseAudio();
    SDL_UnlockAudio();

    avcodec_close(codecCtx);
    avcodec_free_context(&codecCtx);
}

void AudioDecoder::readFileFinished()
{
    isReadFinished=true;
}

void AudioDecoder::pauseAudio(bool pause)
{
    isPause=pause;
}

void AudioDecoder::stopAudio()
{
    isStop=true;
}

void AudioDecoder::packetEnqueue(AVPacket *packet)
{
    packetQueue.enqueue(packet);
}

void AudioDecoder::emptyAudioData()
{
    audioBuffer=nullptr;

    audioBufferIndex=0;
    audioBufferSize=0;
    audioBufferSize1=0;

    clock=0;

    sendReturn=0;

    packetQueue.empty();
}

void
