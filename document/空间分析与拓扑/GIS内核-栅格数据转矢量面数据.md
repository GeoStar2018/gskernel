GIS内核-栅格数据转矢量面数据

```C++
// 栅格转矢量,生成的是拆撒的面, 且带属性,可支持过滤
GS_TEST(GsRasterToPolygon, GsRasterToPolygon, chijing, 20201222)
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

	GsRasterClassPtr pClass = ptrDB->CreateRasterClass("GsRasterToPolygon.tif", eGTiff, info, ptrSR);
	pClass->MetadataItem("KernelCustom", "RasterBand/Nodata", GsStringHelp::ToString(nodata));

	GsRasterDataMapping rasterDataMapping;
	rasterDataMapping.OriginalValues()->push_back(0);
	rasterDataMapping.OriginalValues()->push_back(10);
	rasterDataMapping.OriginalValues()->push_back(15);
	rasterDataMapping.OriginalValues()->push_back(25);
	rasterDataMapping.OriginalValues()->push_back(35);
	rasterDataMapping.OriginalValues()->push_back(100);
	rasterDataMapping.MappingValues()->push_back(5);
	rasterDataMapping.MappingValues()->push_back(10);
	rasterDataMapping.MappingValues()->push_back(20);
	rasterDataMapping.MappingValues()->push_back(30);
	rasterDataMapping.MappingValues()->push_back(nodata);
	UTILITY_NAME::GsVector<double> nodatas;
	nodatas.push_back(nodata);
	GsRasterToPolygon pRasterToPolygon(info, nodatas);

	GsRaster block;

	GsConnectProperty conn;
	conn.DataSourceType = eSqliteFile;
	conn.Server = MakeInputFolder("../testdata/rasteranalysis");
	GsSqliteGeoDatabaseFactory obj;
	GsGeoDatabasePtr ptrGDB = obj.Open(conn);


	GsGeometryColumnInfo geoinfo;
	geoinfo.FeatureType = GsFeatureType::eSimpleFeature;
	geoinfo.GeometryType = GsGeometryType::eGeometryTypePolygon;
	GsFields fds;
	fds.Fields.emplace_back("VALUE", GsFieldType::eDoubleType);
	GsFeatureClassPtr ptrFeaClass = ptrGDB->CreateFeatureClass("GsRasterToPolygon",
		fds, geoinfo,		ptrSR);
	FeatureClassWrtier fcsWriter(ptrFeaClass);
	pRasterToPolygon.OutputData(&fcsWriter);

	bool bNext = ptrCursor->Next(&block);
	while (bNext)
	{
		//先做颜色区间映射, 不然颜色取值范围太大,计算耗时较长
		rasterDataMapping.Mapping(&block, info.DataType);
		//写入一个文件做调试查看
		pClass->WriteRaster(&block);
		//转为矢量
		pRasterToPolygon.Convert(&block);
		//取下一块
		bNext = ptrCursor->Next(&block);
	}
}
```

