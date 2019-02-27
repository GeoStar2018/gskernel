#include "stdafx.h"
#include "Merger.h"
using namespace KERNEL_NAME;
using namespace UTILITY_NAME;
Merger::Merger(const char* input)
{
	m_ptrTileIndex = new GsRLETileKeyIndex();

	GsConnectProperty conn;
	conn.Server = input;
	GsGeoDatabasePtr ptrGDB =  GsSqliteGeoDatabaseFactory().Open(conn);
	GsVector<GsString> vecNames;
	ptrGDB->DataRoomNames(eFeatureClass, vecNames);

	GsVector<GsString>::iterator it = vecNames.begin();
	for (; it != vecNames.end(); it++)
	{
		GsFeatureClassPtr ptrFeaClass = ptrGDB->OpenFeatureClass(it->c_str());
		if (!ptrFeaClass)
			continue;
		GsPlaceNameManagerExtensionDataPtr ptrMgr = ptrFeaClass->ExtensionData();
		if (!ptrMgr)
			continue;
		m_vecInputPOI.push_back(ptrFeaClass);
	}
	GS_T << "there are " << m_vecInputPOI.size() << " datasets to merge";
	m_nCommit = 0;

}


Merger::~Merger()
{
}

bool Merger::Merge(const char* output)
{
	if (m_vecInputPOI.empty())
		return false;

	GsFile file(output);
	if (file.Exists())
		file.Delete();

	GsConnectProperty conn;
	conn.Server = file.Parent().FullPath();
	GsGeoDatabasePtr ptrGDB = GsSqliteGeoDatabaseFactory().Open(conn);

	m_ptrOutputPOI = ptrGDB->CreateFeatureClass(file.Name().c_str(), m_vecInputPOI.front()->Fields(),
		m_vecInputPOI.front()->GeometryColumnInfo(), m_vecInputPOI.front()->SpatialReference());

	if (!m_ptrOutputPOI)
		return false;

	GsPlaceNameManagerExtensionDataPtr ptrMgr = m_ptrOutputPOI->ExtensionData();
	if (!ptrMgr)
		return false;

	GsPlaceNameManagerExtensionDataPtr ptrMgrInput = m_vecInputPOI.front()->ExtensionData();
	GsPlaceNameMetadata meta = ptrMgrInput->Metadata();
	meta.Name = file.Name(false);
	meta.ClassName = meta.Name;
	meta.CoordinateDimension = 3;
	
	ptrMgr->Metadata(meta);
	//输出的瓦片类
	m_ptrTileClass = ptrMgr->PublishTileClass();
	int n = 0;
	int nTotal = m_vecInputPOI.size();
	std::vector<KERNEL_NAME::GsFeatureClassPtr>::iterator it = m_vecInputPOI.begin();
	for (; it != m_vecInputPOI.end(); it++)
	{
		GS_T << "merge dataset " << (*it)->Name() << "(" << n << "/" << nTotal << ")";
		ptrMgrInput = (*it)->ExtensionData();
		MergeMetadata(ptrMgrInput);

		//第一步、将所有的FeatureMerge进去。
		MergeFeature(*it);

		//第二部开始Merege瓦片。

		MergeTile(ptrMgrInput->PublishTileClass());

	}

	//提交所有未提交的内容。
	Commit();

	return true;
}
void Merger::Begin()
{
	if (m_nCommit == 0)
		m_ptrOutputPOI->Transaction()->StartTransaction();
}
void Merger::AddCommit()
{
	m_nCommit++;
	if (m_nCommit > 10000)
	{
		m_nCommit = 0;
		m_ptrOutputPOI->Transaction()->CommitTransaction();
	}
}
void Merger::Commit()
{
	if (m_nCommit == 0)
		return;
	m_nCommit = 0;
	m_ptrOutputPOI->Transaction()->CommitTransaction();
}
bool Exist(GsPlaceNameSymbolLibrary* lib, const GsPlaceNameSymbol &data,GsString& id)
{
	for (int i = 0; i < lib->Count(); i++)
	{
		GsPlaceNameSymbol d;
		if (!lib->QuerySymbol(i, d))
			continue;
		if (data == d)
		{
			id == d.ID;
			return true;
		}
	}
	return false;
}
bool Exist(GsIconLibrary* lib, const GsIconData &data,long long &id)
{
	for (int i = 0; i < lib->Count(); i++)
	{
		GsIconData d;
		if (!lib->IconData(i, d))
			continue;
		if (data == d)
		{
			id = d.ID;
			return true;
		}
	}
	return false;

}
long long MaxID(GsIconLibrary* iconLib)
{
	long long id = -1;
	for (int i = 0; i < iconLib->Count(); i++)
	{
		GsIconData data;
		iconLib->IconData(i, data);
		if (id < data.ID)
			id = data.ID;
	}
	return id;
}
bool Merger::MergeMetadata(GsPlaceNameManagerExtensionData* data)
{
	Begin(); 
	//合并图标
	GsPlaceNameManagerExtensionDataPtr ptrThisMeta = m_ptrOutputPOI->ExtensionData();
	GsIconLibrary* iconLib = ptrThisMeta->IconLibrary();
	GsIconLibrary* iconLibInput = data->IconLibrary();
	for (int i = 0; i < iconLibInput->Count(); i++)
	{
		GsIconData data;
		iconLibInput->IconData(i, data);
		long long id;
		//判断符号是否存在
		if (Exist(iconLib, data, id))
		{//如果存在的id和要写入的id不相同的话则认为id发生了变化。记录这个变化的映射关系
			if (data.ID != id)
				m_vMapIconID[data.ID] = id;
			continue;
		}
		//记录新的id。
		id = data.ID;
		if (id >= 1000)
		{
			long long nMax = MaxID(iconLib);
			if (nMax < 1000)
				data.ID = 1000;
			else
				data.ID = -1;
		}
		iconLib->SaveIconData(data);

		if (data.ID != id)
			m_vMapIconID[id] = data.ID;
	}


	std::map<GsString, GsString> vMapSymID;
	//合并符号
	GsPlaceNameSymbolLibrary* symLib = ptrThisMeta->SymbolLibrary();
	GsPlaceNameSymbolLibrary* symLibInput = data->SymbolLibrary();
	for (int i = 0; i < symLibInput->Count(); i++)
	{
		GsPlaceNameSymbol sym;
		symLibInput->QuerySymbol(i, sym);
		GsString id;
		if (Exist(symLib, sym, id))
		{
			if (id != sym.ID)
				vMapSymID[sym.ID] = id;
			continue;
		}
		symLib->SaveSymbol(sym);
	}

	GsPlaceNameClassifies *pClassify = ptrThisMeta->Classifies();
	GsPlaceNameClassifies *pClassifyInput = data->Classifies();
	for (int i = 0; i < pClassifyInput->Count(); i++)
	{
		GsPlaceNameClassify cls;
		pClassifyInput->QueryClassify(i, cls);
		std::map<long long, long long>::iterator itIcon = m_vMapIconID.find(cls.IconID);
		if (itIcon != m_vMapIconID.end())
			cls.IconID = itIcon->second;

		std::map<GsString, GsString>::iterator itSym = vMapSymID.find(cls.SymbolID);
		if (itSym != vMapSymID.end())
			cls.SymbolID = itSym->second;
		
		pClassify->SaveClassify(cls);
	}



	//合并配图
	Commit();
	return true;
}
//映射纠正OID，如果OID发生了变化的话
long long Merger::MapOID(long long oid)
{
	if (m_OIDMap.empty())
		return oid;

	GsStlMap<long long, long long>::iterator it = m_OIDMap.find(oid);
	if (it == m_OIDMap.end())
		return oid;
	return it->second;
}
bool Merger::MergeFeature(KERNEL_NAME::GsFeatureClass* feaClass)
{
	m_OIDMap.clear();

	GS_I << "MergeFeatureClass " << feaClass->Name();
	 
	Progress progress("Merge FeatureClass ", GsLogger::Default(), feaClass->FeatureCount());

	GsFeatureCursorPtr ptrCursor = feaClass->Search();
	GsFeaturePtr ptrFea = ptrCursor->Next();
	if (!ptrFea)
		return false;
	do
	{
		progress.Add();

		if (!m_ptrFea)
			m_ptrFea = m_ptrOutputPOI->CreateFeature();
		else
			m_ptrFea->OID(0);

		Begin();
		m_ptrFea->AssignAttribute(ptrFea);
		m_ptrFea->Geometry(ptrFea->GeometryBlob());
		m_ptrFea->Store();

		//如果新存储的地物的OID发生了变化则记录之
		if (ptrFea->OID() != m_ptrFea->OID())
			m_OIDMap[ptrFea->OID()] = m_ptrFea->OID();

		AddCommit();
	} while (ptrCursor->Next(ptrFea));


	return true;
}
bool Merger::MergeTile(KERNEL_NAME::GsTileClass* tileClass)
{
	GS_I << "MergeFeatureClass ";
	Progress progress("Merge TileClass ", GsLogger::Default(), tileClass->TileCount());

	GsTileCursorPtr ptrCursor = tileClass->Search();
	GsTilePtr ptrTile = ptrCursor->Next();
	if (!ptrTile)
		return false;
	 
	do
	{
		progress.Add();
		if (!m_ptrTile)
			m_ptrTile = m_ptrTileClass->CreateTile();

		const unsigned char* data = ptrTile->TileDataPtr();
		unsigned int nLen = ptrTile->TileDataLength(); 

		//将当前读取的数据进行处理。
		POITile poitile(data,nLen);
		/*std::string ss;
		poitile.Save(ss);

		POITile poitile1((const unsigned char*)ss.data(),ss.size());
		std::string ss1;
		poitile1.Save(ss1);

		FILE* f = fopen("d:\\a.bin", "wb");
		fwrite(data, nLen, 1, f);
		fclose(f);

		f = fopen("d:\\b.bin", "wb");
		fwrite(ss.data(), ss.size(), 1, f);
		fclose(f);*/

		GsQuadKey key(ptrTile->Level(), ptrTile->Row(), ptrTile->Col());
		ProcessTile(&poitile,key);

		//判断这个瓦片是是否存在于现有数据集中。
		if (m_ptrTileIndex->Exist(key))
		{
			//如果存在则读取数据出来
			if (m_ptrTileClass->Tile(key.Level, key.Row, key.Col, m_ptrTile))
			{
				data = m_ptrTile->TileDataPtr();
				nLen = m_ptrTile->TileDataLength();
				POITile poiOriTile(data, nLen);
				MergeTile(&poiOriTile, &poitile);
			}
		}
		
		//存储。
		std::string ss;
		poitile.Save(ss);

		Begin();
		m_ptrTile->Level(key.Level);
		m_ptrTile->Row(key.Row);
		m_ptrTile->Col(key.Col);
		m_ptrTile->TileData((const unsigned char*)ss.data(), ss.size());
		m_ptrTile->Store();
		AddCommit();
		m_ptrTileIndex->Add(key);

	} while (ptrCursor->Next(ptrTile));

	return true;
}
int FindStirngIndex(POITile* target, const char* str)
{
	for (int i = 0; i < target->StringTable().size(); i++)
	{
		if (strcmp(target->StringTable().at(i).c_str(), str) == 0)
			return i;
	}
	return -1;
}
//将第一个瓦片内容全部合并到第二个中
void Merger::MergeTile(POITile* tile, POITile* target)
{
	//首先合并两个stirngtable,记录新string的索引在新旧table中的映射关系
	std::map<int, int> strTabOffset;
	for (int i = 0; i < tile->StringTable().size(); i++)
	{
		int idx = FindStirngIndex(target, tile->StringTable().at(i).c_str());
		//如果找到了并且索引一致则不记录
		if (idx == i)
			continue;
		if (idx < 0)
		{
			idx = target->StringTable().size();
			target->StringTable().emplace_back(tile->StringTable().at(i));
		}
		strTabOffset[i] = idx;
	}
	target->reserve(target->size() + tile->size());
	std::vector<POI>::iterator it = tile->begin();
	for (; it != tile->end(); it++)
	{
		target->emplace_back(*it);
	
		std::map<int, int>::iterator it = strTabOffset.find(target->back().m_FontNameIndex);
		if (it != strTabOffset.end())
			target->back().m_FontNameIndex = it->second;
	}
}
//处理一下POI的瓦片数据
void Merger::ProcessTile(POITile* tile, const UTILITY_NAME::GsQuadKey& key)
{
	std::vector<POI>::iterator it = tile->begin();
	for (; it != tile->end(); it++)
	{
		it->m_OID = MapOID(it->m_OID);

		std::map<long long, long long>::iterator itID = m_vMapIconID.find(it->m_SymbolID);
		if (itID != m_vMapIconID.end())
			it->m_SymbolID = itID->second;
	}
}


AssignDEMMerger::AssignDEMMerger(const char* input, const char* dem):Merger(input), DEMSupport(dem)
{
	
}

//处理一下POI的瓦片数据
void AssignDEMMerger::ProcessTile(POITile* tile, const UTILITY_NAME::GsQuadKey& key)
{
	//首先调用父类，实现父类的处理
	Merger::ProcessTile(tile, key);
	 
	//首先查询DEM瓦片
	DEMTile* pDEM = EnsureTile(key);
	//找不到DEM则不干活
	if (!pDEM)
		return;

	
	//如果数据量比较大。
	if (tile->size() > 100)
	{
#pragma omp parallel for 
		for (int i =0;i<tile->size();i++)
		{
			Process(tile->at(i), pDEM);
		}
	}
	else
	{
		std::vector<POI>::iterator it = tile->begin();
		for (; it != tile->end(); it++)
		{
			Process(*it, pDEM);
		}
	}
	
}
void AssignDEMMerger::Process(POI& poi, DEMTile* dem)
{
	if (poi.m_Coordinates.size() < 3)
		poi.m_Coordinates.resize(3);

	for (int i = 0; i < poi.m_Coordinates.size(); i += 3)
	{
		double z = dem->DEMValue(poi.m_Coordinates[i], poi.m_Coordinates[i + 1], poi.m_Coordinates[i + 2]);
		poi.m_Coordinates[i + 2] = z;
	}
}