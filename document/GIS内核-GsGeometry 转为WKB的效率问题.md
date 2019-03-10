GIS内核-GsGeometry 转为WKB的效率问题

因为之前做WKB转换会检查几何的正确性, 这对于复杂几何耗时较长(几秒). 只适操作单个地物,应用场景如: 图上绘制很复杂的几何,想要转为WKB做数据交换给其他流程处理, 这种就可能必须做几何正确检查, 因为编辑不可能绘制完全合法的几何.
另一种应用场景, 我有一堆数据,我这个数据就是生产来的,已经是完整的数据,我想要把此数据直接转为wkb,这时候的应用场景是大批量数据, 这时候就不需要做检查,或者说检查已经没有那么重要

下面的例子演示了,将一个FCS数据内的几何读出来 ,然后写为WKB ,下一步又变为几何对象, 最后存到数据库中,经过效率对比, 98%的时间将画在几何检查, 如果把Simple检查设置false 将大幅度提升写入性能!

GS_TEST(WkbRead,ReadWriterMultiPolygon2,chijing,20150707)
{ 
	this->RecordProperty("readme","利用wkbread读入有两个外圈一个内圈组成的mutipolygon");
	GsConnectProperty conn;
	conn.DataSourceType = eSqliteFile;
	conn.Server = "C:\\Users\\chijing\\Desktop";
	GsSqliteGeoDatabaseFactory obj;
	GsGeoDatabasePtr ptrDB = obj.Open(conn);
	GsFeatureClassPtr feaclass = ptrDB->OpenFeatureClass("WORLD");

	GsFeatureCursorPtr ptrCursor = feaclass->Search();
	GsFeaturePtr ptrFea = ptrCursor->Next();
	GsGrowByteBuffer buff;
	GsWKBOGCWriter w(&buff);
	GsWKBOGCReader r;
	w.Simple(false);
	GsFeatureClassPtr pdstFeacls =  ptrDB->CreateFeatureClass("WORLD43", feaclass->Fields(), feaclass->GeometryColumnInfo(), feaclass->SpatialReference());
	pdstFeacls->CreateSpatialIndex();
	GsFeaturePtr ptrdstFea =  pdstFeacls->CreateFeature();

	pdstFeacls->Transaction()->StartTransaction();
	int nCOunt = 0;
	do
	{
		w.Reset();
		
		w.Write(ptrFea->Geometry());
		r.Begin(buff.BufferHead(), buff.BufferSize());
		GsGeometryPtr ptmp = r.Read();
		nCOunt++;
		if (nCOunt >= 1000)
		{
			pdstFeacls->Transaction()->CommitTransaction();
			pdstFeacls->Transaction()->StartTransaction();
		}
		ptrdstFea->OID(-1);
		ptrdstFea->Geometry(ptmp);
		ptrdstFea->Store();
	} while (ptrCursor->Next(ptrFea));
	pdstFeacls->Transaction()->CommitTransaction();
	pdstFeacls->CreateSpatialIndex();
}