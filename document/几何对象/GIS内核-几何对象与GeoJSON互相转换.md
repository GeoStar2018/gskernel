GIS内核-几何对象与GeoJSON互相转换
	
	void GeometryReadWriter()
	{
	
		GsPolygonPtr ptrPolygon  = new GsPolygon();
		GsRingPtr ptrRing = new GsRing(GsBox(200,200,300,300));
		ptrPolygon->Add(ptrRing);

	
		//GsGeometry -> GeoJson
		GsGeoJSONOGCWriter jsonWriter;
		jsonWriter.Write(ptrPolygon);
		GsString strjson =  jsonWriter.GeoJSON();
	
		//GeoJson -> GsGeometry
		GsGeoJSONOGCReader jsonReader(strjson);
		ptrGeo =  jsonReader.Read();
	
	}