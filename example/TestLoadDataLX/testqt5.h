#ifndef TESTQT5_H
#define TESTQT5_H

#include <QtWidgets/QMainWindow>
#include "ui_testqt5.h"
#include "ui_treeview.h"
//#include "treeviewqt5.h"
#include "stdafx.h"
#include "kernel.h"
#include "utility.h"
#include "geospace2d.h"
#include <QtWidgets/QApplication>
 #include < QtWidgets/qsplitter.h >
#include "geodatabase.h"

class  treeviewqt5;
class MapView;

class testQT5 : public QMainWindow
{
	Q_OBJECT
public:
	testQT5(QWidget *parent = 0);
	~testQT5();

public slots:
	void helloworld();
	void Pan();
	void SelectByEnvelope();
	void Update();
	void SearchByEnvelope();
	void ADDDataPath();
	void ClearAllLayer();
public:
	void setLabelString();
	void showMap();
	GeoStar::Kernel::QT::GsGeoSpace2D* Get_GeoSpace();
	MapView * m_Mapview;
	treeviewqt5* m_LayerTree;

private:
	Ui::testQT5Class ui;
	//Ui_Form treeView_ui;
	GeoStar::Utility::GsConfig xx;
};

#endif // TESTQT5_H
