#include "mytstkernel.h"
#include <QFileDialog>
#include <QFile>

MyTstKernel::MyTstKernel(QWidget *parent)
    : QMainWindow(parent)
{

	createActions();
    //createMenus();
    createToolBars();

	m_MapView = new MapView(this);
    addDockWidget(Qt::RightDockWidgetArea, m_MapView);
}

MyTstKernel::~MyTstKernel()
{

}

void MyTstKernel::createActions()
{
    //"打开"动作
    openFileAction =new QAction(QIcon(":/images/open.png"),tr("打开"),this);
    openFileAction->setShortcut(tr("Ctrl+O"));
    openFileAction->setStatusTip(tr("open a file"));
    connect(openFileAction,SIGNAL(triggered()),this,SLOT(OpenFile()));


    //"放大"动作
    zoomInAction =new QAction(QIcon(":/images/zoomin.png"),tr("放大"),this);
    zoomInAction->setStatusTip(tr("zoomin a pic"));
    connect(zoomInAction,SIGNAL(triggered()),this,SLOT(ShowZoomIn()));

    //"缩小"动作
    zoomOutAction =new QAction(QIcon(":/images/zoomout.png"),tr("缩小"),this);
    zoomOutAction->setStatusTip(tr("zoomout a pic"));
    connect(zoomOutAction,SIGNAL(triggered()),this,SLOT(ShowZoomOut()));


    //"选择"动作
    selectAction =new QAction(QIcon(":/images/select.png"),tr("选择"),this);
    selectAction->setStatusTip(tr("Select By Envelope"));
    connect(selectAction,SIGNAL(triggered()),this,SLOT(SelectByEnve()));

    //"查询"动作
    queryAction =new QAction(QIcon(":/images/query.png"),tr("查询"),this);
    queryAction->setStatusTip(tr("Query By Envelope"));
    connect(queryAction,SIGNAL(triggered()),this,SLOT(QueryByEnve()));
}

void MyTstKernel::createMenus()
{
    zoomMenu =menuBar()->addMenu(tr("显示"));
    zoomMenu->addAction(zoomInAction);
    zoomMenu->addAction(zoomOutAction);

}

void MyTstKernel::createToolBars()
{
	//工具条
    fileTool = addToolBar("文件");
    fileTool->addAction(openFileAction);

    zoomTool = addToolBar("显示");
    zoomTool->addAction(zoomInAction);
    zoomTool->addAction(zoomOutAction);

    selectTool = addToolBar("选择");
    selectTool->addAction(selectAction);

    queryTool = addToolBar("查询");
    queryTool->addAction(queryAction);
}

void MyTstKernel::OpenFile()
{
    QString fileName =QFileDialog::getOpenFileName(this,"打开");
    if(!fileName.isEmpty())
	{
		QFileInfo file(fileName);
		GeoStar::Kernel::GsConnectProperty conn;
		conn.Server = file.dir().path().toStdString();
		GeoStar::Kernel::GsSqliteGeoDatabaseFactory fac;
		GeoStar::Kernel::GsGeoDatabasePtr ptrDb = fac.Open(conn);
		m_MapView->m_ptrFc = ptrDb->OpenFeatureClass(file.fileName().toStdString().c_str());
	}

	if(m_MapView->m_ptrFc)
	{
		m_MapView->GeoSpace()->AddLayer(GeoStar::Kernel::GsFeatureLayerPtr(new GeoStar::Kernel::GsFeatureLayer(m_MapView->m_ptrFc)));
		m_MapView->GeoSpace()->ViewExtent(m_MapView->m_ptrFc->Extent());
		m_MapView->GeoSpace()->Update();
	}
}

void MyTstKernel::ShowZoomIn()
{
	 m_MapView->GeoSpace()->ZoomInEnv();
}

void MyTstKernel::ShowZoomOut()
{
	m_MapView->GeoSpace()->ZoomOutEnv();
}


void MyTstKernel::SelectByEnve()
{
	m_MapView->GeoSpace()->SelectByEnvelope();
}

void MyTstKernel::QueryByEnve()
{
	m_MapView->GeoSpace()->QueryByEnvelope();
}