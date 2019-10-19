#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include <QInputDialog>
#include <rbtree.h>
#include <QString>
#include <QMessageBox>

namespace Ui {
class MainWindow;
}

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    explicit MainWindow(QWidget *parent = 0);
    ~MainWindow();

    bool GetNum(int &);  //获取输入数据

private slots:
    void on_AddBtn_clicked();    //Add槽函数

    void on_DelBtn_clicked();    //Delete槽函数

    void on_ResetBtn_clicked();  //Clear槽函数

private:
    RBTree t;
    Ui::MainWindow *ui;
};

#endif // MAINWINDOW_H
