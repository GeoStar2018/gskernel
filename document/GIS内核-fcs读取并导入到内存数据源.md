GIS内核-将fcs读取并导入到内存数据源
	
	//读取fcs中所有地物并存入到内存数据源
	GsFeatureClassPtr ToMemory()
	{
		GsConnectProperty conn;
		conn.DataSourceType = eSqliteFile;
		conn.Server = "../testdata/sqlite";
		GsSqliteGeoDatabaseFactory obj;
		GsGeoDatabasePtr ptrDB = obj.Open(conn);
		GsFeatureClassPtr feaclass = ptrDB->OpenFeatureClass("BOU1_4M_L");

		GsGeoDatabasePtr ptrGDB = GsMemoryGeoDatabaseFactory().Open(GsConnectProperty());
	
		GsFeatureClassPtr ptrFeaClass = ptrGDB->CreateFeatureClass(feaclass->Name().c_str(),
			feaclass->Fields(), feaclass->GeometryColumnInfo(),
			feaclass->SpatialReference());
	
		GsFeatureCursorPtr ptrCursor = feaclass->Search();
		GsFeaturePtr ptrFea = ptrCursor->Next();
		GsFields fs = feaclass->Fields();
		GsFeaturePtr ptrTarget = ptrFeaClass->CreateFeature();
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
		} while (ptrCursor->Next(ptrFea));
	
		ptrFeaClass->CreateSpatialIndex();
	
		std::cout << "end import to memory" << std::endl;
		return ptrFeaClass;
	}