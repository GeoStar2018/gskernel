#include "CMap.h"
//#include "mapproperty.h"
//using namespace GeoStar::Utility;

#include <QPushButton>
#include <QVBoxLayout>


MapView::MapView(QWidget *parent):QDockWidget(parent),m_ui(new Ui_desktopshow)
{
	m_ui->setupUi(this);

	m_GeoSpace2D = new GsGeoSpace2D(this->windowHandle());
	QWidget *widget =QWidget::createWindowContainer(m_GeoSpace2D,this);
	m_GeoSpace2D->Widget(widget);
	setWidget(widget);

	//connect(m_GeoSpace2D,SIGNAL(MouseDown(const GsMapMouseEvent&)),
	//	this,SLOT(MouseDown(const GsMapMouseEvent&)));

	//connect(m_GeoSpace2D,SIGNAL(MouseMove(const GsMapMouseEvent&)),
	//	this,SLOT(MouseMove(const GsMapMouseEvent&)));

	//connect(m_GeoSpace2D,SIGNAL(MouseUp(const GsMapMouseEvent&)),
	//	this,SLOT(MouseUp(const GsMapMouseEvent&)));

	//connect(m_GeoSpace2D,SIGNAL(QueryByGeometry(GsGeometry*)),
	//	this,SLOT(QueryByGeometry(GsGeometry*)));

	//GeoStar::Kernel::GsConnectProperty cnnProperty;
	//cnnProperty.Server = "F:/data";
	//cnnProperty.DataSourceType = GeoStar::Kernel::eSqliteFile;
	//GeoStar::Kernel::GsGeoDatabasePtr pDB;

	//GeoStar::Kernel::GsSqliteGeoDatabaseFactory pDBFac;
	//pDB = pDBFac.Open(cnnProperty);
	//if(pDB == nullptr)
	//{
	//	std::cout<<"Open Database failed!"<<std::endl;
	//	return  ;
	//}
	//GeoStar::Kernel::GsFeatureClassPtr pFeaCls = pDB->OpenFeatureClass("BOU1_4M_S.fcs");
	//GeoStar::Kernel::GsFeatureLayer pLayer(pFeaCls);

	//m_GeoSpace2D->ClearLayers();
	//m_GeoSpace2D->AddLayer(&pLayer);
	//m_GeoSpace2D->Update();

}

MapView::~MapView()
{

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

	//m_QueryResult.ShowResult(vec,ptrQF);

}
