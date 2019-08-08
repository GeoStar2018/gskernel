package unittest;
import com.geostar.kernel.*;
import java.io.File;
/**
 * 大型文件制图
 */
public class GsHugeLayout {
	 
	/**
	 * 获取标准页面配置
	 * @return
	 */
	public static GsPage[] StandardPages()
	{
		GsPage[] pgs = {
				new GsPage("A0",841,1189),
				new GsPage("A1",594,841),
				new GsPage("A2",420,594),
				new GsPage("A3",297,420),
				new GsPage("A4",210,297)};
		
		return pgs;
	}
	/**
	 * 根据纸张的名称获取纸张的信息
	 * @param strName A0~A4
	 * @return
	 */
	public static GsPage StandardPages(String strName){
		GsPage[] pags = StandardPages();
		for(int i =0;i<pags.length;i++)
		{
			if(pags[i].Name.compareToIgnoreCase(strName) ==0)
				return pags[i];
		}
		return null;
	}
	/**
	 * 
	 * @param strTileClassPath 瓦片数据集路径
	 * @param strStyleFile    式样zip文件
	 * @param pageWidthMM		纸张的宽度，单位毫米
	 * @param pageHeightMM		纸张的高度，单位毫米
	 */
	public GsHugeLayout(String strTileClassPath,String strStyleFile,double pageWidthMM,double pageHeightMM){
		m_Map = new GsMap(null);
		GsTileClass ptrTileClass = PrepareTileClass(strTileClassPath);
		m_Pyramid = ptrTileClass.Pyramid();
		
		GsVectorTileLayer pTileLayer = new GsVectorTileLayer(ptrTileClass,0);
		m_Map.Layers().add(pTileLayer);
		
		GsStyleTableFactory styleFac  = new com.geostar.kernel.GsStyleTableFactory ();
		GsStyleTable ptrStyleTable = styleFac.OpenFromZip(strStyleFile);
		GsStyledVectorTileRenderer render = new GsStyledVectorTileRenderer(null, ptrTileClass.Pyramid());
		render.StyleTable(ptrStyleTable);
		pTileLayer.Renderer(render);
		
		m_PageWidth =  pageWidthMM;
		m_PageHeight =  pageHeightMM;
	}
	protected void finalize()
    {
		
          if(m_Map != null) m_Map.delete();
          m_Map = null;
          if(null != m_ImageCanvas) m_ImageCanvas.delete();
          m_ImageCanvas = null;
          
          File f = new File(m_CanvasFile);
          if(f.exists()) f.delete();
          
     }
	GsMap m_Map;
	private GsTileClass PrepareTileClass(String strTileClassPath){
		GsSqliteGeoDatabaseFactory fac  = new GsSqliteGeoDatabaseFactory();
		File f  = new File(strTileClassPath);
		GsConnectProperty conn= new GsConnectProperty();
		conn.setServer(f.getParent());
		GsGeoDatabase ptrGDB = fac.Open(conn);
		return ptrGDB.OpenTileClass(f.getName());
	}
	GsPyramid m_Pyramid;
	/**
	 * 页面物理宽度
	 */
	double m_PageWidth,m_PageHeight;
	/**
	 * 页面的像素大小
	 */
	GsSize m_PageSize;
	double m_DPI;
	/**
	 * 位图边缘留白（像素）
	 */
	GsRect m_Margin;
	
	/**
	 * 准备输入制图数据
	 * @param dpi 要输出的dpi大小 
	 * @param margin 纸张上下左右留白的大小，浮点值代表和纸张的比率。Left,Right代表留白占纸张宽度的比率，Top，Bottom代表六班占纸张高度的比率
	 * @return 返回纸张的像素大小。
	 */
	public GsSize PrepeareOutput(double dpi,GsRectF margin)
	{ 
		//计算MM和一个像素的换算参数。(依据 1英寸=25.4mm）
		//1  英寸=25.4mm
		//dpi=300意味着300像素等于25.4mm,那么1一个像素的毫米长度为
		double mmPerPixel = 25.4 / dpi; //一个像素代表多少毫米。
		
		GsRect rect = new GsRect(0,0,0,0);
		rect.setLeft((int)(margin.getLeft() * m_PageWidth / mmPerPixel));
		rect.setTop((int)(margin.getTop() * m_PageHeight / mmPerPixel));
		rect.setRight((int)(margin.getRight() * m_PageHeight / mmPerPixel));
		rect.setBottom((int)(margin.getBottom() * m_PageHeight / mmPerPixel));
		
		return PrepeareOutput(dpi,rect);
	}
	/**
	 * 准备输入制图数据
	 * @param dpi 要输出的dpi大小 
	 * @param margin 纸张上下左右需要留的空白（单位像素）
	 * @return 返回纸张的像素大小。
	 */
	public GsSize PrepeareOutput(double dpi,GsRect margin)
	{
		m_DPI = dpi;
		//计算MM和一个像素的换算参数。(依据 1英寸=25.4mm）
		//1  英寸=25.4mm
		//dpi=300意味着300像素等于25.4mm,那么1一个像素的毫米长度为
		double mmPerPixel = 25.4 / dpi; //一个像素代表多少毫米。
		
		//计算整个纸张的像素大小
		m_PageSize = new GsSize((int)(0.5 + m_PageWidth / mmPerPixel) ,(int)(0.5 + m_PageHeight / mmPerPixel));
		
		m_Margin = margin;
		//如果存在canvas的话则判断canvas的大小是否适合使用。
		if(m_ImageCanvas != null)
		{
			GsImage ptrImage = m_ImageCanvas.Image();
			boolean bIsSame = true;
			if(m_PageSize.getWidth() != ptrImage.Width() ||
					m_PageSize.getHeight() != ptrImage.Height()	)
			{
				bIsSame = false;
			}
			ptrImage.delete();
			if(!bIsSame) {
				m_ImageCanvas.delete();
				m_ImageCanvas = null;

				//删除PAM文件
				File f = new File(m_CanvasFile);
				f.delete();
			}
		}
		return m_PageSize;
	}
	/**
	 * 内存缓存的大小单位MB
	 */
	int m_MemoryCacheSize = 100;
	/**
	 * 获取制图过程中使用内存缓存的大小（单位MB)，缺省为100MB
	 * @return
	 */
	public int MemoryCacheSize()
	{
		return m_MemoryCacheSize;
	}
	/**
	 * 设置制图过程中使用内存缓存的大小（单位MB)，缺省为100MB
	 * @return
	 */
	public void MemoryCacheSize(int n )
	{
		m_MemoryCacheSize = Math.abs(n);
	}
	/**
	 * 纸张的的大小
	 * @return
	 */
	public GsSize PageSize()
	{
		return m_PageSize;
	}
	/**
	 * 图纸边缘的留白大小。单位像素。Left,Top,Right，Button分别代表上下左右留白的像素大小。
	 * @return
	 */
	public GsRect Margin()
	{
		return m_Margin;
	}
	/**
	 * 有效的地图范围，既纸张中间绘制地图的范围。
	 * @return
	 */
	public GsRect ValidMapExtent()
	{
		//画布上绘制数据的区域，剔除四边留白之后
		return new GsRect(m_Margin.getLeft(),m_Margin.getTop(),
						m_PageSize.getWidth() - m_Margin.getLeft() - m_Margin.getRight(),
						m_PageSize.getHeight() - m_Margin.getTop() - m_Margin.getBottom());
	}
	
	/**
	 * 开始执行输出
	 * @param mapextent  要输出的地理范围。
	 * @param bkColor  画布的背景色。
	 * @param strOutputPNGFile  输出的PNG图片路径
	 * @return
	 */
	public GsImageCanvas  ExecuteOutput(GsBox mapextent,GsColor bkColor,String strOutputPNGFile)
	{
		m_PNG = strOutputPNGFile;
		File pngFile = new File(strOutputPNGFile);
		if(null == m_ImageCanvas) //如果画布为空的话则
		{
			m_CanvasFile= pngFile + ".pam";
			m_ImageCanvas = new GsFileImageCanvas(m_CanvasFile,m_PageSize.getWidth(),m_PageSize.getHeight(),
					m_MemoryCacheSize * 1024 * 1024,m_DPI);
		}
		//画布上绘制数据的区域，剔除四边留白之后
		GsRect rectDraw = ValidMapExtent();
		m_ImageCanvas.Clear(bkColor);
		//将有效的绘制区域作为Cip范围。避免瓦片绘制到留白处。
		m_ImageCanvas.Clip(rectDraw);
		
		//用户输出的边界。
		GsDisplayTransformation dt = new GsDisplayTransformation(mapextent,rectDraw);
		dt.DPI((int)m_DPI);
		//创建display
		GsDisplay pDisplay = new GsDisplay(m_ImageCanvas,dt);
		GsTrackCancel pCancel = new GsTrackCancel(); 
		
		PrepareBackground(pDisplay,pCancel);
		
		//输出位图
		m_Map.Output(pDisplay, pCancel);
		
		//删除相关资源。
		pCancel.delete();
		pDisplay.delete();
		dt.delete();
		
		//清除Clip范围。避免外面无法绘制
		m_ImageCanvas.ClearClip();
		return m_ImageCanvas;
	}
	void PrepareBackground(GsDisplay pDisplay ,GsTrackCancel pCancel)
	{
		/*
		GsDisplayTransformation dt = pDisplay.DisplayTransformation();
		double dpiScale = pDisplay.DisplayTransformation().DPI() / 96;
		
		double dblRes = pDisplay.DisplayTransformation().Resolution();
		
		//GsImagePtr ptrImg =  m_ImageCanvas.CreateImage(pBuff, nLen);
		int nLevel = m_Pyramid.BestLevel(dpiScale *  dblRes);
		
		GsBox box = pDisplay.ClipEnvelope();
		
		int range[] = new int[4];
		m_Pyramid.TileIndexRange(box.getXMin(), box.getYMin(), box.getXMax(), box.getYMax(), nLevel, range);
		
		int[] screenx = new int[1];
		dt.FromMap(x, y, screenX, screenY);
		
		//long sizex = dpiScale * m_Pyramid.getTileSizeX();
		//long sizey = dpiScale * m_Pyramid.getTileSizeY();
		m_ImageCanvas.DrawImage(pImg, source, target)*/
		
		//ptrImg.delete();
		
	}
	/**
	 * 完成绘制压缩数据到PNG图片。
	 */
	public void FinishLayout()
	{
		m_ImageCanvas.Image().SavePNG(m_PNG);
	}
	/**
	 * 回收资源
	 */
	public void Dispose()
	{
		finalize();
	}
	/**
	 * 绘制图外要素，图框等
	 */
	public void DrawLayoutFrameAndElement()
	{
		
	}
	String 			m_PNG = null;
	String 			m_CanvasFile = null;
	GsImageCanvas m_ImageCanvas = null;
}
