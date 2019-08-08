package unittest;

import static org.junit.Assert.assertEquals;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.geostar.kernel.GsArcPointSymbol;
import com.geostar.kernel.GsBox;
import com.geostar.kernel.GsColor;
import com.geostar.kernel.GsDisplayTransformation;
import com.geostar.kernel.GsEllipsePointSymbol;
import com.geostar.kernel.GsKernel;
import com.geostar.kernel.GsMemoryImageCanvas;
import com.geostar.kernel.GsMultiPointSymbol;
import com.geostar.kernel.GsPoint;
import com.geostar.kernel.GsRect;
import com.geostar.kernel.GsSimplePointSymbol;
import com.geostar.kernel.GsSymbol;



public class PointSymbol {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.loadLibrary("gsjavaport");
		GsKernel.Initialize();
		System.out.println("loadlibrary succeed");
		System.out.println("PointSymbol");
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
	public void GsMultiPointSymbolDraw() {
		GsMultiPointSymbol gsmutilSymbol = new GsMultiPointSymbol();
		System.out.println("GsMultiPointSymbol succeed");
		GsArcPointSymbol pointSymbol = new GsArcPointSymbol();
		pointSymbol.EllipseParameter(20, 10);
		GsColor colorP = new GsColor((short)255,(short)255,(short)125,(short)125);
		pointSymbol.Color(colorP);
		pointSymbol.Size(5);
		
		GsEllipsePointSymbol llipsePointSymbol = new GsEllipsePointSymbol();
		llipsePointSymbol.EllipseParameter(30, 15);
		GsColor colorP1 = new GsColor((short)125,(short)125,(short)125,(short)125);
		llipsePointSymbol.Color(colorP);
		llipsePointSymbol.Size(5);
		
		gsmutilSymbol.Add(llipsePointSymbol);
		gsmutilSymbol.Add(pointSymbol);
		GsSymbol p1 =  gsmutilSymbol.ElementAt(0);
		GsSymbol p2 =  gsmutilSymbol.ElementAt(1);
		
        int imageWidth = 512;//图片的宽度  
        int imageHeight =512;//图片的高度  
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);  
        Graphics graphics = image.getGraphics();  
        
        GsMemoryImageCanvas pMemory = new GsMemoryImageCanvas(512,512);
        GsColor color = new GsColor(0);
        pMemory.Clear(color);
       
        
        GsPoint pGeo =  new GsPoint(50,50);
        GsBox mapExtent= new GsBox(0.0,0.0,100.0,100.0);
        GsRect deviceExtent = new GsRect(0,0,512,512);
        
        GsDisplayTransformation pDT = new GsDisplayTransformation(mapExtent,deviceExtent);
        
        p1.StartDrawing(pMemory, pDT);
        p1.Draw(pGeo);
        p1.EndDrawing();
        
        p2.StartDrawing(pMemory, pDT);
        p2.Draw(pGeo);
        p2.EndDrawing();
        
        String strCurDir = System.getProperty("user.dir");
		String filePath =strCurDir + "/GsMultiPointSymbolDraw1.png";
        pMemory.Image().SavePNG(filePath);
	}
	

}
