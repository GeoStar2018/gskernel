GIS内核-栅格数据映射修改与边界提取

```c++
// 栅格映射

// 栅格映射
GS_TEST(GsRasterDataMapping, DataMapping_AND_BoundaryBuilder, chijing, 20201222)
{
	
	GsRasterColumnInfo info;
	double cellsize, xMin, yMin, nodata;

	const char* folder = MakeInputFolder("../testdata/rasteranalysis");

	GeoStar::Kernel::GsFileGeoDatabaseFactory vFac;
	GeoStar::Kernel::GsConnectProperty vConn(folder);
	GeoStar::Kernel::GsGeoDatabasePtr ptrDB = vFac.Open(vConn);
	GsRasterClassPtr ptrRasterClass = ptrDB->OpenRasterClass("slope0.tif");

	GsRect rect(0, 0, ptrRasterClass->Width(), ptrRasterClass->Height());
	GsRasterCursorPtr ptrCursor = ptrRasterClass->Search(rect);

	GsSpatialReferencePtr ptrSR = ptrRasterClass->SpatialReference();
	info = ptrRasterClass->RasterColumnInfo();
	nodata = ptrRasterClass->RasterBand(0)->NoDataValue();

	GsRasterClassPtr pClass = ptrDB->CreateRasterClass("output_DataMapping_BoundaryBuilder.tif", eGTiff, info, ptrSR);
	pClass->MetadataItem("KernelCustom", "RasterBand/Nodata", GsStringHelp::ToString(nodata));

	GsRasterDataMapping rasterDataMapping;
	rasterDataMapping.OriginalValues()->push_back(0);
	rasterDataMapping.OriginalValues()->push_back(25.1);
	rasterDataMapping.OriginalValues()->push_back(90);
	rasterDataMapping.MappingValues()->push_back(25);
	rasterDataMapping.MappingValues()->push_back(1000);

	GsRasterBoundaryBuilder BoundaryBuilder(info, 25);
	BoundaryBuilder.BeginBuild();
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
		rasterDataMapping.Mapping(&block, info.DataType);
		pClass->WriteRaster(&block);
		GsGeometryPtr geotmp = BoundaryBuilder.BuildOneRaster(&block);
		bNext = ptrCursor->Next(&block);
	}
		GsGeometryPtr ptrGeo = BoundaryBuilder.EndBuild();
	{
		GsConnectProperty conn;
		conn.DataSourceType = eSqliteFile;
		conn.Server = MakeInputFolder("../testdata/rasteranalysis");
		GsSqliteGeoDatabaseFactory obj;
		GsGeoDatabasePtr ptrGDB = obj.Open(conn);


		GsGeometryColumnInfo geoinfo;
		geoinfo.FeatureType = GsFeatureType::eSimpleFeature;
		geoinfo.GeometryType = GsGeometryType::eGeometryTypePolygon;
		GsFeatureClassPtr ptrFeaClass = ptrGDB->CreateFeatureClass("output_DataMapping_BoundaryBuilder_one",
			GsFields(), geoinfo,
			ptrSR);


		GsFeaturePtr ptrTarget = ptrFeaClass->CreateFeature();

		ptrFeaClass->Transaction()->StartTransaction();
		GsGeometryCollectionPtr pColl = ptrGeo;
		//for (int i = 0; i < pColl->Count(); i++)
		{
			ptrTarget->OID(-1);
			//ptrTarget->Geometry(pColl->Geometry(i));
			ptrTarget->Geometry(ptrGeo);
			ptrTarget->Store();
		}
		ptrFeaClass->Transaction()->CommitTransaction();
		ptrFeaClass->CreateSpatialIndex();
	}

}



```

