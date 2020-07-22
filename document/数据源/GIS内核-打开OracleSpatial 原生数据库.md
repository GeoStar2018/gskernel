
GIS内核-打开OracleSpatial 原生数据库 

GS_TEST(FileGeoDatabase, OpenDataRosdasadomFolder, cj, 20180615)
{
	GsPCGeoDatabase::Initialize();
	GsRefObject* refObj = GsClassFactory::CreateInstance("OracleSpatialGeoDatabaseFactory");
	GsGeoDatabaseFactoryPtr vFac = dynamic_cast<GsGeoDatabaseFactory*>(refObj);
	GsConnectProperty connOra;
	connOra.Server = "guanyu-pc";
	connOra.Database = "orcl";
	connOra.User = "testuser";
	connOra.Password = "1";
	connOra.Port = 1521;
	//原生Oracle
	connOra.DataSourceType = eOracleSpatial2;
	GsGeoDatabasePtr pGeoDB =  vFac->Open(connOra);
	GsFeatureClassPtr pFcs = pGeoDB->OpenFeatureClass("R1");
	GsFeatureType ptype =  pFcs->GeometryColumnInfo().FeatureType;
}

GS_TEST(FileGeoDatabase, OpenDataRosdasadomFolder2, cj, 20180615)
{
	GsPCGeoDatabase::Initialize();
	GsRefObject* refObj = GsClassFactory::CreateInstance("OracleSpatialGeoDatabaseFactory");
	GsGeoDatabaseFactoryPtr vFac = dynamic_cast<GsGeoDatabaseFactory*>(refObj);
	GsConnectProperty connOra;
	connOra.Server = "guanyu-pc";
	connOra.Database = "orcl";
	connOra.User = "testuser";
	connOra.Password = "1";
	connOra.Port = 1521;
	//geostar Oracle
	connOra.DataSourceType = eOracleSpatial;
	GsGeoDatabasePtr pGeoDB =  vFac->Open(connOra);
	GsFeatureClassPtr pFcs = pGeoDB->OpenFeatureClass("R1");
	GsFeatureType ptype =  pFcs->GeometryColumnInfo().FeatureType;
}