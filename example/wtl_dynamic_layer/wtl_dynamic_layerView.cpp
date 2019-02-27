// wtl_openmapView.cpp : implementation of the CWtl_openmapView class
//
/////////////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "resource.h"

#include "wtl_dynamic_layerView.h"
#include <atltypes.h>
using namespace GeoStar::Kernel;
using namespace GeoStar::Utility;
CWtl_dynamic_layerView::CWtl_dynamic_layerView()
{
	//自定义日志输出
	GsLogger::Default().LogLevel(eLOGALL);
	GsLogger::Default().CustomOutput(this);

	m_Cursor = NULL;
	m_CurrentTracker = NULL;
}
CWtl_dynamic_layerView::~CWtl_dynamic_layerView()
{
	m_ptrMap.Release();
}
BOOL CWtl_dynamic_layerView::PreTranslateMessage(MSG* pMsg)
{
	pMsg;
	return FALSE;
}
bool CWtl_dynamic_layerView::OnLog(const char* log)
{
	//输出到vs运行栏，方便调试
	OutputDebugStringA(log);
	return true;
}
//响应漫游结束的事件，设置地图范围。
void CWtl_dynamic_layerView::OnExtentChanged(const KERNEL_NAME::GsBox& box)
{ 
	//增加更新计数异步更新
	m_ptrMap->Cancel();
	m_ptrMap->ViewExtent(box);
	m_UpdateRef.Increment();  
}
bool CWtl_dynamic_layerView::OnUpdate(GsRefObject* ref, int reason, const GsBox& extent, double res, void* param)
{
	//刷新范围如果和当前范围不相交则认为无需更新。
	GsBox box = m_ptrMap->ViewExtent();
	if (box.IsDisjoin(extent))
		return true;
	
	//增加更新计数异步更新
	m_UpdateRef.Increment();

	return true;
}

LRESULT CWtl_dynamic_layerView::OnTimer(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
{
	//定时刷新,如果没有更新计数则认为无需更新
	if (m_UpdateRef.Read() <= 0)
		return S_OK;

	//如果正在漫游，或者正在绘制，那么都不许要更新
	if (m_ptrMap->ScreenDisplay()->HasStartPan())
		return S_OK;

	if (m_ptrMap->ScreenDisplay()->IsDrawing())
		return S_OK;
	
	//清除更新标识
	m_UpdateRef = 0;
	//停止绘制进行新的更新
	m_ptrMap->Cancel(); 
	m_ptrMap->Update();
	return S_OK;
}
void CWtl_dynamic_layerView::ZoomPan()
{
	//设置当前的Tracker为漫游Tracker
	m_CurrentTracker = m_ptrPanTracker;
	m_Cursor = LoadCursor(NULL, IDC_HAND);
}
void CWtl_dynamic_layerView::FullMap()
{
	//全图显示
	m_ptrMap->Cancel();
	m_ptrMap->ViewExtent(m_ptrMap->FullExtent());
	m_ptrMap->Update();
}

void CWtl_dynamic_layerView::CreateMap()
{
	if(m_ptrMap)
		return;

	GsKernel::Initialize();
	//更新助理，获取basemap异步绘制图层的更新消息
	m_ptrAgent = new KERNEL_NAME::GsUpdateAgent();
	m_ptrAgent->OnNeedUpdate.Add(this, &CWtl_dynamic_layerView::OnUpdate);

	//窗体设备
	GeoStar::Kernel::GsPaintDevicePtr ptrDevice =
		GeoStar::Kernel::GsPaintDevice::CreatePaintDevice(
			GeoStar::Kernel::eWin32HwndDeviceD2D,m_hWnd);

	//窗体范围。
	CRect rect;
	this->GetClientRect(&rect);
	  
	//屏幕和地图范围
	GeoStar::Kernel::GsDisplayTransformationPtr ptrDT = new 
		GeoStar::Kernel::GsDisplayTransformation(
		GsBox(0,0,rect.Width(),rect.Height()),
		GsRect(0,0,rect.Width(),rect.Height())); 

	GeoStar::Kernel::GsScreenDisplayPtr ptrDisplay = new 
		GsScreenDisplay(ptrDevice,ptrDT);

	//创建地图
	m_ptrMap = new GsMap(ptrDisplay);

	//打开数据
	GeoStar::Kernel::GsConnectProperty conn;
	conn.Server = GsFileSystem::Combine(GsFileSystem::WorkingFolder().c_str(), "../data/");

	GsSqliteGeoDatabaseFactory fac;
	GsGeoDatabasePtr ptrGDB = fac.Open(conn);
	GsFeatureClassPtr ptrFea = ptrGDB->OpenFeatureClass("CHN_ADM3");

	//异步绘制的BaseMap图层。
	GsBaseMapLayerPtr ptrBaseMap = new	GsBaseMapLayer();
	ptrBaseMap->UpdateAgent(m_ptrAgent);
	//普通的FeatureLayer
	GsFeatureLayerPtr ptrFeaLyr = new GsFeatureLayer(ptrFea); 
	//将普通的图层加入到basemap中达到异步绘制的目的。
	ptrBaseMap->LayerCollection()->Add(ptrFeaLyr);

	//加入底图图层
	m_ptrMap->Layers()->push_back(ptrFeaLyr);
	 
	//创建金字塔网格图层作为动态图层。
	//动态图层绘制的结果不会保存到缓存，因此每次刷新都会导致动态图层的绘制。
	GsPyramidLayerPtr ptrDynamicLyr = new GsPyramidLayer(GsPyramid::WellknownPyramid(e360DegreePyramid));
	m_ptrMap->DynamicLayerCollection()->Add(ptrDynamicLyr);

	//缺省全图显示
	m_ptrMap->ViewExtent(m_ptrMap->FullExtent());

	//启动异步绘制的定时器。
	SetTimer(10010, 100);

	//漫游tracker和对应的事件。
	m_ptrPanTracker = new KERNEL_NAME::GsZoomPanClassicTracker(m_ptrMap);
	m_ptrPanTracker->OnExtentChanged.Add(this, &CWtl_dynamic_layerView::OnExtentChanged);
	 
	ZoomPan();
} 
LRESULT CWtl_dynamic_layerView::OnMouseDown(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& /*bHandled*/)
{
	if (m_CurrentTracker)
	{
		int x = GET_X_LPARAM(lParam);
		int y = GET_Y_LPARAM(lParam);

		m_ptrMap->Cancel();
		m_CurrentTracker->OnMouseDown(KERNEL_NAME::eLeftButton, 0, GsPT(x, y));
	}
	return S_OK;
}
LRESULT CWtl_dynamic_layerView::OnMouseUp(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& /*bHandled*/)
{
	if (m_CurrentTracker)
	{
		int x = GET_X_LPARAM(lParam);
		int y = GET_Y_LPARAM(lParam);
		m_CurrentTracker->OnMouseUp(KERNEL_NAME::eLeftButton, 0, GsPT(x, y));
	}
	return S_OK;
}
LRESULT CWtl_dynamic_layerView::OnMouseWheel(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& /*bHandled*/)
{
	if (m_CurrentTracker)
	{
		CPoint pt(GET_X_LPARAM(lParam), GET_Y_LPARAM(lParam));
		ScreenToClient(&pt);

		int zDelta = GET_WHEEL_DELTA_WPARAM(wParam);
		m_CurrentTracker->OnMouseWheel(KERNEL_NAME::eLeftButton, zDelta, 0, GsPT(pt.x, pt.y));
	}
	return S_OK;
}
LRESULT CWtl_dynamic_layerView::OnMouseMove(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& /*bHandled*/)
{
	if (m_Cursor)
		SetCursor(m_Cursor);

	if (m_CurrentTracker && (wParam & MK_LBUTTON))
	{
		int x = GET_X_LPARAM(lParam);
		int y = GET_Y_LPARAM(lParam);
		OutputDebugStringA("MouseMove\n");
		m_CurrentTracker->OnMouseMove(KERNEL_NAME::eLeftButton, 0, GsPT(x, y));
	}
	
	return S_OK;
}
LRESULT CWtl_dynamic_layerView::OnPaint(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
{
	CreateMap();
	CPaintDC dc(m_hWnd); 
	m_ptrMap->Paint();  

	return 0;
}
LRESULT CWtl_dynamic_layerView::OnSize(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
{
	
	if(!m_ptrMap)
		return S_OK;

	//设备（窗体）范围发生变化，通知DisplayTransformation
	CRect rect;
	this->GetClientRect(&rect);
	m_ptrMap->ScreenDisplay()->DisplayTransformation()->DeviceExtent(GsRect(rect.left,rect.top,rect.Width(),rect.Height()));
	m_ptrMap->Invalidate();
	 
	return 0;
}
