// wtl_openmapView.cpp : implementation of the CWtl_openmapView class
//
/////////////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "resource.h"
#include "PointCloudLayer.h"

#include "wtl_osgbcheckView.h"
#include <atltypes.h>
using namespace GeoStar::Kernel;
using namespace GeoStar::Utility;
CWtl_osgbcheckView::CWtl_osgbcheckView()
{
	m_Cursor = NULL;
	m_CurrentTracker = NULL;
	//自定义日志输出
	GsLogger::Default().LogLevel(eLOGALL);
	GsLogger::Default().CustomOutput(this);
}
CWtl_osgbcheckView::~CWtl_osgbcheckView()
{
	m_ptrMap->Cancel();
	m_ptrMap.Release();
}
bool CWtl_osgbcheckView::OnLog(const char* log)
{
	//输出到vs运行栏，方便调试
	OutputDebugStringA(log);
	return true;
}
BOOL CWtl_osgbcheckView::PreTranslateMessage(MSG* pMsg)
{
	pMsg;
	return FALSE;
}
void CWtl_osgbcheckView::OnExtentChanged(const KERNEL_NAME::GsBox& box)
{
	m_ptrMap->Cancel();
	m_ptrMap->ViewExtent(box);
	m_ptrMap->Update();
}
void CWtl_osgbcheckView::NeedUpdate()
{
	m_UpdateRef.Increment();

}
bool CWtl_osgbcheckView::OnUpdate(GsRefObject* ref, int reason, const GsBox& extent, double res, void* param)
{
	GsBox box = m_ptrMap->ViewExtent();
	if (box.IsDisjoin(extent))
		return true;

	m_UpdateRef.Increment();

	return true;
}

LRESULT CWtl_osgbcheckView::OnTimer(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
{
	if (m_UpdateRef.Read() <= 0)
		return S_OK;
	m_UpdateRef = 0;

	if (m_ptrMap->ScreenDisplay()->HasStartPan())
		return S_OK;

	if (m_ptrMap->ScreenDisplay()->IsDrawing())
		return S_OK;
	m_ptrMap->Cancel(); 
	m_ptrMap->Update();
	return S_OK;
}
void CWtl_osgbcheckView::ZoomPan()
{
	m_CurrentTracker = m_ptrPanTracker;
	m_Cursor = LoadCursor(NULL, IDC_HAND);
}
void CWtl_osgbcheckView::Select()
{
	m_CurrentTracker = m_ptrSelectTracker;
	m_Cursor = LoadCursor(NULL, IDC_ARROW);
}
void CWtl_osgbcheckView::New()
{
	m_ptrMap->Cancel();
	m_ptrMap->LayerCollection()->Clear();
	m_ptrMap->Update();

}
void CWtl_osgbcheckView::OpenData()
{
	CMultiFileDialog dialog(_T("*.fcs"), NULL, 4UL, _T("*.fcs"));
	INT_PTR ret = dialog.DoModal();
	if (ret != IDOK)
		return;

	TCHAR* p = dialog.m_ofn.lpstrFile;
	if (!p[0])
		return;

	GsConnectProperty conn;
	conn.Server = GsUtf8(p).Str();
	GsVector<GsString> vec;
	
	if (GsFileSystem::IsFile(conn.Server.c_str())) //如果文件存在则表示之选择了一个文件。
	{
		GsFile file(conn.Server.c_str());
		conn.Server = file.Parent().FullPath();
		vec.push_back(file.Name());
	}
	else
	{
		p += strlen(p) + 1;
		while (p[0])
		{
			vec.push_back(GsUtf8(p).Str());
			p += strlen(p) + 1;
		}
	}
	//暂停地图绘制
	m_ptrMap->Cancel();

	GsGeoDatabasePtr ptrGDB = GsSqliteGeoDatabaseFactory().Open(conn);
	GsVector<GsString>::iterator it = vec.begin();
	int nCount = m_ptrMap->LayerCollection()->Count();
  
	for (; it != vec.end(); it++)
	{ 
		GsFeatureClassPtr ptrFeaClass = ptrGDB->OpenFeatureClass(it->c_str());
		if (!ptrFeaClass)
			return;
		GsFeatureLayerPtr ptrFeaLyr = new GsFeatureLayer(ptrFeaClass); 
		m_ptrMap->LayerCollection()->Add(ptrFeaLyr);
	}
	if (nCount == 0)
		m_ptrMap->ViewExtent(m_ptrMap->FullExtent());
	m_ptrMap->Update();
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
void CWtl_osgbcheckView::CreateMap()
{
	if(m_ptrMap)
		return;

	GsKernel::Initialize();
	m_ptrAgent = new KERNEL_NAME::GsUpdateAgent();
	m_ptrAgent->OnNeedUpdate.Add(this, &CWtl_osgbcheckView::OnUpdate);

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
	m_ptrMap->LayerRenderer(new GsSequentialLayerRenderer());

	GeoStar::Kernel::GsConnectProperty conn;
	conn.Server = GsFileSystem::Combine(GsFileSystem::WorkingFolder().c_str(), "../data/");
	conn.Server = "H:\\01-Data\\shenzhen\\xqsl";
	GsShpGeoDatabaseFactory fac;
	GsGeoDatabasePtr ptrGDB = fac.Open(conn);
	GsFeatureClassPtr ptrFea = ptrGDB->OpenFeatureClass("jzw");
	GsFeatureLayerPtr ptrFeaLyr = new GsFeatureLayer(ptrFea);  
	//ptrFeaLyr->Transparency(0.9);
	m_ptrMap->Layers()->push_back(OpenTMS());
	m_ptrMap->Layers()->push_back(ptrFeaLyr);
	 

	m_ptrMap->ViewExtent(ptrFeaLyr->Extent());


	m_PointLayer = new PointCloudLayer("d:\\point.bin");
	m_PointLayer->Center() =  GsRawPoint(113.838490849983, 22.7240020341737);// m_ptrMap->FullExtent().Center();
	m_ptrMap->Layers()->push_back(m_PointLayer);

	SetTimer(10010, 100);

	m_ptrPanTracker = new KERNEL_NAME::GsZoomPanClassicTracker(m_ptrMap);
	m_ptrPanTracker->OnExtentChanged.Add(this, &CWtl_osgbcheckView::OnExtentChanged);

	m_ptrSelectTracker = new KERNEL_NAME::GsNewEnvelopeTracker(m_ptrMap);
	m_ptrSelectTracker->OnGeometryTracked.Add(this, &CWtl_osgbcheckView::OnSelect);
	ZoomPan();
}
void SelectLayer(GsLayer* lyr, GsQueryFilter* qf)
{
	GsFeatureLayer* pFea = lyr->CastTo<GsFeatureLayer>();
	if (pFea)
	{
		pFea->Select(qf);
		return;
	}
	GsMultiLayer* multi = lyr->CastTo<GsMultiLayer>();
	if (multi)
	{
		for (int i = 0; i < multi->LayerCollection()->Count(); i++)
		{
			SelectLayer(multi->LayerCollection()->Layer(i), qf);
		}
	}
}
KERNEL_NAME::GsSymbolTracker::GsTrackerFeedBack CWtl_osgbcheckView::OnSelect(KERNEL_NAME::GsGeometry* geo)
{
	GsSpatialQueryFilterPtr ptrQF = new GsSpatialQueryFilter(geo);
	m_ptrMap->Cancel();
	GsLayerCollection* coll = m_ptrMap->LayerCollection();
	for (int i = 0; i < coll->Count(); i++)
	{
		SelectLayer(coll->Layer(i), ptrQF);
	}
	m_ptrMap->Paint(eDrawSelectionSet);

	return KERNEL_NAME::GsSymbolTracker::eRestartTracker;
}
LRESULT CWtl_osgbcheckView::OnMouseDown(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& /*bHandled*/)
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
LRESULT CWtl_osgbcheckView::OnMouseUp(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& /*bHandled*/)
{
	if (m_CurrentTracker)
	{
		int x = GET_X_LPARAM(lParam);
		int y = GET_Y_LPARAM(lParam);
		m_CurrentTracker->OnMouseUp(KERNEL_NAME::eLeftButton, 0, GsPT(x, y));
	}
	return S_OK;
}
LRESULT CWtl_osgbcheckView::OnMouseWheel(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& /*bHandled*/)
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
LRESULT CWtl_osgbcheckView::OnMouseMove(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& /*bHandled*/)
{
	if (m_Cursor)
		SetCursor(m_Cursor);

	if (m_CurrentTracker && (wParam & MK_LBUTTON))
	{
		int x = GET_X_LPARAM(lParam);
		int y = GET_Y_LPARAM(lParam);

		m_CurrentTracker->OnMouseMove(KERNEL_NAME::eLeftButton, 0, GsPT(x, y));
	}
	
	return S_OK;
}
LRESULT CWtl_osgbcheckView::OnPaint(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
{
	CreateMap();
	CPaintDC dc(m_hWnd);

	m_ptrMap->Paint();
	//TODO: Add your drawing code here

	return 0;
}
LRESULT CWtl_osgbcheckView::OnSize(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
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
