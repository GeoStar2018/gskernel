GIS内核-地形转等值线示例

	#include "spatialanalysis.h"
	#include "spatialanalysishelp.h"
	#include <gstest.h>  
	#include "geodatabase.h"
	using namespace  GeoStar::Kernel;
	using namespace  GeoStar::Utility::Data;
	using namespace  GeoStar::Utility;
	GS_TEST(GsRasterAnalysis, CheckPreprocesser, chijing, 20180917)
	{
		//创建fcs 用于保存等值线分析结果
		std::string fcsFolder = this->MakeInputFolder(GsEncoding::ToUtf8("rasteranalysis"));
		GsSqliteGeoDatabaseFactoryPtr fcsfac = new GsSqliteGeoDatabaseFactory();
		GsConnectProperty conn;
		conn.Server = GsUtf8( fcsFolder.c_str()).Str().c_str();
		conn.DataSourceType = eSqliteFile;
		GsGeoDatabasePtr pteDB =  fcsfac->Open(conn);
		GsFeatureClassPtr pFcs = pteDB->OpenFeatureClass("worldIsoline");
		if (pFcs)
			pFcs->Delete();
		
		GsFields fds;
		fds.Fields.emplace_back("id", GsFieldType::eIntType);
		fds.Fields.emplace_back("height", GsFieldType::eDoubleType);
		GsGeometryColumnInfo geoInfo;
		geoInfo.FeatureType = eSimpleFeature;
		geoInfo.GeometryType = eGeometryTypePolyline;
		geoInfo.XYDomain = GsBox(-180, -90, 180, 90);
		pFcs = pteDB->CreateFeatureClass("worldIsoline8", fds, geoInfo, new GsSpatialReference(4326));
		if (!pFcs)
			return;
		FeatureClassWrtier FeatureIO(pFcs.p);
	
		//打开栅格,用于查询数据
		GsFileGeoDatabaseFactoryPtr tiffac = new GsFileGeoDatabaseFactory();
		GsConnectProperty conn2;
		conn2.Server = GsUtf8(fcsFolder.c_str()).Str().c_str();
		conn2.DataSourceType = eFile;
		GsGeoDatabasePtr pteDB2 = tiffac->Open(conn2);
		GsRasterClassPtr pRcls = pteDB2->OpenRasterClass("dem_0_0_8");
		if (!pRcls)
			return;
		//非地形数据不可用
		if (pRcls->BandCount() > 1)
		{
			return;
		}
	
		//构造栅格分析类
		GsRasterContourPtr ptrRaserAna = new GsRasterContour();
		ptrRaserAna->RasterColumnInfo(pRcls->RasterColumnInfo());
		double Nodatavalue = pRcls->RasterBand(0)->NoDataValue();
		bool useNoData = true;
		ptrRaserAna->UseNoData(true);
		ptrRaserAna->NoDataValue(Nodatavalue);
		//如果只能分块读取,需要一个完整的大内存,这里需要外部分块给入, 不然几十G的内存块是new不出来的
		if (pRcls->RasterColumnInfo().BlockHeight != pRcls->RasterColumnInfo().Height || pRcls->RasterColumnInfo().BlockWidth != pRcls->RasterColumnInfo().Width)
		{
			//数据大小
			int pixelsize = GsRasterBand::RasterDataTypeBitSize(pRcls->RasterColumnInfo().DataType) / 8;
	
			GsRasterPtr ptrDataCombineBlob = new GsRaster();
			GsRasterPtr ptrLineDataBlob = new GsRaster();
			unsigned char* data = (unsigned char*)malloc(pRcls->RasterColumnInfo().Height* pRcls->RasterColumnInfo().Width* pixelsize);
			//内存分配不出来
			if (!data)
				return;
			GsRasterCursorPtr ptrRcursor = pRcls->Search(pRcls->ExtentToRange(pRcls->Extent()));
			if (!ptrRcursor)
				return;
			unsigned char* ptmpdata = data;
			while (ptrRcursor->Next(ptrLineDataBlob))
			{
				unsigned char* tmp= ptrLineDataBlob->DataPtr();
				memcpy(ptmpdata, tmp, ptrLineDataBlob->DataLength());
				ptmpdata += ptrLineDataBlob->DataLength();
			}
			ptrDataCombineBlob->Width(pRcls->RasterColumnInfo().Width);
			ptrDataCombineBlob->Height(pRcls->RasterColumnInfo().Height);
			ptrDataCombineBlob->OffsetX(0);
			ptrDataCombineBlob->OffsetY(0);
			ptrDataCombineBlob->DataPtr(data, pRcls->RasterColumnInfo().Height* pRcls->RasterColumnInfo().Width* pixelsize);
			ptrRaserAna->Contour(ptrDataCombineBlob, 500, 0, 0, NULL, &FeatureIO);
			free(data);
		}
		//如果只有一块则丢进去直接处理
		else
		{
			GsRasterCursorPtr ptrRcursor = pRcls->Search(pRcls->ExtentToRange(pRcls->Extent()));
			GsRasterPtr ptrRDataBlob = new GsRaster();
			if (ptrRcursor->Next(ptrRDataBlob))
			{
				ptrRaserAna->Contour(ptrRDataBlob, 500, 0, 0, NULL, &FeatureIO);
			}
		}
	}