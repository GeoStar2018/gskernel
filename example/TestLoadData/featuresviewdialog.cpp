#include "featuresviewdialog.h"
#include "ui_mainwindow.h"
#include "ui_featuresviewdialog.h"

Dialog::Dialog(QWidget *parent):	
	    QDialog(parent),ui(new Ui::Dialog)
{
	 //ui->setupUi(this);//(ui->tableView);
}


Dialog::~Dialog(void)
{
}
