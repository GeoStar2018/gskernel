package unittest;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.geostar.kernel.*;

public class layout {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		System.loadLibrary("gsjavaport");
		System.out.println(System.getProperty("java.library.path"));
		GsKernel.Initialize();
		System.out.println("loadlibrary");
		System.out.println("layout");
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

	public boolean FileExist(String strPath)
	{
		File file = new File(strPath);
		if(!file.exists())
		{
			System.out.print("file not exists" + strPath);
			return false;
		}
		return true;
	}
	@Test
	public void testHugeLayout() {
	
		
		GsLogger.Default().LogLevel(GsLogLevel.eLOGALL);
		System.out.println("testHugeLayout");
		//需要什么纸张
		GsPage pg = GsHugeLayout.StandardPages("A2");
		System.out.println("create GsHugeLayout");
		
		String strCurDir = System.getProperty("user.dir");
		String strLayout = strCurDir +"/layout/xuzhoudata_Mercator_Tile0307_no.tile";
		String strStyle = strCurDir +"/layout/china.stylez";
		String strOutputPNG = strCurDir +"/layout/xuzhou_A2_800DPI.png";
		
		if(!FileExist(strLayout)|| !FileExist(strStyle))
		{
			return;
		}
		
		double dpi = 96;
		int 	memSize = 200;//300mb缓存空间
		GsBox mapextent = new GsBox(116.31923,33.865960, 118.470528, 34.8679);//(13038505,4082164,13047426,4090377);
	
		//创建layout
		//采用横向的纸张
		GsHugeLayout layout = new GsHugeLayout (strLayout,strStyle,pg.Height,pg.Width);
		
		
		//准备输出，返回页面的像素大小
		//上下左右留白
		GsRect rectMarian = new GsRect(100,100,0,0);
		rectMarian.setRight(100);
		rectMarian.setBottom(100);
		//内存缓存到校300mb
		layout.MemoryCacheSize(memSize);
		
		GsSize pagePixelSize = layout.PrepeareOutput(dpi, rectMarian); 
		
		System.out.println(" ExecuteOutput");
		//执行输出输出，完成后获得可以自己绘制的画布。
		GsImageCanvas pCanvas = layout.ExecuteOutput(mapextent, new GsColor(GsColor.White), strOutputPNG);
		
		System.out.println(" DrawLayoutFrameAndElement");
		///绘制图外要素
		GsPen p = pCanvas.CreatePen(new GsColor(GsColor.Red),5);
		pCanvas.DrawLine(0,0,1000,1000,p);
		p.delete();
		
		GsStringFormat sFontFormat = new GsStringFormat();
		System.out.println("GsStringFormat create succeed");
		sFontFormat.Font("SimSun");
		sFontFormat.FontStyle(GsFontStyle.eFontStyleRegular);
		sFontFormat.FontSize(23);
		String str = "ghjjjsdf地方出现jj";
		GsRectF rf =new GsRectF();
		pCanvas.MeasureString(str, str.length(), rf, sFontFormat);
		GsPTF point = new GsPTF(200,150);
		GsColor color = new GsColor();
		color.SetARGB((short)125, (short)125, (short)125);
		GsSolidBrush brush = new GsSolidBrush(color);
		pCanvas.DrawString(str, str.length(),brush,point, sFontFormat);
		System.out.println("MeasureString  succeed");
		float h = rf.Height();
		System.out.println(h);
		Assert.assertSame(h>0, true);
		
		//调用这个方法实现绘制图外要素吗？
		layout.DrawLayoutFrameAndElement();
		

		System.out.println(" FinishLayout");
		//结束Layout
		layout.FinishLayout();
		
		layout.Dispose();
	}

}
