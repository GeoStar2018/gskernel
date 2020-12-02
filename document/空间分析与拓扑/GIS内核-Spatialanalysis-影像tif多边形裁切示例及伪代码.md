# 栅格数据影像tif裁切支持多边形裁切示例，测试用例 #


```c++

GS_TEST(GsRasterClip, Clip, wuyongbo, 20201201)
{
	GsRasterColumnInfo info;
	double cellsize, xMin, yMin, nodata;

	const char* folder = MakeInputFolder("../toolbox/sourcecode/data/terraindata");

	GeoStar::Kernel::GsFileGeoDatabaseFactory vFac;
	GeoStar::Kernel::GsConnectProperty vConn(folder);
	GeoStar::Kernel::GsGeoDatabasePtr ptrDB = vFac.Open(vConn);
	GsRasterClassPtr ptrRasterClass = ptrDB->OpenRasterClass("world.tif");

	GsRect rect(0, 0, ptrRasterClass->Width(), ptrRasterClass->Height());
	GsRasterCursorPtr ptrCursor = ptrRasterClass->Search(rect);

	GsSpatialReferencePtr ptrSR = ptrRasterClass->SpatialReference();
	info = ptrRasterClass->RasterColumnInfo();
	nodata = ptrRasterClass->RasterBand(0)->NoDataValue();
	if (1)
	{
		GsString strInput = this->MakeOutputFolder("tifoutput/rasterclip");
		GsDir dir(strInput.c_str());
		if (dir.Exists())
			dir.Delete();
		dir.Create();
		GeoStar::Kernel::GsConnectProperty vConn(strInput);
		GeoStar::Kernel::GsGeoDatabasePtr ptrDB = GsFileGeoDatabaseFactory().Open(vConn);

		GsRasterClassPtr pClass = ptrDB->CreateRasterClass("output.tif", eGTiff, info, ptrSR);
		pClass->MetadataItem("KernelCustom", "RasterBand/Nodata", GsStringHelp::ToString(nodata));

		GsRingPtr ptrGeoClip = new GsRing();
		{
			ptrGeoClip->Add(GsRawPoint(-50, -50));
			ptrGeoClip->Add(GsRawPoint(50, 25));
			ptrGeoClip->Add(GsRawPoint(50, 50));
			ptrGeoClip->Add(GsRawPoint(25, 60));
			ptrGeoClip->Add(GsRawPoint(-50, 30));
			ptrGeoClip->Add(GsRawPoint(-50, -50));
		}

		GsRasterClip rasterClip(info, ptrGeoClip, nodata);

		GsRaster block;
		bool bNext = ptrCursor->Next(&block);
		while (bNext)
		{
			int ddd = block.DataLength();
			int w = block.Width();
			int h = block.Height();
			int x = block.OffsetX();
			int y = block.OffsetY();
			unsigned char* pBlock = block.DataPtr();


			rasterClip.Clip(&block);

			pClass->WriteRaster(&block);

			bNext = ptrCursor->Next(&block);
		}
	}
}

```