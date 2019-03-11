GIS内核-几何对象与GML3互相转换
	
	void GeometryReadWriter()
	{
	
		GsPolygonPtr ptrPolygon  = new GsPolygon();
		GsRingPtr ptrRing = new GsRing(GsBox(200,200,300,300));
		ptrPolygon->Add(ptrRing);
		//GsGeometry ->GML3
		GsGML3OGCWriter gmlWriter;
		gmlWriter.Write(ptrPolygon);
		GsString str = gmlWriter.GML();
	
		//GML3 -> GsGeometry
		GsGMLOGCReader gmlReader(str.c_str());
		ptrGeo =  gmlReader.Read();
	

	
	}