package unittest;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.geostar.kernel.*;
public class maptest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.loadLibrary("gsjavaport");
		System.out.println("maptest");
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
	public void DrawFeatureLayer() {
		String strCurDir = System.getProperty("user.dir");
		
		String strServer = strCurDir +"/Data/400w";
		GsSqliteGeoDatabaseFactory fac = new GsSqliteGeoDatabaseFactory();
		GsConnectProperty conn = new GsConnectProperty();
		conn.setServer(strServer);
		GsGeoDatabase gdb = fac.Open(conn);
		GsFeatureClass feaclass = gdb.OpenFeatureClass("BOU1_4M_S");
		GsFeatureLayer lyr = new GsFeatureLayer(feaclass);
		
		GsMap map = new GsMap(null);
		map.Layers().add(lyr);
		int w = 1024;
		int h = 1024;
		GsMemoryImageCanvas canvas =new GsMemoryImageCanvas(1024,1024);
		canvas.Clear(new GsColor(0));
		GsDisplayTransformation dt = new GsDisplayTransformation(feaclass.Extent(),new GsRect(0,0,w,h));
		GsDisplay disp = new GsDisplay(canvas, dt);
		GsTrackCancel pCancel = new GsTrackCancel();
		long starttime = System.currentTimeMillis();
		map.Output(disp, pCancel);
		
		System.out.println("map.output time " + (System.currentTimeMillis() - starttime ));
		starttime = System.currentTimeMillis();
		
		String strResult = strCurDir +"/mapoutout.png";
		canvas.Image().SavePNG(strResult);
		System.out.println("save png" + (System.currentTimeMillis() - starttime ));
		
		
		
		
	}

}
