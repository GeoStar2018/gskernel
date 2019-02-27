#include "stdafx.h"
#include "PointCloudLayer.h"


PointCloudLayer::PointCloudLayer(const char* file):m_GeoDesc(GsSpatialReferencePtr(new GsSpatialReference(4326)))
{
	GsFile(file).ReadAllBytes(&m_Buffer);
	float* f = m_Buffer.PtrT<float>();
	for (int i = 0; i < m_Buffer.BufferSizeT<float>(); i += 3)
	{
		m_Extent.Union(GsBox(f[i], f[i + 1], f[i], f[i + 1]));
	}
	m_Symbol = new GsSimplePointSymbol(GsColor::Red, 0.1);
	m_CenterSymbol = new GsSimplePointSymbol(GsColor::Red, 1);
	m_ptrPoint = new GsMultiPoint();
	m_MaxHeight = 40;
	m_vecPoints.reserve(m_Buffer.BufferSizeT<float>() / 3);
	m_ptrCenterPoint = new GsPoint();

}


PointCloudLayer::~PointCloudLayer()
{
}
GsRawPoint3D& PointCloudLayer::Center()
{
	return m_Center;
}
double&  PointCloudLayer::MaxHeight()
{
	return m_MaxHeight;
}
/// \brief 内部绘制入口
/// \details 子类通过覆盖此函数实现绘制。
bool PointCloudLayer::InnerDraw(GsDisplay* pDisplay, GsTrackCancel* pCancel, GsDrawPhase eDrawPhase)
{
	
	m_ptrCenterPoint->Set(m_Center);
	m_CenterSymbol->StartDrawing(pDisplay->Canvas(), pDisplay->DisplayTransformation());
	m_CenterSymbol->Draw(m_ptrCenterPoint);
	m_CenterSymbol->EndDrawing();


	m_vecPoints.clear();
	float* f = m_Buffer.PtrT<float>();
	double x, y, z;
	double t;
	for (int i = 0; i < m_Buffer.BufferSizeT<float>(); i += 3)
	{
		if (f[i + 2] > m_MaxHeight)
			continue;
		m_GeoDesc.Direct(m_Center.X, m_Center.Y, 90, f[i], &x, &t);
		m_GeoDesc.Direct(m_Center.X, m_Center.Y, 0, f[i+1], &t, &y);
		m_vecPoints.emplace_back(x, y);
	}
	if (m_vecPoints.empty())
		return true;

	m_ptrPoint->Set(&m_vecPoints[0], m_vecPoints.size());
	m_Symbol->StartDrawing(pDisplay->Canvas(), pDisplay->DisplayTransformation());
	m_Symbol->Draw(m_ptrPoint);
	m_Symbol->EndDrawing();

	return true;
}
/// \brief 图层的最大范围,以图层的空间参考显示
GsBox PointCloudLayer::Extent(GsSpatialReference* pTargetSR )
{
	return m_Extent;
}

/// \brief 图层是否存在选择集
bool PointCloudLayer::HasSelection()
{
	return false;
}


/// \brief 克隆图层
GsSmarterPtr<GsLayer> PointCloudLayer::Clone()
{
	return 0;
}
