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
