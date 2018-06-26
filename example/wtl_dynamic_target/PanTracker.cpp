#include "stdafx.h"
#include "PanTracker.h"
using namespace GeoStar::Kernel;
using namespace GeoStar::Utility;

PanTracker::PanTracker(GeoStar::Kernel::GsMap* map, CWindow* hWnd):GsSymbolTracker(map)
{
	m_bIsPan = false;
	m_hWnd = hWnd;

}

PanTracker::~PanTracker()
{
}
/// \brief 接收鼠标按下消息
void PanTracker::OnMouseDown(GsButton eButton, int nKeyboard, const GsPT& pt)
{
	m_bIsPan = true;

	GsRawPoint map = this->m_ptrDT->ToMap(pt);

	View()->ScreenDisplay()->PanStart(map.X,map.Y);

}
/// \brief 接收鼠标弹起消息
void PanTracker::OnMouseUp(GsButton eButton, int nKeyboard, const GsPT& pt)
{
	if (!m_bIsPan)
		return;
	GsBox box = View()->ScreenDisplay()->PanStop();
	m_ptrDT->MapExtent(box);
	View()->Invalidate();
	m_hWnd->Invalidate(FALSE);
	m_bIsPan = false;
}
/// \brief 接收鼠标移动消息
void PanTracker::OnMouseMove(GsButton eButton, int nKeyboard, const GsPT& pt)
{
	if (!m_bIsPan)
		return;
	GsRawPoint map = this->m_ptrDT->ToMap(pt);
	View()->ScreenDisplay()->PanMoveTo(map.X, map.Y);


}
/// \brief 接收鼠标滚轮消息
void PanTracker::OnMouseWheel(GsButton eButton, int nDelta, int nKeyboard, const GsPT& pt)
{

	GsRawPoint map = this->m_ptrDT->ToMap(pt);
	GsBox box = m_ptrDT->MapExtent();
	if (nDelta > 0)
		box = box.Scale(map,0.8);
	else
		box = box.Scale(map,1.2);
	m_ptrDT->MapExtent(box);
	View()->Invalidate();
	m_hWnd->Invalidate(FALSE);
}

