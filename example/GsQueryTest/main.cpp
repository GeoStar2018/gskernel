#include "test.h"
#include <QtWidgets/QApplication>
#include "geospace2d.h"
#include <kernel.h>
#include <utility.h>

#ifdef WIN32
#ifdef _DEBUG
#pragma comment(lib,"gsgeodatabased.lib")
#pragma comment(lib,"gssymbold.lib")
#pragma comment(lib,"gsgeometryd.lib")
#pragma comment(lib,"gsmapd.lib") 
#pragma comment(lib,"gsutilityd.lib") 
#pragma comment(lib,"gsspatialreferenced.lib") 

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
#ifdef BUILD_QT 
#pragma comment(lib,"gsqtport.lib") 
#endif
#pragma comment(lib,"gswin32port.lib") 
#pragma comment(lib,"gspcgeodatabaseport.lib") 
#endif
#endif 

#pragma comment(lib,"gsqtport.lib")

int main(int argc, char *argv[])
{
	GeoStar::Kernel::GsKernel::Initialize();
	QApplication a(argc, argv);

	Test w;
	w.show();

	return a.exec();
}
