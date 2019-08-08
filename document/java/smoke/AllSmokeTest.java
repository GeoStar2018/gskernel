package unittest.smoke;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({smokeTest.class,SmokeGeometry.class,SmokeSpatialReference.class,SmokeVectorTile.class,LoadLibrary.class})
public class AllSmokeTest {
	

}
