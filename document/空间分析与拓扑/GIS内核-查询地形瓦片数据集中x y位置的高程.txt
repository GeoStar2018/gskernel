

```c++
GS_TEST(SqliteGeoDatabase, querydemtile, chijing, 20200211)
{
	GsString strInput = this->MakeInputFolder("400sqlite");
	GeoStar::Kernel::GsSqliteGeoDatabaseFactory vFac;
	GeoStar::Kernel::GsConnectProperty vConn;
	vConn.Server = strInput;
	GeoStar::Kernel::GsGeoDatabasePtr ptrGDB = vFac.Open(vConn);
	ASSERT_TRUE(ptrGDB);
	GsTileClassPtr ptrTileClass = ptrGDB->OpenTileClass("dg_dx.tile");
	//12 762 3342   113.778681 23.01233
	//ptrTileClass
	double x = 113.778681, y = 23.01233;

	GsQueryTileAnalysisPtr ptrQueryTile = new GsQueryTileAnalysis();
	ptrQueryTile->Pyramid(ptrTileClass->Pyramid());
	//查询点位对应的瓦片层行列
	GsQuadKey key  = ptrQueryTile->QueryRowCol(12,x,y);
	//获取指定行列的瓦片
	GsTilePtr pTile =  ptrTileClass->Tile(key.Level, key.Row, key.Col);
	//设置处理类型
	ptrQueryTile->TileEncodingType(pTile->TileType());
	
	//获取dem的数据类型
	GsString datatype =  ptrTileClass->MetadataItem("DEM", "DEMValueType");
	GsStringHelp::IsEqual("int16", datatype.c_str())? 
		ptrQueryTile->IsFloatDemValue(false): 
		ptrQueryTile->IsFloatDemValue(true);
	
	//三种方式 查询x,y 位的高程
	double height1 = ptrQueryTile->QueryValue(pTile, x, y);
	double height2 = ptrQueryTile->QueryValue(pTile->TileDataPtr(),pTile->TileDataLength(),key.Level,key.Row, key.Col, x, y);
	GsTileBuffer buffer;
	buffer.ReadFromTile(pTile);
	double height3 = ptrQueryTile->QueryValue(&buffer, x, y);

}
```

