GIS内核-栅格切片tif -> tile


	#include <gstest.h>  
	#include "tilesplit.h"
	#include "spatialreference.h"
	#include "geodatabase.h"
	
	//#include "../map/vectortile.h"
	//
	bool MyPro(int a, int b, int c, long long d, long long e)
	{
		return true;
	}
	
	GS_TEST(VectorTileSplit,tilesplit,chijing,20161104)
	{
		this->RecordProperty("readme","矢量瓦片切割并存库-360度金字塔");
	
		// 数据库接口
		GeoStar::Kernel::GsFileGeoDatabaseFactory vFileFac;
		GsConnectProperty conn;
		conn.Server = GsUtf8("C:\\Users\\chijing\\Desktop").Str().c_str();
		GsGeoDatabasePtr ptrGeoTf =  vFileFac.Open(conn);
		GsRasterClassPtr pRaster =	ptrGeoTf->OpenRasterClass(GsUtf8("test.tif").Str().c_str());
	
	
		// 创建金字塔
		GsPyramidPtr ptrPyramid = new GsPyramid();
		ptrPyramid->CreateDefault();
	
		// 创建TileColumnInfo
		GsTileColumnInfo TileColinfo;
		TileColinfo.FeatureType = eImageTileFeature;
		TileColinfo.ValidTopLevel = 10;
		TileColinfo.ValidBottomLevel = 12;
		TileColinfo.XYDomain = pRaster->RasterColumnInfo().XYDomain;
	
	
		// 数据库接口
		GeoStar::Kernel::GsSqliteGeoDatabaseFactory vFac;
		GeoStar::Kernel::GsConnectProperty vConn;
		vConn.DataSourceType = eSqliteFile;
		vConn.Server = GeoStar::Utility::GsFileSystem::Combine( CurrentFolder().c_str(),"../testdata/sqlite");
		GeoStar::Kernel::GsGeoDatabasePtr ptrGDB = vFac.Open(vConn);
	
		// tileclass
		GsSpatialReferencePtr ptrSP = new GsSpatialReference(eCGCS2000);
		GsTileClassPtr ptrTileClass = ptrGDB->CreateTileClass("test10_12", ptrSP, ptrPyramid, TileColinfo);
		
		GsRasterTileSpliter rastersp(pRaster, ptrPyramid, ptrTileClass);
		rastersp.OnProgress.Add(&MyPro);
		rastersp.Execute(10, 12);
		rastersp.OnProgress.Remove(&MyPro);
	
	}