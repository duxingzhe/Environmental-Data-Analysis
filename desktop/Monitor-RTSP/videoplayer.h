#ifndef VIDEOPLAYER_H
#define VIDEOPLAYER_H

#include <QThread>
#include <QImage>

class VlcInstance;
class VlcMedia;
class VlcMediaPlayer;

class VideoPlayer : public QThread
{
    Q_OBJECT

public:
    explicit VideoPlayer();
    ~VideoPlayer();

    void startPlay();

signals:
    void sig_GetOneFrame(QImage);
    void sig_GetRFrame(QImage);

protected:
    void run();

private:
    QString mFileName;

    VlcInstance *_instance;
    VlcMedia *_media;
    VlcMediaPlayer *_player;
};

#endif // VIDEOPLAYER_H
