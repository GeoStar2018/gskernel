// wtl_openmapView.cpp : implementation of the CWtl_openmapView class
//
/////////////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "resource.h"

#include "wtl_basemapView.h"
#include <atltypes.h>
#include <logger.h>
using namespace GeoStar::Kernel;
using namespace GeoStar::Utility;
CWtl_basemapView::CWtl_basemapView()
{
	//自定义日志输出
	GsLogger::Default().LogLevel(eLOGALL);
	GsLogger::Default().CustomOutput(this);
	 
}
CWtl_basemapView::~CWtl_basemapView()
{
	m_ptrMap->Cancel();
	m_ptrMap.Release();
}
bool CWtl_basemapView::OnLog(const char* log)
{
	//输出到vs运行栏，方便调试
	OutputDebugStringA(log);
	return true;
}
BOOL CWtl_basemapView::PreTranslateMessage(MSG* pMsg)
{
	pMsg;
	return FALSE;
}
void CWtl_basemapView::OnExtentChanged(const KERNEL_NAME::GsBox& box)
{
	GS_T << "recive ExtentChanged event from tracker";
	m_ptrMap->Cancel();
	m_ptrMap->ViewExtent(box);
	m_ptrMap->Update();
}
bool CWtl_basemapView::OnUpdate(GsRefObject* ref, int reason, const GsBox& extent, double res, void* param)
{
	GsBox box = m_ptrMap->ViewExtent();
	if (box.IsDisjoin(extent))
		return true;

	m_UpdateRef.Increment();
	GS_T << "recive layer update message add ref to "<< m_UpdateRef.Read();

	return true;
}

LRESULT CWtl_basemapView::OnTimer(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
{
	if (m_UpdateRef.Read() <= 0)
		return S_OK;

	if (m_ptrMap->ScreenDisplay()->HasStartPan())
		return S_OK;

	if (m_ptrMap->ScreenDisplay()->IsDrawing())
		return S_OK;

	int n = m_UpdateRef;
	m_UpdateRef = 0;
	GS_T << "fire update by timer on updateRef="<< n;
	m_ptrMap->Cancel();
	m_ptrMap->Update();
	return S_OK;
}
GsLayerPtr OpenTMS()
{
	GsGeoDatabasePtr ptrGDB = GsWebGeoDatabaseFactory().Open(GsConnectProperty());
	GsWellknownTMSUriParser url(GsWellknownWebTileService::eTiandituImageGeographicGeoTile);
	GsTileColumnInfo col;
	col.FeatureType = eImageTileFeature;
	col.ValidTopLevel = url.TopLevel();
	col.ValidBottomLevel = url.BottomLevel();
	col.XYDomain = url.LayerExtent();
	GsTMSTileClassPtr ptrTileClass = ptrGDB->CreateTileClass(url.LayerName().c_str(), url.SpatialReference(), url.Pyramid(), col).p;
	ptrTileClass->UrlTemplate(url.FormatUri().c_str());

	GsTileLayerPtr ptrTileLayer = new GsTileLayer(ptrTileClass);
 
	return ptrTileLayer;

}
void CWtl_basemapView::CreateMap()
{
	if(m_ptrMap)
		return;

	GsKernel::Initialize();
	m_ptrAgent = new KERNEL_NAME::GsUpdateAgent();
	m_ptrAgent->OnNeedUpdate.Add(this, &CWtl_basemapView::OnUpdate);

	GeoStar::Kernel::GsPaintDevicePtr ptrDevice =
		GeoStar::Kernel::GsPaintDevice::CreatePaintDevice(
			GeoStar::Kernel::eWin32HwndDeviceD2D,m_hWnd);

	CRect rect;
	this->GetClientRect(&rect);
	  
	GeoStar::Kernel::GsDisplayTransformationPtr ptrDT = new 
		GeoStar::Kernel::GsDisplayTransformation(
		GsBox(0,0,rect.Width(),rect.Height()),
		GsRect(0,0,rect.Width(),rect.Height())); 

	GeoStar::Kernel::GsScreenDisplayPtr ptrDisplay = new 
		GsScreenDisplay(ptrDevice,ptrDT);


	m_ptrMap = new GsMap(ptrDisplay);

	GeoStar::Kernel::GsConnectProperty conn;
	conn.Server = GsFileSystem::Combine(GsFileSystem::WorkingFolder().c_str(), "../data/");

	GsSqliteGeoDatabaseFactory fac;
	GsGeoDatabasePtr ptrGDB = fac.Open(conn);
	GsFeatureClassPtr ptrFea = ptrGDB->OpenFeatureClass("CHN_ADM3");
	GsFeatureLayerPtr ptrFeaLyr = new GsFeatureLayer(ptrFea);
	GsBaseMapLayerPtr ptrBaseMap = new GsBaseMapLayer();
	ptrBaseMap->UpdateAgent(m_ptrAgent);


	ptrBaseMap->LayerCollection()->Add(ptrFeaLyr);
	 
	GsLayerPtr ptrLyr = OpenTMS();
	ptrLyr->UpdateAgent(m_ptrAgent);
	//m_ptrMap->Layers()->push_back(ptrLyr);
	m_ptrMap->Layers()->push_back(ptrBaseMap);

	//创建金字塔网格图层作为动态图层。
	//动态图层绘制的结果不会保存到缓存，因此每次刷新都会导致动态图层的绘制。
	GsPyramidLayerPtr ptrDynamicLyr = new GsPyramidLayer(GsPyramid::WellknownPyramid(e360DegreePyramid));
	m_ptrMap->DynamicLayerCollection()->Add(ptrDynamicLyr);


	m_ptrMap->ViewExtent(m_ptrMap->FullExtent());
	SetTimer(10010, 200);

	m_ptrPanTracker = new KERNEL_NAME::GsZoomPanClassicTracker(m_ptrMap);
	m_ptrPanTracker->OnExtentChanged.Add(this, &CWtl_basemapView::OnExtentChanged);
}

LRESULT CWtl_basemapView::OnMouseDown(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& /*bHandled*/)
{
	if (m_ptrPanTracker)
	{
		int x = GET_X_LPARAM(lParam);
		int y = GET_Y_LPARAM(lParam);
		m_ptrMap->Cancel();
		m_ptrPanTracker->OnMouseDown(KERNEL_NAME::eLeftButton, 0, GsPT(x, y));
	}
	return S_OK;
}
LRESULT CWtl_basemapView::OnMouseUp(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& /*bHandled*/)
{
	if (m_ptrPanTracker)
	{
		int x = GET_X_LPARAM(lParam);
		int y = GET_Y_LPARAM(lParam);

		m_ptrPanTracker->OnMouseUp(KERNEL_NAME::eLeftButton, 0, GsPT(x, y));
	}
	return S_OK;
}
LRESULT CWtl_basemapView::OnMouseWheel(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& /*bHandled*/)
{
	if (m_ptrPanTracker  )
	{

		CPoint pt(GET_X_LPARAM(lParam), GET_Y_LPARAM(lParam));
		ScreenToClient(&pt);

		int zDelta = GET_WHEEL_DELTA_WPARAM(wParam);
		m_ptrPanTracker->OnMouseWheel(KERNEL_NAME::eLeftButton, zDelta, 0, GsPT(pt.x,pt.y));
	}
	return S_OK;
}
LRESULT CWtl_basemapView::OnMouseMove(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& /*bHandled*/)
{
	if (m_ptrPanTracker && (wParam & MK_LBUTTON))
	{
		int x = GET_X_LPARAM(lParam);
		int y = GET_Y_LPARAM(lParam);

		m_ptrPanTracker->OnMouseMove(KERNEL_NAME::eLeftButton, 0, GsPT(x, y));
	}
	return S_OK;
}
LRESULT CWtl_basemapView::OnPaint(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
{
	CreateMap();
	CPaintDC dc(m_hWnd);

	m_ptrMap->Paint();
	//TODO: Add your drawing code here

	return 0;
}
LRESULT CWtl_basemapView::OnSize(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
{
	
	if(!m_ptrMap)
		return S_OK;

	CRect rect;
	this->GetClientRect(&rect);
	m_ptrMap->Cancel();
	m_ptrMap->ScreenDisplay()->DisplayTransformation()->DeviceExtent(GsRect(rect.left,rect.top,rect.Width(),rect.Height()));
	m_ptrMap->Invalidate();

	//m_ptrMap->Paint();
	//TODO: Add your drawing code here

	return 0;
}
