#include "playerwindow.h"

#include <QPushButton>
#include <QSlider>
#include <QLayout>
#include <QMessageBox>
#include <QFileDialog>

using namespace QtAV;

PlayerWindow::PlayerWindow(QWidget *parent)
    : QWidget(parent)
{
    m_unit=1000;
    setWindowTitle(QString::fromLatin1("Mointor"));
    m_player=new AVPlayer(this);
    QVBoxLayout *vboxLayout=new QVBoxLayout();
    setLayout(vboxLayout);
    m_videoOutput=new VideoOutput(this);
    if(!m_videoOutput->widget())
    {
        QMessageBox::warning(0, QString::fromLatin1("Monitor Error"), tr("Cannot create video render"));
        return;
    }
    m_player->setRenderer(m_videoOutput);
    vboxLayout->addWidget(m_videoOutput->widget());
    m_slider=new QSlider();
    m_slider->setOrientation(Qt::Horizontal);
    connect(m_slider,SIGNAL(sliderMoved(int)), SLOT(seekBySlider(int)));
    connect(m_slider, SIGNAL(sliderPressed()), SLOT(seekBySlider()));
    connect(m_slider, SIGNAL(positionChanged(qint64)), SLOT(updateSlider(qint64)));
    connect(m_slider, SIGNAL(started()), SLOT(updateSlider));
    connect(m_player, SIGNAL(notifyIntervalChanged()), SLOT(updateSliderUnit()));

    vboxLayout->addWidget(m_slider);
    QHBoxLayout *hboxLayout=new QHBoxLayout();
    vboxLayout->addLayout(hboxLayout);
    m_openBtn=new QPushButton(tr("Open"));
    m_playBtn=new QPushButton(tr("Play/Pause"));
    m_stopBtn=new QPushButton(tr("Stop"));
    hboxLayout->addWidget(m_openBtn);
    hboxLayout->addWidget(m_playBtn);
    hboxLayout->addWidget(m_stopBtn);
    connect(m_openBtn, SIGNAL(clicked()), SLOT(openMedia()));
    connect(m_playBtn, SIGNAL(clicked()), SLOT(playPause()));
    connect(m_stopBtn, SIGNAL(clicked()), m_player, SLOT(stop()));
}

void PlayerWindow::openMedia()
{
    QString file="rtmp://localhost:1935/myapp/mystream";
    if(file.isEmpty())
        return;
    m_player->play(file);
}

void PlayerWindow::seekBySlider(int value)
{
    if(!m_player->isPlaying())
        return;
    m_player->seek(qint64(value*m_unit));
}

void PlayerWindow::seekBySlider()
{
    seekBySlider(m_slider->value());
}

void PlayerWindow::playPause()
{
    if(!m_player->isPlaying())
    {
        m_player->play();
        return;
    }
    m_player->pause(!m_player->isPaused());
}

void PlayerWindow::updateSlider(qint64 value)
{
    m_slider->setRange(0, int(m_player->duration()/m_unit));
    m_slider->setValue(int(value/m_unit));
}

void PlayerWindow::updateSlider()
{
    updateSlider(m_player->position());
}

void PlayerWindow::updateSliderUnit()
{
    m_unit=m_player->notifyInterval();
    updateSlider();
}
