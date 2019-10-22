#include "playerwindow.h"

#include <QApplication>
#include "playerwindow.h"
#include <QtAVWidgets>

int main(int argc, char *argv[])
{
    QtAV::Widgets::registerRenderers();
    QApplication a(argc, argv);
    PlayerWindow player;
    player.show();
    player.resize(800, 600);
    return a.exec();
}
