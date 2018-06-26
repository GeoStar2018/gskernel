#include "mainwindow.h"
#include "ui_mainwindow.h"
#include "ui_featuresviewdialog.h"
#include <QApplication>
#include <string>
#include <QDesktopWidget>
#include <QDir>
MainWindow::MainWindow(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::MainWindow)
{
	//this->setWindowFlags(Qt::FramelessWindowHint);
    ui->setupUi(this);
	m_GeoSpace2D = new GeoStar::Kernel::QT::GsGeoSpace2D(this->windowHandle());
    QWidget *widget =QWidget::createWindowContainer(m_GeoSpace2D,this);
    m_GeoSpace2D->Widget(widget);
	this->setCentralWidget(widget);
	//this->setwindows
}

MainWindow::~MainWindow()
{
    delete ui;
}

void MainWindow::on_Sqlite_add_triggered()
{
    //adddata
    GeoStar::Kernel::GsSqliteGeoDatabaseFactoryPtr ptrSqliteFac = new GeoStar::Kernel::GsSqliteGeoDatabaseFactory();
    GeoStar::Kernel::GsConnectProperty pConn;
	QDir dir(QCoreApplication::applicationDirPath());
    QString dataFile  = dir.absoluteFilePath("../example/data/");
	QByteArray ba = dataFile.toLatin1();  
    char *mm = ba.data();
    pConn.Database = GeoStar::Utility::GsString(mm);
    pConn.Server = GeoStar::Utility::GsString(mm);
    GeoStar::Kernel::GsGeoDatabasePtr pDB = ptrSqliteFac->Open(pConn);
    m_ptrFeas = pDB->OpenFeatureClass("BOU2_4M_S");
    GeoStar::Kernel::GsFeatureLayerPtr pLayer =  new GeoStar::Kernel::GsFeatureLayer();;
    pLayer->Name("BOU2_4M_S");
    pLayer->FeatureClass(m_ptrFeas);
    m_GeoSpace2D->AddLayer(pLayer);
    m_GeoSpace2D->ViewExtent(m_ptrFeas->Extent());
	//设置颜色
	GeoStar::Kernel::GsSimpleFillSymbolPtr ptrFill =  new GeoStar::Kernel::GsSimpleFillSymbol( GeoStar::Kernel::GsColor(65,65,65) );
	ptrFill->Outline(new GeoStar::Kernel::GsSimpleLineSymbol(GeoStar::Kernel::GsColor::White,0.1f));
	m_GeoSpace2D->Map()->ScreenDisplay()->BackColor(GeoStar::Kernel::GsColor(43,43,43));
	GeoStar::Kernel::GsSymbolPtr ptrSym = ptrFill.p;
	GeoStar::Kernel::GsSimpleFeatureRendererPtr	m_ptrRenderer= (pLayer->Renderer());new GeoStar::Kernel::GsSimpleFeatureRenderer(ptrSym);
	m_ptrRenderer->Symbol(ptrFill.p);
    m_GeoSpace2D->Update();
}

void MainWindow::on_action_view_triggered()
{
	Ui::Dialog ui;
	QDialog *d=new QDialog(this);
	ui.setupUi(d);
	//
	GeoStar::Kernel::GsFeatureCursorPtr pCur = m_ptrFeas->Search("");
	GeoStar::Kernel::GsFeaturePtr pFe = pCur->Next();
	
	QStandardItemModel  *model = new QStandardItemModel();
	model->setColumnCount(m_ptrFeas->Fields().Fields.size());
	for(int i = 0; i < m_ptrFeas->Fields().Fields.size(); i++)
	{
		QString pstr( m_ptrFeas->Fields().Fields.at(i).Name.c_str());
		model->setHeaderData(i,Qt::Horizontal,pstr);
	}
	ui.tableView->setModel(model);
	ui.tableView->horizontalHeader()->setStretchLastSection(true);
	ui.tableView->verticalHeader()->setDefaultSectionSize(20);
	ui.tableView->setSelectionBehavior(QAbstractItemView::SelectRows);
	//ui.tableView->horizontalHeader()->hide();
	ui.tableView->verticalHeader()->hide();
	int j = 0;
	do
	{
		QString strValue = QString::number(pFe->OID(), 10);
		model->setItem(j,0,new QStandardItem(strValue));
		for(int i = 1; i < m_ptrFeas->Fields().Fields.size(); i++)
		{
			QString qStr =  GetFeaValue(pFe,m_ptrFeas->Fields().Fields.at(i).Type,i);
			model->setItem(j,i,new QStandardItem(qStr));
			 model->item(j,i)->setForeground(QBrush(QColor(255, 255, 255)));
		}
		j++;
	} while(pCur->Next(pFe));
	d->show();
}


void MainWindow::on_action_symbol_triggered()
{
	//随机符号颜色
	GeoStar::Kernel::GsFeatureLayerPtr pLayer = m_GeoSpace2D->Map()->Layers()->at(0);
	GeoStar::Kernel::GsSimpleFillSymbolPtr ptrFill =  new GeoStar::Kernel::GsSimpleFillSymbol(GeoStar::Kernel::GsColor::Random() );
	ptrFill->Outline(new GeoStar::Kernel::GsSimpleLineSymbol(GeoStar::Kernel::GsColor::Random(),0.1f));
	//GeoStar::Kernel::GsSimpleFillSymbolPtr ptrFill =  new GeoStar::Kernel::GsSimpleFillSymbol( GeoStar::Kernel::GsColor(65,65,65) );
	//ptrFill->Outline(new GeoStar::Kernel::GsSimpleLineSymbol(GeoStar::Kernel::GsColor::White,0.1f));
	//m_GeoSpace2D->Map()->ScreenDisplay()->BackColor(GeoStar::Kernel::GsColor(43,43,43));
	GeoStar::Kernel::GsSymbolPtr ptrSym = ptrFill.p;
	GeoStar::Kernel::GsSimpleFeatureRendererPtr	m_ptrRenderer= (pLayer->Renderer());new GeoStar::Kernel::GsSimpleFeatureRenderer(ptrSym);
	m_ptrRenderer->Symbol(ptrFill.p);
	
	
	m_GeoSpace2D->Update();
}

QString MainWindow::GetFeaValue(GeoStar::Kernel::GsFeature * ptrFea,GeoStar::Utility::Data::GsFieldType	pType,int i)
{
	QString strValue("");
	switch (pType)
		{
		case GeoStar::Utility::Data::eIntType:
			{
				int nValue = ptrFea->ValueInt(i);
				strValue = QString::number(nValue, 10);
			}
			break;
		case GeoStar::Utility::Data::eUIntType:
			{
				unsigned int nValue = ptrFea->ValueUInt(i);
				strValue = QString::number(nValue, 10);
			}
			break;
		case GeoStar::Utility::Data::eInt64Type:
			{
				long long nValue = ptrFea->ValueInt64(i);
				strValue = QString::number(nValue, 10);
			}
			break;
		case GeoStar::Utility::Data::eUInt64Type:
			{
				unsigned long long nValue = ptrFea->ValueUInt64(i);
				strValue = QString::number(nValue, 10);
			}
			break;
		case GeoStar::Utility::Data::eFloatType:
			{
				strValue = QString("%1").number(ptrFea->ValueFloat(i));
			}
			break;
		case GeoStar::Utility::Data::eDoubleType:
			{
				double dblValue = ptrFea->ValueDouble(i);
				strValue = QString().number(dblValue, 'g', 8);
			}
			break;
		case GeoStar::Utility::Data::eStringType:
			{
				GeoStar::Utility::GsString tmpValue = ptrFea->ValueString(i);
				strValue = tmpValue.c_str();
			}
			break;
		case GeoStar::Utility::Data::eBlobType:
			{
				GeoStar::Utility::GsString tmpValue = "Blob";
				strValue = tmpValue.c_str();
			}
			break;
		case GeoStar::Utility::Data::eGeometryType:
			{
				//GeoStar::Utility::GsString tmpValue = 
				strValue = "GEOMETRY";
			}
		break;
		default:
			break;
		}
	return strValue;
}