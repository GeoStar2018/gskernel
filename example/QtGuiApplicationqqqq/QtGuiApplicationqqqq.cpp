#include "QtGuiApplicationqqqq.h"
#include <QMessageBox>
#include <QLabel>
#include <QFileDialog>
#include <QtoolButton>
#include <QImage>
#include <QPixmap>
#include <QFileDialog>
#include <QPainter>
#include <QScreen>
#include <QGuiApplication>
#include <F:\ALLTest\40_Source\include\utility\utility.h>
#include <F:\ALLTest\40_Source\include\kernel\kernel.h>
#include <F:\ALLTest\40_Source\include\kernel\geometry.h>
#include <QMouseEvent>


using namespace GeoStar::Kernel;
using namespace GeoStar::Utility;
using namespace std;

QImage* fcsToPng(const char* name, const char* filepath);
void paintEvent(QPaintEvent *);

QtGuiApplicationqqqq::QtGuiApplicationqqqq(QWidget *parent)
	: QMainWindow(parent)
{
	openAction = new QAction(tr("&Open"), this);
	openAction->setShortcut(QKeySequence::Open);
	openAction->setStatusTip(tr("Open a file."));
	openAction->setIcon(QIcon(":/open.png"));
	connect(openAction, SIGNAL(triggered()), this, SLOT(open()));

	msgLabel = new QLabel;//增加状态栏
	msgLabel->setMinimumSize(msgLabel->sizeHint());
	msgLabel->setAlignment(Qt::AlignHCenter);

	statusBar()->addWidget(msgLabel);


	QMenu *file = menuBar()->addMenu(tr("&File"));

	file->addAction(openAction);

	QToolBar *toolBar = addToolBar(tr("&File"));
	toolBar->addAction(openAction);

	ui.setupUi(this);

	
		

	
}
QtGuiApplicationqqqq::~QtGuiApplicationqqqq() {

}

QImage*  QtGuiApplicationqqqq::open() {

	QString qfilename = QFileDialog::getOpenFileName(this, tr("open a  data"), "", tr("Images (*.png *.bmp *.jpg *.tif *.GIF *.fcs ))"), 0);//打开一个对话框

	
		QFileInfo fileInfo(qfilename);
		//获取文件的不包含文件名的路径，返回值为 “/usr”
		QString qfilepath = fileInfo.path();
		//获取到文件的名称，返回值为 “test.xxx.yyy”
		QString qname = fileInfo.fileName();
	
		
		string  filepath = qfilepath.toStdString();
		string  name = qname.toStdString();
		
		 result =fcsToPng(name.c_str(), filepath.c_str());


		//if (!(result->load(qfilename))) //加载地图数据
		//{
		//	QMessageBox::information(this, "", tr("fail!"));//加载失败的情况下弹窗
		//	return;
		//}
	

		 w = result->width();
		 h = result->height();
		 qimage = result->scaled(w/4, h/4);
		//result->setScaledSize(QSize(w, h));
		ui.label->setPixmap(QPixmap::fromImage(qimage));


	
		return result;

	
}

QImage* fcsToPng (const char* name, const char* filepath) {
		GsSqliteGeoDatabaseFactory vFac;
		GsConnectProperty vConn;
		vConn.DataSourceType = eSqliteFile;
		vConn.Server = filepath;

		GsGeoDatabasePtr ptrGDB = vFac.Open(vConn);
		
		GsFeatureClassPtr  ptrFeatureClass = ptrGDB->OpenFeatureClass(name);
		GsFeatureLayerPtr	ptrFeatureLayer = new GsFeatureLayer(ptrFeatureClass);

		GsMapPtr ptrMap = new GsMap(NULL);
		ptrMap->Layers()->push_back(ptrFeatureLayer);
		GsMemoryImageCanvasPtr ptrCanvas = new GsMemoryImageCanvas(1024, 1024);
		GsDisplayTransformationPtr ptrDT = new GsDisplayTransformation(ptrFeatureLayer->Extent(), GsRect(0, 0, 1024, 1024));
		GsDisplayPtr ptrDisp = new GsDisplay(ptrCanvas, ptrDT);

		GsBox box = ptrDisp->ClipEnvelope();

		ptrMap->Output(ptrDisp, NULL);
		ptrMap->Paint(eDrawNormal);
		GsScreenDisplay* ScreenDisplay = ptrMap->ScreenDisplay();

		//GsImagePtr Img = ptrCanvas->Image();
		ptrCanvas->Image()->SavePNG("D:\\test2.png");


		GsGrowByteBuffer imageBytes;
		ptrCanvas->Image()->SavePNG(&imageBytes);
		
		unsigned char *image = imageBytes.Ptr();
		//const   char*   pimage = imageBytes.ToBase64().c_str();
		const   char*   p = (const   char*)(char*)image;

		uchar *pData = (unsigned char*)(const_cast<char*>(p));

		
		GsImagePtr ptrImg = GsImage::LoadFrom(pData,imageBytes.BufferSize());
		
		ptrImg->SavePNG("D:\\test5.png");

	
		//QImage *  imagePtr = new QImage(image,w, h, QImage::Format_ARGB32);
		QImage *  imagePtr = new QImage("D:\\test5.png");
		
		//QImage* imagePtr = new QImage(p); //uchar *data, int width, int height, Format format,

			return imagePtr;	
	}





void QtGuiApplicationqqqq::mousePressEvent(QMouseEvent* event) {
	// 判断是否是左键按下
	if (event->button() == Qt::LeftButton) {
		// 获取label矩形区域
		QRect rcBlock = ui.label->frameRect();
		// 平移，保证鼠标和矩形区域坐标系一致
		rcBlock.translate(ui.label->pos());
		// 判断点击的位置是否在rcBlock之内
		if (rcBlock.contains(event->pos()) == true) {
			m_bDrag = true;
			m_ptPos = ui.label->pos() - event->pos(); // QPoint
		}
	}
}
void QtGuiApplicationqqqq::mouseReleaseEvent(QMouseEvent* event) {
	// 判断是否是左键抬起
	if (event->button() == Qt::LeftButton) {
		m_bDrag = false;
	}
}
void QtGuiApplicationqqqq::mouseMoveEvent(QMouseEvent* event) {
	if (m_bDrag) {
		// 计算新的坐标位置
		QPoint ptPos = m_ptPos + event->pos();
		// 设置label移动的边界区域限制
		// 获取父窗口大小
		QSize szClient = size();
		// 获取label的矩形区域 == 大小
		// QRect rcBlock = ui->label->frameRect (); 此行无用
		// x:0 (窗口.宽度 - label.宽度)
		if (ptPos.x() < 0) {
			ptPos.setX(0);
		}
		else
			if (ptPos.x() > szClient.width() - ui.label->width()) {
				ptPos.setX(szClient.width() - ui.label->width());
			}
		// y:0 (窗口.高度 - label.高度)
		if (ptPos.y() < 0) {
			ptPos.setY(0);
		}
		else
			if (ptPos.y() > szClient.height() - ui.label->height()) {
				ptPos.setY(szClient.height() - ui.label->height());
			}
		// 移动label到新的位置
		ui.label->move(ptPos);
	}
} 

void QtGuiApplicationqqqq::wheelEvent(QWheelEvent *event)    // 滚轮事件，滚轮默认滚动一下是15度，此时date（）函数会返回15*8=120的整数，当滚轮远离返回正值，反之负值。
{
	if (event->delta() > 0) {//如果滚轮往上滚  
		
		QImage image = ui.label->pixmap()->toImage();
		double d1= image.width();//设置宽度为原有基础上+25
		double h1 = image.height();
		QImage qimage1 = image.scaled(d1 / 1.2, h1 / 1.2);
		ui.label->setPixmap(QPixmap::fromImage(qimage1));

	}
	else {//同样的      
		QImage image = ui.label->pixmap()->toImage();
		double d1 = image.width();//设置宽度为原有基础上+25
		double h1 = image.height();
		QImage qimage1 = image.scaled(d1 / 0.8, h1 / 0.8);
		ui.label->setPixmap(QPixmap::fromImage(qimage1));


	}

}



