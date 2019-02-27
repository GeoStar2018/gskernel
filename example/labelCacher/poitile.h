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

//POI����
message PBPOI{
//����ΨһID
required uint64 OID = 1;
//����
required string Name =2;
//��������
repeated double Coordinates =3 [packed=true];
//��������
required enumGeometryType GeometryType = 4;
//���ʹ����ͣ�����MultiLineString��Polygon�ȸ���������Ч��
optional int32 Interate = 5;

//����ID
optional int32 SymbolID = 10  [default = 0];
//��ʾ�ĸ߶ȣ�ȱʡ32����
optional double DisplayHeight = 11 [default = 32];
//������ɫ
optional uint32 ShiningColor=12 [default =0];

//�������Ƶ�����
optional uint32	FontNameIndex=13 [default =0];
//�����С
optional int32	FontSize=14 [default =18];
//������ɫ��
optional uint32	FontColor=15 [default =0];
//����ά��
optional uint32	CoordinateDimension=16 [default =2];

};

//�ַ������������е��ַ���
message StringTable {
repeated string s = 1;
}

//��Ƭ����
message PBPOITile{
//��Ƭ�İ汾��
required int64 Version = 1;
//��Ƭ��Key
required int64 TileKey = 2;
//��Ƭ��StringTable�������ظ����ַ���
required StringTable StringTable = 3;
//���е�POI�������顣
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
	//����ΨһID
	unsigned long long m_OID;// = 1;
	//����
	std::string m_Name;// = 2;
	//��������
	std::vector<double> m_Coordinates;// = 3[packed = true];
	//��������
	enumGeometryType m_GeometryType;// = 4;
	//���ʹ����ͣ�����MultiLineString��Polygon�ȸ���������Ч��
	int m_Interate;// = 5;

	//����ID
	int m_SymbolID;// = 10[default = 0];
	//��ʾ�ĸ߶ȣ�ȱʡ32����
	double m_DisplayHeight;// = 11[default = 32];
	//������ɫ
	unsigned int m_ShiningColor;// = 12[default = 0];

	//�������Ƶ�����
	unsigned int m_FontNameIndex;// = 13[default = 0];
	//�����С
	unsigned int m_FontSize;// = 14[default = 18];
	//������ɫ��
	unsigned int m_FontColor;// = 15[default = 0];

	enumZCoordType m_ZCoordType;
	//���л�����
	void Save(std::string& data)const;
};
class POITile:public std::vector<POI>
{
	//��Ƭ�İ汾��
	long long  m_Version;// = 1;
	//��Ƭ��Key
	long long m_TileKey;// = 2;
	//��Ƭ��StringTable�������ظ����ַ���
	std::vector<std::string> m_StringTable;// = 3;
public:
	POITile();
	POITile(const unsigned char* data, size_t nLen);
	virtual ~POITile();
	long long& Version();
	long long& TileKey();
	std::vector<std::string>& StringTable();
	
	//���л�����
	void Save(std::string& data)const;

};
typedef std::shared_ptr<POITile> POITilePtr;
