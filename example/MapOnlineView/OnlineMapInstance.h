#pragma once

#include "MapInstance.h"

class COnlineMapInstance : public CMapInstance
{
public:
	COnlineMapInstance(GsPyramid* pPyramid, const CWnd& hWnd);
	virtual ~COnlineMapInstance();

	virtual bool Init(const char * format, GsSpatialReference* spatial = NULL);

private:
};

