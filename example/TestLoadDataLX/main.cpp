//#include "testqt5.h"
//#include "geospace2d.h"
//#include "stdafx.h"
//#include <QtWidgets/QApplication>

#pragma comment(lib,"gsgeodatabased.lib")
#pragma comment(lib,"gsgeomathdd.lib")
#pragma comment(lib,"gsGeomathSE.lib")
#pragma comment(lib,"gsgeometryd.lib")
#pragma comment(lib,"gsmapd.lib")
#pragma comment(lib,"gspcgeodatabaseportd.lib")
#pragma comment(lib,"gsqtport.lib")
#pragma comment(lib,"gsspatialanalysisd.lib")
#pragma comment(lib,"gsspatialreferenced.lib")
#pragma comment(lib,"gssymbold.lib")
#pragma comment(lib,"gsutilityd.lib")
#pragma comment(lib,"gswin32portd.lib")

#include "testqt5.h"
#include <QtWidgets/QApplication>
#include "geospace2d.h"
#include "utility.h"
//#include "mapView.h"

//#pragma comment(lib,"../debugx86/gsqtport.lib")

int main(int argc, char *argv[])
{
	QApplication a(argc, argv);
	GeoStar::Kernel::GsKernel::Initialize();
	testQT5 w1;

	w1.show();
	return a.exec();
}
