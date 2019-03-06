GIS内核-访问栅格瓦片数据并绘制到Display上


	void TileClassRead(GsDisplay* pDisplay)
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
		GsDisplayTransformationPtr ptrDT = pDisplay->DisplayTransformation();
		GsRect rect(0, 0, ptrPyramid->TileSizeX, ptrPyramid->TileSizeY);
		do
		{
			if (!pTile)
				return;
	
			GsQuadKey key(pTile->Level(), pTile->Row(), pTile->Col());
			GsImagePtr ptrImg = pDisplay->Canvas()->CreateImage(pTile->TileDataPtr(), pTile->TileDataLength());
			if (ptrImg)
			{
				//计算瓦片的范围。
				pTileClass->Pyramid()->TileExtent(pTile->Level(), pTile->Row(), pTile->Col(), &box.XMin, &box.YMin, &box.XMax, &box.YMax);
				GsRectF screen = CeilRect(ptrDT->FromMap(box));
				pDisplay->Canvas()->DrawImage(ptrImg, rect, screen);
			}
		} while (pTileCursor->Next(pTile));
	}
	
	GsRectF CeilRect(const GsRectF& screen)
	{
		GsRectF ret = screen;
		float w = screen.Width();
		float h = screen.Height();
		ret.Left = floor(screen.Left);
		ret.Top = floor(screen.Top);
		ret.Right = ceil(ret.Left + w + 1);
		ret.Bottom = ceil(ret.Top + h + 1);
		return ret;
	}