#include "stdafx.h"
#include "wtl_showpagelayoutView.h"
#include <atltypes.h>
#include <atlstr.h>
using namespace UTILITY_NAME;
using namespace KERNEL_NAME;

CWtl_showpagelayoutView::CWtl_showpagelayoutView()
{
	m_IsEditElement = false;
	GsKernel::Initialize();
}
LRESULT CWtl_showpagelayoutView::OnClose(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
{
	Close();
	return S_OK;
}


void CWtl_showpagelayoutView::FullMap()
{
	GsSizeF s = m_PageLayout->Page()->PageSize();
	m_PageLayout->ViewExtent(GsBox(0, 0, s.Width, s.Height));
	UpdateMap();
}
void CWtl_showpagelayoutView::PushTracker(KERNEL_NAME::GsSymbolTracker* tracker)
{
	m_ptrTracker = tracker;
}
void CWtl_showpagelayoutView::SetCustomCursor(GsFeedbackItem*item)
{
	GsFeedbackItemType g = item->Type;
}
void CWtl_showpagelayoutView::PushFeedback(KERNEL_NAME::GsAffineElementEditFeedback * tracker)
{
	m_ptrFeedback = tracker;
	m_ptrFeedback->OnAffineElementFeedback.Add(this, &CWtl_showpagelayoutView::SetCustomCursor);
}
void CWtl_showpagelayoutView::OnExtentChanged(const GsBox& extent)
{
	m_PageLayout->ViewExtent(extent);
	UpdateMap();
}
void CWtl_showpagelayoutView::ZoomIn()
{
	GsZoomTrackerPtr ptrZoom = new GsZoomTracker(m_PageLayout, GsZoomTracker::eZoomInOutLikeCAD);
	ptrZoom->OnExtentChanged.Add(this, &CWtl_showpagelayoutView::OnExtentChanged);
	PushTracker(ptrZoom);
	
}
void CWtl_showpagelayoutView::ZoomOut()
{
	GsZoomTrackerPtr ptrZoom = new GsZoomTracker(m_PageLayout, GsZoomTracker::eZoomOut);
	ptrZoom->OnExtentChanged.Add(this, &CWtl_showpagelayoutView::OnExtentChanged);
	PushTracker(ptrZoom);

}
void CWtl_showpagelayoutView::ZoomPan()
{
	GsZoomPanClassicTrackerPtr ptrTrack = new GsZoomPanClassicTracker(m_PageLayout);
	ptrTrack->OnExtentChanged.Add(this, &CWtl_showpagelayoutView::OnExtentChanged);
	PushTracker(ptrTrack);
}

GsSymbolTracker::GsTrackerFeedBack CWtl_showpagelayoutView::OnSelect(GsGeometry* geo)
{
	short left = GetKeyState(VK_LCONTROL);
	short right = GetKeyState(VK_RCONTROL);
	bool bCtlPressed = (left < 0 || right < 0);
	m_PageLayout->SelectElement(geo, bCtlPressed?eXorSelection: eNewSelection);
	UpdateMap();
	return 	GsSymbolTracker::GsTrackerFeedBack::eRestartTracker;
}
void CWtl_showpagelayoutView::Select()
{
	
	GsNewPointTrackerPtr ptrTrack = new GsNewPointTracker(m_PageLayout);
	ptrTrack->OnGeometryTracked.Add(this, &CWtl_showpagelayoutView::OnSelect);
	PushTracker(ptrTrack);
}
void CWtl_showpagelayoutView::Modify()
{
	PushTracker(NULL);
	KERNEL_NAME::GsAffineElementEditFeedbackPtr ptrTracker = m_PageLayout->EditFeedback();
	PushFeedback(ptrTracker);
}
void CWtl_showpagelayoutView::UpdateMap()
{
	m_PageLayout->Invalidate();
	Invalidate(FALSE);
}
GsMapPtr CreateMapFromData(const char * folder, const char * name)
{
	GsDisplayTransformationPtr ptrDT = new GsDisplayTransformation(GsBox(0, 0, 100,100),GsRect(0,0,100,100));
	GsScreenDisplayPtr ptrScreen = new GsScreenDisplay(NULL, ptrDT);

	GsMapPtr ptrMap = new GsMap(ptrScreen);

	GsConnectProperty conn(folder);
	GsGeoDatabasePtr ptrGDB = GsSqliteGeoDatabaseFactory().Open(conn);
	GsFeatureClassPtr ptrFeaClass = ptrGDB->OpenFeatureClass(name);
	GsFeatureLayerPtr ptrLyr = new GsFeatureLayer(ptrFeaClass);
	ptrMap->Layers()->push_back(ptrLyr);
	ptrMap->ViewExtent(ptrFeaClass->Extent());

	return ptrMap;
}
void CWtl_showpagelayoutView::CreateLayout()
{
	if (m_PageLayout)
		return;

	CRect winRect;
	GetClientRect(&winRect);
	GsRect rect(0,0,1000,1000);

	GsPaintDevicePtr ptrDevice = GsPaintDevice::CreatePaintDevice(eWin32HwndDeviceD2D, m_hWnd);
	GsDisplayTransformationPtr ptrDT = new GsDisplayTransformation(GsBox(0, 0, rect.Width(), rect.Height()), rect);
	m_ptrDisplay = new GsScreenDisplay(ptrDevice,ptrDT);
	m_PageLayout = new GsPageLayout(m_ptrDisplay);

	GsSizeF s = m_PageLayout->Page()->PageSize();

	m_PageLayout->ViewExtent(GsBox(0,0, s.Width, s.Height));
	
	ptrDT = new GsDisplayTransformation(GsBox(0, 0, rect.Width(), rect.Height()), rect);
	GsScreenDisplayPtr ptrScreen = new GsScreenDisplay(NULL, ptrDT);

	GsString sFile= GsFileSystem::Combine(GsFileSystem::WorkingFolder(), "..\\..\\testdata\\400sqlite");
	//GsFileSystem::MakeExistFullPath();
	GsMapElementPtr ptrEle = new GsMapElement(CreateMapFromData(sFile.c_str(),"RAI_4M_L"));
	m_PageLayout->ElementContainer()->Add(ptrEle);
	ptrEle->ShowBorder(false);

	ptrEle = new GsMapElement(CreateMapFromData(sFile.c_str(), "BOU2_4M_S"));
	ptrEle->Geometry(new GsEnvelope(100, 100, 200, 200));
	m_PageLayout->ElementContainer()->Add(ptrEle);
	
	GsRingPtr ptrRing = new GsRing(GsRawPoint(60,100),40);
	
	GsShapeElementPtr ptrShape = new GsShapeElement(ptrRing);
	m_PageLayout->ElementContainer()->Add(ptrShape);

	GsCircleArcPtr ptrPath = new GsCircleArc(GsRawPoint(85, 5), GsRawPoint(75, 30), GsRawPoint(100, 20));

	GsShapeElementPtr ptrShapeArc = new GsShapeElement(ptrPath);
	m_PageLayout->ElementContainer()->Add(ptrShapeArc);

	GsNorthArrowPtr ptrNA = new GsNorthArrow(100, 222, 10,10);
	ptrNA->Angle(46);
	m_PageLayout->ElementContainer()->Add(ptrNA);

	GsNorthArrowPtr ptrNA2 = new GsNorthArrow(100, 222, 10, 10);
	ptrNA2->Color(GsColor::Red);
	ptrNA2->Angle(0);
	m_PageLayout->ElementContainer()->Add(ptrNA2);

	GsString strNameFile = GsFileSystem::Combine(GsFileSystem::WorkingFolder(), "..\\..\\testdata\\image\\que.bmp");
	GsPictureElementPtr ptrPic = new GsPictureElement(50, 150, strNameFile);
	m_PageLayout->ElementContainer()->Add(ptrPic);

	strNameFile = GsFileSystem::Combine(GsFileSystem::WorkingFolder(), "..\\..\\testdata\\400sqlite\\123.GMAPX");
	GsMapDefine mapDefine(strNameFile);
	GsMapPtr pMap = new GsMap();
	mapDefine.ParserMap(pMap);

	GsVector<GsLayerPtr >* pVecLayers = pMap->Layers();
	GsLegendElementPtr ptrLegendElement= new GsLegendElement(-110, 20, 100, 200, *pVecLayers);
	m_PageLayout->ElementContainer()->Add(ptrLegendElement);

	GsSpecialPointElementPtr ptrPointElent = new GsSpecialPointElement(GsRawPoint(150, 50), 10, new GsSimplePointSymbol());
	m_PageLayout->ElementContainer()->Add(ptrPointElent);


	GsScaleBarElementPtr ptrScaleBar = new GsScaleBarElement(110, 250);
	GsSymbolPtr ptrSymbol = new GsSimpleLineSymbol(GsColor::Red);
	ptrScaleBar->BottomLineSymbol(ptrSymbol);
	ptrScaleBar->Level(3);
	m_PageLayout->ElementContainer()->Add(ptrScaleBar);

	GsThreeNorthElementPtr ptrThreeNorth = new GsThreeNorthElement(20, 250);
	m_PageLayout->ElementContainer()->Add(ptrThreeNorth);

	GsSlopeRulerElementPtr ptrSlopeRulerElement = new GsSlopeRulerElement(250,10);
	m_PageLayout->ElementContainer()->Add(ptrSlopeRulerElement);

}
LRESULT CWtl_showpagelayoutView::OnPaint(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
{
	CPaintDC dc(m_hWnd);

	if (!m_PageLayout)
		return S_OK;

	m_PageLayout->Paint();
	static int i = 0;
	CString str;
	str.Format("OnPaint %d\n", i++);

	OutputDebugString(str);
	return S_OK;
}
LRESULT CWtl_showpagelayoutView::OnSize(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
{
	CPaintDC dc(m_hWnd);
	if (!m_PageLayout)
		return S_OK;

	m_PageLayout->Cancel();
	CRect winRect;
	GetClientRect(&winRect);
	if (winRect.Width() > 0 && winRect.Height() > 0)
	{
		GsRect rect(winRect.left, winRect.top, winRect.Width(), winRect.Height());
		m_ptrDisplay->DisplayTransformation()->DeviceExtent(rect);
	}
	m_PageLayout->Invalidate();
	Invalidate(FALSE);
	return S_OK;
}

LRESULT CWtl_showpagelayoutView::OnCreate(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
{
	CreateLayout();

	return S_OK;
}
void CWtl_showpagelayoutView::Close()
{

	m_PageLayout->Cancel();
	m_PageLayout.Release();
	m_ptrDisplay.Release();
}
GsButton ToButton(WPARAM wParam)
{
	DWORD button;
	if (MK_LBUTTON & wParam)
		button = eLeftButton;
	if (MK_RBUTTON & wParam)
		button = button | eRightButton;
	if (MK_MBUTTON & wParam)
		button = button | eMiddleButton;

	return (GsButton)button;
}
GsPT ToPoint(LPARAM lParam)
{
	return GsPT(GET_X_LPARAM(lParam), GET_Y_LPARAM(lParam));
}
LRESULT CWtl_showpagelayoutView::OnLButtonDown(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& /*bHandled*/)
{
	if (m_ptrTracker)
		m_ptrTracker->OnMouseDown(eLeftButton, 0, ToPoint(lParam));
	if (m_ptrFeedback)
		m_ptrFeedback->OnMouseDown(eLeftButton, 0, ToPoint(lParam));
	return S_OK;
}
LRESULT CWtl_showpagelayoutView::OnLButtonUp(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& /*bHandled*/)
{
	if (m_ptrTracker)
		m_ptrTracker->OnMouseUp(eLeftButton, 0, ToPoint(lParam));
	if(m_ptrFeedback)
		m_ptrFeedback->OnMouseUp(eLeftButton, 0, ToPoint(lParam));
	return S_OK;
}
LRESULT CWtl_showpagelayoutView::OnMouseMove(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& /*bHandled*/)
{
	if (m_ptrTracker)
		m_ptrTracker->OnMouseMove(eLeftButton, 0, ToPoint(lParam));
	if (m_ptrFeedback)
		m_ptrFeedback->OnMouseMove(eLeftButton, 0, ToPoint(lParam));
	return S_OK;
}
LRESULT CWtl_showpagelayoutView::OnMouseWheel(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& /*bHandled*/)
{
	if (!m_ptrTracker) return S_OK;
	int zDelta = GET_WHEEL_DELTA_WPARAM(wParam);

	m_ptrTracker->OnMouseWheel(eLeftButton, zDelta,0, ToPoint(lParam));

	return S_OK;
}
