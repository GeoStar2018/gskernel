

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
		//不在线上的不连接
		if (tmp1.dblValue < 0 || tmp2.dblValue > max)
			continue;

		{
			double f1 = ptrPath->Closet(GsRawPoint(tmp1.ptrPoint->X(), tmp1.ptrPoint->Y()));
			double f2 = ptrPath->Closet(GsRawPoint(tmp2.ptrPoint->X(), tmp2.ptrPoint->Y()));
			GsPolylinePtr subPath = polyline->SubCurve(f1, f2);
			subLines.emplace_back(subPath);
		}
	}

	//起点 在线外直接连接
	{
		GsPathPtr ptrtmpPath = new GsPath();
		if (pointsOnLine[0].dblValue < 0)//超出起点了
		{
			ptrtmpPath->Add(GsRawPoint(pointsOnLine[0].ptrPoint->X(), pointsOnLine[0].ptrPoint->Y()));
			ptrtmpPath->Add(GsRawPoint(ptrPath->StartPoint()));

			GsPolylinePtr ptrtmpPolyline = new GsPolyline();
			ptrtmpPolyline->Add(ptrtmpPath);
			subLines.emplace_back(ptrtmpPolyline);
		}
	}

	{			
		GsPathPtr ptrtmpPath = new GsPath();
		if (pointsOnLine[pointsOnLine.size() - 1].dblValue > max)//超出终点了
		{

			ptrtmpPath->Add(GsRawPoint(ptrPath->EndPoint()));
			ptrtmpPath->Add(GsRawPoint(pointsOnLine[pointsOnLine.size() - 1].ptrPoint->X(), pointsOnLine[pointsOnLine.size() - 1].ptrPoint->Y()));
			GsPolylinePtr ptrtmpPolyline = new GsPolyline();
			ptrtmpPolyline->Add(ptrtmpPath);
			subLines.emplace_back(ptrtmpPolyline);
		}
	}
	//打印输出
	for (int i = 0; i <= pointsOnLine.size(); i++)
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