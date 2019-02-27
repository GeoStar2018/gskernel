#pragma once

#include <QtWidgets/QMainWindow>
#include "ui_QtGuiApplicationqqqq.h"
class QAction;
class QLabel;
class QtGuiApplicationqqqq : public QMainWindow
{
	Q_OBJECT

public:
	QtGuiApplicationqqqq(QWidget *parent = 0);
	~QtGuiApplicationqqqq();
	Ui::QtGuiApplicationqqqqClass ui;
	QAction *openAction;
	QLabel *msgLabel;
	
	void mousePressEvent(QMouseEvent* event);  // 鼠标按下
	void mouseReleaseEvent(QMouseEvent* event);  // 鼠标抬起
	void mouseMoveEvent(QMouseEvent* enevt); // 鼠标移动
	void QtGuiApplicationqqqq::wheelEvent(QWheelEvent *event);
	QImage* result;
	QImage qimage;
	double w;
	double h;
	bool m_bDrag; // 标记鼠标是否是左键选中lable
	QPoint m_ptPos; // 记录鼠标和label相对位置

public slots:
	QImage* open();


	
};
