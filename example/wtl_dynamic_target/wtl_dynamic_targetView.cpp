// wtl_dynamic_targetView.cpp : implementation of the CWtl_dynamic_targetView class
//
/////////////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "resource.h"

#include "wtl_dynamic_targetView.h"
#include <atltypes.h>
#include "PanTracker.h"
using namespace GeoStar::Kernel;
using namespace GeoStar::Utility;
BOOL CWtl_dynamic_targetView::PreTranslateMessage(MSG* pMsg)
{
	pMsg;
	return FALSE;
}
void CWtl_dynamic_targetView::BeginSim()
{
	SetTimer(10010, 500);
}
void CWtl_dynamic_targetView::EndSim()
{
	__super::EndSim();
	KillTimer(10010);
}

GeoStar::Kernel::GsBox CWtl_dynamic_targetView::Extent()
{
	if (!m_ptrMap)
		return GsBox(-180, -90, 180, 90);
	return m_ptrMap->ViewExtent();
}
LRESULT CWtl_dynamic_targetView::OnTimer(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& /*bHandled*/)
{
	if (wParam == 10010)
	{
		for (int i = 0; i < Count(); i++)
		{
			CTargetPtr ptrTarget = Target(i);
			if (!ptrTarget)
				continue;

			ptrTarget->UpdateSim();
		}
	}
	else if (wParam == 10020) //如果是判断地图刷新
	{
		int n = 0;
		for (int i = 0; i < Count(); i++)
		{
			CTargetPtr ptrTarget = Target(i);
			if (!ptrTarget)
				continue;

			n += ptrTarget->UpdateCounter();
		}
		//如果
		if (n > 0 && !m_ptrMap->IsDrawing())
			Invalidate(FALSE);
	}
	return 0;
}
GeoStar::Kernel::GsSymbolTracker* CWtl_dynamic_targetView::CurrentTracker()
{
	return m_ptrTrackerDefault.p;
}
void CWtl_dynamic_targetView::PushTracker(GeoStar::Kernel::GsSymbolTracker* tracker)
{

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
LRESULT CWtl_dynamic_targetView::OnMouseMove(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& /*bHandled*/)
{
	if (CurrentTracker())
		CurrentTracker()->OnMouseMove(ToButton(wParam), 0, ToPoint(lParam));
	return 0;
}
LRESULT CWtl_dynamic_targetView::OnLButtonDown(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& /*bHandled*/)
{
	if (CurrentTracker())
		CurrentTracker()->OnMouseDown(eLeftButton, 0, ToPoint(lParam));
	return 0;
}
LRESULT CWtl_dynamic_targetView::OnLButtonUp(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& /*bHandled*/)
{
	if (CurrentTracker())
		CurrentTracker()->OnMouseUp(eLeftButton, 0, ToPoint(lParam));
	return 0;
}
LRESULT CWtl_dynamic_targetView::OnMouseWheel(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& /*bHandled*/)
{
	int zDelta = GET_WHEEL_DELTA_WPARAM(wParam);

	
	if (CurrentTracker())
		CurrentTracker()->OnMouseWheel(ToButton(wParam),zDelta, 0, ToPoint(lParam));
	return 0;
}

void CWtl_dynamic_targetView::NewMap()
{
	m_ptrMap->Cancel();
	m_ptrMap->Layers()->clear();
	UpdateMap();

}
void CWtl_dynamic_targetView::OpenMap(const char* path, std::vector<std::string>& vecName)
{
	GeoStar::Kernel::GsConnectProperty conn;
	conn.Server = path;
	GsSqliteGeoDatabaseFactory fac;
	GsGeoDatabasePtr ptrGDB = fac.Open(conn);
	if (!ptrGDB)
		return;

	std::vector<std::string>::iterator it = vecName.begin();
	for (; it != vecName.end(); it++)
	{
		GsFeatureClassPtr ptrFea = ptrGDB->OpenFeatureClass(it->c_str());
		if (!ptrFea)
			continue;
		GsFeatureLayerPtr ptrFeaLyr = new GsFeatureLayer(ptrFea);
		m_ptrMap->Layers()->push_back(ptrFeaLyr);
	}
}
void CWtl_dynamic_targetView::CreateMap()
{
	if (m_ptrMap)
		return; 
	
	GeoStar::Kernel::GsPaintDevicePtr ptrDevice =
		GeoStar::Kernel::GsPaintDevice::CreatePaintDevice(
			GeoStar::Kernel::eWin32HwndDeviceD2D, m_hWnd);
	m_ptrDevice = ptrDevice;
	CRect rect;
	this->GetClientRect(&rect);

	GeoStar::Kernel::GsDisplayTransformationPtr ptrDT = new
		GeoStar::Kernel::GsDisplayTransformation(
			GsBox(0, 0, rect.Width(), rect.Height()),
			GsRect(0, 0, rect.Width(), rect.Height()));

	GeoStar::Kernel::GsScreenDisplayPtr ptrDisplay = new
		GsScreenDisplay(ptrDevice, ptrDT);


	m_ptrMap = new GsMap(ptrDisplay);
	ptrDisplay->Simplifier(new GsTopologyPreservingGeometrySimplifier(0));

	GsString strFolder = GsFileSystem::WorkingFolder();
	strFolder = GsFileSystem::Combine(strFolder.c_str(), "../Data");
	std::vector<std::string> vec;
	vec.push_back("BOU2_4M_S");
	OpenMap(strFolder.c_str(), vec);
	if(m_ptrMap->Layers()->size() >0)
		m_ptrMap->ViewExtent(m_ptrMap->FullExtent());

	m_ptrTrackerDefault = new PanTracker(m_ptrMap, this);

	SetTimer(10020, 50);

	m_ptrMap->ScreenDisplay()->OnBeforeEndDrawing.Add(this, &CWtl_dynamic_targetView::OnBeforeEndDrawing);
}
void CWtl_dynamic_targetView::OnBeforeEndDrawing(GeoStar::Kernel::GsDisplay*  disp)
{
	
	for (int i = 0; i < Count(); i++)
	{
		CTargetPtr ptrTarget = Target(i);
		if (!ptrTarget)
			continue;

		ptrTarget->Draw(disp);
	}
}
void CWtl_dynamic_targetView::ViewFullMap()
{
	m_ptrMap->ViewExtent(m_ptrMap->FullExtent());
	UpdateMap();
}
void CWtl_dynamic_targetView::UpdateMap()
{
	m_ptrMap->Cancel();
	m_ptrMap->Invalidate();
	Invalidate(FALSE);
}
LRESULT CWtl_dynamic_targetView::OnSize(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
{ 
	if (!m_ptrMap)
		return 0; 
	CRect rect;
	this->GetClientRect(&rect); 
	m_ptrMap->ScreenDisplay()->DisplayTransformation()->DeviceExtent(
		GsRect(rect.left, rect.top, rect.Width(), rect.Height())
	); 
	m_ptrMap->Invalidate();
	 
	return 0;
}
LRESULT CWtl_dynamic_targetView::OnPaint(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
{

	CreateMap();
	CPaintDC dc(m_hWnd);

	m_ptrMap->Paint();
	//TODO: Add your drawing code here

	return 0;
}
