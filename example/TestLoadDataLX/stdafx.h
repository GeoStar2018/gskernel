// stdafx.h : 标准系统包含文件的包含文件，
// 或是经常使用但不常更改的
// 特定于项目的包含文件
//

#pragma once

//#include "targetver.h"

#include <stdio.h>
#include <tchar.h>

#ifdef WIN32
#ifdef _DEBUG

//#pragma comment(lib,"../../middle/gsqtport/Debug/Win32/debug/gsqtport.lib")
#pragma comment(lib,"gsqtport.lib")

#pragma comment(lib,"gsgeodatabased.lib")
#pragma comment(lib,"gssymbold.lib")
#pragma comment(lib,"gsgeomathdd.lib")
#pragma comment(lib,"gsgeometryd.lib")
#pragma comment(lib,"gsmapd.lib") 
#pragma comment(lib,"gsutilityd.lib") 
#pragma comment(lib,"gsspatialreferenced.lib") 
#pragma comment(lib,"gsspatialanalysisd.lib") 

#ifdef BUILD_QT 
#pragma comment(lib,"gsqtport.lib") 
#endif

#pragma comment(lib,"gswin32portd.lib") 
#pragma comment(lib,"gspcgeodatabaseportd.lib") 
#else
#pragma comment(lib,"gsgeomathd.lib")
#pragma comment(lib,"gsgeodatabase.lib")
#pragma comment(lib,"gssymbol.lib")
#pragma comment(lib,"gsgeometry.lib")
#pragma comment(lib,"gsutility.lib") 
#pragma comment(lib,"gsspatialreference.lib") 
#pragma comment(lib,"gsmap.lib") 
#pragma comment(lib,"gsspatialanalysis.lib") 
#ifdef BUILD_QT 
#pragma comment(lib,"gsqtport.lib") 
#endif
#pragma comment(lib,"gswin32port.lib") 
#pragma comment(lib,"gspcgeodatabaseport.lib") 
#endif
#endif 
// TODO: 在此处引用程序需要的其他头文件
