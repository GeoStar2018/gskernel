关于Geometry的快速做1:N(1对多)查询和操作.
目前内核提供基础的几何操作能力,例如下面的求交示例

	std::vector<int> a = { 1,3,1 };
	std::vector	<double> a1 = { 1,1,0,1,0,0,2,2,0,1,1,0 };

	std::vector<int> b = { 1,3,1 };
	std::vector	<double> b1 = { 1,1,1,0,1.5,2,1,1 };

	GsGeometryPtr ptrGeo1 =  GsGeometryFactory::CreateGeometryFromOracle(&a[0], a.size(), &a1[0], a1.size(), 3);
	GsGeometryPtr ptrGeoOther = GsGeometryFactory::CreateGeometryFromOracle(&b[0], b.size(), &b1[0], b1.size(), 2);
	//求交关系比较: 
	GsGeometryRelationResult pGeoRet =  ptrGeo1->IsIntersect(ptrGeoOther);
	//求交操作
	GsGeometryPtr pGeoIntersectionRet = ptrGeo1->Intersection(ptrGeoOther);


上面示例演示的为直接将两个几何对象做求交, 但是如果需要将ptrGeo1和 n个ptrGeoOther做求交或者比较,建议使用GsIndexGeometry,示例如下:


	
	假如有N个 ptrGeoOther的集合

	GsIndexGeometryPtr ptrIndexGeo = new GsIndexGeometry(ptrGeo1);

	std::vector<GsGeometryPtr> ptrGeoArray = { ptrGeoOther1 ,ptrGeoOther2 ... ptrGeoOtherN};

	for (int i = 0; i < ptrGeoArray.size(); i++)
	{
		pGeoRet = ptrIndexGeo->IsIntersect(ptrGeoArray[i]);
		pGeoIntersectionRet = ptrIndexGeo->Intersection(ptrGeoArray[i]);
	}