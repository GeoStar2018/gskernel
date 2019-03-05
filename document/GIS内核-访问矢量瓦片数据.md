GIS内核-访问矢量瓦片数据

	#include "preconfig.h"
	#include "utility.h"
	#include "kernel.h"
	#include "mbvectortile.h"


	void VectorTileRead()
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
		GeoStar::Kernel::GsZipMBVectorTile tilepaser;
		do
		{
			if (!pTile)
				return;
			tilepaser.Clear();
			GsQuadKey key(pTile->Level(), pTile->Row(), pTile->Col());
			tilepaser.Parse(pTile->TileDataPtr(), pTile->TileDataLength());
			GsBox tmpbox;
			pTileClass->Pyramid()->TileExtent(pTile->Level(), pTile->Row(), pTile->Col(), &tmpbox.XMin, &tmpbox.YMin, &tmpbox.XMax, &tmpbox.YMax);
	
	
			bool tp_b = tilepaser.ExistLayer("BOUS2_4M_S");
			if (tp_b)//存在此图层
			{
				GeoStar::Kernel::GsMBLayer & pLayer = tilepaser.QueryLayer("BOUS2_4M_S");
				//所有列
				UTILITY_NAME::GsVector<UTILITY_NAME::GsString>  Fields = pLayer.Keys;
				for (int i = 0; i < pLayer.Features.size(); i++)
				{
					GsMBFeature& fea = pLayer.Features[i];
					//得到几何
					GsGeometryPtr ptrGeo = fea.EnsureGeometry(tmpbox);
					//属性
					for (int j = 0; j < fea.Tags.size() / 2; j += 2)
					{
						GsAny& any = fea.Value("Name");
					}
				}
			}
	
		} while (pTileCursor->Next(pTile));
	}