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
import com.geostar.kernel.spatialanalysis.GsAnalysisDataIO;
import com.geostar.kernel.spatialanalysis.GsInterpolationAlgorithmType;
import com.geostar.kernel.spatialanalysis.GsInterpolationKrigingParameter;
import com.geostar.kernel.spatialanalysis.GsRasterInterpolationAnalysis;
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
		GsGeoJSONOGCWriter write = new GsGeoJSONOGCWriter();
		write.Write(pGeometry);
		System.out.println(write.GeoJSON());
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
	
	class InterpolationGrid extends GsAnalysisDataIO
	{
		GsRasterClass ptrRasterCls = null;
		GsRaster ptrRaster = new GsRaster();
		
		public InterpolationGrid(String tiffile, int rowsize, int colsize, float[] Geo, GsBox bbox, GsSpatialReference pSp)
		{
			GsFileGeoDatabaseFactory fac = new GsFileGeoDatabaseFactory();
			GsConnectProperty conn = new GsConnectProperty();			

			GsFile file = new GsFile(tiffile);
			if (file.Exists())
				file.Delete();

			conn.setServer(file.Parent().FullPath());
			
			GsGeoDatabase db = fac.Open(conn);
			if(db == null)
				return;
			GsRasterColumnInfo info = new GsRasterColumnInfo();
			info.setBlockHeight(1);
			info.setBlockWidth(rowsize);
			info.setWidth(rowsize);
			info.setHeight(colsize);
			info.setDataType(GsRasterDataType.eCFloat64RDT);
			
			double[] d = new double[6];
			for (int i = 0; i < 6; ++i)
				d[i] = Geo[i];
			
			info.setGeoTransform(d);
			GsIntVector vec = new GsIntVector();
			vec.add(0);
			info.BandTypes(vec);
			info.setXYDomain(bbox);
			
			ptrRasterCls = db.CreateRasterClass(file.Name(false), GsRasterCreateableFormat.eGTiff, info, pSp);
			ptrRaster.Height(1);
			ptrRaster.Width(rowsize);
		}
		
		public Boolean Writer(int i, int j, byte[] pHead, int nlen)
		{
			ptrRaster.OffsetX(i);
			ptrRaster.OffsetY(j);
			ptrRaster.DataPtr(pHead, nlen);
			return ptrRasterCls.WriteRaster(ptrRaster);
		}
		
		public int OnData(GsFeatureBuffer pData)
		{
			if (pData == null)
				return 0;
			int i = pData.IntValue(2);
			int j = pData.IntValue(3);
			int len = pData.BlobValueLength(4);
			byte[] blob = new byte[len];
			pData.BlobValue(4, blob, len);
			Writer(i, j, blob, len);
			return 0;
		}
	}
	
	@Test
	public void SimpleKriging()
	{
		System.out.println("简单克吕金插值");
		//所用的数据是否满足克吕金的要求没有验证，此测试用例主要是为了演示如何使用，具体数据由上层提供
		double coords[] = {
				10.1, 10.11,		10.0, 10.3,		10.2, 10.5,
				10.101, 10.4,		10.1, 10.2,		10.1, 10.6,
				10.1, 10.6,		10.1, 10.3,		10.0, 10.4
			};

			double value[] = {
				1.05, 1.2, 1.1
			};
			
		GsInterpolationKrigingParameter parameter = new GsInterpolationKrigingParameter();
		parameter.getWeightFactors().add(1);
		parameter.getWeightFactors().add(2);;
		parameter.setType(GsInterpolationAlgorithmType.eSimpleKriging);
		
		GsRasterInterpolationAnalysis Analyser = new GsRasterInterpolationAnalysis(parameter);
		Analyser.Height(3);
		Analyser.Width(3);
		boolean b = Analyser.Interpolate(coords, coords.length, value);
		
		assertTrue(b);
		
		GsMatrix mat = new GsMatrix();
		GsBox box = new GsBox(0, 0, 2.0, 2.0);
		float[] m = new float[6];
		mat.Elements(m);
		
		String strCurDir = System.getProperty("user.dir");
		System.out.println(strCurDir);
		strCurDir += "\\Kriging.tif";
		
		InterpolationGrid grid = new InterpolationGrid(strCurDir, 3, 3, m, box, null);
		Analyser.OutputData(grid);
	}
	
	@Test
	public void Feature2GeoJson()
	{
		String strCurDir = System.getProperty("user.dir");
		
		String strServer = strCurDir +"/Data/400w";
		GsConnectProperty conn = new GsConnectProperty(strServer);
		//conn.setDataSourceType(GsDataSourceType.eShapeFile);
		
		GsShpGeoDatabaseFactory fac = new GsShpGeoDatabaseFactory();
		GsGeoDatabase geoDatabase = fac.Open(conn);
		if(geoDatabase == null)
			return;
		GsFeatureClass featureClass = geoDatabase.OpenFeatureClass("BOU1_4M_S");
		if(featureClass == null)
			return;

		GsFeatureCursor cursor = featureClass.Search();
		long nFeaCount = featureClass.FeatureCount();
		int writerCount = 0;
		GsFeature ptrFea = cursor.Next();

		int i = 0;

		GsFields fields = featureClass.Fields();
		GsFieldVector fds = fields.getFields();
		
		GsGeoJSONOGCWriter writer = new GsGeoJSONOGCWriter();
		
		writer.BeginFeatureCollection();
		//writer.StartArray();
		do
		{
			writer.BeginFeature();
			writer.BeginAttribute();
			if (ptrFea == null)
				break;
			writerCount++;
			for (i = 2; i< fds.size(); i++)
			{
				byte value[] = new byte[10240];
				ptrFea.Value(i, value, 10240);
				double height = ptrFea.ValueDouble(i);
				
				switch(fds.get(i).getType())
				{
				case eBlobType:
					break;
				case eBoolType:
					break;
				case eDateType:
					break;
				case eDoubleType:
					writer.Attribute(fds.get(i).getName(), ptrFea.ValueDouble(i));
					break;
				case eErrorType:
					break;
				case eFloatType:
					break;
				case eGeometryType:
					break;
				case eInt64Type:
					break;
				case eIntType:
					break;
				case eStringType:
					writer.Attribute(fds.get(i).getName(), ptrFea.ValueString(i));
					break;
				case eUInt64Type:
					break;
				case eUIntType:
					break;
				default:
					break;
				
				}

			}
			writer.EndAttribute();
			writer.Write(ptrFea.Geometry());
			writer.EndFeature();
			break;
			//ptrFea = cursor.Next();
		} while (ptrFea != null);

		writer.EndFeatureCollection();
		String json = writer.GeoJSON();

		System.out.println(json);


	}

	@Test
	public void ToVoronoiDiagram()
	{
		System.out.println("ToVoronoiDiagram(),返回泰森多边形");
		
		GsBox box = new GsBox(0.0, 0.0, 3.0, 3.0);
		GsRing ring = new GsRing(box);
		GsPolygon border = new GsPolygon();
		border.Add(ring);
		
		GsMultiPoint multiPoint = new GsMultiPoint();
		multiPoint.Add(1.0, 1.0);
		multiPoint.Add(2.0, 1.0);
		multiPoint.Add(2.0, 2.0);
		multiPoint.Add(1.0, 2.0);
		
		GsPolygon VoronoiDiagram = multiPoint.ToVoronoiDiagram(border);
		long count = VoronoiDiagram.Count();
		assertEquals(count, 4, 0);
		
		GsGeometryBlob blob = multiPoint.GeometryBlobPtr();
		VoronoiDiagram = blob.ToVoronoiDiagram(border);
		count = VoronoiDiagram.Count();
		assertEquals(count, 4, 0);
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
