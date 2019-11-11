#include "mainwindow.h"
#include <QSqlDatabase>
#include <QDebug>
#include <QApplication>

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    QSqlDatabase m_db;
    m_db = QSqlDatabase::addDatabase("QMYSQL");
    m_db.setHostName("127.0.0.1");
    m_db.setPort(3306);
    m_db.setDatabaseName("environment");
    m_db.setUserName("root");
    m_db.setPassword("");
    if(!m_db.open()){
        qDebug() << "error";
        return 0;
    }else{
        qDebug() << "Success";
    }
    MainWindow w;
    w.show();
    return a.exec();
}
