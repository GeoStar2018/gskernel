GIS内核-对比两项影像的相关性[用于变化检测]

```c++
#include "spatialanalysis.h"
#include "spatialanalysishelp.h"

#include "geodatabase.h"

using namespace  GeoStar::Kernel;
using namespace  GeoStar::Utility::Data;
using namespace  GeoStar::Utility;

//如果两幅图像大小不一样,重采样为一样的
std::string Resample(const char * t1, const char * t2)
{
	GsFile ft1(t1), ft2(t2);
	if (!ft1.Exists() || !ft2.Exists())
		return "";
	GsRasterPtr ptrTif = new GsRaster;
	GeoStar::Kernel::GsFileGeoDatabaseFactory vFac;
	GeoStar::Kernel::GsConnectProperty vConn;

	vConn.Server = ft1.Parent().Path();
	GeoStar::Kernel::GsGeoDatabasePtr ptrDB = vFac.Open(vConn);
	GsRasterClassPtr ptrRasterClass1 = ptrDB->OpenRasterClass(ft1.Name());
	vConn.Server = ft1.Parent().Path();
	GeoStar::Kernel::GsGeoDatabasePtr ptrDB2 = vFac.Open(vConn);
	GsRasterClassPtr ptrRasterClass2 = ptrDB2->OpenRasterClass(ft2.Name());

	if (ptrRasterClass1->RasterColumnInfo().Width == ptrRasterClass2->RasterColumnInfo().Width &&
		ptrRasterClass1->RasterColumnInfo().Height == ptrRasterClass2->RasterColumnInfo().Height)
		return (char*)t2;
	GsOStringStream oss;
	oss << ft2.Name(false) << ptrRasterClass1->RasterColumnInfo().Width << "_" << ptrRasterClass2->RasterColumnInfo().Height << ".tif";
	std::string newname = GsFileSystem::Combine(ft2.Parent().FullPath(), oss.str().c_str());


	//计算写出的像素范围
	GsRasterColumnInfo info = ptrRasterClass1->RasterColumnInfo();
	double srcd[6] = { 0 };
	memcpy(srcd, ptrRasterClass1->RasterColumnInfo().GeoTransform, 6 * sizeof(double));
	double srcCell = srcd[1];
	int srcW = info.Width;
	int srcH = info.Height;
	double xyCellw = ptrRasterClass2->Extent().Width() / srcW;
	double xyCellh = ptrRasterClass2->Extent().Height() / srcH;
	srcd[1] = xyCellw;
	srcd[5] = -xyCellh;
	GsRasterColumnInfo columnInfo = info;
	memcpy(columnInfo.GeoTransform, (srcd), 48);
	columnInfo.Width = info.Width;;
	columnInfo.Height = info.Height;
	GsFile newn(newname.c_str());
	GsRasterClassPtr rasterClassDst = ptrDB->CreateRasterClass(newn.Name(),
		GsRasterCreateableFormat::eGTiff, columnInfo, ptrRasterClass1->SpatialReference());

	GsBox boundary = ptrRasterClass2->Extent();
	GsRect rect = ptrRasterClass2->ExtentToRange(boundary);
	GsRasterCursorPtr cursor = ptrRasterClass2->Search(rect, GsSize(info.Width, info.Height), GsRasterResampleAlg::eNearestNeighbour);
	GsRasterPtr ptrRaster = new GsRaster();
	while (cursor->Next(ptrRaster)) {
		rasterClassDst->WriteRaster(ptrRaster);
	}
	return newname;
}


//GsRasterQualityCompare 比较调用方式
GS_TEST(RasterSpatialAnalysis, GsRasterQualityCompare, chijing, 20210720)
{
	GsRasterPtr ptrTif = new GsRaster;
	GeoStar::Kernel::GsFileGeoDatabaseFactory vFac;
	GeoStar::Kernel::GsConnectProperty vConn;
	//GsFile f1(MakeInputFile("../testdata/rasterspatialanalysis/test_data/2019_1.tif"));
	//GsFile f2(MakeInputFile("../testdata/rasterspatialanalysis/test_data/2020_1.tif"));
	//GsFile f1(MakeInputFile("../testdata/rasterspatialanalysis/test_data/2019_330421170101202101110211000000.tif"));
	//GsFile f2(MakeInputFile("../testdata/rasterspatialanalysis/test_data/2020_330421170101202101110211000000.tif"));
	GsFile f1(MakeInputFile("../testdata/rasterspatialanalysis/test_data/2019_330421170101202101110128000000.tif"));
	GsFile f2(MakeInputFile("../testdata/rasterspatialanalysis/test_data/2020_330421170101202101110128000000.tif"));


	vConn.Server = f1.Parent().FullPath();
	GeoStar::Kernel::GsGeoDatabasePtr ptrDB = vFac.Open(vConn);
	GsRasterClassPtr ptrRasterClass1 = ptrDB->OpenRasterClass(f1.Name());

	vConn.Server = f2.Parent().FullPath();
	GeoStar::Kernel::GsGeoDatabasePtr ptrDB2 = vFac.Open(vConn);
	GsRasterClassPtr ptrRasterClass2 = ptrDB2->OpenRasterClass(f2.Name());

	if (ptrRasterClass1->RasterColumnInfo().Width != ptrRasterClass2->RasterColumnInfo().Width ||
		ptrRasterClass1->RasterColumnInfo().Height != ptrRasterClass2->RasterColumnInfo().Height)
	{
		std::string temppath = Resample(f1.FullPath(), f2.FullPath());
		GsFile f3(temppath.c_str());
		vConn.Server = f3.Parent().FullPath();
		GeoStar::Kernel::GsGeoDatabasePtr ptrDB2 = vFac.Open(vConn);
		ptrRasterClass2 = ptrDB2->OpenRasterClass(f3.Name());
	}
	GsRasterQualityComparePtr ptr = new GsRasterQualityCompare();
	ptr->NodataValue(ptrRasterClass2->RasterBand(0)->NoDataValue());
	double gg = ptr->Compare(ptrRasterClass1, ptrRasterClass2);
	std::cout << gg << std::endl;

}
```

