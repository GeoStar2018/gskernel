#include "test.h"
#include <QPushButton>
#include <QVBoxLayout>
#include <QDockWidget>

#include <QSplitter>
#include <QTextCodec>
#include <QTextEdit>

using namespace GeoStar::Kernel::QT;
#include <QTableWidget>

#ifdef WIN32
#ifdef _DEBUG
#pragma comment(lib,"gsgeodatabased.lib")
#pragma comment(lib,"gssymbold.lib")
#pragma comment(lib,"gsgeometryd.lib")
#pragma comment(lib,"gsmapd.lib") 
#pragma comment(lib,"gsutilityd.lib") 
#pragma comment(lib,"gsspatialreferenced.lib") 

#ifdef BUILD_QT 
#pragma comment(lib,"gsqtport.lib") 
#endif

#pragma comment(lib,"gswin32portd.lib") 
#pragma comment(lib,"gspcgeodatabaseportd.lib") 
#else
#pragma comment(lib,"gsgeodatabase.lib")
#pragma comment(lib,"gssymbol.lib")
#pragma comment(lib,"gsgeometry.lib")
#pragma comment(lib,"gsutility.lib") 
#pragma comment(lib,"gsspatialreference.lib") 
#pragma comment(lib,"gsmap.lib") 
#ifdef BUILD_QT 
#pragma comment(lib,"gsqtport.lib") 
#endif
#pragma comment(lib,"gswin32port.lib") 
#pragma comment(lib,"gspcgeodatabaseport.lib") 
#endif
#endif


Test::Test(QWidget *parent)
	: QMainWindow(parent)
{
	ui.setupUi(this);

	connect(ui.actionAaa, SIGNAL(triggered()), this, SLOT(AddData()));
	connect(ui.action, SIGNAL(triggered()), this, SLOT(ZoomInEnv()));
	connect(ui.action_2, SIGNAL(triggered()), this, SLOT(ZoomOutEnv()));
	connect(ui.ZoomPan, SIGNAL(triggered()), this, SLOT(ZoomPan()));
	connect(ui.FullMap, SIGNAL(triggered()), this, SLOT(FullMap()));
	connect(ui.SelectByEnvelop, SIGNAL(triggered()), this, SLOT(SelectByEnvelop()));
	connect(ui.QueryByEnv, SIGNAL(triggered()), this, SLOT(QueryByEnv()));

	m_MapView = new MapView(tr("MapView"), tr("map"), this);
	m_MapView->setMinimumHeight(600);
	m_MapView->setMinimumWidth(1000);
	m_MapView->showMinimized();
	addDockWidget(Qt::RightDockWidgetArea, m_MapView);

}

Test::~Test()
{

}

void Test::AddData()
{
	GeoStar::Kernel::GsSqliteGeoDatabaseFactoryPtr pData = new GeoStar::Kernel::GsSqliteGeoDatabaseFactory();
	GeoStar::Kernel::GsConnectProperty pConn;
 	QString path;
	QDir dir;
	path = dir.currentPath();
	pConn.Server = string((const char *)path.toLocal8Bit()) + "\\..\\data";
	pConn.DataSourceType = GeoStar::Kernel::eSqliteFile;
	GeoStar::Kernel::GsGeoDatabasePtr pDb = pData->Open(pConn);
	GeoStar::Kernel::GsFeatureClassPtr pFeaCls = pDb->OpenFeatureClass("BOU2_4M_S.fcs");
	m_MapView->m_GeoSpace2D->AddLayer(GeoStar::Kernel::GsFeatureLayerPtr(new GeoStar::Kernel::GsFeatureLayer(pFeaCls)));

	m_MapView->m_GeoSpace2D->Map()->ViewExtent(m_MapView->m_GeoSpace2D->Map()->FullExtent());
	m_MapView->m_GeoSpace2D->Update();
}

void Test::ZoomInEnv()
{
	m_MapView->m_GeoSpace2D->ZoomInEnv();
}

void Test::ZoomOutEnv()
{
	m_MapView->m_GeoSpace2D->ZoomOutEnv();
}

void Test::ZoomPan()
{
	m_MapView->m_GeoSpace2D->ZoomPan();
}

void Test::FullMap()
{
	m_MapView->m_GeoSpace2D->Map()->ViewExtent(m_MapView->m_GeoSpace2D->Map()->FullExtent());
	m_MapView->m_GeoSpace2D->Update();
}

void Test::SelectByEnvelop()
{
	m_MapView->m_GeoSpace2D->SelectByEnvelope();
}

void Test::QueryByEnv()
{
	m_MapView->m_GeoSpace2D->QueryByEnvelope();
}

