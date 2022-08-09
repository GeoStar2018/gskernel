GIS内核-根据属性字段融合面

注意这里是融合, 并不是合并
```c++

        GsShpGeoDatabaseFactoryPtr shp = new GsShpGeoDatabaseFactory();
	GsConnectProperty conn;
	conn.Server = u8"D:\\testdata\\罗湖测试";
	GsGeoDatabasePtr pDB = shp->Open(conn);
	GsFeatureClassPtr pFcs = pDB->OpenFeatureClass(u8"罗湖测试");
	//0.0002 根据pro显示数据大致测量出来得房屋之间得间隔
	GsPathUnionBoundaryPtr ptrUnxx = new GsPathUnionBoundary(0.0003); ptrUnxx->Begin();
	GsPathUnionBoundaryPtr ptrUnwj = new GsPathUnionBoundary(0.0004); ptrUnwj->Begin();
	GsPathUnionBoundaryPtr ptrUnxn = new GsPathUnionBoundary(0.0005); ptrUnxn->Begin();
	GsFeatureCursorPtr pCursor = pFcs->Search();
	GsFeaturePtr ptrFea = pCursor->Next();
	int FieldIndex = pFcs->Fields().FindField(u8"SQMC");
	do
	{
		if (!ptrFea)
			break;
		if (!ptrFea->Geometry())
			break;
		GsString str = ptrFea->ValueString(FieldIndex);
		if (GsStringHelp::Compare(str, u8"向西") == 0)
		{
			ptrUnxx->AddOneGeometry(ptrFea->Geometry()->Clone());
		}
		else if (GsStringHelp::Compare(str, u8"文锦") == 0)
		{
			ptrUnwj->AddOneGeometry(ptrFea->Geometry()->Clone());
		}
		else if (GsStringHelp::Compare(str, u8"新南") == 0)
		{
			ptrUnxn->AddOneGeometry(ptrFea->Geometry()->Clone());
		}

	} while (pCursor->Next(ptrFea));

	GsGeometryPtr pGeoxx = ptrUnxx->End();
	GsGeometryPtr pGeowj = ptrUnwj->End();
	GsGeometryPtr pGeoxn = ptrUnxn->End();

	GsFeatureClassPtr ptrFcsOut = pDB->CreateFeatureClass(u8"合并小区", pFcs->Fields(), pFcs->GeometryColumnInfo(), pFcs->SpatialReference());
	int FieldIndexOut = ptrFcsOut->Fields().FindField(u8"SQMC");
	ptrFcsOut->Transaction()->StartTransaction();
	GsFeaturePtr pFeaOut = ptrFcsOut->CreateFeature();
	pFeaOut->Geometry(pGeoxx);
	pFeaOut->OID(-1);
	pFeaOut->Value(FieldIndexOut, u8"向西");
	pFeaOut->Store();


	pFeaOut->Geometry(pGeowj);
	pFeaOut->OID(-1);
	pFeaOut->Value(FieldIndexOut, u8"文锦");
	pFeaOut->Store();

	pFeaOut->Geometry(pGeoxn);
	pFeaOut->OID(-1);
	pFeaOut->Value(FieldIndexOut, u8"新南");
	pFeaOut->Store();
	ptrFcsOut->Transaction()->CommitTransaction();
```



