GIS内核-将arcgis缓存文件转为mbtiles



```c++
	GsGeoDatabaseFactoryPtr ptrFac = new GsESRIFileGeoDatabaseFactory();
	GsConnectProperty conn;
	conn.DataSourceType = eFile;
	conn.Server = u8"D:\\testdata\\tc";
	GsGeoDatabasePtr pDB = ptrFac->Open(conn);
	GsTileClassPtr ptrClass =  pDB->OpenTileClass("tj");
	GsGeoDatabaseFactoryPtr ptrFac1 = new GsSqliteGeoDatabaseFactory();
	GsConnectProperty conn1;
	conn1.DataSourceType = eSqliteFile;
	conn1.Server = u8"D:\\testdata\\tc"; 
	GsGeoDatabasePtr pDBsqlite = ptrFac1->Open(conn1);
	GsPyramidPtr pyramid = GsPyramid::WellknownPyramid(GsWellknownPyramid::eWebMercatorPyramid);
	double atmp = pyramid->FromY;
	pyramid->FromY = pyramid->ToY;
	pyramid->ToY = atmp;
	auto ginfo = ptrClass->TileColumnInfo();
	ginfo.ValidTopLevel = 0;
	ginfo.ValidBottomLevel = 11;
	GsTileClassPtr ptrClassdst = pDBsqlite->CreateTileClass("tj6.mbtiles", ptrClass->SpatialReference(), pyramid, ginfo);
	
	GsTilePtr ptiledst = ptrClassdst->CreateTile();

	GsTileCursorPtr ptileCursor =  ptrClass->Search();
	GsTilePtr ptilesrc = ptileCursor->Next();
	ptrClassdst->Transaction()->StartTransaction();
	int count = 0;
	do
	{
		if (!ptilesrc)
			break;
		count++;
		if (ptilesrc->TileDataLength() <= 872)
			continue;

		GsBox box;
		ptrClass->Pyramid()->TileExtent(ptilesrc->Level(), ptilesrc->Row(), ptilesrc->Col(), &box.XMin, &box.YMin, &box.XMax, &box.YMax);;
		GsRawPoint pt = box.Center();
		int dstR = 0, dstC = 0;
		pyramid->TileIndex(ptilesrc->Level(), pt.X, pt.Y, &dstR, &dstC);

		ptiledst->TileData(ptilesrc->TileDataPtr(), ptilesrc->TileDataLength());
		ptiledst->TileType(ptilesrc->TileType());

		ptiledst->Level(ptilesrc->Level());
		ptiledst->Row(dstR);
		ptiledst->Col(dstC);
		ptiledst->Store();
		if (count > 10000) {
			ptrClassdst->Transaction()->CommitTransaction();
			ptrClassdst->Transaction()->StartTransaction();
			count = 0;
		}
		
	} while (ptileCursor->Next(ptilesrc));
	ptrClassdst->Transaction()->CommitTransaction();
```

