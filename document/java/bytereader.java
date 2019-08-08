package unittest;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.geostar.kernel.GsEndian;
import com.geostar.kernel.GsGeometry;
import com.geostar.kernel.GsGeometryFactory;
import com.geostar.kernel.GsGeometryRelationResult;
import com.geostar.kernel.GsKernel;
import com.geostar.kernel.GsWKTOGCReader;

public class bytereader {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.loadLibrary("gsjavaport");
		GsKernel.Initialize();
		System.out.println("loadlibrary succeed");
		System.out.println("bytereader");
	}
	
	@Test
	public void point_test1(){
		String wkt = "POINT(117 56)";
		GsWKTOGCReader baseReader = new GsWKTOGCReader(null);
		baseReader.Begin(wkt);
		GsGeometry g = baseReader.Read();
		int length = (int) g.GeometryBlobPtr().BufferSize();
		byte[] blob = new byte[length];
		g.GeometryBlobPtr().CopyToArray(blob, length);
        GsGeometryFactory.ConvertByteOrderToStorageBlob(blob, length, GsEndian.eLittleEndian);
        
        int nLen = blob.length;
	    GsGeometry geomPtr = GsGeometryFactory.CreateGeometryFromBlob(blob, nLen, GsEndian.eLittleEndian);
	    Assert.assertTrue(geomPtr.IsEqual(g)==GsGeometryRelationResult.eIsTrue);
	}
	
	@Test
	public void multipoint_test1(){
		String wkt = "MULTIPOINT(52.468920393000076 26.990185387000054,"
        		+ "17.057797165000068 35.627044711000053,30.876772083000048 11.443838604000064,"
        		+ "43.400218103000043 57.219193021000081,76.220283533000043 44.263904035000053,"
        		+ "32.172300981000035 41.672846238000034)";
		GsWKTOGCReader baseReader = new GsWKTOGCReader(null);
		baseReader.Begin(wkt);
		GsGeometry g = baseReader.Read();
		int length = (int) g.GeometryBlobPtr().BufferSize();
		byte[] blob = new byte[length];
		g.GeometryBlobPtr().CopyToArray(blob, length);
        GsGeometryFactory.ConvertByteOrderToStorageBlob(blob, length, GsEndian.eLittleEndian);
        
        int nLen = blob.length;
	    GsGeometry geomPtr = GsGeometryFactory.CreateGeometryFromBlob(blob, nLen, GsEndian.eLittleEndian);
	    Assert.assertTrue(geomPtr.IsEqual(g)==GsGeometryRelationResult.eIsTrue);
	}
	
	@Test
	public void line_test1(){
		String wkt = "LINESTRING(3 4,10 50,20 25)";
		GsWKTOGCReader baseReader = new GsWKTOGCReader(null);
		baseReader.Begin(wkt);
		GsGeometry g = baseReader.Read();
		int length = (int) g.GeometryBlobPtr().BufferSize();
		byte[] blob = new byte[length];
		g.GeometryBlobPtr().CopyToArray(blob, length);
        GsGeometryFactory.ConvertByteOrderToStorageBlob(blob, length, GsEndian.eLittleEndian);
        
        int nLen = blob.length;
	    GsGeometry geomPtr = GsGeometryFactory.CreateGeometryFromBlob(blob, nLen, GsEndian.eLittleEndian);
	    Assert.assertTrue(geomPtr.IsEqual(g)==GsGeometryRelationResult.eIsTrue);
	}
	
	@Test
	public void multiline_test1(){
		String wkt = "MULTILINESTRING((12 1.5, 15 3.2),(100 1,200 42),(65.5 41.3,2.6 2.3))";
		GsWKTOGCReader baseReader = new GsWKTOGCReader(null);
		baseReader.Begin(wkt);
		GsGeometry g = baseReader.Read();
		int length = (int) g.GeometryBlobPtr().BufferSize();
		byte[] blob = new byte[length];
		g.GeometryBlobPtr().CopyToArray(blob, length);
        GsGeometryFactory.ConvertByteOrderToStorageBlob(blob, length, GsEndian.eLittleEndian);
        
        int nLen = blob.length;
	    GsGeometry geomPtr = GsGeometryFactory.CreateGeometryFromBlob(blob, nLen, GsEndian.eLittleEndian);
	    Assert.assertTrue(geomPtr.IsEqual(g)==GsGeometryRelationResult.eIsTrue);
	}
	
	@Test
	public void polygon_test1(){
		String wkt = "POLYGON((30 10, 10 20,20 40,40 40,30 10))";
		GsWKTOGCReader baseReader = new GsWKTOGCReader(null);
		baseReader.Begin(wkt);
		GsGeometry g = baseReader.Read();
		int length = (int) g.GeometryBlobPtr().BufferSize();
		byte[] blob = new byte[length];
		g.GeometryBlobPtr().CopyToArray(blob, length);
        GsGeometryFactory.ConvertByteOrderToStorageBlob(blob, length, GsEndian.eLittleEndian);
        
        int nLen = blob.length;
	    GsGeometry geomPtr = GsGeometryFactory.CreateGeometryFromBlob(blob, nLen, GsEndian.eLittleEndian);
	    Assert.assertTrue(geomPtr.IsEqual(g)==GsGeometryRelationResult.eIsTrue);
	}
	
	@Test
	public void polygon_test2(){
		String wkt = "POLYGON((0 0,10 0,10 10,0 10,0 0),(5 5,5 7,7 7,7 5,5 5))";
		GsWKTOGCReader baseReader = new GsWKTOGCReader(null);
		baseReader.Begin(wkt);
		GsGeometry g = baseReader.Read();
		int length = (int) g.GeometryBlobPtr().BufferSize();
		byte[] blob = new byte[length];
		g.GeometryBlobPtr().CopyToArray(blob, length);
        GsGeometryFactory.ConvertByteOrderToStorageBlob(blob, length, GsEndian.eLittleEndian);
        
        int nLen = blob.length;
	    GsGeometry geomPtr = GsGeometryFactory.CreateGeometryFromBlob(blob, nLen, GsEndian.eLittleEndian);
	    Assert.assertTrue(geomPtr.IsEqual(g)==GsGeometryRelationResult.eIsTrue);
	}
	
	@Test
	public void multipolygon_test1(){
		String wkt = "MULTIPOLYGON (((40 40, 20 45, 45 30, 40 40)),((20 35, 45 20, 30 5, 10 10, 10 30, 20 35),(30 20, 20 25, 20 15, 30 20)))";
		GsWKTOGCReader baseReader = new GsWKTOGCReader(null);
		baseReader.Begin(wkt);
		GsGeometry g = baseReader.Read();
		int length = (int) g.GeometryBlobPtr().BufferSize();
		byte[] blob = new byte[length];
		g.GeometryBlobPtr().CopyToArray(blob, length);
        GsGeometryFactory.ConvertByteOrderToStorageBlob(blob, length, GsEndian.eLittleEndian);
        
        int nLen = blob.length;
	    GsGeometry geomPtr = GsGeometryFactory.CreateGeometryFromBlob(blob, nLen, GsEndian.eLittleEndian);
	    Assert.assertTrue(geomPtr.IsEqual(g)==GsGeometryRelationResult.eIsTrue);
	}

}
