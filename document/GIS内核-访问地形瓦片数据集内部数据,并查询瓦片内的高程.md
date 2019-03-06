GIS内核-访问地形瓦片数据集内部数据,并查询瓦片内的高程

	void TileClassRead2()
	{
	
		GsString file("D:\\tiledatafolder\\a.tile");
		GsConnectProperty conntile;
		GsFile ft(file);
		conntile.Server = GsUtf8(ft.Parent().Path()).Str();
		GsSqliteGeoDatabaseFactoryPtr fac = new GsSqliteGeoDatabaseFactory();
		GsGeoDatabasePtr pGeoTile = fac->Open(conntile);
		GsTileClassPtr pTileClass = pGeoTile->OpenTileClass(ft.Name());
		GsTileCursorPtr pTileCursor = pTileClass->Search(5, 10);
	
		GsTilePtr pTile = pTileCursor->Next();
		GsBox box;
		GsPyramidPtr ptrPyramid = pTileClass->Pyramid();
	
		GsGrowByteBuffer tilezipbuff;
		GsGrowByteBuffer tileunzipbuff;
		int tilesize = 150;//
		do
		{
			tilezipbuff.Clear();
			tileunzipbuff.Clear();
			if (!pTile)
				return;
	
			GsQuadKey key(pTile->Level(), pTile->Row(), pTile->Col());
			tilezipbuff.Copy(pTile->TileDataPtr(), pTile->TileDataLength());
			GsZLib::Uncompress(&tilezipbuff, &tileunzipbuff);
			//查询瓦片内第一个点的高程, 瓦片第一个点的地理坐标可以通过金字塔饭算出来
	
	
			//如果地形存储的是 int16
			short h1 = ((short*)tileunzipbuff.BufferHead())[0];
			//如果地形存储的是 float32
			float h2 = ((float*)tileunzipbuff.BufferHead())[0];
		}
		while (pTileCursor->Next(pTile));
	}