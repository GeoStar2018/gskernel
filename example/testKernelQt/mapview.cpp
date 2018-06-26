#include "mapview.h"


MapView::MapView(QWidget *parent) : QDockWidget(parent)
{
	m_GeoSpace2D = new GsGeoSpace2D(this->windowHandle());
    QWidget *widget =QWidget::createWindowContainer(m_GeoSpace2D,this);
    m_GeoSpace2D->Widget(widget);
	setWidget(widget);


	connect(m_GeoSpace2D,SIGNAL(QueryByGeometry(GsGeometry*)),
		this,SLOT(QueryByGeometry(GsGeometry*)));
}


template<class T>
void Pick(std::vector<GsLayerPtr>* Lyrs,std::vector<T>& vec)
{
	std::vector<GsLayerPtr>::iterator it = Lyrs->begin();
	for(;it != Lyrs->end();it++)
	{
		T lyr = *it;
		if(lyr)
		{
			vec.push_back(lyr);
			continue;
		}

		GsMultiLayerPtr ptrMulti = *it;
		if(ptrMulti)
			Pick(ptrMulti->Layers(),vec);
	}
}

void MapView::QueryByGeometry(GeoStar::Kernel::GsGeometry* geo)
{
	GsSpatialQueryFilterPtr ptrQF(new GsSpatialQueryFilter(geo));
	std::vector<GsFeatureLayerPtr> vec;
	Pick<GsFeatureLayerPtr>(m_GeoSpace2D->Map()->Layers(),vec);
	if(vec.empty())
		return ;

	m_QueryResult.ShowResult(vec,ptrQF);
}

