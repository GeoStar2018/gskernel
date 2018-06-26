#include "testqt5.h"
#include < QSplitter >
#include <QStandardItemModel>
#include <QTreeWidget>
#include "treeviewqt5.h"
#include "CMap.h"
//#include "popdialog.h"

testQT5::testQT5(QWidget *parent)
	: QMainWindow(parent)
{
	ui.setupUi(this);
	
	m_LayerTree = new treeviewqt5( this,this);
	m_LayerTree->setMinimumWidth(300);
	addDockWidget(Qt::LeftDockWidgetArea, m_LayerTree);

	m_Mapview = new MapView(this);
	m_Mapview->setMinimumWidth(750);
	addDockWidget(Qt::RightDockWidgetArea, m_Mapview);


	connect(ui.action_pan,SIGNAL(triggered()),
		this,SLOT(Pan()));
	connect(ui.action_selectEnv,SIGNAL(triggered()),
		this,SLOT(SelectByEnvelope()));
	connect(ui.action_update,SIGNAL(triggered()),
		this,SLOT(SelectByEnvelope()));
	connect(ui.action_searchEnv,SIGNAL(triggered()),
		this,SLOT(SearchByEnvelope()));
	connect(ui.action_addSQLite,SIGNAL(triggered()),
		this,SLOT(ADDDataPath()));
	connect(ui.action_clearalllayer,SIGNAL(triggered()),
		this,SLOT(ClearAllLayer()));
}

testQT5::~testQT5()
{

}
void testQT5::Pan()
{
	m_Mapview->m_GeoSpace2D->ZoomPan();
}

void testQT5::SelectByEnvelope()
{
	m_Mapview->m_GeoSpace2D->SelectByEnvelope();
}

void testQT5::Update()
{
m_Mapview->m_GeoSpace2D->Update();
} 
void testQT5::SearchByEnvelope()
{
m_Mapview->m_GeoSpace2D->SelectByEnvelope();
}
void testQT5::helloworld()
{
  int  doNothing;
  doNothing =5;
}
GeoStar::Kernel::QT::GsGeoSpace2D* testQT5::Get_GeoSpace()
{
	return m_Mapview->m_GeoSpace2D;
}
void testQT5::showMap()
{
	;
}
void testQT5::ClearAllLayer()
{
	m_Mapview->m_GeoSpace2D->ClearLayers();
	m_Mapview->m_GeoSpace2D->Update();
}
void testQT5::ADDDataPath()
{
	QStandardItemModel* model = new QStandardItemModel(m_LayerTree->m_pTreeView);
	GeoStar::Kernel::GsConnectProperty cnnProperty;

	char szModuleFilePath[MAX_PATH];  
	char SaveResult[MAX_PATH];  
	int n = GetModuleFileNameA(0, szModuleFilePath, MAX_PATH);
	szModuleFilePath[ strrchr(szModuleFilePath, '\\') - szModuleFilePath + 1 ] = 0;
	strcpy(SaveResult,szModuleFilePath);  
	GeoStar::Utility::GsString path (SaveResult);
	path.append("../example/data");
	cnnProperty.Server = path;
	cnnProperty.DataSourceType = GeoStar::Kernel::eSqliteFile;
	GeoStar::Kernel::GsGeoDatabasePtr pDB;

	GeoStar::Kernel::GsSqliteGeoDatabaseFactory pDBFac;
	pDB = pDBFac.Open(cnnProperty);
	if(pDB == nullptr)
	{
		std::cout<<"Open Database failed!"<<std::endl;
		return   ;
	}
	if (pDB !=nullptr)
	{
	     std::vector<std::string> vecName;
		pDB->DataRoomNames(GeoStar::Kernel::eFeatureClass,vecName);

		model->setHorizontalHeaderLabels(QStringList()<<QStringLiteral("本地数据库文件"));  
		foreach(std::string  name, vecName)
		{
			QStandardItem *aitem =new QStandardItem;
			QString name_tmp=QString::fromStdString(name);
			aitem->setText(name_tmp);
			model->appendRow(aitem);  
		}

	}
	m_LayerTree->m_pTreeView->setModel(model);
}

