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

