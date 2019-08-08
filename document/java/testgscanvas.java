package unittest;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.geostar.kernel.*;

import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.image.*;
import java.io.IOException;
   
public class testgscanvas {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.loadLibrary("gsjavaport");
		System.out.println("loadlibrary");
		System.out.println("testgscanvas");
		
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
//	public void drawToBufferImage() throws IOException {
//		System.out.println("drawToBufferImage");
//		BufferedImage img = new  BufferedImage(256,256,BufferedImage.TYPE_4BYTE_ABGR);
//		GsMemoryImageCanvas canvas = new GsMemoryImageCanvas(256,256);
//		canvas.Clear(new GsColor(GsColor.Yellow));
//		GsPen pen = canvas.CreatePen(new GsColor(GsColor.Red),4);
//		canvas.DrawLine(new float[]{0,0,100,100},2, pen);
//		pen.Color(new GsColor(GsColor.Blue));
//		canvas.DrawLine(new float[]{0,128,250,128},2, pen);
//		
//		pen.Color(new GsColor(GsColor.Green));
//		canvas.DrawLine(new float[]{0,200,250,200},2, pen);
//		
//		pen.delete();
//		
//	
//        
//        DataBufferByte buff  = (DataBufferByte)img.getRaster().getDataBuffer();
//        
//        System.out.println("CopyImageData");
//		
//        canvas.Image().CopyImageData(buff.getData(), buff.getSize(),GsRGBAType.eARGB32);
//        
//        String strCurDir = System.getProperty("user.dir");
//		String filePath =strCurDir + "/BufferedImageSave.png";  
//		String filePathOri =strCurDir + "/BufferedImageSaveCanvas.png";
//		canvas.Image().SavePNG(filePathOri);
//        canvas.delete();
//        
//		ImageIO.write(img, "png", new java.io.File(filePath));
//		
//	}
	@Test
	public void MeasureString() throws IOException {
		try {
//			GsLogger gsLogger =  GsLogger.Default();
//			
//			gsLogger.LogLevel(GsLogLevel.eLOGALL);
//			System.out.println("MeasureString");
//			GsMemoryImageCanvas pmemimage = new GsMemoryImageCanvas(512,512);
//			GsColor colorb = new GsColor();
//			colorb.SetARGB((short)255, (short)2, (short)245);
//			pmemimage.Clear(colorb);
//			String str = "中国sad集散地";
//			GsRectF rf = new GsRectF();
//			GsStringFormat sFontFormat = new GsStringFormat();
//			
//			System.out.println("GsStringFormat create succeed");
//			//GsStringFormat sFontFormat = pmemimage.CreateStringFormat();
//			//sFontFormat.Font("宋体");
//			//sFontFormat.Font("Arial Unicode MS");
//			//sFontFormat.Font("SimSun");
//			//sFontFormat.Font("宋体");
//			//sFontFormat.Font("华文彩云");
//			sFontFormat.FontStyle(GsFontStyle.eFontStyleRegular);
//			sFontFormat.FontSize(23);
//			pmemimage.MeasureString(str, str.length(), rf, sFontFormat);
//			System.out.println("MeasureString  succeed");
//			float h = rf.Height();
//			System.out.println(h);
//			Assert.assertSame(h>0, true);
//			
//			GsColor color = new GsColor();
//			color.SetARGB((short)125, (short)125, (short)125);
//			GsSolidBrush brush = new GsSolidBrush(color);
//	
//			pmemimage.DrawString(str, str.length(), brush, rf, sFontFormat);
//			System.out.println("DrawString  succeed");
//			String strCurDir = System.getProperty("user.dir");
//			String strResult = strCurDir +"/testDrawString.png";
//			pmemimage.Image().SavePNG(strResult);
//			System.out.println("save png  succeed");
//			System.out.println( strResult);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}


}
