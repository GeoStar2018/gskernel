首先明确一个概念，Geometry是GsPath的基类，GsPath 是GsRing的基类。


### 主要思想 ###
1：如果GsPath 类型的对象转换为GsRing的对象，属于父类对象转换成子类对象。直接转换的话，肯定会出错(返回NULL)。
   解决方法：将GsPath对象当参数，构造一个GsPolygon类型，然后再取出面。

2：如果GsRing 类型转换为GsPath类型，属于子类对象转换成父类对象。
   c++中，指针直接赋值即可。
   java中，用DowncastTo函数也可以完成转换。

### 在c++中，GsPath 转换为 GsRing 类型。 ###
```

	int				pInter[] = { 1, 3, 1 };
	double			pCoord[] = { 0, 0, 10,0,10,13,0,10,0,0 };
	GsGeometryBlob	pBlob;
	pBlob.SetCoordinate(pInter, 3, pCoord, 10);
	pBlob.CoordinateDimension(2);
	
	GsPathPtr ptrPath = new  GsPath(pBlob.BufferHead(), pBlob.BufferSize());
	//GsRingPtr ptrRing = ptrPath; //父类强转为子类，返回为NULL。 

	///这个时候如果需要 转换为GsRing类型，那么可以使用代码
	GsPolygonPtr ptrpolygon = new GsPolygon(ptrPath);
	
	GeometryPtr pGeo = ptrpolygon->Geometry(0);
	
	if(GsRingPtr ptrRing = pGeo)
	{
		
	}
	else if(GsEnvelopePtr ptrEnv = pGeo)
	{
		
	}
```

### 在c++中，GsRing 转换为GsPath 类型。 ###
```
	GsRingPtr ptrRing = new GsRing();
	GsPathptr ptrPath = ptrRing;
```


### 在java中，GsPath 转换为 GsRing 类型。 ###
```
			int				pInter[] = { 1, 3, 1 };
			double			pCoord[] = { 0, 0, 10,0,10,10,0,10,0,0 };
			GsPath ptrPath = new GsPath(); 
			GsGeometryBlob blob  = ptrPath.GeometryBlobPtr();
			blob.SetCoordinate(pInter,3,pCoord,10);
			blob.CoordinateDimension(2);
			
			GsPolygon ptrpolygon = new GsPolygon(ptrPath);
			GsGeometry geo = ptrpolygon.Geometry(0);

```

### 在java中，GsRing 转换为 GsPath 类型。 ###
```
	GsRing ptrRing = new GsRing();
    GsPath path = GsPath.DowncastTo(ptrRing);
```
By wuyongbo