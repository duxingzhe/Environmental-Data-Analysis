#include "mainwindow.h"
#include "ui_mainwindow.h"

MainWindow::MainWindow(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::MainWindow)
{
    ui->setupUi(this);
}

MainWindow::~MainWindow()
{
    delete ui;
}

bool MainWindow::GetNum(int &i)   //获得键盘输入的数据
{
    bool ok;
    i = QInputDialog::getInt(this, tr("Prossing..."),
        tr("Number: "), 0, 0, 1000, 1, &ok);
    if(!ok) return false;
    return true;
}

void MainWindow::on_AddBtn_clicked()   //Add槽函数
{
    int num;

    if(!GetNum(num)) return;   //未输入数据，则直接结束

    if(t.find(num))  //如果数据已经存在，根据红黑树的定义，树中不能出现相同节点，此时向用户反馈错误信息
        QMessageBox::warning(this, tr("Warning"), tr("Number Existing!"),QMessageBox::Abort);

    t.insert(num);  //插入

    QString str;

    t.print(str);   //显示

    ui->DisplayEdit->setText(str);

}

void MainWindow::on_DelBtn_clicked()  //Delete槽函数
{
    int num;

    if(!GetNum(num)) return;  //未输入数据，则直接结束

    if(!t.find(num))     //如果树中无此节点，向用户反馈错误信息
        QMessageBox::warning(this, tr("Warning"), tr("Can't Find!"),QMessageBox::Abort);

    t.erase(num);   //删除

    QString str;

    t.print(str);  //显示

    ui->DisplayEdit->setText(str);

}

void MainWindow::on_ResetBtn_clicked()   //Clear槽函数
{
    t.destroy(t.root());   //销毁红黑红树，释放节点
    t.root() = nullptr;
    ui->DisplayEdit->setText("");

}
