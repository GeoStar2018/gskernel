package com.geostar.kernel;

import org.junit.Before;
import org.junit.Test;

import com.geostar.kernel.GsGeometry;
import com.geostar.kernel.GsGeometryType;
import com.geostar.kernel.GsPolygon;
import com.geostar.kernel.GsRawPoint;
import com.geostar.kernel.GsRing;
import com.geostar.kernel.spatialanalysis.GsAnalysisDataIO;
import com.geostar.kernel.spatialanalysis.GsTopologyCheckAnalysis;
import com.geostar.kernel.spatialanalysis.GsTopologyRuleType;
import com.geostar.kernel.spatialanalysis.GsTopologyRuleValidateResult;
import com.geostar.kernel.spatialanalysis.GsTopologyRuleValidator;
import com.geostar.kernel.spatialanalysis.GsTopologyRuleValidator_OnValidateProgress;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 拓扑检查
 *
 * @author songlei
 * @date 2019/6/11
 */
public class GeoTopologyTest  {

    @Before
    public void init() {
		System.out.println("Before loadlibrary");
		System.loadLibrary("gsjavaport");
		System.out.println("After loadlibrary");
    }

    
    
    @Test
    public void testRead()
    {
    	
    	String strjson = "{\"type\":\"Feature\",\"properties\":{\"BCGDJFLY_DLTB_2010\":\"\",\"BCGDJFLY_DLTB_2011\":\"\",\"BGJLH_DLTB_2010\":\"\",\"BGJLH_DLTB_2011\":\"\",\"BGLX_DLTB_2010\":\"\",\"BGLX_DLTB_2011\":\"\",\"BGRQ_DLTB_2010\":\"\",\"BGRQ_DLTB_2011\":\"\",\"BGXW_DLTB_2010\":\"\",\"BGXW_DLTB_2011\":\"\",\"BSM_DLTB_2010\":48231,\"BSM_DLTB_2011\":80187,\"BZ_DLTB_2010\":\"\",\"BZ_DLTB_2011\":\"\",\"CSBSM_DLTB_2010\":\"\",\"CSBSM_DLTB_2011\":\"\",\"CSRQ_DLTB_2010\":\"\",\"CSRQ_DLTB_2011\":\"\",\"DLBMMX_DLTB_2010\":\"\",\"DLBMMX_DLTB_2011\":\"\",\"DLBMOLD_DLTB_2010\":\"\",\"DLBMOLD_DLTB_2011\":\"\",\"DLBM_DLTB_2010\":\"013\",\"DLBM_DLTB_2011\":\"013\",\"DLBZ_DLTB_2010\":\"\",\"DLBZ_DLTB_2011\":\"\",\"DLMC_DLTB_2010\":\"旱地\",\"DLMC_DLTB_2011\":\"旱地\",\"FHDM_DLTB_2010\":\"999\",\"FHDM_DLTB_2011\":\"\",\"Feature_OID\":1445,\"GDJSLX_DLTB_2010\":\"\",\"GDJSLX_DLTB_2011\":\"\",\"GDLX_DLTB_2010\":\"\",\"GDLX_DLTB_2011\":\"\",\"GDPDJ_DLTB_2010\":\"1\",\"GDPDJ_DLTB_2011\":\"1\",\"GXCT_DLTB_2010\":\"\",\"GXCT_DLTB_2011\":\"\",\"GXSJ1_DLTB_2010\":\"\",\"GXSJ1_DLTB_2011\":\"\",\"GXSJ_DLTB_2010\":\"\",\"GXSJ_DLTB_2011\":\"\",\"GXSM_DLTB_2010\":\"\",\"GXSM_DLTB_2011\":\"\",\"ISHD_DLTB_2010\":\"\",\"ISHD_DLTB_2011\":\"\",\"ISPRWY_DLTB_2010\":\"\",\"ISPRWY_DLTB_2011\":\"\",\"In_ID_DLTB_2010\":22889,\"In_ID_DLTB_2011\":9600,\"JSYDBS_DLTB_2010\":\"\",\"JSYDBS_DLTB_2011\":\"\",\"KCDLBM_DLTB_2010\":\"\",\"KCDLBM_DLTB_2011\":\"\",\"KCLX_DLTB_2010\":\"\",\"KCLX_DLTB_2011\":\"\",\"LSSYQBSM_DLTB_2010\":0,\"LSSYQBSM_DLTB_2011\":0,\"LSZDBSM_DLTB_2010\":0,\"LSZDBSM_DLTB_2011\":0,\"LXDWMJ_DLTB_2010\":0,\"LXDWMJ_DLTB_2011\":0,\"OID_0\":5215,\"OID_1\":12648,\"PZWH_DLTB_2010\":\"\",\"PZWH_DLTB_2011\":\"\",\"PZWJ_DLTB_2010\":\"\",\"PZWJ_DLTB_2011\":\"\",\"QSDWDM_DLTB_2010\":\"3203021512080004000\",\"QSDWDM_DLTB_2011\":\"3203021512080004000\",\"QSDWDM_OLD_DLTB_2010\":\"\",\"QSDWDM_OLD_DLTB_2011\":\"\",\"QSDWMC_DLTB_2010\":\"四组\",\"QSDWMC_DLTB_2011\":\"四组\",\"QSXZ_DLTB_2010\":\"31\",\"QSXZ_DLTB_2011\":\"31\",\"RKSJ_DLTB_2010\":\"\",\"RKSJ_DLTB_2011\":\"\",\"RKXH_DLTB_2010\":0,\"RKXH_DLTB_2011\":0,\"SSXT_DLTB_2010\":\"\",\"SSXT_DLTB_2011\":\"\",\"SZTFH_DLTB_2010\":\"I50H083107\",\"SZTFH_DLTB_2011\":\"\",\"Shape_Area_DLTB_2010\":2352.66288119,\"Shape_Area_DLTB_2011\":2352.66343923,\"Shape_Leng_DLTB_2010\":208.096328877,\"Shape_Leng_DLTB_2011\":208.096289623,\"TBBH_DLTB_2010\":\"118\",\"TBBH_DLTB_2011\":\"118\",\"TBDLMJ_DLTB_2010\":2319.29,\"TBDLMJ_DLTB_2011\":2319.29,\"TBMJ_DLTB_2010\":2352.61,\"TBMJ_DLTB_2011\":2352.61,\"TBYBH_DLTB_2010\":\"118\",\"TBYBH_DLTB_2011\":\"118\",\"TDFLBM_DLTB_2010\":\"\",\"TDFLBM_DLTB_2011\":\"\",\"TKMJ_DLTB_2010\":0,\"TKMJ_DLTB_2011\":0,\"TKXS_DLTB_2010\":0,\"TKXS_DLTB_2011\":0,\"TSTYBM_DLTB_2010\":\"\",\"TSTYBM_DLTB_2011\":\"\",\"XWBSM_DLTB_2010\":\"\",\"XWBSM_DLTB_2011\":\"\",\"XWRQ_DLTB_2010\":\"\",\"XWRQ_DLTB_2011\":\"\",\"XZDWMJ_DLTB_2010\":33.32,\"XZDWMJ_DLTB_2011\":33.32,\"XZGDLX_DLTB_2010\":\"\",\"XZGDLX_DLTB_2011\":\"\",\"XZJSYDLX_DLTB_2010\":\"\",\"XZJSYDLX_DLTB_2011\":\"\",\"XZQDM_DLTB_2010\":\"\",\"XZQDM_DLTB_2011\":\"\",\"YSDM_DLTB_2010\":\"2001010100\",\"YSDM_DLTB_2011\":\"2001010100\",\"YSDM_OLD_DLTB_2010\":\"\",\"YSDM_OLD_DLTB_2011\":\"\",\"ZDBL_DLTB_2010\":0,\"ZDBL_DLTB_2011\":0,\"ZHS_DLTB_2010\":\"\",\"ZHS_DLTB_2011\":\"\",\"ZLDWDM_DLTB_2010\":\"3203021512080004000\",\"ZLDWDM_DLTB_2011\":\"3203021512080004000\",\"ZLDWDM_OLD_DLTB_2010\":\"\",\"ZLDWDM_OLD_DLTB_2011\":\"\",\"ZLDWMC_DLTB_2010\":\"四组\",\"ZLDWMC_DLTB_2011\":\"四组\",\"ZRCM_DLTB_2010\":\"\",\"ZRCM_DLTB_2011\":\"\",\"ZXBL_DLTB_2010\":0,\"ZXBL_DLTB_2011\":0},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[39530102.764011,3795440.19700023],[39530119.9080227,3795365.584988665],[39530150.0940069,3795391.62297793],[39530138.67201305,3795445.769001095],[39530135.79198985,3795445.287976375],[39530102.764011,3795440.19700023]]]}}";

    	//String strjson = "";
    	long t = System.currentTimeMillis();
    	GsGeoJSONOGCReader reader = new GsGeoJSONOGCReader("");
    	
    	for(int i =0; i < 1000000; i ++)
    	{
    		reader.Begin(strjson);
    		GsGeometry geo = reader.Read();
    		
    		
    	}
    	long t1 = System.currentTimeMillis();
    	System.out.println(t1 - t);
    	
    }
    //点拓扑

    //点必须被要素边界覆盖
    @Test
    public void point_CoveredByAreaBoundary_TopologyTest(){
        List<GeometryPair> geometryPairssA = new ArrayList<GeometryPair>();
        List<GeometryPair> geometryPairssB = new ArrayList<GeometryPair>();
        geometryPairssA.add(new GeometryPair(createPoint1(),0));
        geometryPairssB.add(new GeometryPair(createPolygon1(),1));
        List<TopologyResult> topologyResults = executeCheck(geometryPairssA, geometryPairssB, GsTopologyRuleType.eTRT_PointCoveredByAreaBoundary, GsGeometryType.eGeometryTypePoint,GsGeometryType.eGeometryTypePolygon,GsGeometryType.eGeometryTypePoint);
        if(topologyResults.size()>0){
            Assert.assertTrue(true);
        }else{
            Assert.assertTrue(false);
        }
    }

    //点必须被线要素覆盖
    @Test
    public void point_PointCoveredByLine_TopologyTest(){
        List<GeometryPair> geometryPairssA = new ArrayList<GeometryPair>();
        List<GeometryPair> geometryPairssB = new ArrayList<GeometryPair>();
        geometryPairssA.add(new GeometryPair(createPoint1(),0));
        geometryPairssB.add(new GeometryPair(createLine1(),1));
        List<TopologyResult> topologyResults = executeCheck(geometryPairssA, geometryPairssB, GsTopologyRuleType.eTRT_PointCoveredByLine, GsGeometryType.eGeometryTypePoint, GsGeometryType.eGeometryTypePath, GsGeometryType.eGeometryTypePath);
        if(topologyResults.size()>0){
            Assert.assertTrue(true);
        }else{
            Assert.assertTrue(false);
        }
    }

    //点必须被其他要素端点覆盖
    @Test
    public void point_PointCoveredByLineEndpoint_TopologyTest(){
        List<GeometryPair> geometryPairssA = new ArrayList<GeometryPair>();
        List<GeometryPair> geometryPairssB = new ArrayList<GeometryPair>();
        geometryPairssA.add(new GeometryPair(createPoint1(),0));
        geometryPairssB.add(new GeometryPair(createLine1(),1));
        List<TopologyResult> topologyResults = executeCheck(geometryPairssA, geometryPairssB, GsTopologyRuleType.eTRT_PointCoveredByLineEndpoint, GsGeometryType.eGeometryTypePoint, GsGeometryType.eGeometryTypePath, GsGeometryType.eGeometryTypePoint);
        if(topologyResults.size()>0){
            Assert.assertTrue(true);
        }else{
            Assert.assertTrue(false);
        }
    }

    //点必须被线覆盖
    @Test
    public void point_PointMustOnLineLineIntersection_TopologyTest(){
        List<GeometryPair> geometryPairssA = new ArrayList<GeometryPair>();
        List<GeometryPair> geometryPairssB = new ArrayList<GeometryPair>();
        geometryPairssA.add(new GeometryPair(createPoint1(),0));
        geometryPairssB.add(new GeometryPair(createLine1(),1));
        List<TopologyResult> topologyResults = executeCheck(geometryPairssA, geometryPairssB, GsTopologyRuleType.eTRT_PointMustOnLineLineIntersection, GsGeometryType.eGeometryTypePoint, GsGeometryType.eGeometryTypePath, GsGeometryType.eGeometryTypePoint);
        if(topologyResults.size()>0){
            Assert.assertTrue(true);
        }else{
            Assert.assertTrue(false);
        }
    }

    //点必须完全位于面内部
    @Test
    public void point_PointProperlyInsideArea_TopologyTest(){
        List<GeometryPair> geometryPairssA = new ArrayList<GeometryPair>();
        List<GeometryPair> geometryPairssB = new ArrayList<GeometryPair>();
        geometryPairssA.add(new GeometryPair(createGsGeometry("POINT(-1 -1)"),0));
		geometryPairssB.add(new GeometryPair(createGsGeometry("POLYGON((0 0,10 0,10 10,0 10,0 0))"),1));
        
		//geometryPairssA.add(new GeometryPair(createPoint1(),0));
        //geometryPairssB.add(new GeometryPair(createPolygon1(),1));
        List<TopologyResult> topologyResults = executeCheck(geometryPairssA, geometryPairssB, GsTopologyRuleType.eTRT_PointProperlyInsideArea, GsGeometryType.eGeometryTypePoint, GsGeometryType.eGeometryTypePolygon, GsGeometryType.eGeometryTypePoint);
        if(topologyResults.size()>0){
            Assert.assertTrue(true);
        }else{
            Assert.assertTrue(false);
        }
    }

    //线拓扑

    //线必须被其他要素边界覆盖
    @Test
    public void line_LineCoveredByAreaBoundary_TopologyTest(){
        List<GeometryPair> geometryPairssA = new ArrayList<GeometryPair>();
        List<GeometryPair> geometryPairssB = new ArrayList<GeometryPair>();
        geometryPairssA.add(new GeometryPair(createPoint1(),0));
        geometryPairssB.add(new GeometryPair(createPolygon1(),1));
        List<TopologyResult> topologyResults = executeCheck(geometryPairssA, geometryPairssB, GsTopologyRuleType.eTRT_LineCoveredByAreaBoundary, GsGeometryType.eGeometryTypePath, GsGeometryType.eGeometryTypePolygon, GsGeometryType.eGeometryTypePath);
        if(topologyResults.size()>0){
            Assert.assertTrue(true);
        }else{
            Assert.assertTrue(false);
        }
    }

    //线必须被其他要素类覆盖
    @Test
    public void line_LineCoveredByLineClass_TopologyTest(){
        List<GeometryPair> geometryPairssA = new ArrayList<GeometryPair>();
        List<GeometryPair> geometryPairssB = new ArrayList<GeometryPair>();
        geometryPairssA.add(new GeometryPair(createLine1(),0));
        geometryPairssB.add(new GeometryPair(createLine1(),1));
        List<TopologyResult> topologyResults = executeCheck(geometryPairssA, geometryPairssB, GsTopologyRuleType.eTRT_LineCoveredByLineClass, GsGeometryType.eGeometryTypePath, GsGeometryType.eGeometryTypePath, GsGeometryType.eGeometryTypePath);
        if(topologyResults.size()>0){
            Assert.assertTrue(true);
        }else{
            Assert.assertTrue(false);
        }
    }

    //线端点必须被其他要素类覆盖
    @Test
    public void line_LineEndpointCoveredByPoint_TopologyTest(){
        List<GeometryPair> geometryPairssA = new ArrayList<GeometryPair>();
        List<GeometryPair> geometryPairssB = new ArrayList<GeometryPair>();
        geometryPairssA.add(new GeometryPair(createLine1(),0));
        geometryPairssB.add(new GeometryPair(createPoint1(),1));
        List<TopologyResult> topologyResults = executeCheck(geometryPairssA, geometryPairssB, GsTopologyRuleType.eTRT_LineEndpointCoveredByPoint, GsGeometryType.eGeometryTypePath, GsGeometryType.eGeometryTypePoint, GsGeometryType.eGeometryTypePoint);
        if(topologyResults.size()>0){
            Assert.assertTrue(true);
        }else{
            Assert.assertTrue(false);
        }
    }

    //线不能有悬挂点
    @Test
    public void line_LineNoDangles_TopologyTest(){
        List<GeometryPair> geometryPairssA = new ArrayList<GeometryPair>();
        List<GeometryPair> geometryPairssB = new ArrayList<GeometryPair>();
        geometryPairssA.add(new GeometryPair(createLine1(),0));
        geometryPairssB.add(new GeometryPair(createLine2(),1));
        List<TopologyResult> topologyResults = executeCheck(geometryPairssA, geometryPairssB, GsTopologyRuleType.eTRT_LineNoDangles, GsGeometryType.eGeometryTypePath, GsGeometryType.eGeometryTypePath, GsGeometryType.eGeometryTypePoint);
        if(topologyResults.size()>0){
            Assert.assertTrue(true);
        }else{
            Assert.assertTrue(false);
        }
    }

    //线不能自重叠
    @Test
    public void line_LineNoIntersection_TopologyTest(){
        List<GeometryPair> geometryPairssA = new ArrayList<GeometryPair>();
        List<GeometryPair> geometryPairssB = new ArrayList<GeometryPair>();
        geometryPairssA.add(new GeometryPair(createLine1(),0));
        geometryPairssB.add(new GeometryPair(createLine2(),1));
        List<TopologyResult> topologyResults = executeCheck(geometryPairssA, geometryPairssB, GsTopologyRuleType.eTRT_LineNoIntersection, GsGeometryType.eGeometryTypePath, GsGeometryType.eGeometryTypePath, GsGeometryType.eGeometryTypePath);
        if(topologyResults.size()>0){
            Assert.assertTrue(true);
        }else{
            Assert.assertTrue(false);
        }
    }

    //线不能自相交
    @Test
    public void line_LineNoIntersectOrInteriorTouch_TopologyTest(){
        List<GeometryPair> geometryPairssA = new ArrayList<GeometryPair>();
        List<GeometryPair> geometryPairssB = new ArrayList<GeometryPair>();
        geometryPairssA.add(new GeometryPair(createLine1(),0));
        geometryPairssB.add(new GeometryPair(createLine2(),1));
        List<TopologyResult> topologyResults = executeCheck(geometryPairssA, geometryPairssB, GsTopologyRuleType.eTRT_LineNoIntersectOrInteriorTouch, GsGeometryType.eGeometryTypePath, GsGeometryType.eGeometryTypePath, GsGeometryType.eGeometryTypePoint);
        if(topologyResults.size()>0){
            Assert.assertTrue(true);
        }else{
            Assert.assertTrue(false);
        }
    }

    //线必须为单一部分
    @Test
    public void line_LineNoMultipart_TopologyTest(){
        List<GeometryPair> geometryPairssA = new ArrayList<GeometryPair>();
        List<GeometryPair> geometryPairssB = new ArrayList<GeometryPair>();
        geometryPairssA.add(new GeometryPair(createLine1(),0));
        geometryPairssB.add(new GeometryPair(createLine2(),1));
        List<TopologyResult> topologyResults = executeCheck(geometryPairssA, geometryPairssB, GsTopologyRuleType.eTRT_LineNoMultipart, GsGeometryType.eGeometryTypePath, GsGeometryType.eGeometryTypePath, GsGeometryType.eGeometryTypePath);
        if(topologyResults.size()>0){
            Assert.assertTrue(true);
        }else{
            Assert.assertTrue(false);
        }
    }

    //线不能自重叠
    @Test
    public void line_LineNoOverlap_TopologyTest(){
        List<GeometryPair> geometryPairssA = new ArrayList<GeometryPair>();
        List<GeometryPair> geometryPairssB = new ArrayList<GeometryPair>();
        geometryPairssA.add(new GeometryPair(createLine1(),0));
        geometryPairssB.add(new GeometryPair(createLine2(),1));
        List<TopologyResult> topologyResults = executeCheck(geometryPairssA, geometryPairssB, GsTopologyRuleType.eTRT_LineNoOverlap, GsGeometryType.eGeometryTypePath, GsGeometryType.eGeometryTypePath, GsGeometryType.eGeometryTypePath);
        if(topologyResults.size()>0){
            Assert.assertTrue(true);
        }else{
            Assert.assertTrue(false);
        }
    }

    //线不能重叠
    @Test
    public void line_LineNoOverlapLine_TopologyTest(){
        List<GeometryPair> geometryPairssA = new ArrayList<GeometryPair>();
        List<GeometryPair> geometryPairssB = new ArrayList<GeometryPair>();
        geometryPairssA.add(new GeometryPair(createLine1(),0));
        geometryPairssB.add(new GeometryPair(createLine2(),1));
        List<TopologyResult> topologyResults = executeCheck(geometryPairssA, geometryPairssB, GsTopologyRuleType.eTRT_LineNoOverlapLine, GsGeometryType.eGeometryTypePath, GsGeometryType.eGeometryTypePath, GsGeometryType.eGeometryTypePath);
        if(topologyResults.size()>0){
            Assert.assertTrue(true);
        }else{
            Assert.assertTrue(false);
        }
    }

    //线不能有伪节点
    @Test
    public void line_LineNoPseudos_TopologyTest(){
        List<GeometryPair> geometryPairssA = new ArrayList<GeometryPair>();
        List<GeometryPair> geometryPairssB = new ArrayList<GeometryPair>();
        geometryPairssA.add(new GeometryPair(createLine1(),0));
        geometryPairssB.add(new GeometryPair(createLine2(),1));
        List<TopologyResult> topologyResults = executeCheck(geometryPairssA, geometryPairssB, GsTopologyRuleType.eTRT_LineNoPseudos, GsGeometryType.eGeometryTypePath, GsGeometryType.eGeometryTypePath, GsGeometryType.eGeometryTypePoint);
        if(topologyResults.size()>0){
            Assert.assertTrue(true);
        }else{
            Assert.assertTrue(false);
        }
    }

    //线不能自相交
    @Test
    public void line_LineNoSelfIntersect_TopologyTest(){
        List<GeometryPair> geometryPairssA = new ArrayList<GeometryPair>();
        List<GeometryPair> geometryPairssB = new ArrayList<GeometryPair>();
        geometryPairssA.add(new GeometryPair(createLine1(),0));
        geometryPairssB.add(new GeometryPair(createLine2(),1));
        List<TopologyResult> topologyResults = executeCheck(geometryPairssA, geometryPairssB, GsTopologyRuleType.eTRT_LineNoSelfIntersect, GsGeometryType.eGeometryTypePath, GsGeometryType.eGeometryTypePath, GsGeometryType.eGeometryTypePoint);
        if(topologyResults.size()>0){
            Assert.assertTrue(true);
        }else{
            Assert.assertTrue(false);
        }
    }

    //线不能自重叠
    @Test
    public void line_LineNoSelfOverlap_TopologyTest(){
        List<GeometryPair> geometryPairssA = new ArrayList<GeometryPair>();
        List<GeometryPair> geometryPairssB = new ArrayList<GeometryPair>();
        geometryPairssA.add(new GeometryPair(createLine1(),0));
        geometryPairssB.add(new GeometryPair(createLine2(),1));
        List<TopologyResult> topologyResults = executeCheck(geometryPairssA, geometryPairssB, GsTopologyRuleType.eTRT_LineNoSelfOverlap, GsGeometryType.eGeometryTypePath, GsGeometryType.eGeometryTypePath, GsGeometryType.eGeometryTypePath);
        if(topologyResults.size()>0){
            Assert.assertTrue(true);
        }else{
            Assert.assertTrue(false);
        }
    }

    //面拓扑

    //面不能重叠
    @Test
    public void polygon_AreaNoOverlapArea_TopologyTest() {
        List<GeometryPair> geometryPairssA = new ArrayList<GeometryPair>();
        List<GeometryPair> geometryPairssB = new ArrayList<GeometryPair>();
        geometryPairssA.add(new GeometryPair(createPolygon1(),0));
        geometryPairssB.add(new GeometryPair(createPolygon2(),1));
        List<TopologyResult> topologyResults = executeCheck(geometryPairssA, geometryPairssB, GsTopologyRuleType.eTRT_AreaNoOverlapArea,GsGeometryType.eGeometryTypePolygon ,GsGeometryType.eGeometryTypePolygon,GsGeometryType.eGeometryTypePolygon);
        if(topologyResults.size()>0){
            Assert.assertTrue(true);
        }else{
            Assert.assertTrue(false);
        }
    }

    //面边界必须被其他要素边界覆盖
    @Test
    public void polygon_AreaBoundaryCoveredByAreaBoundary_TopologyTest() {
        List<GeometryPair> geometryPairssA = new ArrayList<GeometryPair>();
        List<GeometryPair> geometryPairssB = new ArrayList<GeometryPair>();
        geometryPairssA.add(new GeometryPair(createPolygon1(),0));
        geometryPairssB.add(new GeometryPair(createPolygon2(),1));
        List<TopologyResult> topologyResults = executeCheck(geometryPairssA, geometryPairssB, GsTopologyRuleType.eTRT_AreaBoundaryCoveredByAreaBoundary ,GsGeometryType.eGeometryTypePolygon ,GsGeometryType.eGeometryTypePolygon,GsGeometryType.eGeometryTypePath);
        if(topologyResults.size()>0){
            Assert.assertTrue(true);
        }else{
            Assert.assertTrue(false);
        }
    }

    //面边界必须被其他要素覆盖
    @Test
    public void polygon_AreaBoundaryCoveredByLine_TopologyTest() {
        List<GeometryPair> geometryPairssA = new ArrayList<GeometryPair>();
        List<GeometryPair> geometryPairssB = new ArrayList<GeometryPair>();
        geometryPairssA.add(new GeometryPair(createPolygon1(),0));
        geometryPairssB.add(new GeometryPair(createLine1(),1));
        List<TopologyResult> topologyResults = executeCheck(geometryPairssA, geometryPairssB, GsTopologyRuleType.eTRT_AreaBoundaryCoveredByLine ,GsGeometryType.eGeometryTypePolygon ,GsGeometryType.eGeometryTypePath,GsGeometryType.eGeometryTypePath);
        if(topologyResults.size()>0){
            Assert.assertTrue(true);
        }else{
            Assert.assertTrue(false);
        }
    }

    //面包含点
    @Test
    public void polygon_AreaContainPoint_TopologyTest() {
        List<GeometryPair> geometryPairssA = new ArrayList<GeometryPair>();
        List<GeometryPair> geometryPairssB = new ArrayList<GeometryPair>();
        geometryPairssA.add(new GeometryPair(createPolygon1(),0));
        geometryPairssB.add(new GeometryPair(createPoint1(),1));
        List<TopologyResult> topologyResults = executeCheck(geometryPairssA, geometryPairssB, GsTopologyRuleType.eTRT_AreaContainPoint ,GsGeometryType.eGeometryTypePolygon ,GsGeometryType.eGeometryTypePoint,GsGeometryType.eGeometryTypePolygon);
        if(topologyResults.size()>0){
            Assert.assertTrue(true);
        }else{
            Assert.assertTrue(false);
        }
    }

    //面必须相互覆盖
    @Test
    public void polygon_AreaCoverEachOther_TopologyTest() {
        List<GeometryPair> geometryPairssA = new ArrayList<GeometryPair>();
        List<GeometryPair> geometryPairssB = new ArrayList<GeometryPair>();
        geometryPairssA.add(new GeometryPair(createPolygon1(),0));
        geometryPairssB.add(new GeometryPair(createPolygon1(),1));
        List<TopologyResult> topologyResults = executeCheck(geometryPairssA, geometryPairssB, GsTopologyRuleType.eTRT_AreaCoverEachOther ,GsGeometryType.eGeometryTypePolygon ,GsGeometryType.eGeometryTypePolygon,GsGeometryType.eGeometryTypePolygon);
        if(topologyResults.size()>0){
            Assert.assertTrue(true);
        }else{
            Assert.assertTrue(false);
        }
    }

    //面必须被其他要素覆盖
    @Test
    public void polygon_AreaCoveredByArea_TopologyTest() {
        List<GeometryPair> geometryPairssA = new ArrayList<GeometryPair>();
        List<GeometryPair> geometryPairssB = new ArrayList<GeometryPair>();
        geometryPairssA.add(new GeometryPair(createPolygon1(),0));
        geometryPairssB.add(new GeometryPair(createPolygon1(),1));
        List<TopologyResult> topologyResults = executeCheck(geometryPairssA, geometryPairssB, GsTopologyRuleType.eTRT_AreaCoveredByArea ,GsGeometryType.eGeometryTypePolygon ,GsGeometryType.eGeometryTypePolygon,GsGeometryType.eGeometryTypePolygon);
        if(topologyResults.size()>0){
            Assert.assertTrue(true);
        }else{
            Assert.assertTrue(false);
        }
    }

    //面不能有空隙
    @Test
    public void polygon_AreaNoGap_TopologyTest() {
        List<GeometryPair> geometryPairssA = new ArrayList<GeometryPair>();
        List<GeometryPair> geometryPairssB = new ArrayList<GeometryPair>();
        geometryPairssA.add(new GeometryPair(createPolygon1(),0));
        geometryPairssB.add(new GeometryPair(createPolygon2(),1));
        List<TopologyResult> topologyResults = executeCheck(geometryPairssA, geometryPairssB, GsTopologyRuleType.eTRT_AreaNoGaps ,GsGeometryType.eGeometryTypePolygon ,GsGeometryType.eGeometryTypePolygon,GsGeometryType.eGeometryTypePolygon);
        if(topologyResults.size()>0){
            Assert.assertTrue(true);
        }else{
            Assert.assertTrue(false);
        }
    }

    //面必须被其他要素类的要素覆盖
    @Test
    public void polygon_AreaCoveredByAreaClass_TopologyTest() {
        List<GeometryPair> geometryPairssA = new ArrayList<GeometryPair>();
        List<GeometryPair> geometryPairssB = new ArrayList<GeometryPair>();
        geometryPairssA.add(new GeometryPair(createPolygon1(),0));
        geometryPairssB.add(new GeometryPair(createPolygon2(),1));
        List<TopologyResult> topologyResults = executeCheck(geometryPairssA, geometryPairssB, GsTopologyRuleType.eTRT_AreaCoveredByAreaClass ,GsGeometryType.eGeometryTypePolygon ,GsGeometryType.eGeometryTypePolygon,GsGeometryType.eGeometryTypePolygon);
        if(topologyResults.size()>0){
            Assert.assertTrue(true);
        }else{
            Assert.assertTrue(false);
        }
    }

    //面不能重叠
    @Test
    public void polygon_AreaNoOverlap_TopologyTest() {
        List<GeometryPair> geometryPairssA = new ArrayList<GeometryPair>();
        List<GeometryPair> geometryPairssB = new ArrayList<GeometryPair>();
        geometryPairssA.add(new GeometryPair(createPolygon1(),0));
        geometryPairssB.add(new GeometryPair(createPolygon2(),1));
        List<TopologyResult> topologyResults = executeCheck(geometryPairssA, geometryPairssB, GsTopologyRuleType.eTRT_AreaNoOverlap ,GsGeometryType.eGeometryTypePolygon ,GsGeometryType.eGeometryTypePolygon,GsGeometryType.eGeometryTypePolygon);
        if(topologyResults.size()>0){
            Assert.assertTrue(true);
        }else{
            Assert.assertTrue(false);
        }
    }
    /**
     * 执行拓扑检查
     */
    public List<TopologyResult> executeCheck(List<GeometryPair> geometryPairsA, List<GeometryPair> geometryPairsB, GsTopologyRuleType gsTopologyRuleType, GsGeometryType gsGeometryTypeA,GsGeometryType gsGeometryTypeB,GsGeometryType gsGeometryTypeWrite) {
        GeoTopologyReaderIO gsAnalysisDataIOA = new GeoTopologyReaderIO(geometryPairsA.iterator(), gsGeometryTypeA);
        GeoTopologyReaderIO gsAnalysisDataIOB = new GeoTopologyReaderIO(geometryPairsB.iterator(), gsGeometryTypeB);

        List<TopologyResult> results = new ArrayList<TopologyResult>();
        GsTopologyCheckAnalysis ptrTc = new GsTopologyCheckAnalysis();
        //创建拓扑检查规则
        GsTopologyRuleValidator ptrCheckRule = ptrTc.CreateTopologyRule(gsTopologyRuleType, 0.01);
        ptrCheckRule.AddData(gsAnalysisDataIOA);
        ptrCheckRule.AddData(gsAnalysisDataIOB);
        GeoTopologyWriterIO geoTopologyResultWriterIO = new GeoTopologyWriterIO(results, gsGeometryTypeWrite);
        ptrCheckRule.OutputData(geoTopologyResultWriterIO);
        GsTopologyRuleValidator_OnValidateProgress_Java gsOnValidateProgress = new GsTopologyRuleValidator_OnValidateProgress_Java();
        ptrCheckRule.OnValidateProgressAdd(gsOnValidateProgress);
        GsTopologyRuleValidateResult ret = ptrCheckRule.ValidateRule();
        System.out.println(ret);
        return results;
    }

    /**
     * 拓扑检查输入类
     *
     * @author songlei
     * @date 20181129
     */
    public class GeoTopologyReaderIO extends GsAnalysisDataIO {
        Iterator<GeometryPair> geometryPairs;
        GsGeometryType gsGeometryType;

        public GeoTopologyReaderIO(Iterator<GeometryPair> geometryPairs, GsGeometryType gsGeometryType) {
            this.geometryPairs = geometryPairs;
            this.gsGeometryType = gsGeometryType;
        }

        public int OnData(GsFeatureBuffer pData) {
            try {
                if (geometryPairs.hasNext()) {
                    GeometryPair next = geometryPairs.next();
                    GsGeometry geometry = next.getGeometry();
                    long id = next.getOid();
                    pData.SetGeometry(geometry);
                    pData.ID(id);
                    return 0;
                } else {
                    return -1;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }

        public int GeometryDimension() {
            if (gsGeometryType != null) {
                return GsGeometry.GeometryTypeDimension(gsGeometryType);
            }
            return -1;
        }
        }

    /**
     * 拓扑检查结果
     *
     * @author songlei
     * @date 20190611
     */
    public class TopologyResult {
        long oidA;
        long oidB;
        String topologyTypeDeail;

        public TopologyResult(long oidA, long oidB, String topologyTypeDeail) {
            this.oidA = oidA;
            this.oidB = oidB;
            this.topologyTypeDeail = topologyTypeDeail;
        }

        public long getOidA() {
            return oidA;
        }

        public long getOidB() {
            return oidB;
        }

        public String getTopologyTypeDeail() {
            return topologyTypeDeail;
        }
    }

    /**
     * 拓扑检查输出类
     *
     * @author sonlei
     * @date 20190611
     */
    public class GeoTopologyWriterIO extends GsAnalysisDataIO {
        List<TopologyResult> results;
        GsGeometryType gsGeometryType;

        public GeoTopologyWriterIO(List<TopologyResult> results, GsGeometryType gsGeometryType) {
            this.results = results;
            this.gsGeometryType = gsGeometryType;
        }

        public int OnData(GsFeatureBuffer pData) {
            try {
                long featureID_A = pData.Int64Value(2);
                long featureID_B = pData.Int64Value(4);
                String topologyTypeDeail = pData.StringValue(5);
                TopologyResult topologyResult = new TopologyResult(featureID_A, featureID_B, topologyTypeDeail);
                results.add(topologyResult);
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
            return 0;
        }

        public int GeometryDimension() {
            if (gsGeometryType != null) {
                return GsGeometry.GeometryTypeDimension(gsGeometryType);
            }
            return -1;
        }
    }

    /**
     * 拓扑检查结果类(包含几何与id)
     */
    public class GeometryPair {
        long oid;
        GsGeometry gsGeometry;

        public GeometryPair(GsGeometry gsGeometry,long oid){
            this.gsGeometry=gsGeometry;
            this.oid=oid;
        }
        public long getOid() {
            return oid;
        }

        public void setOid(long oid) {
            this.oid = oid;
        }

        public GsGeometry getGeometry() {
            return gsGeometry;
        }

        public void setGeometry(GsGeometry gsGeometry) {
            this.gsGeometry = gsGeometry;
        }
    }

    public class GsTopologyRuleValidator_OnValidateProgress_Java extends GsTopologyRuleValidator_OnValidateProgress {
        /// \brief 验证拓扑规则时的进度
        /// \details 参数意义
        /// \details 参数1 GsTopologyRuleValidator对象指针
        /// \details 参数2 执行步骤的名称
        /// \details 参数3 执行步骤
        /// \details 参数4 总的步骤数量
        /// \details 参数5 单个步骤的进度，范围[0~1]
        /// \details 返回值  返回true，会继续执行验证，返回false会中断执行
        public boolean OnValidateProgress(GsTopologyRuleValidator p, String log, int a, int b, float c) {
            //std::cout << log << a << b << c << std::endl;
            return true;
        }
    }

    //创建点数据
    public GsGeometry createGsGeometry(String wkt){
        GsWKTOGCReader gsWKTOGCReader=new GsWKTOGCReader(null);
        gsWKTOGCReader.Begin(wkt);
        return gsWKTOGCReader.Read();
    }

    public GsPoint  createPoint1() {
        GsPoint gsPoint = new GsPoint(0, 0);
        return gsPoint;
    }

    public GsPoint createPoint2() {
        GsPoint gsPoint = new GsPoint(1, 1);
        return gsPoint;
    }

    //创建线数据
    public GsPath createLine1() {
        GsPath gsPath = new GsPath();
        gsPath.Add(new GsRawPoint(0, 0));
        gsPath.Add(new GsRawPoint(5, 5));
        return gsPath;
    }

    //创建线数据
    public GsPath createLine2() {
        GsPath gsPath = new GsPath();
        gsPath.Add(new GsRawPoint(0, 0));
        gsPath.Add(new GsRawPoint(10, 10));
        return gsPath;
    }

    //创建面数据
    public GsPolygon createPolygon1() {
        GsPolygon gsPolygon = new GsPolygon();
        GsRing gsOutRing = new GsRing();
        gsOutRing.Add(new GsRawPoint(0, 0));
        gsOutRing.Add(new GsRawPoint(10, 0));
        gsOutRing.Add(new GsRawPoint(10, 10));
        gsOutRing.Add(new GsRawPoint(0, 10));
        gsOutRing.Add(new GsRawPoint    (0, 0));
        gsPolygon.Add(gsOutRing);
        return gsPolygon;
    }

    public GsPolygon createPolygon2() {
        GsPolygon gsPolygon = new GsPolygon();
        GsRing gsOutRing = new GsRing();
        gsOutRing.Add(new GsRawPoint(2, 2));
        gsOutRing.Add(new GsRawPoint(8, 2));
        gsOutRing.Add(new GsRawPoint(8, 8));
        gsOutRing.Add(new GsRawPoint(2, 8));
        gsOutRing.Add(new GsRawPoint(2, 2));
        gsPolygon.Add(gsOutRing);
        return gsPolygon;
    }
}
