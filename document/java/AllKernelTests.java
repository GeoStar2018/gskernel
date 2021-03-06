package unittest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({coordtrans.class,gml.class,wkt.class,geojson.class,bytereader.class,
	geodatabase.class, gsConfigTest.class, spatialreference.class,spatialanalysis.class,
	vectortile.class,testgscanvas.class,geometry.class,TileSpliter.class,spatialreference2.class,
	proxyFeatureClassTest.class})
public class AllKernelTests {

}
