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
	
	void mousePressEvent(QMouseEvent* event);  // ��갴��
	void mouseReleaseEvent(QMouseEvent* event);  // ���̧��
	void mouseMoveEvent(QMouseEvent* enevt); // ����ƶ�
	void QtGuiApplicationqqqq::wheelEvent(QWheelEvent *event);
	QImage* result;
	QImage qimage;
	double w;
	double h;
	bool m_bDrag; // �������Ƿ������ѡ��lable
	QPoint m_ptPos; // ��¼����label���λ��

public slots:
	QImage* open();


	
};
