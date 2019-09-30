#include "decoder.h"

Decoder::Decoder() :
    timeTotal(0),
    playState(STOP),
    isStop(false),
    isPause(false),
    isSeek(false),
    isReadFinished(false),
    audioDecoder(new AudioDecoder),
    filterGraph(NULL)
{
    av_init_packet(&seekPacket);
    seekPacket.data=(uint8_t *)"FLUSH";

    connect(audioDecoder, SIGNAL(playFinished()), this, SLOT(audioFinished()));
    connect(this, SIGNAL(readFinished()), audioDecoder, SLOT(readFileFinished()));
}

Decoder::~Decoder()
{

}

void Decoder::displayVideo(QImage image)
{
    emit gotVideo(image);
}

void Decoder::clearData()
{
    videoIndex=-1;
    audioIndex=-1;
    subtitleIndex=-1;

    timeTotal=0;

    isStop=false;
    isPause=false;
    isSeek=false;
    isReadFinished=false;
    isDecodeFinished=false;

    videoQueue.empty();

    audioDecoder->emptyAudioData();

    videoClock=0;
}

void Decoder::setPlayState(Decoder::PlayState state)
{
    emit playStateChanged(state);
    playState=state;
}

bool Decoder::isRealtime(AVFormatContext *pFormatCtx)
{
    if(!strcmp(pFormatCtx->iformat->name, "rtp")
       || !strcmp(pFormatCtx->iformat->name, "rtsp")
       || !strcmp(pFormatCtx->iformat->name, "sdp"))
    {
        return true;
    }

    if(pFormatCtx->pb && (!strncmp(pFormatCtx->filename, "rtp:", 4)
                          || !strncmp(pFormatCtx->filename, "udp:", 4)))
    {
        return true;
    }

    return false;
}
