package unittest;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.geostar.kernel.*;
public class oraclegeodatabase {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {


		System.loadLibrary("gsjavaport");
		GsKernel.Initialize();
		GsPCGeoDatabase.Initialize();
		//System.loadLibrary("ggsextensions");
		GsKernel.Initialize();
		System.out.println("loadlibrary");
		String strCurDir = System.getProperty("user.dir");
		strCurDir += "/../data/coordinatesystem/EPSG.txt";
		System.out.println(strCurDir);
		GsGlobeConfig.Instance().Child("Kernel/SpatialRererence/EPSG")
				.Value(strCurDir);
		System.out.println("spatialreference");
		
		GsSpatialReference p = new GsSpatialReference(4326);
		System.out.println(p.EquatorialRadiusA());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() { 
		

		//GsRefObject obj = GsClassFactory.CreateInstance("OracleSpatialGeoDatabaseFactory");
		
		GsRefObject obj = GsClassFactory.CreateInstance("MySqlGeoDatabaseFactory");
		
		GsGeoDatabaseFactory fac = GsGeoDatabaseFactory.DowncastTo(obj);
		GsConnectProperty conn = new GsConnectProperty ();
		conn.setServer("192.168.42.77");
		conn.setDatabase("desktop");
		conn.setUser("oracledata");
		conn.setPassword("1");
		conn.setPort(1521); 
		if(null == fac)
			return;
		GsGeoDatabase gdb = fac.Open(conn);
		if(null == gdb)
			return;
		GsStringVector vecName =new GsStringVector();
		gdb.DataRoomNames(GsDataRoomType.eFeatureClass, vecName);
		
		for(int i =0;i<vecName.size();i++)
		{
			System.out.println(vecName.get(i));
		}
		
	    GsFeatureClass pFcs = gdb.OpenFeatureClass("dhd");
	    GsPolygon g = new GsPolygon();
	    GsRing ring = new GsRing();
	    ring.Add(new GsRawPoint(0,0));
	    ring.Add(new GsRawPoint(4,0));
	    ring.Add(new GsRawPoint(4,4));
	    ring.Add(new GsRawPoint(0,4));
	    ring.Add(new GsRawPoint(0,0));
	    g.Add(ring);
		GsFeatureCursor pCursor =  pFcs.Search(g);
		GsFeature pFea =  pCursor.Next();
		do {
			if(pFea==null)
				break;
			GsGeometry pGeo = pFea.Geometry();
			GsGeometryBlob pblob =  pGeo.GeometryBlobPtr();
			
			 int coordlength = pblob.CoordinateLength();
			 int intsize = pblob.InterpretLength();
			 double[] coordarray = new double[coordlength];
			 int[] intarray= new int[intsize];
			 
			 pblob.Coordinate(coordarray);
			 pblob.Interpret(intarray);
		} while (pCursor.Next(pFea));
	}

	@Test
	public void gdbtest() { 
		
		System.loadLibrary("gsjavaport");
		GsKernel.Initialize();
		GsPCGeoDatabase.Initialize();
		//GsRefObject obj = GsClassFactory.CreateInstance("OracleSpatialGeoDatabaseFactory");
		
		//GsRefObject obj = GsClassFactory.CreateInstance("MySqlGeoDatabaseFactory");
		GsRefObject obj = GsClassFactory.CreateInstance("GDBGeoDatabaseFactory");
		GsGeoDatabaseFactory fac = GsGeoDatabaseFactory.DowncastTo(obj);
		GsConnectProperty conn = new GsConnectProperty ();
		conn.setServer("D:\\yxl.gdb");
		if(null == fac)
			return;
		GsGeoDatabase gdb = fac.Open(conn);
		if(null == gdb)
			return;
		//枚举出数据集, 仅支持矢量
		GsStringVector vecName =new GsStringVector();
		gdb.DataRoomNames(GsDataRoomType.eFeatureClass, vecName);
		
		for(int i =0;i<vecName.size();i++)
		{
			System.out.println(vecName.get(i));
		}
		
		//打开一个, 存在就删除
	    GsFeatureClass pFcs = gdb.OpenFeatureClass("dhd");
	    if(pFcs != null)
	    	pFcs.Delete();
	    
	    String strName = "dhd"; 
	    GsFields fs = new GsFields(); 
	    GsFieldVector fdsVector = new GsFieldVector();
	    GsField fdoidField =new GsField("OID", GsFieldType.eInt64Type);
	    fdsVector.add(fdoidField);
	    GsField geomield =new GsField("Geometry", GsFieldType.eGeometryType);
	    fdsVector.add(geomield);
	    GsField namefd =new GsField("NAME", GsFieldType.eStringType);
	    fdsVector.add(namefd);
	    GsField idfd =new GsField("ID", GsFieldType.eIntType);
	    fdsVector.add(idfd);
	    
	    
	    GsGeometryColumnInfo oColumnInfo = new GsGeometryColumnInfo(); 
	    oColumnInfo.setFeatureType(GsFeatureType.eSimpleFeature);
	    oColumnInfo.setXYDomain(new GsBox(56, 0, 123, 70));
	    oColumnInfo.setGeometryType(GsGeometryType.eGeometryTypePoint);
	    oColumnInfo.setHasZ(false);
	    GsSpatialReference pSR =  new GsSpatialReference(4490);
	  
	    //创建存储数据
	   GsFeatureClass pfcsClass =   gdb.CreateFeatureClass(strName, fs, oColumnInfo, pSR);
	   
	  pfcsClass.Transaction().StartTransaction();
	  GsFeature pFeature =  pfcsClass.CreateFeature();
	  for(int i = 0; i < 100000; i++)
	  {
		  pFeature.OID(-1);//设-1会自增
		  pFeature.Geometry(new GsPoint(i, i));
		  pFeature.Value(2, "Name"+i);
		  pFeature.Value(3, i+10000);
		  if(!pFeature.Store())
			  continue;
		  if(i%10001== 1)
		  {
			  pfcsClass.Transaction().StartTransaction();
			  pfcsClass.Transaction().CommitTransaction();
		  }
	  }
	  
	  pfcsClass.Transaction().CommitTransaction();
	    
	   
	   
	   //查询遍历数据
	   GsSpatialQueryFilter spatialQueryFilter = new GsSpatialQueryFilter();
		GsFeatureCursor pCursor =  pfcsClass.Search(spatialQueryFilter);
		GsFeature pFea =  pCursor.Next();
		String nameString = "";
		do {
			if(pFea==null)
				break;
			GsGeometry pGeo = pFea.Geometry();
            long oid = pFea.OID();
            nameString =  pFea.ValueString(2);
            
		} while (pCursor.Next(pFea));
	}
}
