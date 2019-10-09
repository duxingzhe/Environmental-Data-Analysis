QT += widgets
TARGET = ImageViewer

SOURCES += \
    main.cpp

RESOURCES += \
    images.qrc

INCLUDEPATH += mainwindow

include(mainwindow/mainwindow.pri)
include(imageviewer/imageviewer.pri)
