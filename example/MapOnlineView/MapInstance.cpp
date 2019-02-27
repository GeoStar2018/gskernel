#include "stdafx.h"

#include "GeoHeader.h"
#include "MapInstance.h"

CMapInstance::CMapInstance(GsPyramid* pPyramid, const CWnd& hWnd)
	: m_pPyramid(pPyramid)
	, m_hShowWnd(hWnd)
{

}

CMapInstance::~CMapInstance()
{

}

CMapInstance::CMapInstance(const CMapInstance& other, const CWnd& hWnd)
	: m_hShowWnd(hWnd)
{
	m_pPyramid = other.m_pPyramid;
	m_pMap = other.m_pMap;
}

#define INCH 0.03937

float CMapInstance::GetDPI()
{
	HDC hdcScreen;
	hdcScreen = CreateDC("DISPLAY", NULL, NULL, NULL);

	int iX = GetDeviceCaps(hdcScreen, HORZRES);    // pixel
	int iY = GetDeviceCaps(hdcScreen, VERTRES);    // pixel
	int iPhsX = GetDeviceCaps(hdcScreen, HORZSIZE);    // mm
	int iPhsY = GetDeviceCaps(hdcScreen, VERTSIZE);    // mm

	if (NULL != hdcScreen)
	{
		DeleteDC(hdcScreen);
	}
	float iTemp = iPhsX * iPhsX + iPhsY * iPhsY;
	float fInch = sqrt(iTemp) * INCH;
	iTemp = iX * iX + iY * iY;
	float fPixel = sqrt(iTemp);

	float iDPI = fPixel / fInch;    // dpi pixel/inch
	return iDPI;
}

void CMapInstance::Desplay()
{
	m_pMap->Update();
}

void CMapInstance::SetScale(ScaleType type)
{
	GsDisplayTransformation* pTransformation = m_pMap->ScreenDisplay()->DisplayTransformation();
	pTransformation->Resolution(pTransformation->Resolution() * (type == ScaleType::ZOOM_in ? 0.5 : 2.0));
	m_pMap->Update();
}

void CMapInstance::PanStart(CPoint& point)
{
	GsScreenDisplay* pScreenDisplay = m_pMap->ScreenDisplay();
	GsDisplayTransformation* pTransformation = pScreenDisplay->DisplayTransformation();
	GsRawPoint mapPoint = pTransformation->ToMap(point.x, point.y);
	pScreenDisplay->PanStart(mapPoint.X, mapPoint.Y);
}

void CMapInstance::PanMoveTo(CPoint& point)
{
	GsScreenDisplay* pScreenDisplay = m_pMap->ScreenDisplay();
	GsDisplayTransformation* pTransformation = pScreenDisplay->DisplayTransformation();
	GsRawPoint mapPoint = pTransformation->ToMap(point.x, point.y);
	pScreenDisplay->PanMoveTo(mapPoint.X, mapPoint.Y);
}

void CMapInstance::PanStop()
{
	GsScreenDisplay* pScreenDisplay = m_pMap->ScreenDisplay();
	m_pMap->ViewExtent(pScreenDisplay->PanStop());
	m_pMap->Update();
}

void CMapInstance::Cancel()
{
	m_pMap->Cancel();
}

bool CMapInstance::OnNeedUpdate(GsRefObject* ptrUpdateAgent, int, const GsBox& box, double, void* ptrUserParam)
{	
	if (!m_pMap->ViewExtent().IsDisjoin(box))
		return m_hShowWnd.GetParent()->PostMessage(WM_MAP_UPDATE, 0, 0);
	return true;
}

void CMapInstance::DrawTextFeature()
{

}

void CMapInstance::DrawSymbolForTest()
{
}

GsScreenDisplay* CMapInstance::CreateScreenDisplay(GsSpatialReference* ptrSR, GsBox& box)
{
	CRect rect;
	m_hShowWnd.GetWindowRect(&rect);
	m_hShowWnd.ScreenToClient(&rect);
	GsRect deviceBox;
	deviceBox.Left = rect.left;
	deviceBox.Top = rect.top;
	deviceBox.Right = rect.right;
	deviceBox.Bottom = rect.bottom;
	GsDisplayTransformationPtr pTransfor = new GsDisplayTransformation(box, deviceBox);
	if (!pTransfor)
		return nullptr;
	pTransfor->DPI(GetDPI());
	pTransfor->MapExtent(box);
	pTransfor->SpatialReference(ptrSR);
	return new GsScreenDisplay(pTransfor);
}

GsPaintDevice* CMapInstance::BindDevice(GsScreenDisplay* pScreenDis)
{
	return pScreenDis->BindDevice(GsPaintDevice::CreatePaintDevice(eWin32HwndDeviceD2D, m_hShowWnd)).p;
}