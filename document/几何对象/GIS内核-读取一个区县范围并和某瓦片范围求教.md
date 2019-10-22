GIS内核-读取一个区县范围并和某瓦片范围求交

	GsShpGeoDatabaseFactoryPtr shp = new GsShpGeoDatabaseFactory();
	GsConnectProperty conn;
	conn.Server = u8"C:\\Users\\chijing\\Desktop\\XZQH\\XZQH";
	GsGeoDatabasePtr pDB = 	shp->Open(conn);
	GsFeatureClassPtr ptrFcs = pDB->OpenFeatureClass(u8"GDDJXZQH2016");
	GsSpatialQueryFilterPtr ptrQuery = new GsSpatialQueryFilter(NULL, u8"\"NAME\"='韶关市'");

	GsFeatureCursorPtr ptrFC = ptrFcs->Search(ptrQuery);
	GsFeaturePtr pFea = ptrFC->Next();

	GsString wkt1 = "POLYGON((113.90625 23.90625, 115.3125 23.90625, 115.3125 25.3125, 113.90625 25.3125, 113.90625 23.90625))";
	GsString wkt2 = "POLYGON((113.90625 25.3125, 115.3125 25.3125, 115.3125 26.71875, 113.90625 26.71875, 113.90625 25.3125))";
	GsWKTOGCReader reader(wkt1);

	GsGeometryPtr ptrGeo1 =  reader.Read();
	reader.Begin(wkt2);
	GsGeometryPtr ptrGeo2 = reader.Read();
	GsGeometryPtr ptrSX= pFea->Geometry();
	//ptrSX =  ptrSX->Simplify();
	GsGeometryRelationResult ggg1 = ptrSX->IsIntersect(ptrGeo1);
	GsGeometryRelationResult ggg2 = ptrSX->IsIntersect(ptrGeo2);
	GsGeometryRelationResult ggg3 = ptrSX->IsContain(ptrGeo1);
	GsGeometryRelationResult ggg4 = ptrSX->IsContain(ptrGeo2);