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

	msgLabel = new QLabel;//����״̬��
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

	QString qfilename = QFileDialog::getOpenFileName(this, tr("open a  data"), "", tr("Images (*.png *.bmp *.jpg *.tif *.GIF *.fcs ))"), 0);//��һ���Ի���

	
		QFileInfo fileInfo(qfilename);
		//��ȡ�ļ��Ĳ������ļ�����·��������ֵΪ ��/usr��
		QString qfilepath = fileInfo.path();
		//��ȡ���ļ������ƣ�����ֵΪ ��test.xxx.yyy��
		QString qname = fileInfo.fileName();
	
		
		string  filepath = qfilepath.toStdString();
		string  name = qname.toStdString();
		
		 result =fcsToPng(name.c_str(), filepath.c_str());


		//if (!(result->load(qfilename))) //���ص�ͼ����
		//{
		//	QMessageBox::information(this, "", tr("fail!"));//����ʧ�ܵ�����µ���
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
	// �ж��Ƿ����������
	if (event->button() == Qt::LeftButton) {
		// ��ȡlabel��������
		QRect rcBlock = ui.label->frameRect();
		// ƽ�ƣ���֤���;�����������ϵһ��
		rcBlock.translate(ui.label->pos());
		// �жϵ����λ���Ƿ���rcBlock֮��
		if (rcBlock.contains(event->pos()) == true) {
			m_bDrag = true;
			m_ptPos = ui.label->pos() - event->pos(); // QPoint
		}
	}
}
void QtGuiApplicationqqqq::mouseReleaseEvent(QMouseEvent* event) {
	// �ж��Ƿ������̧��
	if (event->button() == Qt::LeftButton) {
		m_bDrag = false;
	}
}
void QtGuiApplicationqqqq::mouseMoveEvent(QMouseEvent* event) {
	if (m_bDrag) {
		// �����µ�����λ��
		QPoint ptPos = m_ptPos + event->pos();
		// ����label�ƶ��ı߽���������
		// ��ȡ�����ڴ�С
		QSize szClient = size();
		// ��ȡlabel�ľ������� == ��С
		// QRect rcBlock = ui->label->frameRect (); ��������
		// x:0 (����.��� - label.���)
		if (ptPos.x() < 0) {
			ptPos.setX(0);
		}
		else
			if (ptPos.x() > szClient.width() - ui.label->width()) {
				ptPos.setX(szClient.width() - ui.label->width());
			}
		// y:0 (����.�߶� - label.�߶�)
		if (ptPos.y() < 0) {
			ptPos.setY(0);
		}
		else
			if (ptPos.y() > szClient.height() - ui.label->height()) {
				ptPos.setY(szClient.height() - ui.label->height());
			}
		// �ƶ�label���µ�λ��
		ui.label->move(ptPos);
	}
} 

void QtGuiApplicationqqqq::wheelEvent(QWheelEvent *event)    // �����¼�������Ĭ�Ϲ���һ����15�ȣ���ʱdate���������᷵��15*8=120��������������Զ�뷵����ֵ����֮��ֵ��
{
	if (event->delta() > 0) {//����������Ϲ�  
		
		QImage image = ui.label->pixmap()->toImage();
		double d1= image.width();//���ÿ��Ϊԭ�л�����+25
		double h1 = image.height();
		QImage qimage1 = image.scaled(d1 / 1.2, h1 / 1.2);
		ui.label->setPixmap(QPixmap::fromImage(qimage1));

	}
	else {//ͬ����      
		QImage image = ui.label->pixmap()->toImage();
		double d1 = image.width();//���ÿ��Ϊԭ�л�����+25
		double h1 = image.height();
		QImage qimage1 = image.scaled(d1 / 0.8, h1 / 0.8);
		ui.label->setPixmap(QPixmap::fromImage(qimage1));


	}

}



