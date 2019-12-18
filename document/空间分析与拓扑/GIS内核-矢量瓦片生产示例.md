

```c++

#include "tilesplit.h"
#include "mbvectortile.h"
#include "image.h"
#include <iostream>
#include <array>
#include "utility.h"
#include <kernel.h>
#include <pc/pcgeodatabase.h>
#include "../geomathse/Geomath2015.h"
//主要生产逻辑函数
bool TileMake(  const char* tileName)
{
	GsKernel::Initialize();
	GsPCGeoDatabase::Initialize();
	char fcs[256], *it;
	bool bRangeClip;

	GsFeatureClassPtr fcls;
	GsGeoDatabasePtr sourceDb, targetDb;
	GsTileClassPtr tileClass;
	GsFileVectorTileSpliter Spliter;

	//解析各种参数值
	int nMaxMem = 4;
	_ga().i4_set(geostar::ga_i4_max_pt_in_mem, nMaxMem * 1024 * 1024 * 64);

	GsString strDelTmpFiles = "true";
	GsSpatialReferencePtr ptrSpRef = new GsSpatialReference(4490);

	GsPyramidPtr ptrPy = new GsMultiPyramid();
	int nMinLevel = 5;
	int nMaxLevel = 6; 
	GsString strRangePath = u8"D:\\RangePath.fcs";
	int nTileSize = 256;
	double dTolExpand = 0.0625;
	double dCompressionRatio = 1; 
	int nObjMin = 0;
	int nStartUnionLevel = 0;
	int nTilePiexlExtent = 8192;
	if (nTilePiexlExtent < 256 || nTilePiexlExtent >8192)
		nTilePiexlExtent = 4096;

	GsString strSourcePath = u8"D:\\fcs";
	GsString strTmpPath = u8"D:\\tmp";
	GsString strTarPath = u8"D:\\tile";
	GsString strDataType = "FCS";
	GsString strConsistent = "false";//一致性检查

															  //处理范围
	GsGeometryPtr pGeoRange = NULL;
	if (!GsStringHelp::IsNullOrEmpty(strRangePath))
	{
		GsFile pFile(GsEncoding::ToUtf8(strRangePath));
		GsString strFileEx = GsStringHelp::ToUpper(pFile.Extension());
		GsString strRangeName = pFile.Name(false);
		GsGeoDatabasePtr pRangeDb = str2GeoDB(pFile.Parent().FullPath(), Convert(strFileEx));

		GsFeatureClassPtr pRangeFtrCls = pRangeDb->OpenFeatureClass(strRangeName.c_str());
		GsGeometryType pGeoType = pRangeFtrCls->GeometryType();
		if ((pGeoType != GsGeometryType::eGeometryTypePolygon) &&
			(pGeoType != GsGeometryType::eGeometryTypeEnvelope) &&
			(pGeoType != GsGeometryType::eGeometryTypeRing))
		{
			GS_E << "传入的范围不是面数据";
			return false;
		}
		else
		{
			GsFeatureCursorPtr Cursor = pRangeFtrCls->Search();
			GsFeaturePtr pFea = Cursor->Next();
			pGeoRange = pFea->Geometry();//带范围的情况下，就是这个范围
			GsString strClip = "FALSE";
			if (GsStringHelp::Compare(GsStringHelp::ToUpper(strClip), "TRUE") == 0)
				bRangeClip = true;
			else
				bRangeClip = false;
		}
	}

	if (nTileSize != 256)
	{
		ptrPy->TileSizeX = nTileSize;
		ptrPy->TileSizeY = nTileSize;
	}

	GsDataSourceType dataSourceType = Convert(strDataType.c_str());
	sourceDb = str2GeoDB(GsEncoding::ToUtf8(strSourcePath), dataSourceType);
	targetDb = str2GeoDB(GsEncoding::ToUtf8(strTarPath));
	GsString strTileName = GsEncoding::ToUtf8(tileName);
	tileClass = str2Tile(targetDb, strTileName.c_str(), nMinLevel, nMaxLevel, ptrSpRef, ptrPy);
	Spliter.TilePointCount(nObjMin);
	Spliter.Level(nMinLevel, nMaxLevel);
	Spliter.Pyramid(ptrPy);
	Spliter.TileClass(tileClass);
	Spliter.CachePath(strTmpPath.c_str());
	Spliter.StartUnionLevel(nStartUnionLevel);
	Spliter.CompressionRatio(dCompressionRatio);
	Spliter.TilePixelExtent(nTilePiexlExtent);


	//如果有范围裁切，首先在生成临时文件的时候就处理一下，减少临时文件的大小
	//取最小层级的瓦片的空间坐标范围
	if (pGeoRange.p)
		Spliter.FilterExtent(RangeEnv(pGeoRange, ptrPy, nMinLevel));

	GsVector<GsFeatureClassPtr> vecFtrCls;
	GsVector<GsString> vecNames;
	sourceDb->DataRoomNames(GsDataRoomType::eFeatureClass, vecNames);
	for (int i = 0; i < vecNames.size(); i++)
	{
		GsFeatureClassPtr pFcs = sourceDb->OpenFeatureClass(vecNames[i].c_str());
		//sprintf(prg_msg, "正在打包图层%s", GsEncoding::ToLocal(vecNames[i].c_str()).c_str());
		vecFtrCls.emplace_back(pFcs);
	}

	//数据库得情况下需要去遍历地物类集合下得数据
	if (dataSourceType == GsDataSourceType::eGDB)
	{
		vecNames.clear();
		GsVector<GsString> vecFolderNames;
		sourceDb->DataRoomNames(GsDataRoomType::eDataRoomFolder, vecFolderNames);
		for (int i = 0; i < vecFolderNames.size(); i++)
		{
			GsDataRoomFolderPtr ptrDataRoomFld = sourceDb->OpenDataRoomFolder(vecFolderNames[i].c_str());
			ptrDataRoomFld->DataRoomNames(GsDataRoomType::eFeatureClass, vecNames);
			for (int j = 0; j < vecNames.size(); j++)
			{
				GsFeatureClassPtr pFcs = sourceDb->OpenFeatureClass(vecNames[j].c_str());
				//sprintf(prg_msg, "正在打包图层%s.%s", GsEncoding::ToLocal(vecFolderNames[i].c_str()).c_str(), GsEncoding::ToLocal(vecNames[j].c_str()).c_str());
				vecFtrCls.emplace_back(pFcs);
			}
		}
	}

	if (vecFtrCls.size() < 1)
	{
		//表明所选择的目录下没有源数据，无法进行切片
		//sprintf(prg_msg, "源数据路径下没有可切片数据");
		return false;
	}

	for (int i = 0; i < vecFtrCls.size(); i++)
		Spliter.AddToGseFile(vecFtrCls[i], 0, 0, 0, nMinLevel, nMaxLevel);

	for (int i = nMaxLevel; i >= nMinLevel; --i)
	{
		//一个范围，在不裁切的情况下，每一个层级所占的瓦片的范围要重新设置
		if (pGeoRange.p)
			Spliter.FilterExtent(RangeEnv(pGeoRange, ptrPy, i));

		if (GsStringHelp::Compare(GsStringHelp::ToUpper(strConsistent), "TRUE") == 0)
			Spliter.Consistent(i);

		//顶层不压缩
		if (i == nMinLevel)
			Spliter.TilePointCount(0);

		Spliter.ClipCut(i, dTolExpand);
	}

	if (GsStringHelp::Compare(GsStringHelp::ToUpper(strDelTmpFiles), "TRUE") == 0)
	{
		//删除临时文件
		char fn[512];
		for (int i = nMaxLevel + 1; i >= nMinLevel; --i)
		{
			sprintf(fn, "%s\\level%02d.g2d", strTmpPath.c_str(), i);
			GsFile::Delete(fn);
		}
	}

	return true;
}
static GsEnvelopePtr RangeEnv(GsGeometry* pGeoRange, GsPyramid* pyr, int nLevel)
{
	GsEnvelopePtr pEnv = new GsEnvelope(pGeoRange->Envelope());
	int nMinRow, nMinCol, nMaxRow, nMaxCol;
	double dblXMin, dblYMin, dblXMax, dblYMax;
	pyr->TileIndexRange(pEnv->XMin(), pEnv->YMin(), pEnv->XMax(), pEnv->YMax(), nLevel, &nMinRow, &nMinCol, &nMaxRow, &nMaxCol);
	pyr->TileExtentRange(nLevel, nMinRow, nMinCol, nMaxRow, nMaxCol, &dblXMin, &dblYMin, &dblXMax, &dblYMax);
	return new GsEnvelope(dblXMin, dblYMin, dblXMax, dblYMax);
}

static GsDataSourceType Convert(const char* dataType)
{
	if (GsStringHelp::CompareNoCase(dataType, "FCS") == 0)
		return GsDataSourceType::eSqliteFile;
	else if (GsStringHelp::CompareNoCase(dataType, "SHP") == 0)
		return GsDataSourceType::eShapeFile;
	else if (GsStringHelp::CompareNoCase(dataType, "GDB") == 0)
		return GsDataSourceType::eGDB;

	return GsDataSourceType::eSqliteFile;
}

static GsGeoDatabasePtr str2GeoDB(const char* fcspath, GsDataSourceType eType = GsDataSourceType::eSqliteFile)
{
	GsGeoDatabaseFactoryPtr fac;
	if (eType == eSqliteFile)
		fac = new GsSqliteGeoDatabaseFactory();
	else if (eType == eShapeFile)
		fac = new GsShpGeoDatabaseFactory();
	else if (eType == eGDB)
		fac = GsClassFactory::CreateInstanceT<GsGeoDatabaseFactory>("GDBGeoDatabaseFactory");

	GsConnectProperty conn;
	conn.Server = fcspath;
	return fac->Open(conn);
}

static GsTileClassPtr str2Tile(GsGeoDatabase* db, const char* tile, int level0, int level1, GsSpatialReference* spr, GsPyramid* pyr)
{
	GsTileColumnInfo info;
	info.FeatureType = eDlgTileFeature;
	info.ValidBottomLevel = level0;
	info.ValidTopLevel = level1;
	info.XYDomain = GsBox();

	return db->CreateTileClass(tile, spr, pyr, info);
}
```


