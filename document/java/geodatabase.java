package unittest;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.geostar.kernel.*;
public class geodatabase {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.loadLibrary("gsjavaport");
		System.out.println("loadlibrary");
		System.out.println("geodatabase");
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
	public <T> T Fun()
	{
		return null;
	}
	@Test
	public void testGeometryCast() {
		GsWKTOGCReader o = Fun();
		
		GsWKTOGCReader r = new GsWKTOGCReader("POINT (30 10)");
		GsGeometry ptrGeo = r.Read();
		
		GsPoint pt = GsPoint.DowncastTo(ptrGeo);
		System.out.println(pt.X());
		System.out.println(pt.Y());
		
	}
	@Test
	public void openfeatureclass() {
		System.out.println("openfeatureclass");
		GsSqliteGeoDatabaseFactory fac = new GsSqliteGeoDatabaseFactory();
		GsConnectProperty conn = new GsConnectProperty();
		String strCurDir = System.getProperty("user.dir");
		String strServer = strCurDir+ "/data/400w";
		conn.setServer(strServer);
		System.out.println(strServer);
		GsGeoDatabase ptrGDB =  fac.Open(conn);
		assertNotNull("database is null",ptrGDB);
		
		GsStringVector vecName = new GsStringVector();
		ptrGDB.DataRoomNames(GsDataRoomType.eFeatureClass, vecName);
		for(int i =0;i<vecName.size();i++)
		{
			String strName = vecName.get(i);
			//System.out.println(strName);
			GsFeatureClass ptrFeaClass = ptrGDB.OpenFeatureClass(strName);
			assertNotNull("open featureclass is nulll",ptrFeaClass);
			
			if(strName.toLowerCase().endsWith(".fcs"))
				strName = strName.substring(0, strName.lastIndexOf("."));
			assertEquals(strName, ptrFeaClass.Name());
			//pFeaClass.delete();
			ptrFeaClass.delete();
		}
		vecName.delete();
		ptrGDB.delete();
		conn.delete();
		fac.delete();
		System.out.println("openfeatureclass end");
		
	}
	@Test
	public void openfeatureclass100() throws IOException {
		System.out.println("openfeatureclass100");
		for(int i =0;i<10;i++)
		{
			openfeatureclass();
		}
		System.out.println("openfeatureclass100 end");
		
	}

}
