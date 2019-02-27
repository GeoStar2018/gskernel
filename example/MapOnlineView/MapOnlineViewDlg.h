
// MapOnlineViewDlg.h : ͷ�ļ�
//

#pragma once
#include "afxwin.h"
#include "map"
#include "MapInstance.h"
using namespace std;

#include "GeoHeader.h"

// CMapOnlineViewDlg �Ի���
class CMapOnlineViewDlg : public CDialogEx
{
// ����
public:
	CMapOnlineViewDlg(CWnd* pParent = NULL);	// ��׼���캯��

// �Ի�������
#ifdef AFX_DESIGN_TIME
	enum { IDD = IDD_MAPONLINEVIEW_DIALOG };
#endif

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV ֧��


// ʵ��
protected:
	HICON m_hIcon;

	// ���ɵ���Ϣӳ�亯��
	virtual BOOL OnInitDialog();
	afx_msg void OnSysCommand(UINT nID, LPARAM lParam);
	afx_msg void OnPaint();
	afx_msg HCURSOR OnQueryDragIcon();
	afx_msg void OnSelectMap();
	afx_msg void OnBnClickedBtnUp();
	afx_msg void OnBnClickedBtnDown();
	afx_msg void OnBnClickedPan();
	afx_msg void OnLButtonDown(UINT nFlags, CPoint point);
	afx_msg void OnLButtonUp(UINT nFlags, CPoint point);
	afx_msg void OnMouseMove(UINT nFlags, CPoint point);
	afx_msg void OnBnClickedRadio();

	afx_msg LRESULT OnUpdataMap(WPARAM box, LPARAM);
	afx_msg void OnBnClickedTextfeature();

	DECLARE_MESSAGE_MAP()


private:
	void InitPyramids();

private:
	CRect m_mapRect;
	bool m_lBtnDown = false;
	CStatic m_mapLayer;
	CComboBox m_listTileSrc;
	map<GsString, CMapInstancePtr> m_mapMapInstance;
	CMapInstancePtr m_pCurrentMap;
	bool m_isPan = false;
	int m_iTileType;
};
