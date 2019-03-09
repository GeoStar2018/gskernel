GIS内核- 几何对象与WKB互相转换
	
	void GeometryReadWriter()
	{
	
		GsPolygonPtr ptrPolygon  = new GsPolygon();
		GsRingPtr ptrRing = new GsRing(GsBox(200,200,300,300));
		ptrPolygon->Add(ptrRing);
	
		//GsGeometry->WKB
		GsGrowByteBuffer buff;
		GsWKBOGCWriter w(&buff);
		w.Write(ptrPolygon);
		GsGrowByteBuffer * buffout = w.WKB();
		//WKB->GsGeometry
		GsWKBOGCReader r(buffout);
		GsGeometryPtr ptrGeo = r.Read();
	
	}