GIS内核-自动标注输出计算的标注位置

```c++

//通过绘制缓存计算标注的位置
GS_TEST(AutoLabel, GetPlacedLabelPosByMap, chijing, 20200818)
{
	this->RecordProperty("readme", "加载线数据， 设置多标注,多表查询“兰新线”");

	GeoStar::Kernel::GsSqliteGeoDatabaseFactory vFac;
	GeoStar::Kernel::GsConnectProperty vConn;
	vConn.DataSourceType = eSqliteFile;
	vConn.Server = "../testdata/400sqlite";
	GeoStar::Kernel::GsGeoDatabasePtr ptrGDB = vFac.Open(vConn);
	GsFeatureClassPtr  ptrFeatureClass = ptrGDB->OpenFeatureClass("RAI_4M_L");

	GsFeatureLayerPtr	ptrFeatureLayer = new GsFeatureLayer(ptrFeatureClass);

	GsLineLabelPropertyPtr ptrLineLabelPro = new GsLineLabelProperty();
	ptrLineLabelPro->LabelField("NAME"); //加载的标注的字段名
	GsTextSymbolPtr textSymbol1 = new GsTextSymbol();
	ptrLineLabelPro->Symbol(textSymbol1);
	ptrLineLabelPro->Symbol()->Width(3.5);
	ptrLineLabelPro->Symbol()->Height(3.5);
	GsColor color1 = GsColor(255, 0, 0, 255);
	textSymbol1->Color(color1);
	//ptrLineLabelPro->UnionLabelField("NAME");
	ptrLineLabelPro->MultiLabel(true);

	ptrFeatureLayer->Renderer()->RenditionMode(eMultiRendition);
	GsFeatureRenditionPtr ptrFeatureRendition1 = new GsLabelRendition(ptrLineLabelPro);
	ptrFeatureRendition1->WhereClause(GsUtf8("name='兰新线'"));//多表查询
	ptrFeatureLayer->Renderer()->AddRendition(ptrFeatureRendition1);

	GsMapPtr ptrMap;
	GsDisplayPtr m_ptrDisplay;

	GsRect deviceExtent(0, 0, 1136, 749);
	GsMemoryImageCanvasPtr pImgCanvas = new GsMemoryImageCanvas(deviceExtent.Width(), deviceExtent.Height());/// \brief 根据宽和高构建RGBA32位色的画布
	GsDisplayTransformationPtr pDT = new GsDisplayTransformation(ptrFeatureLayer->Extent(), deviceExtent);
	m_ptrDisplay = new GsDisplay(pImgCanvas, pDT);
	ptrMap = new GsMap(NULL);
	ptrMap->Layers()->push_back(ptrFeatureLayer);
	ptrMap->Output(m_ptrDisplay, NULL);

	std::string strOutPut = this->MakeInputFile("AutoLable", "line_WhereClause", "PNG");
	ptrMap->Output(m_ptrDisplay, NULL);

	GsLabelContainerPtr ptrLabelContin = m_ptrDisplay->LabelContainer();
	auto ptrLabels = ptrLabelContin->PlacedLabel();
	GsGeometryPtr ptrPathGeo = 0;
	GsSimplePointSymbolPtr ptrSymbol = new GsSimplePointSymbol(GsColor::Blue, 2);
	GsPointPtr point = new GsPoint(0., 0.);
	ptrSymbol->StartDrawing(m_ptrDisplay->Canvas(), m_ptrDisplay->DisplayTransformation());

    //遍历缓存拿到标注对象, 查询标注对象的集合对象并绘制成连续的点
	for  (auto var =  ptrLabels->begin(); var != ptrLabels->end(); var++)
	{
		ptrPathGeo = (*var)->QueryDrawLabelPath(m_ptrDisplay->DisplayTransformation(), m_ptrDisplay->Canvas());
		ptrSymbol->Draw(ptrPathGeo);
	}
	ptrSymbol->EndDrawing();
	pImgCanvas->Image()->SavePNG(strOutPut.c_str());
}
```

