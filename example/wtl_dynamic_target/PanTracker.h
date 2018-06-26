#pragma once
class PanTracker:public GeoStar::Kernel::GsSymbolTracker
{
	bool m_bIsPan;
	CWindow* m_hWnd;
public:
	PanTracker(GeoStar::Kernel::GsMap* map, CWindow* hWnd);
	virtual ~PanTracker();
	/// \brief 接收鼠标按下消息
	virtual void OnMouseDown(GeoStar::Kernel::GsButton eButton, int nKeyboard, const GeoStar::Utility::GsPT& pt);
	/// \brief 接收鼠标弹起消息
	virtual void OnMouseUp(GeoStar::Kernel::GsButton eButton, int nKeyboard, const GeoStar::Utility::GsPT& pt);
	/// \brief 接收鼠标移动消息
	virtual void OnMouseMove(GeoStar::Kernel::GsButton eButton, int nKeyboard, const GeoStar::Utility::GsPT& pt);
	/// \brief 接收鼠标滚轮消息
	virtual void OnMouseWheel(GeoStar::Kernel::GsButton eButton, int nDelta, int nKeyboard, const GeoStar::Utility::GsPT& pt);

};

/// \brief GsSymbolTrackerPtr
GS_SMARTER_PTR(PanTracker);
