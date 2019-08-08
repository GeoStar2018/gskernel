package unittest;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.geostar.kernel.*;
public class vectortile {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		

		System.loadLibrary("gsjavaport");
		
		GsKernel.Initialize();
		System.loadLibrary("gsutility");
		System.out.println("vectortile");
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
	void RenderVectorTile(String strStyleFile,String strTileFile,String strResult)
	{
		//1銆佸垱寤哄紡鏍疯〃宸ュ巶
		GsStyleTableFactory fac = new GsStyleTableFactory();
		
		String strCurDir = System.getProperty("user.dir");
		String strStyle = strCurDir+ "/" + strStyleFile;
		GsStyleTable style = null;
		
		File fileSt =  new File(strStyle);
		if(!fileSt.exists())
		{
			System.out.println("file not exists "+ strStyle);
			return;
		}
		
		if(strStyle.endsWith(".zip"))
		{
			System.out.println("open zip style file "+ strStyle);
			style =  fac.OpenFromZip(strStyle);
		}
		else
		{
			style =  fac.OpenFromJson(strStyle,true);
		}
		if(null == style)
			System.out.println("style is nulll ");
		
		//3銆佸垱寤洪噾瀛楀瀵硅薄
		GsMultiPyramid pyramid = new GsMultiPyramid();
		
		//4銆佸垱寤烘覆鏌撳櫒
		GsStyledVectorTileRenderer render = new
				GsStyledVectorTileRenderer(256,256,pyramid);
		render.StyleTable(style); 
		
		GsImageCanvas canvas =  render.ImageCanvas();
		GsImage img = canvas.Image();
		//杈撳嚭鐢ㄧ殑缂撳啿鍖恒�
		GsGrowByteBuffer outBuffer = new GsGrowByteBuffer(); 
		
		String strTile = strCurDir+ "/" + strTileFile; 
		
		
		File file = new File(strTile);  
	    long fileSize = file.length();  
		byte[] buffer = new byte[(int)fileSize];
		
		try {
			
			FileInputStream fi = new FileInputStream(file);
			fi.read(buffer,0,(int)fileSize);
			fi.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.out.println(buffer.length);
		render.DrawVectorTile(buffer, buffer.length,  6, 26, 52); 
		
		outBuffer.Allocate(0); 
		try{
			img.SavePNG(outBuffer);
		}
		catch(Exception e)
		{
			
		} 
		//7銆佸瓨鍌ㄧ粨鏋滃埌鏂囦欢銆�
		String strPNG = strCurDir+ "/" + strResult;
		
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(strPNG);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return ;
		} 
		
		buffer = new byte[(int)outBuffer.BufferSize()];
		outBuffer.CopyToArray(buffer,buffer.length);
		
		try {
			output.write(buffer);
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		img.delete();
		outBuffer.delete();
		render.delete();
		canvas.delete();
		pyramid.delete();
		fac.delete();
		style.delete(); 
	}
	@Test
	public void GsStyleRender() { 
	
		System.out.println("GsStyleRender");
		RenderVectorTile("400w.json","2_1_3.bin","2_1_3.png");
	} 
	@Test
	public void GsStyleRenderZip() { 
	
		System.out.println("GsStyleRenderZip");
		RenderVectorTile("style.zip","5_12_22.pbf","5_12_22.png");
	} 
	@Test
	public void WMTSCrashStyle() { 
	    
		System.out.println("WMTSCrashStyle");
		RenderVectorTile("testdata/wmts_crash_style20170714.zip","testdata/wmts_crash_tile_2017014.bin","testdata/wmts_crash_tile_2017014.png");
	} 
	
	@Test
	public void testRenderXuZhouTile() { 
		System.out.println("testRenderXuZhouTile");
		RenderVectorTile("style.txt","15-13049-27042.bin","15-13049-27042.png");
	} 
	
	@Test
	public void GsStyleRender1000() {
		System.out.println("GsStyleRender1000");
		for(int i =0;i<10;i++)
			GsStyleRender();
		
		System.out.println("GsStyleRender1000 end");
	}
	@Test
	public void testCreateStyleTable() {
		System.out.println("testCreateStyleTable");
		GsStyleTableFactory fac = new GsStyleTableFactory();
		String strStyle="{\"layers\":[{\"id\":\"BOU2_4M_S_0\",\"layout\":{\"visibility\":\"visible\"},\"paint\":{\"fill-color\":\"rgba(132,220,184,1)\"},\"source-layer\":\"BOU2_4M_S\",\"type\":\"fill\"}, {\"id\":\"ROA_4M_L_1\",\"layout\":{\"visibility\":\"visible\"},\"paint\":{\"line-color\":\"rgba(198,132,220,1)\",\"line-width\":1},\"source-layer\":\"ROA_4M_L\",\"type\":\"line\"}, {\"id\":\"XIANCH_P_2\",\"layout\":{\"visibility\":\"visible\"},\"paint\":{\"circle-color\":\"rgba(255,0,0,1)\",\"circle-radius\":1},\"source- layer\":\"XIANCH_P\",\"type\":\"circle\"}],\"name\":\"china.GMAPX\",\"version\":\"1.0\"}";
		
		GsStyleTable ptrStyleTable = fac.OpenFromJson(strStyle, false);
		assertNotNull(ptrStyleTable);
	}
	
	@Test
	public void testOpenStyleTable() {
		GsStyleTableFactory fac = new GsStyleTableFactory();
		
		String strCurDir = System.getProperty("user.dir");
		String strStyle = strCurDir+ "/" + "xzdl_sqlite.stylez";
		GsStyleTable style = null;
		File file = new File(strStyle);
		if(!file.exists())
		{
			System.out.println("file not exists "+ strStyle);
			return;
		}
		System.out.println("open zip style file "+ strStyle);
		
		style =  fac.OpenFromZip(strStyle);

	}

}
