#include "mytstkernel.h"
#include <QApplication>

int main(int argc, char *argv[])
{
    GeoStar::Kernel::GsKernel::Initialize();

    QApplication a(argc, argv);

	MyTstKernel w;
	w.setGeometry(200,100,800,600);
    w.show();

    return a.exec();
}
