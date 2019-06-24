shp,fcs,memory 地物类互相导入导出

	GsGeoDatabasePtr OpenorGeoDatabase(GsConnectProperty conn)
	{
		GsGeoDatabaseFactoryPtr ptrFac = 0;
		switch (conn.DataSourceType)
		{
		case eSqliteFile:ptrFac = new GsSqliteGeoDatabaseFactory(); break;
	
		case eShapeFile: ptrFac = new GsShpGeoDatabaseFactory(); break;
			/// \brief OGC GeoPakcage数据源
		case eGeoPackage: ptrFac = new GsGeoPackageGeoDatabaseFactory(); break;
			/// \brief OracleSpatial数据源
		case eOracleSpatial:
			/// \brief 标准OracleSpatial数据源
		case eOracleSpatial2: ptrFac = new GsOracleSpatialGeoDatabaseFactory(); break;
			/// \brief MySQL数据源
		case	eMySQL: ptrFac = new GsMySqlGeoDatabaseFactory(); break;
			/// \brief PostgreSQL数据源
		case		ePostgreSQL:ptrFac = new GsPostGISGeoDatabaseFactory(); break;
			/// \brief 所有文件类型的数据源
		case		eFile:ptrFac = new GsFileGeoDatabaseFactory(); break;
			/// \brief 达梦数据源
		case eDameng: ptrFac = new GsDamengGeoDatabaseFactory(); break;
			/// \brief web数据源
		case eWeb:ptrFac = new GsWebGeoDatabaseFactory(); break;
			/// \brief RVDB 数据源
		case eRvdb:ptrFac = new GsRVDBGeoDatabaseFactory(); break;
			/// \brief OGR数据源
		case		eOGR: ptrFac = new GsOGRGeoDatabaseFactory(); break;
			/// \brief GDB数据源
		case  eGDB:ptrFac = new GsGDBGeoDatabaseFactory(); break;
		default:
			break;
		}
	}
	
	GsVector<GsString> EnumDatabseFeatureClassName(GsGeoDatabase* pGeo, GsDataRoomType type)
	{
		GsVector<GsString> vec;
		if (!pGeo)
			return vec;
		pGeo->DataRoomNames(type, vec);
		return vec;
	}
	
	GsFeatureClassPtr OpenFeatureClass(GsGeoDatabase* pGeo, const char* strName)
	{
		if (!pGeo)
			return 0;
		return pGeo->OpenFeatureClass(strName);
	}
	
	//读取fcs中所有地物并存入到内存数据源
	int ToOtherFeatureClass(GsFeatureClass* pSrcFcs,GsFeatureClass * pDstFcs)
	{
		if (!pSrcFcs || !pDstFcs)
			return 0;
		GsFeatureCursorPtr ptrCursor = pSrcFcs->Search();
		if (!ptrCursor)
			return 0;
		GsFeaturePtr ptrFea = ptrCursor->Next();
		GsFields fs = pSrcFcs->Fields();
		GsFeaturePtr ptrTarget = pDstFcs->CreateFeature();
		long long nCOunt = 0;
		GeoStar::Utility::Data::GsTransactionPtr ptrTran = pDstFcs->Transaction();
		
		if (ptrTran)
		{
			ptrTran->StartTransaction();
		}
		do
		{
			ptrTarget->OID(-1);
			ptrTarget->Geometry(ptrFea->Geometry());
			for (int i = 2; i < fs.Fields.size(); i++)
			{
				switch (ptrFea->ValueType(i))
				{
				case Data::eInt64Type:
					ptrTarget->Value(i, ptrFea->ValueInt64(i));
					break;
				case Data::eIntType:
					ptrTarget->Value(i, ptrFea->ValueInt(i));
					break;
				case Data::eFloatType:
					ptrTarget->Value(i, ptrFea->ValueFloat(i));
					break;
				case Data::eDoubleType:
					ptrTarget->Value(i, ptrFea->ValueDouble(i));
					break;
				case Data::eStringType:
					ptrTarget->Value(i, (const char*)ptrFea->ValuePtr(i));
					break;
				}
			}
			ptrTarget->Store();
			if (ptrTran)
			{
				if ((nCOunt % 10000) == 0) 
				{
					ptrTran->StartTransaction();
					ptrTran->CommitTransaction();
				}
			}
			nCOunt++;
		} while (ptrCursor->Next(ptrFea));
		if (ptrTran)
		{
			ptrTran->CommitTransaction();
		}
		pDstFcs->CreateSpatialIndex();
	
		std::cout << "end Convert" << std::endl;
		return nCOunt;
	}
	
	//shp memory fcs数据源地物类互相导入导出
	void Test()
	{
		GsConnectProperty conn;
		conn.DataSourceType = eSqliteFile;
		conn.Server = "../testdata/sqlite";
		GsSqliteGeoDatabaseFactory obj;
		//打开fcs数据库
		GsGeoDatabasePtr ptrDB = OpenorGeoDatabase(conn);
	
		//创建fcs地物类
		GsFeatureClassPtr feaclass = ptrDB->OpenFeatureClass("BOU1_4M_L");
		//打开内存数据库
		GsGeoDatabasePtr ptrGDB = GsMemoryGeoDatabaseFactory().Open(GsConnectProperty());
		GsFeatureClassPtr ptrFeaClass = ptrGDB->OpenFeatureClass("BOU1_4M_L");
		//存在则删除
		if (ptrFeaClass)
			ptrFeaClass->Delete();
		//创建内存地物类
		ptrFeaClass = ptrGDB->CreateFeatureClass(feaclass->Name().c_str(),
			feaclass->Fields(), feaclass->GeometryColumnInfo(),
			feaclass->SpatialReference());
	
		GsConnectProperty connshp;
		connshp.DataSourceType = eShapeFile;
		connshp.Server = "../testdata/shp";
		GsGeoDatabasePtr ptrDBshp = OpenorGeoDatabase(connshp);
		//创建shp地物类
		GsFeatureClassPtr feaclassshp = ptrDB->OpenFeatureClass("BOU1_4M_L");
		//fcs 导入到内存数据库
		ToOtherFeatureClass(feaclass, ptrFeaClass);
		//内存导入到fcs
		ToOtherFeatureClass(ptrFeaClass, feaclass);
	
		//fcs 导入到shp数据库
		ToOtherFeatureClass(feaclass, feaclassshp);
		//内存导入到fcs
		ToOtherFeatureClass(feaclassshp, feaclass);
	
	
		//shp 导入到内存数据库
		ToOtherFeatureClass(feaclassshp, ptrFeaClass);
		//内存导入到shp
		ToOtherFeatureClass(ptrFeaClass, feaclassshp);
	}
