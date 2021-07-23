package unittest;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.geostar.kernel.*;
//import com.geostar.kernel.extensions.*;

public class spatialreference {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.loadLibrary("gsjavaport");
		//System.loadLibrary("ggsextensions");
		GsKernel.Initialize();
		System.out.println("loadlibrary");
		String strCurDir = System.getProperty("user.dir");
		strCurDir += "/../data/coordinatesystem/EPSG.txt";
		System.out.println(strCurDir);
		GsGlobeConfig.Instance().Child("Kernel/spatialreference/EPSG")
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
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * 比较错误定义的空间参考或自定义的空间参考
	 */
	@Test
	public void MathErrorSpatialReference() {

		GsSpatialReferenceManager Mgr = new GsSpatialReferenceManager();
		GsSpatialReferenceManager.GsSpatialReferenceCategory pCategory = Mgr
				.SpecialCategory(GsSpatialReferenceManager.GsSpecialCategory.eStandardEPSG);

		String str = "PROJCS[\"Gauss-Kruger (高斯－克里格投影)\",GEOGCS[\"Xian 1980\",DATUM[\"Xian 1980\",SPHEROID[\"Xian 1980\",6378140,298.257]],PRIMEM[\"Greenwich\",0],UNIT [\"degree\",0.0174532925199433]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",114],PARAMETER [\"scale_factor\",1],PARAMETER[\"false_easting\",38500000],PARAMETER[\"false_northing\",0]]";
		GsSpatialReference ptrSR = new GsSpatialReference(str);
		java.util.ArrayList<Integer> vecEPSG = MatchEPSG(pCategory, ptrSR);
		assertTrue(ExistEPSG(vecEPSG, 2362));

		str = "PROJCS[\"Xian 1980 / 3-degree Gauss-Kruger zone 37\",GEOGCS[\"CGCS 2000\",DATUM[\"CGCS 2000\",SPHEROID[\"CGCS 2000\",6378137,298.257222101]],PRIMEM [\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER [\"central_meridian\",111],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",37500000],PARAMETER[\"false_northing\",0],UNIT[\"Meter\",1]]";
		ptrSR = new GsSpatialReference(str);
		vecEPSG = MatchEPSG(pCategory, ptrSR);
		assertTrue(ExistEPSG(vecEPSG, 4525));
	}

	java.util.ArrayList<Integer> MatchEPSG(
			GsSpatialReferenceManager.GsSpatialReferenceCategory pCategory,
			GsSpatialReference sr) {
		java.util.ArrayList<Integer> vEPSG = new java.util.ArrayList<Integer>();

		for (int i = 0; i < pCategory.Count(); i++) {
			GsSpatialReference ptrSR = pCategory.SpatialReference(i);
			if (ptrSR.IsSameParameter(sr))
				vEPSG.add(ptrSR.EPSG());
		}
		return vEPSG;
	}

	boolean ExistEPSG(java.util.ArrayList<Integer> vecEPSG, int n) {
		for (int i = 0; i < vecEPSG.size(); i++) {
			if (vecEPSG.get(i) == n)
				return true;
		}
		return false;
	}

	/**
	 * 高斯坐标数据计算椭球面积
	 */
	@Test
	public void GaoSiSphereArea() {

		GsSpatialReference geo80GS = new GsSpatialReference(
				"PROJCS[\"Gauss-Kruger (高斯－克里格投影)\",GEOGCS[\"Xian 1980\",DATUM[\"Xian 1980\",SPHEROID[\"Xian 1980\",6378140,298.257],TOWGS84[0,0,0,0,0,0,0]],PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",114],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",38500000],PARAMETER[\"false_northing\",0],UNIT[\"Meter\",1]]");

		double coord[] = { 38608255.6247699, 3307348.94667884,
				38608253.2194889, 3307349.32415289, 38608244.5578685,
				3307349.7170546, 38608240.2271178, 3307350.70086463,
				38608237.9960993, 3307351.81633835, 38608237.2741269,
				3307352.80049711, 38608237.6673304, 3307358.37807352,
				38608239.1088673, 3307379.90112885, 38608241.5494969,
				3307398.0180718, 38608244.0063197, 3307414.32352978,
				38608244.13549741, 3307429.16785755, 38608241.2181301,
				3307428.55546942, 38608236.5349302, 3307428.89567127,
				38608234.3241017, 3307419.94784479, 38608232.5459111,
				3307414.61211192, 38608232.2226987, 3307413.64205089,
				38608228.5722647, 3307410.32290694, 38608219.6119152,
				3307406.00774072, 38608211.31507301, 3307403.68387161,
				38608193.9469091, 3307401.115658, 38608192.0885227,
				3307400.05347864, 38608191.1151383, 3307398.54889317,
				38608190.5847055, 3307391.99985587, 38608187.22267591,
				3307379.34369975, 38608185.1881364, 3307368.19247479,
				38608185.0114567, 3307364.20990453, 38608187.1359253,
				3307359.51954242, 38608190.38661, 3307351.75314789,
				38608192.3587275, 3307347.04134695, 38608197.22690141,
				3307338.5455379, 38608204.7900854, 3307318.59654243,
				38608207.4167353, 3307307.56488885, 38608207.0662687,
				3307296.35788651, 38608205.4904942, 3307278.84731151,
				38608201.2879119, 3307266.58986648, 38608194.2837416,
				3307257.13414126, 38608192.7077044, 3307252.40630113,
				38608193.7351085, 3307248.29609196, 38608198.6676948,
				3307245.39327999, 38608201.1697275, 3307242.89154717,
				38608202.8379101, 3307240.16227835, 38608205.18890841,
				3307232.96010949, 38608208.29831921, 3307222.42210301,
				38608211.4072883, 3307214.08267788, 38608213.9855279,
				3307209.38254957, 38608224.67649701, 3307195.88827388,
				38608233.6701059, 3307183.44793815, 38608239.5603192,
				3307175.79829721, 38608242.2318522, 3307173.67330403,
				38608244.4176947, 3307173.37007861, 38608245.874902,
				3307173.61311617, 38608247.8785253, 3307175.25247482,
				38608248.8497174, 3307177.01326733, 38608249.8812663,
				3307184.36014444, 38608251.0346746, 3307186.66753672,
				38608255.5273239, 3307192.49671085, 38608257.894946,
				3307195.471853, 38608266.1518651, 3307199.90491891,
				38608268.4588597, 3307200.63365398, 38608271.5554557,
				3307200.33055146, 38608274.7129379, 3307199.96650964,
				38608285.7027251, 3307197.9637327, 38608297.2665069,
				3307196.33188406, 38608301.8372647, 3307195.60288918,
				38608304.8521331, 3307194.43630634, 38608307.2024969,
				3307191.16233625, 38608307.721292, 3307189.0876618,
				38608307.7365078, 3307188.40905365, 38608307.7865468,
				3307186.17026449, 38608307.4623442, 3307184.41965384,
				38608306.4252627, 3307182.66907915, 38608300.4612944,
				3307175.08330114, 38608299.5484237, 3307172.92082894,
				38608299.2296856, 3307172.16567899, 38608298.9060275,
				3307169.30835265, 38608298.9639372, 3307166.70797671,
				38608298.97094461, 3307166.39549768, 38608299.684267,
				3307163.08904857, 38608302.0187315, 3307157.9674674,
				38608302.9269211, 3307154.20734618, 38608302.92713,
				3307149.99333506, 38608302.2577104, 3307144.26627123,
				38608298.9815401, 3307133.83811189, 38608300.4224945,
				3307133.3401532, 38608306.4515127, 3307130.63810863,
				38608313.8315284, 3307129.59934355, 38608320.4836622,
				3307130.53531784, 38608322.7825672, 3307128.07143377,
				38608334.2923433, 3307115.73564536, 38608361.7527333,
				3307093.15732314, 38608369.9803299, 3307072.95369537,
				38608383.5437163, 3307081.50006198, 38608395.9651462,
				3307089.69459504, 38608396.2932917, 3307089.91111384,
				38608404.7721424, 3307097.59643023, 38608409.3139293,
				3307101.71306825, 38608414.7392525, 3307105.10469025,
				38608417.7975102, 3307106.14934361, 38608418.1002839,
				3307106.25274888, 38608420.3004939, 3307107.00408888,
				38608422.1651464, 3307106.94212544, 38608424.3697329,
				3307106.86873415, 38608429.7954798, 3307104.56333229,
				38608432.1717229, 3307104.56357308, 38608432.9153312,
				3307104.56373497, 38608435.49253161, 3307105.37774082,
				38608435.7575077, 3307105.81949047, 38608436.7131162,
				3307107.41251016, 38608438.2043134, 3307115.68668152,
				38608439.9209972, 3307122.31061993, 38608441.05189151,
				3307126.67393062, 38608447.5612976, 3307143.76527197,
				38608448.2390644, 3307147.69907726, 38608448.2385118,
				3307152.85328034, 38608446.8817072, 3307157.73646817,
				38608444.032915, 3307161.94108083, 38608430.1957446,
				3307181.06534199, 38608427.3859742, 3307183.61923609,
				38608424.227104, 3307186.49047636, 38608419.3436603,
				3307189.33845619, 38608413.6466895, 3307189.60926758,
				38608399.5402694, 3307184.72486469, 38608378.7882603,
				3307173.60053424, 38608357.6291059, 3307162.74734588,
				38608347.2866929, 3307158.06669702, 38608344.4385392,
				3307157.76129833, 38608340.5725187, 3307158.77831978,
				38608336.9100964, 3307160.5073507, 38608335.7909066,
				3307162.84714005, 38608335.482882, 3307164.78849513,
				38608334.46748421, 3307171.18886639, 38608334.33966859,
				3307172.31684906, 38608332.6302738, 3307187.36355096,
				38608332.6234103, 3307187.40350901, 38608328.8696591,
				3307200.28346613, 38608326.7325375, 3307210.35495176,
				38608326.4266743, 3307217.06913595, 38608326.6931461,
				3307221.22221886, 38608326.7822908, 3307222.61330907,
				38608327.69774149, 3307225.05509486, 38608330.0626937,
				3307227.19148192, 38608333.51263, 3307228.61257215,
				38608333.9536752, 3307228.79426781, 38608339.3709159,
				3307229.17605243, 38608344.101336, 3307230.24475454,
				38608345.9325092, 3307231.00789311, 38608346.466544,
				3307232.30509045, 38608346.4658696, 3307237.41704831,
				38608345.6253229, 3307253.66853048, 38608344.9565289,
				3307276.33428975, 38608343.71630801, 3307294.24550526,
				38608339.2893102, 3307307.23531048, 38608339.289665,
				3307307.69432332, 38608332.6267787, 3307307.69365285,
				38608328.983495, 3307307.69304934, 38608319.8119415,
				3307308.53449823, 38608313.6816652, 3307310.19534569,
				38608310.452861, 3307312.09015201, 38608305.8489807,
				3307317.46024438, 38608301.1866581, 3307322.89873105,
				38608295.2629067, 3307328.82794817, 38608293.4762588,
				3307329.93133354, 38608291.16387051, 3307330.56171629,
				38608287.8875553, 3307330.61115874, 38608287.6957348,
				3307330.61405643, 38608284.06969731, 3307329.66767844,
				38608277.2825225, 3307326.16283603, 38608274.4009125,
				3307324.67468042, 38608268.0953455, 3307321.52112202,
				38608266.6239275, 3307321.31065037, 38608265.2049226,
				3307321.36334421, 38608264.4166621, 3307321.94106509,
				38608264.1013374, 3307322.7295054, 38608264.2445338,
				3307337.38249323, 38608264.0468629, 3307345.32231104,
				38608263.4563183, 3307346.70034672, 38608261.66911291,
				3307347.7723552, 38608261.1594547, 3307348.07805067,
				38608255.6247699, 3307348.94667884 };

		// 80的地理坐标系
		GsSpatialReference geo80 = new GsSpatialReference(4610);
		GsCoordinateTransformation trans = new GsProjectCoordinateTransformation(
				geo80GS, geo80);
		trans.Transformation(coord, coord.length / 2, 2);

		GsGeodesicPolygon polygon = new GsGeodesicPolygon(geo80);

		for (int i = 0; i < coord.length / 2; i++)
			polygon.AddPoint(coord[i * 2], coord[i * 2 + 1]);
		double area[] = { 0 };
		double len[] = { 0 };
		long n = polygon.Compute(false, true, area, len);

		assertEquals(area[0], 32415.447685294173, 0.001);
	}

	/**
	 * 通过EPSG号实现坐标转换
	 */
	@Test
	public void Bursa7Parameter3819To4291() {
		// <3819> +proj=longlat +ellps=bessel
		// +towgs84=595.48,121.69,515.35,4.115,-2.9383,0.853,-3.408 +no_defs <>
		GsSpatialReference sr = new GsSpatialReference(3819);
		// <4291> +proj=longlat +ellps=GRS67 +towgs84=-57,1,-41,0,0,0,0 +no_defs
		// <>
		GsSpatialReference tar = new GsSpatialReference(4291);

		double[] vec = new double[] { 121.539025 + 0 * 0.0001,
				22.028944 + 0 * 0.0001, 0 * 100, 121.539025 + 1 * 0.0001,
				22.028944 + 0 * 0.0001, 1 * 100, 121.539025 + 0 * 0.0001,
				22.028944 + 1 * 0.0001, 2 * 100 };

		/*
		 * 笑脸4.2工具计算出来的7参数 //同名点，三个点关系如下：
		 * 
		 * |--* 具体坐标如下
		 * P1,22.028944,121.539025,0,22.03538179447719,121.5337829935254
		 * ,-787.2810279736295
		 * P2,22.028944,121.539125,100,22.0353817030173,121.533883092017
		 * ,-687.2823722232133
		 * P3,22.029044,121.539025,200,22.03548161041127,121.5337831800743
		 * ,-587.2805732572451
		 * 
		 * DX(米)=652.480233 DY(米)=120.690084 DZ(米)=556.350031 WX(秒)=-4.11496226
		 * WY(秒)=2.93826279 WZ(秒)=-0.85302873 K(ppm)=-3.40799500
		 */

		double param[] = { 652.480233, 120.690084, 556.350031, -4.11496226,
				2.93826279, -0.85302873, -3.40799500 };
		// 截取3位小数，因为原始的空间参考中7参数定义只定义到3位小数，这里如果不截取一定的小数位，则计算会因为结果精度过高而和实际比较值存在差距。
		for (int i = 0; i < 7; i++) {
			param[i] = (int) (param[i] * 1000);
			param[i] /= 1000;
		}

		GsCoordinateTransformation ptrTrans = new GsParameterProjectCoordinateTransformation(
				sr, tar, GsCoordinateTransformationMethod.eCoordinateFrame,
				param);

		assertTrue(ptrTrans.Transformation(vec, 3, 3));

		double[] vecComp = new double[] { 121.5337829935254, 22.03538179447719,
				-787.2810279736295, 121.533883092017, 22.0353817030173,
				-687.2823722232133, 121.5337831800743, 22.03548161041127,
				-587.2805732572451 };

		double tol = 0.01;
		for (int i = 0; i < 6; i++)
			assertEquals(vecComp[i], vec[i], tol);

	}

	/**
	 * 通过EPSG号实现坐标转换
	 */
	@Test
	public void EPSG4490Compare() {
		String strWKT = "GEOGCS[\"CGCS2000\",DATUM[\"CGCS2000\",SPHEROID[\"CGCS2000\",6378137,298.257222101]],PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433]]";
		GsSpatialReference ptrSR = new GsSpatialReference(strWKT);
		assertEquals(ptrSR.EPSG(),4490);
		assertEquals(ptrSR.EPSG(),4490);
		strWKT = "PROJCS[\"Mercator (1SP) (Web墨卡托投影)\",GEOGCS[\"CGCS 2000\",DATUM[\"D_CGCS_2000\",SPHEROID[\"CGCS_2000\",6378137,298.2572221010],TOWGS84[0,0,0,0,0,0,0]],PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433]],PROJECTION[\"Mercator_1SP\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",0],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",0],PARAMETER[\"false_northing\",0],UNIT[\"Meter\",1],EXTENSION[\"PROJ4\",\"+proj=merc +a=6378137 +b=6378137 +lat_ts=0.0 +lon_0=0.0 +x_0=0.0 +y_0=0 +k=1.0 +units=m +nadgrids=@null +wktext +no_defs\"]]";
		ptrSR = new GsSpatialReference(strWKT);
		assertEquals(ptrSR.EPSG(),904490);
		assertEquals(ptrSR.EPSG(),904490);
	}

	/**
	 * 通过EPSG号实现坐标转换
	 */
	@Test
	public void transByEPSG() {
		int srcEPSG = 4326;
		int targetEPSG = 2401;
		double[] xArray = { 117.67065244, 117.87359439 };
		double[] yArray = { 41.285622, 41.2541047 };
		double[] zArray = { 658.4854, 656.8332 };

		double exp_x = 29089065.449;
		double exp_y = 5551419.037;
		double exp_z = 711.878;
		int len = yArray.length;
		GsSpatialReference sr = new GsSpatialReference(srcEPSG);
		GsSpatialReference tar = new GsSpatialReference(targetEPSG);
		GsProjectCoordinateTransformation gsProjectTrans = new GsProjectCoordinateTransformation(
				sr, tar);
		gsProjectTrans.Transformation(xArray, yArray, zArray, len, 1);

		double tol = 0.001;
		assertEquals(exp_x, xArray[0], tol);
		assertEquals(exp_y, yArray[0], tol);
		assertEquals(exp_z, zArray[0], tol);

		// 销毁对象
		gsProjectTrans.delete();
		tar.delete();
		sr.delete();
	}

	/**
	 * 通过EPSG号实现坐标转换
	 */
	@Test
	public void transByEPSG2() {
		System.out.println("transByEPSG2");
		int srcEPSG = 4326;
		int targetEPSG = 2401;
		double[] xArray = { 117.67065244, 117.87359439 };
		double[] yArray = { 41.285622, 41.2541047 };

		double exp_x = 29089065.45;
		double exp_y = 5551419.03;

		int len = yArray.length;
		GsSpatialReference sr = new GsSpatialReference(srcEPSG);
		GsSpatialReference tar = new GsSpatialReference(targetEPSG);
		GsProjectCoordinateTransformation gsProjectTrans = new GsProjectCoordinateTransformation(
				sr, tar);
		assertTrue(gsProjectTrans.Transformation(xArray, yArray, len, 1));

		double tol = 0.01;
		assertEquals(exp_x, xArray[0], tol);
		assertEquals(exp_y, yArray[0], tol);

		// 销毁对象
		gsProjectTrans.delete();
		tar.delete();
		sr.delete();

	}

	/**
	 * 通过EPSG号实现坐标转换
	 */
	@Test
	public void transByEPSG3() {
		int srcEPSG = 4490;
		int targetEPSG = 2361;
		double[] xArray = { 67.991 };
		double[] yArray = { 12.829 };

		double exp_x = 32381653.65;
		double exp_y = 1916976.91;

		int len = yArray.length;
		GsSpatialReference sr = new GsSpatialReference(srcEPSG);
		GsSpatialReference tar = new GsSpatialReference(targetEPSG);
		GsProjectCoordinateTransformation gsProjectTrans = new GsProjectCoordinateTransformation(
				sr, tar);
		gsProjectTrans.Transformation(xArray, yArray, len, 1);

		double tol = 0.1;
		assertEquals(exp_x, xArray[0], tol);
		assertEquals(exp_y, yArray[0], tol);

		// 销毁对象
		gsProjectTrans.delete();
		tar.delete();
		sr.delete();

	}

	/**
	 * 通过WKT实现坐标转换
	 */
	@Test
	public void transByWKT() {
		System.out.println("transByWKT");
		String srcWKT = "GEOGCS[\"GCS_WGS_1984\",DATUM[\"D_WGS_1984\",SPHEROID[\"WGS_1984\",6378137,298.257223563]],PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433]]";
		String targetWKT = "PROJCS[\"Gauss_Kruger\",GEOGCS[\"Xian 1980\",DATUM[\"Chinese National (Xian1980)\",SPHEROID[\"IUGG 1975\",6378140,298.257],TOWGS84[8.4322,-17.372,-17.1256,-5.266e-006,9.73e-006,-1.4748e-005,3.5816e-006]],PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433]],UNIT[\"meters\",1],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",117],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",500000],PARAMETER[\"false_northing\",0]]";
		// targetWKT = 2345
		double[] xArray = { 117.67065244 };
		double[] yArray = { 41.285622 };

		double exp_x = 556181.37;
		double exp_y = 4572512.61;

		int len = yArray.length;
		GsSpatialReference sr = new GsSpatialReference(srcWKT);
		GsSpatialReference tar = new GsSpatialReference(targetWKT);
		GsProjectCoordinateTransformation gsProjectTrans = new GsProjectCoordinateTransformation(
				sr, tar);
		assertTrue(gsProjectTrans.Transformation(xArray, yArray, len, 1));

		double tol = 5;
		assertEquals(exp_x, xArray[0], tol);
		assertEquals(exp_y, yArray[0], tol);

		// Destructor
		gsProjectTrans.delete();
		tar.delete();
		sr.delete();
	}

	/**
	 * 通过WKT实现坐标转换
	 */
	@Test
	public void transByWKT2() {
		System.out.println("transByWKT2");
		String srcWKT = "GEOGCS[\"GCS_WGS_1984\",DATUM[\"D_WGS_1984\",SPHEROID[\"WGS_1984\",6378137,298.257223563]],PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433]]";
		String targetWKT = "PROJCS[\"Gauss_Kruger\",GEOGCS[\"Xian 1980\",DATUM[\"Chinese National (Xian1980)\",SPHEROID[\"IUGG 1975\",6378140,298.257],TOWGS84[8.4322,-17.372,-17.1256,-5.266e-006,9.73e-006,-1.4748e-005,3.5816e-006]],PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433]],UNIT[\"meters\",1],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",117],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",500000],PARAMETER[\"false_northing\",0]]";
		double[] xArray = { 117.67065244 };
		double[] yArray = { 41.285622 };
		double[] zArray = { 658.4854 };
		double exp_x = 556181.37;
		double exp_y = 4572512.61;
		double exp_z = 658.4854;
		int len = yArray.length;
		GsSpatialReference sr = new GsSpatialReference(srcWKT);
		GsSpatialReference tar = new GsSpatialReference(targetWKT);
		GsProjectCoordinateTransformation gsProjectTrans = new GsProjectCoordinateTransformation(
				sr, tar);
		assertTrue(gsProjectTrans
				.Transformation(xArray, yArray, zArray, len, 1));

		double tol = 30;
		assertEquals(exp_x, xArray[0], tol);
		assertEquals(exp_y, yArray[0], tol);
		assertEquals(exp_z, zArray[0], tol);

		// Destructor
		gsProjectTrans.delete();
		tar.delete();
		sr.delete();
	}

	/**
	 * 通过WKT实现坐标转换
	 */
	@Test
	public void transByWKT3() {
		System.out.println("transByWKT3");
		String srcWKT = "GEOGCS[\"CGCS2000\",DATUM[\"CGCS2000\",SPHEROID[\"CGCS2000\",6378137,298.257222101],TOWGS84[0,0,0,0,0,0,0]],PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433]]";
		String targetWKT = "PROJCS[\"CGCS 2000 / 3-degree Gauss-Kruger zone 38\",GEOGCS[\"CGCS 2000\",DATUM[\"CGCS 2000\",SPHEROID[\"CGCS 2000\",6378137,298.257222101]],PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",114],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",500000],PARAMETER[\"false_northing\",0],UNIT[\"Meter\",1]]";
		double[] xArray = { 114.7453 };
		double[] yArray = { 24.0255 };

		double exp_x = 575822.09;
		double exp_y = 2658313.78;

		int len = yArray.length;
		GsSpatialReference sr = new GsSpatialReference(srcWKT);
		GsSpatialReference tar = new GsSpatialReference(targetWKT);
		GsProjectCoordinateTransformation gsProjectTrans = new GsProjectCoordinateTransformation(
				sr, tar);
		assertTrue(gsProjectTrans.Transformation(xArray, yArray, len, 1));

		double tol = 5;
		assertEquals(exp_x, xArray[0], tol);
		assertEquals(exp_y, yArray[0], tol);

		// Destructor
		gsProjectTrans.delete();
		tar.delete();
		sr.delete();

	}

	/**
	 * 通过WKT实现坐标转换
	 */
	@Test
	public void transByWKT4() {
		System.out.println("transByWKT4");
		String srcWKT = "GEOGCS[\"GCS_WGS_1984\",DATUM[\"D_WGS_1984\",SPHEROID[\"WGS_1984\",6378137,298.257223563]],PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433]]";
		String targetWKT = "PROJCS[\"Gauss_Kruger\",GEOGCS[\"Xian 1980\",DATUM[\"Chinese National (Xian1980)\",SPHEROID[\"IUGG 1975\",6378140,298.257],TOWGS84[8.4322,-17.372,-17.1256,-5.266e-006,9.73e-006,-1.4748e-005,3.5816e-006]],PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433]],UNIT[\"meters\",1],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",117],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",500000],PARAMETER[\"false_northing\",0]]";
		double[] xArray = { 117.67065244, 117.87359439 };
		double[] yArray = { 41.285622, 41.2541047 };
		double[] zArray = { 658.4854, 656.8332 };
		double exp_x = 556181.37;
		double exp_y = 4572512.61;
		double exp_z = 658.4854;
		int len = yArray.length;
		GsSpatialReference sr = new GsSpatialReference(srcWKT);
		GsSpatialReference tar = new GsSpatialReference(targetWKT);
		GsProjectCoordinateTransformation gsProjectTrans = new GsProjectCoordinateTransformation(
				sr, tar);
		assertTrue(gsProjectTrans
				.Transformation(xArray, yArray, zArray, len, 1));

		double tol = 30;
		assertEquals(exp_x, xArray[0], tol);
		assertEquals(exp_y, yArray[0], tol);
		assertEquals(exp_z, zArray[0], tol);

		// Destructor
		gsProjectTrans.delete();
		tar.delete();
		sr.delete();
	}

	@Test
	public void createSpatialreferenceFromEPSG() {

		String strCurDir = System.getProperty("user.dir");
		strCurDir += "/../data/coordinatesystem/EPSG.txt";

		GsGlobeConfig.Instance().Child("Kernel/SpatialRererence/EPSG")
				.Value(strCurDir);

		System.out.println(GsGlobeConfig.Instance()
				.Child("Kernel/SpatialRererence/EPSG").StringValue(""));

		GsSpatialReference sr = new GsSpatialReference(4326);
		System.out.println("WKT is " + sr.ExportToWKT());

		sr.delete();
	}

	@Test
	public void createSpatialreferenceFromWKT() {
		GsSpatialReference sr = new GsSpatialReference("");
		System.out.println(sr.EPSG());
		sr.delete();
	}

	@Test
	public void WGS84ToXian80() {
		System.out.println("WGS84ToXian80");
		//WGS84To80Transformation trans = new WGS84To80Transformation();
		double[] coord = new double[] { 114, 36 };
		//trans.Transformation(coord, 1, 2);
	}

	@Test
	public void WGS84ToBD09() {
		System.out.println("WGS84ToBD09");
		// System.out.println(System.getProperty("java.library.path"));
		double[] xArray = { 132.4586 };
		double[] yArray = { 96.52347 };
		int len = 1;
		GsWGS84ToBD09CoordinateTransformation bdTrans = new GsWGS84ToBD09CoordinateTransformation();
		bdTrans.Transformation(xArray, yArray, len, 1);
		System.out.println(xArray[0]);// 132.46507856376167
		System.out.println(yArray[0]);// 96.5295332844597

		double[] xArray2 = { 132.4586, 133.6987, 135.8742 };
		double[] yArray2 = { 96.52347, 93.5588, 97.12364 };
		int len2 = 3;
		bdTrans.Transformation(xArray2, yArray2, len2, 1);
		bdTrans.delete();
		System.out.println(xArray2[0] + "," + xArray2[1] + "," + xArray2[2]);// 132.46507856376167-133.70501984352137-135.88042492671659
		System.out.println(yArray2[0] + "," + yArray2[1] + "," + yArray2[2]);// 96.5295332844597-93.5650231593156-97.13001179575382
	}

	@Test
	public void BD09ToWGS84() {
		double[] xArray = { 132.46507856376167 };
		double[] yArray = { 96.5295332844597 };
		int len = 1;
		double precision = 0.001;
		GsBD09ToWGS84CoordinateTransformation bdTrans = new GsBD09ToWGS84CoordinateTransformation(
				precision);
		bdTrans.Transformation(xArray, yArray, len, 1);
		System.out.println(xArray[0]);// 132.4586
		System.out.println(yArray[0]);// 96.52347

		double[] xArray2 = { 132.46507856376167, 133.70501984352137,
				135.88042492671659 };//
		double[] yArray2 = { 96.5295332844597, 93.5650231593156,
				97.13001179575382 };//
		int len2 = 3;
		bdTrans.Transformation(xArray2, yArray2, len2, 1);
		System.out.println(xArray2[0] + "," + xArray2[1] + "," + xArray2[2]);// 132.4586,133.6987,135.8742
		System.out.println(yArray2[0] + "," + yArray2[1] + "," + yArray2[2]);// 96.52347,93.5588,97.12364

		double[] xArray3 = { 114.348818 };
		double[] yArray3 = { 30.484786 };
		bdTrans.Transformation(xArray3, yArray3, len, 1);
		System.out.println(xArray3[0]);// 132.4586
		System.out.println(yArray3[0]);// 96.52347

		double[] xArray4 = { 112.989992 };
		double[] yArray4 = { 28.11831 };
		bdTrans.Transformation(xArray4, yArray4, len, 1);
		System.out.println("---4-----");
		System.out.println(xArray4[0]);// 132.4586
		System.out.println(yArray4[0]);// 96.52347
		bdTrans.delete();
	}

	@Test
	public void GCJ02ToWGS84() {
		double[] xArray = { 114.14573029972055 };//
		double[] yArray = { 30.83244980489334 };//
		int len = 1;
		double precision = 0.1;
		GsGCJ02ToWGS84CoordinateTransformation gcjTrans = new GsGCJ02ToWGS84CoordinateTransformation(
				precision);
		gcjTrans.Transformation(xArray, yArray, len, 1);
		System.out.println(xArray[0]);// 114.147949
		System.out.println(yArray[0]);// 30.826781

		double[] xArray2 = { 114.14573029972055, 113.66347647490316,
				109.04897677188546 };//
		double[] yArray2 = { 30.83244980489334, 34.80186884751153,
				34.23957996821254 };//
		int len2 = 3;
		gcjTrans.Transformation(xArray2, yArray2, len2, 1);
		System.out.println(xArray2[0] + "," + xArray2[1] + "," + xArray2[2]);// 114.147949,113.664551,109.050293
		System.out.println(yArray2[0] + "," + yArray2[1] + "," + yArray2[2]);// 30.826781,34.795762,34.234512

		gcjTrans.delete();
	}

	@Test
	public void WGS84ToGCJ02() {
		double[] xArray = { 114.147949 };
		double[] yArray = { 30.826781 };
		int len = 1;

		GsWGS84ToGCJ02CoordinateTransformation gcjTrans = new GsWGS84ToGCJ02CoordinateTransformation();
		gcjTrans.Transformation(xArray, yArray, len, 1);

		System.out.println(xArray[0]);// 114.14573029972055
		System.out.println(yArray[0]);// 30.83244980489334

		double[] xArray2 = { 114.147949, 113.664551, 109.050293 };
		double[] yArray2 = { 30.826781, 34.795762, 34.234512 };
		int len2 = 3;

		gcjTrans.Transformation(xArray2, yArray2, len2, 1);
		System.out.println(xArray2[0] + "," + xArray2[1] + "," + xArray2[2]);// 114.14573029972055,113.66347647490316,109.04897677188546
		System.out.println(yArray2[0] + "," + yArray2[1] + "," + yArray2[2]);// 30.83244980489334,34.80186884751153,34.23957996821254

		gcjTrans.delete();
	}

	/**
	 * 获取仿射变换的参数
	 */
	@Test
	public void affineCoordinateParam() {
		double[] srcPoints = { 352645.245, 1225950.438, 353800.402,
				1230000.378, 351600.519, 1225959.506, 345800.101, 1225959.8 };
		double[] targetPoints = { 3924063.3, 21499758.9, 3944871.4, 21500009.5,
				3904193.8, 21499987.5, 3870898.1, 21499994.9 };
		GsAffineCoordinateTransformation gsAffTrans = new GsAffineCoordinateTransformation(
				srcPoints, targetPoints, 8);
		double[] param = new double[6];
		gsAffTrans.Elements(param);

		// 验证
		double tol = 0.01;
		double[] except_param = { -4131626.615, 7.087, 4.527, 21452272.349,
				-0.0232, 0.045 };
		// double[] except_param =
		// {7.087,-0.0232,4.527,0.045,-4131626.615,21452272.349 };
		assertEquals(except_param[0], param[4], tol);
		assertEquals(except_param[1], param[0], tol);
		assertEquals(except_param[2], param[2], tol);
		assertEquals(except_param[3], param[5], tol);
		assertEquals(except_param[4], param[1], tol);
		assertEquals(except_param[5], param[3], tol);
	}

	@Test
	public void affineCoordinateTrans() {
		double[] srcPoints = {};
		double[] targetPoints = {};
		GsAffineCoordinateTransformation gsAffTrans = new GsAffineCoordinateTransformation(
				srcPoints, targetPoints, 0);
		double[] param = new double[6];
		gsAffTrans.Elements(param);

		double[] xArray = {};
		double[] yArray = {};
		int len = yArray.length;
		gsAffTrans.Transformation(yArray, yArray, len, 1);

		// 验证
		double tol = 0.001;
	}

	@Test
	public void affineCoordinateTrans1() {

		System.out.println("affineCoordinateTrans");
		double[] source = new double[] { 352645.245, 1225950.438, 353800.402,
				1230000.378, 351600.519, 1225959.506, 345800.101, 1225959.8 };
		double[] target = new double[] { 3924063.3, 21499758.9, 3944871.4,
				21500009.5, 3904193.8, 21499987.5, 3870898.1, 21499994.9 };

		GsAffineCoordinateTransformation aff = new GsAffineCoordinateTransformation(
				source, target, 8);

		double ele[] = new double[6];
		aff.Elements(ele);

		double[] exp_a = new double[] { -4131626.615, 7.087, 4.527 };
		double[] exp_b = new double[] { 21452272.349, -0.0232, 0.045 };

		double tol = 0.01;

		assertEquals(exp_a[0], ele[4], tol);
		assertEquals(exp_a[1], ele[0], tol);
		assertEquals(exp_a[2], ele[2], tol);

		assertEquals(exp_b[0], ele[5], tol);
		assertEquals(exp_b[1], ele[1], tol);
		assertEquals(exp_b[2], ele[3], tol);
		double[] x = new double[] { 1, 2 };
		double[] y = new double[] { 1, 2 };

		aff.Transformation(x, y, 2, 1);

		aff.delete();
	}

	/**
	 * 通过仿射参数，获取变换结果
	 */
	@Test
	public void affineCoordinateTrans2() {
		double[] srcPoints = { 352645.245, 1225950.438, 353800.402,
				1230000.378, 351600.519, 1225959.506, 345800.101, 1225959.8 };
		double[] targetPoints = { 3924063.3, 21499758.9, 3944871.4, 21500009.5,
				3904193.8, 21499987.5, 3870898.1, 21499994.9 };
		GsAffineCoordinateTransformation gsAffTrans = new GsAffineCoordinateTransformation(
				srcPoints, targetPoints, 8);
		double[] param = new double[6];
		gsAffTrans.Elements(param);
		double t = 0.01;
		assertEquals(-4131626.61523, param[4], t);
		assertEquals(7.08764, param[0], t);
		assertEquals(4.52754, param[2], t);
		assertEquals(21452272.34961, param[5], t);
		assertEquals(-0.02324, param[1], t);
		assertEquals(0.04550, param[3], t);

		param[0] = 7.08764;
		param[1] = -0.02324;
		param[2] = 4.52754;
		param[3] = 0.04550;
		param[4] = -4131626.61523;
		param[5] = 21452272.34961;
		gsAffTrans.delete();

		gsAffTrans = new GsAffineCoordinateTransformation(param);

		double[] xArray = { 352645.245, 353800.402 };
		double[] yArray = { 1225950.438, 1230000.378 };
		int len = yArray.length;
		gsAffTrans.Transformation(xArray, yArray, len, 1);

		double exp_x[] = { 3918335.575, 3944859.177 };
		double exp_y[] = { 21499857.619, 21500015.045 };
		// 验证
		double tol = 1;
		assertEquals(exp_x[0], xArray[0], tol);
		assertEquals(exp_x[1], xArray[1], tol);
		assertEquals(exp_y[0], yArray[0], tol);
		assertEquals(exp_y[1], yArray[1], tol);
	}

	/**
	 * 通过仿射参数，获取变换结果
	 */
	@Test
	public void affineCoordinateTrans3() {

		double[] param = new double[6];
		param[4] = -4131626.61523;
		param[0] = 7.08764;
		param[2] = 4.52754;
		param[5] = 21452272.34961;
		param[1] = -0.02324;
		param[3] = 0.04550;
		GsAffineCoordinateTransformation gsAffTrans = new GsAffineCoordinateTransformation(
				param);

		double[] xArray = { 352645.245, 353800.402, 351600.519, 345800.101 };
		double[] yArray = { 1225950.438, 1230000.378, 1225959.506, 1225959.8 };
		int len = yArray.length;
		assertTrue(gsAffTrans.Transformation(xArray, yArray, len, 1));

		double exp_x[] = { 3918335.575, 3944859.177 };
		double exp_y[] = { 21499857.619, 21500015.045 };
		// 验证
		double tol = 0.001;
		assertEquals(exp_x[0], xArray[0], tol);
		assertEquals(exp_x[1], xArray[1], tol);
		assertEquals(exp_y[0], yArray[0], tol);
		assertEquals(exp_y[1], yArray[1], tol);
	}

	@Test
	public void ProjectCoordinateTrans() {

		GsSpatialReference sr1 = new GsSpatialReference(4326);
		GsSpatialReference sr2 = new GsSpatialReference(4326);

		// GsProjectCoordinateTransformation tras = new
		// GsProjectCoordinateTransformation(sr1,sr2);

	}

	@Test
	public void test() {

		double l = 112.2;
		double b = 39.1;
		double azimuth = 283.15;
		double xori = 112.2;
		double yori = 39.1;
		double zori = 1510;
		double[] xArray = { 0.9 };
		double[] yArray = { 0 };
		double[] zArray = { -4010 };

		double exp_x = 112.1894515461139;
		double exp_y = 39.06483630341012;
		double exp_z = 1511.2634319216013;
		int len = yArray.length;

		//TrajectoryTransformation trajectoryTrans = new TrajectoryTransformation(
		//		l, b, azimuth, xori, yori, zori);
		//trajectoryTrans.Transformation(xArray, yArray, zArray, len, 1);
		//trajectoryTrans.delete();

		// 验证
		double tol = 0.001;
		//assertEquals(exp_x, xArray[0], tol);
		//assertEquals(exp_y, yArray[0], tol);
		//assertEquals(exp_z, zArray[0], tol);

	}
	
	@Test
	public void ProjectCoordinateCreate() {
		GsSpatialReference S80 = new GsSpatialReference(2360);
		GsSpatialReference S200 = new GsSpatialReference(4524);

		GsCoordinateTransformation ptrTran;

		// 布尔莎参数
		double a[] = { 1, 2, 3, 4, 5, 6, 7 };
		ptrTran = GsCoordinateTransformationFactory
				.CreateBursa7CoordinateTransformation(S80, S200, a);
		// 二维7参
		ptrTran = GsCoordinateTransformationFactory
				.CreatePlane7PatameterCoordinateTransformation(S80, S200, a);
		// 二维四参数
		double b[] = { 1, 2, 3, 4 };
		ptrTran = GsCoordinateTransformationFactory
				.CreatePlane4PatameterCoordinateTransformation(b);
		// "三维四参数"
		double c[] = { 1, 2, 3, 4, 5, 6 };
		double BL[] = { 32, 110 };
		ptrTran = GsCoordinateTransformationFactory
				.CreateCube4PatameterCoordinateTransformation(S80, S200, BL[0],
						BL[1], c);

		// "三维七参数"
		ptrTran = GsCoordinateTransformationFactory
				.Create3DPlane7PatameterCoordinateTransformation(S80, S200, a);
		double d1[] = { 2, 3, 4, 5, 6, 0 };
		double d2[] = { 1, 2, 3, 3, 2, 1 };
		// "平面多项式拟合"
		// "椭球多项式拟合"
		ptrTran = GsCoordinateTransformationFactory
				.CreateGeoidFittingCoordinateTransformation(S80, S200, 2, 3,
						d1, d2);

		// "莫洛金斯基"
		double d3O[] = { 0, 0, 0 };
		ptrTran = GsCoordinateTransformationFactory
				.CreateMolodenskyCoordinateTransformation(S80, S200, a, d3O);
		double bl[] = { 108.1, 32, 109.1, 32, 108.1, 30, 109.1, 30 };
		double dpt[] = { 0.001, 0.001, 0.001, 0.001, 0.001, 0.001, 0.001, 0.001 };

		ptrTran = GsCoordinateTransformationFactory
				.CreateGraphsInterpolationCoordinateTransformation(S80, S200,
						bl, dpt);
		// 改正量

		double dx[] = { 108.1 };
		double dy[] = {  32 };	
		double dz[] = { 0 };
		ptrTran.Transformation(dx, dy, dz, 1, 0);
	}
	
	@Test
	public void testBursa() {

		double src_80[] = { 36590405.0511,3351529.3730,1085.6975,36587811.4492,3351592.8277,1073.3695,36590853.3447,3351638.3663,1075.5065,36588925.1413,3351711.1922,1073.0075,36589561.1570,3351779.5352,1074.1085,36588043.6048,3351978.7439,1073.9395,36590247.0110,3352244.4763,1068.1955,36590688.9841,3352553.3323,1075.2985,36588859.8064,3352597.7914,1072.2525,36587685.3914,3352802.0963,1081.0395,36589491.6288,3352901.4517,1078.3215,36589036.7276,3353025.5747,1072.3385,36588206.9239,3353079.1279,1073.7795,36587825.9399,3353444.1621,1071.9865,36590600.5878,3353519.5999,1079.2675,36589013.5537,3353692.1934,1100.5445,36588251.6563,3353711.6590,1081.1375,36589413.6916,3353728.0189,1081.0735,36590136.0189,3353973.9857,1081.6545 };
		double src_2000[] = { 36590519.769,3351534.961,1085.698,36587926.158,3351598.415,1073.370,36590968.065,3351643.954,  1075.507,36589039.854,3351716.780,1073.008,36589675.872,3351785.123,1074.109,36588158.315,3351984.332,1073.940,36590361.729,3352250.066,1068.196,36590803.703,3352558.924,1075.299,36588974.519,3352603.382,1072.253,36587800.100,3352807.688,1081.040,36589606.344,3352907.044,1078.322,36589151.441,3353031.167,1072.339,36588321.634,3353084.720,1073.780,36587940.649,3353449.756,1071.987,36590715.306,3353525.195,1079.268,36589128.266,3353697.788,1100.545,36588366.366,3353717.254,1081.138,36589528.406,3353733.614,1081.074,36590250.736,3353979.582,1081.655 };

		GsSpatialReference src = new GsSpatialReference(2360);
		GsSpatialReference tar = new GsSpatialReference(4524);
		double param[] = {0,1,2,3,4,5,6};

		double m = GsCoordinateTransformationFactory.ComputeBursa7Parameter(src,tar, 19,3,src_80,src_2000, param);
		System.out.println("测试布尔莎");
		System.out.println(param[0] + "," + param[1] + "," + param[2]+","+param[3] + "," + param[4] + "," + param[5]+","+param[6]);
	}			

	@Test
	public void testEKBReadWriter() {

		String base64geometry = "AQYAACCKEQAAAQAAAAEDAAAAAQAAAAoAAAAGuqps6V9dQF5quU3 + KEFAzZc2ZupfXUBAkPR6 / ihBQG9vB6nsX11AcFIT6 / 4oQUANdwcT8V9dQHFIBMb / KEFAPXZte / BfXUBzN8t9FylBQJdtBjPmX11AvaFYxRgpQUA2k8JY5V9dQJx2BZYZKUFAntPxDuVfXUAmUEj2GSlBQNKfEcHnX11A / oxKAP4oQUAGuqps6V9dQF5quU3 + KEFA";

		GsGrowByteBuffer buffer =new GsGrowByteBuffer();
		buffer.FromBase64(base64geometry);
		GsEWKBOGCReader baseReader = new GsEWKBOGCReader(buffer);
		GsGeometry gsGem = baseReader.Read();
		GsSpatialReference  gsSpatialReference = gsGem.SpatialReference();
		System.out.println("---"+ gsSpatialReference.EPSG());
		
		GsWKTOGCWriter baseWriter = new GsWKTOGCWriter();
		baseWriter.Reset();
		baseWriter.Write(gsGem);
		String wkt_p = baseWriter.WKT();
		System.out.println("---"+ wkt_p);
		
		GsGrowByteBuffer pOutBuffer =  new GsGrowByteBuffer();
		int SridOrEPSG = gsSpatialReference.EPSG();
		GsEWKBOGCWriter writer =  new GsEWKBOGCWriter(pOutBuffer,SridOrEPSG);
		writer.Write(gsGem);
		//这里base64string会与原有可能存在不一致, 原有是有几何改正功能,调整为geostar的顺序
		String base64String =  writer.WKB().ToBase64();
		System.out.println("---"+ base64String);
		}	
	@Test
	public void DownTianditutest()
	{

		GsWellknownTMSUriParser ptrUriParser = new GsWellknownTMSUriParser(GsWellknownWebTileService.eTiandituVectorWebMercatorWMTS);
		boolean g = ptrUriParser.ParseCapability();
		String str = ptrUriParser.FormatUri();

		GsTileColumnInfo info = new GsTileColumnInfo();
		info.setFeatureType(GsFeatureType.ePrevectorTileFeature);
		info.setValidBottomLevel(ptrUriParser.BottomLevel());
		info.setValidTopLevel(ptrUriParser.TopLevel());
		info.setXYDomain(ptrUriParser.LayerExtent());
		GsWebGeoDatabaseFactory pfac  = new GsWebGeoDatabaseFactory();
		GsConnectProperty connProperty = new GsConnectProperty();
		connProperty.setDataSourceType(GsDataSourceType.eWeb);
		GsGeoDatabase pDB = pfac.Open(connProperty);
		GsTileClass tileClass = pDB.CreateTileClass("img", ptrUriParser.SpatialReference(), ptrUriParser.Pyramid(), info);
		if(tileClass == null)
			return;
		GsTMSTileClass tms = GsTMSTileClass.DowncastTo(tileClass);
		if(tms == null)
			return;
		tms.UrlTemplate(ptrUriParser.FormatUri());
		tms.TileType(GsTileEncodingType.ePngType);
		
		//这里是缓存,浏览过的瓦片会缓存到此数据集  
		GsSqliteGeoDatabaseFactory fac = new GsSqliteGeoDatabaseFactory();
		GsConnectProperty conn = new GsConnectProperty();
		String strCurDir = System.getProperty("user.dir");
		String strServer = strCurDir+ "/data/400w";
		conn.setServer(strServer);
		System.out.println(strServer);
		GsGeoDatabase ptrGDB =  fac.Open(conn);
		if(pDB == null)
			return;
		GsTileClass //cacheTcs =  ptrGDB.OpenTileClass("webtest");
		//if(cacheTcs != null)
		//	cacheTcs.Delete();
		cacheTcs = ptrGDB.CreateTileClass("webtest", ptrUriParser.SpatialReference(), ptrUriParser.Pyramid(), info);
		if(cacheTcs== null)
			return ;
	
		boolean bok = cacheTcs.Transaction().StartTransaction();
		GsTile ptrDstTile = cacheTcs.CreateTile();
	
		GsTileCursor ptrCursor = tileClass.Search(4,4);
		if(ptrCursor== null)
			return ;
		GsTile ptileSrc = ptrCursor.Next();
		do
		{
			if (ptileSrc == null)
				break;
			ptrDstTile.OID(-1);
			byte[] pData= new byte[ptileSrc.TileDataLength()];
			ptileSrc.TileDataPtr(pData);
			
			ptrDstTile.TileData(pData, ptileSrc.TileDataLength());
			ptrDstTile.TileType(ptileSrc.TileType());
			ptrDstTile.Level(ptileSrc.Level());
			ptrDstTile.Row(ptileSrc.Row());
			ptrDstTile.Col(ptileSrc.Col());
			ptrDstTile.Store();

		} 
		while (ptrCursor.Next(ptileSrc));
		cacheTcs.Transaction().CommitTransaction();
	}
	@Test
	public void  DownWMTSTest()
    {
		String strUrl = "http://192.168.100.244:6080/arcgis/rest/services/DLG100/MapServer/wmts";
		GsWMTSUriParser ptrUriParser = new GsWMTSUriParser(strUrl);
		GsWebUriParser webparser = ptrUriParser;
		//webparser->UserParameter().AddPair("tk", "2ce94f67e58faa24beb7cb8a09780552");
		if(ptrUriParser.ParseCapability() == false)
			return;
		
//		ptrUriParser.LayerName(ptrUriParser.AllLayerName().get(0));
//		ptrUriParser.CurrentTileMatrixSet(ptrUriParser.TileMatrixSets().get(0));
//		ptrUriParser.CurrentImageFormat(ptrUriParser.ImageFormats().get(0));
//		ptrUriParser.CurrentLayerStyle(ptrUriParser.Styles().get(0));
		GsPyramid pyramid = ptrUriParser.Pyramid();
		GsSpatialReference  spatial = ptrUriParser.SpatialReference();
		
		boolean g = ptrUriParser.ParseCapability();
		String str = ptrUriParser.FormatUri();

		GsTileColumnInfo info = new GsTileColumnInfo();
		info.setFeatureType(GsFeatureType.ePrevectorTileFeature);
		info.setValidBottomLevel(ptrUriParser.BottomLevel());
		info.setValidTopLevel(ptrUriParser.TopLevel());
		info.setXYDomain(ptrUriParser.LayerExtent());
		GsWebGeoDatabaseFactory pfac  = new GsWebGeoDatabaseFactory();
		GsConnectProperty connProperty = new GsConnectProperty();
		connProperty.setDataSourceType(GsDataSourceType.eWeb);
		GsGeoDatabase pDB = pfac.Open(connProperty);
		GsTileClass tileClass = pDB.CreateTileClass("img", ptrUriParser.SpatialReference(), ptrUriParser.Pyramid(), info);
		if(tileClass == null)
			return;
		GsTMSTileClass tms = GsTMSTileClass.DowncastTo(tileClass);
		if(tms == null)
			return;
		tms.UrlTemplate(ptrUriParser.FormatUri());
		tms.TileType(GsTileEncodingType.ePngType);
		
		//这里是缓存,浏览过的瓦片会缓存到此数据集  
		GsSqliteGeoDatabaseFactory fac = new GsSqliteGeoDatabaseFactory();
		GsConnectProperty conn = new GsConnectProperty();
		String strCurDir = System.getProperty("user.dir");
		String strServer = strCurDir+ "/data/400w";
		conn.setServer(strServer);
		System.out.println(strServer);
		GsGeoDatabase ptrGDB =  fac.Open(conn);
		if(pDB == null)
			return;
		GsTileClass cacheTcs =  ptrGDB.OpenTileClass("webtest2");
		//if(cacheTcs != null)
		//	cacheTcs.Delete();
		cacheTcs = ptrGDB.CreateTileClass("webtest2", ptrUriParser.SpatialReference(), ptrUriParser.Pyramid(), info);
		if(cacheTcs== null)
			return ;
		cacheTcs.Transaction().StartTransaction();
		GsTile ptrDstTile = cacheTcs.CreateTile();
	
		GsTileCursor ptrCursor = tileClass.Search(0,4);
		if(ptrCursor== null)
			return ;
		GsTile ptileSrc = ptrCursor.Next();
		do
		{
			if (ptileSrc == null)
				break;
			ptrDstTile.OID(-1);
			byte[] pData= new byte[ptileSrc.TileDataLength()];
			ptileSrc.TileDataPtr(pData);
			
			ptrDstTile.TileData(pData, ptileSrc.TileDataLength());
			ptrDstTile.TileType(ptileSrc.TileType());
			ptrDstTile.Level(ptileSrc.Level());
			ptrDstTile.Row(ptileSrc.Row());
			ptrDstTile.Col(ptileSrc.Col());
			ptrDstTile.Store();

		} 
		while (ptrCursor.Next(ptileSrc));
		cacheTcs.Transaction().CommitTransaction();
    }
}
