	//打开ftp上得tif文件,  不用映射盘符
    GsConnectProperty conn;
	conn.Server = u8"ftp://127.0.0.1/";
	conn.User = user;
	conn.Password = pwd;
	//rasterclass 打开遍历数据
	{
		GsFileSystemDataSourceFactoryPtr ptr = new GsFileSystemDataSourceFactory();
		GsFileGeoDatabaseFactoryPtr pfac = new GsFileGeoDatabaseFactory();

		GsGeoDatabasePtr pGDB = pfac->Open(conn);
		GsRasterClassPtr ptrFeaClass = 0;
		if (pGDB) {

			ptrFeaClass = pGDB->OpenRasterClass(name);
		}
		auto r = ptrFeaClass->ExtentToRange(ptrFeaClass->Extent());
		auto cursor = ptrFeaClass->Search(r);
		GsRaster ras;
		while (cursor->Next(&ras))
		{
			std::cout << ras.DataLength() << std::endl;
		}
	}