
```c
void clip_raster_2_smalltif(GsFeatureClass*ptrFcs, GsRasterClass* ptrRS)
{
	GsFileGeoDatabaseFactoryPtr ptrFac = new GsFileGeoDatabaseFactory();
	GsGeoDatabasePtr pGeo = ptrFac->Open(g_params.tifpath.c_str());
	GsFeatureCursorPtr ptrcursor = ptrFcs->Search();
	long long nCount = 0;
	GsFeaturePtr ptrFea = ptrcursor->Next();
	do
	{
		if (!ptrFea)
			break;
		GsGeometryPtr pGeo = ptrFea->Geometry();
		GsBox box(pGeo->Envelope().XMin, pGeo->Envelope().YMin, pGeo->Envelope().XMax, pGeo->Envelope().YMax);
		GsRect rt = ptrRS->ExtentToRange(box);
		GsRasterCursorPtr ptrCursor = ptrRS->Search(rt);
		GsRasterPtr pRaster = new GsRaster();
		bool hasNodata = ptrRS->RasterBand(1)->HasNoDataValue();
		double nodata = ptrRS->RasterBand(1)->NoDataValue();
		auto dttype = ptrRS->RasterColumnInfo().DataType;
		GsEnvelopePtr ptrEnv = new GsEnvelope(box);
		GsGeometryPtr pGeodiff = ptrEnv->Difference(pGeo);
		GsRasterColumnInfo infotmp = ptrRS->RasterColumnInfo();
		infotmp.Width = rt.Width();
		infotmp.Height = rt.Height();
		infotmp.GeoTransform[0] = box.XMin;
		infotmp.GeoTransform[3] = box.YMax;
		infotmp.GeoTransform[1] = box.Width() / rt.Width();
		infotmp.GeoTransform[5] = -box.Height() / rt.Height();
		infotmp.XYDomain = box;
		std::ostringstream oss;
		oss << ptrFea->OID() << ".tif";
		GsFileGeoDatabaseFactoryPtr ptrFac = new GsFileGeoDatabaseFactory();
		GsGeoDatabasePtr pGeoDB = ptrFac->Open(g_params.tifpath.c_str());
		GsRasterClassPtr pClass = pGeoDB->CreateRasterClass(oss.str().c_str(), eGTiff, infotmp, ptrRS->SpatialReference());
		pClass->MetadataItem("KernelCustom", "RasterBand/Nodata", GsStringHelp::ToString(nodata));

		GsRasterClipPtr ptrRasterClip = new GsRasterClip(ptrRS->RasterColumnInfo(), pGeodiff, nodata);
		int fx = 0, fy = 0; bool bfirst = true;


		while (ptrCursor->Next(pRaster))
		{
			if (bfirst)
			{
				fx = pRaster->OffsetX(); fy = pRaster->OffsetY();
				bfirst = false;
			}
			ptrRasterClip->Clip(pRaster);
			int tmpfx = pRaster->OffsetX(); int tmpfy = pRaster->OffsetY();
			pRaster->OffsetX(pRaster->OffsetX() - fx);
			pRaster->OffsetY(pRaster->OffsetY() - fy);
			pClass->WriteRaster(pRaster);
			pRaster->OffsetX(tmpfx);
			pRaster->OffsetY(tmpfy);
		};

	} while (ptrcursor->Next(ptrFea));
}
```


