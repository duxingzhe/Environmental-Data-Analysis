#include "mainwindow.h"
#include <QApplication>
#include <QSqlDatabase>
#include <QSqlError>
#include <QSqlQuery>

//连接数据库
bool createConnection()
{
    QSqlDatabase db;
    db = QSqlDatabase::addDatabase("QSQLITE");
    db.setDatabaseName("F:\\Project\\Environment\\database\\environmentDBForQt.db");
    if(!db.open())
    {
        return false;
    }
    return true;
}

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    if(!createConnection())
        return 1;
    MainWindow w;
    w.show();

    return a.exec();
}
