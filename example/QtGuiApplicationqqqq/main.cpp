#include "QtGuiApplicationqqqq.h"
#include <QtWidgets/QApplication>
#include <QLibrary>
#include <QSpinBox>
#include <QHBoxLayout>
#include <F:\ALLTest\40_Source\include\utility\utility.h>
#include <F:\ALLTest\40_Source\include\kernel\kernel.h>
#include <F:\ALLTest\40_Source\include\kernel\geometry.h>

using namespace GeoStar::Kernel;
using namespace GeoStar::Utility;
int main(int argc, char *argv[])
{

	QApplication app(argc, argv);
	

	QtGuiApplicationqqqq w;
	w.show();
	return app.exec();
}

