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


		System.out.println("loadlibrary");
		System.out.println("oraclegeodatabase");
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
		
		System.loadLibrary("gsjavaport");
		GsKernel.Initialize();
		GsPCGeoDatabase.Initialize();
		//GsRefObject obj = GsClassFactory.CreateInstance("OracleSpatialGeoDatabaseFactory");
		
		GsRefObject obj = GsClassFactory.CreateInstance("MySqlGeoDatabaseFactory");
		
		GsGeoDatabaseFactory fac = GsGeoDatabaseFactory.DowncastTo(obj);
		GsConnectProperty conn = new GsConnectProperty ();
		conn.setServer("192.168.42.77");
		conn.setDatabase("desktop");
		conn.setUser("oracledata");
		conn.setPassword("1");
		conn.setPort(1521); 
		GsGeoDatabase gdb = fac.Open(conn);
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

}
