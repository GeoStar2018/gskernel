MySQL数据源瓦片导入到fcs数据源
		
		System.loadLibrary("gsjavaport");
		GsKernel.Initialize();
		GsPCGeoDatabase.Initialize();
		
		GsRefObject pFac =  GsClassFactory.CreateInstance("MySqlGeoDatabaseFactory");
		GsGeoDatabaseFactory facwww = 	GsGeoDatabaseFactory.DowncastTo(pFac);

		com.geostar.kernel.GsConnectProperty connProperty  =new GsConnectProperty();
		connProperty.setDatabase("test");
		connProperty.setServer("127.0.0.1");
		connProperty.setUser("t");//("t");
		connProperty.setPassword("1");
		connProperty.setPort(3306);
		GsGeoDatabase pdb = facwww.Open(connProperty);
		
		GsTileClass ptcls =  pdb.OpenTileClass("ddd");
		
		//创建一个tile文件并打开
		GsSqliteGeoDatabaseFactory sqliteFac  = new GsSqliteGeoDatabaseFactory();
		com.geostar.kernel.GsConnectProperty connProperty2  =new GsConnectProperty();
		connProperty2.setDatabase("D:\\");
		GsGeoDatabase pdb2 =  sqliteFac.Open(connProperty2);
		GsTileClass ptclsw = pdb2.CreateTileClass("a", ptcls.SpatialReference(), ptcls.Pyramid(), ptcls.TileColumnInfo());
		
		
		GsTileCursor tileCursor =  ptcls.Search();
		GsTile tile = tileCursor.Next();	
		
		GsTile pWTile =  ptclsw.CreateTile();
		ptcls.Transaction().StartTransaction();
		int count = 0;
		do
		{
			byte[] by =  new byte[tile.TileDataLength()];
			tile.TileDataPtr(by);
			pWTile.TileData(by, tile.TileDataLength());
			pWTile.Level(tile.Level());
			pWTile.Row(tile.Row());
			pWTile.Col(tile.Col());
			pWTile.Store();
			if(count++ >1000)
			{
				ptcls.Transaction().StartTransaction();
				ptcls.Transaction().CommitTransaction();
			}
		}while(tileCursor.Next(tile));
		
		ptcls.Transaction().CommitTransaction();