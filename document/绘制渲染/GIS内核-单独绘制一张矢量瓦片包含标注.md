GIS内核-单独绘制一张矢量瓦片包含标注



```c++
	GsGrowByteBuffer buff;
	std::string strStyleFile = MakeInputFile("400sqlite", "fff", "stylez");

	GsStyleTableFactory fac;
	GsStyleTablePtr ptrTale = fac.OpenFromZip(strStyleFile.c_str());
	GsCairoMemoryImageCanvasPtr ptrCanvas = new GsCairoMemoryImageCanvas(256, 256);
	GsDisplayTransformationPtr ptrDT = new GsDisplayTransformation(GsBox(0, 0, 256, 256), GsRect(0, 0, 256, 256));
	GsDisplayPtr ptrDplay = new GsDisplay(ptrCanvas, ptrDT);
	ptrDplay->LabelContainer(new GsAdvancedLabelContainer());
	ptrDplay->LabelContainer()->Enabled(true);

	GsPyramidPtr ptrPyramid = new GsMultiPyramid();
	GsStyledVectorTileRendererPtr ptrStyleRender = new GsStyledVectorTileRenderer(ptrCanvas, ptrPyramid, ptrTale);
	if (ptrDplay->LabelContainer()->Enabled())
	{
		ptrStyleRender->DrawBehavior(GsStyledVectorTileRenderer::eDrawVectorAndLabelData);
		ptrStyleRender->LabelContainer(ptrDplay->LabelContainer());
		ptrStyleRender->ScreenDisplayTransformation(ptrDT);
		//开启标注计算
		ptrDplay->LabelContainer()->Begin();
	}
	else
	{
		ptrStyleRender->DrawBehavior(GsStyledVectorTileRenderer::eDrawVectorData);
		ptrStyleRender->LabelContainer(NULL);
		ptrStyleRender->ScreenDisplayTransformation(NULL);
	}


	GsSqliteGeoDatabaseFactoryPtr ptrFac2 = new GsSqliteGeoDatabaseFactory();
	GsConnectProperty conn2;
	conn2.DataSourceType = eSqliteFile;
	conn2.Server = MakeInputFolder("400sqlite/vttest");
	GsGeoDatabasePtr shpdb = ptrFac2->Open(conn2);
	GsTileClassPtr ptrSrcCls = shpdb->OpenTileClass(u8"矢量瓦片绘制.tile");
	GsTilePtr ptrTile = ptrSrcCls->Tile(5, 4, 25);
	ptrStyleRender->DrawVectorTile(ptrTile);
	ptrStyleRender->GenerateLabels();
	//结束标注计算,并绘制
	if (ptrDplay->LabelContainer() && ptrDplay->LabelContainer()->Enabled())
	{
		ptrDplay->LabelContainer()->End();
		ptrDplay->LabelContainer()->DrawLabel(ptrDplay->DisplayTransformation(), ptrCanvas, NULL);
	}

	ptrStyleRender->ImageCanvas()->Image()->SavePNG(MakeOutputFile("VectorTile", this->TestName(), "png"));

```