GIS内核-单独绘制一张矢量瓦片
    GsGrowByteBuffer buff;
	GsString pbffile = MakeInputFile("vectortile", "12-1264-6764", ".pbf");
	GsFile g(pbffile);
	g.ReadAllBytes(&buff);
	std::string strStyleFile = MakeInputFile("vectortile", "xz", "stylez");

	GsStyleTableFactory fac;
	GsStyleTablePtr ptrTale = fac.OpenFromZip(strStyleFile.c_str());

	GsPyramidPtr ptrPyramid = new GsMultiPyramid();
	GsStyledVectorTileRenderer ptrRender(256, 256, ptrPyramid);
	ptrRender.StyleTable(ptrTale);

	ptrRender.DrawVectorTile(buff.BufferHead(), buff.BufferSize(), 12, 1264, 6764);
	ptrRender.ImageCanvas()->Image()->SavePNG(MakeOutputFile("VectorTile", this->TestName(), "png"));