#pragma once
#include <kernel.h>
#include <vector>
using namespace KERNEL_NAME;
using namespace UTILITY_NAME;

class PointCloudLayer:public KERNEL_NAME::GsLayer
{
	GsBox m_Extent;
	GsGrowByteBuffer m_Buffer;
	GsRawPoint3D m_Center;
	GsSimplePointSymbolPtr m_Symbol;
	GsSimplePointSymbolPtr m_CenterSymbol;
	GsPointPtr				m_ptrCenterPoint;
	GsVector<GsRawPoint> m_vecPoints;

	GsMultiPointPtr m_ptrPoint;
	double m_MaxHeight;
protected:

	/// \brief �ڲ��������
	/// \details ����ͨ�����Ǵ˺���ʵ�ֻ��ơ�
	virtual bool InnerDraw(GsDisplay* pDisplay, GsTrackCancel* pCancel, GsDrawPhase eDrawPhase);

public:
	PointCloudLayer(const char* file);
	GsGeodesic m_GeoDesc;
	GsRawPoint3D& Center();
	double&  MaxHeight();

	virtual ~PointCloudLayer();
	/// \brief ͼ������Χ,��ͼ��Ŀռ�ο���ʾ
	virtual GsBox Extent(GsSpatialReference* pTargetSR = 0);

	/// \brief ͼ���Ƿ����ѡ��
	virtual bool HasSelection();


	/// \brief ��¡ͼ��
	virtual GsSmarterPtr<GsLayer> Clone();
};

