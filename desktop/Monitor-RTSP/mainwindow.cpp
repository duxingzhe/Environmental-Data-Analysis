#include "mainwindow.h"
#include "ui_mainwindow.h"

#include <QThread>
#include <QPainter>
#include <QInputDialog>
#include <QtMath>

#include <iostream>
using namespace std;

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::MainWindow)
{
    ui->setupUi(this);

    mPlayer=new VideoPlayer;
    connect(mPlayer, SIGNAL(sig_GetOneFrame(QImage)), this, SLOT(slotGetOneFrame(QImage)));
    connect(mPlayer, SIGNAL(sig_GetRFrame(QImage)), this, SLOT(slotGetRFrame(QImage)));

    connect(ui->Open_red, &QAction::triggered, this, &MainWindow::slotOpenRed);
    connect(ui->Close_red, &QAction::triggered, this, &MainWindow::slotCloseRed);

    mPlayer->startPlay();
}

MainWindow::~MainWindow()
{
    delete ui;
}

void MainWindow::paintEvent(QPaintEvent *event)
{
    QPainter painter(this);

    painter.setBrush(Qt::white);
    painter.drawRect(0, 0, this->width(), this->height());

    if(mImage.size().width() <= 0)
    {
        return;
    }

    QImage img=mImage.scaled(this->size(), Qt::KeepAspectRatio);
    int x=this->width()-img.width();
    int y=this->height()-img.height();

    x/=2;
    y/=2;

    painter.drawImage(QPoint(x,y), img);

    if(open_red==true)
    {
        QWidget *red_video=new QWidget(this);
        red_video->resize(this->width()/3, this->height()/3);
        painter.setBrush(Qt::white);
        painter.drawRect(0, 0, red_video->width(), red_video->height());

        if(R_mImage.size().width()<=0)
        {
            return;
        }

        QImage img=R_mImage.scaled(this->size(), Qt::KeepAspectRatio);
        int x=this->width()-img.width();
        int y=this->height()-img.height();

        x/=2;
        y/=2;

        painter.drawImage(QPoint(x,y), img);
    }
}

void MainWindow::slotGetOneFrame(QImage img)
{
    mImage=img;
    update();
}

QImage Indentificate(QImage image)
{
    QSize size=image.size();
    QImage binaryImage(size, QImage::Format_RGB32);
    int width=image.width(), height=image.height();
    int threshold=200;
    for(int i=0;i<width;i++)
    {
        for(int j=0;j<height;j++)
        {
            QRgb pixel=image.pixel(i,j);
            int r=qRed(pixel)*0.3;
            int g=qGreen(pixel)*0.59;
            int b=qBlue(pixel)*0.11;
            int rgb=r+g+b;
            if(rgb>threshold)
                rgb=255;
            else
                rgb=0;

            QRgb newPixel=qRgb(rgb, rgb, rgb);
            binaryImage.setPixel(i, j, newPixel);
        }
    }

    return binaryImage;
}

void MainWindow::slotGetRFrame(QImage img)
{
    R_mImage=img;

    update();
}

bool MainWindow::slotOpenRed()
{
    open_red=true;
    return open_red;
}

bool MainWindow::slotCloseRed()
{
    open_red=false;
    return open_red;
}
