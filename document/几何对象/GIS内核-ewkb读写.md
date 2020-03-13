GIS内核-ewkb读写

```c++
	GsPolygonPtr poly = new  GsPolygon();
	GsRingPtr ptrring = new GsRing(GsBox(1, 1, 3, 3));
	GsGrowByteBuffer buff;
	GsEWKBOGCWriter w(&buff,4326);
	w.Write(poly);
	unsigned char* pHead = buff.BufferHead();
	int size = buff.BufferSize();
	string wkbStr = toHexString(pHead, size);

	GsEWKBOGCReader read(pHead, size);
	GsGeometryPtr ptrGeo = read.Read();
	GsSpatialReferencePtr ptrref =	ptrGeo->SpatialReference();
	int epsg = ptrref->EPSG();
```

