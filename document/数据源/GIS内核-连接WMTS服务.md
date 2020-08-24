# GIS内核-连接WMTS服务 #

```c++


	GeoStar::Kernel::GsWebGeoDatabaseFactoryPtr fac = new GsWebGeoDatabaseFactory();
	GsConnectProperty conn;
	conn.DataSourceType = GeoStar::Kernel::eWeb;
	GsGeoDatabasePtr pDB = fac->Open(conn);
	GsString url = "http://192.168.100.244:6080/arcgis/rest/services/DLG100/MapServer/wmts";
	//url = "https://basemap.nationalmap.gov/arcgis/services/USGSTopo/MapServer/WmsServer";
	//url = "http://www.supermap.com.cn:8090/iserver/services/map-china400/wms111/China";
	//url = "http://www.supermap.com.cn:8090/iserver/services/map-china400/wmts-china";
	//url = "https://t0.tianditu.gov.cn/img_w/wmts";
	url = "https://t0.tianditu.gov.cn/img_c/wmts&tk=a5f4f12ecf3cc967724504f58a750eef";
	GsWMTSUriParserPtr ptrUriParser = new GsWMTSUriParser(url.c_str());// conn.Server);
	//GsArcGISWMTSUriParserPtr ptrUriParser = new GsArcGISWMTSUriParser(url.c_str());
	//GsWellknownTMSUriParserPtr ptrUriParser = new GsWellknownTMSUriParser(eTiandituImageWebMercatorWMTS);
	GsWebUriParserPtr webparser = ptrUriParser;
	//这里直接拿为空
	GsSpatialReferencePtr ptrspa = ptrUriParser->SpatialReference();
	GsPyramidPtr hjhjh= ptrUriParser->Pyramid();

	//这里需要使用自己的key,不然可能会被拒绝,可以需要用户自己申请
	//天地图set
	 //webparser->UserParameter().AddPair(GsString("tk"), GsString("fb1bfb9e06cd7681813a42f4c934e1ea"));
	//webparser->UserParameter().AddPair("tk", "fb1bfb9e06cd7681813a42f4c934e1ea");
	//webparser->UserParameter().AddPair("tk", "a5f4f12ecf3cc967724504f58a750eef");
	bool bok = ptrUriParser->ParseCapability();
	//默认可以不设置
	ptrUriParser->LayerName(ptrUriParser->AllLayerName().at(0));
	ptrUriParser->CurrentTileMatrixSet(ptrUriParser->TileMatrixSet().at(0));
	ptrUriParser->CurrentImageFormat(ptrUriParser->ImageFormat().at(0));
	ptrUriParser->CurrentLayerStyle(ptrUriParser->Style().size() > 0 ? ptrUriParser->Style().at(0) : "default");
	//测试拿到的空间参考和金字塔
	ptrspa = ptrUriParser->SpatialReference();
	int epsg =  ptrspa->EPSG();
	hjhjh = ptrUriParser->Pyramid();
	GsString ss = hjhjh->ToString();
	//wms set  可设置版本信息
	//ptrUriParser->Version("1.1.1");
	//ptrUriParser->ParseCapability();
	//GsVector<GsWMSLayerInformation> vecLayers = ptrUriParser->Layers();
	//ptrUriParser->LayerName(vecLayers[0].Name);
	//if (ptrUriParser->CRS() != vecLayers[0].CRS)
	//	ptrUriParser->CRS("EPSG:3857");
	GsPyramidPtr ptrPy = ptrUriParser->Pyramid();// GsPyramid::WellknownPyramid(eWebMercatorPyramid));
	//这个是最重要的请求串
	GsString str = ptrUriParser->FormatUri();

	GsTileColumnInfo info;
	info.FeatureType = ePrevectorTileFeature;
	info.ValidBottomLevel = ptrUriParser->BottomLevel();
	info.ValidTopLevel = ptrUriParser->TopLevel();
	info.XYDomain = ptrUriParser->LayerExtent();

	GsTileClassPtr Raster = pDB->CreateTileClass("img", ptrUriParser->SpatialReference(), ptrUriParser->Pyramid(), info);
	GsTMSTileClassPtr tms = Raster;
	tms->UrlTemplate(ptrUriParser->FormatUri());
	tms->TileType(ePngType);


	GsTMSParameterPtr ptrTMS = dynamic_cast<GsTMSParameter*>(tms->ExtensionData());
	if (!ptrTMS)
		return ;
	//GsConfig config;  //如果与cookie 可以加
	//if (ptrUriParser->Cookie().size() > 0)
	//{
	//	(config).Child("RequestHeaders").Child("Cookie").Value(ptrUriParser->Cookie());
	//}
	//如果有自定义瓦片抓取也可以用这个 默认不需要
	//ptrTMS->TileCrawler(GsTileCrawler::CreateSpecialTileCrawler(eTiandituImageWebMercatorWMTS, config));

	//http://t1.tianditu.com/DataServer?T=vec_w&x=${Col}&y=${Row}&l=${Level}  
	//"http://t6.tianditu.com/DataServer?T=img_w&x=${Col}&y=${Row}&l=${Level}"
	//http://172.15.110.2:9010/zj/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=zj&STYLE=zj&TILEMATRIXSET=Matrix_zj_0&TILEMATRIX=${Level}&TILEROW=${Row}&TILECOL=${Col}&FORMAT=image%2Fpng
	//这里为模板,这个模板是根据网络请求串抓取的,后期可能封装成常用服务串  

	//这里是缓存,浏览过的瓦片会缓存到此数据集  
	GsGeoDatabaseFactoryPtr cacheFac = new GsSqliteGeoDatabaseFactory();
	GsConnectProperty cacheConn;
	cacheConn.DataSourceType = GsDataSourceType::eSqliteFile;
	cacheConn.Server = "D:\\";
	GsGeoDatabasePtr ptrDB = cacheFac->Open(cacheConn);
	GsTileClassPtr ptrCacheTs = ptrDB->CreateTileClass(GsMath::NewGUID(), new  GsSpatialReference(4326), GsPyramid::WellknownPyramid(e360DegreePyramid), info);
	tms->Cache(ptrCacheTs);		
 
```