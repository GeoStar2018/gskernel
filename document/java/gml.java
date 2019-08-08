package unittest;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.geostar.kernel.GsGML3OGCWriter;
import com.geostar.kernel.GsGMLOGCReader;
import com.geostar.kernel.GsGMLOGCWriter;
import com.geostar.kernel.GsGeometry;
import com.geostar.kernel.GsKernel;

public class gml {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.loadLibrary("gsjavaport");
		GsKernel.Initialize();
		System.out.println("loadlibrary succeed");
		System.out.println("gml");
	}
	
	@Test
	public void point_test1(){
		String gml = "<gml:Point><gml:coordinates>39.923615,116.38094</gml:coordinates></gml:Point>";
		GsGMLOGCReader baseReader = new GsGMLOGCReader("");
		baseReader.Begin(gml);
		GsGeometry g = baseReader.Read();
		GsGMLOGCWriter gmlWriter = new GsGMLOGCWriter();
		gmlWriter.Reset();
		gmlWriter.Write(g);
		String gml_p = gmlWriter.GML();
		Assert.assertEquals(gml,gml_p);
	}
	
	@Test
	public void point_test2(){
		String gml = "<gml:Point><gml:coordinates>39.923615,116.38094</gml:coordinates></gml:Point>";
		GsGMLOGCReader baseReader = new GsGMLOGCReader("");
		baseReader.Begin(gml);
		GsGeometry g = baseReader.Read();
		GsGML3OGCWriter gmlWriter = new GsGML3OGCWriter();
		gmlWriter.Reset();
		gmlWriter.Write(g);
		String gml_p = gmlWriter.GML();
		String out = "<gml:Point><gml:pos>39.923615 116.38094</gml:pos></gml:Point>";
		Assert.assertEquals(out,gml_p);
	}
	
	@Test
	public void line_test1(){
		String gml = "<gml:LineString><gml:coordinates>53.28751,121.00548 53.28738,121.00812 53.287788,121.01209 53.287178,121.03068 53.28677,121.04173"
				+ " 53.289009,121.04666 53.291149,121.05119 53.298786,121.05955 53.301941,121.06545 53.303978,121.07645 53.305405,121.08546 53.303368,"
				+ "121.09158 53.297462,121.09595 53.293388,121.10196 53.291962,121.10746 53.290539,121.11173 53.288807,121.11407 53.286873,121.11571 "
				+ "53.282085,121.11398 53.279335,121.1153 53.276382,121.11693 53.274448,121.1208 53.273224,121.12477 53.273735,121.12915 53.275059,"
				+ "121.13363 53.27964,121.1431 53.280964,121.15054 53.279335,121.15939 53.278828,121.1655 53.279541,121.17284 53.279846,121.18424 "
				+ "53.278419,121.19972 53.278011,121.20664 53.277298,121.21601 53.277096,121.22456 53.278011,121.23312 53.280964,121.24025 53.28524,"
				+ "121.24768 53.288197,121.2544 53.287991,121.26316 53.286362,121.27141 53.287277,121.28017 53.291351,121.29239 53.298786,121.3043 "
				+ "53.31131,121.31306 53.318848,121.32426 53.321392,121.33333 53.323124,121.34269 53.323326,121.35165 53.319763,121.37314 53.316097,"
				+ "121.38995 53.316097,121.3961 53.316402,121.40349 53.318542,121.41219 53.320782,121.42059 53.320782,121.42513 53.320171,121.43118 "
				+ "53.319458,121.43689 53.321289,121.44417 53.322514,121.44906 53.324142,121.45354 53.325668,121.45955 53.327503,121.46565 53.328724,"
				+ "121.46953 53.330254,121.47391 53.331779,121.48154 53.332596,121.48827 53.332649,121.48844</gml:coordinates></gml:LineString>";
		GsGMLOGCReader baseReader = new GsGMLOGCReader("");
		baseReader.Begin(gml);
		GsGeometry g = baseReader.Read();
		GsGMLOGCWriter gmlWriter = new GsGMLOGCWriter();
		gmlWriter.Reset();
		gmlWriter.Write(g);
		String gml_p = gmlWriter.GML();
		Assert.assertEquals(gml,gml_p);
	}
	
	@Test
	public void line_test2(){
		String gml = "<gml:LineString><gml:coordinates>53.28751,121.00548 53.28738,121.00812 53.287788,121.01209 53.287178,121.03068 53.28677,121.04173"
				+ " 53.289009,121.04666 53.291149,121.05119 53.298786,121.05955 53.301941,121.06545 53.303978,121.07645 53.305405,121.08546 53.303368,"
				+ "121.09158 53.297462,121.09595 53.293388,121.10196 53.291962,121.10746 53.290539,121.11173 53.288807,121.11407 53.286873,121.11571 "
				+ "53.282085,121.11398 53.279335,121.1153 53.276382,121.11693 53.274448,121.1208 53.273224,121.12477 53.273735,121.12915 53.275059,"
				+ "121.13363 53.27964,121.1431 53.280964,121.15054 53.279335,121.15939 53.278828,121.1655 53.279541,121.17284 53.279846,121.18424 "
				+ "53.278419,121.19972 53.278011,121.20664 53.277298,121.21601 53.277096,121.22456 53.278011,121.23312 53.280964,121.24025 53.28524,"
				+ "121.24768 53.288197,121.2544 53.287991,121.26316 53.286362,121.27141 53.287277,121.28017 53.291351,121.29239 53.298786,121.3043 "
				+ "53.31131,121.31306 53.318848,121.32426 53.321392,121.33333 53.323124,121.34269 53.323326,121.35165 53.319763,121.37314 53.316097,"
				+ "121.38995 53.316097,121.3961 53.316402,121.40349 53.318542,121.41219 53.320782,121.42059 53.320782,121.42513 53.320171,121.43118 "
				+ "53.319458,121.43689 53.321289,121.44417 53.322514,121.44906 53.324142,121.45354 53.325668,121.45955 53.327503,121.46565 53.328724,"
				+ "121.46953 53.330254,121.47391 53.331779,121.48154 53.332596,121.48827 53.332649,121.48844</gml:coordinates></gml:LineString>";
		GsGMLOGCReader baseReader = new GsGMLOGCReader("");
		baseReader.Begin(gml);
		GsGeometry g = baseReader.Read();
		GsGML3OGCWriter gmlWriter = new GsGML3OGCWriter();
		gmlWriter.Reset();
		gmlWriter.Write(g);
		String gml_p = gmlWriter.GML();
		String gml3 = "<gml:LineString><gml:posList>53.28751 121.00548 53.28738 121.00812 53.287788 121.01209 53.287178 121.03068 53.28677 121.04173 53.289009 "
				+ "121.04666 53.291149 121.05119 53.298786 121.05955 53.301941 121.06545 53.303978 121.07645 53.305405 121.08546 53.303368 121.09158 53.297462"
				+ " 121.09595 53.293388 121.10196 53.291962 121.10746 53.290539 121.11173 53.288807 121.11407 53.286873 121.11571 53.282085 121.11398 53.279335 "
				+ "121.1153 53.276382 121.11693 53.274448 121.1208 53.273224 121.12477 53.273735 121.12915 53.275059 121.13363 53.27964 121.1431 53.280964"
				+ " 121.15054 53.279335 121.15939 53.278828 121.1655 53.279541 121.17284 53.279846 121.18424 53.278419 121.19972 53.278011 121.20664 53.277298"
				+ " 121.21601 53.277096 121.22456 53.278011 121.23312 53.280964 121.24025 53.28524 121.24768 53.288197 121.2544 53.287991 121.26316 53.286362 "
				+ "121.27141 53.287277 121.28017 53.291351 121.29239 53.298786 121.3043 53.31131 121.31306 53.318848 121.32426 53.321392 121.33333 53.323124 "
				+ "121.34269 53.323326 121.35165 53.319763 121.37314 53.316097 121.38995 53.316097 121.3961 53.316402 121.40349 53.318542 121.41219 53.320782 "
				+ "121.42059 53.320782 121.42513 53.320171 121.43118 53.319458 121.43689 53.321289 121.44417 53.322514 121.44906 53.324142 121.45354 53.325668 "
				+ "121.45955 53.327503 121.46565 53.328724 121.46953 53.330254 121.47391 53.331779 121.48154 53.332596 121.48827 53.332649 121.48844</gml:posList></gml:LineString>";
		Assert.assertEquals(gml3,gml_p);
	}
	
	@Test
	public void multiline_test1(){
		String gml = "<gml:MultiLineString><gml:lineStringMember><gml:LineString><gml:coordinates>53.28751,121.00548 53.28738,121.00812 53.287788,123.002</gml:coordinates>"
				+ "</gml:LineString></gml:lineStringMember><gml:lineStringMember><gml:LineString><gml:coordinates>53.330254,121.47391 53.331779,121.48154 53.332596,121.48827 53.332649,121.48844"
				+ "</gml:coordinates></gml:LineString></gml:lineStringMember></gml:MultiLineString>";
		GsGMLOGCReader baseReader = new GsGMLOGCReader("");
		baseReader.Begin(gml);
		GsGeometry g = baseReader.Read();
		GsGMLOGCWriter gmlWriter = new GsGMLOGCWriter();
		gmlWriter.Reset();
		gmlWriter.Write(g);
		String gml_p = gmlWriter.GML();
		Assert.assertEquals(gml,gml_p);
	}
	
	@Test
	public void multiline_test2(){
		String gml = "<gml:MultiLineString><gml:lineStringMember><gml:LineString><gml:coordinates>53.28751,121.00548 53.28738,121.00812 53.287788,123.002</gml:coordinates>"
				+ "</gml:LineString></gml:lineStringMember><gml:lineStringMember><gml:LineString><gml:coordinates>53.330254,121.47391 53.331779,121.48154 53.332596,121.48827 53.332649,121.48844"
				+ "</gml:coordinates></gml:LineString></gml:lineStringMember></gml:MultiLineString>";
		GsGMLOGCReader baseReader = new GsGMLOGCReader("");
		baseReader.Begin(gml);
		GsGeometry g = baseReader.Read();
		GsGML3OGCWriter gmlWriter = new GsGML3OGCWriter();
		gmlWriter.Reset();
		gmlWriter.Write(g);
		String gml_p = gmlWriter.GML();
		String gml3 = "<gml:MultiLineString><gml:lineStringMember><gml:LineString><gml:posList>53.28751 121.00548 53.28738 121.00812 53.287788 123.002</gml:posList>"
				+ "</gml:LineString></gml:lineStringMember><gml:lineStringMember><gml:LineString><gml:posList>53.330254 121.47391 53.331779 121.48154 53.332596 121.48827 53.332649 121.48844"
				+ "</gml:posList></gml:LineString></gml:lineStringMember></gml:MultiLineString>";
		Assert.assertEquals(gml3,gml_p);
	}
	
	@Test
	public void polygon_test1(){
		String gml = "<gml:Polygon><gml:outerBoundaryIs><gml:LinearRing><gml:coordinates>-4027932.748063923,3900164.023267765 -4027919.518870803,"
				+ "3900046.28344895 -4027680.070475237,3900023.793820635 -4027669.487120736,3900173.283702945 -4027932.748063923,3900164.023267765"
				+ "</gml:coordinates></gml:LinearRing></gml:outerBoundaryIs></gml:Polygon>";
		GsGMLOGCReader baseReader = new GsGMLOGCReader("");
		baseReader.Begin(gml);
		GsGeometry g = baseReader.Read();
		GsGMLOGCWriter gmlWriter = new GsGMLOGCWriter();
		gmlWriter.Reset();
		gmlWriter.Write(g);
		String gml_p = gmlWriter.GML();
		Assert.assertEquals(gml,gml_p);
	}
	
	@Test
	public void polygon_test2(){
		String gml = "<gml:Polygon><gml:outerBoundaryIs><gml:LinearRing><gml:coordinates>-4027932.748063923,3900164.023267765 -4027919.518870803,"
				+ "3900046.28344895 -4027680.070475237,3900023.793820635 -4027669.487120736,3900173.283702945 -4027932.748063923,3900164.023267765"
				+ "</gml:coordinates></gml:LinearRing></gml:outerBoundaryIs></gml:Polygon>";
		GsGMLOGCReader baseReader = new GsGMLOGCReader("");
		baseReader.Begin(gml);
		GsGeometry g = baseReader.Read();
		GsGML3OGCWriter gmlWriter = new GsGML3OGCWriter();
		gmlWriter.Reset();
		gmlWriter.Write(g);
		String gml_p = gmlWriter.GML();
		String gml3 = "<gml:Polygon><gml:exterior><gml:LinearRing><gml:posList>-4027932.748063923 3900164.023267765 -4027919.518870803 3900046.28344895 "
				+ "-4027680.070475237 3900023.793820635 -4027669.487120736 3900173.283702945 -4027932.748063923 3900164.023267765</gml:posList>"
				+ "</gml:LinearRing></gml:exterior></gml:Polygon>";
		Assert.assertEquals(gml3,gml_p);
	}
	
	@Test
	public void multipolygon_test1(){
		String gml = "<gml:MultiPolygon><gml:polygonMember><gml:Polygon><gml:exterior><gml:LinearRing><gml:posList>"+
	            "89.36102119958275 -11.499275407075515 89.36093791844752 -11.500013039987323 89.36203247051037 -11.500572213323778 89.3618778055449 -11.499263509770515 89.36102119958275 -11.499275407075515"+ 
	          "</gml:posList></gml:LinearRing></gml:exterior></gml:Polygon></gml:polygonMember><gml:polygonMember><gml:Polygon><gml:exterior><gml:LinearRing><gml:posList>"+
	            "89.36297235760759 -11.499215920550398 89.36277010342212 -11.500631699848896 89.36419778002573 -11.501060002829888 89.36582771081464 -11.501202770490238 89.36652965181133 -11.49928730438063 89.36297235760759 -11.499215920550398"+ 
	          "</gml:posList></gml:LinearRing></gml:exterior></gml:Polygon></gml:polygonMember><gml:polygonMember><gml:Polygon><gml:exterior><gml:LinearRing><gml:posList>"+
	            "89.36340066058858 -11.499620428921446 89.36425726655074 -11.499739401971738 89.3641026015855 -11.500631699848896 89.36331737945352 -11.500393753748313 89.36340066058858 -11.499620428921446 "+
	          "</gml:posList></gml:LinearRing></gml:exterior></gml:Polygon></gml:polygonMember><gml:polygonMember><gml:Polygon><gml:exterior><gml:LinearRing><gml:posList>"+
	           " 89.36498300215766 -11.499786991191854 89.36597047847494 -11.499882169632087 89.36558976471412 -11.500964824389655 89.36457849378667 -11.500584110628779 89.36498300215766 -11.499786991191854 "+
	          "</gml:posList></gml:LinearRing></gml:exterior></gml:Polygon></gml:polygonMember></gml:MultiPolygon>";
		GsGMLOGCReader baseReader = new GsGMLOGCReader("");
		baseReader.Begin(gml);
		GsGeometry g = baseReader.Read();
		GsGMLOGCWriter gmlWriter = new GsGMLOGCWriter();
		gmlWriter.Reset();
		gmlWriter.Write(g);
		String gml_p = gmlWriter.GML();
		String g1 = "<gml:polygonMember><gml:Polygon><gml:outerBoundaryIs><gml:LinearRing><gml:coordinates>89.36297235760759,-11.4992159205504 89.36277010342212,-11.5006316998489"
				+ " 89.36419778002573,-11.50106000282989 89.36582771081464,-11.50120277049024 89.36652965181133,-11.49928730438063 89.36297235760759,-11.4992159205504</gml:coordinates></gml:LinearRing>"
				+ "</gml:outerBoundaryIs><gml:innerBoundaryIs><gml:LinearRing><gml:coordinates>89.36340066058858,-11.49962042892145 89.36425726655074,-11.49973940197174 89.3641026015855,-11.5006316998489"
				+ " 89.36331737945352,-11.50039375374831 89.36340066058858,-11.49962042892145</gml:coordinates></gml:LinearRing></gml:innerBoundaryIs><gml:innerBoundaryIs><gml:LinearRing><gml:coordinates>"
				+ "89.36498300215766,-11.49978699119185 89.36597047847494,-11.49988216963209 89.36558976471412,-11.50096482438966 89.36457849378667,-11.50058411062878 89.36498300215766,-11.49978699119185"
				+ "</gml:coordinates></gml:LinearRing></gml:innerBoundaryIs></gml:Polygon></gml:polygonMember>";
		boolean bg1 = gml_p.contains(g1);
		//Assert.assertTrue(bg1);
	}

}
