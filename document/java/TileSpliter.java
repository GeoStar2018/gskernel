package unittest;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.geostar.kernel.*;
public class TileSpliter {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	    System.loadLibrary("gsjavaport");
		
		GsKernel.Initialize();
		System.out.println("TileSpliter");
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
	void SplitTile(String strTif,String strOutput,boolean bDEM)
	{
		GsPyramid pyramid = new GsMultiPyramid();
		SliceService service = new SliceService(strTif,strOutput, bDEM, pyramid);
		service.startSlice();
		
	}
	@Test
	public void SplitRasterTile() {

		String strCurDir = System.getProperty("user.dir");
		String strInput = strCurDir+ "/test.tiff";
		File file =  new File(strInput);
		if(!file.exists())
		{
			System.out.println("file not exists "+ strInput);
			return;
		}
		String strOutput = strCurDir+ "/testautotile.tile";
		SplitTile(strInput,strOutput,false);
		
	}

}
