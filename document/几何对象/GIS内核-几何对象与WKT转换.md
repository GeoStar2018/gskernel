GIS内核- 几何对象与WKT转换
	
	void GeometryReadWriter()
	{
	
		GsPolygonPtr ptrPolygon  = new GsPolygon();
		GsRingPtr ptrRing = new GsRing(GsBox(200,200,300,300));
		ptrPolygon->Add(ptrRing);

	
		//GsGeometry ->WKT
		GsWKTOGCWriter wktWriter;
		wktWriter.Write(ptrPolygon);
		GsString str = wktWriter.WKT();
	
		//WKT -> GsGeometry
		GsWKTOGCReader wktReader(str);
		ptrGeo =  wktReader.Read();
	
	}