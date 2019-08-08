package unittest;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.image.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.geostar.kernel.*;

public class rasterClass {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.loadLibrary("gsjavaport");
		GsKernel.Initialize();
		System.out.println("loadlibrary");
		System.out.println("rasterClass");
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
	private static int m_RasterClassOrder = 0;
	/**
	    * 
	    * 通过GDAL对图片TIFF格式进行转换
	    * 
	    * @param image
	    * 
	    * @param gp
	    * 
	    * @return
	    */
	   public byte[] GeoTiffEncodeByGdal(BufferedImage image,
	          double[] bbox) throws IOException {
	      
		   String strCurDir = System.getProperty("user.dir");
			
		   //唯一的文件名
		  int nIndex = m_RasterClassOrder++;
		  String strFileName = String.format("tempTiff%d.tif", nIndex);
		  File fOutput = new File(strCurDir + "/" + strFileName);
	      //如果输出文件存在则删除。
		  if(fOutput.exists())
	    	  fOutput.delete();
	      
	      int xsize = image.getWidth();
	      int ysize = image.getHeight();
	      
	      //打开数据库
	      GsFileGeoDatabaseFactory fac = new GsFileGeoDatabaseFactory();
	      GsConnectProperty conn = new GsConnectProperty ();
	      conn.setServer(strCurDir);
	      
	      GsGeoDatabase ptrGDB = fac.Open(conn);
		  System.out.println(" GsGeoDatabase open");
	      //空间参考
	      GsSpatialReference sr = new GsSpatialReference(GsWellKnownSpatialReference.eWGS84);
	      //栅格的基本信息
	      GsRasterColumnInfo col = new GsRasterColumnInfo();
	      col.setWidth(xsize);
	      col.setHeight(ysize);
	      col.setBlockWidth(xsize);
	      col.setBlockHeight(1);
	      col.setDataType(GsRasterDataType.eByteRDT);
	      //地理范围
	      GsBox box = new GsBox(bbox[0],bbox[1],bbox[2],bbox[3]);
	      col.setXYDomain(box);
	      box.delete();

	      //设置波段类型
	      GsIntVector vec = col.BandTypes();
	      vec.add(GsRasterBandType.eRedBand.ordinal());
	      vec.add(GsRasterBandType.eGreenBand.ordinal());
	      vec.add(GsRasterBandType.eBlueBand.ordinal());
	      col.BandTypes(vec);
	      vec.delete();
	       
	      //创建用于写入数据的Raster对象
	      GsRaster raster = new GsRaster();
	      raster.Width(xsize);
	      raster.Height(ysize);
	      //设置Raster的数据
	      DataBufferByte buff  = (DataBufferByte)image.getData().getDataBuffer();
	      raster.DataPtr(buff.getData(), buff.getSize());
	      
	    //创建栅格类
	      GsRasterClass ptrRaster =  ptrGDB.CreateRasterClass(strFileName, GsRasterCreateableFormat.eGTiff, col, sr);
		  System.out.println(" GsGeoDatabase open");
	      //写入栅格数据
	      ptrRaster.WriteRaster(raster);
	      		  System.out.println("  ptrRaster.WriteRaster(raster);");
	      
		  raster.delete();
		    System.out.println(" raster.delete();");
	      //ptrRaster.delete();
		  System.out.println(" ptrRaster.delete();");
	      ptrGDB.delete();
		  System.out.println(" ptrGDB.delete();");
	      sr.delete();
		   System.out.println(" sr.delete();");
	      fac.delete();
		   System.out.println("fac.delete();");
	      col.delete();
	       System.out.println(" col.delete();");
	      //读取输出结果。
	      FileInputStream fis = new FileInputStream(fOutput);
	       System.out.println("FileInputStream(fOutput);");
	      int l = fis.available();
	      byte[] b = new byte[l];
	      fis.read(b, 0, l);
	      fis.close();
	      //文件读取之后则删除临时的文件。
	      fOutput.delete();
	      System.out.println("fOutput.delete();");
	      return b; 
	   }

	@Test
	public void rgbArrayToGeoTiff()throws FileNotFoundException,IOException {
		String strCurDir = System.getProperty("user.dir");
		String filePath =strCurDir + "/image.png";  
		BufferedImage bufferedImage = ImageIO.read(new FileInputStream(filePath));  
		System.out.println("bufferedImage load");
		/*for(int i =0;i<10;i++)
		{
			byte[] bytes = GeoTiffEncodeByGdal(bufferedImage,new double[]{0,0,90,90});
			assertNotNull(bytes); 
			assertEquals(bytes.length,199151);
		}*/
	}

}
