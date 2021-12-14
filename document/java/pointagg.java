package ekwbtest;


import java.awt.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

import com.geostar.kernel.*;
import com.geostar.kernel.spatialanalysis.*;

public class pointagg {
	public static void main(String arg[])
	{	
		test(arg);
	}
	public static void testfcs(String arg[])
	{		
		System.loadLibrary("gsjavaport");
		com.geostar.kernel.GsKernel.Initialize();
		System.out.println("loadlibrary succeed");
		
        String epsgDataPath = "D:\\source\\44_KernelPort\\package\\data\\coordinatesystem\\EPSG.txt";
        GsGlobeConfig.Instance().Child("Kernel/SpatialReference/EPSG").Value(epsgDataPath);
        String uppath = "D:\\source\\44_KernelPort\\package\\data\\coordinatesystem";
        GsGlobeConfig.Instance().Child("Kernel/SpatialReference/DataFolder").Value(uppath);
		
		//数据准备
		String fcsFolder = "D:\\source\\kernel\\testdata\\400sqlite";

		GsSqliteGeoDatabaseFactory fac = new GsSqliteGeoDatabaseFactory();
		GsConnectProperty conn = new GsConnectProperty();
		conn.setServer(fcsFolder);
		GsGeoDatabase pDB = fac.Open(conn);

		GsFeatureClass pFcs = pDB.OpenFeatureClass("XIANCH_P");

		FeatureReader pIO1 = new FeatureReader(pFcs);

		//创建一个输出数据集
		GsGeometryColumnInfo g = pFcs.GeometryColumnInfo();

		GsFeatureClass mfd_PointFcs = pDB.CreateFeatureClass("XIANCH_P_PointGenerality_32_java", pFcs.Fields(), g, pFcs.SpatialReference());
		FeatureWriter writer = new FeatureWriter(mfd_PointFcs);

		//创建抽稀对象
		//补偿 
		GsPointGeneralityAnalysisParameters params = new GsPointGeneralityAnalysisParameters();
		params.setFieldIndex(pFcs.Fields().FindField("ESRI_NUM"));
		params.setGridSize( 32);	//分块给太多会慢和耗用大量内存,,如果一次搞不定可多次处理完成
		params.setType(GsPointGeneralityAttributeStatisticsType.eSum);
		double res = pFcs.Extent().Width() / params.getGridSize();
		GsGeneralityAnalysis ptrGenerality = new GsGeneralityAnalysis(GsGeneralityAnalysisType.eGPT_PointGenerality, res);//创建抽稀对象
		
		ptrGenerality.PointGeneralityParameters(params);
		ptrGenerality.Tolerance(res);
		ptrGenerality.AddData(pIO1);	//设置输入流    	
		ptrGenerality.OutputData(writer);//设置输出流
		Boolean bRes = ptrGenerality.Preprocess();//进行抽稀处理
		writer.Commit();
	} 
	
	public static void test(String arg[])
	{		
		System.loadLibrary("gsjavaport");
		com.geostar.kernel.GsKernel.Initialize();
		System.out.println("loadlibrary succeed");
		
        String epsgDataPath = "D:\\source\\44_KernelPort\\package\\data\\coordinatesystem\\EPSG.txt";
        GsGlobeConfig.Instance().Child("Kernel/SpatialReference/EPSG").Value(epsgDataPath);
        String uppath = "D:\\source\\44_KernelPort\\package\\data\\coordinatesystem";
        GsGlobeConfig.Instance().Child("Kernel/SpatialReference/DataFolder").Value(uppath);
		
		//数据准备
		String fcsFolder = "D:\\source\\kernel\\testdata\\400sqlite";

		GsSqliteGeoDatabaseFactory fac = new GsSqliteGeoDatabaseFactory();
		GsConnectProperty conn = new GsConnectProperty();
		conn.setServer(fcsFolder);
		GsGeoDatabase pDB = fac.Open(conn);

		GsFeatureClass pFcs = pDB.OpenFeatureClass("XIANCH_P");

		MemoryFeatureReader  pIO1 = new MemoryFeatureReader(120);

		//创建一个输出数据集
		GsGeometryColumnInfo g = pFcs.GeometryColumnInfo();

		GsFields fdsFields = new GsFields();
		GsFieldVector fdsFieldVector = new  GsFieldVector();
		GsField fdField1 = new GsField("OID", GsFieldType.eInt64Type);
		fdsFieldVector.add(fdField1);
		GsField fdField2 = new GsField("GEOMETRY", GsFieldType.eGeometryType);
		fdsFieldVector.add(fdField2);
		GsField fdField = new GsField("a", GsFieldType.eDoubleType);
		fdsFieldVector.add(fdField);
		fdsFields.setFields(fdsFieldVector);
		
		
		GsFeatureClass mfd_PointFcs = pDB.CreateFeatureClass("XIANCH_P_memory_32_java", fdsFields, g, pFcs.SpatialReference());
		FeatureWriter writer = new FeatureWriter(mfd_PointFcs);

		//创建抽稀对象
		//补偿 
		GsPointGeneralityAnalysisParameters params = new GsPointGeneralityAnalysisParameters();
		params.setFieldIndex(2);
		params.setGridSize(32);	//分块给太多会慢和耗用大量内存,,如果一次搞不定可多次处理完成
		params.setType(GsPointGeneralityAttributeStatisticsType.eSum);
		double res = 2;
		GsGeneralityAnalysis ptrGenerality = new GsGeneralityAnalysis(GsGeneralityAnalysisType.eGPT_PointGenerality, res);//创建抽稀对象
		
		ptrGenerality.PointGeneralityParameters(params);
		ptrGenerality.Tolerance(res);
		ptrGenerality.AddData(pIO1);	//设置输入流    	
		ptrGenerality.OutputData(writer);//设置输出流
		Boolean bRes = ptrGenerality.Preprocess();//进行抽稀处理
		writer.Commit();
	} 
}
class FeatureWriter extends GsAnalysisDataIO{
	GsFeatureClass m_feaClass;
	String m_Name;
	int m_Index  = 0;
	public FeatureWriter(GsFeatureClass feaClass)
	{
		m_feaClass = feaClass;
		m_Name = m_feaClass.Name();
		
	}
	
	GsFeature m_ptrFea = null;
	GsFeature GetFeature()
	{
		if(m_ptrFea != null)
		{
			m_ptrFea.OID(-1);
			return m_ptrFea;
		}
		m_ptrFea =  m_feaClass.CreateFeature();
		return m_ptrFea;
	}
	int m_nTrans = 0;
	void StoreFeature(GsFeature fea)
	{
		GsTransaction trans = m_feaClass.Transaction();
		if(m_nTrans ==0 && trans != null )
			trans.StartTransaction();
		fea.Store();
		
		m_nTrans++;
		if(m_nTrans > 10000 && trans != null )
		{
			trans.CommitTransaction();
			m_nTrans = 0;
			System.out.println("Commit");
		}
	}
	public void Commit()
	{
		GsTransaction trans = m_feaClass.Transaction();
		if(m_nTrans >0 && trans != null)
		{
			trans.CommitTransaction();
			m_nTrans = 0;
			
		}
		if(m_ptrFea != null)
			m_ptrFea.delete();
		System.out.println(m_Index);
	} 
	public int OnData(GsFeatureBuffer pData) {
		 
		GsFeature fea = GetFeature();
		pData.WriteToFeature(fea);
		
		System.out.print(pData.ID() + "_"+pData.DoubleValue(0)+"\t\n");
		fea.OID(pData.ID());
		StoreFeature(fea); 
		m_Index++;
		return 0;
	}
}
class FeatureReader extends GsAnalysisDataIO{
	GsFeatureClass m_feaClass;
	String m_Name;
	int m_nIndex = 0;
	public FeatureReader(GsFeatureClass feaClass)
	{
		m_feaClass = feaClass;
		m_Name = m_feaClass.Name();
		m_ptrCursor = m_feaClass.Search();
		GsFieldVector vec = m_feaClass.Fields().getFields();
		m_Fields = new GsFieldType[(int)(vec.size() -2)];
		for(int i = 2;i<vec.size();i++)
		{
			m_Fields[i-2] = vec.get(i).getType(); 
		}
	}
	GsFieldType[] m_Fields;

	public int GeometryDimension() {
		return GsGeometry.GeometryTypeDimension(m_feaClass.GeometryType());
	}
	GsFeatureCursor m_ptrCursor = null;
	GsFeature 		m_ptrFeature = null;
	public int OnData(GsFeatureBuffer pData) {
		  if(m_ptrFeature == null){
			  GsFeature ptrFea = m_ptrCursor.Next();
			  if(null == ptrFea) return -1;
			  
			  m_ptrFeature  = ptrFea;
		  }
		  else{
			  if(!m_ptrCursor.Next(m_ptrFeature)){
				  return -1;
			  }
		  }
		 

		  m_nIndex++;
		  return pData.ReadFromFeature(m_ptrFeature)?0:-1;
	}
}


class MemoryFeatureReader extends GsAnalysisDataIO{
	ArrayList<GsFeatureBuffer> FeaList=new ArrayList<GsFeatureBuffer>();//oid, geometry, num
	String m_Name= "memory";
	int m_nIndex = 0;
	public MemoryFeatureReader(int nCount)
	{
		for(int i = 0; i < nCount; i++)
		{
			GsFeatureBuffer featureBuffer =new GsFeatureBuffer();

			GsPoint point =  new GsPoint(i, i);
			featureBuffer.SetGeometry(point);
			featureBuffer.ID(i);
			featureBuffer.SetValue(0, (double)i*100.01);
			FeaList.add(featureBuffer);
		}
	}
	GsFieldType[] m_Fields;

	public int GeometryDimension() {
		return 0;
	}

	public int OnData(GsFeatureBuffer pData) {

			if(m_nIndex >= FeaList.size())
				return -1;
		  GsFeatureBuffer tmpBuffer =  FeaList.get(m_nIndex);
		  pData.SetGeometry(tmpBuffer.GeometryPtr());
		  pData.ID(tmpBuffer.ID());
		  pData.SetValue(0, tmpBuffer.DoubleValue(0));
			System.out.print("read"+ tmpBuffer.ID() + "_"+tmpBuffer.DoubleValue(0)+"\t\n");
		  m_nIndex++;
		  return 0;
	}
}
