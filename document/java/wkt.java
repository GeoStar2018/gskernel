package unittest;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.geostar.kernel.GsGeometry;
import com.geostar.kernel.GsKernel;
import com.geostar.kernel.GsWKTOGCReader;
import com.geostar.kernel.GsWKTOGCWriter;

public class wkt {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.loadLibrary("gsjavaport");
		GsKernel.Initialize();
		System.out.println("wkt");
	}
	
	@Test
	public void point_test1(){
		String wkt = "POINT(117 56)";
		GsWKTOGCReader baseReader = new GsWKTOGCReader(null);
		baseReader.Begin(wkt);
		GsGeometry g = baseReader.Read();
		GsWKTOGCWriter baseWriter = new GsWKTOGCWriter();
		baseWriter.Reset();
		baseWriter.Write(g);
		String wkt_p = baseWriter.WKT();
		Assert.assertEquals(wkt,wkt_p);
	}
	
	@Test
	public void point_test2(){
		String wkt = "POINT(113.4589 60.58945)";
		GsWKTOGCReader baseReader = new GsWKTOGCReader(null);
		baseReader.Begin(wkt);
		GsGeometry g = baseReader.Read();
		GsWKTOGCWriter baseWriter = new GsWKTOGCWriter();
		baseWriter.Reset();
		baseWriter.Write(g);
		String wkt_p = baseWriter.WKT();
		Assert.assertEquals(wkt,wkt_p);
	}
	
	@Test
	public void multipoint_test1(){
		String wkt = "MULTIPOINT(52.4689203930076 26.9901853870054,115.56614 38.55145)";
		GsWKTOGCReader baseReader = new GsWKTOGCReader(null);
		baseReader.Begin(wkt);
		GsGeometry g = baseReader.Read();
		GsWKTOGCWriter baseWriter = new GsWKTOGCWriter();
		baseWriter.Reset();
		baseWriter.Write(g);
		String wkt_p = baseWriter.WKT();
		String wkt_r = "MULTIPOINT((52.4689203930076 26.9901853870054),(115.56614 38.55145))";
		Assert.assertEquals(wkt_r,wkt_p);
	}
	
	@Test
	public void multipoint_test2(){
		String wkt = "MULTIPOINT((10 40),(40 30),(20 20),(30 10))";
		GsWKTOGCReader baseReader = new GsWKTOGCReader(null);
		baseReader.Begin(wkt);
		GsGeometry g = baseReader.Read();
		GsWKTOGCWriter baseWriter = new GsWKTOGCWriter();
		baseWriter.Reset();
		baseWriter.Write(g);
		String wkt_p = baseWriter.WKT();
		Assert.assertEquals(wkt,wkt_p);
	}
	
	@Test
	public void linestring_test1(){
		String wkt = "LINESTRING(3 4,10 50,20 25)";
		GsWKTOGCReader baseReader = new GsWKTOGCReader(null);
		baseReader.Begin(wkt);
		GsGeometry g = baseReader.Read();
		GsWKTOGCWriter baseWriter = new GsWKTOGCWriter();
		baseWriter.Reset();
		baseWriter.Write(g);
		String wkt_p = baseWriter.WKT();
		Assert.assertEquals(wkt,wkt_p);
	}
	
	@Test
	public void linestring_test2(){
		String wkt = "LINESTRING(115.26 43.489,117.598 56.4875)";
		GsWKTOGCReader baseReader = new GsWKTOGCReader(null);
		baseReader.Begin(wkt);
		GsGeometry g = baseReader.Read();
		GsWKTOGCWriter baseWriter = new GsWKTOGCWriter();
		baseWriter.Reset();
		baseWriter.Write(g);
		String wkt_p = baseWriter.WKT();
		Assert.assertEquals(wkt,wkt_p);
	}
	
	@Test
	public void multilinestring_test1(){
		String wkt = "MULTILINESTRING((3 4,10 50,20 25),(-5 -8,-10 -8,-15 -4))";
		GsWKTOGCReader baseReader = new GsWKTOGCReader(null);
		baseReader.Begin(wkt);
		GsGeometry g = baseReader.Read();
		GsWKTOGCWriter baseWriter = new GsWKTOGCWriter();
		baseWriter.Reset();
		baseWriter.Write(g);
		String wkt_p = baseWriter.WKT();
		Assert.assertEquals(wkt,wkt_p);
	}
	
	@Test
	public void multilinestring_test2(){
		String wkt = "MULTILINESTRING((10 10,20 20,10 40),(40 40,30 30,40 20,30 10))";
		GsWKTOGCReader baseReader = new GsWKTOGCReader(null);
		baseReader.Begin(wkt);
		GsGeometry g = baseReader.Read();
		GsWKTOGCWriter baseWriter = new GsWKTOGCWriter();
		baseWriter.Reset();
		baseWriter.Write(g);
		String wkt_p = baseWriter.WKT();
		Assert.assertEquals(wkt,wkt_p);
	}
	
	@Test
	public void polygon_test1(){
		String wkt = "POLYGON((1 1,5 1,5 5,1 5,1 1),(2 2,2 3,3 3,3 2,2 2))";
		GsWKTOGCReader baseReader = new GsWKTOGCReader(null);
		baseReader.Begin(wkt);
		GsGeometry g = baseReader.Read();
		GsWKTOGCWriter baseWriter = new GsWKTOGCWriter();
		baseWriter.Reset();
		baseWriter.Write(g);
		String wkt_p = baseWriter.WKT();
		Assert.assertEquals(wkt,wkt_p);
	}
	
	@Test
	public void polygon_test2(){
		String wkt = "POLYGON((30 10,40 40,20 40,10 20,30 10))";
		GsWKTOGCReader baseReader = new GsWKTOGCReader(null);
		baseReader.Begin(wkt);
		GsGeometry g = baseReader.Read();
		GsWKTOGCWriter baseWriter = new GsWKTOGCWriter();
		baseWriter.Reset();
		baseWriter.Write(g);
		String wkt_p = baseWriter.WKT();
		Assert.assertEquals(wkt,wkt_p);
	}
	
	@Test
	public void polygon_test3(){
		String wkt = "POLYGON((35 10,45 45,15 40,10 20,35 10),(20 30,35 35,30 20,20 30))";
		GsWKTOGCReader baseReader = new GsWKTOGCReader(null);
		baseReader.Begin(wkt);
		GsGeometry g = baseReader.Read();
		GsWKTOGCWriter baseWriter = new GsWKTOGCWriter();
		baseWriter.Reset();
		baseWriter.Write(g);
		String wkt_p = baseWriter.WKT();
		Assert.assertEquals(wkt,wkt_p);
	}
	
	@Test
	public void multipolygon_test1(){
		String wkt = "MULTIPOLYGON(((1 1,5 1,5 5,1 5,1 1),(2 2,2 3,3 3,3 2,2 2)),((6 3,9 2,9 4,6 3)))";
		GsWKTOGCReader baseReader = new GsWKTOGCReader(null);
		baseReader.Begin(wkt);
		GsGeometry g = baseReader.Read();
		GsWKTOGCWriter baseWriter = new GsWKTOGCWriter();
		baseWriter.Reset();
		baseWriter.Write(g);
		String wkt_p = baseWriter.WKT();
		boolean t1 = wkt_p.contains("MULTIPOLYGON");
		 Assert.assertTrue(t1);
		 boolean p1 = wkt_p.contains("((1 1,5 1,5 5,1 5,1 1),(2 2,2 3,3 3,3 2,2 2))");
		 Assert.assertTrue(p1);
		 boolean p2 = wkt_p.contains("((6 3,9 2,9 4,6 3))");
		 Assert.assertTrue(p2);
	}

}
