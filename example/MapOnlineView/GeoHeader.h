#ifndef _GEO_HEADER_H__
#define _GEO_HEADER_H__
#include "preconfig.h"
#include "pc/pcgeodatabase.h"

#define UTF8(x) x

#include "utility.h"
#define ENABLE_WIN32
#include "kernel.h"
#include "tilesplit.h"
#include "layout.h"

#ifdef _WIN32
#ifdef _DEBUG
#pragma comment(lib,"gsgeodatabased.lib")
#pragma comment(lib,"gssymbold.lib")
#pragma comment(lib,"gsgeometryd.lib")
#pragma comment(lib,"gsmapd.lib") 
#pragma comment(lib,"gsutilityd.lib") 
#pragma comment(lib,"gsspatialreferenced.lib") 
#pragma comment(lib,"gsspatialanalysisd.lib") 
#pragma comment(lib,"gsluaport.lib") 
#pragma comment(lib,"gslayoutd.lib")
#ifdef BUILD_QT 
#pragma comment(lib,"gsqtport.lib") 
#endif

#pragma comment(lib,"gswin32portd.lib") 
#pragma comment(lib,"gspcgeodatabaseportd.lib") 
#else
#pragma comment(lib,"gsgeodatabase.lib")
#pragma comment(lib,"gssymbol.lib")
#pragma comment(lib,"gsgeometry.lib")
#pragma comment(lib,"gsutility.lib") 
#pragma comment(lib,"gsspatialreference.lib") 
#pragma comment(lib,"gsmap.lib") 
#pragma comment(lib,"gsspatialanalysis.lib") 
#pragma comment(lib,"gsluaport.lib") 
#ifdef BUILD_QT 
#pragma comment(lib,"gsqtport.lib") 
#endif
#pragma comment(lib,"gswin32port.lib") 
#pragma comment(lib,"gspcgeodatabaseport.lib") 
#endif

#endif 

#endif//_GEO_HEADER_H__

using namespace GeoStar::Kernel;
using namespace GeoStar::Utility;
#define WM_MAP_UPDATE  WM_USER + 1