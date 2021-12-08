GIS内核-地形瓦片数据集和tif文件数据集 计算坡度后按照坡度范围渲染

```c++

//将一个地形瓦片数据集通过坡度处理分类渲染成影像瓦片
GS_TEST(GsTileProcessor, GsGridAnalysisTileProcessor, chijing, 20210911)
{

	
	// 打开参考的classtile
	GsConnectProperty vConn;
	vConn.Server = MakeInputFolder("rasteranalysis");
	vConn.DataSourceType = GsDataSourceType::eSqliteFile;
	GsSqliteGeoDatabaseFactory pFac;
	GsGeoDatabasePtr ptrDatabase = pFac.Open(vConn);
	GsTileClassPtr ptrSrcTileCls = ptrDatabase->OpenTileClass("tileDEM.tile");
	if (!ptrSrcTileCls)
		return;
	GsConnectProperty vConn1;
	vConn1.Server = MakeInputFolder("rasteranalysis");
	vConn1.DataSourceType = GsDataSourceType::eSqliteFile;
	GsSqliteGeoDatabaseFactory pFac2;
	GsGeoDatabasePtr ptrDatabase2 = pFac2.Open(vConn1);
	GsTileClassPtr ptrTagTileCls = ptrDatabase2->OpenTileClass("dem2img.tile");
	if (ptrTagTileCls)
		ptrTagTileCls->Delete();
	GsTileColumnInfo colInfo =  ptrSrcTileCls->TileColumnInfo();
	colInfo.FeatureType = eImageTileFeature;
	ptrTagTileCls = ptrDatabase2->CreateTileClass("dem2img.tile", ptrSrcTileCls->SpatialReference(), ptrSrcTileCls->Pyramid(), colInfo);
	ASSERT_TRUE(!!ptrTagTileCls);

	GsVector<GsTileClassPtr> vec;
	vec.push_back(ptrSrcTileCls);
	GsTileCursorPtr ptrCursor = ptrSrcTileCls->Search();
	GsTilePtr ptrSrcTile = ptrCursor->Next();
	GsTilePtr ptrTagTile = ptrTagTileCls->CreateTile();

	GsPyramidPtr ptrPyramid = ptrSrcTileCls->Pyramid();
	////自定义组合的瓦片处理组合
	GsTileProcessorCollectionPtr ptrColl = new GsTileProcessorCollection();

	//坡度计算
	GsSlopeGridrAnalysisTileProcessorPtr ptrSlope = new GsSlopeGridrAnalysisTileProcessor();
	ptrSlope->Pyramid(ptrPyramid);
	GsGridAnalysisTileProcessorContextPtr ptrContext = new GsGridAnalysisTileProcessorContext();
	GsString demV = (ptrSrcTileCls->MetadataItem("DEM", "DEMValueType"));
	float InvalidValue = (GsCRT::_atof(ptrSrcTileCls->MetadataItem("DEM", "InvalidValue")));
	ptrContext->OutputTileType(GsTileEncodingType::ePngType);
	ptrContext->NoDataValue(InvalidValue);
	//坡度配色
	GsDataRendererGridrAnalysisTileProcessorPtr ptrDataRenderer = new GsDataRendererGridrAnalysisTileProcessor();
	ptrDataRenderer->Pyramid(ptrPyramid);
	ptrDataRenderer->RasterDataMappingType(GsRasterDataMappingType::eInterval);
	ptrDataRenderer->MappingValues()->push_back(GsColor::Green);
	ptrDataRenderer->MappingValues()->push_back(GsColor::Yellow);
	ptrDataRenderer->MappingValues()->push_back(GsColor::Red);

	ptrDataRenderer->OriginalValues()->push_back(0);
	ptrDataRenderer->OriginalValues()->push_back(GsMath::ToRadian(30));
	ptrDataRenderer->OriginalValues()->push_back(GsMath::ToRadian(45));
	ptrDataRenderer->OriginalValues()->push_back(GsMath::ToRadian(90));
	ptrColl->Add(ptrSlope);
	ptrColl->Add(ptrDataRenderer);

	
	ptrTagTileCls->Transaction()->StartTransaction();
	int nCount = 0;
	do
	{
		if (!ptrSrcTile)
			break;

		ptrContext->DataType(demV == "int16" ? GsRasterDataType::eInt16RDT : GsRasterDataType::eFloat32RDT);
		ptrContext->OutputSize(GsSize(ptrPyramid->TileSizeX, ptrPyramid->TileSizeY));
		if (!(ptrContext->DecodeFromTile(ptrSrcTile)
			&&
			ptrColl->Processing(ptrContext)))
			continue;

		if (ptrContext->EncodeToTile(ptrTagTile))
			ptrTagTile->Store();
		nCount++;
		if (nCount > 2000)
		{
			ptrTagTileCls->Transaction()->CommitTransaction();
			ptrTagTileCls->Transaction()->StartTransaction();
			nCount = 0;
		}
	} while (ptrCursor->Next(ptrSrcTile));
	ptrTagTileCls->Transaction()->CommitTransaction();
}

struct ImageSpliter
{
	int m_Width = 150, m_Height =150, m_nPixelByteSize =1;
	GsRasterDataType m_type = eByteRDT;
	double m_dblNodata = 0;;
	GsGrowByteBuffer m_BufferNoData;// 保存Image的空白或无效内存，方便将m_Buffer直接初始化
	bool m_isInit = false;
	ImageSpliter(int w, int h,int pbs, GsRasterDataType type, double nodata)
		:m_Width(w),m_Height(h),m_nPixelByteSize(pbs),m_type(type),m_dblNodata(nodata)
	{
		Init();
	}

	void Init()
	{
		if (m_isInit)
			return;
		m_BufferNoData.Allocate(m_Width * m_Width * m_nPixelByteSize);
		InitBufferNoData(m_dblNodata, m_type);
	}
	template<class T>
	void BufferSetNoDateValue(GsGrowByteBuffer* pBuffer, T dNodate)
	{
		if (!pBuffer)
			return;
		T* pBit = (T*)pBuffer->BufferHead();
		int nCount = pBuffer->BufferSize();
		nCount = nCount / sizeof(T);
		for (int i = 0; i < nCount; i++)
		{
			pBit[i] = dNodate;
		}
	}
	void InitBufferNoData(double dNoDate, GsRasterDataType dt)
	{
		if (dt == eByteRDT)
			BufferSetNoDateValue<unsigned char>(&m_BufferNoData, (unsigned char)dNoDate);
		else if (dt == eUInt16RDT)
			BufferSetNoDateValue<unsigned short>(&m_BufferNoData, (unsigned short)dNoDate);
		else if (dt == eInt16RDT)
			BufferSetNoDateValue<short>(&m_BufferNoData, (short)dNoDate);
		else if (dt == eUInt32RDT)
			BufferSetNoDateValue<unsigned int>(&m_BufferNoData, (unsigned int)dNoDate);
		else if (dt == eInt32RDT)
			BufferSetNoDateValue<int>(&m_BufferNoData, (int)dNoDate);
		else if (dt == eFloat32RDT)
			BufferSetNoDateValue<float>(&m_BufferNoData, (float)dNoDate);
		else if (dt == eFloat64RDT)
			BufferSetNoDateValue<double>(&m_BufferNoData, (double)dNoDate);
		else
			BufferSetNoDateValue<short>(&m_BufferNoData, (short)dNoDate);
	}


	void GetOneTileStartmemRC(double dblRes,GsPyramid* ptrPyramid, int L, int R, int C, GsRaster* ptrOneRastrer,
		int & InTileStartR, int &inTileStartC)
	{
		GsBox TileBox;
		ptrPyramid->TileExtent(L, R, C, &TileBox.XMin, &TileBox.YMin, &TileBox.XMax, &TileBox.YMax);
		//box只有XMin 和YMax是对得
		GsBox box = ptrOneRastrer->RasterClass()->RangeToExtent(GsRect(ptrOneRastrer->OffsetX(), ptrOneRastrer->OffsetY(), ptrOneRastrer->Width(), ptrOneRastrer->Height()));
		double dx = box.XMin - TileBox.XMin;
		double dy = TileBox.YMax - box.YMax;
		InTileStartR = fabs(dx / dblRes);
		inTileStartC = fabs(dy / dblRes);
	}

	bool QueryLRCData(GsPyramid* ptrPyramid, int L, int R, int C, GsRasterClass *ptrRaster, GsGrowByteBuffer*buffer)
	{
		int tilesize = m_Width;
		GsBox box;
		ptrPyramid->TileExtent(L, R, C, &box.XMin,&box.YMin, &box.XMax, &box.YMax);
		GsRect rt = ptrRaster->ExtentToRange(box);
		GsRasterCursorPtr ptrCursor = ptrRaster->Search(rt, GsSize(tilesize, tilesize), GsRasterResampleAlg::eBilinear);
		//当前级别的分辨率。
		double dblRes = ptrPyramid->TileSpanX(L, 0, 0) / tilesize;//地形可能是150;
		//需要分辨率是因为Search得到的数据不包含无效范围数据, 比如半个瓦片那么需要做平移
		GsRasterDataType type = ptrRaster->RasterBand(0)->BandDataType();
		int pixelsize = ptrRaster->RasterBand(0)->RasterDataTypeBitSize(type) / 8;

		buffer->Copy(m_BufferNoData.BufferHead(), m_BufferNoData.BufferSize());

		GsRasterPtr ptrOneRastre = new GsRaster();

		unsigned char* data = buffer->BufferHead();

		//内存分配不出来
		if (!data)
			return  false;
		unsigned char* ptmpdata = data;
		int InTileStartR = 0, inTileStartC = 0;
		while (ptrCursor->Next(ptrOneRastre))
		{
			GetOneTileStartmemRC(dblRes, ptrPyramid, L, R, C, ptrOneRastre, InTileStartR, inTileStartC);
			unsigned char* tmp = ptrOneRastre->DataPtr();
			//Seacrh出来的size有可能大于tilesize 1
			int rasterW = ptrOneRastre->Width(), rasterH = ptrOneRastre->Height();
			if (ptrOneRastre->Width() > tilesize)
			{
				rasterW = tilesize;
			}
			if (ptrOneRastre->Height() > tilesize)
			{
				rasterH = tilesize;
			}
			for (int i = 0; i < rasterH; i++)
			{
				unsigned char* beginHead = ptmpdata + InTileStartR* pixelsize + (inTileStartC+i) * tilesize * pixelsize;
				memcpy(beginHead, tmp + i * ptrOneRastre->Width()*pixelsize, rasterW * pixelsize);
			}
		}


		return true;
	}

};

void SaveDem2PNG(int l, int r, int c,unsigned char*ptmpdata)
{
	GsSimpleBitmapPtr ptrTmpImg = new GsSimpleBitmap(150, 150);
	auto g = (GsColor*)ptrTmpImg->Bit();
	//0-3780
	short* pd = (short*)ptmpdata;
	for (int i = 0; i < 150*150; i++)
	{
		if(pd[i] <0 || pd[i] > 3780)
			g[i] = GsColor(0, 0, 0, 0);
		else {
			unsigned char v = 255.000 * (1.000*pd[i] / 3780);
			g[i] = GsColor(v, v, v);
		}
	}
	GsStringStream oss;
	oss << "D:\\temp\\tiff\\" << l << "_" << r << "_" << c << ".png" ;
	ptrTmpImg->SavePNG(oss.str().c_str());
}

//将一个GsRasterClass数据集通过坡度处理分类渲染成影像瓦片
GS_TEST(GsTileProcessor, GsGridAnalysisTileProcessor_tiff, chijing, 20210911)
{
	//return;//还未通过
	// 打开参考的classtile
	GsConnectProperty vConn;
	vConn.Server = MakeInputFolder("rasteranalysis");
	vConn.DataSourceType = GsDataSourceType::eFile;
	GsFileGeoDatabaseFactory pFac;
	GsGeoDatabasePtr ptrDatabase = pFac.Open(vConn);
	GsRasterClassPtr ptrRaster = ptrDatabase->OpenRasterClass("srtm_61_08.tif");
	if (!ptrRaster)
		return;
	GsConnectProperty vConn1;
	vConn1.Server = MakeInputFolder("rasteranalysis");
	vConn1.DataSourceType = GsDataSourceType::eSqliteFile;
	GsSqliteGeoDatabaseFactory pFac2;
	GsGeoDatabasePtr ptrDatabase2 = pFac2.Open(vConn1);
	GsTileClassPtr ptrTagTileCls = ptrDatabase2->OpenTileClass("demtif2img.tile");
	if (ptrTagTileCls)
		ptrTagTileCls->Delete();
	GsTileColumnInfo colInfo;
	int nLevel = 10;
	colInfo.ValidTopLevel = nLevel;
	colInfo.ValidBottomLevel = nLevel;
	colInfo.FeatureType = eImageTileFeature;
	//colInfo.XYDomain = ptrRaster->Extent();
	GsPyramidPtr ptrPyramid = GsPyramid::WellknownPyramid(GsWellknownPyramid:: e360DegreePyramid);
	ptrTagTileCls = ptrDatabase2->CreateTileClass("demtif2img.tile", ptrRaster->SpatialReference(), ptrPyramid, colInfo);
	ASSERT_TRUE(!!ptrTagTileCls);

	ptrTagTileCls->MetadataItem("DEM", "DEMValueType", "int16");//值域 int16 或者flaot32 );
	ptrTagTileCls->MetadataItem("DEM", "InvalidValue",GsStringHelp::ToString(-32768));

	GsBox box =  ptrRaster->Extent();
	int minrow, mincol, maxrow, maxcol;
	ptrPyramid->TileIndexRange(box.XMin, box.YMin, box.XMax, box.YMax, nLevel,&minrow,&mincol,&maxrow, &maxcol);


	////自定义组合的瓦片处理组合
	GsTileProcessorCollectionPtr ptrColl = new GsTileProcessorCollection();
	//坡度计算
	GsSlopeGridrAnalysisTileProcessorPtr ptrSlope = new GsSlopeGridrAnalysisTileProcessor();
	ptrSlope->SpatialRefeence(ptrRaster->SpatialReference());
	ptrSlope->Pyramid(ptrPyramid);
	GsGridAnalysisTileProcessorContextPtr ptrContext = new GsGridAnalysisTileProcessorContext();
	auto gasd =  ptrTagTileCls->MetadataDomain();
	GsString demV = ptrTagTileCls->MetadataItem("DEM", "DEMValueType");
	float InvalidValue = (GsCRT::_atof(ptrTagTileCls->MetadataItem("DEM", "InvalidValue")));//值域 double
	ptrContext->OutputTileType(GsTileEncodingType::ePngType);
	ptrContext->NoDataValue(InvalidValue);
	//坡度配色
	GsDataRendererGridrAnalysisTileProcessorPtr ptrDataRenderer = new GsDataRendererGridrAnalysisTileProcessor();
	ptrDataRenderer->Pyramid(ptrPyramid);
	ptrDataRenderer->RasterDataMappingType(GsRasterDataMappingType::eInterval);
	ptrDataRenderer->MappingValues()->push_back(GsColor(GsColor::Green));
	ptrDataRenderer->MappingValues()->push_back(GsColor(GsColor::Yellow));
	ptrDataRenderer->MappingValues()->push_back(GsColor(GsColor::Blue));
	ptrDataRenderer->MappingValues()->push_back(GsColor(GsColor::Red));

	ptrDataRenderer->OriginalValues()->push_back(0);
	ptrDataRenderer->OriginalValues()->push_back(GsMath::ToRadian(30));
	ptrDataRenderer->OriginalValues()->push_back(GsMath::ToRadian(35));
	ptrDataRenderer->OriginalValues()->push_back(GsMath::ToRadian(45));
	ptrDataRenderer->OriginalValues()->push_back(GsMath::ToRadian(90));
	ptrColl->Add(ptrSlope);
	ptrColl->Add(ptrDataRenderer);

	GsGrowByteBuffer tmpbuff;
	GsTilePtr ptrTagTile = ptrTagTileCls->CreateTile();
	ptrTagTileCls->Transaction()->StartTransaction();
	int nCount = 0;
	bool isDem = false;
	int tilesize = 256;
	if (ptrRaster->BandCount() == 1)
	{
		isDem = true;
		tilesize = 256;
	}		
	double dblRes = ptrPyramid->TileSpanX(nLevel, 0, 0) / tilesize;//地形可能是150;
	GsRasterDataType type = ptrRaster->RasterBand(0)->BandDataType();
	int pixelsize = ptrRaster->RasterBand(0)->RasterDataTypeBitSize(type) / 8;
	double dblNodata = ptrRaster->RasterBand(0)->NoDataValue();
	ImageSpliter pImageSpliter(tilesize, tilesize, pixelsize, type, dblNodata);
	pImageSpliter.Init();
	for(int i = minrow; i < maxrow; i++)
		for (int j = mincol; j < maxcol; j++)
		{
			tmpbuff.Clear();
			pImageSpliter.QueryLRCData(ptrPyramid, nLevel, i, j, ptrRaster, &tmpbuff);
			//SaveDem2PNG(nLevel, i, j, tmpbuff.BufferHead());

			//压根就是原始数据,暂时不增加对象直接用
			ptrTagTile->TileType(GsTileEncodingType::eUnKnownType);
			ptrTagTile->Level(nLevel);
			ptrTagTile->Row(i);
			ptrTagTile->Col(j);
			ptrTagTile->TileData(tmpbuff.BufferHead(), tmpbuff.BufferSize());

			if (!ptrTagTile)
				break;
			//png可以具备透明,jpg不具备
			ptrContext->OutputTileType(GsTileEncodingType::ePngType);
			ptrContext->NoDataValue(InvalidValue);
			//DataType 会在中间处理过程中改掉数据 比如转为坡度可能从short->float .
			ptrContext->DataType(ptrRaster->RasterBand(0)->BandDataType());
			ptrContext->OutputSize(GsSize(ptrPyramid->TileSizeX, ptrPyramid->TileSizeY));
			ptrContext->Width(tilesize);
			ptrContext->Height(tilesize);
			if (!(ptrContext->DecodeFromTile(ptrTagTile)
				&&
				ptrColl->Processing(ptrContext)))
				continue;

			if (ptrContext->EncodeToTile(ptrTagTile))
				ptrTagTile->Store();
			nCount++;
			if (nCount > 2000)
			{
				ptrTagTileCls->Transaction()->CommitTransaction();
				ptrTagTileCls->Transaction()->StartTransaction();
				nCount = 0;
			}
		}
	ptrTagTileCls->Transaction()->CommitTransaction();
}
```

