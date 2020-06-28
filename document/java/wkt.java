package unittest;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.geostar.kernel.GsEnvelope;
import com.geostar.kernel.GsGeometry;
import com.geostar.kernel.GsGeometryType;
import com.geostar.kernel.GsGlobeConfig;
import com.geostar.kernel.GsGrowByteBuffer;
import com.geostar.kernel.GsKernel;
import com.geostar.kernel.GsPath;
import com.geostar.kernel.GsPolygon;
import com.geostar.kernel.GsPolyline;
import com.geostar.kernel.GsRing;
import com.geostar.kernel.GsSegment;
import com.geostar.kernel.GsSpatialReference;
import com.geostar.kernel.GsWKBOGCReader;
import com.geostar.kernel.GsWKBOGCWriter;
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

	
	 GsGeometry Normalize(GsGeometry geo)
	{
		if (null == geo)
			return null;
		
		GsGeometryType eType = geo.GeometryType();
		switch (eType)
		{
			/// \brief 未知或无效几何类型
		case eGeometryTypeUnknown:
		case eGeometryTypeAnnotation:
			return geo;
			/// \brief 单点
		case eGeometryTypePoint:
		case eGeometryTypePolygon:
		case eGeometryTypeMultiPoint:
		case eGeometryTypePolyline:
		case eGeometryTypeCollection:
			return geo;
			/// \brief 单段线
		case eGeometryTypePath:
		{
			GsPolyline ptrPolyline = new GsPolyline();
			ptrPolyline.Add(geo);
			return ptrPolyline;
		}
		case eGeometryTypeEnvelope:
		{
			GsRing ptrRing = new GsRing(GsEnvelope.DowncastTo(geo));
			GsPolygon ptrPolygon = new GsPolygon();
			ptrPolygon.Add(ptrRing);
			return ptrPolygon;
		}
		case eGeometryTypeLine:
		case eGeometryTypeCircleArc:
		{
			GsPath ptrPath = new GsPath();
			ptrPath.Add(GsSegment.DowncastTo(geo));

			GsPolyline ptrPolyline = new GsPolyline();
			ptrPolyline.Add(ptrPath);
			return ptrPolyline;
		}
		case eGeometryTypeRing:
		{
			GsPolygon ptrPolygon = new GsPolygon();
			ptrPolygon.Add(geo);
			return ptrPolygon;
		}
		default:
			break;
		}
		return geo;
	}

	 void geoWriter(String str,boolean  bmutil)
	{	
		GsWKTOGCReader r =  new GsWKTOGCReader(str);
		GsGeometry ptrGeo = r.Read();	
		//写wkb
		GsGrowByteBuffer buffer = new GsGrowByteBuffer();
		GsWKBOGCWriter writer = new GsWKBOGCWriter(buffer);
		GsGeometry ptrGeoNor =  Normalize(ptrGeo);
		
		writer.Write(ptrGeoNor, bmutil);
		
		byte[] pBuff = new byte[(int)buffer.BufferSize()];
		buffer.CopyToArray(pBuff,  (int)buffer.BufferSize());
		System.out.print("WKB type int \n");
		for(int i = 1; i < 5; i++)
		{
			System.out.print(pBuff[i]+" ");
		}
		System.out.print("\n");
		//读wkb
		GsWKBOGCReader wkbReader = new GsWKBOGCReader(buffer);
		GsGeometry gsGeo = wkbReader.Read();
		//写wkt
		GsWKTOGCWriter wktwrite = new GsWKTOGCWriter();
		GsGeometry gsGeoNor =  Normalize(gsGeo);
		
		wktwrite.Write(gsGeoNor, bmutil);
		System.out.print("转换前 \n");
		System.out.print(str+"\n");
		System.out.print("转换后 \n");
		System.out.print(wktwrite.WKT()+"\n");
	}

	 void  MultiGeowriter(String str)
	{
		geoWriter(str,true);
	}

	 void  SingleGeowriter(String str){
			geoWriter(str,false);
	 }
	 
		@Test
		public  void MultiGeowriter() {

        System.loadLibrary("gsjavaport");
		GsKernel.Initialize();
		System.out.println("loadlibrary succeed");
		String strCurDir = System.getProperty("user.dir");
		strCurDir += "/../data/coordinatesystem/EPSG.txt";
		System.out.println(strCurDir);
		GsGlobeConfig.Instance().Child("Kernel/SpatialRererence/EPSG")
				.Value(strCurDir);
		System.out.println("spatialreference");
	
		System.out.println("spatialreference");
		
		GsSpatialReference p = new GsSpatialReference(4326);
		System.out.println(p.EquatorialRadiusA());
		

		

		MultiGeowriter("Polygon  ((10 10, 20 15, 20 20, 10 20, 10 10))");
		MultiGeowriter("LineString  (10 10, 20 15, 20 20, 10 20, 10 10)");
		MultiGeowriter("POINT ((1 1 ))"); 
		
		MultiGeowriter("Polygon Z((10 10 6, 20 15 5, 20 20 6, 10 20 5, 10 10 6))");
		MultiGeowriter("LineString Z (10 10 5, 20 15 5, 20 20 5, 10 20 5, 10 10 6)");
		MultiGeowriter("POINT Z ((1 1 1))"); 
		MultiGeowriter("MULTIPOINT ((1 1 ),(2 2 ))"); 
		MultiGeowriter("MULTIPOINT Z((1 1 1),(2 2 2))"); 
		
		MultiGeowriter("MultiLineString( (10 10, 10 20, 20 20, 20 15, 10 10) ,(12 12, 15 12, 15 15, 12 15, 12 12), (60 60, 70 70, 80 60, 60 60 )) ");
		MultiGeowriter("MultiPolygon  ( ((10 10, 10 20, 20 20, 20 15, 10 10) ,(12 12, 15 12, 15 15, 12 15, 12 12)), ((60 60, 70 70, 80 60, 60 60 )) ) ");
		MultiGeowriter("Polygon  ((0 0,300 0,300 300,0 300,0 0),(100 100, 100 200, 200 200, 200 100, 100 100))");
		MultiGeowriter( "MultiLineString Z((10 10 10, 20 20 20), (15 15 1, 30 15 1))");
		
		
		SingleGeowriter("Polygon  ((10 10, 20 15, 20 20, 10 20, 10 10))");
		SingleGeowriter("LineString  (10 10, 20 15, 20 20, 10 20, 10 10)");
		SingleGeowriter("POINT ((1 1 ))"); 
		SingleGeowriter("Polygon Z ((10 10 6, 20 15 5, 20 20 6, 10 20 5, 10 10 6))");
		SingleGeowriter("LineString Z (10 10 5, 20 15 5, 20 20 5, 10 20 5, 10 10 6)");
		SingleGeowriter("POINT Z ((1 1 1))"); 
		SingleGeowriter("MULTIPOINT ((1 1 ),(2 2 ))"); 
		SingleGeowriter("MULTIPOINT Z((1 1 1),(2 2 2))"); 
		
		SingleGeowriter("MultiLineString( (10 10, 10 20, 20 20, 20 15, 10 10) ,(12 12, 15 12, 15 15, 12 15, 12 12), (60 60, 70 70, 80 60, 60 60 )) ");
		SingleGeowriter("MultiPolygon  ( ((10 10, 10 20, 20 20, 20 15, 10 10) ,(12 12, 15 12, 15 15, 12 15, 12 12)), ((60 60, 70 70, 80 60, 60 60 )) ) ");
		SingleGeowriter("Polygon  ((0 0,300 0,300 300,0 300,0 0),(100 100, 100 200, 200 200, 200 100, 100 100))");
		SingleGeowriter( "MultiLineString Z((10 10 10, 20 20 20), (15 15 1, 30 15 1))");
		
		System.out.print("\n测试完成"+"\n");
	}

}
