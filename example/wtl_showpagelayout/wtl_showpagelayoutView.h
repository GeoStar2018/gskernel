// wtl_showpagelayoutView.h : interface of the CWtl_showpagelayoutView class
//
/////////////////////////////////////////////////////////////////////////////

#pragma once
#include <kernel.h>
#include <layout.h>
#include "layoutelement.h"
class CWtl_showpagelayoutView : public CWindowImpl<CWtl_showpagelayoutView>
{
	KERNEL_NAME::GsScreenDisplayPtr m_ptrDisplay;
	KERNEL_NAME::GsPageLayoutPtr m_PageLayout;
	KERNEL_NAME::GsSymbolTrackerPtr m_ptrTracker;
	KERNEL_NAME::GsAffineElementEditFeedbackPtr m_ptrFeedback;

	void CreateLayout();
	void PushTracker(KERNEL_NAME::GsSymbolTracker* tracker);
	void PushFeedback(KERNEL_NAME::GsAffineElementEditFeedback* tracker);
	void OnExtentChanged(const KERNEL_NAME::GsBox& extent);
	KERNEL_NAME::GsSymbolTracker::GsTrackerFeedBack OnSelect(KERNEL_NAME::GsGeometry* geo);
public:
	DECLARE_WND_CLASS(NULL)

	BOOL PreTranslateMessage(MSG* pMsg)
	{
		pMsg;
		return FALSE;
	}

	BEGIN_MSG_MAP(CWtl_showpagelayoutView)
		MESSAGE_HANDLER(WM_PAINT, OnPaint)
		MESSAGE_HANDLER(WM_SIZE, OnSize)
		MESSAGE_HANDLER(WM_DESTROY, OnClose)
		MESSAGE_HANDLER(WM_CREATE, OnCreate)
		MESSAGE_HANDLER(WM_LBUTTONDOWN, OnLButtonDown);
		MESSAGE_HANDLER(WM_LBUTTONUP, OnLButtonUp);
		MESSAGE_HANDLER(WM_MOUSEMOVE, OnMouseMove);
		MESSAGE_HANDLER(WM_MOUSEWHEEL, OnMouseWheel);
	END_MSG_MAP()
	CWtl_showpagelayoutView();
	void Close();
	void FullMap();
	void ZoomIn();
	void ZoomOut();
	void ZoomPan();
	void UpdateMap();
	void Select();
	void Modify();
	//void OnAffineElementFeedbacking();
// Handler prototypes (uncomment arguments if needed):
//	LRESULT MessageHandler(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
//	LRESULT CommandHandler(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
//	LRESULT NotifyHandler(int /*idCtrl*/, LPNMHDR /*pnmh*/, BOOL& /*bHandled*/)

	LRESULT OnPaint(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
	LRESULT OnSize(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
	LRESULT OnClose(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
	LRESULT OnCreate(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
	LRESULT OnLButtonDown(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
	LRESULT OnLButtonUp(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
	LRESULT OnMouseMove(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
	LRESULT OnMouseWheel(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
	bool m_IsEditElement;
	void SetCustomCursor(GeoStar::Kernel::GsFeedbackItem*item);
};
