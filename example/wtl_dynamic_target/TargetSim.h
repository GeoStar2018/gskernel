#pragma once
//动态目标模拟的对话框
#include "atlddx.h"
#include "atlctrls.h"
#include "resource.h"
#include "Commctrl.h"
#include "TargetSimer.h"

class TargetSim : public CDialogImpl<TargetSim>, public CWinDataExchange<TargetSim>
{
	int m_Oil;
	int m_Speed;
	int m_Temp; 
	int m_nCount;
	CTrackBarCtrl m_SpeedTrack;
	CTrackBarCtrl m_TempTrack;
	CTrackBarCtrl m_OilTrack;
	CTargetSimer* m_Simer;
	CListBox		m_ListTarget;
	CTargetPtr		m_CurrentTarget;
	CEdit			m_TargetCount;
	bool	m_nInnerFlag;
	bool m_bStarted;
	void EnableModify(bool bOk);
public:
	enum { IDD = IDD_SIM};
	TargetSim();
	~TargetSim();
	void Bind(CTargetSimer* Simer);

	//TRBN_THUMBPOSCHANGING
	BEGIN_MSG_MAP(TargetSim) 
		MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
		MESSAGE_HANDLER(WM_CLOSE, OnClose)
		//MESSAGE_HANDLER(WM_NOTIFY, OnNotify)
		COMMAND_ID_HANDLER(IDC_BEGINSIM, OnBeginSim) 
		COMMAND_HANDLER(IDC_LIST_TARGET, LBN_SELCHANGE, OnSelectTarget)
		COMMAND_HANDLER(IDC_SLIDER_SPEED, TRBN_THUMBPOSCHANGING, OnTrackPosChanged)
		COMMAND_HANDLER(IDC_SLIDER_TEMP, TRBN_THUMBPOSCHANGING, OnTrackPosChanged)
		COMMAND_HANDLER(IDC_SLIDER_OIL, 0xfffffff4, OnTrackPosChanged)
		//COMMAND_HANDLER(IDC_TARGET_COUNT, EN_CHANGE, OnTextChanged)
		
		NOTIFY_HANDLER(IDC_SLIDER_SPEED, 0xfffffff4, OnPosChanged)
		NOTIFY_HANDLER(IDC_SLIDER_TEMP, 0xfffffff4, OnPosChanged)
		NOTIFY_HANDLER(IDC_SLIDER_OIL, 0xfffffff4, OnPosChanged) 
	END_MSG_MAP()
	BEGIN_DDX_MAP(TargetSim)
		DDX_INT(IDC_OIL_VALUE, m_Oil)
		DDX_INT(IDC_SPEED_VALUE, m_Speed)
		DDX_INT(IDC_TEMP_VALUE, m_Temp)
		//DDX_INT(IDC_TARGET_COUNT, m_nCount)
	END_DDX_MAP()


	// Handler prototypes (uncomment arguments if needed):
	//	LRESULT MessageHandler(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
	//	LRESULT CommandHandler(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
	//	LRESULT NotifyHandler(int /*idCtrl*/, LPNMHDR /*pnmh*/, BOOL& /*bHandled*/)
	 
	LRESULT OnNotify(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
	LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/); 
	LRESULT OnClose(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/); 
	 
	LRESULT OnBeginSim(WORD /*wNotifyCode*/, WORD wID, HWND /*hWndCtl*/, BOOL& /*bHandled*/); 
	LRESULT OnPosChanged(int /*idCtrl*/, LPNMHDR /*pnmh*/, BOOL& /*bHandled*/);
	LRESULT OnSelectTarget(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/);
	LRESULT OnTrackPosChanged(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/);
	LRESULT OnTextChanged(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/);
	


};

