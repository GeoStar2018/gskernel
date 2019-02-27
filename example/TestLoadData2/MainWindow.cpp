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

	connect(ui.actionViewin,SIGNAL(triggered()),
            this,SLOT(ViewIn()));
	connect(ui.actionViewout,SIGNAL(triggered()),
            this,SLOT(ViewOut()));
	connect(ui.actionSelectByEnvelope,SIGNAL(triggered()),
            this,SLOT(SelectByEnvelope()));
	connect(ui.actionFullView,SIGNAL(triggered()),
            this,SLOT(FullView()));
	connect(ui.actionUpdate,SIGNAL(triggered()),
            this,SLOT(Update()));
	connect(ui.actionMappan,SIGNAL(triggered()),
            this,SLOT(Pan()));

	connect(m_GeoSpace2D,SIGNAL(MouseMove(const GsMapMouseEvent&)),
            this,SLOT(MouseMove(const GsMapMouseEvent&)));
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

void MainWindow::Pan()
{
	m_GeoSpace2D->ZoomPan();
}

void MainWindow::SelectByEnvelope()
{
	m_GeoSpace2D->SelectByEnvelope();
}

void MainWindow::Update()
{
	m_GeoSpace2D->Update();
} 

void MainWindow::FullView()
{
	ViewFullMap();
}

void MainWindow::MouseMove(const GeoStar::Kernel::QT::GsMapMouseEvent& event)
{
    QString str;
    str.sprintf("X=%3.8f Y=%3.8f ,Sx=%d SY=%d",event.MapX(),event.MapY(),event.pos().x(),event.pos().y());
	m_pLabelCoord->setText(str);
}

MainWindow::~MainWindow()
{
	delete m_GeoSpace2D;
	delete m_pLabelCoord;
}