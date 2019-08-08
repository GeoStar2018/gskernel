package unittest;

import java.io.File;

import com.geostar.kernel.*;
/**
 * 切片服务
 * @author Administrator
 * 该工具负责切片,获取状态
 */
public class SliceService extends TileSpliter_OnProgress{
	public SliceService(String fileAbsoultePath,String strOutputTile,boolean bDEM,GsPyramid pyramid)
	{

		File file = new File(fileAbsoultePath);   
		GsGeoDatabaseFactory fac =new GsFileGeoDatabaseFactory (); 
		GsConnectProperty conn = new GsConnectProperty();
 
		conn.setServer(file.getParent());
		
		GsGeoDatabase gdb = fac.Open(conn);
		if(null == gdb)
		{
			System.out.println("can't open raster file database");
			return;
		}
		System.out.println(file.getName());
		
		GsRasterClass pRaster = gdb.OpenRasterClass(file.getName());
		if(null == pRaster)
		{
			System.out.println("can't open raster class");
			return;
		}
		double dblRes = pRaster.Extent().Width() / pRaster.Width();
		double dblResTop = pRaster.Extent().Width() / pyramid.getTileSizeX();
		
		setParam(pRaster,strOutputTile,bDEM,pyramid,pyramid.BestLevel(dblResTop),pyramid.BestLevel(dblRes));
		
	}
	void setParam(GsRasterClass pRaster,String strOutputTile,boolean bDEM,GsPyramid pyramid,int topLevel,int bottomLevel)
	{

		m_StartLevel = topLevel;
		m_EndLevel = bottomLevel;
		m_strOutput = strOutputTile;
			
		File file = new File(strOutputTile);  
		GsSqliteGeoDatabaseFactory facSqlite =new GsSqliteGeoDatabaseFactory(); 
		
		GsConnectProperty conn = new GsConnectProperty();
		conn.setServer(file.getParent());
		GsGeoDatabase gdb = facSqlite.Open(conn);
		if(null == gdb)
		{
			System.out.println("can't open output sqlite database");
			return;
		}
		GsTileColumnInfo col = new GsTileColumnInfo();
		//如果是DEM则设置DEM的类型
		if(bDEM)
			col.setFeatureType(GsFeatureType.eTerrainTileFeature);
		else
			col.setFeatureType(GsFeatureType.eImageTileFeature);
		
		GsTileClass tileClass = gdb.CreateTileClass(file.getName(), pRaster.SpatialReference(), pyramid, col);
		if(null == tileClass)
		{
			System.out.println("can't open output tile class");
			return;
		}
		
		m_Spliter = new GsRasterTileSpliter(pRaster,pyramid,tileClass);
	}
	/**
	 * 切片方法
	 * @param fileAbsoultePath 文件绝对路径
	 * @param pyramid 金字塔参数，固定值传360度金字塔
	 * @param spatialReference 空间参考wkt串 例如 GEOGCS["CGCS2000",DATUM["D_CGCS2000",SPHEROID["CGCS2000",6378137,298.257222101]],PRIMEM["Greenwich",0],UNIT["degree",0.0174532925199433]]
	 * @param topLevel 顶级级别索引
	 * @param bottomLevel 底级级别索引
	 * @return void
	 * 
	 * 金字塔参数值
	 *  <Config Name="SchemeData" Type="System.String" Visible="True" VariantType="System.String" Description="" Caption="">
			<Value>
				<Pyramid>
					<Basic Description="360度金字塔方案" PyramidID="72f3fcab-c1c2-4942-bb17-af5a3b469e5d" Name="360度金字塔"/>
					<Level TopLevelIndex="0" BottomLevelIndex="20" ScaleX="2" ScaleY="2"/>
					<TileBasic TileSizeX="256" TileSizeY="256" OriginRowIndex="0" OriginColIndex="0"/>
					<TopTile FromX="-180.00000000000000000000" FromY="90.00000000000000000000" ToX="180.00000000000000000000" ToY="-270.00000000000000000000"/>
					<Const PI="3.14159265358979310000" Tolerance="0.00000011920928955078"/>
					<Range XMin="-180.00000000000000000000" YMin="-90.00000000000000000000" XMax="180.00000000000000000000" YMax="90.00000000000000000000"/>
				</Pyramid>
			</Value>
		</Config>
	 */
	public void setSliceConfigure(String fileAbsoultePath,String strOutputTile,boolean bDEM,String pyramid,int topLevel,int bottomLevel) {
		
		GsPyramid pPyramid = new GsMultiPyramid(pyramid);
		File file = new File(fileAbsoultePath);  
		GsFileGeoDatabaseFactory fac =new GsFileGeoDatabaseFactory (); 
		GsConnectProperty conn = new GsConnectProperty();
		conn.setServer(file.getParent());
		GsGeoDatabase gdb = fac.Open(conn);
		if(null == gdb)
		{
			System.out.println("can't open raster file database");
			return;
		}
		GsRasterClass pRaster = gdb.OpenRasterClass(file.getName());
		if(null == pRaster)
		{
			System.out.println("can't open raster file database");
			return;
		}	
		setParam(pRaster,strOutputTile,bDEM,pPyramid,topLevel,bottomLevel);
	}
	int m_StartLevel;
	int m_EndLevel;
	GsRasterTileSpliter m_Spliter;
	
	/**
	 * 启动切片服务
	 * @return true表示成功启动,false表示启动失败
	 */
	public boolean startSlice(){
		//TODO
		System.out.println("开始切片");
		if(m_Spliter == null)
		{
			System.out.println("m_Spliter is null");
			return false;	
		}
		m_Spliter.OnProgressAdd(this);
		return m_Spliter.Execute(m_StartLevel,m_EndLevel);
	}
	public  boolean OnProgress(int level,int row,int col,long nNow,long nTotal){
		if(m_Level != level)
		{
			m_Level = level;

			System.out.println("process  leve " + m_Level);
		}
		m_Progress = (int)(100 * nNow / nTotal);
		return true;
	}
	int m_Level =-1;
	/**
	 * 获取当前进度,100表示完成
	 */
	public int getProgress() {
		//TODO
		return m_Progress;
	}
	int m_Progress=0;
	String m_strOutput;
	/**
	 * 获取成果文件路径
	 */
	public String getResultFilePath() {
		//TODO
		return m_strOutput;
	}
}
