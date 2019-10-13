#ifndef QABOUT_H
#define QABOUT_H

#include <QWidget>
#include <QPushButton>
#include <QLabel>
#include <QTextEdit>

#define QABOUT_WIDGET_WIDTH 320
#define QABOUT_WIDGET_HEIGHT 240

class QAbout : public QWidget
{
    Q_OBJECT
public:
    explicit QAbout(QWidget *parent = nullptr);
    ~QAbout();

private:
    QPushButton *exitButton;
    QLabel *infoLabel;
    QLabel *titleLabel;
    QLabel *authorLabel;
    QTextEdit *infoTextEdit;
    void initUiComponent(void);

signals:

public slots:
    void exitButtonClicked(void);
};

#endif // QABOUT_H
