package unittest.smoke;

import org.junit.BeforeClass;
import org.junit.Test;

import com.geostar.kernel.GsGrowByteBuffer;
import com.geostar.kernel.GsImage;
import com.geostar.kernel.GsImageCanvas;
import com.geostar.kernel.GsKernel;
import com.geostar.kernel.GsMultiPyramid;
import com.geostar.kernel.GsStyleTable;
import com.geostar.kernel.GsStyleTableFactory;
import com.geostar.kernel.GsStyledVectorTileRenderer;
import com.geostar.kernel.*;
public class SmokeVectorTile {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.loadLibrary("gsjavaport");
		GsKernel.Initialize();
		System.out.println("loadlibrary succeed");
	}
	@Test
	public void initRender(){
		//1、创建式样表工厂
		GsStyleTableFactory fac = new GsStyleTableFactory();
		//2、从Json打开式样表
		//this.style = fac.OpenFromJson(style, false);20170228删除
		GsStyleTable style = fac.OpenFromZip("");
		//3、创建金字塔对象
		String pyramids = "<Pyramid><Basic Description=\"360度金字塔方案\" PyramidID=\"72f3fcab-c1c2-4942-bb17-af5a3b469e5d\" Name=\"360度金字塔\" />"
				+ "<Level TopLevelIndex=\"0\" BottomLevelIndex=\"20\" ScaleX=\"2\" ScaleY=\"2\" /><TileBasic TileSizeX=\"256\" TileSizeY=\"256\" OriginRowIndex=\"0\" OriginColIndex=\"0\" />"
				+ "<TopTile FromX=\"-180.00000000000000000000\" FromY=\"90.00000000000000000000\" ToX=\"180.00000000000000000000\" ToY=\"-270.00000000000000000000\" />"
				+ "<Const PI=\"3.14159265358979310000\" Tolerance=\"0.00000011920928955078\" /><Range XMin=\"-180.00000000000000000000\" "
				+ "YMin=\"-90.00000000000000000000\" XMax=\"180.00000000000000000000\" YMax=\"90.00000000000000000000\" /></Pyramid>";
		GsMultiPyramid pyramid = new GsMultiPyramid(pyramids);
		//4、创建渲染器
		GsStyledVectorTileRenderer render = new GsStyledVectorTileRenderer(256,256,pyramid);
		render.StyleTable(style);
		
		GsImageCanvas canvas =  render.ImageCanvas();
		GsImage img = canvas.Image();
		//输出用的缓冲区。
		GsGrowByteBuffer outBuffer = new GsGrowByteBuffer();
	}
		@Test
	public void TestTpkread() {

		
		String strCurDir = System.getProperty("user.dir");
		
		String strServer = strCurDir +"/Data/400w";
		GsESRIFileGeoDatabaseFactory fac = new GsESRIFileGeoDatabaseFactory();
		GsConnectProperty conn = new GsConnectProperty();
		conn.setServer(strServer);
		GsGeoDatabase gdb = fac.Open(conn);
		GsTileClass feaclass = gdb.OpenTileClass("img");
		if(feaclass == null)
			return;
		GsTileColumnInfo fg = feaclass.TileColumnInfo();
		System.out.println(fg.getXYDomain().getXMin());
		System.out.println(fg.getXYDomain().getYMin());
		System.out.println(fg.getXYDomain().getXMax());
		System.out.println(fg.getXYDomain().getYMax());
		System.out.println("TPKread succeedful");
		
	}
}
