#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include "InitFile.h"

namespace Ui {
class MainWindow;
}

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    explicit MainWindow(QWidget *parent = 0);
    ~MainWindow();

private slots:

    void on_Sqlite_add_triggered();

    void on_action_view_triggered();

    void on_action_symbol_triggered();

private:
    Ui::MainWindow *ui;
	GeoStar::Kernel::QT::GsGeoSpace2D* m_GeoSpace2D;
	GeoStar::Kernel::GsFeatureClassPtr  m_ptrFeas;

	QString GetFeaValue(GeoStar::Kernel::GsFeature * pFea,GeoStar::Utility::Data::GsFieldType	pType,int i);
};

#endif // MAINWINDOW_H
