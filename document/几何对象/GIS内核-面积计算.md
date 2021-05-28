GIS内核-面积计算

```c++
GsOracleSpatialGeoDatabaseFactory fac;
GsConnectProperty conn; 
conn.Database = "ORCL";
conn.Server = "172.17.45.189";
conn.Port = 1521;
conn.User = "bigdata";
conn.Password = "bigdata";
GsGeoDatabasePtr pDB =  fac.Open(conn);
GsFeatureClassPtr pFcs =  pDB->OpenFeatureClass("BYZTBDF");
GsGeometryPtr ptrGeo =  pFcs->Search()->Next()->Geometry();
GsPolygonPtr pgo = ptrGeo;

//如果是投影转地理坐标系
if(!pFcs->SpatialReference()->IsGeographic())
{
    	ptrGeo->Transform(GsCoordinateTransformationFactory::CreateProjectCoordinateTransformation(pFcs->SpatialReference(), pFcs->SpatialReference()->Geographic()));
		ptrGeo->SpatialReference(pFcs->SpatialReference()->Geographic());
}
//积分面积
double garea = pgo->Area();
GsGeodesicPolygon p(pFcs->SpatialReference()->Geographic(), false);
//椭球面积
double ghj   =  pgo->GeodesicArea(p);
```