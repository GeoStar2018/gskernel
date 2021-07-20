GIS内核-重采样栅格数据 [根据宽高重采样]

```c++
std::string Resample(const char * t1,const char * t2)
{
	GsFile ft1(t1), ft2(t2);
	if (!ft1.Exists() || !ft2.Exists())
		return "";
	GsRasterPtr ptrTif = new GsRaster;
	GeoStar::Kernel::GsFileGeoDatabaseFactory vFac;
	GeoStar::Kernel::GsConnectProperty vConn;

	vConn.Server = ft1.Parent().Path();
	GeoStar::Kernel::GsGeoDatabasePtr ptrDB = vFac.Open(vConn);
	GsRasterClassPtr ptrRasterClass1 = ptrDB->OpenRasterClass(ft1.Name());
	vConn.Server = ft1.Parent().Path();
	GeoStar::Kernel::GsGeoDatabasePtr ptrDB2 = vFac.Open(vConn);
	GsRasterClassPtr ptrRasterClass2 = ptrDB2->OpenRasterClass(ft2.Name());

	if(ptrRasterClass1->RasterColumnInfo().Width == ptrRasterClass2->RasterColumnInfo().Width &&
		ptrRasterClass1->RasterColumnInfo().Height == ptrRasterClass2->RasterColumnInfo().Height)
		return (char*)t2;
	GsOStringStream oss;
	oss << ft2.Name(false) << ptrRasterClass1->RasterColumnInfo().Width << "_" << ptrRasterClass2->RasterColumnInfo().Height << ".tif"  ;
	std::string newname = GsFileSystem::Combine(ft2.Parent().FullPath(), oss.str().c_str());


	//计算写出的像素范围
	GsRasterColumnInfo info = ptrRasterClass1->RasterColumnInfo();
	double srcd[6] = { 0 };
	memcpy(srcd, ptrRasterClass1->RasterColumnInfo().GeoTransform, 6 * sizeof(double));
	double srcCell = srcd[1];
	int srcW = info.Width;
	int srcH = info.Height;
	double xyCellw =  ptrRasterClass2->Extent().Width()/srcW;
	double xyCellh = ptrRasterClass2->Extent().Height()/srcH;
	srcd[1] = xyCellw;
	srcd[5] = -xyCellh;
	GsRasterColumnInfo columnInfo = info;
	memcpy(columnInfo.GeoTransform, (srcd), 48);
	columnInfo.Width = info.Width;;
	columnInfo.Height = info.Height;
	GsFile newn(newname.c_str());
	GsRasterClassPtr rasterClassDst = ptrDB->CreateRasterClass(newn.Name(),
		GsRasterCreateableFormat::eGTiff, columnInfo, ptrRasterClass1->SpatialReference());

	GsBox boundary = ptrRasterClass2->Extent();
	GsRect rect = ptrRasterClass2->ExtentToRange(boundary);
	GsRasterCursorPtr cursor = ptrRasterClass2->Search(rect,  GsSize(info.Width, info.Height), GsRasterResampleAlg::eNearestNeighbour);
	GsRasterPtr ptrRaster = new GsRaster();
	while (cursor->Next(ptrRaster)) {
		rasterClassDst->WriteRaster(ptrRaster);
	}
	return newname;
}
```



