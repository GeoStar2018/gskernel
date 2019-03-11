# GIS内核-连接WMTS服务 #

     GsWebGeoDatabaseFactoryPtr ptrFac = new GsWebGeoDatabaseFactory();  
    GsGeoDatabaseFactoryPtr fac = ptrFac;  
    GsConnectProperty cp;  
    cp.DataSourceType = GsDataSourceType::eWeb;  
      
    GsString str = "http://t0.tianditu.com/img_c/wmts";  
    cp.Server = str;  
    GsLogger::Default().AutoFlush(true);  
    GsLogger::Default().LogLevel(eLOGALL);  
    GsGeoDatabasePtr db = fac->Open(cp);  
      
    GsTileColumnInfo info;  
    info.XYDomain = GsBox(-180, -90, 180, 90);  
    info.ValidBottomLevel = 20;  
    info.ValidTopLevel = 0;  
    info.FeatureType = eImageTileFeature;  
    GsTileClassPtr Raster = db->CreateTileClass("img", new  GsSpatialReference(4326), GsPyramid::WellknownPyramid(e360DegreePyramid), info);  
    GsTMSTileClassPtr tms = Raster;  
    //http://t1.tianditu.com/DataServer?T=vec_w&x=${Col}&y=${Row}&l=${Level}  
    //这里为模板,这个模板是根据网络请求串抓取的,后期可能封装成常用服务串  
    tms->UrlTemplate("http://t${Odd-Even7}.tianditu.com/DataServer?T=img_w&x=${Col}&y=${Row}&l=${Level}&tk=2ce94f67e58faa24beb7cb8a09780552");  
    tms->TileType(ePngType);  
    //这里是缓存,浏览过的瓦片会缓存到此数据集  
    GsGeoDatabaseFactoryPtr cacheFac = new GsSqliteGeoDatabaseFactory();  
    GsConnectProperty cacheConn;  
    cacheConn.DataSourceType = GsDataSourceType::eSqliteFile;  
    cacheConn.Server = "D:\\";  
    GsGeoDatabasePtr ptrDB = cacheFac->Open(cacheConn);  
    GsTileClassPtr ptrCacheTs =  ptrDB->CreateTileClass("imgCache", new  GsSpatialReference(4326), GsPyramid::WellknownPyramid(e360DegreePyramid), info);  
    tms->Cache(ptrCacheTs);  
    //加载图层到视图  
    GsTileLayerPtr layer = new  GsTileLayer(Raster);  
    space->layerBox().addLayer(layer); 