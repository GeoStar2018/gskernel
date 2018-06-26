// wtl_dynamic_targetView.h : interface of the CWtl_dynamic_targetView class
//
/////////////////////////////////////////////////////////////////////////////

#pragma once
#include "TargetSimer.h"

class CWtl_dynamic_targetView : public CWindowImpl<CWtl_dynamic_targetView>,public CTargetSimer
{ 
	GeoStar::Kernel::GsMapPtr m_ptrMap;
	GeoStar::Kernel::GsPaintDevicePtr m_ptrDevice;
	GeoStar::Kernel::GsSymbolTrackerPtr m_ptrTrackerDefault;
public:
	void PushTracker(GeoStar::Kernel::GsSymbolTracker* tracker);
	GeoStar::Kernel::GsSymbolTracker* CurrentTracker();
	void CreateMap();
protected:

	virtual GeoStar::Kernel::GsBox Extent();
	void OnBeforeEndDrawing(GeoStar::Kernel::GsDisplay*  disp);
public:
	DECLARE_WND_CLASS(NULL)

	BOOL PreTranslateMessage(MSG* pMsg);

	BEGIN_MSG_MAP(CWtl_dynamic_targetView)
		MESSAGE_HANDLER(WM_PAINT, OnPaint)
		MESSAGE_HANDLER(WM_SIZE, OnSize)
		MESSAGE_HANDLER(WM_TIMER, OnTimer)
		MESSAGE_HANDLER(WM_MOUSEMOVE, OnMouseMove)
		MESSAGE_HANDLER(WM_LBUTTONDOWN, OnLButtonDown)
		MESSAGE_HANDLER(WM_LBUTTONUP, OnLButtonUp)
		MESSAGE_HANDLER(WM_MOUSEWHEEL, OnMouseWheel)

	END_MSG_MAP()

	virtual void BeginSim();
	virtual void EndSim();

	void ViewFullMap();
	void UpdateMap();
	void NewMap();
	void OpenMap(const char* path, std::vector<std::string>& vecName);


// Handler prototypes (uncomment arguments if needed):
//	LRESULT MessageHandler(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
//	LRESULT CommandHandler(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
//	LRESULT NotifyHandler(int /*idCtrl*/, LPNMHDR /*pnmh*/, BOOL& /*bHandled*/)

	LRESULT OnMouseMove(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
	LRESULT OnLButtonDown(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
	LRESULT OnLButtonUp(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
	LRESULT OnMouseWheel(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
	LRESULT OnTimer(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);


	LRESULT OnSize(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
	LRESULT OnPaint(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
};
