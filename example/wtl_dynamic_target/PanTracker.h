#pragma once
class PanTracker:public GeoStar::Kernel::GsSymbolTracker
{
	bool m_bIsPan;
	CWindow* m_hWnd;
public:
	PanTracker(GeoStar::Kernel::GsMap* map, CWindow* hWnd);
	virtual ~PanTracker();
	/// \brief ������갴����Ϣ
	virtual void OnMouseDown(GeoStar::Kernel::GsButton eButton, int nKeyboard, const GeoStar::Utility::GsPT& pt);
	/// \brief ������굯����Ϣ
	virtual void OnMouseUp(GeoStar::Kernel::GsButton eButton, int nKeyboard, const GeoStar::Utility::GsPT& pt);
	/// \brief ��������ƶ���Ϣ
	virtual void OnMouseMove(GeoStar::Kernel::GsButton eButton, int nKeyboard, const GeoStar::Utility::GsPT& pt);
	/// \brief ������������Ϣ
	virtual void OnMouseWheel(GeoStar::Kernel::GsButton eButton, int nDelta, int nKeyboard, const GeoStar::Utility::GsPT& pt);

};

/// \brief GsSymbolTrackerPtr
GS_SMARTER_PTR(PanTracker);
