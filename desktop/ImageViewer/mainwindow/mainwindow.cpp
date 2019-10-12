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
                                 tr("Open an image, please."));
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
                                 tr("Open an image, please"));
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
                                 tr("Open an image, please"));
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
                                 tr("Open an image, please"));
        return ;
    }
    loadImageResource();
}

void MainWindow::toEnlargeActionTriggered(void)
{
    int ret=imageViewer->zoomIn();
    if(ret)
    {
        QMessageBox::information(this,
                                 tr("Error"),
                                 tr("Open an image, please"));
        return ;
    }
    loadImageResource();
}

void MainWindow::toLessenActionTriggered(void)
{
    int ret=imageViewer->zoomOut();
    if(ret)
    {
        QMessageBox::information(this,
                                 tr("Error"),
                                 tr("Open an image, please"));
        return ;
    }
    loadImageResource();
}

void MainWindow::deleteActionTriggered(void)
{
    if(!QFile(imageViewer->filename).exists())
    {
        QMessageBox::information(this, tr("Error"),
                                 tr("Open an image, please"));

        return ;
    }

    QMessageBox message(QMessage::Warning, tr("Warning"),
                        tr("Do you want to delete this image?"),
                        QMessageBox::Yes|QMessage::No, NULL);

    if(message.exec()==QMessageBox::No)
    {
        return ;
    }

    int ret=imageViewer->delImageFile();
    if(ret)
    {
        QMessageBox::warning(this, tr("Error"), tr("Delete an image failed!"));

        return ;
    }

    initImageResource();
}

void MainWindow::setImageViewerWidget(void)
{
    imageLabel=new QLabel();

    QScrollArea *imageScrollArea=new QScrollArea();
    imageScrollArea->setAlignment(Qt::AlignCenter);
    imageScrollArea->setFrameShape(QFrame::NoFrame);
    imageScrollArea->setWidget(imageLabel);

    QGridLayout *mainLayout=new QGridLayout();
    mainLayout->addWidget(imageScrollArea, 0, 0);
    centralWidget->setLayout(mainLayout);
}

void MainWindow::aboutQtTriggered()
{
    qApp->aboutQt();
}
