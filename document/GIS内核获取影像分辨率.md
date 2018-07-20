GIS 内核如何获取影像的分辨率

    void GetImgRes(const char* strFile)
	{
	
		GsFileGeoDatabaseFactoryPtr fcsFac = new GsFileGeoDatabaseFactory();
		GsGeoDatabaseFactoryPtr fac = fcsFac;
		GsConnectProperty cp;
		cp.DataSourceType = GsDataSourceType::eFile;
	
		GsString str = strFile;
		GsFile vmpFile(str.c_str());
		GsDir  dir = vmpFile.Parent();
		cp.Server = dir.FullPath();
		if (vmpFile.Exists())
		{
			GsGeoDatabasePtr db = fac->Open(cp);
			GsRasterClassPtr pRaster = db->OpenRasterClass(vmpFile.Name().c_str());
			GsRasterColumnInfo pInfo = pRaster->RasterColumnInfo();
			//分辨率
			double xd = pInfo.XYDomain.Width() / pInfo.Width;
			double yd = pInfo.XYDomain.Height() / pInfo.Height;
		}
	}