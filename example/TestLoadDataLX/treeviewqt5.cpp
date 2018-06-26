#include "treeviewqt5.h"
#include <QStyle>
#include <QStyleFactory>
#include <QApplication>
#include "CMap.h"

treeviewqt5::treeviewqt5( QWidget *parent,testQT5 * qt5) :QDockWidget(parent),
	ui(new Ui_Form)
{
	m_qt5 =qt5;
	ui->setupUi(this);
	 m_pTreeView = ui->treeView;
	//绑定到dock窗口
	setWidget(m_pTreeView);
}

treeviewqt5::~treeviewqt5()
{

}

void treeviewqt5::showtable()
{
	//表头
	GeoStar::Kernel::GsFeatureClassPtr pFeaCls = getFeatureClass();
	GeoStar::Kernel::GsFields pFields =pFeaCls->Fields() ;
	long long nCountX =pFields.Fields.size();
	long long nCountY = (int)pFeaCls->FeatureCount();
	data_table.ui.tableWidget->setWindowTitle("table");
	data_table.ui.tableWidget->setColumnCount(nCountX); // 设置表格控件列大小  
	data_table.ui.tableWidget->setRowCount(nCountY); // 设置表格控件行的大小 
	QStringList strList;  
	foreach(GeoStar::Kernel::GsField field ,pFields.Fields)
	{
		GeoStar::Utility::GsString name_tmp =field.Name;
		QString q_name_tmp =QString::fromStdString(name_tmp);
		strList <<q_name_tmp ;  
	}
	data_table.ui.tableWidget->setHorizontalHeaderLabels(strList); // 设置表格控件，列表头内容  

	for (long long i=0;i<nCountX;++i)
	{
			GeoStar::Kernel::GsFeaturePtr pFea = pFeaCls->Feature(i);
			if (pFea ==nullptr)
			{
				continue;
			}
			GeoStar::Kernel::GsRowPtr pRow =pFea ;
			GeoStar::Utility::GsString name_tmp;
			for (long long j=0;j<nCountY;++j)
			{
				 //name_tmp = pRow->ValueString(j);
				 //item.setText(QString::fromStdString(name_tmp));
				 //name_tmp = pRow->ValueString(j);
				QString name_tmp =GetFeaValue(pFea,pFeaCls->Fields().Fields.at(i).Type,i);
				//QString name_tmp("xxx");
				//item->setText(name_tmp);
				data_table.ui.tableWidget-> setItem((int)j, (int)i, new QTableWidgetItem(name_tmp));
			}
	}

	data_table.show();
}
QString treeviewqt5::GetFeaValue(GeoStar::Kernel::GsFeature * ptrFea,GeoStar::Utility::Data::GsFieldType	pType,int i)
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
void treeviewqt5::showdata()
{
	GeoStar::Kernel::GsFeatureClassPtr pFeaCls = getFeatureClass();
	GeoStar::Kernel::GsFeatureLayerPtr pLayer = new GeoStar::Kernel::GsFeatureLayer(pFeaCls);

	m_qt5->m_Mapview->m_GeoSpace2D->ClearLayers();
	m_qt5->m_Mapview->m_GeoSpace2D->AddLayer(pLayer);
	m_qt5->m_Mapview->m_GeoSpace2D->Update();
}
GeoStar::Kernel::GsFeatureClassPtr treeviewqt5::getFeatureClass()
{
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
		std::cout<<"Open Database failed, 指定路径没有数据!"<<std::endl;
		return NULL ;
	}

	QStandardItemModel* model = static_cast<QStandardItemModel*>(ui->treeView->model());
	QModelIndex currentIndex = ui->treeView->currentIndex();
	QStandardItem* currentItem = model->itemFromIndex(currentIndex);
	QString name =currentItem->text() ;
	std::string c_name = name.toStdString();

	GeoStar::Kernel::GsFeatureClassPtr pFeaCls = pDB->OpenFeatureClass(c_name.c_str());
	return pFeaCls;
}