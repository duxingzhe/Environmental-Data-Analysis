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
