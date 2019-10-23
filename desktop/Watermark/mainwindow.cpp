#include "mainwindow.h"
#include "ui_mainwindow.h"
#include <QPainter>
#include <QWidget>

MainWindow::MainWindow(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::MainWindow)
{
    ui->setupUi(this);

    setWindowTitle(tr("Main Window"));
    QSize size(200,100); //指定图片大小;
    QImage image(size,QImage::Format_ARGB32);
    //以ARGB32格式构造一个QImage
    image.fill(qRgba(0, 253, 0, 255));//填充图片背景,120/250为透明度
    QPainter painter(&image); //为这个QImage构造一个QPainter
    //改变画笔和字体
    painter.setCompositionMode(QPainter::CompositionMode_SourceIn);
    QPen pen = painter.pen();
    pen.setColor(qRgb(240,248,255));
    QFont font = painter.font();
    font.setBold(true);//加粗
    font.setPixelSize(30);//改变字体大小

    painter.setPen(pen);
    painter.setFont(font);
    //设置画刷的组合模式CompositionMode_SourceOut这个模式为目标图像在上。
    //改变组合模式和上面的填充方式可以画出透明的图片。

    //设置透明度
    painter.setOpacity(0.67);
    painter.drawText(image.rect(),Qt::AlignCenter,"Environment");
    //将Hello写在Image的中心

    ui->label->setPixmap(QPixmap::fromImage(image));
}

MainWindow::~MainWindow()
{
    delete ui;
}
