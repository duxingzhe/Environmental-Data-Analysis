#include "mainwindow.h"

#include <QWidget>
#include <QImage>
#include <QFileDialog>
#include <QPixmap>
#include <QAction>
#include <QMessageBox>
#include <QDebug>
#include <QScrollArea>
#include <QGridLayout>
#include <QErrorMessage>
#include <QApplication>

MainWindow::MainWindow(QWidget *parent) : QMainWindow(parent)
{
    initMainWindow();

    initUiComponent();

    initImageResource();

    imageViewer=new ImageViewer();
}

void MainWindow::initImageResource(void)
{
    imageLabel->clear();
    imageLabel->resize(QSize(200,100));
    setWindowTitle(tr("ImageViewer"));
}

void MainWindow::loadImageResource(void)
{
    imageLabel->setPixmap(imageViewer->pixmap);
    imageLabel->resize(imageViewer->size);
    setWindowTitle(QFileInfo(imageViewer->filename).fileName()+tr(" - ImageViewer"));
}

void MainWindow::openActionTriggered(void)
{
    int ret=imageViewer->openImageFile(tr("Select image:"),
                                       "C:\\",
                                       tr("Images (.jpg *.jpeg *.png *.bmp *.gif)"));
    if(ret)
    {
        QMessageBox::information(this, tr("Error"), tr("Open a file failed!"));
        return ;
    }

    loadImageResource();
}

void MainWindow::closeActionTriggered(void)
{
    initImageResource();
    imageViewer->closeImageFile();
}

void MainWindow::lastActionTriggered(void)
{
    int ret=imageViewer->last();
    if(ret)
    {
        QMessageBox::information(this, tr("Error"),
                                 tr("Open an image please."));
        return ;
    }
    loadImageResource();
}

void MainWindow::nextActionTriggered(void)
{
    int ret=imageViewer->next();
    if(ret)
    {
        QMessageBox::information(this,
                                 tr("Error"),
                                 tr("Open an image please"));
        return ;
    }
    loadImageResource();
}

void MainWindow::toLeftActionTriggered(void)
{
    int ret=imageViewer->spinToLeft();
    if(ret)
    {
        QMessageBox::information(this,
                                 tr("Error"),
                                 tr("Open an image please"));
        return ;
    }
    loadImageResource();
}

void MainWindow::toRightActionTriggered(void)
{
    int ret=imageViewer->spinToRight();
    if(ret)
    {
        QMessageBox::information(this,
                                 tr("Error"),
                                 tr("Open an image please"));
        return ;
    }
    loadImageResource();
}
