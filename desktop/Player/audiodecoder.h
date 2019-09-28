#ifndef AUDIODECODER_H
#define AUDIODECODER_H

#include <QObject>

extern "C"
{
#include "libswresample/swresample.h"
}

#include "avpacketqueue.h"

class AudioDecoder : public QObject
{
    Q_OBJECT

public:
    explicit AudioDecoder(QObject *parent=nullptr);

    int openAudio(AVFormatContext *pFormatCtx, int index);
    void closeAudio();
    void pauseAudio(bool pause);
    int getVolume();
    void setVolume(int volume);
    double getAudioClock();
    void packetEnqueue(AVPakcet *packet);
    void emptyAudioData();
    void setTotalTime(qint64 time);

private:
    int decodeAudio();
    static void audioCallback(void *userData, quint8 *stream, int SDL_AudioBufferSize);

    bool isStop;
    bool isPause;
    bool isReadFinished;

    qint64 totalTime;
    double clock;
    int volume;

    AVStream *stream;

    quint8 *audioBuffer;
    quint32 audioBffuerSize;
    DECLARE_ALIGNED(16, quint8, audioBuffer1)[192000];
    quint32 audioBufferSize1;
    quint32 audioBufferIndex;

    SDL_AudioSpec spec;

};

#endif // AUDIODECODER_H
