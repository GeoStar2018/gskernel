// wtl_openmapView.cpp : implementation of the CWtl_openmapView class
//
/////////////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "resource.h"

#include "wtl_dynamic_layerView.h"
#include <atltypes.h>
using namespace GeoStar::Kernel;
using namespace GeoStar::Utility;
CWtl_dynamic_layerView::CWtl_dynamic_layerView()
{
	//�Զ�����־���
	GsLogger::Default().LogLevel(eLOGALL);
	GsLogger::Default().CustomOutput(this);

	m_Cursor = NULL;
	m_CurrentTracker = NULL;
}
CWtl_dynamic_layerView::~CWtl_dynamic_layerView()
{
	m_ptrMap.Release();
}
BOOL CWtl_dynamic_layerView::PreTranslateMessage(MSG* pMsg)
{
	pMsg;
	return FALSE;
}
bool CWtl_dynamic_layerView::OnLog(const char* log)
{
	//�����vs���������������
	OutputDebugStringA(log);
	return true;
}
//��Ӧ���ν������¼������õ�ͼ��Χ��
void CWtl_dynamic_layerView::OnExtentChanged(const KERNEL_NAME::GsBox& box)
{ 
	//���Ӹ��¼����첽����
	m_ptrMap->Cancel();
	m_ptrMap->ViewExtent(box);
	m_UpdateRef.Increment();  
}
bool CWtl_dynamic_layerView::OnUpdate(GsRefObject* ref, int reason, const GsBox& extent, double res, void* param)
{
	//ˢ�·�Χ����͵�ǰ��Χ���ཻ����Ϊ������¡�
	GsBox box = m_ptrMap->ViewExtent();
	if (box.IsDisjoin(extent))
		return true;
	
	//���Ӹ��¼����첽����
	m_UpdateRef.Increment();

	return true;
}

LRESULT CWtl_dynamic_layerView::OnTimer(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
{
	//��ʱˢ��,���û�и��¼�������Ϊ�������
	if (m_UpdateRef.Read() <= 0)
		return S_OK;

	//����������Σ��������ڻ��ƣ���ô������Ҫ����
	if (m_ptrMap->ScreenDisplay()->HasStartPan())
		return S_OK;

	if (m_ptrMap->ScreenDisplay()->IsDrawing())
		return S_OK;
	
	//������±�ʶ
	m_UpdateRef = 0;
	//ֹͣ���ƽ����µĸ���
	m_ptrMap->Cancel(); 
	m_ptrMap->Update();
	return S_OK;
}
void CWtl_dynamic_layerView::ZoomPan()
{
	//���õ�ǰ��TrackerΪ����Tracker
	m_CurrentTracker = m_ptrPanTracker;
	m_Cursor = LoadCursor(NULL, IDC_HAND);
}
void CWtl_dynamic_layerView::FullMap()
{
	//ȫͼ��ʾ
	m_ptrMap->Cancel();
	m_ptrMap->ViewExtent(m_ptrMap->FullExtent());
	m_ptrMap->Update();
}

void CWtl_dynamic_layerView::CreateMap()
{
	if(m_ptrMap)
		return;

	GsKernel::Initialize();
	//����������ȡbasemap�첽����ͼ��ĸ�����Ϣ
	m_ptrAgent = new KERNEL_NAME::GsUpdateAgent();
	m_ptrAgent->OnNeedUpdate.Add(this, &CWtl_dynamic_layerView::OnUpdate);

	//�����豸
	GeoStar::Kernel::GsPaintDevicePtr ptrDevice =
		GeoStar::Kernel::GsPaintDevice::CreatePaintDevice(
			GeoStar::Kernel::eWin32HwndDeviceD2D,m_hWnd);

	//���巶Χ��
	CRect rect;
	this->GetClientRect(&rect);
	  
	//��Ļ�͵�ͼ��Χ
	GeoStar::Kernel::GsDisplayTransformationPtr ptrDT = new 
		GeoStar::Kernel::GsDisplayTransformation(
		GsBox(0,0,rect.Width(),rect.Height()),
		GsRect(0,0,rect.Width(),rect.Height())); 

	GeoStar::Kernel::GsScreenDisplayPtr ptrDisplay = new 
		GsScreenDisplay(ptrDevice,ptrDT);

	//������ͼ
	m_ptrMap = new GsMap(ptrDisplay);

	//������
	GeoStar::Kernel::GsConnectProperty conn;
	conn.Server = GsFileSystem::Combine(GsFileSystem::WorkingFolder().c_str(), "../data/");

	GsSqliteGeoDatabaseFactory fac;
	GsGeoDatabasePtr ptrGDB = fac.Open(conn);
	GsFeatureClassPtr ptrFea = ptrGDB->OpenFeatureClass("CHN_ADM3");

	//�첽���Ƶ�BaseMapͼ�㡣
	GsBaseMapLayerPtr ptrBaseMap = new	GsBaseMapLayer();
	ptrBaseMap->UpdateAgent(m_ptrAgent);
	//��ͨ��FeatureLayer
	GsFeatureLayerPtr ptrFeaLyr = new GsFeatureLayer(ptrFea); 
	//����ͨ��ͼ����뵽basemap�дﵽ�첽���Ƶ�Ŀ�ġ�
	ptrBaseMap->LayerCollection()->Add(ptrFeaLyr);

	//�����ͼͼ��
	m_ptrMap->Layers()->push_back(ptrFeaLyr);
	 
	//��������������ͼ����Ϊ��̬ͼ�㡣
	//��̬ͼ����ƵĽ�����ᱣ�浽���棬���ÿ��ˢ�¶��ᵼ�¶�̬ͼ��Ļ��ơ�
	GsPyramidLayerPtr ptrDynamicLyr = new GsPyramidLayer(GsPyramid::WellknownPyramid(e360DegreePyramid));
	m_ptrMap->DynamicLayerCollection()->Add(ptrDynamicLyr);

	//ȱʡȫͼ��ʾ
	m_ptrMap->ViewExtent(m_ptrMap->FullExtent());

	//�����첽���ƵĶ�ʱ����
	SetTimer(10010, 100);

	//����tracker�Ͷ�Ӧ���¼���
	m_ptrPanTracker = new KERNEL_NAME::GsZoomPanClassicTracker(m_ptrMap);
	m_ptrPanTracker->OnExtentChanged.Add(this, &CWtl_dynamic_layerView::OnExtentChanged);
	 
	ZoomPan();
} 
LRESULT CWtl_dynamic_layerView::OnMouseDown(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& /*bHandled*/)
{
	if (m_CurrentTracker)
	{
		int x = GET_X_LPARAM(lParam);
		int y = GET_Y_LPARAM(lParam);

		m_ptrMap->Cancel();
		m_CurrentTracker->OnMouseDown(KERNEL_NAME::eLeftButton, 0, GsPT(x, y));
	}
	return S_OK;
}
LRESULT CWtl_dynamic_layerView::OnMouseUp(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& /*bHandled*/)
{
	if (m_CurrentTracker)
	{
		int x = GET_X_LPARAM(lParam);
		int y = GET_Y_LPARAM(lParam);
		m_CurrentTracker->OnMouseUp(KERNEL_NAME::eLeftButton, 0, GsPT(x, y));
	}
	return S_OK;
}
LRESULT CWtl_dynamic_layerView::OnMouseWheel(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& /*bHandled*/)
{
	if (m_CurrentTracker)
	{
		CPoint pt(GET_X_LPARAM(lParam), GET_Y_LPARAM(lParam));
		ScreenToClient(&pt);

		int zDelta = GET_WHEEL_DELTA_WPARAM(wParam);
		m_CurrentTracker->OnMouseWheel(KERNEL_NAME::eLeftButton, zDelta, 0, GsPT(pt.x, pt.y));
	}
	return S_OK;
}
LRESULT CWtl_dynamic_layerView::OnMouseMove(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& /*bHandled*/)
{
	if (m_Cursor)
		SetCursor(m_Cursor);

	if (m_CurrentTracker && (wParam & MK_LBUTTON))
	{
		int x = GET_X_LPARAM(lParam);
		int y = GET_Y_LPARAM(lParam);
		OutputDebugStringA("MouseMove\n");
		m_CurrentTracker->OnMouseMove(KERNEL_NAME::eLeftButton, 0, GsPT(x, y));
	}
	
	return S_OK;
}
LRESULT CWtl_dynamic_layerView::OnPaint(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
{
	CreateMap();
	CPaintDC dc(m_hWnd); 
	m_ptrMap->Paint();  

	return 0;
}
LRESULT CWtl_dynamic_layerView::OnSize(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
{
	
	if(!m_ptrMap)
		return S_OK;

	//�豸�����壩��Χ�����仯��֪ͨDisplayTransformation
	CRect rect;
	this->GetClientRect(&rect);
	m_ptrMap->ScreenDisplay()->DisplayTransformation()->DeviceExtent(GsRect(rect.left,rect.top,rect.Width(),rect.Height()));
	m_ptrMap->Invalidate();
	 
	return 0;
}
