// AnalysisVectorTile.cpp : 定义控制台应用程序的入口点。
//

#include "stdafx.h"
#include <utility.h>
#include <kernel.h>
#include <gobject.inl>
#include <GeomathSE.cpp>
#include <omp.h>

#ifdef _WIN32
#include <Windows.h>
#include <sstream>
#ifdef _DEBUG
#pragma comment(lib,"gsgeodatabased.lib")
#pragma comment(lib,"gssymbold.lib")
#pragma comment(lib,"gsgeometryd.lib")
#pragma comment(lib,"gsutilityd.lib") 
#pragma comment(lib,"gsspatialreferenced.lib")  
#pragma comment(lib,"gsmapd.lib")
#else
#pragma comment(lib,"gsspatialreference.lib") 
#pragma comment(lib,"gsgeodatabase.lib")
#pragma comment(lib,"gssymbol.lib")
#pragma comment(lib,"gsgeometry.lib")
#pragma comment(lib,"gsutility.lib")  
#pragma comment(lib,"gsmap.lib")
#endif
#endif
#include <list>
#include "../../map/vectortile.h"
using namespace GeoStar;
using namespace GeoStar::Kernel;
using namespace GeoStar::Utility;
using namespace GeoStar::Utility::Data;



int main(int argc, char **argv)
{
	std::cout << "反解析矢量,保存成fcs" << std::endl;
	std::cout << "[矢量瓦片文件.tile] [层] [行] [列] [图层名称] [OID]" << std::endl;

	int i = 1;
	std::string strTilePath = GsEncoding::ToUtf8( argv[i++]);
	int level = GsCRT::_atoi64(argv[i++]);
	int row = GsCRT::_atoi64(argv[i++]);
	int col = GsCRT::_atoi64(argv[i++]);
	std::string strLayerName = argv[i++];
	long long	foid = GsCRT::_atoi64(argv[i++]); 

	GsFile file(strTilePath.c_str());
	GsSqliteGeoDatabaseFactoryPtr ptrFac = new GsSqliteGeoDatabaseFactory();
	GsConnectProperty conn;
	conn.Server = file.Parent().FullPath();
	GsGeoDatabasePtr ptrGeo = ptrFac->Open(conn);
	GsTileClassPtr pTcs = ptrGeo->OpenTileClass(file.Name().c_str());
	GsTilePtr pTile = pTcs->Tile(level, row, col);
	
	GsGrowByteBuffer buffer, unZipbuffer;
	long long length = 0; 
	unsigned char* pData= pTile->TileDataPtr(); 
	int nLen= pTile->TileDataLength();
	//如果是GZ压缩的则解压
	if (GsGZipFile::IsGZipCompressed((const unsigned char*)(pTile->TileDataPtr()), pTile->TileDataLength()))
	{
		if (GsGZipFile::Decompress(pData, nLen, &unZipbuffer))
		{
			pData = unZipbuffer.Ptr();
			nLen = unZipbuffer.BufferSize();
		}
	}
	else
	{
		pData = pTile->TileDataPtr();
		nLen = pTile->TileDataLength();
	}
	GsBox box;
	GsPyramidPtr ptrPyramid = pTcs->Pyramid();
	ptrPyramid->TileExtent(level, row, col, &box.XMin, &box.YMin, &box.XMax, &box.YMax);
	GsVectorTilePtr ptrVT = new GsVectorTile();
	ptrVT->TileData(pData, nLen);
	ptrVT->TileDataLength();
	ptrVT->Level(level);
	ptrVT->Row(row);
	ptrVT->Col(col);
	ptrVT->Extent(box);

	std::vector<std::string> layers1 = ptrVT->LayerNames();
	VectorTileLayer _layer = ptrVT->Layer(strLayerName.c_str());
	VectorTileFeature _fea = _layer.Feature(foid);
	_fea.m_box = box;
	if (_fea.ID() > 0)
	{
		std::cout << strLayerName << "图层中找到ID为" << foid << "的地物" << std::endl;
		GsGeometryPtr ptrGeometry = _fea.Geometry();

		GsWKTOGCWriter write;
		write.Write(ptrGeometry); 
		GsString str = write.WKT();
		std::cout << "WKT" << str.c_str() << std::endl;
	}
	else
	{
		std::cout << strLayerName <<"图层中未找到ID为"<< foid <<"的地物"<< std::endl;
	}

	std::cout << "开始将此瓦片的所有面图层保存输出到当前目录下的vector_out.fcs"<<"会将矢量瓦片屏幕坐标转为地理坐标"<< std::endl;
	std::cout << "vector_out 有LyaerName,SOID 字段表示一个地物在矢量瓦片属于那个图层和ID" <<std::endl;

	GsFeatureClassPtr ptrFcs = ptrGeo->OpenFeatureClass("vector_out");
	if (ptrFcs)
	{
		ptrFcs->Delete();
	}

	GsFields oFields;
	GsField oField1("OID", eInt64Type);
	GsField oFieldg("geometry", eGeometryType);
	GsField oField2("LyaerName", eStringType);
	GsField oField3("SOID", eInt64Type);

	oFields.Fields.push_back(oField1);
	oFields.Fields.push_back(oFieldg);
	oFields.Fields.push_back(oField2);
	oFields.Fields.push_back(oField3);
	GsGeometryColumnInfo oGeometryColumnInfo;
	oGeometryColumnInfo.FeatureType = eSimpleFeature;
	oGeometryColumnInfo.GeometryType = eGeometryTypePolygon;
	oGeometryColumnInfo.HasZ = false;
	oGeometryColumnInfo.MapScale = 400;
	/*oGeometryColumnInfo.XYDomain.XMax = 1000;
	oGeometryColumnInfo.XYDomain.XMin = 100;
	oGeometryColumnInfo.XYDomain.YMax = 800;
	oGeometryColumnInfo.XYDomain.YMin = 80;*/
	GsFeatureClassPtr pFoutcs =  ptrGeo->CreateFeatureClass("vector_out", oFields, oGeometryColumnInfo, pTcs->SpatialReference());
	
	pFoutcs->StartTransaction();
	GsFeaturePtr pOutFea = pFoutcs->CreateFeature();
	std::vector<std::string> layers = ptrVT->LayerNames();
	
	for (int i = 0; i < layers.size(); i++)
	{
		const char* atmp = layers[i].c_str();
		VectorTileLayer tmp_layer =  ptrVT->Layer(atmp);

		GsGeometryType type = tmp_layer.GeometryType();
		if (type != eGeometryTypePolygon && type!= eGeometryTypeRing)
			continue;
		VectorTileFeature tmp_VTFea =  tmp_layer.Next();
		while (tmp_VTFea.ID() > 0)
		{
			pOutFea->OID(-1);
			pOutFea->Geometry(tmp_VTFea.Geometry());
			pOutFea->Value(2, layers[i].c_str());
			pOutFea->Value(3, tmp_VTFea.ID());
			pOutFea->Store();
			tmp_VTFea = tmp_layer.Next();
		}
	}
	pFoutcs->CommitTransaction();
	pFoutcs->DeleteSpatialIndex();
	pFoutcs->CreateSpatialIndex();
	pFoutcs->CommitTransaction();
	system("pause");
	return 0;
}
int pbfRead(const char* strPbfPath)
{
	GsGrowByteBuffer buffer, unZipbuffer;
	GsFile file(strPbfPath);
	long long length = file.ReadAllBytes(&buffer);
	unsigned char* pData; int nLen;
	//如果是GZ压缩的则解压
	if (GsGZipFile::IsGZipCompressed(buffer.BufferHead(), length))
	{
		if (GsGZipFile::Decompress(buffer.BufferHead(), length, &unZipbuffer))
		{
			pData = unZipbuffer.Ptr();
			nLen = unZipbuffer.BufferSize();
		}
	}
	return 0;
}