package unittest;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

//import com.geostar.core.spatialreference.CoordRefSystem;
//import com.geostar.core.spatialreference.ICoordRefSystem;

import com.geostar.kernel.*;
public class PyramidTest {
	GsSpatialReference crs = null;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.loadLibrary("gsjavaport");
		GsKernel.Initialize();
		System.out.println("loadlibrary");
		String strCurDir = System.getProperty("user.dir");
		strCurDir += "/../data/coordinatesystem/EPSG.txt";
		System.out.println(strCurDir);
		GsGlobeConfig.Instance().Child("Kernel/SpatialRererence/EPSG")
				.Value(strCurDir);
		System.out.println("spatialreference");
		
		GsSpatialReference p = new GsSpatialReference(4326);
		System.out.println(p.EquatorialRadiusA());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		crs = new GsSpatialReference(4527);
	}

	@After
	public void tearDown() throws Exception {
	}

	double Scale2Res(double dScale, GsSpatialReference pSpa)
	{
		// 长半轴, m_ptrRef 为空间参考
		double dRadius = pSpa.EquatorialRadiusA();
		// 米转换度因子
		double dFactor = 2.0 * dRadius * Math.PI / 360.0;
	
		if (pSpa.IsGeographic())
		{
			// 地理坐标系先将比例尺单位转为度
			dScale = dScale / dFactor;
		}
		double dRes = dScale / (1000 * 96 / 2.54);
		return dRes; 
	}
	
	double Res2Scale(double res, GsSpatialReference  pSpa)
	{
		// 长半轴, m_ptrRef 为空间参考
		double dRadius = pSpa.EquatorialRadiusA();
		// 米转换度因子
		double dFactor = 2.0 * dRadius * Math.PI  / 360.0;
		res *= (1000 * 96 / 2.54);
		if (pSpa.IsGeographic())
		{
			res *= dFactor;
		}
		return res;
	}

	@Test
	public void testScale2Res2() {
		
//		1.3961381632719252E8 
//		6.980690816359626E7 
//		3.490345408179813E7 
//		1.7451727040899064E7
//		8725863.520449532 
//		4362931.760224766 
		GsPyramid pyScale = new GsPyramid();

		double scale[] = {1.3961381632719252E8 , 6.980690816359626E7 , 3.490345408179813E7 , 1.7451727040899064E7,8725863.520449532 ,4362931.760224766 };
		for(int i = 0; i< 6;i++)
			System.out.println(scale[i]);
		
		for(int i = 0; i< 6;i++)
			scale[i]= Scale2Res(scale[i],crs);
		System.out.println("-----------------");
		
		for(int i = 0; i< 6;i++)
			System.out.println(scale[i]);
		
		pyScale = GsPyramid.CreatePyramid( -180, 90, scale, 6, 256, 256, GsPyramidOriginLocation.eNorthWest);
		String pyScaleStr = pyScale.ToString();

		System.out.println("通过比例尺创建金字塔成功");
		System.out.println(pyScaleStr);
	}
}
