GIS内核-抽稀点图层并合并其某一字段属性

```c++

#include "spatialanalysis.h"
#include "spatialanalysishelp.h"
#include <gstest.h>  
#include "geodatabase.h"

using namespace  GeoStar::Kernel;
using namespace  GeoStar::Utility::Data;
using namespace  GeoStar::Utility;

GS_TEST(GsGeneralityAnalysis, CheckGenerality_PointGenerality, chijing, 20190219)
{
	//数据准备
	std::string fcsFolder = this->MakeInputFolder("../testdata/400sqlite");
	std::string outFolder = this->MakeOutputFolder("PointGenerality");

	GsSqliteGeoDatabaseFactoryPtr fac = new GsSqliteGeoDatabaseFactory();
	GsConnectProperty conn;
	conn.Server = fcsFolder.c_str();
	GsGeoDatabasePtr pDB = fac->Open(conn);

	GsFeatureClassPtr pFcs = pDB->OpenFeatureClass(("XIANCH_P"));

	FeatureClassReader pIO1(pFcs, 0);

	//创建一个输出数据集
	GsGeometryColumnInfo g = pFcs->GeometryColumnInfo();
	g.GeometryType = eGeometryTypePoint;
	GeoStar::Kernel::GsFeatureClassPtr mfd_PointFcs = pDB->CreateFeatureClass(u8"XIANCH_P_PointGenerality_32", pFcs->Fields(), g, pFcs->SpatialReference());
	FeatureClassUpdateWrtier writer(mfd_PointFcs);

	//创建抽稀对象
	//补偿 
	GsPointGeneralityAnalysisParameters params;
	params.FieldIndex = pFcs->FieldsPtr()->FindField("ESRI_NUM");
	params.GridSize = 32;	//分块给太多会慢和耗用大量内存,,如果一次搞不定可多次处理完成
	params.Type = GsPointGeneralityAttributeStatisticsType::eSum;
	double res = pFcs->Extent().Width() / params.GridSize;
	GsGeneralityAnalysisPtr ptrGenerality = new GsGeneralityAnalysis(GsGeneralityAnalysisType::eGPT_PointGenerality, res);//创建抽稀对象
	
	ptrGenerality->PointGeneralityParameters(params);
	ptrGenerality->Tolerance(res);
	ptrGenerality->AddData(&pIO1);	//设置输入流    	
	ptrGenerality->OutputData(&writer);//设置输出流
	bool bRes = ptrGenerality->Preprocess();//进行抽稀处理
	ASSERT_EQ(bRes, true);
}
```



辅助类:

```c++

//输出结果。
class FeatureClassUpdateWrtier :public GsAnalysisDataIO
{
	GsFeatureClassPtr		m_ptrFeaClass;
	GsFeaturePtr			m_ptrFea;
	int						m_nCommit;
	GsFeature*				CreateFeature()
	{
		if (m_ptrFea)
		{
			m_ptrFea->OID(-1);
			return m_ptrFea.p;
		}
		m_ptrFea = m_ptrFeaClass->CreateFeature();
		return m_ptrFea;
	}
	void Store(GsFeature* pFea)
	{
		if (m_nCommit == 0)
			m_ptrFeaClass->Transaction()->StartTransaction();
		m_nCommit++;
		pFea->Store();
		if (m_nCommit > 10000)
		{
			m_ptrFeaClass->Transaction()->CommitTransaction();
			m_nCommit = 0;
		}

	}
	int m_nRef;
public:

	FeatureClassUpdateWrtier(GsFeatureClass* pFeaClass)
	{
		m_nRef = 0;
		m_nCommit = 0;
		m_ptrFeaClass = pFeaClass;
	}
	~FeatureClassUpdateWrtier()
	{
		if (m_nCommit > 0)
			m_ptrFeaClass->Transaction()->CommitTransaction();

		m_ptrFeaClass->CreateSpatialIndex();
	}
	virtual int OnData(GsFeatureBuffer* pData)
	{

		GsFeature*	pFea = CreateFeature();
		pData->WriteToFeature(pFea);
		pFea->OID(pData->ID());//更新设置为>-1
		Store(pFea);
		return 0;
	}

};

class FeatureClassReader :public GsAnalysisDataIO
{
	GsFeatureCursorPtr m_ptrCursor;
	GsFeaturePtr		m_ptrFea;
	GsFields			m_FS;
	int					m_nDim;
	UTILITY_NAME::GsString m_StrName;
	GsFeatureClassPtr		m_ptrFeaClass;


public:
	FeatureClassReader(int nDim, GsFeatureCursor* pCursor)
	{
		m_nDim = nDim;
		m_ptrCursor = pCursor;
	}

	FeatureClassReader(GsFeatureClass* pFcs, GsQueryFilter* pQueryFiler) :
		m_ptrCursor(pFcs->Search(pQueryFiler)),
		m_nDim(GsGeometry::GeometryTypeDimension(pFcs->GeometryType()))
	{
		m_ptrFeaClass = pFcs;
		m_StrName = pFcs->Name();

	}

	GsFeatureClass* FeatureClass()
	{
		return m_ptrFeaClass;
	}

	virtual int OnData(GsFeatureBuffer* pData)
	{
		if (!m_ptrFea)
		{
			m_ptrFea = m_ptrCursor->Next();
			if (!m_ptrFea)
				return -1;
		}
		else
			if (!m_ptrCursor->Next(m_ptrFea))
				return -1;
		pData->ReadFromFeature(m_ptrFea);
		return 0;
	}
	/// \brief 获取数据源的几何维度，0=点，1=线，2=面
	virtual int GeometryDimension()
	{
		return m_nDim;
	}
};
//输出结果。OID永远为-1, 只执行插入操作
class FeatureClassWrtier :public GsAnalysisDataIO
{
	GsFeatureClassPtr		m_ptrFeaClass;
	GsFeaturePtr			m_ptrFea;
	int						m_nCommit;
	GsFeature*				CreateFeature()
	{
		if (m_ptrFea)
		{
			m_ptrFea->OID(-1);
			return m_ptrFea.p;
		}
		m_ptrFea = m_ptrFeaClass->CreateFeature();
		return m_ptrFea;
	}
	void Store(GsFeature* pFea)
	{
		if (m_nCommit == 0)
			m_ptrFeaClass->Transaction()->StartTransaction();
		m_nCommit++;
		pFea->Store();
		if (m_nCommit > 10000)
		{
			m_ptrFeaClass->Transaction()->CommitTransaction();
			m_nCommit = 0;
		}

	}
	int m_nRef;
public:

	FeatureClassWrtier(GsFeatureClass* pFeaClass)
	{
		m_nRef = 0;
		m_nCommit = 0;
		m_ptrFeaClass = pFeaClass;
	}
	~FeatureClassWrtier()
	{
		if (m_nCommit > 0)
			m_ptrFeaClass->Transaction()->CommitTransaction();

		m_ptrFeaClass->CreateSpatialIndex();
	}
	virtual int OnData(GsFeatureBuffer* pData)
	{

		GsFeature*	pFea = CreateFeature();
		pData->WriteToFeature(pFea);

		Store(pFea);
		return 0;
	}

};
```

