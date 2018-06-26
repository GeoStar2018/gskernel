#ifndef MAPVIEW_H
#define MAPVIEW_H

#include <QDockWidget>
#include "queryresult.h"
#include "utility.h"
#include "kernel.h"
#include "geospace2d.h"
using namespace GeoStar::Kernel::QT;
using namespace GeoStar::Kernel;

class MapView : public QDockWidget
{
    Q_OBJECT
    GsGeoSpace2D* m_GeoSpace2D;
	QueryResult m_QueryResult;
public:
    explicit MapView(QWidget *parent = 0);
	GsFeatureClassPtr  m_ptrFc;

	GsGeoSpace2D* GeoSpace(){return m_GeoSpace2D;}
signals:

public slots:
	void QueryByGeometry(GsGeometry* geo);
};

#endif // MAPVIEW_H
