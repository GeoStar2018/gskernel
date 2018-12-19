	//字体生产	
	GsStyleTableFactory fac;
	GsStyleTablePtr pStyleTable = fac.CreateEmptyStyleTable();
	GsGrowByteBuffer refByteBuf;
	//GsDir path(u8"D:\\宋体 常规");
	GsDir path(u8"D:\\微软雅黑 粗体");
	for (int i = 0; i < 65535; i++)
	{
		refByteBuf.Reset();
		int st = i;
		i+= 255;
		//bool bo = pStyleTable->GenerateRasterGlyph(GsUtf8("宋体"), "Regular", 0, st, i, &refByteBuf);
		
		bool bo = pStyleTable->GenerateRasterGlyph(GsUtf8("微软雅黑"), "Blod", 0, st, i, &refByteBuf);

		GsString str = GsStringHelp::Format(255, "%d-%d.pbf", st, i);
		GsString pbdpath =	GsFileSystem::Combine(path.FullPath().c_str(), str.c_str());

		GsFile f(pbdpath.c_str());
		f.WriteAllBytes(refByteBuf.BufferHead(), refByteBuf.BufferSize());

	}


	//样式生产
	GsString tt1 =u8"D:\\source\\kernel\\testdata\\400sqlite\\123.GMAPX";
	GsPyramidPtr pPy = GsPyramid::WellknownPyramid(GsWellknownPyramid::e360DegreePyramid);
	std::string mapFile = tt1;// // fdfdfthis->MakeInputFile("vectortile\\json", "bou2_4m_s", "json");
	GsStyleTablePtr ptrStyle = GsStyleTableFactory().OpenFromMapDefine(mapFile.c_str(), pPy);

	EXPECT_TRUE(!ptrStyle);
	std::string styleOutFile = u8"C:\\Users\\chijing\\Desktop\\asdf\\1234.stylez";// this->MakeOutputFile("vectortile\\json", "bou2_4m_s", "json");
	ptrStyle->SaveJson(styleOutFile.c_str());