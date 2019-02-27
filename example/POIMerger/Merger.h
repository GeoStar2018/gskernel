#pragma once
#include <kernel.h>
#include <vector>
#include "poi.h"
#include "poitile.h"
#include "spatialindex.h"
#include "DEMSupport.h"
class Progress
{
	int m_Total;
	int m_Count;
	int m_nProgress;
	UTILITY_NAME::GsLogger& m_log;
	UTILITY_NAME::GsString m_strTitle;

public:
	Progress(const char* title,UTILITY_NAME::GsLogger& log, int nTotal) :m_log(log)
	{
		m_strTitle = title;
		m_Total = nTotal;
		m_Count = 0;
		m_nProgress = 0;
	}
	void Add()
	{
		m_Count++;
		if (m_Total < 1000)
			return;

		int p = 100.0 * m_Count / m_Total;
		if (p == m_nProgress)
			return;
		m_nProgress = p;
		m_log << UTILITY_NAME::eLOGTRACE <<m_strTitle<< m_nProgress << "%(" << m_Count << "/" << m_Total << ")";

	}
};

class Merger
{
	std::vector<KERNEL_NAME::GsFeatureClassPtr> m_vecInputPOI;
	
	KERNEL_NAME::GsTileKeyIndexPtr m_ptrTileIndex;

	KERNEL_NAME::GsFeatureClassPtr m_ptrOutputPOI;
	KERNEL_NAME::GsFeaturePtr m_ptrFea;
	KERNEL_NAME::GsTileClassPtr m_ptrTileClass;
	KERNEL_NAME::GsTilePtr m_ptrTile;

	//OIDӳ����򣬴洢��OID�����仯�����
	UTILITY_NAME::GsStlMap<long long, long long> m_OIDMap;

	std::map<long long, long long> m_vMapIconID;
	int m_nCommit;
	void AddCommit();
	void Commit();
	void Begin();
	bool MergeMetadata(KERNEL_NAME::GsPlaceNameManagerExtensionData* data);
	bool MergeFeature(KERNEL_NAME::GsFeatureClass* feaClass);
	bool MergeTile(KERNEL_NAME::GsTileClass* tileClass);
	//ӳ�����OID�����OID�����˱仯�Ļ�
	long long MapOID(long long oid);

	//����һ����Ƭ����ȫ���ϲ����ڶ�����
	void MergeTile(POITile* tile, POITile* target);
	

protected:
	//����һ��POI����Ƭ����
	virtual void ProcessTile(POITile* tile,const UTILITY_NAME::GsQuadKey& key);
public:
	Merger(const char* input);
	~Merger();
	bool Merge(const char* output);
};



//��DEM�̶߳����Merge
class AssignDEMMerger :public Merger, DEMSupport
{
	
	void Process(POI& poi, DEMTile* dem);
public:
	AssignDEMMerger(const char* input,const char* dem);

protected:
	//����һ��POI����Ƭ����
	virtual void ProcessTile(POITile* tile, const UTILITY_NAME::GsQuadKey& key);
};
