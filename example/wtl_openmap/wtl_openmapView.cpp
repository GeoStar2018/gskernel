// wtl_openmapView.cpp : implementation of the CWtl_openmapView class
//
/////////////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "resource.h"

#include "wtl_openmapView.h"
#include <atltypes.h>
using namespace GeoStar::Kernel;
using namespace GeoStar::Utility;
CWtl_openmapView::~CWtl_openmapView()
{
	m_ptrMap.Release();


}
BOOL CWtl_openmapView::PreTranslateMessage(MSG* pMsg)
{
	pMsg;
	return FALSE;
}
void CWtl_openmapView::CreateMap()
{
	if(m_ptrMap)
		return;
	GsKernel::Initialize();

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
	conn.Server = "D:\\400W\\sqlite";
	GsSqliteGeoDatabaseFactory fac;
	GsGeoDatabasePtr ptrGDB = fac.Open(conn);
	GsFeatureClassPtr ptrFea = ptrGDB->OpenFeatureClass("countries");
	GsFeatureLayerPtr ptrFeaLyr = new GsFeatureLayer(ptrFea);
	m_ptrMap->Layers()->push_back(ptrFeaLyr);
	GsSpatialReferencePtr ptrSR = new GsSpatialReference(GsWellKnownSpatialReference::eWebMercator);
	//m_ptrMap->ScreenDisplay()->DisplayTransformation()->SpatialReference(ptrSR);

	m_ptrMap->ViewExtent(m_ptrMap->FullExtent());

	

}
LRESULT CWtl_openmapView::OnPaint(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
{
	CreateMap();
	CPaintDC dc(m_hWnd);

	m_ptrMap->Paint();
	//TODO: Add your drawing code here

	return 0;
}
LRESULT CWtl_openmapView::OnSize(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
{
	
	if(!m_ptrMap)
		return S_OK;

	CRect rect;
	this->GetClientRect(&rect);
	m_ptrMap->ScreenDisplay()->DisplayTransformation()->DeviceExtent(GsRect(rect.left,rect.top,rect.Width(),rect.Height()));
	m_ptrMap->Invalidate();

	//m_ptrMap->Paint();
	//TODO: Add your drawing code here

	return 0;
}
