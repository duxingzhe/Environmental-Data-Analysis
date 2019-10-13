#include "qabout.h"

#include <QFrame>
#include <QFont>
#include <QPalette>
#include <QDebug>

#define SAFE_FREE(p) {if(p!=NULL){delete p; p=NULL;}}

QAbout::QAbout(QWidget *parent) : QWidget(parent)
{
    initUiComponent();
}

QAbout::~QAbout()
{
    SAFE_FREE(titleLabel)
    SAFE_FREE(authorLabel)
    SAFE_FREE(infoLabel)
    SAFE_FREE(infoTextEdit)
    SAFE_FREE(exitButton)

}

void QAbout::exitButtonClicked(void)
{
    this->close();
}

void QAbout::initUiComponent(void)
{
    int label_w=300, label_h=20;
    int text_w=300, text_h=120;
    int btn_w=80, btn_h=30;
    int btn_x=QABOUT_WIDGET_WIDTH-btn_w;
    int btn_y=QABOUT_WIDGET_HEIGHT-btn_h;

    titleLabel=new QLabel(this);
    titleLabel->setText(tr("ImageViewer"));
    titleLabel->setGeometry(20, 10, label_w, label_h);

    QFont titleFont("Microsoft YaHei", 10, QFont::Bold);
    titleLabel->setFont(titleFont);

    exitButton=new QPushButton(this);
    exitButton->setText(tr("OK"));
    exitButton->setGeometry(btn_x-10, btn_y-5, btn_w, btn_y);
    connect(exitButton, SIGNAL(clicked(bool)), this, SLOT(exitButtonClicked()));
}
