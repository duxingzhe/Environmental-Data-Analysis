#ifndef IMAGEVIEWER_H
#define IMAGEVIEWER_H

#include <QWidget>
#include <QImage>
#include <QPixmap>
#include <QDir>
#include <QSize>
#include <QFileInfo>
#include <QFileInfoList>

class ImageViewer : public QWidget
{
    Q_OBJECT
public:
    explicit ImageViewer(QWidget *parent=0);
    explicit ImageViewer(QWidget *parent, QString &caption, QString &dir, QString &filer);
    ~ImageViewer();

    QWidget *parent;

    int index;
    int angle;
    QSize size;
    QString filename;
    QString path;
    QDir dir;
    QFileInfo fileInfo;
    QFileInfoList fileInfoList;

    QImage image;
    QPixmap;

    int openImageFile(const QString &caption, const QString &dir, const QString &filer);

    int closeImageFile(void);

    int delImageFile(void);

    int last(void);
    int next(void);

    int zoomIn(void);
    int zoomOut(void);
    int spinToRight(void);
    int spinToLeft(void);

private:

    void initImageResource(void);

    int loadImageResource(void);
    int loadImageResource(const QString &caption, const QString &dir, const QString &filer);

    int getFileInfoList(void);
    int getFileCurIndex(void);
    int upgradeFileInfo(QString &filename, int angle, int sizeScale);

public slots:
};

#endif // IMAGEVIEWER_H
