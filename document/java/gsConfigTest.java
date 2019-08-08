package unittest;
import java.io.File;  
import java.io.FileInputStream;
import java.io.FileOutputStream;  
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.geostar.kernel.*;
public class gsConfigTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.loadLibrary("gsjavaport");
		System.out.println("loadlibrary");
		System.out.println("gsConfigTest");
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
	public void GsGlobeConfigSetValueAndGetValue() {
		System.out.println("GsGlobeConfigSetValueAndGetValue");
		GsConfig gconfig =  GsGlobeConfig.Instance();
		GsGlobeConfig.Instance().Child("InputPath").Value("ABC123abc");
		String str = gconfig.Item("InputPath").StringValue("EmptyString");
		assertEquals(str,"ABC123abc");
	}
	
	@Test
	public void GsGlobeConfigSetIntValueAndGetValue() {
		System.out.println("GsGlobeConfigSetIntValueAndGetValue");
		GsConfig gconfig =  GsGlobeConfig.Instance();
		GsGlobeConfig.Instance().Child("NumberConfig").Value(11);
		int nVal = gconfig.Item("NumberConfig").IntValue(0);
		assertEquals(nVal,11);
	}
	
}
