#ifndef TEST_H
#define TEST_H

#include <QtWidgets/QMainWindow>
#include "ui_test.h"
#include "CMap.h"

using namespace GeoStar::Kernel;


class Test : public QMainWindow
{
	Q_OBJECT

public:
	Test(QWidget *parent = 0);
	~Test();
	MapView* m_MapView;

private:
	Ui::TestClass ui;
private slots:
	void AddData();
	void ZoomInEnv();
	void ZoomOutEnv();
	void ZoomPan();
	void FullMap();
	void SelectByEnvelop();
	void QueryByEnv();
};

#endif // TEST_H
