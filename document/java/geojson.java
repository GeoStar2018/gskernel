package unittest;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.geostar.kernel.GsGeoJSONOGCReader;
import com.geostar.kernel.GsGeoJSONOGCWriter;
import com.geostar.kernel.GsGeometry;
import com.geostar.kernel.GsKernel;

public class geojson {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.loadLibrary("gsjavaport");
		GsKernel.Initialize();
		System.out.println("loadlibrary succeed");
		System.out.println("geojson");
	}
	
	@Test
	public void point_test1(){
		String jsonstr= "{\"type\":\"Point\",\"coordinates\":[100,0]}";
		GsGeoJSONOGCReader geoJsonReader = new GsGeoJSONOGCReader("");
		geoJsonReader.Begin(jsonstr);
	    GsGeometry geo = geoJsonReader.Read();
		GsGeoJSONOGCWriter geoJsonWriter = new GsGeoJSONOGCWriter();
		geoJsonWriter.Reset();
		geoJsonWriter.Write(geo);
		String result = geoJsonWriter.GeoJSON();
		System.out.println(result);
		//Assert.assertTrue(result.equalsIgnoreCase(jsonstr));
	}
	
	@Test
	public void multipoint_test1(){
		String jsonstr= "{\"type\":\"MultiPoint\",\"coordinates\":[[100.45,0.23],[101.7,1.1]]}";
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
	public void line_test1(){
		String jsonstr= "{\"type\":\"LineString\",\"coordinates\":[[100.777,0.869],[101.888,1.39]]}";
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
	public void multiline_test1(){
		String jsonstr= "{\"type\":\"MultiLineString\",\"coordinates\":[[[100.57,0.8],[101.66,1.8]],[[102.77,2.475],[103.877,3.99]]]}";
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
	public void polygon_test1(){
		String jsonstr= "{\"type\":\"Polygon\",\"coordinates\":[[[100.88,0.5],[101.44,0.7],[101.36,1.44],[100.77,1.2],[100.88,0.5]]]}";
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
	public void polygon_test2(){
		String jsonstr= "{\"type\":\"Polygon\",\"coordinates\":[[[100.88,0.9],[101.7,0.8],[101.88,1.5],[100.5,1.3],[100.88,0.9]],"
				+ "[[100.9,1.1],[100.9,1.2],[101.2,1.27],[101.2,1.1],[100.9,1.1]]]}";
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
	public void multipolygon_test1(){
		String jsonstr= "{\"type\":\"MultiPolygon\",\"coordinates\":[[[[102.1,2.1],[103.1,2.1],[103.1,3.1],[102.1,3.1],[102.1,2.1]]],"
				+ "[[[100.1,0.1],[101.1,0.1],[101.1,1.1],[100.1,1.1],[100.1,0.1]],[[100.2,0.2],[100.2,0.8],[100.8,0.8],[100.8,0.2],"
				+ "[100.2,0.2]]]]}";
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
}
