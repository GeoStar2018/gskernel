#pragma once
#define _SCL_SECURE_NO_WARNINGS
#include <vector>
#include <string>
#include <memory>
/*
file::geopoi.proto

option optimize_for = LITE_RUNTIME;

package GEOPOI;

enum enumGeometryType {
ePoint = 0;
eMultiLineString = 1;
ePolygon = 2;
} ;

//POI对象
message PBPOI{
//对象唯一ID
required uint64 OID = 1;
//名称
required string Name =2;
//坐标数组
repeated double Coordinates =3 [packed=true];
//几何类型
required enumGeometryType GeometryType = 4;
//解释串类型，对于MultiLineString，Polygon等复杂类型有效。
optional int32 Interate = 5;

//符号ID
optional int32 SymbolID = 10  [default = 0];
//显示的高度，缺省32像素
optional double DisplayHeight = 11 [default = 32];
//光晕颜色
optional uint32 ShiningColor=12 [default =0];

//字体名称的索引
optional uint32	FontNameIndex=13 [default =0];
//字体大小
optional int32	FontSize=14 [default =18];
//字体颜色。
optional uint32	FontColor=15 [default =0];
//坐标维度
optional uint32	CoordinateDimension=16 [default =2];

};

//字符串表，保存所有的字符串
message StringTable {
repeated string s = 1;
}

//瓦片对象
message PBPOITile{
//瓦片的版本号
required int64 Version = 1;
//瓦片的Key
required int64 TileKey = 2;
//瓦片的StringTable，保存重复的字符串
required StringTable StringTable = 3;
//所有的POI对象数组。
repeated PBPOI POIS = 4;
};
*/

enum enumZCoordType {
	eCloseGround = 0,
	eCloseSeaSurface = 1,
	eRelativelyGround = 2,
	eAbsolute = 3
};
enum enumGeometryType {
	ePoint = 0,
	eMultiLineString = 1,
	ePolygon = 2,
};
class POI
{
public:
	POI(const unsigned char* data,size_t n); 
	POI();
public:
	//对象唯一ID
	unsigned long long m_OID;// = 1;
	//名称
	std::string m_Name;// = 2;
	//坐标数组
	std::vector<double> m_Coordinates;// = 3[packed = true];
	//几何类型
	enumGeometryType m_GeometryType;// = 4;
	//解释串类型，对于MultiLineString，Polygon等复杂类型有效。
	int m_Interate;// = 5;

	//符号ID
	int m_SymbolID;// = 10[default = 0];
	//显示的高度，缺省32像素
	double m_DisplayHeight;// = 11[default = 32];
	//光晕颜色
	unsigned int m_ShiningColor;// = 12[default = 0];

	//字体名称的索引
	unsigned int m_FontNameIndex;// = 13[default = 0];
	//字体大小
	unsigned int m_FontSize;// = 14[default = 18];
	//字体颜色。
	unsigned int m_FontColor;// = 15[default = 0];

	enumZCoordType m_ZCoordType;
	//序列化数据
	void Save(std::string& data)const;
};
class POITile:public std::vector<POI>
{
	//瓦片的版本号
	long long  m_Version;// = 1;
	//瓦片的Key
	long long m_TileKey;// = 2;
	//瓦片的StringTable，保存重复的字符串
	std::vector<std::string> m_StringTable;// = 3;
public:
	POITile();
	POITile(const unsigned char* data, size_t nLen);
	virtual ~POITile();
	long long& Version();
	long long& TileKey();
	std::vector<std::string>& StringTable();
	
	//序列化数据
	void Save(std::string& data)const;

};
typedef std::shared_ptr<POITile> POITilePtr;
