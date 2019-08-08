package unittest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.geostar.kernel.GsAffineCoordinateTransformation;
import com.geostar.kernel.GsCoordinateTransformation;
import com.geostar.kernel.GsCoordinateTransformationMethod;
import com.geostar.kernel.GsGlobeConfig;
import com.geostar.kernel.GsParameterProjectCoordinateTransformation;
import com.geostar.kernel.GsProjectCoordinateTransformation;
import com.geostar.kernel.GsSpatialReference;
import com.geostar.kernel.GsSpatialReferenceFormat;
import com.geostar.kernel.GsSpatialReferenceManager;
//import com.geostar.kernel.extensions.TrajectoryTransformation;

public class spatialreference2 {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.loadLibrary("gsjavaport");
		System.out.println("spatialreference2 loadlibrary gsjavaport");
		//System.loadLibrary("ggsextensions");
		System.out.println("spatialreference2 loadlibrary ggsextensions");
		String strCurDir = System.getProperty("user.dir");
		
		System.out.println("		String strCurDir = System.getProperty;");
		strCurDir += "/../data/coordinatesystem/EPSG.txt";
		GsGlobeConfig.Instance().Child("Kernel/SpatialRererence/EPSG")
				.Value(strCurDir);
		System.out.println("spatialreference2");
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

	/**
	 * 根据WKT串获取EPSG号
	 * 
	 * @param wkt
	 *            WKT空间描述串
	 * @return 对应的EPSG号，未找到对应的EPSG号时返回0
	 */
	public List<Integer> getMatchEPSG(GsSpatialReference sr) {
		GsSpatialReferenceManager Mgr = new GsSpatialReferenceManager();
		GsSpatialReferenceManager.GsSpatialReferenceCategory pCategory = Mgr
				.SpecialCategory(GsSpatialReferenceManager.GsSpecialCategory.eStandardEPSG);
		List<Integer> epsgs = new ArrayList<Integer>();
		int size = pCategory.Count();
		for (int i = 0; i < size; i++) {
			GsSpatialReference ptrSR = pCategory.SpatialReference(i);
			if (ptrSR.IsSameParameter(sr)) {
				epsgs.add(ptrSR.EPSG());
				ptrSR.delete();
				break;
			}
			ptrSR.delete();
		}
		pCategory.delete();
		Mgr.delete();
		return epsgs;
	}

	/**
	 * 根据两组坐标控制点计算六个放射变换参数
	 * 
	 * @param srcXArray
	 *            源X轴坐标点
	 * 
	 * @param srcYArray
	 *            源Y轴坐标点
	 * 
	 * @param srcLen
	 *            源坐标点的个数
	 * 
	 * @param targetXArray
	 *            目标X轴坐标点
	 * 
	 * @param targetYArray
	 *            目标Y轴坐标点
	 * 
	 * @param targetLen
	 *            目标坐标点的个数
	 * 
	 * @return 仿射变换参数，依次为：X轴平移、X轴旋转弧度、X轴缩放尺度、Y轴平移、Y轴旋转弧度、Y轴缩放尺度
	 */
	public double[] coordinateToAffine(double[] srcXArray, double[] srcYArray,
			int srcLen, double[] targetXArray, double[] targetYArray,
			int targetLen) {
		double[] srcPoints = new double[srcLen * 2];
		for (int i = 0, j = 0; i < srcLen; i++, j++) {
			srcPoints[j] = srcXArray[i];
			j++;
			srcPoints[j] = srcYArray[i];
		}
		double[] targetPoints = new double[targetLen * 2];
		for (int i = 0, j = 0; i < targetLen; i++, j++) {
			targetPoints[j] = targetXArray[i];
			j++;
			targetPoints[j] = targetYArray[i];
		}
		GsAffineCoordinateTransformation gsAffTrans = new GsAffineCoordinateTransformation(
				srcPoints, targetPoints, srcLen * 2);
		double[] param = new double[6];
		gsAffTrans.Elements(param);
		gsAffTrans.delete();
		// 转换成old版本参数
		double[] oldParam = new double[6];
		oldParam[0] = param[4];
		oldParam[1] = param[0];
		oldParam[2] = param[2];
		oldParam[3] = param[5];
		oldParam[4] = param[1];
		oldParam[5] = param[3];
		return oldParam;
	}

	/**
	 * 根据WKT串获取EPSG号
	 * 
	 * @param wkt
	 *            WKT空间描述串
	 * @return 对应的EPSG号，未找到对应的EPSG号时返回0
	 */
	public int WKTToEPSG(String wkt) {
		GsSpatialReference sr = new GsSpatialReference(wkt,
				GsSpatialReferenceFormat.eUnknownFormat);
		int epsg = sr.EPSG();
		if (epsg < 1) {
			// 不规范或者用户自定义的wkt
			List<Integer> matchEPSG = getMatchEPSG(sr);
			if (matchEPSG.size() > 0) {
				// 默认取第一个
				epsg = matchEPSG.get(0);
			}
		}
		sr.delete();
		return epsg;
	}

	/**
	 * 根据WKT描述串获取对应的空间参考参数对象
	 * 
	 * @param wkt
	 *            WKT空间描述串
	 * 
	 * @return 空间参考参数对象
	 */
	public GsSpatialReference wktToSpatialRef(String wkt) {
		return new GsSpatialReference(wkt,
				GsSpatialReferenceFormat.eUnknownFormat);
	}

	/**
	 * 根据EPSG号及布尔莎七参数进行坐标转换
	 * 
	 * @param srcEPSG
	 *            坐标参考EPSG号
	 * 
	 * @param targetEPSG
	 *            目标坐标参考的EPSG号
	 * 
	 * @param bursaParams
	 *            布尔莎七参数
	 * 
	 * @param xArray
	 *            X轴坐标点数组（输出参数）
	 * 
	 * @param yArray
	 *            Y轴坐标点数组（输出参数）
	 * 
	 * @param zArray
	 *            高程
	 * 
	 * @param len
	 *            坐标点的个数
	 * 
	 * @return 转换成功返回“true”，否则返回“false”
	 */
	public boolean coordinateTransformByParamEPSG(int srcEPSG, int targetEPSG,
			double[] bursaParams, double[] xArray, double[] yArray,
			double[] zArray, int len) {
		boolean flag = false;
		try {
			GsSpatialReference sr = new GsSpatialReference(srcEPSG);
			GsSpatialReference tar = new GsSpatialReference(targetEPSG);
			double param[] = new double[bursaParams.length];
			// 截取3位
			for (int i = 0; i < bursaParams.length; i++) {
				param[i] = (int) (bursaParams[i] * 1000);
				param[i] /= 1000;
			}
			GsCoordinateTransformation ptrTrans = new GsParameterProjectCoordinateTransformation(
					sr, tar, GsCoordinateTransformationMethod.eCoordinateFrame,
					param);
			flag = ptrTrans.Transformation(xArray, yArray, zArray, len, 1);
			// 销毁对象
			ptrTrans.delete();
			tar.delete();
			sr.delete();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return flag;
	}

	/**
	 * 按仿射变换参数进行坐标转换
	 * 
	 * @param affine
	 *            仿射变换参数，依次为：X轴平移、X轴旋转弧度、X轴缩放尺度、Y轴平移、Y轴旋转弧度、Y轴缩放尺度
	 * 
	 * @param xArray
	 *            X轴坐标点数组（输出参数）
	 * 
	 * @param yArray
	 *            Y轴坐标点数组（输出参数）
	 * 
	 * @param len
	 *            坐标点的个数
	 * 
	 * 
	 * @return 转换成功返回“true”，否则返回“false”
	 */
	public boolean coordinateTransformByAffine(double[] affine,
			double[] xArray, double[] yArray, int len) {
		double[] param = new double[6];
		param[4] = affine[0];
		param[0] = affine[1];
		param[2] = affine[2];
		param[5] = affine[3];
		param[1] = affine[4];
		param[3] = affine[5];
		boolean flag = false;
		try {
			GsAffineCoordinateTransformation gsAffTrans = new GsAffineCoordinateTransformation(
					param);
			flag = gsAffTrans.Transformation(xArray, yArray, len, 1);
			gsAffTrans.delete();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return flag;
	}

	/**
	 * 根据EPSG号进行坐标转换
	 * 
	 * @param srcEPSG
	 *            坐标参考EPSG号
	 * 
	 * @param targetEPSG
	 *            目标坐标参考的EPSG号
	 * 
	 * @param xArray
	 *            X轴坐标点数组（输出参数）
	 * 
	 * @param yArray
	 *            Y轴坐标点数组（输出参数）
	 * 
	 * @param len
	 *            坐标点的个数
	 * 
	 * @return 转换成功返回“true”，否则返回“false”
	 */
	public boolean coordinateTransformByEPSG(int srcEPSG, int targetEPSG,
			double[] xArray, double[] yArray, int len) {
		boolean flag = false;
		try {
			GsSpatialReference sr = new GsSpatialReference(srcEPSG);
			GsSpatialReference tar = new GsSpatialReference(targetEPSG);
			GsProjectCoordinateTransformation gsProjectTrans = new GsProjectCoordinateTransformation(
					sr, tar);
			flag = gsProjectTrans.Transformation(xArray, yArray, len, 1);
			// 销毁对象
			gsProjectTrans.delete();
			tar.delete();
			sr.delete();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return flag;
	}

	/**
	 * 构造函数测试：通过EPSG号构建对象
	 */
	@Test
	public void structByEPSG1() {
		System.out.println("structByEPSG1");
		GsSpatialReference sr = new GsSpatialReference(4326);
		System.out.println(sr.ExportToWKT());
		String wkt = "GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AXIS[\"Latitude\",NORTH],AXIS[\"Longitude\",EAST],AUTHORITY[\"EPSG\",\"4326\"]]";
		boolean flag = wkt.equalsIgnoreCase(sr.ExportToWKT());
		sr.delete();
		assertTrue(flag);
	}

	/**
	 * 构造函数测试：通过EPSG号构建对象
	 */
	@Test
	public void structByEPSG2() {
		System.out.println("structByEPSG2");
		GsSpatialReference sr = new GsSpatialReference(4490);
		System.out.println(sr.ExportToWKT());
		String wkt = "GEOGCS[\"China Geodetic Coordinate System 2000\",DATUM[\"China_2000\",SPHEROID[\"CGCS2000\",6378137,298.257222101,AUTHORITY[\"EPSG\",\"1024\"]],AUTHORITY[\"EPSG\",\"1043\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AXIS[\"Latitude\",NORTH],AXIS[\"Longitude\",EAST],AUTHORITY[\"EPSG\",\"4490\"]]";
		boolean flag = wkt.equalsIgnoreCase(sr.ExportToWKT());
		sr.delete();
		assertTrue(flag);
	}

	/**
	 * 构造函数测试：通过EPSG号构建对象
	 */
	@Test
	public void structByEPSG3() {
		System.out.println("structByEPSG3");
		GsSpatialReference sr = new GsSpatialReference(4214);
		System.out.println(sr.ExportToWKT());
		String wkt = "GEOGCS[\"Beijing 1954\",DATUM[\"Beijing_1954\",SPHEROID[\"Krassowsky 1940\",6378245,298.3,AUTHORITY[\"EPSG\",\"7024\"]],TOWGS84[15.8,-154.4,-82.3,0,0,0,0],AUTHORITY[\"EPSG\",\"6214\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AXIS[\"Latitude\",NORTH],AXIS[\"Longitude\",EAST],AUTHORITY[\"EPSG\",\"4214\"]]";
		boolean flag = wkt.equalsIgnoreCase(sr.ExportToWKT());
		sr.delete();
		assertTrue(flag);
	}

	/**
	 * 构造函数测试：通过EPSG号构建对象
	 */
	@Test
	public void structByEPSG4() {
		System.out.println("structByEPSG4");
		GsSpatialReference sr = new GsSpatialReference(4555);
		System.out.println(sr.ExportToWKT());
		String wkt = "GEOGCS[\"New Beijing\",DATUM[\"New_Beijing\",SPHEROID[\"Krassowsky 1940\",6378245,298.3,AUTHORITY[\"EPSG\",\"7024\"]],AUTHORITY[\"EPSG\",\"1045\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AXIS[\"Latitude\",NORTH],AXIS[\"Longitude\",EAST],AUTHORITY[\"EPSG\",\"4555\"]]";
		boolean flag = wkt.equalsIgnoreCase(sr.ExportToWKT());
		sr.delete();
		assertTrue(flag);
	}

	/**
	 * 构造函数测试：通过EPSG号构建对象
	 */
	@Test
	public void structByEPSG5() {
		System.out.println("structByEPSG5");
		GsSpatialReference sr = new GsSpatialReference(4610);
		System.out.println(sr.ExportToWKT());
		String wkt = "GEOGCS[\"Xian 1980\",DATUM[\"Xian_1980\",SPHEROID[\"IAG 1975\",6378140,298.257,AUTHORITY[\"EPSG\",\"7049\"]],AUTHORITY[\"EPSG\",\"6610\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AXIS[\"Latitude\",NORTH],AXIS[\"Longitude\",EAST],AUTHORITY[\"EPSG\",\"4610\"]]";
		boolean flag = wkt.equalsIgnoreCase(sr.ExportToWKT());
		sr.delete();
		assertTrue(flag);
	}

	/**
	 * 构造函数测试：通过EPSG号构建对象
	 */
	@Test
	public void structByEPSG6() {
		System.out.println("structByEPSG6");
		GsSpatialReference sr = new GsSpatialReference(4526);
		System.out.println(sr.ExportToWKT());
		String wkt = "PROJCS[\"CGCS2000 / 3-degree Gauss-Kruger zone 38\",GEOGCS[\"China Geodetic Coordinate System 2000\",DATUM[\"China_2000\",SPHEROID[\"CGCS2000\",6378137,298.257222101,AUTHORITY[\"EPSG\",\"1024\"]],AUTHORITY[\"EPSG\",\"1043\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AXIS[\"Latitude\",NORTH],AXIS[\"Longitude\",EAST],AUTHORITY[\"EPSG\",\"4490\"]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",114],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",38500000],PARAMETER[\"false_northing\",0],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],AXIS[\"X\",NORTH],AXIS[\"Y\",EAST],AUTHORITY[\"EPSG\",\"4526\"]]";
		boolean flag = wkt.equalsIgnoreCase(sr.ExportToWKT());
		sr.delete();
		assertTrue(flag);
	}

	/**
	 * 构造函数测试：通过EPSG号构建对象
	 */
	@Test
	public void structByEPSG7() {
		System.out.println("structByEPSG7");
		GsSpatialReference sr = new GsSpatialReference(2345);
		System.out.println(sr.ExportToWKT());
		String wkt = "PROJCS[\"Xian 1980 / Gauss-Kruger CM 117E\",GEOGCS[\"Xian 1980\",DATUM[\"Xian_1980\",SPHEROID[\"IAG 1975\",6378140,298.257,AUTHORITY[\"EPSG\",\"7049\"]],AUTHORITY[\"EPSG\",\"6610\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AXIS[\"Latitude\",NORTH],AXIS[\"Longitude\",EAST],AUTHORITY[\"EPSG\",\"4610\"]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",117],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",500000],PARAMETER[\"false_northing\",0],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],AXIS[\"X\",NORTH],AXIS[\"Y\",EAST],AUTHORITY[\"EPSG\",\"2345\"]]";
		boolean flag = wkt.equalsIgnoreCase(sr.ExportToWKT());
		sr.delete();
		assertTrue(flag);
	}

	/**
	 * 构造函数测试：通过EPSG号构建对象
	 */
	@Test
	public void structByEPSG8() {
		System.out.println("structByEPSG8");
		GsSpatialReference sr = new GsSpatialReference(2428);
		System.out.println(sr.ExportToWKT());
		String wkt = "PROJCS[\"Beijing 1954 / 3-degree Gauss-Kruger CM 93E\",GEOGCS[\"Beijing 1954\",DATUM[\"Beijing_1954\",SPHEROID[\"Krassowsky 1940\",6378245,298.3,AUTHORITY[\"EPSG\",\"7024\"]],TOWGS84[15.8,-154.4,-82.3,0,0,0,0],AUTHORITY[\"EPSG\",\"6214\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AXIS[\"Latitude\",NORTH],AXIS[\"Longitude\",EAST],AUTHORITY[\"EPSG\",\"4214\"]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",93],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",500000],PARAMETER[\"false_northing\",0],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],AXIS[\"X\",NORTH],AXIS[\"Y\",EAST],AUTHORITY[\"EPSG\",\"2428\"]]";
		boolean flag = wkt.equalsIgnoreCase(sr.ExportToWKT());
		sr.delete();
		assertTrue(flag);
	}

	@Test
	public void structByEPSG9() {
		GsSpatialReference sr = new GsSpatialReference(21473);
		System.out.println(sr.ExportToWKT());
		String wkt = "PROJCS[\"Beijing 1954 / Gauss-Kruger 13N (deprecated)\",GEOGCS[\"Beijing 1954\",DATUM[\"Beijing_1954\",SPHEROID[\"Krassowsky 1940\",6378245,298.3,AUTHORITY[\"EPSG\",\"7024\"]],TOWGS84[15.8,-154.4,-82.3,0,0,0,0],AUTHORITY[\"EPSG\",\"6214\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AXIS[\"Latitude\",NORTH],AXIS[\"Longitude\",EAST],AUTHORITY[\"EPSG\",\"4214\"]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",75],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",500000],PARAMETER[\"false_northing\",0],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],AXIS[\"X\",NORTH],AXIS[\"Y\",EAST],AUTHORITY[\"EPSG\",\"21473\"]]";
		boolean flag = wkt.equalsIgnoreCase(sr.ExportToWKT());
		sr.delete();
		assertTrue(flag);
	}

	@Test
	public void structByEPSG10() {
		GsSpatialReference sr = new GsSpatialReference(3395);
		System.out.println(sr.ExportToWKT());
		String wkt = "PROJCS[\"WGS 84 / World Mercator\",GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AXIS[\"Latitude\",NORTH],AXIS[\"Longitude\",EAST],AUTHORITY[\"EPSG\",\"4326\"]],PROJECTION[\"Mercator_1SP\"],PARAMETER[\"central_meridian\",0],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",0],PARAMETER[\"false_northing\",0],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],AXIS[\"Easting\",EAST],AXIS[\"Northing\",NORTH],AUTHORITY[\"EPSG\",\"3395\"]]";
		boolean flag = wkt.equalsIgnoreCase(sr.ExportToWKT());
		sr.delete();
		assertTrue(flag);
	}

	// *******************************************************************************

	// *******************************************************************************
	// 常用WKT测试
	/**
	 * 构造函数测试：通过WKT构建对象
	 */
	@Test
	public void structByWKT1() {
		int epsg = 4326;
		String wkt = "GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4326\"]]";
		int wktToEPSG = WKTToEPSG(wkt);
		WKTToEPSG(wkt);
		boolean flag = wktToEPSG == epsg;
		assertTrue(flag);
	}

	/**
	 * 构造函数测试：通过WKT构建对象
	 */
	@Test
	public void structByWKT2() {
		int epsg = 4490;
		String wkt = "GEOGCS[\"China Geodetic Coordinate System 2000 \",DATUM[\"China 2000\",SPHEROID[\"CGCS2000\",6378137,298.257222101,AUTHORITY[\"EPSG\",\"1024\"]],AUTHORITY[\"EPSG\",\"6610\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4490\"]]";
		int wktToEPSG = WKTToEPSG(wkt);
		WKTToEPSG(wkt);
		boolean flag = wktToEPSG == epsg;
		assertTrue(flag);
	}

	/**
	 * 构造函数测试：通过WKT构建对象
	 */
	@Test
	public void structByWKT3() {
		int epsg = 4214;
		String wkt = "GEOGCS[\"Beijing 1954\",DATUM[\"Beijing_1954\",SPHEROID[\"Krassowsky 1940\",6378245,298.3,AUTHORITY[\"EPSG\",\"7024\"]],AUTHORITY[\"EPSG\",\"6214\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4214\"]]";
		int wktToEPSG = WKTToEPSG(wkt);
		WKTToEPSG(wkt);
		boolean flag = wktToEPSG == epsg;
		assertTrue(flag);
	}

	/**
	 * 构造函数测试：通过WKT构建对象
	 */
	@Test
	public void structByWKT4() {
		int epsg = 4555;
		String wkt = "GEOGCS[\"New Beijing\",DATUM[\"New_Beijing\",SPHEROID[\"Krassowsky 1940\",6378245,298.3,AUTHORITY[\"EPSG\",\"7024\"]],AUTHORITY[\"EPSG\",\"1045\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4555\"]]";
		int wktToEPSG = WKTToEPSG(wkt);
		WKTToEPSG(wkt);
		boolean flag = wktToEPSG == epsg;
		assertTrue(flag);
	}

	/**
	 * 构造函数测试：通过WKT构建对象
	 */
	@Test
	public void structByWKT5() {
		int epsg = 4610;
		String wkt = "GEOGCS[\"Xian 1980\",DATUM[\"Xian_1980\",SPHEROID[\"IAG 1975\",6378140,298.257,AUTHORITY[\"EPSG\",\"7049\"]],AUTHORITY[\"EPSG\",\"6610\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4610\"]]";
		int wktToEPSG = WKTToEPSG(wkt);
		WKTToEPSG(wkt);
		boolean flag = wktToEPSG == epsg;
		assertTrue(flag);
	}

	/**
	 * 构造函数测试：通过WKT构建对象
	 */
	@Test
	public void structByWKT6() {
		int epsg = 4526;
		String wkt = "PROJCS[\"CGCS2000 / 3-degree Gauss-Kruger zone 38\",GEOGCS[\"China Geodetic Coordinate System 2000\",DATUM[\"China_2000\",SPHEROID[\"CGCS2000\",6378137,298.257222101,AUTHORITY[\"EPSG\",\"1024\"]],AUTHORITY[\"EPSG\",\"1043\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4490\"]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",114],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",38500000],PARAMETER[\"false_northing\",0],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],AUTHORITY[\"EPSG\",\"4526\"]]";
		int wktToEPSG = WKTToEPSG(wkt);
		WKTToEPSG(wkt);
		boolean flag = wktToEPSG == epsg;
		assertTrue(flag);
	}

	/**
	 * 构造函数测试：通过WKT构建对象
	 */
	@Test
	public void structByWKT7() {
		int epsg = 2345;
		String wkt = "PROJCS[\"Xian 1980 / Gauss-Kruger CM 117E\",GEOGCS[\"Xian 1980\",DATUM[\"Xian_1980\",SPHEROID[\"IAG 1975\",6378140,298.257,AUTHORITY[\"EPSG\",\"7049\"]],AUTHORITY[\"EPSG\",\"6610\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4610\"]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",117],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",500000],PARAMETER[\"false_northing\",0],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],AUTHORITY[\"EPSG\",\"2345\"]]";
		int wktToEPSG = WKTToEPSG(wkt);
		WKTToEPSG(wkt);
		boolean flag = wktToEPSG == epsg;
		assertTrue(flag);
	}

	/**
	 * 构造函数测试：通过WKT构建对象
	 */
	@Test
	public void structByWKT8() {
		int epsg = 2428;
		String wkt = "PROJCS[\"Beijing 1954 / 3-degree Gauss-Kruger CM 93E\",GEOGCS[\"Beijing 1954\",DATUM[\"Beijing_1954\",SPHEROID[\"Krassowsky 1940\",6378245,298.3,AUTHORITY[\"EPSG\",\"7024\"]],TOWGS84[15.8,-154.4,-82.3,0,0,0,0],AUTHORITY[\"EPSG\",\"6214\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4214\"]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",93],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",500000],PARAMETER[\"false_northing\",0],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],AUTHORITY[\"EPSG\",\"2428\"]]";
		int wktToEPSG = WKTToEPSG(wkt);
		WKTToEPSG(wkt);
		boolean flag = wktToEPSG == epsg;
		assertTrue(flag);
	}

	/**
	 * 构造函数测试：通过WKT构建对象
	 */
	@Test
	public void structByWKT9() {
		int epsg = 21473;
		String wkt = "PROJCS[\"Beijing 1954 / Gauss-Kruger 13N (deprecated)\",GEOGCS[\"Beijing 1954\",DATUM[\"Beijing_1954\",SPHEROID[\"Krassowsky 1940\",6378245,298.3,AUTHORITY[\"EPSG\",\"7024\"]],TOWGS84[15.8,-154.4,-82.3,0,0,0,0],AUTHORITY[\"EPSG\",\"6214\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4214\"]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",75],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",500000],PARAMETER[\"false_northing\",0],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],AUTHORITY[\"EPSG\",\"21473\"]]";
		int wktToEPSG = WKTToEPSG(wkt);
		WKTToEPSG(wkt);
		boolean flag = wktToEPSG == epsg;
		assertTrue(flag);
	}

	/**
	 * 构造函数测试：通过WKT构建对象
	 */
	@Test
	public void structByWKT10() {
		int epsg = 3395;
		String wkt = "PROJCS[\"WGS 84 / World Mercator\",GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4326\"]],PROJECTION[\"Mercator_1SP\"],PARAMETER[\"central_meridian\",0],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",0],PARAMETER[\"false_northing\",0],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],AXIS[\"Easting\",EAST],AXIS[\"Northing\",NORTH],AUTHORITY[\"EPSG\",\"3395\"]]";
		int wktToEPSG = WKTToEPSG(wkt);
		WKTToEPSG(wkt);
		boolean flag = wktToEPSG == epsg;
		assertTrue(flag);
	}

//	/**
//	 * 构造函数测试：通过WKT构建对象
//	 */
//	@Test
//	public void structByWKT11() {
//		int epsg = 4490;
//		String wkt = "GEOGCS[\"CGCS2000\",DATUM[\"CGCS2000\",SPHEROID[\"CGCS2000\",6378137,298.257222101]],PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433]]";
//		int wktToEPSG = WKTToEPSG(wkt);
//		WKTToEPSG(wkt);
//		boolean flag = wktToEPSG == epsg;
//		assertTrue(flag);
//	}

	/**
	 * 构造函数测试：通过WKT构建对象
	 */
	@Test
	public void structByWKT11() {
		int epsg = 2361;
		String wkt = "PROJCS[\"Xian 1980 / 3-degree Gauss-Kruger zone 37\",GEOGCS[\"Xian 1980\",DATUM[\"Xian 1980\",SPHEROID[\"Xian 1980\",6378140,298.257]],PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",111],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",37500000],PARAMETER[\"false_northing\",0],UNIT[\"Meter\",1]]";
		int wktToEPSG = WKTToEPSG(wkt);
		WKTToEPSG(wkt);
		boolean flag = wktToEPSG == epsg;
		assertTrue(flag);
	}

	/**
	 * 构造函数测试：通过Geostar5串构建对象
	 */
	@Test
	public void structByWKT13() {
		int epsg = 2361;
		String wkt = "83,Xian 1980,6378.14,6356.7552881575,298.257,6378.14,11,Xian 1980 / 3-degree Gauss-Kruger zone "
				+

				"37,1,37,0,500.,0.,111.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,1.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0, ,0.,0.,0.,0., "
				+

				",0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., "
				+

				",0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., "
				+

				",0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., "
				+

				",0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., , , , "
				+

				",0,0,0,0,0,0,0,0,0,0,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,,";
		int wktToEPSG = WKTToEPSG(wkt);
		WKTToEPSG(wkt);
		boolean flag = wktToEPSG == epsg;
		assertTrue(flag);
	}

//	/**
//	 * 构造函数测试：通过Geostar5串构建对象
//	 */
//	@Test
//	public void structByWKT14() {
//		int epsg = 3857;
//		String wkt = "92,WGS_1984,6378.137,6356.7523142452,298.257223563,6378.137,18,Mercator (1SP) (Web墨卡托投"+
// 
//"影),0,0,0,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,1.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0, ,0.,0.,0.,0., "+
// 
//",0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., "+
// 
//",0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., "+
// 
//",0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., "+
// 
//",0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., , , ,"+ 
// 
//",0,0,0,0,0,0,0,0,0,0,7.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,,";
//		int wktToEPSG = WKTToEPSG(wkt);
//		WKTToEPSG(wkt);
//		boolean flag = wktToEPSG == epsg;
//		assertTrue(flag);
//	}

//	/**
//	 * 构造函数测试：通过Geostar5串构建对象
//	 */
//	@Test
//	public void structByWKT15() {
//		int epsg = 4490;
//		String wkt = "93,CGCS_2000,6378.137,6356.7523141404,298.257222101,6378.137,1,0,0,0,0,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,"
//				+
//
//				"0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,"
//				+
//
//				"0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,"
//				+
//
//				"0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,"
//				+
//
//				"0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,"
//				+
//
//				"0.,0., , , ,0,0,0,0,0,0,0,0,0,0,0,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,,";
//		int wktToEPSG = WKTToEPSG(wkt);
//		WKTToEPSG(wkt);
//		boolean flag = wktToEPSG == epsg;
//		assertTrue(flag);
//	}

	/**
	 * 构造函数测试：通过Geostar5串构建对象
	 */
	@Test
	public void structByWKT14() {
		int epsg = 904490;
		String wkt = "93,CGCS_2000,6378.137,6356.7523141404,298.257222101,6378.137,18,Mercator (1SP) (Web墨卡托投影),0,0,0,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,"
				+ "0.,0.,1.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0, ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0.,"
				+ " ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,"
				+ "0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,"
				+ "0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,"
				+ "0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., , , , ,0,0,0,0,0,0,0,0,0,0,7.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,,";
		int wktToEPSG = WKTToEPSG(wkt);
		WKTToEPSG(wkt);
		boolean flag = wktToEPSG == epsg;
		assertTrue(flag);
	}

//	/**
//	 * 构造函数测试：通过WKT构建对象
//	 */
//	@Test
//	public void structByWKT17() {
//		int epsg = 904490;
//		String wkt = "PROJCS[\"Mercator (1SP) (墨卡托投影)\",GEOGCS[\"CGCS 2000\",DATUM[\"CGCS 2000\",SPHEROID[\"CGCS 2000\",6378137,298.257222101],TOWGS84[0,0,0,0,0,0,0]],PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433]],PROJECTION[\"Mercator_1SP\"],PARAMETER[\"central_meridian\",0],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",0],PARAMETER[\"false_northing\",0],UNIT[\"Meter\",1]]";
//		int wktToEPSG = WKTToEPSG(wkt);
//		WKTToEPSG(wkt);
//		boolean flag = wktToEPSG == epsg;
//		assertTrue(flag);
//	}

	/**
	 * 构造函数测试：通过WKT构建对象
	 */
	@Test
	public void structByWKT15() {
		int epsg = 904490;
		String wkt = "PROJCS[\"Mercator (1SP) (Web墨卡托投影)\",GEOGCS[\"CGCS_2000\",DATUM[\"CGCS_2000\",SPHEROID[\"CGCS_2000\",6378137,298.257222101],TOWGS84[0,0,0,0,0,0,0]],PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433]],PROJECTION[\"Mercator_1SP\"],PARAMETER[\"central_meridian\",0],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",0],PARAMETER[\"false_northing\",0],UNIT[\"Meter\",1]]";
		int wktToEPSG = WKTToEPSG(wkt);
		WKTToEPSG(wkt);
		boolean flag = wktToEPSG == epsg;
		assertTrue(flag);
	}

//	/**
//	 * 构造函数测试：通过WKT构建对象
//	 */
//	@Test
//	public void structByWKT19() {
//		int epsg = 904490;
//		String wkt = "PROJCS[\"Mercator (1SP) (Web墨卡托投影)\",GEOGCS[\"CGCS 2000\",DATUM[\"D_CGCS_2000\",SPHEROID[\"CGCS_2000\",6378137,298.2572221010],TOWGS84[0,0,0,0,0,0,0]],PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433]],PROJECTION[\"Mercator_1SP\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",0],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",0],PARAMETER[\"false_northing\",0],UNIT[\"Meter\",1],EXTENSION[\"PROJ4\",\"+proj=merc +a=6378137 +b=6378137 +lat_ts=0.0 +lon_0=0.0 +x_0=0.0 +y_0=0 +k=1.0 +units=m +nadgrids=@null +wktext +no_defs\"]]";
//		int wktToEPSG = WKTToEPSG(wkt);
//		WKTToEPSG(wkt);
//		boolean flag = wktToEPSG == epsg;
//		assertTrue(flag);
//	}

	@Test
	public void structByWKT16() {
		int epsg = 2385;
		String wkt = "PROJCS[\"Xian 1980 / 3-degree Gauss-Kruger CM 120E\",GEOGCS[\"IAG 1975\",DATUM[\"IAG 1975\",SPHEROID[\"IAG 1975\",6378140,298.257]],PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",120],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",500000],PARAMETER[\"false_northing\",0],UNIT[\"Meter\",1]]";
		int wktToEPSG = WKTToEPSG(wkt);
		WKTToEPSG(wkt);
		boolean flag = wktToEPSG == epsg;
		assertTrue(flag);
	}

	/**
	 * 构造函数测试：通过Geostar5串构建对象
	 */
	@Test
	public void structByWKT17() {
		int epsg = 2361;
		String wkt = "83,,6378.2064,6356.583799999,294.9786982,6371.116,0,,0,20,0,500.,0.,117.,30.,0.,100.,100.,100.,25.,45.,100.,120.,105.,123.,25.,45.,1.,0.,0.,30.,1.,0.,0.,0.,1.,0.,0.,0.,1.,1.,0.,0.,0.,1.,0.,0.,0.,1.,0.,0.,0.,1.,1.,0, ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., , , , ,0,0,0,0,0,0,0,0,0,0,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,,";
		GsSpatialReference sr = new GsSpatialReference(wkt,GsSpatialReferenceFormat.eUnknownFormat);
		System.out.println(sr.EPSG());
		System.out.println(sr.ExportToWKT());
		boolean flag = sr.EPSG()==epsg;
		sr.delete();
		assertFalse(flag);
	}

	/**
	 * 构造函数测试：通过WKT构建对象
	 */
	@Test
	public void structByWKT18() {
		int epsg = 2362;
		String wkt = "PROJCS[\"Gauss-Kruger (高斯－克里格投影)\",GEOGCS[\"Xian 1980\",DATUM[\"Xian 1980\",SPHEROID[\"Xian 1980\",6378140,298.257]],PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",114],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",38500000],PARAMETER[\"false_northing\",0]]";
		int wktToEPSG = WKTToEPSG(wkt);
		WKTToEPSG(wkt);
		boolean flag = wktToEPSG == epsg;
		assertTrue(flag);
	}

	/**
	 * 构造函数测试：通过WKT构建对象
	 */
	@Test
	public void structByWKT19() {
		int epsg = 4525;
		String wkt = "PROJCS[\"Xian 1980 / 3-degree Gauss-Kruger zone 37\",GEOGCS[\"CGCS 2000\",DATUM[\"CGCS 2000\",SPHEROID[\"CGCS 2000\",6378137,298.257222101]],PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",111],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",37500000],PARAMETER[\"false_northing\",0],UNIT[\"Meter\",1]]";
		int wktToEPSG = WKTToEPSG(wkt);
		WKTToEPSG(wkt);
		boolean flag = wktToEPSG == epsg;
		assertTrue(flag);
	}

//	/**
//	 * 构造函数测试：通过WKT构建对象
//	 */
//	@Test
//	public void structByWKT31() {
//		int epsg = 4525;
//		String wkt = "GEOGCS[\"GCS_WGS_1984\",DATUM[\"D_WGS_1984\",SPHEROID[\"WGS_1984\",6378137,298.257223563]],PRIMEM[\"Greenwich\",0],UNIT";
//		int wktToEPSG = WKTToEPSG(wkt);
//		WKTToEPSG(wkt);
//		boolean flag = wktToEPSG == epsg;
//		assertTrue(flag);
//	}

	/**
	 * 构造函数测试：通过WKT构建对象
	 */
	@Test
	public void structByWKT20() {
		int epsg = 4326;
		String wkt = "92,WGS_1984,6378.137,6356.7523142452,298.257223563,6378.137,1,0,0,0,0,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0., , , ,0,0,0,0,0,0,0,0,0,0,0,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,,";
		int wktToEPSG = WKTToEPSG(wkt);
		WKTToEPSG(wkt);
		boolean flag = wktToEPSG == epsg;
		assertTrue(flag);
	}

	/**
	 * 构造函数测试：通过WKT构建对象
	 */
	@Test
	public void structByWKT21() {
		int epsg = 4326;
		String wkt = "92,WGS_1984,6378.137,6356.7523142452,298.257223563,6378.137,1,,0,0,0,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0, ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., , , , ,0,0,0,0,0,0,0,0,0,0,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,,";
		int wktToEPSG = WKTToEPSG(wkt);
		WKTToEPSG(wkt);
		boolean flag = wktToEPSG == epsg;
		assertTrue(flag);
	}

	@Test
	public void structByWKT22() {
		int epsg = 4326;
		String wkt = "92, WGS_1984, 6378.137, 6356.7523142452, 298.257223563, 6378.137, 1, 地理坐标系, 0, 0, 0, 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0, 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., , , , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., ,";
		int wktToEPSG = WKTToEPSG(wkt);
		WKTToEPSG(wkt);
		boolean flag = wktToEPSG == epsg;
		assertTrue(flag);
	}

	@Test
	public void structByWKT23() {
		int epsg = 4326;
		String wkt = "92,WGS_1984,6378.137,6356.7523142452,298.257223563,6378.137,1,0,0,0,0,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0., , , ,0,0,0,0,0,0,0,0,0,0,0,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,,";
		int wktToEPSG = WKTToEPSG(wkt);
		WKTToEPSG(wkt);
		boolean flag = wktToEPSG == epsg;
		assertTrue(flag);
	}

//	@Test
//	public void structByWKT36() {
//		int epsg = 4490;
//		String wkt = "GEOGCS[\"GCS_China_Geodetic_Coordinate_System_2000\",DATUM[\"D_China_2000\",SPHEROID[\"CGCS2000\",6378137.0,298.257222101]],PRIMEM[\"Greenwich\",0.0],UNIT[\"Degree\",0.0174532925199433]]";
//		int wktToEPSG = WKTToEPSG(wkt);
//		WKTToEPSG(wkt);
//		boolean flag = wktToEPSG == epsg;
//		assertTrue(flag);
//	}

//	@Test
//	public void structByWKT37() {
//		int epsg = 4490;
//		String wkt = "PROJCS[\"Xian_1980_3_Degree_GK_CM_111E\",GEOGCS[\"GCS_Xian_1980\",DATUM[\"D_Xian_1980\",SPHEROID[\"Xian_1980\",6378140.0,298.257]],PRIMEM[\"Greenwich\",0.0],UNIT[\"Degree\",0.0174532925199433]],PROJECTION[\"Gauss_Kruger\"],PARAMETER[\"False_Easting\",500000.0],PARAMETER[\"False_Northing\",0.0],PARAMETER[\"Central_Meridian\",111.0],PARAMETER[\"Scale_Factor\",1.0],PARAMETER[\"Latitude_Of_Origin\",0.0],UNIT[\"Meter\",1.0]]";
//		int wktToEPSG = WKTToEPSG(wkt);
//		WKTToEPSG(wkt);
//		boolean flag = wktToEPSG == epsg;
//		assertTrue(flag);
//	}

//	@Test
//	public void structByWKT39() {
//		String wkt = "83,Xian 1980,6378.14,6356.7552881575,298.257,6378.14,1,0,0,0,0,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,"
//				+ "0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,"
//				+ "0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,"
//				+ "0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0., , , ,0,0,0,0,0,0,0,0,0,0,7,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,,";
//		int g = WKTToEPSG(wkt);
//		GsSpatialReference f = wktToSpatialRef(wkt);
//		System.out.println(f.EPSG());
//	}


	// *******************************************************************************

	/**
	 * EPSG与WKT互转
	 */
	@Test
	public void epsg2wkt1() {
		int epsg = 4326;
		GsSpatialReference gs = new GsSpatialReference(epsg);
		String wkt = gs.ExportToWKT();
		GsSpatialReference gg = new GsSpatialReference(wkt);
		boolean flag = epsg == gg.EPSG();
		gg.delete();
		gs.delete();
		assertTrue(flag);
	}

	/**
	 * EPSG与WKT互转
	 */
	@Test
	public void epsg2wkt2() {
		int epsg = 2362;
		GsSpatialReference gs = new GsSpatialReference(epsg);
		String wkt = gs.ExportToWKT();
		GsSpatialReference gg = new GsSpatialReference(wkt);
		boolean flag = epsg == gg.EPSG();
		gg.delete();
		gs.delete();
		assertTrue(flag);
	}

	/**
	 * WKT与EPSG互转
	 */
	@Test
	public void wkt2epsg1() {
		String wkt = "GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AXIS[\"Latitude\",NORTH],AXIS[\"Longitude\",EAST],AUTHORITY[\"EPSG\",\"4326\"]]";
		GsSpatialReference gs = new GsSpatialReference(wkt);
		int epsg = gs.EPSG();
		GsSpatialReference gg = new GsSpatialReference(epsg);
		boolean flag = wkt.equalsIgnoreCase(gg.ExportToWKT());
		gg.delete();
		gs.delete();
		assertTrue(flag);
	}

	/**
	 * WKT与EPSG互转
	 */
	@Test
	public void wkt2epsg2() {
		String wkt = "PROJCS[\"Xian 1980 / 3-degree Gauss-Kruger zone 38\",GEOGCS[\"Xian 1980\",DATUM[\"Xian_1980\",SPHEROID[\"IAG 1975\",6378140,298.257,AUTHORITY[\"EPSG\",\"7049\"]],AUTHORITY[\"EPSG\",\"6610\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AXIS[\"Latitude\",NORTH],AXIS[\"Longitude\",EAST],AUTHORITY[\"EPSG\",\"4610\"]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",114],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",38500000],PARAMETER[\"false_northing\",0],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],AXIS[\"X\",NORTH],AXIS[\"Y\",EAST],AUTHORITY[\"EPSG\",\"2362\"]]";
		GsSpatialReference gs = new GsSpatialReference(wkt);
		int epsg = gs.EPSG();
		GsSpatialReference gg = new GsSpatialReference(epsg);
		boolean flag = wkt.equalsIgnoreCase(gg.ExportToWKT());
		gg.delete();
		gs.delete();
		assertTrue(flag);
	}

	/**
	 * WKT与EPSG互转
	 */
	@Test
	public void wkt2epsg3() {
		String wkt = "GEOGCS[\"TWD97\",DATUM[\"Taiwan_Datum_1997\",SPHEROID[\"GRS 1980\",6378137,298.257222101,AUTHORITY[\"EPSG\",\"7019\"]],TOWGS84[0,0,0,0,0,0,0],AUTHORITY[\"EPSG\",\"1026\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AXIS[\"Latitude\",NORTH],AXIS[\"Longitude\",EAST],AUTHORITY[\"EPSG\",\"3824\"]]";
		wkt = "83,,6378.2064,6356.583799999,294.9786982,6371.116,0,,0,20,0,500.,0.,117.,30.,0.,100.,100.,100.,25.,45.,100.,120.,105.,123.,25.,45.,1.,0.,0.,30.,1.,0.,0.,0.,1.,0.,0.,0.,1.,1.,0.,0.,0.,1.,0.,0.,0.,1.,0.,0.,0.,1.,1.,0, ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., , , , ,0,0,0,0,0,0,0,0,0,0,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,,";
		int epsg = WKTToEPSG(wkt);
		System.out.println(epsg);
	}

	/**
	 * 从EPSG号构建对象 判断是否是地理坐标
	 */
	@Test
	public void isGeographic1() {
		int epsg = 4326;
		GsSpatialReference gs = new GsSpatialReference(epsg);
		boolean flag = gs.IsGeographic();
		assertTrue(flag);
	}

	/**
	 * 从EPSG号构建对象 判断是否是地理坐标
	 */
	@Test
	public void isGeographic2() {
		int epsg = 3857;
		GsSpatialReference gs = new GsSpatialReference(epsg);
		boolean flag = gs.IsGeographic();
		assertFalse(flag);
	}

	/**
	 * 从wkt构建对象 判断是否是地理坐标
	 */
	@Test
	public void isGeographic3() {
		String wkt = "GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4326\"]]";
		GsSpatialReference gs = new GsSpatialReference(wkt);
		boolean flag = gs.IsGeographic();
		assertTrue(flag);
	}

	/**
	 * 从wkt构建对象 判断是否是地理坐标
	 */
	@Test
	public void isGeographic4() {
		String wkt = "PROJCS[\"WGS 84 / Pseudo-Mercator\",GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4326\"]],PROJECTION[\"Mercator_1SP\"],PARAMETER[\"central_meridian\",0],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",0],PARAMETER[\"false_northing\",0],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],AXIS[\"X\",EAST],AXIS[\"Y\",NORTH],EXTENSION[\"PROJ4\",\"+proj=merc +a=6378137 +b=6378137 +lat_ts=0.0 +lon_0=0.0 +x_0=0.0 +y_0=0 +k=1.0 +units=m +nadgrids=@null +wktext  +no_defs\"],AUTHORITY[\"EPSG\",\"3857\"]]";
		GsSpatialReference gs = new GsSpatialReference(wkt);
		boolean flag = gs.IsGeographic();
		assertFalse(flag);
	}

	/**
	 * 从EPSG构建对象 判断坐标轴顺序
	 */
	@Test
	public void axisInfo1() {
		int epsg = 4326;
		String[] axisInfo = { "Latitude", "NORTH", "Longitude", "EAST" };
		GsSpatialReference gs = new GsSpatialReference(epsg);
		String[] axisParams = new String[4];
		// 输出参数:第一坐标轴
		int[] firstParam = new int[1];
		axisParams[0] = gs.Axis(0, firstParam);
		switch (firstParam[0]) {
		case 0:
			axisParams[1] = "Other";
			break;
		case 1:
			axisParams[1] = "NORTH";
			break;
		case 2:
			axisParams[1] = "SOUTH";
			break;
		case 3:
			axisParams[1] = "EAST";
			break;
		case 4:
			axisParams[1] = "WEST";
			break;
		default:
			axisParams[1] = "UNKNOW";
			break;
		}
		int[] secondParam = new int[1];
		axisParams[2] = gs.Axis(1, secondParam);
		switch (secondParam[0]) {
		case 0:
			axisParams[3] = "Other";
			break;
		case 1:
			axisParams[3] = "NORTH";
			break;
		case 2:
			axisParams[3] = "SOUTH";
			break;
		case 3:
			axisParams[3] = "EAST";
			break;
		case 4:
			axisParams[3] = "WEST";
			break;
		default:
			axisParams[3] = "UNKNOW";
			break;
		}
		boolean flag = Arrays.equals(axisInfo, axisParams);
		assertTrue(flag);
	}

	/**
	 * 从WKT构建对象 判断坐标轴顺序
	 */
	@Test
	public void axisInfo2() {
		String wkt = "GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4326\"]]";
		String[] axisInfo = { "LATITUDE", "NORTH", "LONGITUDE", "EAST" };
		GsSpatialReference gs = new GsSpatialReference(wkt);
		String[] axisParams = new String[4];
		// 输出参数:第一坐标轴
		int[] firstParam = new int[1];
		axisParams[0] = gs.Axis(0, firstParam);
		switch (firstParam[0]) {
		case 0:
			axisParams[1] = "Other";
			break;
		case 1:
			axisParams[1] = "NORTH";
			break;
		case 2:
			axisParams[1] = "SOUTH";
			break;
		case 3:
			axisParams[1] = "EAST";
			break;
		case 4:
			axisParams[1] = "WEST";
			break;
		default:
			axisParams[1] = "UNKNOW";
			break;
		}
		int[] secondParam = new int[1];
		axisParams[2] = gs.Axis(1, secondParam);
		switch (secondParam[0]) {
		case 0:
			axisParams[3] = "Other";
			break;
		case 1:
			axisParams[3] = "NORTH";
			break;
		case 2:
			axisParams[3] = "SOUTH";
			break;
		case 3:
			axisParams[3] = "EAST";
			break;
		case 4:
			axisParams[3] = "WEST";
			break;
		default:
			axisParams[3] = "UNKNOW";
			break;
		}
		boolean flag = Arrays.equals(axisInfo, axisParams);
		assertTrue(flag);
	}

	/**
	 * 从EPSG构建对象 判断坐标轴顺序
	 */
	@Test
	public void axisInfo3() {
		int epsg = 4490;
		String[] axisInfo = { "Latitude", "NORTH", "Longitude", "EAST" };
		GsSpatialReference gs = new GsSpatialReference(epsg);
		String[] axisParams = new String[4];
		// 输出参数:第一坐标轴
		int[] firstParam = new int[1];
		axisParams[0] = gs.Axis(0, firstParam);
		switch (firstParam[0]) {
		case 0:
			axisParams[1] = "Other";
			break;
		case 1:
			axisParams[1] = "NORTH";
			break;
		case 2:
			axisParams[1] = "SOUTH";
			break;
		case 3:
			axisParams[1] = "EAST";
			break;
		case 4:
			axisParams[1] = "WEST";
			break;
		default:
			axisParams[1] = "UNKNOW";
			break;
		}
		int[] secondParam = new int[1];
		axisParams[2] = gs.Axis(1, secondParam);
		switch (secondParam[0]) {
		case 0:
			axisParams[3] = "Other";
			break;
		case 1:
			axisParams[3] = "NORTH";
			break;
		case 2:
			axisParams[3] = "SOUTH";
			break;
		case 3:
			axisParams[3] = "EAST";
			break;
		case 4:
			axisParams[3] = "WEST";
			break;
		default:
			axisParams[3] = "UNKNOW";
			break;
		}
		boolean flag = Arrays.equals(axisInfo, axisParams);
		assertTrue(flag);
	}

	/**
	 * 从EPSG构建对象 判断坐标轴顺序
	 */
	@Test
	public void axisInfo4() {
		int epsg = 3857;
		String[] axisInfo = { "X", "EAST", "Y", "NORTH" };
		GsSpatialReference gs = new GsSpatialReference(epsg);
		String[] axisParams = new String[4];
		// 输出参数:第一坐标轴
		int[] firstParam = new int[1];
		axisParams[0] = gs.Axis(0, firstParam);
		switch (firstParam[0]) {
		case 0:
			axisParams[1] = "Other";
			break;
		case 1:
			axisParams[1] = "NORTH";
			break;
		case 2:
			axisParams[1] = "SOUTH";
			break;
		case 3:
			axisParams[1] = "EAST";
			break;
		case 4:
			axisParams[1] = "WEST";
			break;
		default:
			axisParams[1] = "UNKNOW";
			break;
		}
		int[] secondParam = new int[1];
		axisParams[2] = gs.Axis(1, secondParam);
		switch (secondParam[0]) {
		case 0:
			axisParams[3] = "Other";
			break;
		case 1:
			axisParams[3] = "NORTH";
			break;
		case 2:
			axisParams[3] = "SOUTH";
			break;
		case 3:
			axisParams[3] = "EAST";
			break;
		case 4:
			axisParams[3] = "WEST";
			break;
		default:
			axisParams[3] = "UNKNOW";
			break;
		}
		boolean flag = Arrays.equals(axisInfo, axisParams);
		assertTrue(flag);
	}

	/**
	 * 从EPSG构建对象 判断坐标轴顺序
	 */
	@Test
	public void axisInfo5() {
		int epsg = 2362;
		String[] axisInfo = { "X", "NORTH", "Y", "EAST" };
		GsSpatialReference gs = new GsSpatialReference(epsg);
		String[] axisParams = new String[4];
		// 输出参数:第一坐标轴
		int[] firstParam = new int[1];
		axisParams[0] = gs.Axis(0, firstParam);
		switch (firstParam[0]) {
		case 0:
			axisParams[1] = "Other";
			break;
		case 1:
			axisParams[1] = "NORTH";
			break;
		case 2:
			axisParams[1] = "SOUTH";
			break;
		case 3:
			axisParams[1] = "EAST";
			break;
		case 4:
			axisParams[1] = "WEST";
			break;
		default:
			axisParams[1] = "UNKNOW";
			break;
		}
		int[] secondParam = new int[1];
		axisParams[2] = gs.Axis(1, secondParam);
		switch (secondParam[0]) {
		case 0:
			axisParams[3] = "Other";
			break;
		case 1:
			axisParams[3] = "NORTH";
			break;
		case 2:
			axisParams[3] = "SOUTH";
			break;
		case 3:
			axisParams[3] = "EAST";
			break;
		case 4:
			axisParams[3] = "WEST";
			break;
		default:
			axisParams[3] = "UNKNOW";
			break;
		}
		boolean flag = Arrays.equals(axisInfo, axisParams);
		assertTrue(flag);
	}

	/**
	 * 仿射变换测试
	 */
	@Test
	public void test() {

		double[] srcPointsX = { 352645.245, 353800.402, 351600.519, 345800.101 };
		double[] srcPointsY = { 1225950.438, 1230000.378, 1225959.506,
				1225959.8 };
		double[] targetPointsX = { 3924063.3, 3944871.4, 3904193.8, 3870898.1 };
		double[] targetPointsY = { 21499758.9, 21500009.5, 21499987.5,
				21499994.9 };

		double[] param = coordinateToAffine(srcPointsX, srcPointsY,
				srcPointsX.length, targetPointsX, targetPointsY,
				targetPointsY.length);

		double t = 0.01;
		assertEquals(-4131626.61523, param[0], t);
		assertEquals(7.08764, param[1], t);
		assertEquals(4.52754, param[2], t);
		assertEquals(21452272.34961, param[3], t);
		assertEquals(-0.02324, param[4], t);
		assertEquals(0.04550, param[5], t);

		double[] xArray = { 352645.245, 353800.402 };
		double[] yArray = { 1225950.438, 1230000.378 };
		int len = yArray.length;
		double[] param2 = new double[6];
		param2[0] = -4131626.61523;
		param2[1] = 7.08764;
		param2[2] = 4.52754;
		param2[3] = 21452272.34961;
		param2[4] = -0.02324;
		param2[5] = 0.04550;
		coordinateTransformByAffine(param2, xArray, yArray, len);

		double exp_x[] = { 3918335.575, 3944859.177 };
		double exp_y[] = { 21499857.619, 21500015.045 };
		// 验证
		double tol = 0.001;
		assertEquals(exp_x[0], xArray[0], tol);
		assertEquals(exp_x[1], xArray[1], tol);
		assertEquals(exp_y[0], yArray[0], tol);
		assertEquals(exp_y[1], yArray[1], tol);

		GsSpatialReference sr = new GsSpatialReference(4490);
		System.out.println(sr.ExportToWKT());
		String wkt = "GEOGCS[\"China Geodetic Coordinate System 2000\",DATUM[\"China_2000\",SPHEROID[\"CGCS2000\",6378137,298.257222101,AUTHORITY[\"EPSG\",\"1024\"]],AUTHORITY[\"EPSG\",\"1043\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AXIS[\"Latitude\",NORTH],AXIS[\"Longitude\",EAST],AUTHORITY[\"EPSG\",\"4490\"]]";
		boolean flag = wkt.equalsIgnoreCase(sr.ExportToWKT());
		sr.delete();
		assertTrue(flag);

//		int epsg = 4490;
//		String wkt2 = "GEOGCS[\"CGCS2000\",DATUM[\"CGCS2000\",SPHEROID[\"CGCS2000\",6378137,298.257222101]],PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433]]";
//		GsSpatialReference sr2 = new GsSpatialReference(wkt2);
//		System.out.println(sr2.EPSG());
//		boolean flag2 = sr2.EPSG() == epsg;
//		sr2.delete();
//		assertTrue(flag2);

		String wkt3 = "GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4326\"]]";
		GsSpatialReference sr3 = new GsSpatialReference(wkt3);
		System.out.println(sr3.EPSG());

		String wkt4 = "PROJCS[\"Xian 1980 / 3-degree Gauss-Kruger zone 37\",GEOGCS[\"Xian 1980\",DATUM[\"Xian 1980\",SPHEROID[\"Xian 1980\",6378140,298.257]],PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",111],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",37500000],PARAMETER[\"false_northing\",0],UNIT[\"Meter\",1]]";
		GsSpatialReference sr4 = new GsSpatialReference(wkt4);
		System.out.println(sr4.EPSG());

		int epsg88 = 904490;
		String wkt8 = "93,CGCS_2000,6378.137,6356.7523141404,298.257222101,6378.137,18,Mercator (1SP) (Web墨卡托投影),0,0,0,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,"
				+ "0.,0.,1.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0, ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0.,"
				+ " ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,"
				+ "0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,"
				+ "0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,"
				+ "0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., ,0.,0.,0.,0., , , , ,0,0,0,0,0,0,0,0,0,0,7.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,,";
		GsSpatialReference pa = wktToSpatialRef(wkt8);
		System.out.println(pa.EPSG());
		System.out.println(WKTToEPSG(wkt8));
		GsSpatialReference sr5 = new GsSpatialReference(wkt8,
				GsSpatialReferenceFormat.eUnknownFormat);
		System.out.println(sr5.EPSG());
		boolean f = pa.EPSG() == epsg88;
		System.out.println(f);

		GsSpatialReference sr6 = new GsSpatialReference(904490);
		String w = sr6.ExportToWKT();
		GsSpatialReference sr7 = new GsSpatialReference(w,
				GsSpatialReferenceFormat.eUnknownFormat);
		System.out.println(sr7.EPSG());
		boolean dd = sr7.EPSG() == 904490;
		assertTrue(dd);

		String wkt17 = "PROJCS[\"Mercator (1SP) (Web墨卡托投影)\",GEOGCS[\"CGCS 2000\",DATUM[\"D_CGCS_2000\",SPHEROID[\"CGCS_2000\",6378137,298.2572221010],TOWGS84[0,0,0,0,0,0,0]],PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433]],PROJECTION[\"Mercator_1SP\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",0],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",0],PARAMETER[\"false_northing\",0],UNIT[\"Meter\",1],EXTENSION[\"PROJ4\",\"+proj=merc +a=6378137 +b=6378137 +lat_ts=0.0 +lon_0=0.0 +x_0=0.0 +y_0=0 +k=1.0 +units=m +nadgrids=@null +wktext +no_defs\"]]";
		GsSpatialReference sr8 = new GsSpatialReference(wkt17,
				GsSpatialReferenceFormat.eUnknownFormat);
		System.out.println(sr8.EPSG());

		double[] xArray2 = { 73.44695999999999, 135.08583 };
		double[] yArray2 = { 6.3186412, 53.557925999999995 };
		int len2 = yArray2.length;
		boolean flag3 = coordinateTransformByEPSG(4326, 4490, xArray2, yArray2,
				len2);
		assertTrue(flag3);
		System.out.println(xArray2[0]);
		System.out.println(xArray2[1]);
		System.out.println(yArray2[0]);
		System.out.println(yArray2[1]);

	}

	/**
	 * 布尔莎7参数转换
	 */
	@Test
	public void bursatest() {

		// double[] vec = new double[]{121.539025 + 0 * 0.0001,22.028944 + 0 *
		// 0.0001,0 * 100,
		// 121.539025 + 1 * 0.0001,22.028944 + 0 * 0.0001,1 * 100,
		// 121.539025 + 0 * 0.0001,22.028944 + 1 * 0.0001,2 * 100};
		double[] xArray = { 121.539025 + 0 * 0.0001, 121.539025 + 1 * 0.0001,
				121.539025 + 0 * 0.0001 };
		double[] yArray = { 22.028944 + 0 * 0.0001, 22.028944 + 0 * 0.0001,
				22.028944 + 1 * 0.0001, };
		double[] zArray = { 0 * 100, 1 * 100, 2 * 100 };
		int srcEPSG = 3819;
		int targetEPSG = 4291;
		int len = 3;
		double bursaParams[] = { 652.480233, 120.690084, 556.350031,
				-4.11496226, 2.93826279, -0.85302873, -3.40799500 };

		boolean f = coordinateTransformByParamEPSG(srcEPSG, targetEPSG,
				bursaParams, xArray, yArray, zArray, len);
		assertTrue(f);
		double[] exp_xArray = { 121.5337829935254, 121.533883092017,
				121.5337831800743 };
		double[] exp_yArray = { 22.03538179447719, 22.0353817030173,
				22.03548161041127 };
		double[] exp_zArray = { -787.2810279736295, -687.2823722232133,
				-587.2805732572451 };

		double tol = 0.01;
		for (int i = 0; i < len; i++) {
			assertEquals(exp_xArray[i], xArray[i], tol);
			assertEquals(exp_yArray[i], yArray[i], tol);
			assertEquals(exp_zArray[i], zArray[i], tol);
		}
	}
	public void TrajectoryTransformationTest() {
		double l = 112.2;
		double b = 39.1;
		double azimuth = 283.15;
		double xori = 112.2;
		double yori = 39.1;
		double zori = 1510;
		double[] xArray = {0.9};
		double[] yArray = {0};
		double[] zArray = {-4010};
		
		double exp_x = 112.1894515461139;
		double exp_y = 39.06483630341012;
		double exp_z = 1511.2634319216013;
		int len = yArray.length;
		//TrajectoryTransformation trajectoryTrans = new TrajectoryTransformation(l,b,azimuth,xori,yori,zori);
		//trajectoryTrans.Transformation(xArray, yArray, zArray, len, 1);
		//trajectoryTrans.delete();
		
		//验证
		double tol = 0.001;
		//assertEquals(exp_x,xArray[0],tol);
		///assertEquals(exp_y,yArray[0],tol);
		//assertEquals(exp_z,zArray[0],tol);
	}
}
