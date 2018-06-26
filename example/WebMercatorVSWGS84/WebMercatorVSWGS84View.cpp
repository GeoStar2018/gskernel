// WebMercatorVSWGS84View.cpp : implementation of the CWebMercatorVSWGS84View class
//
/////////////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "resource.h"
#include <atltypes.h>
#include "WebMercatorVSWGS84View.h"
#include <atlstr.h>

BOOL CWebMercatorVSWGS84View::PreTranslateMessage(MSG* pMsg)
{
	pMsg;
	return FALSE;
}
void CWebMercatorVSWGS84View::CreateMap()
{
	
	if(m_ptrMap)
		return;
	m_DownPointMap = new GsPoint(0.0,0.0);
	m_NowPointMap = new GsPoint(0.0,0.0);

	//获得窗体的大小
	CRect rect;
	this->GetClientRect(&rect);

	//创建DT
	GsDisplayTransformationPtr ptrDT = new GsDisplayTransformation(
		GsBox(0,0,rect.Width(),rect.Height()),
		GsRect(0,0,rect.Width(),rect.Height()));

	//创建绘制设备
	GsPaintDevicePtr ptrDevice = GsPaintDevice::CreatePaintDevice(GsPaintDeviceType::eWin32HwndDeviceD2D,
		this->m_hWnd);
	
	if(!ptrDevice)
		return;

	//创建屏幕的显示设备
	GsScreenDisplayPtr ptrDisplay = new GsScreenDisplay(ptrDevice,ptrDT);

	//创建地图。
	m_ptrMap = new GsMap(ptrDisplay);

	m_ptrMap->ScreenDisplay()->OnBeforeEndDrawing.Add(this,&CWebMercatorVSWGS84View::OnSketch);

	LoadData();
}
void CWebMercatorVSWGS84View::OnSketch(GsDisplay* disp)
{
	GS_LOCK_IT(m_Lock);
	if(!m_ptrGeo)
		return;
	if(!m_ptrSym)
	{
		m_ptrSym = new GsSimpleFillSymbol(GsColor(255,0,0,128));
	}
	m_ptrSym->StartDrawing(disp->Canvas(),disp->DisplayTransformation());
	m_ptrSym->Draw(m_ptrGeo);
	m_ptrSym->EndDrawing();	
}
void CWebMercatorVSWGS84View::LoadData()
{
	GsConnectProperty conn;
	conn.Server = "D:\\400W\\sqlite";


	GsSpatialReferencePtr ptrSR = new GsSpatialReference(eWebMercator); 
	GsSpatialReferencePtr ptrWGS84 = new GsSpatialReference(eWGS84);
	m_ptrWGS2MKT = new GsProjectCoordinateTransformation(ptrWGS84,ptrSR);
	m_ptrMKT2WGS = new GsProjectCoordinateTransformation(ptrSR,ptrWGS84);

	//类厂
	GsSqliteGeoDatabaseFactory fac;
	//打开geodatabase
	GsGeoDatabasePtr ptrGDB = fac.Open(conn);
	if(!ptrGDB)
		return;

	//打开featureclass
	m_ptrFeaClass = ptrGDB->OpenFeatureClass("MKT");
	if(!m_ptrFeaClass)
		return;

	//创建Featrelayer
	GsFeatureLayerPtr ptrFeaLyr = new GsFeatureLayer(m_ptrFeaClass);

	//featurelayer加入到地图中去。
	m_ptrMap->Layers()->push_back(ptrFeaLyr);


	//计算全图范围
	GsBox box  = m_ptrMap->FullExtent(); 
	m_ptrMap->ViewExtent(box);

	//设置地图无效
	m_ptrMap->Invalidate();

}
GsRawPoint CWebMercatorVSWGS84View::ToMap(POINT pt)
{
	GsRawPoint  ptRet;
	m_ptrMap->ScreenDisplay()->DisplayTransformation()->ToMap(pt.x,pt.y,ptRet.X,ptRet.Y);
	return ptRet;
}
double CWebMercatorVSWGS84View::ToMapMeasure(int len)
{
	return m_ptrMap->ScreenDisplay()->DisplayTransformation()->Resolution() * len;
}
LRESULT CWebMercatorVSWGS84View::OnLButtonDown(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& /*bHandled*/)
{
	if(!m_ptrMap)
		return S_OK;
	CPoint pt(lParam);
	m_DownPoint.X = pt.x;
	m_DownPoint.Y = pt.y;
	GsRawPoint mapPt =  ToMap(pt);
	m_DownPointMap->Set(mapPt.X,mapPt.Y);
	m_DownPointMap->Transform(m_ptrMKT2WGS);

	double tol = ToMapMeasure(2);
	GsBox box(mapPt.X - tol,mapPt.Y - tol,mapPt.X + tol,mapPt.Y + tol);

	GsRingPtr ptrRing = new GsRing(box);
	//查询条件
	GsSpatialQueryFilterPtr ptrQF = new GsSpatialQueryFilter(ptrRing);
	GsSelectionSetPtr ptrSel = m_ptrFeaClass->Select(ptrQF.p);
	if(!ptrSel)
		return S_OK;
	//地物游标
	GsFeatureCursorPtr ptrCursor = ptrSel->Search();
	GsFeaturePtr ptrFea = ptrCursor->Next();
	if(!ptrFea)
		return S_OK;
	
	{
		GS_LOCK_IT(m_Lock);
		m_ptrGeoOri = ptrFea->Geometry();
		m_ptrGeo = m_ptrGeoOri->Clone();
	}
	

	m_ptrMap->Paint();

	return S_OK;
}
class SimpleTrans:public GsCoordinateTransformation
{
	double m_x,m_y;
public:
	SimpleTrans(double x,double y)
	{
		m_x =x;
		m_y = y;
	}
	
	/// \brief 对x数组和y数组以及Z数组分别转换
	virtual bool Transformation(double* pX,double *pY,double *pZ,int nPointCount,int nPointOff)
	{
		for(int i =0;i<nPointCount;i++)
		{
			pX[0] +=m_x;
			pY[0] +=m_y;

			pX+=nPointOff;
			pY+=nPointOff;
		}
		return true;
	}

};

LRESULT CWebMercatorVSWGS84View::OnMouseMove(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& /*bHandled*/)
{
	if(!m_ptrMap)
		return S_OK;
	CPoint pt(lParam);
	GsRawPoint  mapPt = ToMap(pt);
	m_NowPointMap->Set(mapPt.X,mapPt.Y);
	m_NowPointMap->Transform(m_ptrMKT2WGS);

	CString str;
	str.Format(L"x=%f,y=%f",mapPt.X,mapPt.Y);
	
	m_Status.SetWindowText(str);


	if(m_ptrGeo)
	{
		
		double dx = m_NowPointMap->X() - m_DownPointMap->X();
		double dy = m_NowPointMap->Y() - m_DownPointMap->Y();
		{
			GS_LOCK_IT(m_Lock);
			
			m_ptrGeo = m_ptrGeoOri->Clone();
			m_ptrGeo->Transform(m_ptrMKT2WGS);

			SimpleTrans s(dx,dy);
			m_ptrGeo->Transform(&s);

			m_ptrGeo->Transform(m_ptrWGS2MKT);
		}
	
		m_ptrMap->Paint();
	}

	return S_OK;
}

LRESULT CWebMercatorVSWGS84View::OnSize(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
{
	if(!m_ptrMap)
		return S_OK;

	//获得窗体的大小
	CRect rect;
	this->GetClientRect(&rect);
	m_ptrMap->ScreenDisplay()->DisplayTransformation()->DeviceExtent(GsRect(0,0,rect.Width(),rect.Height()));
	m_ptrMap->ScreenDisplay()->OnSizeChanged();
	m_ptrMap->Cancel();
	m_ptrMap->Invalidate();
	return S_OK;
}
LRESULT CWebMercatorVSWGS84View::OnPaint(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
{

	CreateMap();
	CPaintDC dc(m_hWnd);
	if(m_ptrMap)
		m_ptrMap->Paint();

	//TODO: Add your drawing code here

	return 0;
}
