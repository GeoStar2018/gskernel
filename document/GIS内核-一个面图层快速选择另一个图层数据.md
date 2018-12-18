GIS内核-一个面图层快速选择另一个图层数据, 输出OID

    		GsSqliteGeoDatabaseFactory fac;
		GsConnectProperty conn;
		conn.Server = "D:\\source\\kernel\\testdata\\400sqlite";
	
		GsGeoDatabasePtr ptrGDB =  fac.Open(conn);
		GsFeatureClassPtr ptrtarget = ptrGDB->OpenFeatureClass("housegrid.fcs");
		GsFeatureClassPtr ptrSource = ptrGDB->OpenFeatureClass("gridwg.fcs");

		GsQueryFilterPtr d = new GsQueryFilter(0);
		GsSelectionSetPtr ptrSel =  ptrSource->Select(d);
		int n = ptrSel->Count();
		GsEnumIDsPtr pIDS = ptrSel->EnumIDs();

		std::set<long long> ids;

		for(int i = 0; i< n;i++)
		{
			long long id =  pIDS->Next();

			GsFeaturePtr fea = ptrSource->Feature(id);
			GsSpatialQueryFilterPtr d1 = new GsSpatialQueryFilter(fea->Geometry());
			
			{
				GsSelectionSetPtr ptrSel = ptrtarget->Select(d1);
				GsEnumIDsPtr pIDS2 = ptrSel->EnumIDs();
				int id2 = 0;
				while ((id2  = pIDS2->Next()) >0)
				{
					ids.emplace(id2);
				}
			}
		}

		int count= ids.size();