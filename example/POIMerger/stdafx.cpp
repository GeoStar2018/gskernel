// stdafx.cpp : 只包括标准包含文件的源文件
// POIMerger.pch 将作为预编译头
// stdafx.obj 将包含预编译类型信息

#include "stdafx.h"

// TODO: 在 STDAFX.H 中引用任何所需的附加头文件，
//而不是在此文件中引用
#ifdef _DEBUG 
#pragma comment(lib,"gsgeometryd.lib")
#pragma comment(lib,"gsutilityd.lib")  
#pragma comment(lib,"gsspatialreferenced.lib")
#pragma comment(lib,"gssymbold.lib")
#pragma comment(lib,"gsgeodatabased.lib")
#else 
#pragma comment(lib,"gsgeometry.lib")
#pragma comment(lib,"gsutility.lib") 
#pragma comment(lib,"gsspatialreference.lib")
#pragma comment(lib,"gssymbol.lib")
#pragma comment(lib,"gsgeodatabase.lib")
#endif