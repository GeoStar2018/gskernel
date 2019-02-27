#include "stdafx.h"
#include "Pagelayout.h"


CPageLayout::CPageLayout(GsPyramid* pPyramid, const CWnd& hWnd)
	: CLocalMapInstance(pPyramid, hWnd)
{
}


CPageLayout::~CPageLayout()
{
}

CPageLayout::CPageLayout(const CLocalMapInstance& other, const CWnd& hWnd)
	: CLocalMapInstance(other, hWnd)
{
	GsPyramidPtr py = GsPyramid::WellknownPyramid(e360DegreePyramid);
	GsBox mapBox;
	mapBox.XMin = py->FromX;
	mapBox.XMax = py->ToX;
	mapBox.YMin = py->ToY;
	mapBox.YMax = py->FromY;
	GsScreenDisplayPtr pScreenDis = CreateScreenDisplay(m_pMap->Layers()->at(0)->SpatialReference(), mapBox);
	BindDevice(pScreenDis);

	m_ptrPageLayout = new GsPageLayout(pScreenDis);
	m_ptrPageLayout->ElementContainer()->Add(new GsMapElement(m_pMap));
	m_ptrPageLayout->FullExtent();
	m_ptrPageLayout->Update();
}

bool CPageLayout::Init(const char * format)
{
	if (m_ptrPageLayout)
		return false;
	if (!CLocalMapInstance::Init(format))
		return false;

	GsPyramidPtr py = GsPyramid::WellknownPyramid(e360DegreePyramid);
	GsBox mapBox;
	mapBox.XMin = py->FromX;
	mapBox.XMax = py->ToX;
	mapBox.YMin = py->ToY;
	mapBox.YMax = py->FromY;
	GsScreenDisplayPtr pScreenDis = CreateScreenDisplay(m_pMap->Layers()->at(0)->SpatialReference(), mapBox);
	BindDevice(pScreenDis);

	m_ptrPageLayout = new GsPageLayout(pScreenDis);
	m_ptrPageLayout->ElementContainer()->Add(new GsMapElement(m_pMap));
	m_ptrPageLayout->FullExtent();

	return true;
}

void CPageLayout::Desplay()
{
	m_pMap->Cancel();
	m_ptrPageLayout->Update();
}
