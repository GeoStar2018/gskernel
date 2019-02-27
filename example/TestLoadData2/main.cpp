
#pragma comment(lib,"gsgeodatabased.lib")
#pragma comment(lib,"gsgeomathdd.lib")
#pragma comment(lib,"gsGeomathSE.lib")
#pragma comment(lib,"gsgeometryd.lib")
#pragma comment(lib,"gsmapd.lib")
#pragma comment(lib,"gspcgeodatabaseportd.lib")
#pragma comment(lib,"gsqtport.lib")
#pragma comment(lib,"gsspatialanalysisd.lib")
#pragma comment(lib,"gsspatialreferenced.lib")
#pragma comment(lib,"gssymbold.lib")
#pragma comment(lib,"gsutilityd.lib")
#pragma comment(lib,"gswin32portd.lib")

#include "MainWindow.h"
#include <QtWidgets/QApplication>
#include "geospace2d.h"
#include "utility.h"


int main(int argc, char *argv[])
{
	QApplication a(argc, argv);

	GeoStar::Kernel::GsKernel::Initialize();
	MainWindow w;
	//GeoStar::Kernel::QT::GsGeoSpace2D w;


	GeoStar::Kernel::GsConnectProperty cnnProperty;

	QDir dir(QCoreApplication::applicationDirPath());
	QString dataFile  = dir.absoluteFilePath("../example/data/");
	QByteArray ba = dataFile.toLatin1();  
    char *mm = ba.data();
    cnnProperty.Server = GeoStar::Utility::GsString(mm);
	cnnProperty.DataSourceType = GeoStar::Kernel::eSqliteFile;

	GeoStar::Kernel::GsGeoDatabasePtr pDB;

	GeoStar::Kernel::GsSqliteGeoDatabaseFactory pDBFac;
	pDB = pDBFac.Open(cnnProperty);
	if(pDB == nullptr)
	{
		std::cout<<"Open Database failed!"<<std::endl;
		return 1;
	}

	//pDB->DataRoomNames(GeoStar::Kernel::eFeatureClass,feaNames);

	//std::vector<string>::iterator i = feaNames.begin();

	//for(;i!=feaNames.end();i++)
	//{
	//	std::cout<<*i<<std::endl;
	//}

	GeoStar::Kernel::GsFeatureClassPtr pFeaCls = pDB->OpenFeatureClass("BOU2_4M_S.fcs");

	GeoStar::Kernel::GsFeatureLayerPtr pFealry = new GeoStar::Kernel::GsFeatureLayer(pFeaCls);
	w.GeoSpace()->AddLayer(pFealry);

	w.ViewFullMap();

	w.show();
	return a.exec();
}
