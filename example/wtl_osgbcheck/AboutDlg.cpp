// aboutdlg.cpp : implementation of the CAboutDlg class
//
/////////////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "resource.h"

#include "aboutdlg.h"

LRESULT CAboutDlg::OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
{
	CenterWindow(GetParent());
	return TRUE;
} 
LRESULT CAboutDlg::OnCloseCmd(WORD /*wNotifyCode*/, WORD wID, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
{
	EndDialog(wID);
	return 0;
}
void CAboutDlg::MoveCenter(double val, bool x)
{
	DoDataExchange(TRUE);
	val *= m_Delta;
	if (x)
		m_view->m_PointLayer->m_GeoDesc.Direct(m_OffsetX, m_OffsetY, 90, val, &m_OffsetX, &m_OffsetY);
	else
		m_view->m_PointLayer->m_GeoDesc.Direct(m_OffsetX, m_OffsetY, 0, val, &m_OffsetX, &m_OffsetY);
	m_view->m_PointLayer->Center().X = m_OffsetX;
	m_view->m_PointLayer->Center().Y = m_OffsetY;
	DoDataExchange(FALSE);
	m_view->NeedUpdate();
}
LRESULT CAboutDlg::OnAddX(WORD /*wNotifyCode*/, WORD wID, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
{
	MoveCenter(1, true);
	return S_OK;
}
LRESULT CAboutDlg::OnAddY(WORD /*wNotifyCode*/, WORD wID, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
{
	MoveCenter(1,false);
	return S_OK;
}
LRESULT CAboutDlg::OnSubX(WORD /*wNotifyCode*/, WORD wID, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
{
	MoveCenter(-1 ,true);
	return S_OK;
}
LRESULT CAboutDlg::OnSubY(WORD /*wNotifyCode*/, WORD wID, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
{
	MoveCenter( -1,false);
	return S_OK;
}

void CAboutDlg::BindView(CWtl_osgbcheckView* view)
{
	m_view = view;
	m_OffsetX = m_view->m_PointLayer->Center().X;
	m_OffsetY = m_view->m_PointLayer->Center().Y;
	m_Delta = 5;
	DoDataExchange(FALSE);

}