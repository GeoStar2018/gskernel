GIS查询某一点的高程值
	
	/// \brief 获取查询到的地形块的高程值
	/// \param GsRasterClass* pRaster, 数据集
	/// \param double pCoord[2]点
	/// \return double 
	double QueryDem(GsRasterClass* pRaster, double pCoord[2])
	{
		int pixelBytes = 0;
		for (int i = 0; i < pRaster->BandCount(); i++)
			pixelBytes += GsRasterBand::RasterDataTypeBitSize(pRaster->RasterBand(i)->BandDataType());
		pixelBytes /= 8;
	
		GsRasterDataType eType = pRaster->RasterBand(pRaster->BandCount() - 1)->BandDataType();
	
		GsRaster raster;
		GsRect rect(0, 0, pRaster->Width(), pRaster->Height());
		GsRasterCursorPtr ptrCursor = pRaster->Search(rect);
	
		//这里只查一个点,多个点可以循环查
		while (ptrCursor->Next(&raster))
		{
			rect = GsRect(raster.OffsetX(), raster.OffsetY(), raster.Width(), raster.Height());
			GsBox box = pRaster->RangeToExtent(rect);
			double res = box.Width() / rect.Width();
			//获取分辨率
			double val = ComputerValue(pCoord, &raster, box, res, pixelBytes, eType);
			return val;
		}
		return 0.0;
	}
	/// \brief 获取查询到的地形块的高程值
	/// \param const double* pHead,  pHead[0] ->x, pHead[1]->y
	/// \param GsRaster* raster GsRasterClass 查询出来的高程数据
	/// \param GsBox& box 数据对应的地理范围
	/// \param double res 当前raster分辨率
	/// \param int pixelBytes,
	/// \param GsRasterDataType GsRasterClass数据类型
	double ComputerValue(const double* pHead, GsRaster* raster, GsBox& box,double res,int pixelBytes,GsRasterDataType eType)
	{
		int x = (pHead[0] - box.XMin) / res ;
		int y = (box.YMax - pHead[1]) / res ;
		unsigned char* pCoord = raster->DataPtr() + (y * raster->Width() + x) * pixelBytes;
		switch (eType)
		{
		case eByteRDT ://= 1,
			return pCoord[0];
			break;
			// \brief 16位无符号整数
			case eUInt16RDT:// = 2,
				return ((unsigned short*)pCoord)[0];
				break;
			// \brief 16位整数
			case eInt16RDT://= 3,
				return ((short*)pCoord)[0];
				break;
			// \brief 32位无符号整数
			case eUInt32RDT:// = 4,
				return ((unsigned int*)pCoord)[0];
				break;
			// \brief 32位整数
			case eInt32RDT:// = 5,
				return ((int*)pCoord)[0];
				break;
			// \brief 32位单精度浮点数
			case eFloat32RDT:// = 6,
				return ((float*)pCoord)[0];
				break;
			// \brief 64位双精度浮点数
			case eFloat64RDT:// = 7,
				return ((double*)pCoord)[0];
				break;
		}
		return -9999;
	}