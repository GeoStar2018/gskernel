package unittest.smoke;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.geostar.kernel.GsEndian;
import com.geostar.kernel.GsGML3OGCWriter;
import com.geostar.kernel.GsGMLOGCReader;
import com.geostar.kernel.GsGMLOGCWriter;
import com.geostar.kernel.GsGeoJSONOGCReader;
import com.geostar.kernel.GsGeoJSONOGCWriter;
import com.geostar.kernel.GsGeometry;
import com.geostar.kernel.GsGeometryCollection;
import com.geostar.kernel.GsGeometryFactory;
import com.geostar.kernel.GsKernel;
import com.geostar.kernel.GsMultiPoint;
import com.geostar.kernel.GsPath;
import com.geostar.kernel.GsPoint;
import com.geostar.kernel.GsPolygon;
import com.geostar.kernel.GsPolyline;
import com.geostar.kernel.GsRing;
import com.geostar.kernel.GsWKTOGCReader;
import com.geostar.kernel.GsWKTOGCWriter;

public class SmokeGeometry {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.loadLibrary("gsjavaport");
		GsKernel.Initialize();
		System.out.println("loadlibrary succeed");
	}
	
	@Test
	public void point_test1(){
		GsPoint point = new GsPoint(101.58,26.76);
		Assert.assertFalse(point.IsEmpty());
	}
	@Test
	public void point_GMLReader1(){
		String gml = "<gml:Point><gml:coordinates>39.923615,116.38094</gml:coordinates></gml:Point>";
		GsGMLOGCReader baseReader = new GsGMLOGCReader("");
		baseReader.Begin(gml);
		GsGeometry g = baseReader.Read();
		Assert.assertFalse(g.IsEmpty());
		GsGMLOGCWriter gmlWriter = new GsGMLOGCWriter();
		gmlWriter.Reset();
		gmlWriter.Write(g);
		String gml_p = gmlWriter.GML();
		System.out.println(gml_p);
		Assert.assertEquals(gml,gml_p);
	}
	@Test
	public void point_WKTReader1(){
		String wkt = "POINT(112 34)";
		GsWKTOGCReader baseReader = new GsWKTOGCReader(null);
		baseReader.Begin(wkt);
		GsGeometry g = baseReader.Read();
		Assert.assertFalse(g.IsEmpty());
		GsWKTOGCWriter baseWriter = new GsWKTOGCWriter();
		baseWriter.Reset();
		baseWriter.Write(g);
		String wkt_p = baseWriter.WKT();
		System.out.println(wkt_p);
		Assert.assertEquals(wkt,wkt_p);
	}
	@Test
	public void point_ByteReader1(){
		String wkt = "POINT(120.58 33.777)";
		GsWKTOGCReader baseReader = new GsWKTOGCReader(null);
		baseReader.Begin(wkt);
		GsGeometry g = baseReader.Read();
		int length = (int) g.GeometryBlobPtr().BufferSize();
		byte[] blob = new byte[length];
		g.GeometryBlobPtr().CopyToArray(blob, length);
        GsGeometryFactory.ConvertByteOrderToStorageBlob(blob, length, GsEndian.eLittleEndian);
	}
	@Test
	public void point_GeoJsonReader1(){
		String jsonstr= "{\"type\":\"Point\",\"coordinates\":[118.52,67]}";
		GsGeoJSONOGCReader geoJsonReader = new GsGeoJSONOGCReader("");
		geoJsonReader.Begin(jsonstr);
	    GsGeometry geo = geoJsonReader.Read();
		GsGeoJSONOGCWriter geoJsonWriter = new GsGeoJSONOGCWriter();
		geoJsonWriter.Reset();
		geoJsonWriter.Write(geo);
		String result = geoJsonWriter.GeoJSON();
		System.out.println(result);
		Assert.assertTrue(result.equalsIgnoreCase(jsonstr));
	}
	@Test
	public void multiPoint_test1(){
		GsMultiPoint mPoint = new GsMultiPoint(101.58,26.76);
		mPoint.Add(101.42, 62.05);
		Assert.assertFalse(mPoint.IsEmpty());
	}
	@Test
	public void line_test1(){
		GsPath path = new GsPath();
		Assert.assertNotNull(path);
	}
	@Test
	public void ring_test1(){
		GsRing ring = new GsRing();
		Assert.assertNotNull(ring);
	}
	@Test
	public void multiline_test1(){
		GsPolyline polyline = new GsPolyline();
		Assert.assertNotNull(polyline);
	}
	@Test
	public void polygon_test1(){
		GsPolygon polygon = new GsPolygon();
		Assert.assertNotNull(polygon);
	}
	@Test
	public void polygon_test2(){
		String gml = "<gml:Polygon><gml:outerBoundaryIs><gml:LinearRing><gml:coordinates>109.2329754960001,34.38871420600003 109.2329856600001,34.38832485800003 109.233018911,34.38832557900003 109.2330109390001,34.38871381100006 109.2329754960001,34.38871420600003</gml:coordinates></gml:LinearRing></gml:outerBoundaryIs><gml:innerBoundaryIs><gml:LinearRing><gml:coordinates>109.23298808,34.38817142600004 109.2329970970001,34.38779824800002 109.2330242610001,34.38779779600003 109.2330103180001,34.38817118100008 109.23298808,34.38817142600004</gml:coordinates></gml:LinearRing></gml:innerBoundaryIs></gml:Polygon>";
		GsGMLOGCReader baseReader = new GsGMLOGCReader("");
		baseReader.Begin(gml);
		GsGeometry g = baseReader.Read();
		Assert.assertFalse(g.IsEmpty());
	}
	@Test
	public void polygon_test3(){
		String gml = "<gml:Polygon><gml:outerBoundaryIs><gml:LinearRing><gml:coordinates>109.2259330640001,34.38344946200004 109.2262294890001,34.38191034900007 109.2262510860001,34.38191292100004 109.2261008580001,34.38267785100004 109.2260568310001,34.38291368700004 109.2260029820001,34.38318611100004 109.2259550570001,34.38345060700004 109.2259330640001,34.38344946200004</gml:coordinates></gml:LinearRing></gml:outerBoundaryIs></gml:Polygon>";
		GsGMLOGCReader baseReader = new GsGMLOGCReader("");
		baseReader.Begin(gml);
		GsGeometry g = baseReader.Read();
		Assert.assertFalse(g.IsEmpty());
	}
	@Test
	public void polygon_test4(){
		String gml = "<gml:Polygon><gml:outerBoundaryIs><gml:LinearRing><gml:coordinates>109.2329754960001,34.38871420600003 109.2329856600001,34.38832485800003 109.233018911,34.38832557900003 109.2330109390001,34.38871381100006 109.2329754960001,34.38871420600003</gml:coordinates></gml:LinearRing></gml:outerBoundaryIs><gml:innerBoundaryIs><gml:LinearRing><gml:coordinates>109.23298808,34.38817142600004 109.2329970970001,34.38779824800002 109.2330242610001,34.38779779600003 109.2330103180001,34.38817118100008 109.23298808,34.38817142600004</gml:coordinates></gml:LinearRing></gml:innerBoundaryIs></gml:Polygon>";
		GsGMLOGCReader baseReader = new GsGMLOGCReader("");
		baseReader.Begin(gml);
		GsGeometry g = baseReader.Read();
		Assert.assertFalse(g.IsEmpty());
	}
	@Test
	public void collection_test1(){
		GsGeometryCollection coll = new GsGeometryCollection();
		Assert.assertNotNull(coll);
	}
	@Test
	public void factory_test1(){
		int elemInfo[] = {1,1,1};
		double ordinates[] = {45.2,77.32};
		int dimension = 2;
		GsGeometry gptr = GsGeometryFactory.CreateGeometryFromOracle(elemInfo, elemInfo.length, ordinates, ordinates.length, dimension);
		Assert.assertNotNull(gptr);
	}
	@Test
	public	void ReaderWriter_test1(){
		GsGML3OGCWriter gml3Writer = new GsGML3OGCWriter();
		Assert.assertNotNull(gml3Writer);
		GsGMLOGCReader baseReader = new GsGMLOGCReader("");
		Assert.assertNotNull(baseReader);
		GsGMLOGCWriter gmlWriter = new GsGMLOGCWriter();
		Assert.assertNotNull(gmlWriter);
		GsGeoJSONOGCWriter geoJsonWriter = new GsGeoJSONOGCWriter();
		Assert.assertNotNull(geoJsonWriter);
	}
	
}
