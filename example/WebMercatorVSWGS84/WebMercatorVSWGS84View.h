// WebMercatorVSWGS84View.h : interface of the CWebMercatorVSWGS84View class
//
/////////////////////////////////////////////////////////////////////////////

#pragma once
#include <atlctrls.h>

using namespace GeoStar::Kernel;
using namespace GeoStar::Utility;
class CWebMercatorVSWGS84View : public CWindowImpl<CWebMercatorVSWGS84View>
{
	GsMapPtr m_ptrMap;
	GsFeatureClassPtr m_ptrFeaClass;
	GsGeometryPtr m_ptrGeo;
	GsGeometryPtr m_ptrGeoOri;
	GsSimpleFillSymbolPtr m_ptrSym;
	GsProjectCoordinateTransformationPtr m_ptrMKT2WGS;
	GsProjectCoordinateTransformationPtr m_ptrWGS2MKT; 
	GsLock m_Lock;
	
	GsPointPtr m_DownPointMap;
	GsPointPtr m_NowPointMap;
	GsPT m_DownPoint;

public:
	CStatusBarCtrl m_Status;
	DECLARE_WND_CLASS(NULL)

	BOOL PreTranslateMessage(MSG* pMsg);

	BEGIN_MSG_MAP(CWebMercatorVSWGS84View)
		MESSAGE_HANDLER(WM_PAINT, OnPaint)
		MESSAGE_HANDLER(WM_SIZE, OnSize)
		MESSAGE_HANDLER(WM_LBUTTONDOWN, OnLButtonDown)
		MESSAGE_HANDLER(WM_MOUSEMOVE, OnMouseMove)
		
	END_MSG_MAP()
	GsRawPoint ToMap(POINT pt);
	double ToMapMeasure(int len);

	void CreateMap();
	void LoadData();
	void OnSketch(GsDisplay* disp);

// Handler prototypes (uncomment arguments if needed):
//	LRESULT MessageHandler(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
//	LRESULT CommandHandler(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
//	LRESULT NotifyHandler(int /*idCtrl*/, LPNMHDR /*pnmh*/, BOOL& /*bHandled*/)

	LRESULT OnPaint(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
	LRESULT OnSize(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);

	LRESULT OnLButtonDown(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
	LRESULT OnMouseMove(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);

};
