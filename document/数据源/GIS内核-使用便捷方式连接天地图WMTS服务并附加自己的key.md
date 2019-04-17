# GIS内核-使用便捷方式连接天地图WMTS服务并附加自己的key #

	GsWebGeoDatabaseFactoryPtr ptrFac = new GsWebGeoDatabaseFactory();
	GsGeoDatabaseFactoryPtr fac = ptrFac;
	GsConnectProperty cp;
	GsGeoDatabasePtr db = fac->Open(cp);

	GsWellknownTMSUriParserPtr ptrUriParser = new GsWellknownTMSUriParser(GsWellknownWebTileService::eTiandituVectorGeographicWMTS);
	GsWebUriParserPtr webparser = ptrUriParser;
	webparser->UserParameter().AddPair("tk", "2ce94f67e58faa24beb7cb8a09780552");
	GsString str = ptrUriParser->FormatUri();

	GsTileColumnInfo info;
	info.FeatureType = ePrevectorTileFeature;
	info.ValidBottomLevel = ptrUriParser->BottomLevel();
	info.ValidTopLevel = ptrUriParser->TopLevel();
	info.XYDomain = ptrUriParser->LayerExtent();

	GsTileClassPtr Raster = db->CreateTileClass("img", ptrUriParser->SpatialReference(), ptrUriParser->Pyramid(), info);
	GsTMSTileClassPtr tms = Raster; 
	tms->UrlTemplate(ptrUriParser->FormatUri());
	tms->TileType(ePngType);
	//这里是缓存,浏览过的瓦片会缓存到此数据集  
	GsGeoDatabaseFactoryPtr cacheFac = new GsSqliteGeoDatabaseFactory();
	GsConnectProperty cacheConn;
	cacheConn.DataSourceType = GsDataSourceType::eSqliteFile;
	cacheConn.Server = "D:\\";
	GsGeoDatabasePtr ptrDB = cacheFac->Open(cacheConn);
	GsTileClassPtr ptrCacheTs = ptrDB->CreateTileClass("imgCache", new  GsSpatialReference(4326), GsPyramid::WellknownPyramid(e360DegreePyramid), info);
	tms->Cache(ptrCacheTs);
	//加载图层到视图  
	GsTileLayerPtr layer = new  GsTileLayer(Raster);
	space->layerBox().addLayer(layer);