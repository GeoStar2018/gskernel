GIS内核-矢量数据转栅格数据

```C++

GS_TEST(GsPolygonToRaster, GsPolygonToRaster, chijing, 20201222)
{
	GsConnectProperty conn;
	conn.DataSourceType = eSqliteFile;
	conn.Server = MakeInputFolder("../testdata/rasteranalysis");
	GsSqliteGeoDatabaseFactory obj;
	GsGeoDatabasePtr ptrGDB = obj.Open(conn);
	GsFeatureClassPtr ptrFeaClass = ptrGDB->OpenFeatureClass("GsRasterToPolygon");

	GsRasterColumnInfo info;
	info.XYDomain = GsBox(105.8599523112, 38.5533420613 , 105.9256254350, 38.6061660956);
	info.Width = 237;
	info.Height = 191;
	info.DataType = GsRasterDataType::eFloat32RDT;
	info.BlockHeight = 237;
	info.BlockHeight = 191;
	info.FeatureType = GsFeatureType::eRasterFeature;
	info.BandTypes.push_back(GsRasterBandType::eGrayIndexBand);
	GsFileGeoDatabaseFactory ptrDBFile;
	GsGeoDatabasePtr  ptrFileDB =  ptrDBFile.Open(conn);
	GsRasterClassPtr pRasterClass = ptrFileDB->CreateRasterClass("GsPolygonToRaster.tif", eGTiff, info, ptrFeaClass->SpatialReference());
	pRasterClass->MetadataItem("KernelCustom", "RasterBand/Nodata", GsStringHelp::ToString(-1));
	GsGeometryToRaster Geo2Raster(info,-1);

	GsFeatureCursorPtr ptrCursor =  ptrFeaClass->Search();
	GsFeaturePtr pFea = ptrCursor->Next();
	Geo2Raster.BeginConvert();
	do
	{
		if (!pFea)
			break;
		double value = pFea->ValueDouble(2);
		Geo2Raster.ConvertOneGeometry(pFea->Geometry(), value);
	} while (ptrCursor->Next(pFea));
	GsRaster* pRetturn =  Geo2Raster.EndConvert();

	pRasterClass->WriteRaster(pRetturn);

	if(pRasterClass->Transaction())
		pRasterClass->Transaction()->CommitTransaction();
}

```

