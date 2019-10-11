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
