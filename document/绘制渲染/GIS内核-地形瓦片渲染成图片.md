GIS内核-地形瓦片渲染成图片

```c++


//将一个GsRasterClass数据集通过坡度处理分类渲染成影像瓦片
GS_TEST(GsTileProcessor, GsDataRendererGridrAnalysisTileProcessor, chijing, 20210911)
{

	return;
	GsPyramidPtr gsPyramid = new GsMultiPyramid();
	GsTileProcessorCollectionPtr collection = 0;
	GsGridAnalysisTileProcessorContextPtr context = 0;
	GsTilePtr srcTile = 0;
	GsTilePtr targTile = 0;
	GsDataRendererGridrAnalysisTileProcessorPtr dataRendererProcessor = 0;
	collection = new GsTileProcessorCollection();
	// 坡度配色
	dataRendererProcessor = new GsDataRendererGridrAnalysisTileProcessor();
	dataRendererProcessor->Pyramid(gsPyramid);
	dataRendererProcessor->RasterDataMappingType(GsRasterDataMappingType::eInterval);
	std::vector<unsigned int> colors = {
			0xffff00ff, 0xfffa00fa, 0xff00ff72, 0xff722323, 0xff5e0e0e
	};
	std::vector<int> intv = {
		0, 10, 30, 50, 100, 200
	};
	auto mvs = dataRendererProcessor->MappingValues();
	auto ovs = dataRendererProcessor->OriginalValues();
	for (int i = 0; i < colors.size(); i++) {
		mvs->push_back(GsColor(colors[i]));
	}
	for (int i = 0; i < intv.size(); i++) {
		ovs->push_back(intv[i]);
	}
	// 添加至工具集合
	collection->Add(dataRendererProcessor);

	context = new GsGridAnalysisTileProcessorContext();
	context->OutputTileType(GsTileEncodingType::ePngType);
	context->NoDataValue(0);


	context->DataType(GsRasterDataType::eByteRDT);
	GsSize ff(256, 256);
	context->OutputSize(ff);

	GsSqliteGeoDatabaseFactoryPtr factory = new GsSqliteGeoDatabaseFactory();
	GsConnectProperty connectProperty;
	
	connectProperty.Server = MakeInputFolder("../testdata/400sqlite");
	auto pdbDatabase = factory->Open(connectProperty);
	auto pClass = pdbDatabase->OpenTileClass("dg_dx_pro.tile");
	srcTile = pClass->Tile(13, 1525, 6684);

	if (!(context->DecodeFromTile(srcTile) && collection->Processing(context)))
	{
		std::cerr << "error ";
	}
	targTile = pClass->CreateTile();
	if (context->EncodeToTile(targTile)) {
		GsImagePtr img = GsImage::LoadFrom(targTile->TileDataPtr(), targTile->TileDataLength());
		std::string strsvg1 = MakeOutputFile("../testdata/400sqlite/dfg","png");
		std::string strsvg2 = MakeOutputFile("../testdata/400sqlite/dfg", "jpg");
		img->SavePNG(strsvg1.c_str());
		img->SaveJPEG(strsvg2.c_str());
	}

}
```

