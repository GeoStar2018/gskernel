package unittest;

import static org.junit.Assert.*;

import java.io.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.geostar.kernel.*;
import com.sun.corba.se.spi.ior.ObjectId;
public class geometry {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.loadLibrary("gsjavaport");
		GsKernel.Initialize();
		System.out.println("loadlibrary succeed");
		System.out.println("geometry");
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
//	@Test
//	public void errorMultiPolygonCrash() 
//	{
//		System.out.println("errorMultiPolygonCrash");
//		InputStream stream = null;
//		BufferedReader reader = null;
//		try { 
//			String strCurDir = System.getProperty("user.dir");
//			String filePath = strCurDir+ "/data/coord.txt"; 
//			stream = new FileInputStream(filePath);
//			
//			reader = new BufferedReader(new InputStreamReader(stream));
//			
//			List<Double> dList = new ArrayList<Double>();
//			String str = reader.readLine();
//			
//			while(null != str) {
//				String[] ss = str.split(",");
//				for(int i=0;i<ss.length;i++) {
//					ss[i] = ss[i].trim();
//					if(ss[i].length() > 0) {
//						double d = Double.parseDouble(ss[i]);
//						dList.add(d);
//					}
//				}
//				str = reader.readLine();
//			}
//			
//			int size = dList.size();
//			System.out.println("size =" + size);
//			double[] coord = new double[size];
//			for(int i=0;i<size;i++) {
//				coord[i] = dList.get(i);
//			}
//			
//			GeoGeometryFactory fac = new GeoGeometryFactory();
//			int[] eleinfo = {1, 1003, 1, 6117, 2003, 1};
//			GsGeometry gptr = GsGeometryFactory.CreateGeometryFromOracle(eleinfo, eleinfo.length, coord, coord.length, 2);
////			GeoGeometry geo = fac.createGeometryByType(gptr);
//			GsGeometryType type = gptr.GeometryType();
//			if(GsGeometryType.eGeometryTypePolygon == type) {
//				GsPolygon gsGeometry = GsPolygon.DowncastTo(GsGeometryCollection.DowncastTo(gptr));
//				if(gsGeometry.IsSimple()) {
//					System.out.println("IsSimple");
//				} else {
//					System.out.println("IsSimple----------false");
//				}
//				if(gsGeometry.IsMultiPolygon()) {
//					System.out.println("IsMultiPolygon");
//				} else {
//					System.out.println("IsMultiPolygon----------false");
//				}
//				System.out.println("ok11111");
//			}
//			System.out.println("ok2222222");
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if(null != reader) {
//				try {
//					reader.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			if(null != stream) {
//				try {
//					stream.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//	@Test
//	public void geometryDowncastToRing() throws IOException
//	{ 
//		System.out.println("geometryDowncastToRing");
//		String strCurDir = System.getProperty("user.dir");
//		String filePath = strCurDir+ "/data/ogs.bin"; 
//		InputStream in = new FileInputStream(filePath);
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		byte[] buffer = new byte[1024];
//		int n = 0;
//		while ((n = in.read(buffer)) != -1) {
//		      out.write(buffer, 0, n);
//		 }
//		byte[] data = out.toByteArray();
//		in.close();
//		GsGeometry geomPtr = GsGeometryFactory.CreateGeometryFromBlob(data ,data.length, GsEndian.eLittleEndian);
//		System.out.println("geometry type " + geomPtr.GeometryType());
//		
//		GsWKTOGCWriter baseWriter2 = new GsWKTOGCWriter();
//		baseWriter2.Write(geomPtr);
//		String wkt2 = baseWriter2.WKT();
//			
//		System.out.println("geometry type 2" + geomPtr.GeometryType());
//		
//		GsPolygon gsGeometry = GsPolygon.DowncastTo(GsGeometryCollection.DowncastTo(geomPtr));
//		GsWKTOGCWriter writer = new GsWKTOGCWriter ();
//		if(!writer.Write(gsGeometry))
//		{
//			System.out.println("write false");
//		}
//
//		System.out.println(writer.WKT());
//	}
	void printfGeometry(GsGeometry pGeometry )
	{
		GsGeometryBlob pBlob = pGeometry.GeometryBlobPtr();
		
		
		int[] elemInfo = new int[pBlob.InterpretLength()];
		double[] ordinates=new double[pBlob.CoordinateLength()];
		pBlob.Interpret(elemInfo);
		pBlob.Coordinate(ordinates);
		
		for(int i =0; i< elemInfo.length;i++)
		{
			System.out.println(elemInfo[i]);
		}
		for(int i =0; i< ordinates.length;i++)
		{
			System.out.println(ordinates[i]);
		}
		System.out.println("end");
	}
	void geometryToGeoJson(GsGeometry pGeometry)
	{
		GsGeoJSONOGCWriter Gjson= new GsGeoJSONOGCWriter();
		Gjson.Reset();
		Gjson.Attribute("qwe", new GsAny(1.111));
		Gjson.Write(pGeometry);
		System.out.println(Gjson.GeoJSON());
	}

	@Test
	public void Geometry2GeoJson()
	{ /*
		System.out.println("GeometryRepeatPointTest");
			GsShpGeoDatabaseFactory  pFactory = new GsShpGeoDatabaseFactory();
			GsConnectProperty conn  = new GsConnectProperty();
			String strCurDir = System.getProperty("user.dir");
			System.out.println(strCurDir);
			conn.setServer(strCurDir);
			GsGeoDatabase pDB = pFactory.Open(conn);
			if(pDB == null)
			{
				System.out.println("GeometryRepeatPointTest no data");
				return ;
			}
			GsFeatureClass pFcsClass =  pDB.OpenFeatureClass("TDYT");
			GsFeature pFeature =  pFcsClass.Feature(12576);
			int ObjectId = pFeature.ValueInt(2);
			GsGeometry pGeometry =  pFeature.Geometry();
			geometryToGeoJson(pGeometry);
			*/
			
			
	}
	@Test
	public void GeometrySimplify()
	{
		GsMultiPoint point = new GsMultiPoint();
		point.Add(0.1, 0.1);
		point.Add(0.1, 0.1);
		GsTopologyPreservingGeometrySimplifier spGeometrySimplifier = new GsTopologyPreservingGeometrySimplifier();
		spGeometrySimplifier.Tolerance(0.0001);
		GsGeometry pGeometry = spGeometrySimplifier.Simplify(point);
	}
	public void test(BaseFeature feature)
	{
		for(int i=0;i<100000;i++)
		{
			System.out.print("-");
		}
		System.out.println("");
		feature.setOID(105);
		
		GeoGeometry geo2 = feature.getGeometry();
		GeoPoint point2 = (GeoPoint) geo2;
		System.out.println("before System.out.println(point2.getX());");
		
		System.out.println(point2.getX());
		System.out.println("after System.out.println(point2.getX());");
		
	}
	
	//内嵌类
	class BaseFeature{
		
		private GeoGeometry geometry;
		private int OID;
		private String AttributeInfo;
		
		public GeoGeometry getGeometry() {
			return geometry;
		}
		public void setGeometry(GeoGeometry geometry) {
			this.geometry = geometry;
		}
		public int getOID() {
			return OID;
		}
		public void setOID(int oID) {
			OID = oID;
		}
		public String getAttributeInfo() {
			return AttributeInfo;
		}
		public void setAttributeInfo(String attributeInfo) {
			AttributeInfo = attributeInfo;
		}
	}
	
	class GeoGeometry{
		private GsGeometry innerGeometry;
		private GeoGeometryFactory factory;

		public GsGeometry getInnerGeometry() {
			return innerGeometry;
		}

		public void setInnerGeometry(GsGeometry innerGeometry) {
			this.innerGeometry = innerGeometry;
		}

		public GeoGeometryFactory getFactory() {
			return factory;
		}

		public void setFactory(GeoGeometryFactory factory) {
			this.factory = factory;
		}
	}
	
	class GeoPoint extends GeoGeometry{
		private GsPoint basePoint;
		
		public GeoPoint(GsPoint basePoint) {
			this.basePoint = basePoint;
			setInnerGeometry(basePoint);
		}
		
		public double getX() {
			return this.getBasePoint().X();
		}
		
		public double getY() {
			return this.getBasePoint().Y();
		}

		public GsPoint getBasePoint() {
			return basePoint;
		}

		public void setBasePoint(GsPoint basePoint) {
			this.basePoint = basePoint;
		}
		
		
	}
	
	class GeoGeometryFactory{
		public GeoGeometry createGeometryByType(GsGeometry geom) {
			if (null == geom) { // 对象为空则返回空
				return null;
			}
			GeoGeometry g;
			GsGeometryType type = geom.GeometryType();
			switch (type)
			{
			   case eGeometryTypePoint:
				   g = new GeoPoint(GsPoint.DowncastTo(geom));
				   break;
			   default:
				   g = null;   
				   
			}
			g.setFactory(this);
			return g;
		}
		
	}

}
