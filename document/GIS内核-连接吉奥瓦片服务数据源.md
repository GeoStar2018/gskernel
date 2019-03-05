GIS内核-连接吉奥瓦片服务数据源

	GsGeoDatabasePtr ptrGDB =  GsWebGeoDatabaseFactory().Open(GsConnectProperty());
	GsWMSUriParser parse("http://sampleserver1.arcgisonline.com/ArcGIS/services/Specialty/ESRI_StateCityHighway_USA/MapServer/WMSServer");
	GsGeoTileUriParser parse("http://19.12.2.22:7001/dg/services/tile");
	ASSERT_TRUE(parse.ParseCapability());
	
	GsString str = parse.FormatUri();
	
	GsTileColumnInfo col;
	col.FeatureType = ePrevectorTileFeature;
	col.XYDomain = parse.LayerExtent();
	col.ValidTopLevel = parse.TopLevel();
	col.ValidBottomLevel = parse.BottomLevel();

	GsTMSTileClassPtr ptrTMS = ptrGDB->CreateTileClass("GeoTile", parse.SpatialReference(), parse.Pyramid(),col);
	ptrTMS->UrlTemplate(str);

	GsTileCursorPtr ptrCursor =  ptrTMS->Search();
	GsTilePtr ptrTile = ptrCursor->Next();