#ifndef PLAYWINDOW_H
#define PLAYWINDOW_H

#include <QWidget>
#include <QtAV>

class QSlider;
class QPushButton;

namespace Ui
{
    class MainWindow;
}


class PlayerWindow : public QWidget
{
    Q_OBJECT

public:
    explicit PlayerWindow(QWidget *parent = nullptr);

public Q_SLOTS:
    void openMedia();
    void seekBySlider(int value);
    void seekBySlider();
    void playPause();

private Q_SLOTS:
    void updateSlider(qint64 value);
    void updateSlider();
    void updateSliderUnit();

private:
    QtAV::VideoOutput *m_vo;
    QtAV::AVPlayer *m_player;
    QSlider *m_slider;
    QPushButton *m_openBtn;
    QPushButton *m_playBtn;
    QPushButton *m_stopBtn;
    int m_unit;
};
#endif // PLAYWINDOW_H
