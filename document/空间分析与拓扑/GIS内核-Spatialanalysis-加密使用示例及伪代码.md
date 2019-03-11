## 加密类使用示例代码 ##
    
    GS_TEST(GsIncreaseProcess, CheckIncrease_Process, yulei, 20190221)
    {
    	GsString fcsFolder = this->MakeInputFolder(GsEncoding::ToUtf8("topo/抽稀处理"));
    
    	GsSqliteGeoDatabaseFactoryPtr fac = new GsSqliteGeoDatabaseFactory();
    	GsConnectProperty conn;
    	conn.Server = fcsFolder.c_str();
    	GsGeoDatabasePtr pDB = fac->Open(conn);
    	GsString strFileName = GsEncoding::ToUtf8("T3007_2_A");
    
    	GsFeatureClassPtr pFcs = pDB->OpenFeatureClass(strFileName);
    	ASSERT_TRUE(pFcs);
    
    	FeatureClassReader pIO1(pFcs, 0);
    
    	GsIncreaseProcessPtr ptrGenerality = new GsIncreaseProcess(GsIncreaseProcessType::eIPT_EqualIncrease, 5);
    	ptrGenerality->AddData(&pIO1);
    
    	GsFields fds;
    	CreateTestGsFields(fds);
    	//创建一个输出数据集
    	GsGeometryColumnInfo g = pFcs->GeometryColumnInfo();
    	g.GeometryType = eGeometryTypePolygon;
    	GsString strResFile = strFileName + GsEncoding::ToUtf8("_结果");
    	GsString strFileDele = fcsFolder + GsEncoding::ToUtf8("\\") + strFileName + GsEncoding::ToUtf8("_结果.fcs");
    	if (GsFileSystem::Exists(strFileDele))
    	{
    		GsFile::Delete(strFileDele);
    	}
    
    	GeoStar::Kernel::GsFeatureClassPtr resFcs = pDB->CreateFeatureClass(strResFile, fds, g, pFcs->SpatialReference());
    	ASSERT_TRUE(resFcs);
    
    	FeatureClassesWrtier writer(NULL, NULL, resFcs);
    	ptrGenerality->OutputData(&writer);
    
    	bool bRes = ptrGenerality->Preprocess();
    	ASSERT_EQ(bRes, true);
    }