GIS 内核- 二维表数据保存



```c++
GS_TEST(SqliteRowClass, Create, chijing, 20200213)
{
	GeoStar::Kernel::GsSqliteGeoDatabaseFactory vFac;
	GeoStar::Kernel::GsConnectProperty vConn;
	vConn.Server = GeoStar::Utility::GsFileSystem::Combine(CurrentFolder().c_str(), "../testdata/400sqlite");
	GeoStar::Kernel::GsGeoDatabasePtr ptrDB = vFac.Open(vConn);

	std::map<GsString, Data::GsFieldType> mapFields;
	mapFields["PTUID"] = Data::GsFieldType::eIntType;//
	mapFields["PTUIP"] = Data::GsFieldType::eStringType;//
	mapFields["MonitorNo"] = Data::GsFieldType::eIntType;//
	mapFields["LocationI"] = Data::GsFieldType::eStringType;//
	mapFields["LocationII"] = Data::GsFieldType::eStringType;//
	mapFields["LocationIII"] = Data::GsFieldType::eStringType;//
	mapFields["LPUTypeCode"] = Data::GsFieldType::eIntType;//
	mapFields["LPUTypeName"] = Data::GsFieldType::eStringType;//
	mapFields["MonitorTypeCode"] = Data::GsFieldType::eIntType;//
	mapFields["MonitorTypeName"] = Data::GsFieldType::eStringType;//
	mapFields["OnlineTime"] = Data::GsFieldType::eDateType;//
	mapFields["OfflineTime"] = Data::GsFieldType::eDateType;//
	mapFields["PTUState"] = Data::GsFieldType::eIntType;//
	mapFields["Others"] = Data::GsFieldType::eStringType;//

	if (!ptrDB)
		return ;
	GsFields fields;
	std::map<std::string, int> m_mapFieldsIndex;
	m_mapFieldsIndex["OID"] = 0;
	fields.Fields.emplace_back("OID", Data::GsFieldType::eInt64Type);
	for (auto itr = mapFields.begin();
		itr != mapFields.end(); 
		itr++)
	{
		GsField field(itr->first.c_str(), itr->second);
		fields.Fields.push_back(field);
		int h = std::distance(mapFields.begin(), itr)+1;
		m_mapFieldsIndex[itr->first.c_str()] = h;
	}
	GsFeatureClassPtr ptrFcs =  ptrDB->OpenFeatureClass("1");
	GeoStar::Utility::Data:: GsTransaction * ptran1 =  ptrFcs->GeoDatabase()->Transaction();
	GeoStar::Utility::Data::GsTransaction * ptran2 = ptrFcs->Transaction();
	GsRowClassPtr ptrTable = ptrDB->OpenRowClass("cj_cretaerow");
	if (ptrTable)
		ptrTable->Delete();
	ptrTable = ptrDB->CreateRowClass("cj_cretaerow", fields);
	ptrTable->Transaction()->StartTransaction();
	GsRowPtr ptrRow = ptrTable->CreateRow();
	for (int i = 0; i < 10; i++) {
		ptrRow->OID(i);
		ptrRow->Value(m_mapFieldsIndex["PTUID"], 5110 + i);
		ptrRow->Value(m_mapFieldsIndex["PTUIP"], "192.168.3.11");
		ptrRow->Value(m_mapFieldsIndex["MonitorNo"], i);
		ptrRow->Value(m_mapFieldsIndex["LocationI"], u8"湖北");
		ptrRow->Value(m_mapFieldsIndex["LocationII"], u8"武汉");
		ptrRow->Value(m_mapFieldsIndex["LocationIII"], u8"洪山");
		ptrRow->Value(m_mapFieldsIndex["LPUTypeCode"], i);
		ptrRow->Value(m_mapFieldsIndex["LPUTypeName"], "PTUType");
		ptrRow->Value(m_mapFieldsIndex["MonitorTypeCode"], i);
		ptrRow->Value(m_mapFieldsIndex["MonitorTypeName"], "MonitorType" );
		ptrRow->Value(m_mapFieldsIndex["OnlineTime"], GsDateTime::Now());
		ptrRow->Value(m_mapFieldsIndex["OfflineTime"], GsDateTime::Now());
		ptrRow->Value(m_mapFieldsIndex["PTUState"], i);
		ptrRow->Value(m_mapFieldsIndex["Others"], "00");
		ptrRow->Store();
	}
	ptrTable->Transaction()->CommitTransaction();

}
```

