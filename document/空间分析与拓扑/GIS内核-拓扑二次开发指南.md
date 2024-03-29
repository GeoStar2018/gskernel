﻿GIS内核-拓扑二次开发指南
	#include "spatialanalysis.h"
	#include "spatialanalysishelp.h"
	#include <gstest.h>  
	#include "geodatabase.h"
	#include "layoutelement.h"
	#include "layout.h"
	using namespace  GeoStar::Kernel;
	using namespace  GeoStar::Utility::Data;
	using namespace  GeoStar::Utility;
	
	
	/// \brief 验证拓扑规则时的进度
	/// \details 参数意义
	/// \details 参数1 GsTopologyRuleValidator对象指针
	/// \details 参数2 执行步骤的名称
	/// \details 参数3 执行步骤
	/// \details 参数4 总的步骤数量
	/// \details 参数5 单个步骤的进度，范围[0~1]
	/// \details 返回值  返回true，会继续执行验证，返回false会中断执行
	bool Progress(GsTopologyRuleValidator*, const char* log, int a, int b, float c)
	{
		std::cout << log << a << b << c << std::endl;
		return true;
	}
	
	bool OnPropareProgress(float b)
	{
		return true;
	}
	
	static GsField NewField(const char * bstrName, GsFieldType type, long nLen, long nScale, const char* defaultValue)
	{
		GsField ptrF;
		ptrF.Name = bstrName;
		ptrF.Type = type;
		ptrF.Precision = nLen;
		ptrF.Scale = nScale;
		return ptrF;
	}
	

	GS_TEST(TopologyCheck, LineNoDanglesValidator, chijing, 20180820)//线有悬挂点.fcs
	{
		GsString fcsFolder = this->MakeInputFolder(GsEncoding::ToUtf8("topo/线拓扑检查"));
		GsString tempFolder = this->MakeInputFolder(GsEncoding::ToUtf8("topo/temp"));
	
		GsSqliteGeoDatabaseFactoryPtr fac = new GsSqliteGeoDatabaseFactory();
		GsConnectProperty conn;
		conn.Server = fcsFolder.c_str();
		GsGeoDatabasePtr pDB = fac->Open(conn);
	
		GsFeatureClassPtr pFcs = pDB->OpenFeatureClass(GsEncoding::ToUtf8("线有悬挂点"));
	
		GsConfig topoConfig = GeoStar::Utility::GsGlobalConfig::Instance().Child("TopologyAnalysis");
		topoConfig.Child("CachePath").Value(tempFolder.c_str());
	
		GsTopologyCheckAnalysisPtr ptrTc = new GsTopologyCheckAnalysis();
	
		//拓扑预处理, 预处理将生成临时文件GsAnalysisDataIO ,通过向Preprocesser拿数据再去做拓扑规则检查
		GsTopologyCheckPreprocesserPtr ptrPrepareTop = ptrTc->CreatePreprocesser(0.001);
		//直接加入地物类
		ptrPrepareTop->Add(pFcs);
	
		ptrPrepareTop->OnProgress.Add(&OnPropareProgress);
		bool bok = ptrPrepareTop->Preprocess();
		if (!bok)
			return;
	
		GsAnalysisDataIOAgentPtr ptrResult = ptrPrepareTop->Result(0);
	
		//创建拓扑检查规则
		GsTopologyRuleValidatorPtr ptrCheckRule = ptrTc->CreateTopologyRule(GsTopologyRuleType::eTRT_LineNoDangles, 0.001);
		//预处理的数据加入到拓扑验证,如果不进行拓扑预处理,可以直接加入IO数据
		ptrCheckRule->AddData(ptrResult->Ptr());
	
		//默认输出必须字段,如果需要自己添加字段可以自行添加,在IO中赋值即可
		GsFields fds;
		GsField fdOID = NewField("TOPOLOGYRULETYPE",GsFieldType::eStringType,256,0,"");
		fds.Fields.push_back(fdOID);
	
		GsField fcs1 = NewField("CLASS_1", GsFieldType::eStringType, 36, 0, "");
		fds.Fields.push_back(fcs1);
	
		GsField fdSID = NewField("FEATURE_1", GsFieldType::eInt64Type, 38, 0, "");
		fds.Fields.push_back(fdSID);
	
		GsField fcs2 = NewField("CLASS_2", GsFieldType::eStringType, 36, 0, "");
		fds.Fields.push_back(fcs2);
	
		GsField fdSID2 = NewField("FEATURE_2", GsFieldType::eInt64Type, 38, 0, "");
		fds.Fields.push_back(fdSID2);
	
		GsField ex= NewField("EXCEPTION", GsFieldType::eIntType, 2, 0, "");
		fds.Fields.push_back(ex);
		//创建一个输出数据集
	
		GsGeometryColumnInfo g =  pFcs->GeometryColumnInfo();
		g.GeometryType = eGeometryTypePoint;
	
		GeoStar::Kernel::GsFeatureClassPtr mfd_Fcs = pDB->CreateFeatureClass(GsEncoding::ToUtf8("线有悬挂点_结果"),fds, g,pFcs->SpatialReference());
	
		FeatureClassWrtier writer(mfd_Fcs);
		ptrCheckRule->OutputData(&writer);
		ptrCheckRule->Tolerance(0.001);
		ptrCheckRule->OnValidateProgress.Add(&Progress);
		//执行拓扑验证
		GsTopologyRuleValidateResult result =  ptrCheckRule->ValidateRule();
		mfd_Fcs->Transaction()->CommitTransaction(); 
	}

GsAnalysisDataIO 封装类,下面演示例子为将FeatureClass 封装为GsAnalysisDataIO的数据流给拓扑检查使用, 
例子如下



	//输出结果IO 封装类。
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
			if (m_nCommit >0)
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