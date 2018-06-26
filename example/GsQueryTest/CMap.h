#ifndef MAPVIEW_H
#define MAPVIEW_H

#include <QDockWidget>
#include "geospace2d.h"
#include "queryresult.h"

using namespace GeoStar::Kernel::QT;
using namespace GeoStar::Kernel;

class MapView: public QDockWidget
{
	Q_OBJECT
public:
	MapView(const QString &config, const QString &title, QWidget *parent = 0);
	~MapView();
	GsGeoSpace2D* m_GeoSpace2D;
	QueryResult m_QueryResult;
private slots:
	void QueryByGeometry(GsGeometry* geo);
};

#endif // MAPVIEW_H
