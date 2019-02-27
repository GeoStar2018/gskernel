// aboutdlg.h : interface of the CAboutDlg class
//
/////////////////////////////////////////////////////////////////////////////

#pragma once
#define _ATL_USE_DDX_FLOAT
#include <atlddx.h>
#include <atlctrlx.h>

#include <atlctrls.h>
#include <atlctrls.h>
#include "wtl_osgbcheckView.h"
class CAboutDlg : public CDialogImpl<CAboutDlg>  ,public CWinDataExchange<CAboutDlg>   
{
public:
	enum { IDD = IDD_ABOUTBOX }; 

	BEGIN_MSG_MAP(CAboutDlg)
		MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
		COMMAND_ID_HANDLER(IDOK, OnCloseCmd)
		COMMAND_ID_HANDLER(IDCANCEL, OnCloseCmd)
		COMMAND_ID_HANDLER(IDC_BUTTONADDX, OnAddX)
		COMMAND_ID_HANDLER(IDC_BUTTONADDY, OnAddY)
		COMMAND_ID_HANDLER(IDC_BUTTONSUBX, OnSubX)
		COMMAND_ID_HANDLER(IDC_BUTTONSUBY, OnSubY)
	END_MSG_MAP()
	BEGIN_DDX_MAP(CAboutDlg)
		DDX_FLOAT(IDC_EDITX, m_OffsetX)
		DDX_FLOAT(IDC_EDITY, m_OffsetY)
		DDX_FLOAT(IDC_EDITDELTA, m_Delta)

	END_DDX_MAP()
	double m_OffsetX;
	double m_OffsetY;
	double m_Delta;

	CWtl_osgbcheckView* m_view;
	void BindView(CWtl_osgbcheckView* view);
	void MoveCenter(double val,bool x = false);

	LRESULT OnAddX(WORD /*wNotifyCode*/, WORD wID, HWND /*hWndCtl*/, BOOL& /*bHandled*/);
	LRESULT OnAddY(WORD /*wNotifyCode*/, WORD wID, HWND /*hWndCtl*/, BOOL& /*bHandled*/);
	LRESULT OnSubX(WORD /*wNotifyCode*/, WORD wID, HWND /*hWndCtl*/, BOOL& /*bHandled*/);
	LRESULT OnSubY(WORD /*wNotifyCode*/, WORD wID, HWND /*hWndCtl*/, BOOL& /*bHandled*/);

// Handler prototypes (uncomment arguments if needed):
//	LRESULT MessageHandler(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
//	LRESULT CommandHandler(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
//	LRESULT NotifyHandler(int /*idCtrl*/, LPNMHDR /*pnmh*/, BOOL& /*bHandled*/)

	LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
	LRESULT OnCloseCmd(WORD /*wNotifyCode*/, WORD wID, HWND /*hWndCtl*/, BOOL& /*bHandled*/);
};
