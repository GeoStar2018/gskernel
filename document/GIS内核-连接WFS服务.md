
GIS内核-连接WFS服务数据源

	GsConnectProperty prop;
	prop.Server = "http://192.168.37.172:9010/shengjiewfs/wfs";

	GsWebGeoDatabaseFactory fact;
	GsGeoDatabasePtr ptrDatabase = fact.Open(prop);
	GsVector<GsString> vecNames;
	ptrDatabase->DataRoomNames(eFeatureClass, vecNames);

	GsFeatureClassPtr ptrFeatureClass = ptrDatabase->OpenFeatureClass(vecNames[0]);
	GsBox box = ptrFeatureClass->Extent();

	GsEnvelopePtr ptrEnv = new GsEnvelope(box);
	GsSpatialQueryFilterPtr ptrQuery = new GsSpatialQueryFilter(ptrEnv);
	GsFeatureCursorPtr ptrCur = ptrFeatureClass->Search(ptrQuery);
	GsFeaturePtr ptrFea = ptrCur->Next();

	int nCount = 0;
	do {
		if (!ptrFea)
			break;
		int t = ptrFea->FeatureClass()->Fields().FindField("OID");
		int oid = ptrFea->ValueInt64(0);
		nCount++;
	} while (ptrCur->Next(ptrFea));
	EXPECT_EQ(nCount > 0, true);