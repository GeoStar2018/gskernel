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

	/// \brief 内部绘制入口
	/// \details 子类通过覆盖此函数实现绘制。
	virtual bool InnerDraw(GsDisplay* pDisplay, GsTrackCancel* pCancel, GsDrawPhase eDrawPhase);

public:
	PointCloudLayer(const char* file);
	GsGeodesic m_GeoDesc;
	GsRawPoint3D& Center();
	double&  MaxHeight();

	virtual ~PointCloudLayer();
	/// \brief 图层的最大范围,以图层的空间参考显示
	virtual GsBox Extent(GsSpatialReference* pTargetSR = 0);

	/// \brief 图层是否存在选择集
	virtual bool HasSelection();


	/// \brief 克隆图层
	virtual GsSmarterPtr<GsLayer> Clone();
};

