
// MapOnlineView.h : PROJECT_NAME Ӧ�ó������ͷ�ļ�
//

#pragma once

#ifndef __AFXWIN_H__
	#error "�ڰ������ļ�֮ǰ������stdafx.h�������� PCH �ļ�"
#endif

#include "resource.h"		// ������


// CMapOnlineViewApp: 
// �йش����ʵ�֣������ MapOnlineView.cpp
//

class CMapOnlineViewApp : public CWinApp
{
public:
	CMapOnlineViewApp();

// ��д
public:
	virtual BOOL InitInstance();

// ʵ��

	DECLARE_MESSAGE_MAP()
};

extern CMapOnlineViewApp theApp;