	//创建连接
	GsConnectProperty conn;
	conn.Server = "192.168.22.122";
	conn.Database = "orcl";
	conn.Port = 1521;
	conn.User = "test";
	conn.Password = "123456";
	GsGeoDatabasePtr ptrGDB = GsOracleSpatialGeoDatabaseFactory().Open(conn);
	GsRasterCreateableFormat eFormat;
	GsRasterColumnInfo oColumnInfo;
	GsSpatialReference* pSR;
	const char* strOptions = NULL;
	//打开数据集
	GsRasterClassPtr ptrRasterCls = ptrGDB->CreateRasterClass("a", eFormat, oColumnInfo, pSR);

	if (!ptrRasterCls)
		return ;
	//如果为镶嵌栅格数据集
	GsMosaicRasterClassManagerPtr ptrMgr = ptrRasterCls->ExtensionData();
	if (!ptrMgr)
		return ;

	//注册
	ptrMgr->AddRaster("C:\testdata\a.tif");
	ptrMgr->AddRaster("C:\testdata\b.tif");
	ptrMgr->AddRaster("C:\testdata\c.tif");

	//显示
	GsRasterLayerPtr ptrLyr = new GsRasterLayer(ptrRasterCls);
	//gsmap 为 GsMap,地图对象;
	gsmap->Layers()->emplace_back(ptrLyr);
	gsmap->Update();