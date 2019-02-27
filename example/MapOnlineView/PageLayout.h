#pragma once

#include "LocalMapInstance.h"

class CPageLayout : public CLocalMapInstance
{
public:
	CPageLayout(GsPyramid* pPyramid, const CWnd& hWnd);
	CPageLayout(const CLocalMapInstance& other, const CWnd& hWnd);
	~CPageLayout();
	virtual bool Init(const char * format);
	virtual void Desplay();

protected:
	//virtual GsPaintDevice* BindDevice(GsScreenDisplay* pScreenDis) { return nullptr; }

private:
	GsPageLayoutPtr m_ptrPageLayout;
};

