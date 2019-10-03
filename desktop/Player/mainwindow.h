#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include <QImage>
#include <QSystemTrayIcon>
#include <QTimer>
#include <QVector>
#include <QList>

#include "decoder.h"

namespace Ui {
class MainWindow;
}

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    explicit MainWindow(QWidget *parent = nullptr);
    ~MainWindow();

private:
    void paintEvent(QPaintEvent *event);
    void closeEvent(QCloseEvent *event);
    void changeEvent(QEvent *event);
    void keyReleaseEvent(QKeyEvent *event);
    void mouseMoveEvent(QMouseEvent *event);
    void mousePressEvent(QMouseEvent *event);
    void mouseDoubleClickEvent(QMouseEvent *event);
    bool eventFilter(QObject *obj, QEvent *event);

    void initUi();
    void initFFmpeg();
    void initSlot();
    void initTray();

    QString fileType(QString file);
    void addPathVideoToList(QString path);
    void playVideo(QString file);
    void playNext();
    void playPreview();
    void showPlayMenu();

    void setHide(QWidget *widget);
    void showControl(bool show);

    inline QString getFileNameFromPath(QString path);

    Ui::MainWindow *ui;

    Decoder *decoder;
    QList<QString> playList;

    QString currentPlay;
    QString currentPlayType;

    QTimer *menuTimer;
    QTimer *progressTimer;

    bool menuIsVisible;
    bool isKeepAspectRatio;

    QImage image;

    bool autoPlay;
    bool loopPlay;
    bool closeNotExit;

    Decoder::PlayState playState;

    QVector<QWidget *> hideVector;

    qint64 timeTotal;

    int seekInterval;

};

#endif // MAINWINDOW_H
