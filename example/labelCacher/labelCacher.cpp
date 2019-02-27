// labelCacher.cpp : 定义控制台应用程序的入口点。
//

#include "stdafx.h"
#include "kernel.h"
#include "LabelCacheMaker.h"
#include "pc/pcgeodatabase.h"
#ifdef _DEBUG
#pragma comment (lib,"gsutilityd.lib")
#pragma comment (lib,"gsgeometryd.lib")
#pragma comment (lib,"gsmapd.lib")
#pragma comment (lib,"gssymbold.lib")
#pragma comment (lib,"gsgeodatabased.lib")
#pragma comment (lib,"gsspatialreferenced.lib")

#else
#pragma comment (lib,"gsutility.lib")
#pragma comment (lib,"gsgeometry.lib")
#pragma comment (lib,"gsmap.lib")
#pragma comment (lib,"gssymbol.lib")
#pragma comment (lib,"gsgeodatabase.lib")
#pragma comment (lib,"gsspatialreference.lib")
//#pragma comment (lib,"gspcgeodatabaseportd.lib")
#endif

using namespace KERNEL_NAME;
using namespace UTILITY_NAME;
#include <iostream>
/*
瓦片跨度，单位米
7       313086
8       156543
9       78271.5
10      39135.8
11      19567.9
12      9783.94
13      4891.97
14      2445.98
15      1222.99
16      611.496
17      305.748
18      152.874
19      76.437
*/
void PrintTileWidth()
{
	GsPyramidPtr ptrPyramid = new GsPyramid();
	for (int i = 0; i < 20; i++)
	{
		double span =ptrPyramid->TileSpanX(i, 0, 0);
		span = GsMath::ToRadian(span);
		span *= 6378137;
		std::cout << i << "\t" << span << std::endl;

	}
}
/*
void Convert(GsFeatureClass* feaClass, GsGeoDatabase* gdb)
{
	GsFeatureClassPtr ptrOut = gdb->OpenFeatureClass(feaClass->Name().c_str());
	if (ptrOut)
		ptrOut->Delete();
	GsFields fs = feaClass->Fields();
	fs.Fields.erase(fs.Fields.begin());
	fs.Fields.erase(fs.Fields.begin());
	ptrOut = gdb->CreateFeatureClass(feaClass->Name(),fs , feaClass->GeometryColumnInfo(), feaClass->SpatialReference());
	if (!ptrOut)
		return;
	Progress progress(GsLogger::Default(), feaClass->FeatureCount());

	GsFeatureCursorPtr ptrCursor = feaClass->Search();
	GsFeaturePtr ptrFea = ptrCursor->Next();
	if (!ptrFea)
		return;

	GsFeaturePtr ptrFeaOut = 0;
	int nCommit = 0;
	do
	{
		progress.Add();
		if (!ptrFeaOut)
			ptrFeaOut = ptrOut->CreateFeature();
		else
			ptrFeaOut->OID(0);
		ptrFeaOut->Geometry(ptrFea->GeometryBlob());

		ptrFeaOut->AssignAttribute(ptrFea);
		if (nCommit == 0)
			ptrOut->Transaction()->StartTransaction();
		nCommit++;
		ptrFeaOut->Store();

		if (nCommit > 10000)
		{
			nCommit = 0;
			ptrOut->Transaction()->CommitTransaction();
		}
	} while (ptrCursor->Next(ptrFea));
	if (nCommit > 0)
		ptrOut->Transaction()->CommitTransaction();

}
void ConvertGDB()
{
	GsConnectProperty conn;
	conn.Server = GsUtf8("G:\\01-Data\\Vector\\plant_new\\planet_new.gdb").Str();
	GsGeoDatabasePtr ptrGDB = GsGDBGeoDatabaseFactory().Open(conn);

	conn.Server = GsUtf8("G:\\01-Data\\Vector\\fcs").Str();
	GsGeoDatabasePtr ptrGDBOutput = GsSqliteGeoDatabaseFactory().Open(conn);

	GsVector<GsString> vecNames;
	ptrGDB->DataRoomNames(eFeatureClass, vecNames);
	GsVector<GsString>::iterator it = vecNames.begin();
	for (; it != vecNames.end(); it++)
	{
		GsFeatureClassPtr ptrFeaClass = ptrGDB->OpenFeatureClass(it->c_str());
		if (!ptrFeaClass)
			continue;
		Convert(ptrFeaClass, ptrGDBOutput);
	}


}*/ 
int main(int argc, char *argv[])
{
	//ConvertGDB();

	//PrintTileWidth();
	GsString strConfig;
	GsString strInputFolder;
	GsString strOutputFolder;
	GsString strDEM;
	GsString strFormat;
	for (int i = 1; i < argc; i++)
	{
		if(GsStringHelp::StartWith(argv[i],"-input:"))
			strInputFolder = GsUtf8(argv[i] + 7).Str();
		else if (GsStringHelp::StartWith(argv[i], "-output:"))
			strOutputFolder = GsUtf8(argv[i] + 8).Str();
		else if (GsStringHelp::StartWith(argv[i], "-config:"))
			strConfig = GsUtf8(argv[i] + 8).Str();
		else if (GsStringHelp::StartWith(argv[i], "-demtile:"))
			strDEM = GsUtf8(argv[i] + 9).Str();
		else if (GsStringHelp::StartWith(argv[i], "-format:"))
			strFormat = GsUtf8(argv[i] + 8).Str();
	}
	if (GsStringHelp::Compare(strFormat.c_str(), "PBF", true) != 0 &&
		GsStringHelp::Compare(strFormat.c_str(), "JSON", true) != 0)
		strFormat = "JSON";

	if (strConfig.empty() || strInputFolder.empty() || strOutputFolder.empty())
	{
		std::cout << "labelcacher [option]" << std::endl;
		std::cout << "-input:inputfolder   矢量地物类输入目录" << std::endl;
		std::cout << "-output:outputfolder 输出目录" << std::endl;
		std::cout << "-config:levelconfigfile 层级配置信息" << std::endl;
		std::cout << "-demtile:DEMtilepath DEM瓦片数据集路径" << std::endl;
		std::cout << "-format:[PBF|JSON] 输出格式" << std::endl;
		return 0;
	}
	int  i = 1;
	
	GsString strLog = strOutputFolder;
	GsDateTime now = GsDateTime::Now();
	std::stringstream ss;
	ss << now.Year() << "-" << now.Month() << "-" << now.Day() << "-" << now.Hour() << "-" << now.Minute() << "-" << now.Second() << ".log";
	strLog = GsFileSystem::Combine(strLog.c_str(), ss.str().c_str());

	GsFileCustomLogOutput logFile(strLog.c_str(), false, true);
	GsLogger::Default().CustomOutput(&logFile);
	GsLogger::Default().LogLevel(eLOGALL);
	GsLogger::Default().AutoFlush(true);

	GS_T << "begin to create label cache";
	GS_T << "intput folder: "<<strInputFolder;
	GS_T << "output folder: " << strOutputFolder; 
	GS_T << "config file: " << strConfig;
	 
	//输出目录
	LabelCacheMaker maker(strConfig.c_str(), strOutputFolder.c_str(), strDEM.c_str(), strFormat.c_str());
	GsStopWatch watch;
	watch.Start();

	GsConnectProperty conn;
	//输入
	conn.Server = strInputFolder;

	GsGeoDatabasePtr ptrGDB = GsSqliteGeoDatabaseFactory().Open(conn);
	if (!ptrGDB)
	{
		GS_E << "input folder is invalid";
		return 0;
	}

	GsVector<GsString> vecNames;
	ptrGDB->DataRoomNames(eFeatureClass, vecNames);
	if (vecNames.empty())
	{
		GS_E << "there has'nt any featureclass to open";
		return 0;
	}

	GsVector<GsString>::iterator itN = vecNames.begin();
	for (; itN != vecNames.end(); itN++)
	{
		GsFeatureClassPtr ptrFeaClass = ptrGDB->OpenFeatureClass(itN->c_str());
		if (!ptrFeaClass)
			continue;
		if (GsGeometry::GeometryTypeDimension(ptrFeaClass->GeometryType()) != 1)
			continue;

		GS_T << "process feature class "<<itN->c_str();
		maker.AddFeatureClass(ptrFeaClass);
	} 

	GS_T << "process finished cost " << watch.EscapedSecond() << "seconds";
	
	return 0;
}
