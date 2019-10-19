QT       += core gui

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

CONFIG += c++11

TEMPLATE = app

SOURCES += main.cpp \
    videoplayer.cpp \
    videoplayer.cpp \
    mainwindow.cpp

HEADERS  += \
    videoplayer.h \
    videoplayer.h \
    mainwindow.h

FORMS    += \
    mainwindow.ui

INCLUDEPATH += $$PWD/ffmpeg/include \
                $$PWD/src

LIBS += $$PWD/ffmpeg/lib/avcodec.lib \
        $$PWD/ffmpeg/lib/avdevice.lib \
        $$PWD/ffmpeg/lib/avfilter.lib \
        $$PWD/ffmpeg/lib/avformat.lib \
        $$PWD/ffmpeg/lib/avutil.lib \
        $$PWD/ffmpeg/lib/postproc.lib \
        $$PWD/ffmpeg/lib/swresample.lib \
        $$PWD/ffmpeg/lib/swscale.lib
