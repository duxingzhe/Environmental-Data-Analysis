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
