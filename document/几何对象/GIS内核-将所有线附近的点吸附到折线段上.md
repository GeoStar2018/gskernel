

```c++
struct PointValue
{
	GsPointPtr ptrPoint = new GsPoint();
	double dblValue = 0.0;
	PointValue()
	{

	}
	PointValue(PointValue& o)
	{
		ptrPoint->X(o.ptrPoint->X());
		ptrPoint->Y(o.ptrPoint->Y());
		dblValue = o.dblValue;

	}
};




GS_TEST(path, pathcut, chijing, 20191230) 
{
	int pInter[] = { 1,2,1 };
	double pCoord[] = { 0,0,1,1,2,1,3,-1,4,0};
	GsGeometryBlob blob;
	blob.GeometryType(eGeometryTypePath);
	blob.SetCoordinate(pInter, 3, pCoord, 10);
	GsPathPtr ptrPath = new GsPath(blob.BufferHead(), blob.BufferSize());
	GsPolylinePtr polyline = new GsPolyline();
	polyline->Add(ptrPath);


	double max = ptrPath->MaxParameter();

	std::vector<GsPointPtr> points = {};
	points.push_back(new GsPoint(0.0, 1.0));
	points.push_back(new GsPoint(1.5, 0.8)); 
	points.push_back(new GsPoint(2.5, 0.4 )); 
	points.push_back(new GsPoint(5.0 ,0.00 ));
	std::vector<PointValue> pointsOnLine = {};
	auto it = points.begin();
	//求出最近点
	for (; it != points.end(); it++)
	{
		PointValue pv;
		GsPointPtr pp = (*it);
		ptrPath->QueryNearestPoint(pp, GsSegmentExtension::eNoExtension, pv.ptrPoint);
		//最近点在线的延长线上,需要特殊处理
		if (ptrPath->Closet(GsRawPoint(pv.ptrPoint->X(), pv.ptrPoint->Y())) <= 0
			|| ptrPath->Closet(GsRawPoint(pv.ptrPoint->X(), pv.ptrPoint->Y())) >= max)
		{
			pv.ptrPoint->X((*it)->X());
			pv.ptrPoint->Y((*it)->Y());
		}
		pv.dblValue = ptrPath->Closet(GsRawPoint(pv.ptrPoint->X(), pv.ptrPoint->Y()));
		pointsOnLine.emplace_back(pv);
		std::cout<< pv.ptrPoint->X() << "   "<< pv.ptrPoint->Y() << std::endl;
	}
	//最近点起点距离参数排序
	std::sort(pointsOnLine.begin(), pointsOnLine.end(),[](const PointValue & a, const PointValue & b){
		return a.dblValue < b.dblValue;
	});
	
	//连接每两个最近点生成子线段
	std::vector<GsPolylinePtr> subLines = {};
	for (int i = 0; i< pointsOnLine.size()-1; i++)
	{
		const PointValue& tmp1 = pointsOnLine[i];
		const PointValue& tmp2 = pointsOnLine[i+1];
		{
			double f1 = ptrPath->Closet(GsRawPoint(tmp1.ptrPoint->X(), tmp1.ptrPoint->Y()));
			double f2 = ptrPath->Closet(GsRawPoint(tmp2.ptrPoint->X(), tmp2.ptrPoint->Y()));
			GsPolylinePtr subPath = polyline->SubCurve(f1, f2);
			subLines.emplace_back(subPath);
		}
	}


	//打印输出
	for (int i = 0; i < subLines.size(); i++)
	{
		int ncount = subLines[i]->GeometryBlobPtr()->CoordinateLength();
		double *gcoord = subLines[i]->GeometryBlobPtr()->Coordinate();
		std::cout << "-----" << std::endl;
		for (int k = 0; k < ncount; k++)
			std::cout << gcoord[k] << std::endl;
	}
}
```





```c++

```