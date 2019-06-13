// wtl_openmapView.h : interface of the CWtl_openmapView class
//
/////////////////////////////////////////////////////////////////////////////

#pragma once

class CWtl_dynamic_layerView : public CWindowImpl<CWtl_dynamic_layerView>,public UTILITY_NAME::GsCustomLogOutput
{
	KERNEL_NAME::GsMapPtr m_ptrMap;
	KERNEL_NAME::GsUpdateAgentPtr m_ptrAgent;
	UTILITY_NAME::GsAtomic<int> m_UpdateRef;
	
	KERNEL_NAME::GsZoomPanClassicTrackerPtr m_ptrPanTracker; 

	KERNEL_NAME::GsSymbolTracker* m_CurrentTracker;
	HCURSOR m_Cursor;
public:
	DECLARE_WND_CLASS(NULL)

	BOOL PreTranslateMessage(MSG* pMsg);

	virtual bool OnLog(const char* log);

	BEGIN_MSG_MAP(CWtl_dynamic_layerView)
		MESSAGE_HANDLER(WM_PAINT, OnPaint)
		MESSAGE_HANDLER(WM_SIZE, OnSize)
		MESSAGE_HANDLER(WM_TIMER, OnTimer)
		MESSAGE_HANDLER(WM_LBUTTONDOWN, OnMouseDown)
		MESSAGE_HANDLER(WM_LBUTTONUP, OnMouseUp)
		MESSAGE_HANDLER(WM_MOUSEMOVE, OnMouseMove)
		MESSAGE_HANDLER(WM_MOUSEWHEEL, OnMouseWheel)

	END_MSG_MAP()
	CWtl_dynamic_layerView();
	~CWtl_dynamic_layerView();
	bool OnUpdate(UTILITY_NAME::GsRefObject* ref, int reason, const KERNEL_NAME::GsBox& extent, double res, void* param);
	void OnExtentChanged(const KERNEL_NAME::GsBox& box);
	void CreateMap();
	void ZoomPan();
	void FullMap();

// Handler prototypes (uncomment arguments if needed):
//	LRESULT MessageHandler(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
//	LRESULT CommandHandler(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
//	LRESULT NotifyHandler(int /*idCtrl*/, LPNMHDR /*pnmh*/, BOOL& /*bHandled*/)

	LRESULT OnPaint(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
	LRESULT OnSize(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
	LRESULT OnTimer(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
	LRESULT OnMouseDown(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
	LRESULT OnMouseUp(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
	LRESULT OnMouseMove(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
	LRESULT OnMouseWheel(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);

};