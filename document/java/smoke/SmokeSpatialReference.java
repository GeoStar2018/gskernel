package unittest.smoke;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.geostar.kernel.GsGlobeConfig;
import com.geostar.kernel.GsKernel;
import com.geostar.kernel.GsSpatialReference;

public class SmokeSpatialReference {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.loadLibrary("gsjavaport");
		System.loadLibrary("ggsextensions");
		GsKernel.Initialize();
		System.out.println("loadlibrary");
		String strCurDir = System.getProperty("user.dir");
		strCurDir += "/../data/coordinatesystem/EPSG.txt";
		System.out.println(strCurDir);
		GsGlobeConfig.Instance().Child("Kernel/SpatialRererence/EPSG")
				.Value(strCurDir);

	}
	@Test
	public void structByEPSG1() {
		GsSpatialReference sr = new GsSpatialReference(4326);
		System.out.println(sr.ExportToWKT());
		String wkt = "GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AXIS[\"Latitude\",NORTH],AXIS[\"Longitude\",EAST],AUTHORITY[\"EPSG\",\"4326\"]]";
		boolean flag = wkt.equalsIgnoreCase(sr.ExportToWKT());
		sr.delete();
		assertTrue(flag);
	}
	@Test
	public void structByEPSG2() {
		GsSpatialReference sr = new GsSpatialReference(4490);
		System.out.println(sr.ExportToWKT());
		String wkt = "GEOGCS[\"China Geodetic Coordinate System 2000\",DATUM[\"China_2000\",SPHEROID[\"CGCS2000\",6378137,298.257222101,AUTHORITY[\"EPSG\",\"1024\"]],AUTHORITY[\"EPSG\",\"1043\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AXIS[\"Latitude\",NORTH],AXIS[\"Longitude\",EAST],AUTHORITY[\"EPSG\",\"4490\"]]";
		boolean flag = wkt.equalsIgnoreCase(sr.ExportToWKT());
		sr.delete();
		assertTrue(flag);
	}
	@Test
	public void structByEPSG3() {
		GsSpatialReference sr = new GsSpatialReference(4610);
		System.out.println(sr.ExportToWKT());
		String wkt = "GEOGCS[\"Xian 1980\",DATUM[\"Xian_1980\",SPHEROID[\"IAG 1975\",6378140,298.257,AUTHORITY[\"EPSG\",\"7049\"]],AUTHORITY[\"EPSG\",\"6610\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AXIS[\"Latitude\",NORTH],AXIS[\"Longitude\",EAST],AUTHORITY[\"EPSG\",\"4610\"]]";
		boolean flag = wkt.equalsIgnoreCase(sr.ExportToWKT());
		sr.delete();
		assertTrue(flag);
	}
	@Test
	public void structByEPSG4() {
		GsSpatialReference sr = new GsSpatialReference(3857);
		Assert.assertTrue(sr.EPSG()==3857);
	}
	@Test
	public void structByEPSG5() {
		GsSpatialReference sr = new GsSpatialReference(2362);
		Assert.assertTrue(sr.EPSG()==2362);
	}
	@Test
	public void structByEPSG6() {
		GsSpatialReference sr = new GsSpatialReference(2385);
		Assert.assertTrue(sr.EPSG()==2385);
	}
	@Test
	public void structByEPSG7() {
		GsSpatialReference sr = new GsSpatialReference(2365);
		Assert.assertTrue(sr.EPSG()==2365);
	}
	@Test
	public void structByWKT1() {
		String wkt = "GEOGCS[\"Xian 1980\",DATUM[\"Xian_1980\",SPHEROID[\"IAG 1975\",6378140,298.257,AUTHORITY[\"EPSG\",\"7049\"]],AUTHORITY[\"EPSG\",\"6610\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AXIS[\"Latitude\",NORTH],AXIS[\"Longitude\",EAST],AUTHORITY[\"EPSG\",\"4610\"]]";
		GsSpatialReference sr = new GsSpatialReference(wkt);
		System.out.println(sr.EPSG());
		Assert.assertTrue(sr.EPSG()==4610);
	}
	@Test
	public void structByWKT2() {
		String wkt = "GEOGCS[\"China Geodetic Coordinate System 2000\",DATUM[\"China_2000\",SPHEROID[\"CGCS2000\",6378137,298.257222101,AUTHORITY[\"EPSG\",\"1024\"]],AUTHORITY[\"EPSG\",\"1043\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AXIS[\"Latitude\",NORTH],AXIS[\"Longitude\",EAST],AUTHORITY[\"EPSG\",\"4490\"]]";
		GsSpatialReference sr = new GsSpatialReference(wkt);
		System.out.println(sr.EPSG());
		Assert.assertTrue(sr.EPSG()==4490);
	}
	@Test
	public void structByWKT3() {
		String wkt = "GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AXIS[\"Latitude\",NORTH],AXIS[\"Longitude\",EAST],AUTHORITY[\"EPSG\",\"4326\"]]";
		GsSpatialReference sr = new GsSpatialReference(wkt);
		System.out.println(sr.EPSG());
		Assert.assertTrue(sr.EPSG()==4326);
	}
	@Test
	public void structByWKT4() {
		String wkt = "PROJCS[\"WGS 84 / Pseudo-Mercator\",GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4326\"]],PROJECTION[\"Mercator_1SP\"],PARAMETER[\"central_meridian\",0],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",0],PARAMETER[\"false_northing\",0],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],AXIS[\"X\",EAST],AXIS[\"Y\",NORTH],EXTENSION[\"PROJ4\",\"+proj=merc +a=6378137 +b=6378137 +lat_ts=0.0 +lon_0=0.0 +x_0=0.0 +y_0=0 +k=1.0 +units=m +nadgrids=@null +wktext  +no_defs\"],AUTHORITY[\"EPSG\",\"3857\"]]";
		GsSpatialReference sr = new GsSpatialReference(wkt);
		System.out.println(sr.EPSG());
		Assert.assertTrue(sr.EPSG()==3857);
	}
	@Test
	public void structByWKT5() {
		String wkt = "PROJCS[\"Xian 1980 / 3-degree Gauss-Kruger zone 38\",GEOGCS[\"Xian 1980\",DATUM[\"Xian_1980\",SPHEROID[\"IAG 1975\",6378140,298.257,AUTHORITY[\"EPSG\",\"7049\"]],AUTHORITY[\"EPSG\",\"6610\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4610\"]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",114],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",38500000],PARAMETER[\"false_northing\",0],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],AUTHORITY[\"EPSG\",\"2362\"]]";
		GsSpatialReference sr = new GsSpatialReference(wkt);
		System.out.println(sr.EPSG());
		Assert.assertTrue(sr.EPSG()==2362);
	}
}
