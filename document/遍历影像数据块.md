	     
	void ReadRasterClass()
	{
		GsFileGeoDatabaseFactoryPtr pfac = new GsFileGeoDatabaseFactory();
		GsConnectProperty conn;
		conn.Server = MakeInputFile("tif");//tif文件目录
		GsGeoDatabasePtr ptrDb = pfac->Open(conn);
		GsRasterClassPtr ptrRaster= ptrDb->OpenRasterClass("8bit");//打开一个叫8bit的文件8bit.tif
	 
		//如果金字塔不存在则创建金字塔
		if(!ptrRaster->ExistsPyramid())
		bool bCreateSuccess = ptrRaster->CreatePyramid(GsRasterResampleAlg::eAverage, 4);
	 
		//屏幕显示的地理空间范围
		GsBox box(100,34,102,36);
		//计算空间范围在栅格类上的像素范围。
		GsRect rect = ptrRaster->ExtentToRange(box);
	 
		//要显示的设备的范围。
		GsSize s;//pDisplay->DisplayTransformation()->DeviceExtent().Size();
	 
		//查询需要操作的数据
		GsRasterCursorPtr ptrCursor = ptrRaster->Search(rect, s, eNearestNeighbour);
	 
		std::shared_ptr< GsColorToRGBA> m_ptrTrans(new GsColorToRGBA());
		Utility::GsSimpleBitmapPtr m_ptrImage;
		GsRasterPtr ptrRasterData = new GsRaster();
		while (ptrCursor->Next(ptrRasterData))
		{
			if (m_ptrImage)
			{
				if (m_ptrImage->Width() < ptrRaster->Width() ||
					m_ptrImage->Height() <ptrRaster->Height())
					m_ptrImage.Release();
			}
			if (!m_ptrImage)
				m_ptrImage = new Utility::GsSimpleBitmap(ptrRaster->Width(), ptrRaster->Height(), 32);
			memset((void*)m_ptrImage->Bit(), 0x00, m_ptrImage->Stride() * m_ptrImage->Height());
			//采样一块数据到image
			m_ptrTrans->Translate(ptrRasterData, m_ptrImage);
			//下面拿image 做其他事情, 比如绘制. 
	 
			//拿ptrRasterData对应的地理范围计算需要贴图的 屏幕范围
			//GsBox box = ptrRasterClass->RangeToExtent(Utility::GsRect(ptrRaster->OffsetX(), ptrRaster->OffsetY(), ptrRaster->Width(), ptrRaster->Height()));
			//Utility::GsPT pt;
			//pDisplay->DisplayTransformation()->FromMap(box.XMin, box.YMax, pt.X, pt.Y);
	 
			//贴图
			//ptrCanvas->DrawImage(m_ptrImage, pt);
		}
	}
