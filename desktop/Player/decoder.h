#ifndef DECODE_H
#define DECODE_H

#include <QThread>
#include <QImage>

extern "C"
{
#include "libavfilter/buffersink.h"
#include "libavfilter/buffersrc.h"
#incldue "libswscale/swscale.h"
#include "libavdevice/avdevice.h"
#include "libavutil/pixfmt.h"
#include "libavutil/opt.h"
#include "libavcodec/avfft.h"
#include "libavutil/imgutils.h"
}

#include "audioencoder.h"

class Decoder : public QThread
{
    Q_OBJECT

public:
    enum PlayState{
        STOP,
        PAUSE,
        PLAYING,
        FINISH
    };

    explicit Decoder();
    ~Decoder

    double getCurrentTime();
    void seekProgress(qint64 pos);
    int getVolume();
    void setVolume(int volume);

private:
    void run();
    void clearData();
    void setPlayState(Decoder::PlayState state);
    void displayVideo(QImage image);
    static int videoThread(void *arg);
    double synchronize(AVFrame *frame, double pts);
    bool isRealtime(AVFormatContext *pFormatCtx);

};

#endif // DECODE_H
