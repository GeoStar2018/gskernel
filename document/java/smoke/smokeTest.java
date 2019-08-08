package unittest.smoke;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.geostar.kernel.*;
import com.geostar.kernel.extensions.*;

public class smokeTest {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println(System.getProperty("java.library.path"));
		System.loadLibrary("gsjavaport");
		
		System.out.println("gsjavaport  load succeed");
		System.out.println("kernel jar Path "+GsKernel.class.getProtectionDomain().getCodeSource().getLocation());
		GsKernel.Initialize();
		System.out.println("kernel  Initialize succeed");
		System.loadLibrary("ggsextensions");		
		System.out.println(ggsextensions.class.getProtectionDomain().getCodeSource().getLocation());
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
	public void NewConfig() {
		GsConfig gconfig =  GsGlobeConfig.Instance();
		GsGlobeConfig.Instance().Child("InputPath").Value("ABC123abc");
		String str = gconfig.Item("InputPath").StringValue("EmptyString");
		assertEquals(str,"ABC123abc");
		System.out.println("GsConfig is Good");
	}
	
}
