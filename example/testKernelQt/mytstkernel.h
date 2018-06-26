#ifndef MYTSTKERNEL_H
#define MYTSTKERNEL_H

#include <QMainWindow>
#include <QMenu>
#include <QMenuBar>
#include <QAction>
#include <QToolBar>
#include <QToolButton>
#include "mapview.h"

class MyTstKernel : public QMainWindow
{
    Q_OBJECT

public:
    MyTstKernel(QWidget *parent = 0);
    ~MyTstKernel();

    void createActions();                        	//创建动作
    void createMenus();                           	//创建菜单
    void createToolBars();                      	//创建工具栏


private:
	MapView*        m_MapView;

    QMenu *fileMenu;                           		//各项菜单栏
    QMenu *zoomMenu;

    QAction *openFileAction;
    QAction *zoomInAction;
    QAction *zoomOutAction;
	QAction *selectAction;
    QAction *queryAction;

    QToolBar *fileTool;                          	//工具栏
    QToolBar *zoomTool;
    QToolBar *selectTool;
    QToolBar *queryTool;

protected slots:
    void OpenFile();

    void ShowZoomIn();
    void ShowZoomOut();

    void SelectByEnve();
    void QueryByEnve();
};

#endif // MYTSTKERNEL_H
