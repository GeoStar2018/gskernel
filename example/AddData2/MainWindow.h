#ifndef USEKERNELDEMO_H
#define USEKERNELDEMO_H

#include <QtWidgets/QMainWindow>
#include <QtWidgets/QMenuBar>
#include <QtWidgets/QToolBar>
#include <QtWidgets/qlayout.h>
#include "kernel.h"
#include "utility.h"
#include "geospace2d.h"
#include "ui_MainWindow.h"
#include <QtWidgets/QLabel>
#include <QtWidgets/QDockWidget>

class MainWindow : public QMainWindow
{
	Q_OBJECT
	GeoStar::Kernel::QT::GsGeoSpace2D* m_GeoSpace2D;
    QLabel* m_pLabelCoord;

public:
    GeoStar::Kernel::QT::GsGeoSpace2D* GeoSpace(){return m_GeoSpace2D;}

	void ViewFullMap();
	void ClearLayers();

	MainWindow(QWidget *parent = 0);
	~MainWindow();

public slots:
	void AddData();
	void ViewIn();
	void ViewOut();
	void SelectByEnvelope();
	void FullView();

private:
	Ui::MainWindowClass ui;
};

#endif // USEKERNELDEMO_H
