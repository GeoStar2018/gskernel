#include "MainWindow.h"

MainWindow::MainWindow(QWidget *parent)
	: QMainWindow(parent)
{
	ui.setupUi(this);

	m_GeoSpace2D = new GeoStar::Kernel::QT::GsGeoSpace2D(this->windowHandle());
	QWidget* widget = QWidget::createWindowContainer(m_GeoSpace2D,this);
    m_GeoSpace2D->Widget(widget);
	setCentralWidget(widget);

	m_pLabelCoord = new QLabel(this);
	ui.statusBar->addWidget(m_pLabelCoord);

	connect(ui.actionAddData,SIGNAL(triggered()),
		    this,SLOT(AddData()));
	connect(ui.actionViewin,SIGNAL(triggered()),
            this,SLOT(ViewIn()));
	connect(ui.actionViewout,SIGNAL(triggered()),
            this,SLOT(ViewOut()));
	connect(ui.actionSelectByEnvelope,SIGNAL(triggered()),
            this,SLOT(SelectByEnvelope()));
	connect(ui.actionFullView,SIGNAL(triggered()),
            this,SLOT(FullView()));

}


void MainWindow::AddData()
{
	QDir dir(QCoreApplication::applicationDirPath());
	QString dataFile  = dir.absoluteFilePath("../example/data/");
	QByteArray byar = dataFile.toLatin1();  
	char *mm = byar.data();
	GeoStar::Kernel::GsConnectProperty conProp;
	conProp.Server = GeoStar::Utility::GsString(mm);
	conProp.DataSourceType = GeoStar::Kernel::eSqliteFile;

	GeoStar::Kernel::GsSqliteGeoDatabaseFactory pSqDBFac;
	GeoStar::Kernel::GsGeoDatabasePtr pDaBa = pSqDBFac.Open(conProp);
	GeoStar::Kernel::GsFeatureClassPtr pFeaCls = pDaBa->OpenFeatureClass("BOU2_4M_S.fcs");
	GeoStar::Kernel::GsFeatureLayerPtr pFeaLay = new GeoStar::Kernel::GsFeatureLayer(pFeaCls);
	GeoStar::Kernel::GsKernel::Initialize();

	m_GeoSpace2D->AddLayer(pFeaLay);
	ViewFullMap();
}
void MainWindow::ViewFullMap()
{
	m_GeoSpace2D->Map()->ViewExtent(m_GeoSpace2D->Map()->FullExtent());
    m_GeoSpace2D->Update();
}

void MainWindow::ViewIn()
{
	 m_GeoSpace2D->ZoomInEnv();
}

void MainWindow::ViewOut()
{
	m_GeoSpace2D->ZoomOutEnv();
}


void MainWindow::SelectByEnvelope()
{
	m_GeoSpace2D->SelectByEnvelope();
}

void MainWindow::FullView()
{
	ViewFullMap();
}


MainWindow::~MainWindow()
{
	delete m_GeoSpace2D;
	delete m_pLabelCoord;
}