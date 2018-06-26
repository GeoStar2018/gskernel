// wtl_openmap.cpp : main source file for wtl_openmap.exe
//

#include "stdafx.h"

#include "resource.h"

#include "wtl_openmapView.h"
#include "aboutdlg.h"
#include "MainFrm.h"

#ifdef _DEBUG
#pragma comment(lib,"geodatabased.lib")
#pragma comment(lib,"symbold.lib")
#pragma comment(lib,"geomathdd.lib")
#pragma comment(lib,"geometryd.lib")
#pragma comment(lib,"mapd.lib") 
#pragma comment(lib,"utilityd.lib") 
#pragma comment(lib,"spatialreferenced.lib") 
#pragma comment(lib,"qtport.lib") 
#pragma comment(lib,"win32portd.lib") 
#else
#pragma comment(lib,"geomathd.lib")
#pragma comment(lib,"geodatabase.lib")
#pragma comment(lib,"symbol.lib")
#pragma comment(lib,"geometry.lib")
#pragma comment(lib,"utility.lib") 
#pragma comment(lib,"spatialreference.lib") 
#pragma comment(lib,"map.lib") 
#pragma comment(lib,"qtport.lib") 
#pragma comment(lib,"win32port.lib") 
#endif
#include <iostream>

CAppModule _Module; 
int Run(LPTSTR /*lpstrCmdLine*/ = NULL, int nCmdShow = SW_SHOWDEFAULT)
{
	CMessageLoop theLoop;
	_Module.AddMessageLoop(&theLoop);

	CMainFrame wndMain;

	if(wndMain.CreateEx() == NULL)
	{
		ATLTRACE(_T("Main window creation failed!\n"));
		return 0;
	}

	wndMain.ShowWindow(nCmdShow);

	int nRet = theLoop.Run();

	_Module.RemoveMessageLoop();
	return nRet;
}

int WINAPI _tWinMain(HINSTANCE hInstance, HINSTANCE /*hPrevInstance*/, LPTSTR lpstrCmdLine, int nCmdShow)
{
	HRESULT hRes = ::CoInitialize(NULL);
// If you are running on NT 4.0 or higher you can use the following call instead to 
// make the EXE free threaded. This means that calls come in on a random RPC thread.
//	HRESULT hRes = ::CoInitializeEx(NULL, COINIT_MULTITHREADED);
	ATLASSERT(SUCCEEDED(hRes));

	// this resolves ATL window thunking problem when Microsoft Layer for Unicode (MSLU) is used
	::DefWindowProc(NULL, 0, 0, 0L);

	AtlInitCommonControls(ICC_COOL_CLASSES | ICC_BAR_CLASSES);	// add flags to support other controls

	hRes = _Module.Init(NULL, hInstance);
	ATLASSERT(SUCCEEDED(hRes));

	int nRet = Run(lpstrCmdLine, nCmdShow);

	_Module.Term();
	::CoUninitialize();

	return nRet;
}
