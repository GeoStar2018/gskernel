GIS内核-地形转等值线示例
	#include "spatialanalysis.h"
	#include "spatialanalysishelp.h"
	#include <gstest.h>  
	#include "geodatabase.h"
	#include "json.h"
	using namespace  GeoStar::Kernel;
	using namespace  GeoStar::Utility::Data;
	using namespace  GeoStar::Utility;
	
	GS_TEST(GsRasterAnalysis, CheckPreprocesser, chijing, 20190917)
	{
		return;
		//创建fcs 用于保存等值线分析结果
		std::string fcsFolder = this->MakeInputFolder(GsEncoding::ToUtf8("rasteranalysis"));
		GsSqliteGeoDatabaseFactoryPtr fcsfac = new GsSqliteGeoDatabaseFactory();
		GsConnectProperty conn;
		conn.Server = GsUtf8(fcsFolder.c_str()).Str().c_str();
		conn.DataSourceType = eSqliteFile;
		GsGeoDatabasePtr pteDB = fcsfac->Open(conn);
		GsFeatureClassPtr pFcs = pteDB->OpenFeatureClass("worldIsoline_ASTGTM2_N25E108");
		if (pFcs)
			pFcs->Delete();
	
		GsFields fds;
		fds.Fields.emplace_back("id", GsFieldType::eIntType);
		fds.Fields.emplace_back("height", GsFieldType::eDoubleType);
		GsGeometryColumnInfo geoInfo;
		geoInfo.FeatureType = eSimpleFeature;
		geoInfo.GeometryType = eGeometryTypePolyline;
		geoInfo.XYDomain = GsBox(-180, -90, 180, 90);
		pFcs = pteDB->CreateFeatureClass("worldIsoline_ASTGTM2_N25E108", fds, geoInfo, new GsSpatialReference(4326));
		if (!pFcs)
			return;
		FeatureClassWrtier FeatureIO(pFcs.p);
	
		//打开栅格,用于查询数据
		GsFileGeoDatabaseFactoryPtr tiffac = new GsFileGeoDatabaseFactory();
		GsConnectProperty conn2;
		conn2.Server = GsUtf8(fcsFolder.c_str()).Str().c_str();
		conn2.DataSourceType = eFile;
		GsGeoDatabasePtr pteDB2 = tiffac->Open(conn2);
		GsRasterClassPtr pRcls = pteDB2->OpenRasterClass("dem_0_0_8.tif");
		if (!pRcls)
			return;
		//非地形数据不可用
		if (pRcls->BandCount() > 1)
		{
			return;
		}
	
		//构造栅格分析类
		GsRasterContourPtr ptrRaserAna = new GsRasterContour();
		//ptrRaserAna->RasterColumnInfo(pRcls->RasterColumnInfo());
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
			ptrDataCombineBlob->RasterClass(pRcls);
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
				unsigned char* tmp = ptrLineDataBlob->DataPtr();
				memcpy(ptmpdata, tmp, ptrLineDataBlob->DataLength());
				ptmpdata += ptrLineDataBlob->DataLength();
			}
			ptrDataCombineBlob->Width(pRcls->RasterColumnInfo().Width);
			ptrDataCombineBlob->Height(pRcls->RasterColumnInfo().Height);
			ptrDataCombineBlob->OffsetX(0);
			ptrDataCombineBlob->OffsetY(0);
			ptrDataCombineBlob->DataPtr(data, pRcls->RasterColumnInfo().Height* pRcls->RasterColumnInfo().Width* pixelsize);
			ptrRaserAna->ResolutionX(pRcls->RasterColumnInfo().GeoTransform[1]);
			ptrRaserAna->ResolutionY(pRcls->RasterColumnInfo().GeoTransform[5]);
			//自定义地形间隔
			//std::vector<double> lv = { 0,100,400,500,1000 };
			//ptrRaserAna->FixedLevels(&lv[0], lv.size());
			//double minvalue, maxvalue, c, d;
			//pRcls->RasterBand(0)->GetStatistics(minvalue, maxvalue, c, d);
			ptrRaserAna->ContourBase(50);
			ptrRaserAna->ContourBase(-9999);
			ptrRaserAna->ContourInterval(500);
			ptrRaserAna->GeometryDimType(1);
			ptrRaserAna->OutputData(&FeatureIO);
			ptrRaserAna->Contour(ptrDataCombineBlob);
			free(data);
		}
		//如果只有一块则丢进去直接处理
		else
		{
			GsRasterCursorPtr ptrRcursor = pRcls->Search(pRcls->ExtentToRange(pRcls->Extent()));
			ptrRaserAna->ContourBase(-9999);
			ptrRaserAna->ContourInterval(500);
			ptrRaserAna->GeometryDimType(1);
			ptrRaserAna->ResolutionX(pRcls->RasterColumnInfo().GeoTransform[1]);
			ptrRaserAna->ResolutionY(pRcls->RasterColumnInfo().GeoTransform[5]);
			ptrRaserAna->OutputData(&FeatureIO);
			GsRasterPtr ptrRDataBlob = new GsRaster();
			if (ptrRcursor->Next(ptrRDataBlob))
			{
				ptrRaserAna->Contour(ptrRDataBlob);
			}
		}
	}
	
	/*
		7, 4, 1
		8, 5, 2
		9, 6, 3
		13,12,11
	
			||
			\/
		1,2,3 ,11  
		4,5,6 ,12  
		7,8,9 ,13
	*/
	
	
	//矩阵行列转换
	void RowColumnTransposition(double * pData, int &w, int &h)
	{
		double * pdbltmp = (double*)malloc(sizeof(double)*w*h);
		memcpy(pdbltmp, pData, sizeof(double)*w*h);
		for (int i = 0; i < w; i++)
		{
			for (int j = 0; j < h; j++)
			{
				pData[i*h+j] = pdbltmp[j*w + i];
			}
		}
		free(pdbltmp);
		int tmp = w;
		w = h;
		h = tmp;
	}
	
	
	GS_TEST(GsRasterAnalysis, GRIDGeoJson, chijing, 20190917)
	{
	
		//创建fcs 用于保存等值线分析结果
		std::string fcsFolder = this->MakeInputFolder(GsEncoding::ToUtf8("rasteranalysis"));
		GsSqliteGeoDatabaseFactoryPtr fcsfac = new GsSqliteGeoDatabaseFactory();
		GsConnectProperty conn;
		conn.Server = GsUtf8(fcsFolder.c_str()).Str().c_str();
		conn.DataSourceType = eSqliteFile;
		GsGeoDatabasePtr pteDB = fcsfac->Open(conn);
		GsFeatureClassPtr pFcs = pteDB->OpenFeatureClass("gridrwp");
		if (pFcs)
			pFcs->Delete();
	
		GsFields fds;
		fds.Fields.emplace_back("id", GsFieldType::eIntType);
		fds.Fields.emplace_back("h", GsFieldType::eDoubleType);
		GsGeometryColumnInfo geoInfo;
		geoInfo.FeatureType = eSimpleFeature;
		//geoInfo.GeometryType = eGeometryTypePolyline;
		geoInfo.GeometryType = eGeometryTypePolygon;
		geoInfo.XYDomain = GsBox(-180, -90, 180, 90);
		pFcs = pteDB->CreateFeatureClass("gridrwp", fds, geoInfo, new GsSpatialReference(4326));
		if (!pFcs)
			return;
		FeatureClassWrtier FeatureIO(pFcs.p);
	
		//读取高程
		std::vector<double> pbuff;
		Json::Reader *pJsonParser = new Json::Reader();
		GsString ptrjsF = GsFileSystem::Combine(GsUtf8(fcsFolder.c_str()).Str().c_str(), "grid.json");
		GsFile file(ptrjsF.c_str());
		GsString strJson = file.ReadAll();
	
		Json::Value tempVal;
	
		if (!pJsonParser->parse(strJson.c_str(), tempVal)) {
			return;
		}
	
		double max = 0, min = 0;
		int w = 0;
		int h = tempVal.size();
		GsSimpleBitmapPtr bitmap = new GsSimpleBitmap(412, 501);
	
		bool flag = true;
		for (int i = 0; i < tempVal.size(); i++) {
			w = tempVal[i].size();
			unsigned char* pRow = (unsigned char*)bitmap->Row(i);
			for (int j = 0; j < tempVal[i].size(); j++)
				if (tempVal[i][j].isNull())
				{
					pbuff.emplace_back(-10);
					pRow[4*j] = (char)0;
					pRow[4*j+1] = (char)0;
					pRow[4*j+2] = (char)0;
					pRow[4*j+3] = (char)255;
				}
				else
				{
					double db = tempVal[i][j].asDouble();
					if (flag) {
						max = min = db;
						flag = false;
					}
					if (db > max) max = db;
					if (db < min) min = db;
	
					pbuff.emplace_back(db);
					pRow[4*j] = (char)125;
					pRow[4*j + 1] = (char)125;
					pRow[4*j + 2] = (char)125;
					pRow[4*j + 3] = (char)125;
				}
		}
		//查看数据的二值图像
		//bitmap->SavePNG("D:\\a.png");
		double dfContourInterval =  (max - min) / 10/2;
	
		double *pHead = &pbuff[0];
		//行列转置
		RowColumnTransposition(&pbuff[0], w, h);
	
		//构造栅格分析类
		GsRasterContourPtr ptrRaserAna = new GsRasterContour();
		ptrRaserAna->ContourInterval(dfContourInterval);
	
		ptrRaserAna->ResolutionX(0.00476953125);
		ptrRaserAna->ResolutionY(0.00476953125);
		ptrRaserAna->OutputData(&FeatureIO);
		ptrRaserAna->GeometryDimType(1);
	
		ptrRaserAna->SrcX(108.61524498800009);
		ptrRaserAna->SrcY(18.193182648400135);
		ptrRaserAna->Contour(&pbuff[0], w, h);
	}
	
