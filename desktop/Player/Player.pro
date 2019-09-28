#-------------------------------------------------
#
# Project created by QtCreator 2019-08-30T21:06:57
#
#-------------------------------------------------

QT       += core gui

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = Player
TEMPLATE = app

# The following define makes your compiler emit warnings if you use
# any feature of Qt which has been marked as deprecated (the exact warnings
# depend on your compiler). Please consult the documentation of the
# deprecated API in order to know how to port your code away from it.
DEFINES += QT_DEPRECATED_WARNINGS

# You can also make your code fail to compile if you use deprecated APIs.
# In order to do so, uncomment the following line.
# You can also select to disable deprecated APIs only up to a certain version of Qt.
#DEFINES += QT_DISABLE_DEPRECATED_BEFORE=0x060000    # disables all the APIs deprecated before Qt 6.0.0

CONFIG += c++11

SOURCES += \
        audiodecoder.cpp \
        avpacketqueue.cpp \
        decoder.cpp \
        main.cpp \
        mainwindow.cpp

HEADERS += \
        audiodecoder.h \
        avpacketqueue.h \
        decoder.h \
        mainwindow.h

FORMS += \
        mainwindow.ui

unix:!macx:

INCLUDEPATH += /usr/local/include/
LIBS += `sdl2-config --cflags --libs` -lpthread -lavcodec  -lavformat  -lavutil  -lswscale -lswresample -lavdevice -lavfilter -lssl

win32:

LIBS += -L$$PWD/windows/ffmpeg/lib/ -lavcodec \
        -L$$PWD/windows/ffmpeg/lib/ -lavcdevice \
        -L$$PWD/windows/ffmpeg/lib/ -lavfilter \
        -L$$PWD/windows/ffmpeg/lib/ -lavformat \
        -L$$PWD/windows/ffmpeg/lib/ -lavutil \
        -L$$PWD/windows/ffmpeg/lib/ -lpostproc \
        -L$$PWD/windows/ffmpeg/lib/ -lswresample \
        -L$$PWD/windows/ffmpeg/lib/ -lswscale \
        -L$$PWD/windows/openssl/lib/ -lcryto \
        -L$$PWD/windows/openssl/lib/ -lssl \
        -L$$PWD/windows/sdl/lib/ -lSDL2

INCLUDEPATH += $$PWD/windows/ffmpeg/include \
                $$PWD/windows/openssl/include \
                $$PWD/windows/sdl/include
DEPENDPATH += $$PWD/windows/ffmpeg/include \
                $$PWD/windows/openssl/include \
                $$PWD/windows/sdl/include

unix:macx:

LIBS += -L$$PWD/mac/ffmpeg/lib/ -lavcodec \
            -L$$PWD/mac/ffmpeg/lib/ -lavcdevice \
            -L$$PWD/mac/ffmpeg/lib/ -lavfilter \
            -L$$PWD/mac/ffmpeg/lib/ -lavformat \
            -L$$PWD/mac/ffmpeg/lib/ -lavutil \
            -L$$PWD/mac/ffmpeg/lib/ -lpostproc \
            -L$$PWD/mac/ffmpeg/lib/ -lswresample \
            -L$$PWD/mac/ffmpeg/lib/ -lswscale \
            -L$$PWD/mac/openssl/lib/ -lcryto \
            -L$$PWD/mac/openssl/lib/ -lssl \
            -L$$PWD/mac/sdl/lib/ -lSDL2

INCLUDEPATH += $$PWD/mac/ffmpeg/include \
                $$PWD/mac/openssl/include
DEPENDPATH += $$PWD/mac/ffmpeg/include \
                $$PWD/mac/openssl/include
