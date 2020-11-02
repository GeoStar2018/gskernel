GIS内核-接入ArcGIS 的 MapServer 和ImageServer

```c++
//下面代码演示如何打开一个ArcGIS的 服务并创建未内核的GsTileClass对象
GsGeoDatabasePtr ConnectMapServer(const char* webUrl)
{
	GsConnectProperty conn;
	conn.DataSourceType = GeoStar::Kernel::eWeb;
	GeoStar::Kernel::GsGeoDatabaseFactoryPtr fac = OpenGeoDatabaseFactory("WEB");
	GsGeoDatabasePtr  pDB = fac->Open(conn);
	//webUrl = "http://172.15.110.3:6080/arcgis/rest/services/2000/MapServer";//wms
	//"http://172.15.110.3:6080/arcgis/rest/services/2K/MapServer";wmts
	GsArcGISRestServerUriParserPtr ptrUriParser = new GsArcGISRestServerUriParser(webUrl);// conn.Server);
	GsWebUriParserPtr webparser = ptrUriParser;
	bool bok = ptrUriParser->ParseCapability();
	GsPyramidPtr ptrPy = ptrUriParser->Pyramid();// GsPyramid::WellknownPyramid(eWebMercatorPyramid));
	GsString str = ptrUriParser->FormatUri();

	GsTileColumnInfo info;
	info.FeatureType = ePrevectorTileFeature;
	info.ValidBottomLevel = ptrUriParser->BottomLevel();
	info.ValidTopLevel = ptrUriParser->TopLevel();
	info.XYDomain =  GsBox(-180, -90, 180, 90);

	GsTileClassPtr Raster = pDB->CreateTileClass(ptrUriParser->LayerName().c_str(), ptrUriParser->SpatialReference(), ptrUriParser->Pyramid(), info);
	GsTMSTileClassPtr tms = Raster;
	tms->UrlTemplate(ptrUriParser->FormatUri());
	tms->TileType(ePngType);

	GsTMSParameterPtr ptrTMS = dynamic_cast<GsTMSParameter*>(tms->ExtensionData());
	if (!ptrTMS)
		return 0;

	//这里是缓存,浏览过的瓦片会缓存到此数据集  
	GsGeoDatabaseFactoryPtr cacheFac = new GsSqliteGeoDatabaseFactory();
	GsConnectProperty cacheConn;
	cacheConn.DataSourceType = GsDataSourceType::eSqliteFile;
	cacheConn.Server = "D:\\";
	GsGeoDatabasePtr ptrDB = cacheFac->Open(cacheConn);
	GsTileClassPtr ptrCacheTs = ptrDB->CreateTileClass(GsMath::NewGUID(), new  GsSpatialReference(4326), GsPyramid::WellknownPyramid(e360DegreePyramid), info);
	//tms->Cache(ptrCacheTs);
	return pDB;
}
```

