package unittest;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.geostar.kernel.*;

public class proxyFeatureClassTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.loadLibrary("gsjavaport");
		GsKernel.Initialize();
		System.out.println("loadlibrary succeed");
		System.out.println("proxyFeatureClassTest");
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
	public void Test() {

		System.out.println("hello world");
	}
	@Test
	public void MapDefineFileCallback() {
		String strCurDir = System.getProperty("user.dir");
			
		String definefile = strCurDir+ "/data/mapdefine/400w.GMAPX";

		System.out.println(definefile);
		GsMapDefine def = new GsMapDefine(definefile);
	    GsMap map = new GsMap(null);
	    
	    OnOpenFeatureClassCallback onFeaClass = new OnOpenFeatureClassCallback();
	    
		def.OnOpenFeatureClassAdd(onFeaClass);
	    def.ParserMap(map);
	
		GsMemoryImageCanvas canvas = new GsMemoryImageCanvas(1024,1024);
		GsDisplayTransformation dt = new GsDisplayTransformation(new GsBox(-200,-200,200,200),new GsRect(0,0,1024,1024));
		GsDisplay disp = new GsDisplay(canvas,dt);
		GsTrackCancel pCancel = new GsTrackCancel();
		map.Output(disp, pCancel);
		

		String result = strCurDir+ "/data/mapdefine/result.png";
		canvas.Image().SavePNG(result);
	}
}
//sim feature cursor
class FeatureDataIO extends GsFeatureDataIO
{
	int m_nCount  =0;
	public FeatureDataIO(GsQueryFilter pFilter)
	{
		
	}

	public int OnData(GsFeatureBuffer pData)
	{
		if(m_nCount >0)
			return -1;
		m_nCount++;
		
		System.out.println("Read Feature from feature cursor");
		GsEnvelope env = new GsEnvelope(0,0,100,100);
		pData.SetGeometry(env);
		return 0;
	}
	
}
//feature class 
class FeatureClassIO extends GsProxyFeatureClassIO
{

	public FeatureClassIO(GsConnectProperty conn,   String name)
	{
		
	} 
	public GsFeatureDataIO Search(GsQueryFilter pFilter)
	{
		System.out.println("Search Proxy FeatureClass ");
		
		return new FeatureDataIO(pFilter);
	}
	 
	
}
class OnOpenFeatureClassCallback extends GsMapDefine_OnOpenFeatureClass
{

	public GsFeatureClass OnOpenFeatureClass(  GsConnectProperty conn,   String name)
	{

		System.out.println("Open Proxy FeatureClass " + name);
		
		GsProxyFeatureClass feaclass = new GsProxyFeatureClass(name);
		
		feaclass.DataIO(new FeatureClassIO(conn,name));
		
		return feaclass;
	}
}