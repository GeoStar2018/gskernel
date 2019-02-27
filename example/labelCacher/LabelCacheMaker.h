#pragma once
#include <memory>
#include <kernel.h>
#include "demsupport.h"
#include "poitile.h"
class Progress
{
	int m_Total;
	int m_Count;
	int m_nProgress;
	UTILITY_NAME::GsLogger& m_log;
public:
	Progress(UTILITY_NAME::GsLogger& log, int nTotal) :m_log(log)
	{
		m_Total = nTotal;
		m_Count = 0;
		m_nProgress = -1;
	}
	void Add()
	{
		m_Count++;
		if (m_Total < 100)
			return;

		int p = 100.0 * m_Count / m_Total;
		if (p == m_nProgress)
			return;
		m_nProgress = p;
		m_log << UTILITY_NAME::eLOGTRACE << m_nProgress << "%(" << m_Count << "/" << m_Total << ")";

	}
};
//制作标注的缓存
struct TileRule
{
	int Level;
	double Delta;
	TileRule(int l, double delta)
	{
		Level = l;
		Delta = delta;
	}
};
class FeatureClassConfig
{
	bool m_EnableDefaultLevels;
	UTILITY_NAME::GsString m_strName;
	UTILITY_NAME::GsString m_strLabelFieldName;
	UTILITY_NAME::GsString m_strClassFieldName;
	std::map<UTILITY_NAME::GsString, std::vector<int> > m_Levels;

	std::vector<int> m_DefaultLevels;
public:
	FeatureClassConfig(const UTILITY_NAME::GsConfig& config);
	std::vector<int>& DefaultLevels();
	const std::vector<int>& QueryClassLevels(const char* value);
	UTILITY_NAME::GsString& Name();
	UTILITY_NAME::GsString& LabelFieldName();
	UTILITY_NAME::GsString& ClassFieldName();

};
typedef std::shared_ptr<FeatureClassConfig> FeatureClassConfigPtr;
class TileRuleFile
{
	std::vector<FeatureClassConfigPtr> m_FeaClass;
	std::vector<TileRule> m_Rules;
	std::vector<int> m_DefaultLevels;
	FeatureClassConfigPtr m_ptrDefault;
public:
	TileRuleFile(const char* file);

	FeatureClassConfigPtr FindClassConfig(const char* name);
	std::vector<TileRule>& Rules();
};
class LabelTile
{
protected:
	UTILITY_NAME::GsQuadKey m_Key;
public: 
	LabelTile(const UTILITY_NAME::GsQuadKey& key);
	virtual void WriteTo(KERNEL_NAME::GsTile* tile) = 0;
	~LabelTile();
	virtual void Add(KERNEL_NAME::GsPath* path,const KERNEL_NAME::GsRawPoint3D& ct,const char* name) = 0;
};
typedef std::shared_ptr<LabelTile> LabelTilePtr;

class JsonLabelTile :public LabelTile
{
	Json::Value m_TileData;
public:
	JsonLabelTile(const char* json, const UTILITY_NAME::GsQuadKey& key);
	JsonLabelTile(const UTILITY_NAME::GsQuadKey& key);
	virtual void WriteTo(KERNEL_NAME::GsTile* tile); 
	virtual void Add(KERNEL_NAME::GsPath* path, const KERNEL_NAME::GsRawPoint3D& ct, const char* name);

};
class PBFLabelTile :public LabelTile
{
	POITile m_TileData;
public:
	PBFLabelTile(const unsigned char* data,int nLen, const UTILITY_NAME::GsQuadKey& key);
	PBFLabelTile(const UTILITY_NAME::GsQuadKey& key);
	virtual void WriteTo(KERNEL_NAME::GsTile* tile);
	virtual void Add(KERNEL_NAME::GsPath* path, const KERNEL_NAME::GsRawPoint3D& ct, const char* name);
};

class TileCache:public UTILITY_NAME::GsSimpleLRUCache<UTILITY_NAME::GsQuadKey, LabelTilePtr>
{
	UTILITY_NAME::GsString m_Format;
	int m_nTileType;
protected:

	virtual void OnEraseItem(const UTILITY_NAME::GsQuadKey& k, LabelTilePtr& v)
	{
		if (!m_ptrOutputTile)
			m_ptrOutputTile = m_ptrOutputTileClass->CreateTile();
		if (m_nCommit == 0)
			m_ptrOutputTileClass->Transaction()->StartTransaction();
		m_nCommit++;
		v->WriteTo(m_ptrOutputTile);
		m_ptrOutputTile->TileType((KERNEL_NAME::GsTileEncodingType)m_nTileType);
		m_ptrOutputTile->Store();

		if (m_nCommit > 1000)
		{
			m_nCommit = 0;
			m_ptrOutputTileClass->Transaction()->CommitTransaction();
		}
	}
public:
	TileCache(const char* format,int nMax = 10000):GsSimpleLRUCache(nMax)
	{
		m_nCommit = 0;
		m_Format = format;
		if (UTILITY_NAME::GsStringHelp::IsNullOrEmpty(m_Format.c_str()))
			m_Format = "JSON";
		if (UTILITY_NAME::GsStringHelp::Compare(m_Format.c_str(), "JSON"))
			m_nTileType = 100;
		else
			m_nTileType = 101;

	}
	~TileCache()
	{
		UTILITY_NAME::GsList<UTILITY_NAME::GsPair<UTILITY_NAME::GsQuadKey, LabelTilePtr> >::iterator it = m_Data.begin();
		for (; it != m_Data.end(); it++)
		{
			OnEraseItem(it->first, it->second);
		}

		if(m_nCommit>0)
			m_ptrOutputTileClass->Transaction()->CommitTransaction();
	}

	KERNEL_NAME::GsTileClassPtr m_ptrOutputTileClass;
	KERNEL_NAME::GsTilePtr m_ptrOutputTile;
	int m_nCommit;
};
 
class LabelCacheMaker:DEMSupport
{ 
	bool m_bJsonFormat;
	//保存上一个值
	UTILITY_NAME::GsString m_strVal, m_strValClass;

	UTILITY_NAME::GsVector<KERNEL_NAME::GsGeometryPtr> m_vecCacheGeo;
	KERNEL_NAME::GsTileClassPtr m_ptrOutputTileClass;
	KERNEL_NAME::GsTilePtr m_ptrOutputTile;
	
	TileCache m_TileCache;

	KERNEL_NAME::GsPyramidPtr m_ptrPyramid;
	KERNEL_NAME::GsGeodesic m_Geodesic;
	KERNEL_NAME::GsBox m_Extent;
	UTILITY_NAME::GsString m_Folder; 
	void AddToTile(int l, int r, int c, KERNEL_NAME::GsPath* path, const KERNEL_NAME::GsRawPoint& middle,const char* name);
	void AddFeature(const TileRule& rule,const char* label, UTILITY_NAME::GsVector<KERNEL_NAME::GsPathPtr>& vec);
	void AddFeature(const TileRule& rule, const char* label, KERNEL_NAME::GsPath* path);
	void AddFeature(int level, const char* label, KERNEL_NAME::GsRawPoint* pt,int nLen);
	KERNEL_NAME::GsRawPoint MiddlePoint(double dblLen,KERNEL_NAME::GsRawPoint* pt, int nLen);

	void TraverseSegment(const TileRule& rule, KERNEL_NAME::GsGeometry* geo,const char* label);
	void TraverseSegment(const TileRule& rule, KERNEL_NAME::GsPath* path, const char* label);

	void AddGeometry(const char* strClass,const char* label, KERNEL_NAME::GsGeometry* geo);
	void CommitCache();
	LabelTilePtr OpenTile(const UTILITY_NAME::GsQuadKey& key);
	void AssignDEM(int l,KERNEL_NAME::GsPath* path);  
	FeatureClassConfigPtr m_ptrConfig;

	std::vector<TileRule> SelectRules(std::vector<int>& vecl);
	TileRuleFile m_TileRuleFile;
public:
	LabelCacheMaker(const char* config,const char* folder,const char* dem,const char* format);
	~LabelCacheMaker();
	
	void AddFeatureClass(KERNEL_NAME::GsFeatureClass* feaClass);


	void AddFeature(const char* strClass,const char* label, KERNEL_NAME::GsGeometry* geo);
	//将多个Geometry串接为一个Geometry
	KERNEL_NAME::GsGeometryPtr Combine(UTILITY_NAME::GsVector<KERNEL_NAME::GsGeometryPtr>& vecGeo);

};

