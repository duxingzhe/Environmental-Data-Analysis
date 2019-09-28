#include <QDebug>

#include "audiodecoder.h"

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

    return 0;
}
