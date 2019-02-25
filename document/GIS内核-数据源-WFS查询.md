GIS内核-数据源示例-WFS查询示例

	GS_TEST(VectorTile, WFS, cj, 20170714)
	{
	//	Res2Scale(360.0 / 256, new GsSpatialReference(4326));
	
		GsConnectProperty prop;
		//prop.Server = "http://192.168.100.134:9010/road_label2/wfs";
		//prop.Server = "http://192.168.36.131:9010/WFS/wfs";
		//prop.Server = "http://gisserver.tianditu.com/TDTService/wfs";
		//prop.Server = "http://172.15.103.160:7001/wfstest/wfs";
		prop.Server = "http://172.15.103.160:7001/wfst_xx/wfs";
	
		GsWebGeoDatabaseFactory fact;
		GsGeoDatabasePtr ptrDatabase = fact.Open(prop);
		GsVector<GsString> vecNames;
		ptrDatabase->DataRoomNames(eFeatureClass, vecNames);
	
		GsFeatureClassPtr ptrFeatureClass = ptrDatabase->OpenFeatureClass(vecNames[3]);
		GsBox box = ptrFeatureClass->Extent();
	
		GsEnvelopePtr ptrEnv = new GsEnvelope(box);
		GsSpatialQueryFilterPtr ptrQuery = new GsSpatialQueryFilter(ptrEnv);
		//ptrQuery->WhereClause(strSQL.c_str());
		GsFeatureCursorPtr ptrCur = ptrFeatureClass->Search(ptrQuery);
		GsFeaturePtr ptrFea = ptrCur->Next();
	
		int nCount = 0;
		do {
			if (!ptrFea)
				break;
			int t = ptrFea->FeatureClass()->Fields().FindField("OID");
			int oid = ptrFea->ValueInt64(t);
			nCount++;
		} while (ptrCur->Next(ptrFea));
		EXPECT_EQ(nCount > 0, true);
	}