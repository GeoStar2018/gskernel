#ifndef MAPVIEW_H
#define MAPVIEW_H

#include <QDockWidget>
#include "geospace2d.h"
#include "ui_desktopshow.h"

using namespace GeoStar::Kernel::QT;
using namespace GeoStar::Kernel;

class MapView: public QDockWidget
{
	Q_OBJECT
	
public:
	MapView(QWidget *parent = 0);
	~MapView();
	GsGeoSpace2D* m_GeoSpace2D;
private slots:
	void QueryByGeometry(GsGeometry* geo);
private:
	Ui_desktopshow* m_ui;
};

#endif // MAPVIEW_H
