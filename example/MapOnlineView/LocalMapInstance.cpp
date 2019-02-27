#include "stdafx.h"

#include "LocalMapInstance.h"

CLocalMapInstance::CLocalMapInstance(GsPyramid* pPyramid, const CWnd& hWnd)
	: CMapInstance(pPyramid, hWnd)
{

}

CLocalMapInstance::~CLocalMapInstance()
{

}

CLocalMapInstance::CLocalMapInstance(const CLocalMapInstance& oter, const CWnd& hWnd)
	: CMapInstance(oter, hWnd)
{

}

bool CLocalMapInstance::Init(const char * format, GsSpatialReference* spatial)
{
	do {
		GsSqliteGeoDatabaseFactoryPtr webFactory = CreateClassGsSqliteGeoDatabaseFactory();
		if (!webFactory)
			break;
		GsConnectProperty connect;
		connect.DataSourceType = eSqliteFile;
		connect.Server = "../data/";
		GsGeoDatabasePtr database = webFactory->Open(connect);
		if (!database)
			break;
		GsPyramidPtr py = GsPyramid::WellknownPyramid(e360DegreePyramid);
		GsFeatureClassPtr feature = database->OpenFeatureClass("CHN_ADM3");
		if (!feature)
			break;

		GsBox mapBox;
		mapBox.XMin = py->FromX;
		mapBox.XMax = py->ToX;
		mapBox.YMin = py->ToY;
		mapBox.YMax = py->FromY;

		GsScreenDisplayPtr pScreenDis = CreateScreenDisplay(feature->SpatialReference(), mapBox);
		if (!pScreenDis)
			break;
		//pScreenDis->BindDevice(GsPaintDevice::CreatePaintDevice(eWin32HwndDeviceD2D, m_hShowWnd));
		BindDevice(pScreenDis);

		GsFeatureLayerPtr pFeatureLayer = new GsFeatureLayer(feature);
		if (!pFeatureLayer)
			break;
		m_pMap = new GsMap(pScreenDis);
		if (!m_pMap)
			break;
		m_pMap->Layers()->push_back(pFeatureLayer);

		GsBox& box = m_pMap->FullExtent();
		m_pMap->ViewExtent(box);
		return true;
	} while (0);
	return false;
}

void CLocalMapInstance::DrawTextFeature()
{
	GsFeatureLayerPtr pLayer = m_pMap->Layers()->at(0);
	m_pMap->ScreenDisplay()->LabelContainer()->AutoLabel(false);
	GsGeometryType geoType = pLayer->FeatureClass()->GeometryType();
	GsTextSymbolPtr ptrTxtSymbol = GsTextSymbolPtr(new GsTextSymbol());
	GsLabelRenditionPtr ptrCurrentLabelRendtion;
	float fLabelSize = 10;//标注字体大小
	static int nCount = 4;
	auto& fields = pLayer->FeatureClass()->Fields().Fields;
	const char* strFieldName = fields[nCount].Name.c_str();
	if (geoType == eGeometryTypePoint)//点标注
	{
		GsPointLabelPropertyPtr ptrPointLabelProp = GsPointLabelPropertyPtr(new GsPointLabelProperty());
		ptrPointLabelProp->LabelField(strFieldName);
		ptrPointLabelProp->PointSpaceByLabel(3);
		ptrPointLabelProp->Symbol(ptrTxtSymbol);
		ptrPointLabelProp->Symbol()->Size(fLabelSize / 2.500);
		//由于底层多标注避让有待完善，暂时采用此法避免标注重叠
		if (nCount - 4 == 0)
			ptrPointLabelProp->PlaceOrder(eBottom, eHighPriority);
		else if (nCount - 4 == 1)
			ptrPointLabelProp->PlaceOrder(eRight, eHighPriority);
		else if (nCount - 4 == 2)
			ptrPointLabelProp->PlaceOrder(eTop, eHighPriority);
		else if (nCount - 4 == 3)
			ptrPointLabelProp->PlaceOrder(eLeft, eHighPriority);
		else if (nCount - 4 == 4)
			ptrPointLabelProp->PlaceOrder(eTopLeft, eHighPriority);
		else if (nCount - 4 == 5)
			ptrPointLabelProp->PlaceOrder(eTopRight, eHighPriority);
		else if (nCount - 4 == 6)
			ptrPointLabelProp->PlaceOrder(eBottomLeft, eHighPriority);
		else
			ptrPointLabelProp->PlaceOrder(eBottomRight, eHighPriority);
		ptrCurrentLabelRendtion = new GsLabelRendition(ptrPointLabelProp);//标注渲染器
		ptrCurrentLabelRendtion->Name(strFieldName);//设置标注类名称
		ptrCurrentLabelRendtion->Visible(true);
		nCount++;
		//return m_ptrCurrentLabelRendtion;
	}
	else if (geoType == eGeometryTypePolyline)//线标注
	{
		GsLineLabelPropertyPtr ptrLineLabelProp = GsLineLabelPropertyPtr(new GsLineLabelProperty());
		ptrLineLabelProp->LabelField(strFieldName);
		GsTextSymbolPtr ptrTxtSymbol = GsTextSymbolPtr(new GsTextSymbol());
		ptrLineLabelProp->Symbol(ptrTxtSymbol);
		ptrLineLabelProp->Symbol()->Size(fLabelSize / 2.500);
		ptrLineLabelProp->LineLabelType(eAlongLine);
		if (nCount - 4 == 0)
			ptrLineLabelProp->LabelPos(eTopLine);
		else if (nCount - 4 == 1)
			ptrLineLabelProp->LabelPos(eBottomLine);
		else if (nCount - 4 == 2)
			ptrLineLabelProp->LabelPos(eCenterLine);
		else
			ptrLineLabelProp->LabelPos(eAutoPlace);
		ptrCurrentLabelRendtion = new GsLabelRendition(ptrLineLabelProp);
		ptrCurrentLabelRendtion->Name(strFieldName);
		ptrCurrentLabelRendtion->Visible(true);
		nCount++;
		//return m_ptrCurrentLabelRendtion;
	}
	else if (geoType == eGeometryTypePolygon)//面标注
	{
		GsSurfaceLabelPropertyPtr ptrSurfaceLabelProp = GsSurfaceLabelPropertyPtr(new GsSurfaceLabelProperty());
		ptrSurfaceLabelProp->LabelField(strFieldName);
		GsTextSymbolPtr ptrTxtSymbol = GsTextSymbolPtr(new GsTextSymbol());
		ptrSurfaceLabelProp->Symbol(ptrTxtSymbol);
		ptrSurfaceLabelProp->Symbol()->Size(fLabelSize / 2.500);
		ptrSurfaceLabelProp->SurfaceLabelType(eNormal);
		if (nCount - 4 == 0)
			ptrSurfaceLabelProp->LabelPos(eTopLine);
		else if (nCount - 4 == 1)
			ptrSurfaceLabelProp->LabelPos(eBottomLine);
		else if (nCount - 4 == 2)
			ptrSurfaceLabelProp->LabelPos(eCenterLine);
		else
			ptrSurfaceLabelProp->LabelPos(eAutoPlace);
		ptrCurrentLabelRendtion = new GsLabelRendition(ptrSurfaceLabelProp);
		ptrCurrentLabelRendtion->Name(strFieldName);
		ptrCurrentLabelRendtion->Visible(true);
		nCount++;
		//return m_ptrCurrentLabelRendtion;
	}
	pLayer->Renderer()->RenditionMode(eMultiRendition);
	pLayer->Renderer()->AddRendition(ptrCurrentLabelRendtion);
}

void CLocalMapInstance::DrawSymbolForTest()
{
	GsFeatureLayerPtr pLayer = m_pMap->Layers()->at(0);
	{
		GsSimpleFeatureRendererPtr ptrSimpleRender = pLayer->Renderer()->Clone();
		GsSymbolPtr ptrSymbol;
		{
			GeoStar::Kernel::GsSymbolLibrary lib("F:\\08_Source\\data\\symbol\\junbiao.symx", true);
			ptrSymbol = lib.SymbolByCode(220214, GsSymbolType::eLineSymbol);
		}
		if (GsMultiPointSymbolPtr ptrMulti = ptrSymbol)
		{
			GsColor c = ptrMulti->Color();
			ptrMulti->Color(c);
		}
		if (GsLineSymbolPtr ptrLine = ptrSymbol)
		{
		}
		ptrSimpleRender->Symbol(ptrSymbol);
		pLayer->Renderer(ptrSimpleRender);
		return;
	}
}
