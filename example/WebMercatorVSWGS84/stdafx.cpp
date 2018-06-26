// stdafx.cpp : source file that includes just the standard includes
//	WebMercatorVSWGS84.pch will be the pre-compiled header
//	stdafx.obj will contain the pre-compiled type information

#include "stdafx.h"

#if (_ATL_VER < 0x0700)
#include <atlimpl.cpp>
#endif //(_ATL_VER < 0x0700)

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