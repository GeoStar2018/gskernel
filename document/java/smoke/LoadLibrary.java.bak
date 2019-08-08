package unittest.smoke;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.geostar.kernel.GsCircleArc;
import com.geostar.kernel.GsConnectProperty;
import com.geostar.kernel.GsDataRoomType;
import com.geostar.kernel.GsFileGeoDatabaseFactory;
import com.geostar.kernel.GsGeoDatabase;
import com.geostar.kernel.GsGeometry;
import com.geostar.kernel.GsGeometryFactory;
import com.geostar.kernel.GsGeometryRelationResult;
import com.geostar.kernel.GsKernel;
import com.geostar.kernel.GsLinePointSymbol;
import com.geostar.kernel.GsMultiPyramid;
import com.geostar.kernel.GsPoint;
import com.geostar.kernel.GsPyramid;
import com.geostar.kernel.GsRasterClass;
import com.geostar.kernel.GsRawPoint;
import com.geostar.kernel.GsSqliteGeoDatabaseFactory;
import com.geostar.kernel.GsStringVector;
import com.geostar.kernel.GsStyleTable;
import com.geostar.kernel.GsStyleTableFactory;
import com.geostar.kernel.GsWKTOGCReader;



public class LoadLibrary {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("***************.dll start load***************");
	
		System.loadLibrary("gsjavaport");
			GsKernel.Initialize();
		System.out.println("***************gsjavaport  load succeed***************");
		

		
	}


	@Test
	public void LoadGsUtility() {

		System.out.println("Determine the existence of GsUtility");
		
		int a=2;
		assertEquals(2,a);	
	}
	
	@Test
	public void LoadGsUtility1() {

		System.out.println("Determine the existence of GsUtility1");
		
		GsStyleTableFactory fac = new GsStyleTableFactory();
		String strCurDir = System.getProperty("user.dir");
		strCurDir+="/data/zip/xuzhou.zip";
		System.out.println(strCurDir);
		GsStyleTable ptrStyleTable = fac.OpenFromZip(strCurDir);
		assertEquals(true,ptrStyleTable != null);	
	}
	
	
	@Test
	public void Loadgsgeodatabase() {
		System.out.println("Determine the existence of GsGeoDatabase");
		GsSqliteGeoDatabaseFactory vFac = new GsSqliteGeoDatabaseFactory();
		GsConnectProperty vConn =new GsConnectProperty();
		String strCurDir = System.getProperty("user.dir");
		strCurDir+="/data/sqlite";
				System.out.println(strCurDir);
		vConn.setServer(strCurDir);
		
		GsGeoDatabase ptrGDB = vFac.Open(vConn);

		assertEquals(ptrGDB != null,true);

		GsStringVector vecName = new GsStringVector();
		ptrGDB.DataRoomNames(GsDataRoomType.eFeatureClass, vecName);

		assertEquals(vecName.size()>0, true);
		
	}
	
	@Test
	public void LoadGsGeomathSE() {
		System.out.println("Determine the existence of GsGeomathSE");
	
		int inter[] = { 1,2,1 };
		double coord[] = { 0,0,1,0,0,0 };
		
		GsGeometryFactory gsGeometryFactory=new GsGeometryFactory();
		GsGeometry gFactory= gsGeometryFactory.CreateGeometryFromOracle(inter, 3, coord, 6,2);
		assertEquals(gFactory !=null, true);
		
	}
		

	
	@Test
	public void LoadGsMap() {
		System.out.println("Determine the existence of GsMap");
		GsStyleTableFactory fac = new GsStyleTableFactory();
		String strCurDir = System.getProperty("user.dir");
		strCurDir+="/data/vectortile/json/china3.json";
		System.out.println(strCurDir);
		GsStyleTable gsStyle=fac.OpenFromJson(strCurDir, true);
		assertEquals(true,gsStyle != null);	
	}
	
	
	@Test
	public void LoadGsGeometry() {
		System.out.println("Determine the existence of GsGeometry");
		GsRawPoint from = new GsRawPoint(1, 2);
		GsRawPoint middle = new GsRawPoint(3, 4);
		GsRawPoint end = new GsRawPoint(5, 2);
		GsCircleArc arctest = new GsCircleArc();
		arctest.Set(from, middle, end);

		GsRawPoint pfrom = new GsRawPoint();
		GsRawPoint pMiddle = new GsRawPoint();
		GsRawPoint pTo = new GsRawPoint();
		arctest.Get(pfrom, pMiddle, pTo);
		assertEquals(1, pfrom.getX(), 0.000001);
		assertEquals(2, pfrom.getY(), 0.000001);

		assertEquals(3, pMiddle.getX(), 0.000001);
		assertEquals(4, pMiddle.getY(), 0.000001);

		assertEquals(5, pTo.getX(), 0.000001);
		assertEquals(2, pTo.getY(), 0.000001);
	}
	
	@After
	public void tearDown() throws Exception {
	}
	public <T> T Fun()
	{
		return null;
	}
	@Test
	public void LoadGsPcgeodatabaseport() {
		System.out.println("Determine the existence of GsPcgeodatabaseport");
			GsWKTOGCReader o = Fun();
			GsWKTOGCReader r = new GsWKTOGCReader("POINT (-30 10)");
			GsGeometry ptrGeo = r.Read();
			GsPoint pt = GsPoint.DowncastTo(ptrGeo);
			assertEquals(-30, pt.X(),0.000001);
			assertEquals(10, pt.Y(),0.000001);	
	}	
		
	@Test
	public void LoadGsSpatialreference() {

		System.out.println("Determine the existence of LoadGsSpatialreference");

		GsPoint ptrPoint1 = new GsPoint(0.0, 0);
		GsPoint ptrPoint2 = new GsPoint(1, 2);
		assertEquals(GsGeometryRelationResult.eIsTrue,
				ptrPoint1.IsDisjoin(ptrPoint2));	
	}
	
	
	@Test
	public void LoadGsSymbol() {

		System.out.println("Determine the existence of GsSymbol");
		
   GsLinePointSymbol gsLinePointSymbol = new GsLinePointSymbol();
   gsLinePointSymbol.StartPoint(0,0);
   gsLinePointSymbol.EndPoint(10, 10);
   
   assertEquals(true,gsLinePointSymbol != null);	
		
	}
	
	
	
	
}

