package unittest;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.geostar.kernel.*;

public class proxyTileClassTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.loadLibrary("gsjavaport");
		GsKernel.Initialize();
		System.out.println("loadlibrary succeed");
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
			
		String definefile = strCurDir+ "../../../package/javaport/data/mapdefine/xz2010.GMAPX";
		System.out.println(definefile);
		GsMapDefine def = new GsMapDefine(definefile);
	    GsMap map = new GsMap(null);
	    
	    OnOpenTileClassCallback onTileClass = new OnOpenTileClassCallback();
	    
		def.OnOpenTileClassAdd(onTileClass);
	    def.ParserMap(map);
	    GsLayerVector layvec = map.Layers();
	    GsLayer lay = layvec.get(0);
	    	    
	    GsTileLayer tileLayer = GsTileLayer.DowncastTo(lay);
	    tileLayer.Synchronization(true);
		GsBox box = new GsBox(73.446952819824219,6.3186411857604980,135.08583068847656,53.557926177978516);

	
		GsMemoryImageCanvas canvas = new GsMemoryImageCanvas(1366,1024);
		GsDisplayTransformation dt = new GsDisplayTransformation(box,new GsRect(0,0,1366,1024));
		GsDisplay disp = new GsDisplay(canvas,dt);
		GsTrackCancel pCancel = new GsTrackCancel();
		map.Output(disp, pCancel);
		

		String result = strCurDir+ "/data/mapdefine/proxyTileClassTest_output.java.png";
		System.out.println(result);
		GsImage img =  canvas.Image();
		img.SavePNG(result);
	}
}
//tile cursor TileDataIO
class TileDataIO extends GsTileDataIO
{
	int m_nCount  =0;
	public TileDataIO()
	{
		
	}
	public TileDataIO(int startLevel, int endLevel)
	{
		
	}
	
	public TileDataIO(int nlevel, int startRow, int startCol, int endRow, int endCol)
	{
		
	}

	public int OnData(GsTileBuffer pData)
	{
		if(m_nCount >0)
			return -1;
		m_nCount++;
		
		System.out.println("Read Tile from Tile cursor");
		String strCurDir = System.getProperty("user.dir");
		
		String server = strCurDir+ "../../../package/javaport/data/sqlite/";
		
		GsSqliteGeoDatabaseFactory fac = new GsSqliteGeoDatabaseFactory();
		GsConnectProperty conn = new GsConnectProperty();
		conn.setServer(server);
		GsGeoDatabase geo = fac.Open(conn);
		GsTileClass ptrTile = geo.OpenTileClass("BOU2_4M_S_TILE");
		
		GsTileCursor ptrCursor = ptrTile.Search(5,4,25,4,25);
		
		GsTile tile = ptrCursor.Next();
		 
		byte [] data = new byte[tile.TileDataLength()];
		tile.TileDataPtr(data);


//		GsSimpleBitmap bitmap = new GsSimpleBitmap(data, data.length);
//		bitmap.SavePNG("c://javaabc.png");
		pData.TileData(data, data.length);
		pData.ID(tile.OID());
		pData.Level(tile.Level());
		pData.Row(tile.Row());
		pData.Col(tile.Col());
		return 0;
	}
	
}
//tile class 
class TileClassIO extends GsProxyTileClassIO
{

	public TileClassIO(GsConnectProperty conn,   String name)
	{
		
	} 
	public GsTileDataIO Search()
	{
		System.out.println("Search Proxy FeatureClass ");
		
		return new TileDataIO();
	}
	
	public GsTileDataIO Search(int nStartLevel, int nEndLevel)
	{
		System.out.println("Search Proxy FeatureClass ");
		
		return new TileDataIO(nStartLevel, nEndLevel);
	}
	
	public GsTileDataIO Search(int nLevel, int nStartRow, int nStartCol, int nEndRow, int nEndCol)
	{
		System.out.println("Search Proxy FeatureClass ");
		
		return new TileDataIO(nLevel, nStartRow, nStartCol, nEndRow, nEndCol);
	}
	
}
class OnOpenTileClassCallback extends GsMapDefine_OnOpenTileClass
{
	public GsTileClass OnOpenTileClass(  GsConnectProperty conn,   String name)
	{

		System.out.println("Open Proxy TileClass " + name);
		
		GsProxyTileClass tileclass = new GsProxyTileClass(name);
		
		tileclass.DataIO(new TileClassIO(conn,name));
		
		return tileclass;
	}
}