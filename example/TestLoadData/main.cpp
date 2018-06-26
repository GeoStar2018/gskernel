#include "mainwindow.h"
#include <QApplication>
#include <string>
#include <QDesktopWidget>
#include <QDir>

class CommonHelper
{
public:
    static void setStyle(const QString &style) {
        QFile qss(style);
        qss.open(QFile::ReadOnly);
        qApp->setStyleSheet(qss.readAll());
        qss.close();
    }
	static void InitGs()
	{
		GeoStar::Kernel::GsKernel::Initialize();
	}
};

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    // 加载QSS样式
    //获取应用程序的路径
    QDir dir(QCoreApplication::applicationDirPath());
    QString cssFile  = dir.absoluteFilePath("../example/QSS/white.qss");
    CommonHelper::setStyle(cssFile);
	CommonHelper::InitGs();
    MainWindow w;
    w.show();

    return a.exec();
}
