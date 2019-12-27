

	struct PointValue
	{
		GsPointPtr ptrPoint = new GsPoint();
		double dblValue = 0.0;
		PointValue()
		{}
	    PointValue(PointValue& o)
	    {
	        ptrPoint->X(o.ptrPoint->X());
	        ptrPoint->Y(o.ptrPoint->Y());
	        dblValue = o.dblValue;
	
	    }
	};





```c++



GS_TEST(path, CircleArc_Value2, zhangqiankun, 20170315) 
{
	int pInter[] = { 1,2,1 };
	double pCoord[] = { 0,0,1,1,2,1,3,-1,4,0};
	GsGeometryBlob blob;
	blob.GeometryType(eGeometryTypePath);
	blob.SetCoordinate(pInter, 9, pCoord, 10);
	GsPathPtr ptrPath = new GsPath(blob.BufferHead(), blob.BufferSize());
	GsPolylinePtr polyline = new GsPolyline();
	polyline->Add(ptrPath);
double max = ptrPath->MaxParameter();

std::vector<GsPointPtr> points = { new GsPoint(0,1), new GsPoint(1.5,0), new GsPoint(2.5,0), new GsPoint(5,1) };
std::vector<PointValue> pointsOnLine = {};
auto it = points.begin();
for (; it != points.end(); it++)
{
	PointValue pv;
	ptrPath->QueryNearestPoint(*it, GsSegmentExtension::eNoExtension, pv.ptrPoint);
	if (ptrPath->Closet(GsRawPoint(pv.ptrPoint->X(), pv.ptrPoint->Y())) <= 0
		|| ptrPath->Closet(GsRawPoint(pv.ptrPoint->X(), pv.ptrPoint->Y())) >= max)
	{
		pv.ptrPoint->X((*it)->X());
		pv.ptrPoint->Y((*it)->Y());
	}
	pv.dblValue = ptrPath->Closet(GsRawPoint(pv.ptrPoint->X(), pv.ptrPoint->Y()));
	pointsOnLine.emplace_back(pv);
}

std::sort(pointsOnLine.begin(), pointsOnLine.end(),[](const PointValue & a, const PointValue & b){
	return a.dblValue < b.dblValue;
});

std::vector<GsPolylinePtr> subLines = {};
for (int i = 0; i< pointsOnLine.size(); i++)
{
	const PointValue& tmp1 = pointsOnLine[i];
	const PointValue& tmp2 = pointsOnLine[i];
	double f1 = ptrPath->Closet(GsRawPoint(tmp1.ptrPoint->X(), tmp1.ptrPoint->Y()));
	double f2 = ptrPath->Closet(GsRawPoint(tmp2.ptrPoint->X(), tmp2.ptrPoint->Y()));
	GsPolylinePtr subPath = polyline->SubCurve(f1, f2);
	sub
	Lines.emplace_back(subPath);
}
}
```