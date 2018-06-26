// stdafx.cpp : source file that includes just the standard includes
//	wtl_showpagelayout.pch will be the pre-compiled header
//	stdafx.obj will contain the pre-compiled type information

#include "stdafx.h"

#if (_ATL_VER < 0x0700)
#include <atlimpl.cpp>
#endif //(_ATL_VER < 0x0700)


#ifdef _WIN32 
#ifdef _DEBUG
#pragma comment(lib,"gsgeodatabased.lib")
#pragma comment(lib,"gssymbold.lib")
#pragma comment(lib,"gsgeometryd.lib")
#pragma comment(lib,"gsutilityd.lib") 
#pragma comment(lib,"gsspatialreferenced.lib") 
#pragma comment(lib,"gsmapd.lib") 
#pragma comment(lib,"gslayoutd.lib") 
#pragma comment(lib,"gswin32portd.lib") 
#else
#pragma comment(lib,"gsspatialreference.lib") 
#pragma comment(lib,"gsgeodatabase.lib")
#pragma comment(lib,"gssymbol.lib")
#pragma comment(lib,"gsgeometry.lib")
#pragma comment(lib,"gsutility.lib") 
#pragma comment(lib,"gsmap.lib") 
#pragma comment(lib,"gslayout.lib") 
#pragma comment(lib,"gswin32port.lib") 
#endif

#endif
