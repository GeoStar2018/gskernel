GIS内核- 获取地形瓦片数据集DEM相关元数据信息



	GsGeoDatabasePtr ptrFcsGeoDb = OpenSQLITEGDB(u8"D:\\testdata");
	GsTileClassPtr ptrTls =  ptrFcsGeoDb->OpenTileClass("ff");
	GsVector<GsString>  Domain=  ptrTls->MetadataDomain();
	for (int i = 0; i < Domain.size(); i++)
	{
		GsVector<GsString>  DomainKeys =  ptrTls->MetadataName(Domain[i]);
		for (int j = 0; j < DomainKeys.size(); j++)
		{
			GsString value =  ptrTls->MetadataItem(Domain[i], DomainKeys[j]);
			std::cout << Domain[i] << " -------  " <<DomainKeys[j]<<"--------"<< value << std::endl;
		}
	}
	
	static GsGeoDatabasePtr OpenSQLITEGDB(const char* str)
	{
		GsConnectProperty vConn;
		vConn.Server = str;
		vConn.DataSourceType = GsDataSourceType::eGDB;
		GsSqliteGeoDatabaseFactoryPtr ptrFac = new GsSqliteGeoDatabaseFactory();
		return ptrFac->Open(vConn);
	}

----------
控制台输出结果:
DEM -------  Samples--------150
DEM -------  ValueType--------int16
DEM -------  InvalidValue---------1
