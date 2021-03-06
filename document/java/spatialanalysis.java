package unittest;

import static org.junit.Assert.assertNotNull;
import java.io.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.geostar.kernel.*;
import com.geostar.kernel.spatialanalysis.*;


public class spatialanalysis {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		System.out.println("bofore loadlibrary");
		System.loadLibrary("gsjavaport");
		System.out.println("loadlibrary");
		System.out.println("spatialreference");
		
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
	void CopyField(GsFieldVector target,GsFieldVector source,String strTag){
		
		GsField f = source.get(0);
		f.setName("OID" + strTag);
		target.add(f);
		for(int i =2;i<source.size();i++)
		{
			f = source.get(i);
			f.setName(f.getName() + strTag);
			target.add(f);
		}
		
	}
	long union(String folder,String f1,String f2,double tol,String strOut){
		GsSqliteGeoDatabaseFactory fac = new GsSqliteGeoDatabaseFactory();
		GsConnectProperty conn = new GsConnectProperty();
		String strServer = folder;
		conn.setServer(strServer);
		GsGeoDatabase ptrGDB =  fac.Open(conn);
		assertNotNull("database is null",ptrGDB);

		GsFeatureClass ptrFeaClass1 = ptrGDB.OpenFeatureClass(f1);
		GsFeatureClass ptrFeaClass2 = ptrGDB.OpenFeatureClass(f2);
		assertNotNull("featureclass1 is null",ptrFeaClass1);
		assertNotNull("featureclass2 is null",ptrFeaClass2);
	
		
		FeatureReader io1 = new FeatureReader(ptrFeaClass1);
		FeatureReader io2 = new FeatureReader(ptrFeaClass2);
		
		GsFeatureClass ptrFeaClassOutput = ptrGDB.OpenFeatureClass(strOut);
		if(null != ptrFeaClassOutput)
		{
			//删除地物类
			ptrFeaClassOutput.Delete();
			//删除封装对象
			ptrFeaClassOutput.delete();
		}
		//第一个地物类的地物。
		GsFields fs = ptrFeaClass1.Fields();
		GsFieldVector vecNew = new GsFieldVector(); 
		GsFieldVector vec = fs.getFields();
		 
		System.out.println("vec.size()");
		System.out.println(vec.size());
		
		//拼装两个地物的属性字段为一个字段。
		CopyField(vecNew,vec,"1");
		CopyField(vecNew,ptrFeaClass2.Fields().getFields(),"2");
		fs.setFields(vecNew);
		
		//创建输出的地物类。
		ptrFeaClassOutput = ptrGDB.CreateFeatureClass(strOut, fs, ptrFeaClass1.GeometryColumnInfo(), ptrFeaClass1.SpatialReference());
		assertNotNull("featureclass2 is null",ptrFeaClassOutput );
		
		
		FeatureWriter ioOut = new FeatureWriter(ptrFeaClassOutput);
		GsOverlayAnalysis over = new GsOverlayAnalysis(tol);
		over.Union(new GsAnalysisDataIO[]{io1, io2},2, GsJoinAttributeType.eJoinAll, ioOut);
		System.out.println("after over.Union");
		ioOut.Commit();
		ioOut.delete();
		io1.delete();
		io2.delete();
		
		//结果数量。
		long nRet = ptrFeaClassOutput.FeatureCount();
		ptrFeaClassOutput.delete();
		ptrFeaClass1.delete();
		ptrFeaClass2.delete();
		over.delete();
		ptrGDB.delete();
		conn.delete();
		fac.delete();
		return nRet;
	}
	
	long erase(String folder,String f1,String f2,double tol,String strOut){
		GsSqliteGeoDatabaseFactory fac = new GsSqliteGeoDatabaseFactory();
		GsConnectProperty conn = new GsConnectProperty();
		String strServer = folder;
		conn.setServer(strServer);
		GsGeoDatabase ptrGDB =  fac.Open(conn);
		assertNotNull("database is null",ptrGDB);
		GsFeatureClass ptrFeaClass1 = ptrGDB.OpenFeatureClass(f1);
		GsFeatureClass ptrFeaClass2 = ptrGDB.OpenFeatureClass(f2);
		assertNotNull("featureclass1 is null",ptrFeaClass1);
		assertNotNull("featureclass2 is null",ptrFeaClass2);
	
		
		FeatureReader io1 = new FeatureReader(ptrFeaClass1);
		FeatureReader io2 = new FeatureReader(ptrFeaClass2);
		
		GsFeatureClass ptrFeaClassOutput = ptrGDB.OpenFeatureClass(strOut);
		if(ptrFeaClassOutput != null)
		{
			//删除地物类
			ptrFeaClassOutput.Delete();
			//删除封装对象
			ptrFeaClassOutput.delete();
		}
		//第一个地物类的地物。
		GsFields fs = ptrFeaClass1.Fields();
		GsFieldVector vecNew = new GsFieldVector(); 
		GsFieldVector vec = fs.getFields();
		 
		System.out.println("vec.size()");
		System.out.println(vec.size());
		
		//拼装两个地物的属性字段为一个字段。
		CopyField(vecNew,vec,"1"); 
		fs.setFields(vecNew);
		
		//创建输出的地物类。
		ptrFeaClassOutput = ptrGDB.CreateFeatureClass(strOut, fs, ptrFeaClass1.GeometryColumnInfo(), ptrFeaClass1.SpatialReference());
		assertNotNull("featureclass2 is null",ptrFeaClassOutput);
		
		
		FeatureWriter ioOut = new FeatureWriter(ptrFeaClassOutput);
		GsOverlayAnalysis over = new GsOverlayAnalysis(tol);
		over.Erase(io1, io2, ioOut);
		ioOut.Commit();
		ioOut.delete();
		io1.delete();
		io2.delete();
		
		//结果数量。
		long nRet = ptrFeaClassOutput.FeatureCount();
		ptrFeaClassOutput.delete();
		ptrFeaClass1.delete();
		ptrFeaClass2.delete();
		over.delete();
		ptrGDB.delete();
		conn.delete();
		fac.delete();
		return nRet;
	}
	

	long intersect(String folder,String f1,String f2,double tol,String strOut){
		GsSqliteGeoDatabaseFactory fac = new GsSqliteGeoDatabaseFactory();
		GsConnectProperty conn = new GsConnectProperty();
		String strServer = folder;
		conn.setServer(strServer);
		GsGeoDatabase ptrGDB =  fac.Open(conn);
		if(ptrGDB== null)
			return 1;
		 assertNotNull("database is null",ptrGDB); 

		GsFeatureClass ptrFeaClass1 = ptrGDB.OpenFeatureClass(f1);
		GsFeatureClass ptrFeaClass2 = ptrGDB.OpenFeatureClass(f2);
		assertNotNull("featureclass1 is null",ptrFeaClass1);
		assertNotNull("featureclass2 is null",ptrFeaClass2);
	
		
		FeatureReader io1 = new FeatureReader(ptrFeaClass1);
		FeatureReader io2 = new FeatureReader(ptrFeaClass2);
		
		GsFeatureClass ptrFeaClassOutput = ptrGDB.OpenFeatureClass(strOut);
		if(ptrFeaClassOutput != null)
		{
			//删除地物类
			ptrFeaClassOutput.Delete();
			//删除封装对象
			ptrFeaClassOutput.delete();
		}
		//第一个地物类的地物。
		GsFields fs = ptrFeaClass1.Fields();
		GsFieldVector vecNew = new GsFieldVector(); 
		GsFieldVector vec = fs.getFields();
		 
		System.out.println("vec.size()");
		System.out.println(vec.size());
		
		//拼装两个地物的属性字段为一个字段。
		CopyField(vecNew,vec,"1");
		CopyField(vecNew,ptrFeaClass2.Fields().getFields(),"2");
		fs.setFields(vecNew);
		
		//创建输出的地物类。
		ptrFeaClassOutput = ptrGDB.CreateFeatureClass(strOut, fs, ptrFeaClass1.GeometryColumnInfo(), ptrFeaClass1.SpatialReference());
		assertNotNull("featureclass2 is null",ptrFeaClassOutput);
		
		
		FeatureWriter ioOut = new FeatureWriter(ptrFeaClassOutput);
		GsOverlayAnalysis over = new GsOverlayAnalysis(tol);
		
		over.Intersect(new GsAnalysisDataIO[]{io1, io2},2, GsJoinAttributeType.eJoinAll,ioOut);
		ioOut.Commit();
		ioOut.delete();
		io1.delete();
		io2.delete();
		//结果数量。
		long nRet = ptrFeaClassOutput.FeatureCount();
		ptrFeaClassOutput.delete();
		ptrFeaClass1.delete();
		ptrFeaClass2.delete();
		over.delete();
		ptrGDB.delete();
		conn.delete();
		fac.delete();
		return nRet;
	}
	
	long intersect2(String folder,String f1,String f2,double tol,String strOut){
		GsSqliteGeoDatabaseFactory fac = new GsSqliteGeoDatabaseFactory();
		GsConnectProperty conn = new GsConnectProperty();
		String strServer = folder;
		conn.setServer(strServer);
		GsGeoDatabase ptrGDB =  fac.Open(conn);
		if(ptrGDB== null)
			return 1;
		 assertNotNull("database is null",ptrGDB); 

		GsFeatureClass ptrFeaClass1 = ptrGDB.OpenFeatureClass(f1);
		GsFeatureClass ptrFeaClass2 = ptrGDB.OpenFeatureClass(f2);
		assertNotNull("featureclass1 is null",ptrFeaClass1);
		assertNotNull("featureclass2 is null",ptrFeaClass2);
	
		
		FeatureReader io1 = new FeatureReader(ptrFeaClass1);
		FeatureReader io2 = new FeatureReader(ptrFeaClass2);
		
		GsFeatureClass ptrFeaClassOutput = ptrGDB.OpenFeatureClass(strOut);
		if(ptrFeaClassOutput != null)
		{
			//删除地物类
			ptrFeaClassOutput.Delete();
			//删除封装对象
			ptrFeaClassOutput.delete();
		}
		//第一个地物类的地物。
		GsFields fs = ptrFeaClass1.Fields();
		GsFieldVector vecNew = new GsFieldVector(); 
		GsFieldVector vec = fs.getFields();
		 
		System.out.println("vec.size()"+vec.size());
		
		//拼装两个地物的属性字段为一个字段。
		CopyField(vecNew,vec,"1");
		CopyField(vecNew,ptrFeaClass2.Fields().getFields(),"2");
		fs.setFields(vecNew);
		
		GsGeometryColumnInfo pinfo = ptrFeaClass1.GeometryColumnInfo();
		pinfo.setGeometryType(GsGeometryType.eGeometryTypePoint);
		//创建输出的地物类。
		ptrFeaClassOutput = ptrGDB.CreateFeatureClass(strOut, fs, pinfo, ptrFeaClass1.SpatialReference());
		assertNotNull("featureclass2 is null",ptrFeaClassOutput);
		
		
		FeatureWriter ioOut = new FeatureWriter(ptrFeaClassOutput);
		GsOverlayAnalysis over = new GsOverlayAnalysis(tol);
		
		over.Intersect(new GsAnalysisDataIO[]{io1, io2},2, GsJoinAttributeType.eJoinAll,ioOut,GsOverlapResultType.eAsPoint);
		ioOut.Commit();
		ioOut.delete();
		io1.delete();
		io2.delete();
		//结果数量。
		long nRet = ptrFeaClassOutput.FeatureCount();
		ptrFeaClassOutput.delete();
		ptrFeaClass1.delete();
		ptrFeaClass2.delete();
		over.delete();
		ptrGDB.delete();
		conn.delete();
		fac.delete();
		return nRet;
	}

	@Test
	 public void unionGTText() {
		 
		System.out.println("textfileunion TDLYGNFQ_DT and ZG_YD_GHDK_DT");
		String strCurDir = System.getProperty("user.dir");
		
		String strServer = strCurDir+ "/data/gt";
		
		
		GsOverlayAnalysis over = new GsOverlayAnalysis(0.001);
		for(int i =0;i<5;i++)
		{
			System.out.println("union" + i);
			GsAnalysisDataIO io1 = new TxtFeatureReader(strServer + "/TDLYGNFQ_DT.txt");
			GsAnalysisDataIO io2 = new TxtFeatureReader(strServer + "/ZG_YD_GHDK_DT.txt");
	
			GsAnalysisDataIO  ioOut = new TxtFeatureWriter(strServer + "/Union.txt");
			over.Union(new GsAnalysisDataIO[]{io1, io2},2, GsJoinAttributeType.eJoinAll, ioOut);
			io1.delete();
			io2.delete();
			ioOut.delete();
			
			System.out.println("intersect" + i);
			io1 = new TxtFeatureReader(strServer + "/TDLYGNFQ_DT.txt");
			io2 = new TxtFeatureReader(strServer + "/ZG_YD_GHDK_DT.txt");
			ioOut = new TxtFeatureWriter(strServer + "/intersect.txt");
			over.Intersect(new GsAnalysisDataIO[]{io1, io2},2, GsJoinAttributeType.eJoinAll, ioOut);
			io1.delete();
			io2.delete();
			ioOut.delete();
			
			System.out.println("erase" + i);
			io1 = new TxtFeatureReader(strServer + "/TDLYGNFQ_DT.txt");
			io2 = new TxtFeatureReader(strServer + "/ZG_YD_GHDK_DT.txt");
			ioOut = new TxtFeatureWriter(strServer + "/erase.txt");
			over.Erase(io1, io2, ioOut);
			io1.delete();
			io2.delete();			
		} 
	 }


	@Test
	public void unionGT() {
		
		System.out.println("union TDLYGNFQ_DT and ZG_YD_GHDK_DT");
		String strCurDir = System.getProperty("user.dir");
		//strCurDir="D:\\02-Work\\8888-GeoStarKernel\\44_KernelPort\\package\\javaport";
		String strServer = strCurDir+ "/data/gt";
		union(strServer,"TDLYGNFQ_DT","ZG_YD_GHDK_DT",0.001,"Union");
	}
	
	@Test
	public void eraseGT() {
		
		 System.out.println("erase TDLYGNFQ_DT and ZG_YD_GHDK_DT");
		String strCurDir = System.getProperty("user.dir");

		String strServer = strCurDir+ "/data/gt";
	    erase(strServer,"TDLYGNFQ_DT","ZG_YD_GHDK_DT",0.001,"erase");
	}
	@Test
	public void intersectGT() { 
		System.out.println("intersect TDLYGNFQ_DT and ZG_YD_GHDK_DT");
		String strCurDir = System.getProperty("user.dir");

		String strServer = strCurDir+ "/data/gt";
		intersect(strServer,"TDLYGNFQ_DT","ZG_YD_GHDK_DT",0.001,"intersect");
	}
	@Test
	public void intersectlines() { 
		
		System.out.println("intersect test and test2");
		String strCurDir = System.getProperty("user.dir");

		String strServer = strCurDir+ "/data/gt";
		intersect2(strServer,"test","test2", 0.0000001,"intersectlines");
	}
	
    public static byte[] float2byte(float f) {
        
        // 把float转换为byte[]
        int fbit = Float.floatToIntBits(f);
        
        byte[] b = new byte[4];  
        for (int i = 0; i < 4; i++) {  
            b[i] = (byte) (fbit >> (24 - i * 8));  
        } 
        
        // 翻转数组
        int len = b.length;
        // 建立一个与源数组元素类型相同的数组
        byte[] dest = new byte[len];
        // 为了防止修改源数组，将源数组拷贝一份副本
        System.arraycopy(b, 0, dest, 0, len);
        byte temp;
        // 将顺位第i个与倒数第i个交换
        for (int i = 0; i < len / 2; ++i) {
            temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }
        
        return dest;
        
    }
    
    /**
     * 字节转换为浮点
     * 
     * @param b 字节（至少4个字节）
     * @param index 开始位置
     * @return
     */
    public static float byte2float(byte[] b, int index) {  
        int l;                                           
        l = b[index + 0];                                
        l &= 0xff;                                       
        l |= ((long) b[index + 1] << 8);                 
        l &= 0xffff;                                     
        l |= ((long) b[index + 2] << 16);                
        l &= 0xffffff;                                   
        l |= ((long) b[index + 3] << 24);                
        return Float.intBitsToFloat(l);                  
    }
	
	@Test
	public void testRasterContour() { 
	
		//创建矢量输出
		String strCurDir = System.getProperty("user.dir");
		String strServer = strCurDir+ "/data";
		GsSqliteGeoDatabaseFactory fcsfac = new GsSqliteGeoDatabaseFactory();
		GsConnectProperty conn = new GsConnectProperty() ;
		conn.setServer( strServer);
		conn.setDataSourceType ( GsDataSourceType.eSqliteFile);
		GsGeoDatabase pteDB =  fcsfac.Open(conn);
		GsFeatureClass pFcs = pteDB.OpenFeatureClass("rasterContourtest");
		if (pFcs != null)
			pFcs.Delete();
		GsFieldVector fdvec = new GsFieldVector();
		GsFields fds = new GsFields();
		fdvec.add(new GsField("id",GsFieldType.eIntType));
		fdvec.add(new GsField("height", GsFieldType.eDoubleType));
		fds.setFields(fdvec);
		GsGeometryColumnInfo geoInfo = new GsGeometryColumnInfo();
		geoInfo.setFeatureType(GsFeatureType.eSimpleFeature);
		geoInfo.setGeometryType(GsGeometryType.eGeometryTypePolyline);
		geoInfo.setXYDomain(new GsBox(-180, -90, 180, 90));
		pFcs = pteDB.CreateFeatureClass("rasterContourtest",fds, geoInfo, new GsSpatialReference(4326));
		if (pFcs == null)
			return;
		
		FeatureWriter fwriter = new FeatureWriter(pFcs);
		
		//随机生成地形数据 256*256
		int length =64*64;
		Random r = new Random();
		byte[] bytebuff = new byte[64*64];
		r.nextBytes(bytebuff);
		GsRaster pras = new GsRaster();
		pras.DataPtr(bytebuff, length);
		pras.Width(32);
		pras.Height(32);
		GsRasterContour ptrRaserAna= new GsRasterContour();

		//ptrRaserAna.ContourInterval(0.25);
		double a[] ={0,50,100};
		ptrRaserAna.FixedLevels(a, 3);
		ptrRaserAna.ResolutionX(0.700389105058);
		ptrRaserAna.ResolutionY(-0.700389105058);
		ptrRaserAna.OutputData(fwriter);
		ptrRaserAna.GeometryDimType(3);
		ptrRaserAna.SrcX(0.350194552529);
		ptrRaserAna.SrcY(89.649805447471);
		ptrRaserAna.MinArea(0);
		boolean bok = ptrRaserAna.Contour(pras, GsRasterDataType.eByteRDT);
		fwriter.Commit();
	}




public void testJsonGRIDRasterContour() throws IOException { 

	//创建矢量输出
	String strCurDir = System.getProperty("user.dir");
	String strServer = strCurDir+ "/data";
	GsSqliteGeoDatabaseFactory fcsfac = new GsSqliteGeoDatabaseFactory();
	GsConnectProperty conn = new GsConnectProperty() ;
	conn.setServer( strServer);
	conn.setDataSourceType ( GsDataSourceType.eSqliteFile);
	GsGeoDatabase pteDB =  fcsfac.Open(conn);
	GsFeatureClass pFcs = pteDB.OpenFeatureClass("rasterContourtest2");
	if (pFcs != null)
		pFcs.Delete();
	GsFieldVector fdvec = new GsFieldVector();
	GsFields fds = new GsFields();
	fdvec.add(new GsField("id",GsFieldType.eIntType));
	fdvec.add(new GsField("height", GsFieldType.eDoubleType));
	fds.setFields(fdvec);
	GsGeometryColumnInfo geoInfo = new GsGeometryColumnInfo();
	geoInfo.setFeatureType(GsFeatureType.eSimpleFeature);
	geoInfo.setGeometryType(GsGeometryType.eGeometryTypePolyline);
	geoInfo.setXYDomain(new GsBox(-180, -90, 180, 90));
	pFcs = pteDB.CreateFeatureClass("rasterContourtest2",fds, geoInfo, new GsSpatialReference(4326));
	if (pFcs == null)
		return;
	
//	FeatureWriter fwriter = new FeatureWriter(pFcs);
//	File file = new File(strServer+"/grid.json");
//    FileReader reader = new FileReader(file);//定义一个fileReader对象，用来初始化BufferedReader
//    BufferedReader bReader = new BufferedReader(reader);//new一个BufferedReader对象，将文件内容读取到缓存
//    StringBuilder sb = new StringBuilder();//定义一个字符串缓存，将字符串存放缓存中
//    String s = "";
//    while ((s =bReader.readLine()) != null) {//逐行读取文件内容，不读取换行符和末尾的空格
//        sb.append(s + "\n");//将读取的字符串添加换行符后累加存放在缓存中
//        //System.out.println(s);
//    }
//    bReader.close();
//    String str = sb.toString();
//	//org.json.JSONObject o = new org.json.JSONObject(str);
//	//int h = o.length();
//	//int w = o.getJSONArray("").length();
//	double valuedata[] =  new double [501*412];
//	GsRasterContour ptrRaserAna = new GsRasterContour();
//	double nn[] = {};
//	//boolean bok = ptrRaserAna.Contour(valuedata, w,h,0.00476953125, 0.00476953125, 108.61524498800009, 18.193182648400135, false, -10, 2,24,0,0,nn,fwriter);
//	fwriter.Commit();
}
}

class TxtFeatureWriter extends GsAnalysisDataIO{
	String m_strFile;
	
	public TxtFeatureWriter(String strFile)
	{
		m_strFile = strFile;
		
	}
	BufferedWriter  m_Writer = null;
	
	OutputStreamWriter   m_StreamWriter = null;
	int m_OID = 0;
	public int OnData(GsFeatureBuffer pData){
		if(null == m_Writer){
			try {
				m_StreamWriter = new OutputStreamWriter(
				        new FileOutputStream(m_strFile),"GBK");
			} catch (UnsupportedEncodingException e) { 
				e.printStackTrace();
				return -1;
			} catch (FileNotFoundException e) { 
				e.printStackTrace();
				return -1;
			}//考虑到编码格式
			m_Writer = new BufferedWriter(m_StreamWriter);
		}
		int[] head = new int[6];
		head[0] =  m_OID++;
		head[2] = 3;
		head[3] = pData.CoordinateLength();
		head[4] = pData.CoordinateDimension();
		head[5] = pData.InterpretLength();
		int[] pInter = new int[pData.InterpretLength()];
		double[] pCoord = new double[pData.CoordinateLength()];
		pData.Interpret(pInter, pInter.length);
		pData.Coordinate(pCoord, pCoord.length);
		
		//System.out.println(m_strFile + m_OID);
		try {
			for(int i =0;i<head.length;i++)
				m_Writer.write(String.format("%d,",head[i]));
			for(int i =0;i<pInter.length;i++)
				m_Writer.write(String.format("%d,",pInter[i]));
			for(int i =0;i<pCoord.length;i++)
				m_Writer.write(String.format("%3.8f,",pCoord[i]));
			
			m_Writer.newLine(); 
		}catch (IOException e) { 
			e.printStackTrace();
			return -1;
		}
		return 0;
	}
}
class TxtFeatureReader extends GsAnalysisDataIO{
	String m_strFile;
	public TxtFeatureReader(String strFile){
		m_strFile = strFile;
		
	}
	BufferedReader  m_Reader = null;
	InputStreamReader m_StreamReader = null;
	int m_OID = 0;
	public int GeometryDimension() {
		return 2;
	}
	public int OnData(GsFeatureBuffer pData){
		
		if(null == m_Reader){
			try {
				m_StreamReader = new InputStreamReader(
				        new FileInputStream(m_strFile),"GBK");
			} catch (UnsupportedEncodingException e) { 
				e.printStackTrace();
				return -1;
			} catch (FileNotFoundException e) { 
				e.printStackTrace();
				return -1;
			}//考虑到编码格式
			m_Reader = new BufferedReader(m_StreamReader);
		}
		//System.out.println(m_strFile + m_OID);
		try {
			String strLine = m_Reader.readLine();
			//System.out.println(strLine);
			if(strLine == null || strLine.length() ==0)
				return -1;
			String[] arr = strLine.split("\\,");
			m_OID++;
			
			
			pData.ID(m_OID);
			int[] head = new int[6];
			for(int i =0;i<6;i++)
				head[i] = Integer.parseInt(arr[i]);
			int [] pInter = new int[head[5]];
			double [] pCoord = new double[head[3]];
			for(int i =0;i<pInter.length;i++)
			{
				pInter[i] = Integer.parseInt(arr[i + 6]);
			}
			
			for(int i =0;i<pCoord.length;i++)
			{
				pCoord[i] = Double.parseDouble(arr[pInter.length +i + 6]);
			}
			
			pData.SetGeometry(pInter.length, pInter,2, head[4], pCoord.length,pCoord);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		
		
		return 0;
	}
}
class FeatureWriter extends GsAnalysisDataIO{
	GsFeatureClass m_feaClass;
	String m_Name;
	int m_Index  = 0;
	public FeatureWriter(GsFeatureClass feaClass)
	{
		m_feaClass = feaClass;
		m_Name = m_feaClass.Name();
		
	}
	
	GsFeature m_ptrFea = null;
	GsFeature GetFeature()
	{
		if(m_ptrFea != null)
		{
			m_ptrFea.OID(-1);
			return m_ptrFea;
		}
		m_ptrFea =  m_feaClass.CreateFeature();
		return m_ptrFea;
	}
	int m_nTrans = 0;
	void StoreFeature(GsFeature fea)
	{
		GsTransaction trans = m_feaClass.Transaction();
		if(m_nTrans ==0 && trans != null )
			trans.StartTransaction();
		fea.Store();
		
		m_nTrans++;
		if(m_nTrans > 10000 && trans != null )
		{
			trans.CommitTransaction();
			m_nTrans = 0;
			System.out.println("Commit");
		}
	}
	public void Commit()
	{
		GsTransaction trans = m_feaClass.Transaction();
		if(m_nTrans >0 && trans != null)
		{
			trans.CommitTransaction();
			m_nTrans = 0;
			
		}
		if(m_ptrFea != null)
			m_ptrFea.delete();
		System.out.println(m_Index);
	} 
	public int OnData(GsFeatureBuffer pData) {
		 
		GsFeature fea = GetFeature();
		pData.WriteToFeature(fea);
		System.out.print(pData.ID() + pData.GeometryPtr().GeometryType().toString()+"\t\n");
		StoreFeature(fea); 
		m_Index++;
		//System.out.print(m_Name);
		//System.out.println(m_Index++);
		return 0;
	}
}
class FeatureReader extends GsAnalysisDataIO{
	GsFeatureClass m_feaClass;
	String m_Name;
	int m_nIndex = 0;
	public FeatureReader(GsFeatureClass feaClass)
	{
		m_feaClass = feaClass;
		m_Name = m_feaClass.Name();
		m_ptrCursor = m_feaClass.Search();
		GsFieldVector vec = m_feaClass.Fields().getFields();
		m_Fields = new GsFieldType[(int)(vec.size() -2)];
		for(int i = 2;i<vec.size();i++)
		{
			m_Fields[i-2] = vec.get(i).getType(); 
		}
	}
	GsFieldType[] m_Fields;

	public int GeometryDimension() {
		return GsGeometry.GeometryTypeDimension(m_feaClass.GeometryType());
	}
	GsFeatureCursor m_ptrCursor = null;
	GsFeature 		m_ptrFeature = null;
	public int OnData(GsFeatureBuffer pData) {
		  if(m_ptrFeature == null){
			  GsFeature ptrFea = m_ptrCursor.Next();
			  if(null == ptrFea) return -1;
			  
			  m_ptrFeature  = ptrFea;
			  //System.out.println(m_Name+"_begin");
		  }
		  else{
			  if(!m_ptrCursor.Next(m_ptrFeature)){
				  //System.out.print(m_Name);
				  //System.out.println(m_nIndex);
				  return -1;
			  }
		  }
		 
		  //System.out.print(m_Name);
		  //System.out.println(m_nIndex++);
		  m_nIndex++;
		  return pData.ReadFromFeature(m_ptrFeature)?0:-1;
	}
}
