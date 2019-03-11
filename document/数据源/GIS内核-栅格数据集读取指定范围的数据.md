栅格数据集，读取指定范围的图片数据。
```
void ReadRasterClass()
{
	GsFileGeoDatabaseFactoryPtr pfac = new GsFileGeoDatabaseFactory();
	GsConnectProperty conn;
	conn.Server = MakeInputFile("tif");
	GsGeoDatabasePtr ptrDb = pfac->Open(conn);
	GsRasterClassPtr ptrRass = ptrDb->OpenRasterClass("16bit");
	GsRasterLayerPtr ptrRasterLayer = new GsRasterLayer(ptrRass);
	GsMemoryImageCanvasPtr ptrImager = new GsMemoryImageCanvas(512, 512);
	GsDisplayTransformationPtr pDt = new GsDisplayTransformation(ptrRass->Extent(), GsRect(0, 0, 512, 512));
	GsDisplayPtr pDisplay = new GsDisplay(ptrImager, pDt);
	GsMapPtr pMap = new GsMap(NULL);
	GsTrackCancelPtr ptrTrance = new GsTrackCancel();
	pMap->Layers()->push_back(ptrRasterLayer);
	pMap->Output(pDisplay, ptrTrance);
	std::string strResultPNG = MakeOutputFile("tif", "tif_out_16bit", "png");
	ptrImager->Image()->SavePNG(strResultPNG.c_str());



	double width = 128;
	double height = 128;
	GsRasterCursorPtr pRasterCursor = ptrRass->Search(GsRect(0, 0, width, height));

	//void GsRasterRenderer::DrawCursor(GsRasterCursor* pRasterCursor, GsDisplay* pDisplay, GsTrackCancel* pCancel)
	{
		GsRasterClassPtr ptrRasterClass = ptrRass;
		auto dataType = ptrRasterClass->RasterColumnInfo().DataType;

		GsRasterPtr ptrRaster = new GsRaster();
		GsAggMemoryImageCanvasPtr ptrCanvas_OutPut = new GsAggMemoryImageCanvas(512, 512);
		GsRasterCursorPtr ptrCcursor = pRasterCursor->RasterClass()->Search(Utility::GsRect(50, 50, 178, 178));

		while (ptrCcursor->Next(ptrRaster))
		{

			Utility::GsSimpleBitmapPtr ptrImage(new Utility::GsSimpleBitmap(128, 128, 32));
			memset((void*)ptrImage->Bit(), 0x00, ptrImage->Stride() * ptrImage->Height());


			GsBox box = ptrRasterClass->RangeToExtent(Utility::GsRect(ptrRaster->OffsetX(), ptrRaster->OffsetY(), ptrRaster->Width(), ptrRaster->Height()));

			Utility::GsPT pt;
			pDisplay->DisplayTransformation()->FromMap(box.XMin, box.YMax, pt.X, pt.Y);
			
			//主要做转换的事情，将Raster中的数据转换为RGBA的数据。
			auto Translate = [](GsRasterDataType dataType, GsRaster* pRaster, Utility::GsImage* pImg, Utility::GsPT &offset)
			{
				std::pair<int, int> range[4];

				auto initRange = [](GsRaster* pRaster, int i) {
					auto band = pRaster->RasterClass()->RasterBand(i);
					return std::pair<int, int>(band->Minimum(), band->Maximum());
				};
				range[0] = initRange(pRaster, 0);
				range[1] = initRange(pRaster, 1);
				range[2] = initRange(pRaster, 2);
				range[3] = initRange(pRaster, 3);

				int nBandCount = pRaster->RasterClass()->RasterColumnInfo().BandTypes.size();
				int nStrip = pRaster->Width() * nBandCount;
				if (dataType == eByteRDT)
				{
					unsigned char* pHead = pRaster->DataPtr();
					int b = 2, g = 1, r = 0, a = 3;
					int R = 0, G = 1, B = 2;
					for (int i = 0; i < pRaster->Height(); i++)
					{
						if ((i + offset.Y) >= pImg->Height() || (i + offset.Y) < 0)
							continue;
						unsigned char* pRow = (unsigned char*)pImg->Row(i + offset.Y);
						for (int j = 0; j < pRaster->Width(); j++)
						{
							if ((j + offset.X) >= pImg->Width() || (j + offset.X) < 0)
								continue;
							pRow[(j + offset.X) * 4 + r] = pHead[j * nBandCount + R];
							pRow[(j + offset.X) * 4 + g] = pHead[j * nBandCount + G];
							pRow[(j + offset.X) * 4 + b] = pHead[j * nBandCount + B];
							pRow[(j + offset.X) * 4 + a] = 0xff * (double)1;
						}
						pHead += nStrip;
					}
				}
				else if (dataType == eUInt16RDT || dataType == eInt16RDT)
				{
					unsigned short* pHead = (unsigned short*)pRaster->DataPtr();
					int b = 2, g = 1, r = 0, a = 3;
					int R = 0, G = 1, B = 2;
					auto BandValue = [](unsigned short val, int band, std::pair<int, int> r = std::pair<int, int>(0, 255)) {

						double i = 255.0 * (val - r.first) / (r.second - r.first);
						if (i > 255)
							i = 255;
						unsigned char ch = (unsigned char)i;
						return ch;
					};
					for (int i = 0; i < pRaster->Height(); i++)
					{
						if ((i + offset.Y) >= pImg->Height() || (i + offset.Y) < 0)
							continue;
						unsigned char* pRow = (unsigned char*)pImg->Row(i + offset.Y);
						for (int j = 0; j < pRaster->Width(); j++)
						{
							if ((j + offset.X) >= pImg->Width() || (j + offset.X) < 0)
								continue;


							pRow[(j + offset.X) * 4 + r] = BandValue(pHead[j * nBandCount + R], 0, range[0]);
							pRow[(j + offset.X) * 4 + g] = BandValue(pHead[j * nBandCount + G], 1, range[1]);
							pRow[(j + offset.X) * 4 + b] = BandValue(pHead[j * nBandCount + B], 2, range[2]);
							pRow[(j + offset.X) * 4 + a] = 255;
						}
						pHead += nStrip;
					}

				}
				return true;
			};


			Utility::GsPT ptemp(0, 0);
			//bool bl2 = Translate(eUInt16RDT, ptrRaster, m_ptrImage, ptemp);
			bool bl = Translate(dataType, ptrRaster, ptrImage, ptemp);
			//m_ptrTrans->Translate(ptrRaster, m_ptrImage);
			ptrCanvas_OutPut->DrawImage(ptrImage, pt);
			ptrCanvas_OutPut->Image()->SavePNG("tif_out_16bit_128_128.png");
		}
		exit(0);
	}
}
```