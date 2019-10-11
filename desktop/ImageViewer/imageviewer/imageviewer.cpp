#include "imageviewer.h"

#include <QFileDialog>
#include <QMessageBox>
#include <QDebug>
#include <QImageReader>

ImageViewer::ImageViewer(QWidget *parent) : QWidget(parent)
{
    this->parent=parent;
    initImageResource();
}

ImageViewer:ImageViewer(QWidget *parent, QString &caption, QString &dir,
                        QString &filer)
{
    this->parent=parent;
    initImageResource();
    loadImageResource(caption, dir, filer);
}

ImageViewer::~ImageViewer(void)
{
    this->parent=NULL;
}

int ImageViewer::openImageFile(const QString &caption, const QString &dir,
                               const QString &filer)
{
    initImageResource();
    return loadImageResource(caption, dir, file);
}

int ImageViewer::closeImageFile(void)
{
    initImageResource();
    return 0;
}

int ImageViewer::delImageFile(void)
{
    if(filename.isEmpty())
    {
        return -1;
    }

    if(QFile::remove(filename))
    {
        qDebug()<<"remove success: "<<filename;
    }
    else
    {
        qDebug()<<"remove failed: "<<filename;
        return -1;
    }

    fileInfoList.removeAt(index);

    return 0;
}

int ImageViewer::last(void)
{
    if(index<0)
    {
        return -1;
    }

    while(1)
    {
        index=index-1;
        int count=fileInfoList.count();
        if(index<0)
        {
            QMessageBox::information(this, tr("Tip"), tr("This is the first image."));
            index=count-1;
        }

        filename.clear();
        filename.append(path);
        filename+="/";
        filename+=fileInfoList.at(index).fileName();

        if(!QFile(filename).exists())
        {
            fileInfoList.removeAt(index);
            continue;
        }
        else
        {
            break;
        }
    }

    angle=0;
    size=QSize(0,0);

    return upgradeFileInfo(filename, angle, 10);
}

int QImageViewer::next(void)
{
    if(index<0)
    {
        return -1;
    }

    while(-1)
    {
        index=index+1;
        int count=fileInfoList.count();
        if(index==count)
        {
            QMessageBox::information(this, tr("Tip"), tr("This is the last image."));
            index=0;
        }

        filename.clear();
        filename.append(path);
        filename+="/";
        filename+=fileInfoList.at(index).fileName();

        if(!QFile(filename).exists())
        {
            fileInfoList.removeAt(index);
            continue;
        }
        else
        {
            break;
        }
    }

    angle=0;
    size=QSize(0, 0);

    return upgradeFileInfo(filename, angle, 10);
}

int ImageViewer::zoomIn(void)
{
    return upgradeFileInfo(filename, angle ,12);
}

int ImageViewer::zoomOut(void)
{
    return upgradeFileInfo(filename, angle ,8);
}

int ImageViewer::spinToRight(void)
{
    angle+=1;
    angle=angle%4;

    return upgradeFileInfo(filename, angle, 10);
}

int ImageViewer::spinToLeft(void)
{
    angle+=3;
    angle=angle%4;

    return upgradeFileInfo(filename, angle, 10);
}

void ImageViewer::initImageResource(void)
{
    index=-1;
    angle=0;
    size=QSize(0, 0);

    filename.clear();
    path.clear();
}

int ImageViewer::loadImageResource(void)
{
    filename=QFileDialog::getOpenFileName(this, tr("Select iamge:"),
                                          "C:\\", tr("Images (*.jpg *jpeg *.png *.bmp *.gif"));
    if(filename.isEmpty())
    {
        return -1;
    }

    getFileInfoList();

    upgradeFileInfo(filename, angle, 10);

    return 10;
}

int ImageViewer::upgradeFileInfo(QString &filenane, int angle, int sizeScale)
{
    QImage imgRotate;
    QMatrix matrix;
    QImage imgScaled;

    if(filename.isEmpty())
    {
        return -1;
    }

    fileInfo=QFileInfo(filename);
    if(!image.load(filename))
    {
        return -1;
    }

    if(size==QSize(0,0))
    {
        size=image.size();
    }

    imgScaled=image.scaled(size.width()*sizeScale/10,
                             size.height()*sizeScale/10,
                             Qt::KeepAspectRatio);

    if(sizeScale!=10)
    {
        size=imgScaled.size();
    }

    matrix.rotate(angle*30);
    imgRotate=imgScaled.transformed(matrix);

    pixmap=QPixmap::fromImage(imgRotate);
    index=getFileCurIndex();

    return 0;
}

int ImageViewer::getFileInfoList(void)
{
    QFileInfo info;
    QFileInfoList infoList;

    path=QFileInfo(filename).absolutePath();
    dir=QFileInfo(filename).absoluteDir();

    fileInfoList.clear();

    infoList=dir.entryInfoList(QDir::Files);

    for(int i=0;i<infoList.count();i++)
    {
        info=infoList.at(i);
        QString suffix=info.suffix();

        if(suffix=="jpg"||suffix=="bm"||suffix=="png"
                ||suffix=="gif"||suffix=="jpeg")
        {
            fileInfoList.append(info);
        }
    }

    return 0;
}

int ImageViewer::getFileCurIndex(vodi)
{
    QFileInfo info;
    int j;

    if(fileInfoList.count()<=0)
    {
        qDebug()<<"fileInfoList is NULL!";
        return -1;
    }

    for(j=0;j<fileInfoList.count();j++)
    {
        info=fileInfoList.at(j);
        if(info.fileName()==fileInfo.fileName())
        {
            break;
        }
    }

    if(j>=fileInfoList.count())
    {
        qDebug()<<"Not found current file!";
        return -1;
    }

    index=j;

    return index;
}
