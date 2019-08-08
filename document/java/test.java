package unittest;

import static org.junit.Assert.*;

import java.awt.Point;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.geostar.kernel.*;
import com.geostar.kernel.extensions.*;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.geostar.kernel.GsPoint;
 
public class test {
	

//
//	
//	
//	    @Before
//	    public void setUp() {
//	        System.out.println("---begin test---");
//	    }
//	     
//	    @Test
//	    public void test() {
//	        Calculate calculate = new Calculate();
//	        assertEquals(8, calculate.Add(3, 5));
//	        System.out.println("test case");
//	    }
//	     
//	    @After
//	    public void tearDown() {
//	        System.out.println("---end test---");
//	    }
//	}
//
//	
//	 
//	 class Calculate {
//	    public int  Add(int x,int y) {
//	        return x + y;
//	    }
	    
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.loadLibrary("gsjavaport");
		System.out.println("loadlibrary");
		System.out.println("test");
	}
		     
  @Test
	    public void point()throws Exception  {
	    	
	    	GsPoint mypoint=new GsPoint();
	    	mypoint.Set(1.1, 1.1);
	    	
	    	assertEquals(mypoint.X(),1.1,0);
	    	assertEquals(mypoint.Y(),1.1,0); 
	    	
	    	 System.out.println("right");
	    }
}
  


	 
	 
	 
	

