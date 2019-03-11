GIS内核-地名瓦片数据访问

	//创建
	void CreateWTFSClass()
	{
		std::string strInput = MakeOutputFolder("poi");
		GsConnectProperty conn;
		conn.Server = strInput;
		strInput = GsFileSystem::Combine(strInput.c_str(), "testpoi.tfg");
		GsFile f(strInput.c_str());
		if (f.Exists())
			f.Delete();
	
	
		GsGeoDatabasePtr ptrGDB = GsSqliteGeoDatabaseFactory().Open(conn);
		GsFields fs;
		fs.Fields.push_back(GsField("Caption", Data::eStringType, 1024, 1024));
		GsGeometryColumnInfo col;
		col.FeatureType = eSimpleFeature;
		col.GeometryType = eGeometryTypePoint;
		GsSpatialReferencePtr ptrSR = new GsSpatialReference(eWGS84);
		GsFeatureClassPtr ptrFeaClass = ptrGDB->CreateFeatureClass("testpoi.tfg", fs, col, ptrSR);
	
		ASSERT_TRUE(ptrFeaClass);
	
		GsDataRoomExtensionDataPtr ptrData = ptrFeaClass->ExtensionData();
		ASSERT_TRUE(ptrData);
		GsPlaceNameManagerExtensionDataPtr ptrPOI = ptrData;
		ASSERT_TRUE(ptrPOI);
	
	}

 
	打开
	void OpenWTFSClass()
	{
		std::string strInput = MakeInputFolder("poi");
		GsConnectProperty conn;
		conn.Server = strInput;
		GsGeoDatabasePtr ptrGDB = GsSqliteGeoDatabaseFactory().Open(conn);
		GsFeatureClassPtr ptrFeaClass = ptrGDB->OpenFeatureClass("china_all");
		ASSERT_TRUE(ptrFeaClass);
		GsDataRoomExtensionDataPtr ptrData = ptrFeaClass->ExtensionData();
		ASSERT_TRUE(ptrData);
		GsPlaceNameManagerExtensionDataPtr ptrPOI = ptrData;
		ASSERT_TRUE(ptrPOI);
	
		GsIconLibrary* iconLib = ptrPOI->IconLibrary();
		ASSERT_TRUE(iconLib);
		ASSERT_EQ(iconLib->Count(),26);
	
		GsPlaceNameSymbolLibrary* symLib = ptrPOI->SymbolLibrary();
		ASSERT_TRUE(symLib);
		ASSERT_EQ(symLib->Count(), 45);
		
		GsPlaceNameMetadata meta = ptrPOI->Metadata();
		GsTileClassPtr ptrTileClass = ptrPOI->PublishTileClass();
	
		long long n = ptrTileClass->TileCount();
		ASSERT_EQ(n, 23269);
	
	}