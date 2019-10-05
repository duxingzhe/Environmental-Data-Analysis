#include <QFileDialog>
#include <QStandardPaths>
#include <QPainter>
#include <QCloseEvent>
#include <QEvent>
#include <QFileInfoList>
#include <QMenu>

#include <QDebug>

#include "mainwindow.h"
#include "ui_mainwindow.h"

extern "C"
{
#include "libavformat/avformat.h"
}

#define VOLUME_INT 13

MainWindow::MainWindow(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::MainWindow),
    decoder(new Decoder),
    menuTimer(new QTimer),
    progressTimer(new QTimer),
    menuIsVisible(true),
    isKeepAspectRatio(false),
    image(QImage(":/image/MUSIC.jpg")),
    autoPlay(true),
    loopPlay(false),
    closeNotExit(false),
    playState(Decoder::STOP),
    seekInterval(15)
{
    ui->setupUi(this);

    qRegisterMetaType<Decoder::PlayState>("Decoder::PlayState");

    menuTimer->setInterval(8000);
    menuTimer->start(5000);

    progressTimer->setInterval(500);

    initUI();
    initTray();
    initSlot();
    initFFmpeg();
}

MainWindow::~MainWindow()
{
    delete ui;
}

void MainWindow::initUI()
{
    this->setWindowTitle("QtPlayer");
    this->setWindowIcon(QIcon(":/image/player.ico"));
    this->centralWidget();
    this->setMouseTracking(true);

    ui->titleLabel->setAlignment(Qt::AlignCenter);

    ui->labelTime->setStyleSheet("background: #5FFFFFF");
    ui->labelTime->setText(QString("00:00:00/00:00:00"));

    ui->btnNext->setIcon(QIcon(":/iamge/next.ico"));
    ui->btnNext->setIconSize(QSize(48,48));
    ui->btnNext->setStyleSheet("background: transparent;border:none;");

    ui->btnPreview->setIcon(QIcon(":/image/forward.ico"));
    ui->btnPreview->setIconSize(QSize(48,48));
    ui->btnPreview->setStyleSheet("background: transparent;border:none;");

    ui->btnStop->setIcon(QIcon(":/image/stop.ico"));
    ui->btnStop->setIconSize(QSize(48,48));
    ui->btnStop->setStyleSheet("background: transparent;border:none;");

    ui->btnPause->setIcon(QIcon(":/image/pause.ico"));
    ui->btnPause->setIconSize(QSize(48,48));
    ui->btnPause->setStyleSheet("background: transparent;border:none;");

    setHide(ui->btnOpenLocal);
    setHide(ui->btnOpenUrl);
    setHide(ui->btnStop);
    setHide(ui->btnPause);
    setHide(ui->btnNext);
    setHide(ui->btnPreview);
    setHide(ui->lineEdit);
    setHide(ui->videoProgressSlider);
    setHide(ui->labelTime);

    ui->videoProgressSlider->installEventFilter(this);
}

void MainWindow::initFFmpeg()
{

    avfilter_register_all();

    av_register_all();

    if(avformat_network_init())
    {
        qDebug()<<"avformat network init failed.";
    }

    if(SDL_Init(SDL_INIT_AUDIO|SDL_INIT_TIMER))
    {
        qDebug()<<"SDL init failed.";
    }
}

void MainWindow::initSlot()
{
    connect(ui->btnOpenLocal, SIGNAL(clicked(bool)), this, SLOT(buttonClickSlot()));
    connect(ui->btnOpenUrl, SIGNAL(clicked(bool)), this, SLOT(buttonClickSlot()));
    connect(ui->btnStop, SIGNAL(clicked(bool)), this, SLOT(buttonClickSlot()));
    connect(ui->btnPause, SIGNAL(clicked(bool)), this, SLOT(buttonClickSlot()));
    connect(ui->btnNext, SIGNAL(clicked(bool)), this, SLOT(buttonClickSlot()));
    connect(ui->btnPreview, SIGNAL(clicked(bool)), this, SLOT(buttonClickSlot()));
    connect(ui->lineEdit, SIGNAL(cursorPositionChanged(ini, int)), this, SLOT(editText()));

    connect(menuTimer, SIGNAL(timeout()), this, SLOT(timerSlot()));
    connect(progressTimer, SIGNAL(timeout()), this, SLOT(timerSlot()));

    connect(ui->videoProgressSlider, SIGNAL(sliderMoved(int)), this, SLOT(seekProgress(int)));

    connect(this, SIGNAL(selectedVideoFile(QString, QString)), decoder, SLOT(decoderFile(QString, QString)));
    connect(this, SIGNAL(stopVideo()), this, SLOT(stopVideo()));
    connect(this, SIGNAL(pauseVideo()), this, SLOT(stopVideo()));

    connect(decoder, SIGNAL(playStateChanged(Decoder::PlayState)), this, SLOT(playStateChanged(Decoder::PlayState)));
    connect(decoder, SIGNAL(gotVideoTime(qint64)), this, SLOT(videoTime(qint64)));
    connect(decoder, SIGNAL(gotVideo(QImage)), this, SLOT(showVideo(QImage)));
}

void MainWindow::initTray()
{
    QSystemTrayIcon *trayIcon=new QSystemTrayIcon(this);

    trayIcon->setToolTip(tr("Player"));
    trayIcon->setIcon(QIcon(":/image/player.ico"));
    trayIcon->show();

    QAction *minimizeAction=new QAction(tr("最小化 (&I)"), this);
    connect(minimizeAction, SIGNAL(triggered()), this ,SLOT(hide()));
    QAction *restoreAction=new QAction(tr("还原 (&R)"), this);
    connect(restoreAction, SIGNAL(triggered()), this, SLOT(showNormal()));
    QAction *quitAction=new QAction(tr("退出 (&Q)"));
    connect(quitAction, SIGNAL(triggered()), qApp, SLOT(quit()));

    QMenu *trayIconMenu=new QMenu(this);

    trayIconMenu->addAction(minimizeAction);
    trayIconMenu->addAction(restoreAction);
    trayIconMenu->addSeparator();
    trayIconMenu->addAction(quitAction);
    trayIcon->setContextMenu(trayIconMenu);

    connect(trayIcon, SIGNAL(activated(QSystemTrayIcon::ActivationReason)),
            this, SLOT(trayIconActivated(QSystemTrayIcon::ActivationReason)));
}

void MainWindow::paintEvent(QPaintEvent *event)
{
    Q_UNUSED(event);

    QPainter painter(this);

    painter.setRenderHint(QPainter::Antialiasing, true);

    int width=this->width();
    int height=this->height();

    painter.setBrush(Qt::black);
    painter.drawRect(0, 0, width, height);

    if(isKeepAspectRatio)
    {
        QImage img=image.scaled(QSize(width, height), Qt::KeepAspectRatio);

        int x=(this->width()-img.width())/2;
        int y=(this->height()-img.height())/2;

        painter.drawImage(QPoint(x,y), img);
    }
    else
    {
        QImage img=image.scaled(QSize(width, height));

        painter.drawImage(QPoint(0,0), img);
    }
}

void MainWindow::closeEvent(QCloseEvent *event)
{
    if(closeNotExit)
    {
        event->ignore();
        this->hide();
    }
}

void MainWindow::changeEvent(QEvent *event)
{
    if(event->type()==QEvent::WindowStateChange)
    {
        if(this->windowState()==Qt::WindowMinimized)
        {
            event->ignore();
            this->hide();
        }
        else if(this->windowState()==Qt::WindowMaximized)
        {

        }
    }
}

bool MainWindow::eventFilter(QObject *object, QEvent *event)
{
    if(event->type()==QEvent::MouseButtonPress)
    {
        QMouseEvent *mouseEvent=static_cast<QMouseEvent *>(event);
        if(mouseEvent->button()==Qt::LeftButton)
        {
            int duration=ui->videoProgressSlider->maximum()-ui->videoProgressSlider->minimum();
            int pos=ui->videoProgressSlider->minimum()+duration*(static_cast<double>(mouseEvent->x())/ui->videoProgressSlider->width());
            if(pos!=ui->videoProgressSlider->sliderPosition())
            {
                ui->videoProgressSlider->setValue(pos);
                decoder->seekProgress(static_cast<qint64>(pos)*1000000);
            }
        }
    }
    return QObject::eventFilter(object, event);
}

void MainWindow::keyReleaseEvent(QKeyEvent *event)
{
    int progressVal;
    int volumnVal=decoder->getVolume();

    switch(event->key())
    {
    case Qt::Key_Up:
        if(volumnVal+VOLUME_INT>SDL_MIX_MAXVOLUME)
        {
            decoder->setVolume(SDL_MIX_MAXVOLUME);
        }
        else
        {
            decoder->setVolume(volumnVal+VOLUME_INT);
        }
        break;
    case Qt::Key_Down:
        if(volumnVal-VOLUME_INT<0)
        {
            decoder->setVolume(0);
        }
        else
        {
            decoder->setVolume(volumnVal-VOLUME_INT);
        }
        break;
    case Qt::Key_Left:
        if(ui->videoProgressSlider->value()>seekInterval)
        {
            progressVal=ui->videoProgressSlider->value()-seekInterval;
            decoder->seekProgress(static_cast<qint64>(progressVal)*1000000);
        }
        break;
    case Qt::Key_Right:
        if(ui->videoProgressSlider->value()+seekInterval>ui->videoProgressSlider->maximum())
        {
            progressVal=ui->videoProgressSlider->value()+seekInterval;
            decoder->seekProgress(static_cast<qint64>(progressVal)*1000000);
        }
        break;
    case Qt::Key_Escape:
        showNormal();
        break;
    case Qt::Key_Space:
        emit pauseVideo();
        break;
    default:

        break;
    }
}

void MainWindow::mouseMoveEvent(QMouseEvent *event)
{
    Q_UNUSED(event);

    if(currentPlayType=="video")
    {
        menuTimer->stop();
        if(!menuIsVisible)
        {
            showControl(true);
            menuIsVisible=true;
            QApplication::setOverrideCursor(Qt::ArrowCursor);
        }
        menuTimer->start();
    }
}

void MainWindow::mousePressEvent(QMouseEvent *event)
{
    if(event->buttons()==Qt::RightButton)
    {
        showPlayMenu();
    }
    else if(event->buttons()==Qt::LeftButton)
    {
        emit pauseVideo();
    }
}

void MainWindow::mouseDoubleClickEvent(QMouseEvent *event)
{
    if(event->buttons()==Qt::LeftButton)
    {
        if(isFullScreen())
        {
            showNormal();
        }
        else
        {
            showFullScreen();
        }
    }
}

void MainWindow::showPlayMenu()
{
    QMenu *menu=new QMenu;

    QAction *fullSrcAction=new QAction("全屏", this);
    fullSrcAction->setChecked(true);
    if(isFullScreen())
    {
        fullSrcAction->setChecked(true);
    }

    QAction *keepRatioAction=new QAction(QStringLiteral("Video Ratio"), this);
    keepRatioAction->setChecked(true);
    if(isKeepAspectRatio)
    {
        keepRatioAction->setChecked(true);
    }

    QAction *autoPlayAction=new QAction("连续播放", this);
    autoPlayAction->setChecked(true);
    if(autoPlay)
    {
        autoPlayAction->setChecked(true);
    }

    QAction *loopPlayAction=new QAction("循环播放", this);
    loopPlayAction->setChecked(true);
    if(loopPlay)
    {
        loopPlayAction->setChecked(true);
    }

    QAction *captureAction=new QAction("截图", this);

    connect(fullSrcAction, SIGNAL(triggered(bool)), this, SLOT(setFullScreen()));
    connect(keepRatioAction, SIGNAL(triggered(bool)), this, SLOT(setKeepRatio()));
    connect(autoPlayAction, SIGNAL(triggered(bool)), this, SLOT(setAutoPlay()));
    connect(loopPlayAction, SIGNAL(triggered(bool)), this, SLOT(setLoopPlay()));
    connect(captureAction, SIGNAL(triggered(bool)), this, SLOT(saveCurrentFrame()));

    menu->addAction(fullSrcAction);
    menu->addAction(keepRatioAction);
    menu->addAction(autoPlayAction);
    menu->addAction(loopPlayAction);
    menu->addAction(captureAction);

    menu->exec(QCursor::pos());

    disconnect(fullSrcAction, SIGNAL(triggered(bool)), this, SLOT(setFullScreen()));
    disconnect(keepRatioAction, SIGNAL(triggered(bool)), this, SLOT(setKeepRatio()));
    disconnect(autoPlayAction, SIGNAL(triggered(bool)), this, SLOT(setAutoPlay()));
    disconnect(loopPlayAction, SIGNAL(triggered(bool)), this, SLOT(setLoopPlay()));
    disconnect(captureAction, SIGNAL(triggered(bool)), this, SLOT(saveCurrentFrame()));

    delete fullSrcAction;
    delete keepRatioAction;
    delete autoPlayAction;
    delete loopPlayAction;
    delete captureAction;
    delete menu;
}
