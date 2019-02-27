#pragma once

#include "GeoHeader.h"
#include "memory"
using namespace std;

class CMapInstance {
public:
	CMapInstance(GsPyramid* pPyramid, const CWnd& hWnd);
	CMapInstance(const CMapInstance& other, const CWnd& hWnd);
	virtual ~CMapInstance();

	virtual bool Init(const char * format, GsSpatialReference* spatial = NULL) = 0;

	enum ScaleType {
		ZOOM_in,
		ZOOM_out
	};
	void Desplay();
	void SetScale(ScaleType type);
	void PanStart(CPoint& point);
	void PanMoveTo(CPoint& point);
	void PanStop();
	void Cancel();
	virtual void DrawTextFeature();
	virtual void DrawSymbolForTest();

protected:
	float GetDPI();
	bool OnNeedUpdate(GsRefObject* ptrUpdateAgent, int, const GsBox& box, double, void* ptrUserParam);
	GsScreenDisplay* CreateScreenDisplay(GsSpatialReference* ptrDataRoom, GsBox& box);
	virtual GsPaintDevice* BindDevice(GsScreenDisplay* pScreenDis);

protected:
	GsMapPtr			m_pMap;
	GsPyramidPtr		m_pPyramid;
	const CWnd&			m_hShowWnd;
};

using CMapInstancePtr = shared_ptr<CMapInstance>;