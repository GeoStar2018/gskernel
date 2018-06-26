#ifndef FEATURESVIEWDIALOG_H
#define FEATURESVIEWDIALOG_H
#include <QWidget>
#include <QDialog>

#include "InitFile.h"

namespace Ui {
class Dialog;
}

class Dialog : public QDialog
{
    Q_OBJECT

public:
    explicit Dialog(QWidget *parent = 0);
    ~Dialog();

//private slots:


private:
    Ui::Dialog *ui;
	//GeoStar::Kernel::GsFeatureClassPtr  m_ptrFeas;
};

#endif // MAINWINDOW_H
