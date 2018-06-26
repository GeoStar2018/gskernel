#include "stdafx.h"
#include "TargetSim.h"

#include "resource.h"

using namespace GeoStar::Kernel;
using namespace GeoStar::Utility;

TargetSim::TargetSim()
{ 
	m_Oil = 0;
	m_Speed = 0;
	m_Temp = 0;
	m_nCount = 5;
	m_bStarted = false;
	m_nInnerFlag = false;

}
void TargetSim::Bind(CTargetSimer* Simer)
{
	m_Simer = Simer;

}
void TargetSim::EnableModify(bool bOk)
{
	m_SpeedTrack.EnableWindow(bOk ? TRUE : FALSE);
	m_TempTrack.EnableWindow(bOk ? TRUE : FALSE);
	m_OilTrack.EnableWindow(bOk ? TRUE : FALSE);
}
TargetSim::~TargetSim()
{
	
}

#include <atlstr.h>
LRESULT TargetSim::OnBeginSim(WORD /*wNotifyCode*/, WORD wID, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
{
	CString str;
	m_TargetCount.GetWindowText(str);

	m_nCount = atoi(str.GetBuffer());
	EnableModify(false); 
	m_CurrentTarget.reset();
	if (!m_bStarted)
	{
		m_bStarted = true;
		m_Simer->CreateTarget(m_nCount);
		m_Simer->BeginSim();
		
		for (int i = 0; i < m_Simer->Count(); i++)
		{
			CTargetPtr ptrTarget = m_Simer->Target(i);
			if (!ptrTarget) continue;
			m_ListTarget.AddString(ptrTarget->Name());
		}
	}
	else
	{
		m_ListTarget.ResetContent();
		m_Simer->EndSim();
		m_bStarted = false; 
		DoDataExchange();

	}

	return 0;
}
LRESULT TargetSim::OnSelectTarget(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
{
	int n = m_ListTarget.GetCurSel();
	if (n < 0)
	{
		EnableModify(false);
		return 0;
	}
	EnableModify(true);
	m_CurrentTarget = m_Simer->Target(n);

	m_Oil = m_CurrentTarget->Parameter(eOil);
	m_Speed = m_CurrentTarget->Parameter(eSpeed);
	m_Temp = m_CurrentTarget->Parameter(eTemp);
	m_nInnerFlag = true;
	m_OilTrack.SetPos(m_Oil);
	m_TempTrack.SetPos(m_Temp);
	m_SpeedTrack.SetPos(m_Speed);
	m_nInnerFlag = false;
	DoDataExchange();
	return 0;
}

LRESULT TargetSim::OnTrackPosChanged(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
{

	return 0;
}
LRESULT TargetSim::OnPosChanged(int /*idCtrl*/, LPNMHDR /*pnmh*/, BOOL& /*bHandled*/)
{
	if (m_nInnerFlag)
		return 0;

	m_Oil = m_OilTrack.GetPos();
	m_Temp = m_TempTrack.GetPos();
	m_Speed = m_SpeedTrack.GetPos();
	if (m_CurrentTarget)
	{
		m_CurrentTarget->Parameter(eOil, m_Oil);
		m_CurrentTarget->Parameter(eSpeed, m_Speed);
		m_CurrentTarget->Parameter(eTemp, m_Temp);
	}
	DoDataExchange();
	return 0;
}
LRESULT TargetSim::OnClose(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
{
	DestroyWindow();
	return 0;
}

#include <sstream>
LRESULT TargetSim::OnNotify(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& /*bHandled*/)
{
	unsigned int nCode = ((LPNMHDR)lParam)->code;
	if (wParam == IDC_SLIDER_SPEED)
	{
		//unsigned int n = TRBN_THUMBPOSCHANGING;
		//if (nCode != n)
			//7return S_OK;

		std::stringstream ss;
		ss << "wParam" << wParam << "Code=" << std::hex << nCode << std::endl;
		OutputDebugString(ss.str().c_str());
	}
	return 0;

}
LRESULT TargetSim::OnTextChanged(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
{
	DoDataExchange(FALSE);
	return 0;
}
LRESULT TargetSim::OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
{
	m_ListTarget.Attach(GetDlgItem(IDC_LIST_TARGET));

	m_TargetCount.Attach(GetDlgItem(IDC_TARGET_COUNT));

	CString str;
	str.Format("%d", m_nCount);
	m_TargetCount.SetWindowText(str);

	m_SpeedTrack.Attach(GetDlgItem(IDC_SLIDER_SPEED));
	m_TempTrack.Attach(GetDlgItem(IDC_SLIDER_TEMP));
	m_OilTrack.Attach(GetDlgItem(IDC_SLIDER_OIL));

	m_SpeedTrack.SetRangeMin(0);
	m_SpeedTrack.SetRangeMax(100);

	m_OilTrack.SetRangeMin(0);
	m_OilTrack.SetRangeMax(100);

	m_TempTrack.SetRangeMin(0);
	m_TempTrack.SetRangeMax(100);
	 

	EnableModify(false);
	DoDataExchange();
	return 0;
}