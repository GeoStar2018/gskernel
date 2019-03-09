## 抽稀类使用示例代码 ##

	GS_TEST(GsGeneralityProcess, CheckGenerality_PolygonGenerality, yulei, 20190219) 
    {
		//数据准备
    	std::string fcsFolder = this->MakeInputFolder(GsEncoding::ToUtf8("topo/test"));
    	std::string tempFolder = this->MakeInputFolder(GsEncoding::ToUtf8("topo/temp"));
    
    	GsSqliteGeoDatabaseFactoryPtr fac = new GsSqliteGeoDatabaseFactory();
    	GsConnectProperty conn;
    	conn.Server = fcsFolder.c_str();
    	GsGeoDatabasePtr pDB = fac->Open(conn);
    
    	GsFeatureClassPtr pFcs = pDB->OpenFeatureClass(GsEncoding::ToUtf8("LINE2"));
    	FeatureClassReader pIO1(pFcs, 0);
		GsFields fds;
    	CreateTestGsFields(fds);

		//创建一个输出数据集
    	GsGeometryColumnInfo g = pFcs->GeometryColumnInfo();
    	g.GeometryType = eGeometryTypePolygon;
    	GeoStar::Kernel::GsFeatureClassPtr mfd_PointFcs = pDB->CreateFeatureClass(GsEncoding::ToUtf8("LINE2_面"), fds, g, pFcs->SpatialReference());    
    	FeatureClassesWrtier writer(NULL, NULL, mfd_PointFcs);
    	
    	GsGeneralityProcessPtr ptrGenerality = new GsGeneralityProcess(GsGeneralityProcessType::eGPT_PolygonGenerality, 0.01);//创建抽稀对象
    	ptrGenerality->AddData(&pIO1);	//设置输入流    	
    	ptrGenerality->OutputData(&writer);//设置输出流
    
    	bool bRes = ptrGenerality->Preprocess();//进行抽稀处理
    	ASSERT_EQ(bRes, true);
    }