读取GeoJson矢量文件

	GS_TEST(GsRasterAnalysis, GeoJsonInterpolationGRID, chijing, 20190917) {
		
		GsKernel::Initialize();
		GsPCGeoDatabase::Initialize();
		std::string fcsFolder = this->MakeInputFolder(GsEncoding::ToUtf8("rasteranalysis/interpolation"));
		GsString strFolder = GsFileSystem::Combine(GsUtf8(fcsFolder.c_str()).Str().c_str(), "rain_hainan.json");
		GsConnectProperty conn;
		conn.Server = strFolder;
		GsGeoDatabaseFactoryPtr fac= GsClassFactory::CreateInstance("OGRGeoDatabaseFactory");
		GsGeoDatabasePtr ptrGDB = fac->Open(conn);
		ASSERT_TRUE(ptrGDB != nullptr) << "Could not open the OGRDatabase";
		GsFeatureClassPtr feaClass = ptrGDB->OpenFeatureClass("rain_hainan");
		GsFeatureCursorPtr ptrCursor =  feaClass->Search();
	
		GsFeaturePtr ptrFea =  ptrCursor->Next();
		std::vector<double> vecLocationPoints;
		std::vector<double> vecPointsValue;
		do
		{
			if (!ptrFea)
				break;
	
			GsPointPtr pt = ptrFea->Geometry();
			vecLocationPoints.emplace_back(pt->X());
			vecLocationPoints.emplace_back(pt->Y());
			vecPointsValue.emplace_back(ptrFea->ValueDouble(2));
		} while (ptrCursor->Next(ptrFea));
	
		ASSERT_TRUE(feaClass != nullptr);
	}