#pragma once

#include "MapInstance.h"

class CLocalMapInstance : public CMapInstance
{
public:
	CLocalMapInstance(GsPyramid* pPyramid, const CWnd& hWnd);
	CLocalMapInstance(const CLocalMapInstance& other, const CWnd& hWnd);
	virtual ~CLocalMapInstance();

	virtual bool Init(const char * format, GsSpatialReference* spatial = NULL);
	virtual void DrawTextFeature() override;
	virtual void DrawSymbolForTest() override;
private:
};
