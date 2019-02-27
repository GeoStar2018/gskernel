// landcoversimply.cpp : 定义控制台应用程序的入口点。
//

#include "stdafx.h"
#include <utility.h>
#include <kernel.h>
#include <gobject.inl>
#include <GeomathSE.cpp>
#include <omp.h>

#ifdef _WIN32
#include <Windows.h>
#include <sstream>
#ifdef _DEBUG
#pragma comment(lib,"gsgeodatabased.lib")
#pragma comment(lib,"gssymbold.lib")
#pragma comment(lib,"gsgeometryd.lib")
#pragma comment(lib,"gsutilityd.lib") 
#pragma comment(lib,"gsspatialreferenced.lib")  
#else
#pragma comment(lib,"gsspatialreference.lib") 
#pragma comment(lib,"gsgeodatabase.lib")
#pragma comment(lib,"gssymbol.lib")
#pragma comment(lib,"gsgeometry.lib")
#pragma comment(lib,"gsutility.lib")  
#endif
#endif
#include <list>


std::string pFieldValues;

class MultiFeatureClass:public std::list<KERNEL_NAME::GsFeatureClassPtr>, UTILITY_NAME::GsLock
{
	long long m_Total;
	long long m_nNow;
	int m_nProgress;
	void AddProgress()
	{
		m_nNow++;
		int progress = 100.0 * m_nNow / m_Total;
		if (progress == m_nProgress)
			return;
		std::cout << "读取\t" << progress << "%\t" << m_nNow << "/" << m_Total << std::endl;
		m_nProgress = progress;
	}
	KERNEL_NAME::GsFeatureCursorPtr m_ptrCursor;
	KERNEL_NAME::GsFeatureClassPtr m_ptrClass;

	KERNEL_NAME::GsFeatureCursorPtr NextCursor()
	{
		if (this->empty())
			return NULL;

		m_ptrClass = front();
		pop_front();
		m_ptrCursor = m_ptrClass->Search();
		return m_ptrCursor;
	}
public:
	void Begin()
	{
		m_nProgress = -1;
		m_nNow = 0;
		m_Total = 0;
		std::list<KERNEL_NAME::GsFeatureClassPtr>::iterator it = this->begin();
		for (; it != this->end(); it++)
		{
			m_Total += (*it)->FeatureCount();
		}

	}
	KERNEL_NAME::GsFeaturePtr Next()
	{
		GS_LOCK_THIS;

		KERNEL_NAME::GsFeatureCursorPtr ptrCursor = m_ptrCursor;
		if (!ptrCursor)
			ptrCursor = NextCursor();
		if (!ptrCursor)
			return NULL;

		while (ptrCursor)
		{
			KERNEL_NAME::GsFeaturePtr ptrFea = ptrCursor->Next();
			if (ptrFea)
			{
				AddProgress();
				return ptrFea;
			}
			ptrCursor = NextCursor();
		}

		return NULL;
	}
	bool Next(KERNEL_NAME::GsFeature* fea)
	{
		GS_LOCK_THIS;

		KERNEL_NAME::GsFeatureCursorPtr ptrCursor = m_ptrCursor;
		if (!ptrCursor)
			ptrCursor = NextCursor();
		if (!ptrCursor)
			return false;

		while (ptrCursor)
		{
			if (ptrCursor->Next(fea))
			{
				AddProgress();
				return true;
			}

			ptrCursor = NextCursor();
		}

		return false;
	}
};

struct SafeClass
{
	KERNEL_NAME::GsFeatureClassPtr FeatureClass;
	UTILITY_NAME::GsLock Lock;
	int Commit;
	SafeClass(KERNEL_NAME::GsFeatureClass* fea)
	{
		FeatureClass = fea;
		Commit = 0;
	}
	void Begin()
	{
		GS_LOCK_IT(Lock);
		if (Commit == 0)
			FeatureClass->StartTransaction();
		Commit++;
	}
	void Store(KERNEL_NAME::GsFeature* fea)
	{
		GS_LOCK_IT(Lock);
		fea->Store();
		if (Commit > 1000)
		{
			Commit = 0;
			FeatureClass->CommitTransaction();
		}
	}
	void Finish()
	{
		if (Commit > 0)
		{ 
			FeatureClass->CommitTransaction();
		}
		FeatureClass->CreateSpatialIndex();
	}
};

void Simply(SafeClass & out, MultiFeatureClass& input,double tol)
{
	KERNEL_NAME::GsFeaturePtr ptrFea = input.Next();
	if (!ptrFea)
		return;

	KERNEL_NAME::GsFeaturePtr ptrFeaOut;
	KERNEL_NAME::GsFields fs = out.FeatureClass->Fields();

	KERNEL_NAME::GsGeometrySimplifierPtr ptrSimplifier = new KERNEL_NAME::GsTopologyPreservingGeometrySimplifier(tol);
	ptrSimplifier->EnableDegenerated(true); 
	int n = 0;
	do
	{   
		KERNEL_NAME::GsGeometrySimplifyResult result = ptrSimplifier->Simplify(ptrFea->GeometryBlob());
		if (result == KERNEL_NAME::eSimplefyDegenerated)
			continue; 
		//KERNEL_NAME::GsBox box = ptrFea->GeometryBlob()->Envelope();

		if (!ptrFeaOut)
			ptrFeaOut = out.FeatureClass->CreateFeature();
		else
			ptrFeaOut->OID(-1);

		//判断数据是否需要简化
		geostar::gobjptr ptrSrc = ptrFea->GeometryBlob()->GeoSEObject();
		int nRet = _ga().gcheck_1(ptrSrc, -1);
		if (nRet != 0)
			ptrFeaOut->Geometry(ptrFea->Geometry()->Simplify());
		else
			ptrFeaOut->Geometry(ptrFea->GeometryBlob());

		for (int i = 2; i < fs.Fields.size(); i++)
			ptrFeaOut->Value(i, ptrFea->ValuePtr(i), ptrFea->ValueSize(i), ptrFea->ValueType(i));

		out.Begin();
		out.Store(ptrFeaOut);
	} while (input.Next(ptrFea));
}

class CategoryWriter :public geostar::Tobject<geostar::geo_writer>
{
	KERNEL_NAME::GsFeatureClassPtr m_ptrFeaClass;
	int m_nTagIndex;
	UTILITY_NAME::GsAny m_nTag;
	KERNEL_NAME::GsFeaturePtr m_ptrFea;
	int m_nCommit;
public:
	CategoryWriter(KERNEL_NAME::GsFeatureClass* feaclass,const char* fdstr)
	{
		m_nCommit = 0;
		m_ptrFeaClass = feaclass;
		m_nTagIndex = feaclass->Fields().FindField(fdstr);
	}

	~CategoryWriter()
	{
		if (m_nCommit > 0)
		{ 
			m_ptrFeaClass->CommitTransaction();
		}
		m_ptrFeaClass->CreateSpatialIndex();
	}

	//int& Tag()
	UTILITY_NAME::GsAny& Tag()
	{
		return m_nTag;
	}
	/// \brief 写入对象
	/// \param obj 要写入的对象
	virtual void write(geo_object* obj)
	{
		if (!m_ptrFea)
			m_ptrFea = m_ptrFeaClass->CreateFeature();
		else
			m_ptrFea->OID(-1);

		if (m_nCommit == 0)
			m_ptrFeaClass->StartTransaction();
		
		m_nCommit++;
		m_ptrFea->GeometryBlob()->GeoSEObject(obj);


		geostar::gobjptr ptrSrc = m_ptrFea->GeometryBlob()->GeoSEObject();
		int nRet = _ga().gcheck_1(ptrSrc, -1);
		if (nRet != 0)
			m_ptrFea->Geometry(m_ptrFea->Geometry()->Simplify());


		switch (m_nTag.Type)
		{
		case UTILITY_NAME::eI1:
			m_ptrFea->Value(m_nTagIndex, m_nTag.cVal);
			break;
		case UTILITY_NAME::eI2:
			m_ptrFea->Value(m_nTagIndex, m_nTag.sVal);
			break;
		case UTILITY_NAME::eI4:
			m_ptrFea->Value(m_nTagIndex, m_nTag.iVal);
			break;
		case UTILITY_NAME::eI8:
			m_ptrFea->Value(m_nTagIndex, m_nTag.llVal);
			break;
		case UTILITY_NAME::eUI1:
			m_ptrFea->Value(m_nTagIndex, m_nTag.ucVal);
			break;
		case UTILITY_NAME::eUI2:
			m_ptrFea->Value(m_nTagIndex, m_nTag.usVal);
			break;
		case UTILITY_NAME::eUI4:
			m_ptrFea->Value(m_nTagIndex, m_nTag.uiVal);
			break;
		case UTILITY_NAME::eUI8:
			m_ptrFea->Value(m_nTagIndex, m_nTag.ullVal);
			break;
		case UTILITY_NAME::eR4:
			m_ptrFea->Value(m_nTagIndex, m_nTag.fltVal);
			break;
		case UTILITY_NAME::eR8:
			m_ptrFea->Value(m_nTagIndex, m_nTag.dblVal);
			break;
		case UTILITY_NAME::eBool:
			m_ptrFea->Value(m_nTagIndex, m_nTag.boolVal);
			break;
		case UTILITY_NAME::eString:
			m_ptrFea->Value(m_nTagIndex, m_nTag.strVal);
			break;
		default:
			m_ptrFea->Value(m_nTagIndex, m_nTag.strVal);
			break;
		}

		m_ptrFea->Store();

		if (m_nCommit > 1000)
		{
			m_nCommit = 0;
			m_ptrFeaClass->CommitTransaction();
		}
	}
};

class CategoryReader :public geostar::Tobject<geostar::geo_reader>
{
	//int m_Tag;
	UTILITY_NAME::GsAny m_Tag;
	int m_TagIndex;
	KERNEL_NAME::GsFeatureCursorPtr m_ptrCursor;
	KERNEL_NAME::GsFeaturePtr	m_ptrFea;
	UTILITY_NAME::GsLock m_Lock;
public:
	CategoryReader(KERNEL_NAME::GsFeatureCursor* pCursor, int nTagField, UTILITY_NAME::GsAny nTag)
	{
		m_TagIndex = nTagField;
		m_ptrCursor = pCursor;
		m_Tag = nTag;
	}
	/// \brief 读取下一个对象
	/// \return 如果成功返回对象的指针，否则返回0
	/// \details 如果返回0则表示已经读到的流的结尾
	virtual geo_object* read()
	{
		GS_LOCK_IT(m_Lock);
		while (true)
		{
			if (!m_ptrFea)
			{
				m_ptrFea = m_ptrCursor->Next();
				if (!m_ptrFea)
					return NULL;
			}
			else if (!m_ptrCursor->Next(m_ptrFea))
				return NULL;

			//判断数据是否需要简化
			geostar::gobjptr ptrSrc = m_ptrFea->GeometryBlob()->GeoSEObject();
			int nRet = _ga().gcheck_1(ptrSrc, -1);
			if (nRet != 0)
				m_ptrFea->Geometry(m_ptrFea->Geometry()->Simplify());

			if (m_ptrFea->FeatureClass()->Fields().Fields[m_TagIndex].Type == GeoStar::Utility::Data::GsFieldType::eStringType)
			{
				std::string s =  m_ptrFea->ValueString(m_TagIndex);
				std::string s1 = GeoStar::Utility::GsEncoding::ToLocal(m_Tag.strVal);
				std::string s2 = GeoStar::Utility::GsEncoding::ToLocal(s.c_str());
				if (GeoStar::Utility::GsCRT::_stricmp(m_Tag.strVal, s.c_str()) != 0)
					continue;
			}
			else
			{
				long long pLongValue = m_ptrFea->ValueInt64(m_TagIndex);
				if (m_Tag.uiVal != pLongValue)
					continue;
			}

			return 	m_ptrFea->GeometryBlob()->GeoSEObject().detach();
		}
		return NULL;
	}
};

void Combine(KERNEL_NAME::GsFeatureClass *pFeaInput, KERNEL_NAME::GsFeatureClass  *pFeaOutput,const char* field)
{
	std::string pStr = pFieldValues;
	int nInd = 0;
	std::vector<std::string> pFieldVal;
	while (1)
	{
		int n1 = pStr.find(',', nInd);
		if (n1 < 0)
		{
			pFieldVal.push_back(pStr);
			break;
		}

		std::string str1 = pStr.substr(0, n1);
		pFieldVal.push_back(str1);
		pStr = pStr.substr(n1 + 1, pStr.length() - n1);
	}

	int nIndex = pFeaInput->Fields().FindField(field);
	CategoryWriter w(pFeaOutput, field);

	GeoStar::Utility::Data::GsFieldType pFldType = pFeaInput->Fields().Fields[nIndex].Type;
	for (vector<std::string>::iterator iter = pFieldVal.begin(); iter != pFieldVal.end(); iter++)
	{
		UTILITY_NAME::GsAny pAnyData;
		if (pFldType == GeoStar::Utility::Data::GsFieldType::eStringType)
		{
			pAnyData = UTILITY_NAME::GsEncoding::ToUtf8(iter->c_str());
			pAnyData.Type = UTILITY_NAME::eString;
		}
		else
		{
			pAnyData = GeoStar::Utility::GsCRT::_atoi64(iter->c_str());
			pAnyData.Type = UTILITY_NAME::eI8;
		}

		CategoryReader r(pFeaInput->Search(), nIndex, pAnyData);
		w.Tag() = pAnyData;
		_ga().n_union(&r, &w);
	}
}

//E:\02-Soft\gis\lca  E:\02-Soft\gis\out NAME 0.0054931640625 山东省,广东省,福建省,海南省,河北省,辽宁省,上海市,台湾省,浙江省
//E:\02-Soft\gis\lca  E:\02-Soft\gis\out BSM 0.0054931640625 425,426,722,723,724,725,726,727,728
int main(int argc, char **argv)
{
	if (argc < 6)
	{
		std::cout << "lancoversimply  [输入地物类或地物类目录]  [输出目录] [分类字段] [简化容差] [字段分类值]" << std::endl;
		KERNEL_NAME::GsPyramidPtr ptrPyramid = new KERNEL_NAME::GsMultiPyramid();
		std::cout << "金字塔分辨率" << std::endl;
		for (int i = 0; i < 21; i++)
		{
			std::cout << "Level \t"<<i<<"\t"<< ptrPyramid->TileSpanX(i, 0, 0) / 256 << std::endl;
		}
		return 0;
	}
	int i = 1;
	std::string strInput = argv[i++];
	std::string strOutput = argv[i++];
	std::string strField = argv[i++];
	double	dblTol = atof(argv[i++]);
	pFieldValues = argv[i++];

	UTILITY_NAME::GsFile inputFile(strInput.c_str());
	UTILITY_NAME::GsDir inputDir(strInput.c_str());
	if (!inputFile.Exists() && !inputDir.Exists())
	{
		std::cout << "输入地物类目录或者文件并不存在" << std::endl;
		return 0;
	}

	UTILITY_NAME::GsDir outputDir(strOutput.c_str());
	if (!outputDir.Exists())
		outputDir.Create();

	MultiFeatureClass input;


	KERNEL_NAME::GsSqliteGeoDatabaseFactory fac;
	KERNEL_NAME::GsConnectProperty conn;
	std::vector<UTILITY_NAME::GsString> vecNames;

	conn.Server = strInput;
	if (UTILITY_NAME::GsFileSystem::IsFile(strInput.c_str()) && inputFile.Exists())
	{
		conn.Server = inputFile.Parent().FullPath();
		vecNames.push_back(inputFile.Name(false));
	}	

	KERNEL_NAME::GsGeoDatabasePtr ptrGDB = fac.Open(conn);
	if (!ptrGDB)
	{
		std::cout << "打开输入地物类失败。";
		return 0;
	}
	if (vecNames.empty())
		ptrGDB->DataRoomNames(KERNEL_NAME::eFeatureClass, vecNames);
	
	std::vector<UTILITY_NAME::GsString>::iterator it = vecNames.begin();
	for (; it != vecNames.end(); it++)
	{
		KERNEL_NAME::GsFeatureClassPtr ptrFeaClass = ptrGDB->OpenFeatureClass(it->c_str());
		if (ptrFeaClass)
			input.push_back(ptrFeaClass);
	}
	if(input.empty())
	{
		std::cout << "打开输入地物类失败。";
		return 0;
	}
	
	KERNEL_NAME::GsGeometryColumnInfo col = input.front()->GeometryColumnInfo();
	KERNEL_NAME::GsFields fs = input.front()->Fields();
	KERNEL_NAME::GsSpatialReferencePtr ptrSR = input.front()->SpatialReference();
	
	conn.Server = strOutput;
	ptrGDB = fac.Open(conn);
	KERNEL_NAME::GsFeatureClassPtr ptrFeaClassSimple = ptrGDB->CreateFeatureClass("Simply",
		fs,col, ptrSR);
	
	input.Begin();
	SafeClass safe(ptrFeaClassSimple);
	int nCPU = 1;// omp_get_num_procs() * 2;
// #pragma omp parallel for
	for (int i = 0; i < nCPU; i++)
		Simply(safe, input,dblTol);
	
	safe.Finish();
	KERNEL_NAME::GsFeatureClassPtr ptrCombine = ptrGDB->CreateFeatureClass("Combine", fs
			, col, ptrSR);

	Combine(ptrFeaClassSimple, ptrCombine, UTILITY_NAME::GsEncoding::ToUtf8(strField.c_str()).c_str());
	 
    return 0;
}
