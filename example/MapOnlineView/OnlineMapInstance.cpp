#include "stdafx.h"
#include "OnlineMapInstance.h"
#include "MapOnlineViewDlg.h"


COnlineMapInstance::COnlineMapInstance(	GsPyramid* pPyramid, const CWnd& hWnd)
	: CMapInstance(pPyramid, hWnd)
{
}


COnlineMapInstance::~COnlineMapInstance()
{
}


bool COnlineMapInstance::Init(const char * format, GsSpatialReference* spatial)
{
	do {
		GsWebGeoDatabaseFactoryPtr webFactory = CreateClassGsWebGeoDatabaseFactory();
		if (!webFactory)
			break;
		GsConnectProperty connect;
		connect.Server = "";
		GsGeoDatabasePtr database = webFactory->Open(connect);
		if (!database)
			break;
		GsTileColumnInfo info;
		info.FeatureType = eDlgTileFeature;
		info.ValidBottomLevel = m_pPyramid->BottomLevelIndex;
		info.ValidTopLevel = m_pPyramid->TopLevelIndex;
		info.XYDomain = GsBox(m_pPyramid->XMin, m_pPyramid->YMin, m_pPyramid->XMax, m_pPyramid->YMax);
	
		GsSpatialReferencePtr ptrSpat = spatial;
		if (!ptrSpat)
			GsSpatialReferencePtr ptrSpat = new GsSpatialReference(eCGCS2000);

		GsTileClassPtr tileClass = database->CreateTileClass("img", ptrSpat, m_pPyramid, info);
		if (!tileClass)
			break;
		GsTMSTileClassPtr tms = tileClass;
		//tms->UrlTemplate(strUrlTemplate);
		tms->UrlTemplate(format);
		tms->TileType(GsTileEncodingType::ePngType);

		GsSqliteGeoDatabaseFactoryPtr sqliteFactory = CreateClassGsSqliteGeoDatabaseFactory();
		GsConnectProperty sqliteConnect;
		sqliteConnect.Server = "d:/1111";
		GsDir(sqliteConnect.Server.c_str()).Create();

		GsGeoDatabasePtr cacheDatabase = sqliteFactory->Open(sqliteConnect);
		GsTileClassPtr cacheTile = cacheDatabase->CreateTileClass("cache_img", ptrSpat, m_pPyramid, info);
		tms->Cache(cacheTile);

		GsBox mapBox;
		mapBox.XMin = m_pPyramid->XMin;
		mapBox.XMax = m_pPyramid->XMax;
		mapBox.YMin = m_pPyramid->YMin;
		mapBox.YMax = m_pPyramid->YMax;

		GsScreenDisplayPtr pScreenDis = CreateScreenDisplay(tileClass->SpatialReference(), mapBox);
		if (!pScreenDis)
			break;
		//pScreenDis->BindDevice(GsPaintDevice::CreatePaintDevice(eWin32HwndDeviceDC, m_hShowWnd));
		BindDevice(pScreenDis);

		GsTileLayerPtr pTileLayer = new GsTileLayer(tileClass);
		if (!pTileLayer)
			break;
		m_pMap = new GsMap(pScreenDis);
		if (!m_pMap)
			break;

		//最新特性，刷新请求通知
		GsUpdateAgent* ptr = new GsUpdateAgent();
		ptr->OnNeedUpdate.Add(this, &COnlineMapInstance::OnNeedUpdate);
		pTileLayer->UpdateAgent(ptr);

		m_pMap->Layers()->push_back(pTileLayer);

		//m_pMap->ViewExtent(GsBox(-180, 85, 180, -85));
		GsBox& box = m_pMap->FullExtent();
		m_pMap->ViewExtent(box);
		return true;
	} while (0);
	return false;
}
