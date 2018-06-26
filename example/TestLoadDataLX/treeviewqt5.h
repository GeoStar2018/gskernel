#ifndef TREEVIEWQT5_H
#define TREEVIEWQT5_H

#include <QDockWidget>
#include <QTreeView>
//#include "ui_treeview.h"
#include "testqt5.h"
#include "table.h"

class treeviewqt5 : public QDockWidget
{
	Q_OBJECT
public:
	treeviewqt5(QWidget *parent = 0,testQT5 * qt5 =0);
	~treeviewqt5();

	    QTreeView* m_pTreeView;
		Ui_Form * ui;
		testQT5 * m_qt5;
		table data_table ;
public slots:
		void showdata();
		void showtable();
public:
	   GeoStar::Kernel::GsFeatureClassPtr getFeatureClass();
	   QString treeviewqt5::GetFeaValue(GeoStar::Kernel::GsFeature * ptrFea,GeoStar::Utility::Data::GsFieldType	pType,int i);
};

#endif // TREEVIEWQT5_H
