GIS内核-创建栅格数据集	
	
	void CopyRasterClass(GsGeoDatabase* ptrDb, GsGeoDatabase* ptrDbdst)
	{
		GsRasterClassPtr ptrRassSrc = ptrDb->OpenRasterClass("8bit");
	
		GsRasterColumnInfo info = ptrRassSrc->RasterColumnInfo();
		GsSpatialReferencePtr ptrSpatial = ptrRassSrc->SpatialReference();
		GsString rasterDirverFormat =  ptrRassSrc->Format();
	
		if (rasterDirverFormat == "GTIFF")
		{
			//根据源创建一个tif文件
			GsRasterClassPtr ptrRassDst = ptrDbdst->CreateRasterClass("8sd", eGTiff, info, ptrSpatial);
			if (!ptrRassDst)
			{
				GS_I << " not support";
				return;
			}
			GsRasterCursorPtr ptrCursor = ptrRassSrc->Search(ptrRassSrc->ExtentToRange(ptrRassSrc->Extent()));
			GsRasterPtr pRaster = new GsRaster();
	
			while (ptrCursor->Next(pRaster))
			{
				ptrRassDst->WriteRaster(pRaster);
			}
	
			//创建金字塔
			bool bExists = ptrRassDst->ExistsPyramid();
			bool bCreateSuccess = ptrRassDst->CreatePyramid(GsRasterResampleAlg::eAverage, 4);
		}
		else if (rasterDirverFormat == "VRT")
		{
			//根据镶嵌数据集创建另一个镶嵌数据集
			GsRasterClassPtr ptrRassDst = ptrDbdst->CreateRasterClass("8sd", eVRT, info, ptrSpatial);
			if (!ptrRassDst)
			{
				GS_I << " not support";
				return;
			}
			GsMosaicRasterManagerPtr manger = ptrRassSrc->ExtensionData();
			GsMosaicRasterManagerPtr mangerdst = ptrRassDst->ExtensionData();
	
			GsVector< Utility::GsSmarterPtr<GsRasterClass>> rastrs = manger->RasterClasses();
			for(auto var = rastrs.begin(); var != rastrs.end(); var++)
				mangerdst->AddRaster(*var);
		}
	}