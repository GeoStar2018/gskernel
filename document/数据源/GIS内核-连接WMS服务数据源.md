
GIS内核-连接WMS服务数据源
	GsGeoDatabasePtr ptrGDB =  GsWebGeoDatabaseFactory().Open(GsConnectProperty());
	GsWMSUriParser parse("http://sampleserver1.arcgisonline.com/ArcGIS/services/Specialty/ESRI_StateCityHighway_USA/MapServer/WMSServer");
	ASSERT_TRUE(parse.ParseCapability());
	parse.Version("1.1.1");
	GsString str = parse.FormatUri();


	GsTileColumnInfo col;
	col.FeatureType = ePrevectorTileFeature;
	col.XYDomain = parse.LayerExtent();
	col.ValidTopLevel = parse.TopLevel();
	col.ValidBottomLevel = parse.BottomLevel();

	GsTMSTileClassPtr ptrTMS = ptrGDB->CreateTileClass("ESRI", parse.SpatialReference(), parse.Pyramid(),col);
	ptrTMS->UrlTemplate(str);

	GsTileCursorPtr ptrCursor =  ptrTMS->Search();
	GsTilePtr ptrTile = ptrCursor->Next();