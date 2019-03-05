
GIS内核-访问矢量地物类数据源

GsFeatureClassPtr ReadFeatureClass()
{
	GsConnectProperty conn;
	conn.DataSourceType = eSqliteFile;
	conn.Server = "../testdata/sqlite";//../testdata/sqlite/BOU1_4M_L.fcs
	GsSqliteGeoDatabaseFactory obj;
	GsGeoDatabasePtr ptrDB = obj.Open(conn);
	GsFeatureClassPtr feaclass = ptrDB->OpenFeatureClass("BOU1_4M_L");

	GsFeatureCursorPtr ptrCursor = feaclass->Search();
	GsFeaturePtr ptrFea = ptrCursor->Next();
	GsFields fs = feaclass->Fields();
	do
	{
		//得到几何
		GsGeometryPtr ptrGeo = ptrFea->Geometry();
		//获取属性
		GsAny any;
		for (int i = 2; i < fs.Fields.size(); i++)
		{
			switch (ptrFea->ValueType(i))
			{
			case Data::eInt64Type:
				any = ptrFea->ValueInt64(i);
				break;
			case Data::eIntType:
				any = ptrFea->ValueInt(i);
				break;
			case Data::eFloatType:
				any = ptrFea->ValueFloat(i);
				break;
			case Data::eDoubleType:
				any = ptrFea->ValueDouble(i);
				break;
			case Data::eStringType:
				any = (const char*)ptrFea->ValuePtr(i);
				break;
			}
		}

	} while (ptrCursor->Next(ptrFea));

}
