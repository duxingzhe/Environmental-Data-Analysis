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

    QMessageBox message(QMessageBox::Warning, tr("Warning"),
                        tr("Do you want to delete this image?"),
                        QMessageBox::Yes|QMessageBox::No, NULL);

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

void MainWindow::setWindowComponet(void)
{
    openAction=new QAction(tr("Open"), this);
    openAction->setShortcut(QKeySequence::Open);
    openAction->setStatusTip(tr("Open an image."));
    openAction->setIcon(QIcon(":/iamges/open.png"));

    closeAction=new QAction(tr("Close"), this);
    closeAction->setShortcut(QKeySequence::Close);
    closeAction->setStatusTip(tr("Close an image."));
    closeAction->setIcon(QIcon(":/iamges/close.png"));

    lastAction=new QAction(tr("Last"), this);
    lastAction->setStatusTip(tr("Last image."));
    lastAction->setIcon(QIcon(":/iamges/left.png"));

    nextAction=new QAction(tr("Next"), this);
    nextAction->setStatusTip(tr("Next image."));
    nextAction->setIcon(QIcon(":/iamges/right.png"));

    toLeftAction=new QAction(tr("LeftSpin"), this);
    toLeftAction->setStatusTip(tr("To Left."));
    toLeftAction->setIcon(QIcon(":/iamges/toLeft.png"));

    toRightAction=new QAction(tr("RightSpin"), this);
    toRightAction->setStatusTip(tr("To Right."));
    toRightAction->setIcon(QIcon(":/iamges/toRight.png"));

    toEnlargeAction=new QAction(tr("Enlarge"), this);
    toEnlargeAction->setStatusTip(tr("To Enlarge."));
    toEnlargeAction->setIcon(QIcon(":/iamges/large.png"));

    toLessenAction=new QAction(tr("Lessen"), this);
    toLessenAction->setStatusTip(tr("To Lessen."));
    toLessenAction->setIcon(QIcon(":/iamges/small.png"));

    deleteAction=new QAction(tr("Delete"), this);
    deleteAction->setShortcut(QKeySequence::Delete);
    deleteAction->setStatusTip(tr("Delete an image."));
    deleteAction->setIcon(QIcon(":/iamges/clear.png"));

    QAction *exitAction=new QAction(tr("Exit"), this);
    exitAction->setShortcut(QKeySequence::Delete);
    exitAction->setStatusTip(tr("Exit"));
    exitAction->setIcon(QIcon(":/iamges/quite.png"));

    QAction *aboutQt=new QAction(tr("About Qt"), this);
    aboutQt->setStatusTip(tr("About Qt"));
    aboutQt->setIcon(QIcon(":/iamges/Qt.png"));

    QAction *about=new QAction(tr("About ImageViewer"), this);
    about->setStatusTip(tr("About ImageViewer"));
    about->setIcon(QIcon(":/iamges/help.png"));

    QMenu *fileMenu=menuBar->addMenu(tr("File"));
    fileMenu->addAction(openAction);
    fileMenu->addAction(closeAction);
    fileMenu->addSeparator();
    fileMenu->addAction(deleteAction);
    fileMenu->addSeparator();
    fileMenu->addAction(exitAction);

    QMenu *operationMenu=menuBar->addMenu(tr("Operate"));
    operationMenu->addAction(lastAction);
    operationMenu->addAction(nextAction);
    operationMenu->addSeparator();
    operationMenu->addAction(toLeftAction);
    operationMenu->addAction(toRightAction);
    operationMenu->addSeparator();
    operationMenu->addAction(toEnlargeAction);
    operationMenu->addAction(toLessenAction);

    QMenu *helpMenu=menuBar->addMenu(tr("Help"));
    helpMenu->addAction(aboutQt);
    helpMenu->addAction(about);

    toolBar->addAction(openAction);
    toolBar->addAction(closeAction);
    toolBar->addAction(lastAction);
    toolBar->addAction(nextAction);
    toolBar->addAction(toLeftAction);
    toolBar->addAction(toRightAction);
    toolBar->addAction(toEnlargeAction);
    toolBar->addAction(toLessenAction);
    toolBar->addAction(deleteAction);
    toolBar->addAction(about);

    connect(openAction, SIGNAL(triggered(bool)), this, SLOT(openActionTriggered()));
    connect(closeAction, SIGNAL(triggered(bool)), this, SLOT(closeActionTriggered()));
    connect(lastAction, SIGNAL(triggered(bool)), this, SLOT(lastActionTriggered()));
    connect(nextAction, SIGNAL(triggered(bool)), this, SLOT(nextActionTriggered()));
    connect(toLeftAction, SIGNAL(triggered(bool)), this, SLOT(toLeftActionTriggered()));
    connect(toRightAction, SIGNAL(triggered(bool)), this, SLOT(toRightActionTriggered()));
    connect(toEnlargeAction, SIGNAL(triggered(bool)), this, SLOT(toEnlargeActionTriggered()));
    connect(toLessenAction, SIGNAL(triggered(bool)), this, SLOT(toLessenActionTriggered()));
    connect(deleteAction, SIGNAL(triggered(bool)), this, SLOT(deleteActionTriggered()));

    connect(about, SIGNAL(triggered(bool)), this, SLOT(aboutTriggered()));
    connect(aboutQt, SIGNAL(triggered(bool)), this, SLOT(aboutQtTriggered()));
    connect(exitAction, SIGNAL(triggered(bool)), this, SLOT(close()));
}

void MainWindow::aboutQtTriggered()
{
    qApp->aboutQt();
}
