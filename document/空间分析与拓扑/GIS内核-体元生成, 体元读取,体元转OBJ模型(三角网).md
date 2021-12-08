GIS内核-体元生成, 体元读取,体元转OBJ模型(三角网)

```c++
#include <gstest.h>

#pragma once
#ifdef _DEBUG
#pragma comment (lib,"gsvoxeldatabased.lib")
#else
#pragma comment (lib,"gsvoxeldatabase.lib")
#endif // _DEBUG



#include "stringhelp.h" 
#include "voxeldatabase.h"
#include "spatialanalysis.h"
#include "../../3dkernel/include/kernel/utility3d/objwriter.h"
#include "Geomath2015.h"
#include "Geomath2015.cpp"

#pragma comment(lib,"gsutility3dd.lib")
using namespace GeoStar::Utility;
using namespace GeoStar::Kernel;
using namespace GeoStar::Utility::Data;
int _sizeof(GsVarType t)
{
	switch (t)
	{
	case GsVarType::eI1:case GsVarType::eUI1: return 1;
	case GsVarType::eI2: case GsVarType::eUI2: return 2;
	case GsVarType::eI4: case GsVarType::eUI4: return 4;
	case GsVarType::eI8: case GsVarType::eUI8: return 8;
	case GsVarType::eBool: return 1;
	case GsVarType::eR4: return 4;
	case GsVarType::eR8: return 8;
	case GsVarType::eDateTime: return 8;
	default: return 0;
	}
}
class InterpolationIO : public GsAnalysisDataIO
{
	GsVoxelIOPtr m_VIO = 0;
	std::vector<GsColor> fixedcolor = {
	   GsColor(0,1,128),GsColor(0,21,196),GsColor(0,75,243),GsColor(2,146,253),GsColor(32,211,223),GsColor(92,249,163),
	   GsColor(163,249,92),GsColor(223,211,32),GsColor(253,146,2),GsColor(243,75,0),GsColor(196,21,0)
	};
public:
	GsFixedGradsColorPtr ptrColor = new GsFixedGradsColor(&fixedcolor[0], fixedcolor.size());
	GsVoxelSpacePtr m_ptrSpace = new GsVoxelSpace();
	GsVoxelMapPtr m_ptrCache = new GsVoxelMap();
	int m_mCount = 0;
	int spaceXYZ[3] = { 32,32,32 };

	InterpolationIO(const char* fileOrFolder, GsBox3D& box, int* xyz, int epsg, GsVarType vartype)
	{
		if (GsFileSystem::IsFile(fileOrFolder))
		{
			m_ptrSpace->SetVoxelIO(m_VIO = new GsNetCDFVoxelIO(fileOrFolder));
		}
		else
		{
			m_ptrSpace->SetVoxelIO(m_VIO = new GsTileFileVoxelIO(fileOrFolder));
		}
		m_ptrSpace->SetGeoBoxInfo(box, epsg);
		m_ptrSpace->SetMapInfo(spaceXYZ, vartype, _sizeof(vartype));
		GsTileFileVoxelIOPtr ptrTileIO = m_VIO;
		if(ptrTileIO)
			ptrTileIO->ColorRamp(ptrColor);
		m_ptrCache->Create(xyz[0], xyz[1], xyz[2], vartype, _sizeof(vartype));
		m_ptrCache->SetMapBox(box);
	}
	~InterpolationIO()
	{
		Commit();
	}

	bool  Commit()
	{
		GS_E << " InterpolationIO " << m_mCount;
		m_ptrSpace->Update(m_ptrCache);
		m_ptrSpace->Flush();
		return true;
	}
	/// \brief 输入或者输出数据
	/// \param pData 
	/// \return 当输入空间数据时返回0标示正常输入一条数据，否则为错误或者输入完成。
	int OnData(GsFeatureBuffer* pData)
	{
		m_mCount++;
		int z = pData->IntValue(2);
		int y = pData->IntValue(3);
		int x = pData->IntValue(4);
		double value = pData->DoubleValue(5);
		void* pValue = m_ptrCache->At(x, y, z);
		Copy(pValue, value);
		return 0;
	}

	void Copy(void* dst, double invalue)
	{
		switch (m_ptrCache->VarType())
		{
		case GsVarType::eI1: ValueCopy<char>(dst, invalue); break;
		case GsVarType::eUI1:ValueCopy<unsigned char>(dst, invalue); break;
		case GsVarType::eI2:ValueCopy<short>(dst, invalue); break;
		case GsVarType::eUI2:ValueCopy<unsigned short>(dst, invalue); break;
		case GsVarType::eI4:ValueCopy<int>(dst, invalue); break;
		case GsVarType::eUI4: ValueCopy<unsigned int>(dst, invalue); break;
		case GsVarType::eI8:ValueCopy<long long>(dst, invalue); break;
		case GsVarType::eUI8: ValueCopy<unsigned long long>(dst, invalue); break;
		case GsVarType::eBool: ValueCopy<bool>(dst, invalue); break;
		case GsVarType::eR4:ValueCopy<float>(dst, invalue); break;
		case GsVarType::eR8: ValueCopy<double>(dst, invalue); break;
		case GsVarType::eDateTime: ValueCopy<unsigned long long>(dst, invalue); break;
		default: return;
		}
	}
	template<typename T>
	void ValueCopy(void* dst, T invalue)
	{
		memcpy(dst, &invalue, m_ptrCache->VarBytesSize());
	}
};

void PointsInterpolation2VoxelSpace(const char* folder)
{
	GsKernel::Initialize();
	std::string fcsFolder = folder;
	GsString strFolder = folder;
	GsConnectProperty conn;
	conn.Server = strFolder;
	GsGeoDatabaseFactoryPtr fac = new GsShpGeoDatabaseFactory();
	GsGeoDatabasePtr ptrGDB = fac->Open(conn);
	GsFeatureClassPtr feaClass = ptrGDB->OpenFeatureClass("njzk.shp");
	GsFeatureCursorPtr ptrCursor = feaClass->Search();

	GsFeaturePtr ptrFea = ptrCursor->Next();
	std::vector<double> vecLocationPoints;
	std::vector<double> vecPointsValue;
	GsRandom ran;
	double minx = DBL_MAX, miny = DBL_MAX, minz = DBL_MAX, maxx = DBL_MIN, maxy = DBL_MIN, maxz = DBL_MIN;
	int GCJCBLIndex = feaClass->FieldsPtr()->FindField("GCJCBL");
	int TKACCIndex = feaClass->FieldsPtr()->FindField("TKACC");
	int nCurrent = 0;
	GsSpatialReferencePtr ptrSrc = new GsSpatialReference(3857);
	GsSpatialReferencePtr ptrDst = new GsSpatialReference(4490);

	GsCoordinateTransformationPtr ptrTran = GsCoordinateTransformationFactory::CreateProjectCoordinateTransformation(ptrSrc, ptrDst);
	do
	{
		if (nCurrent > 64)
			break;
		nCurrent++;
		if (!ptrFea)
			break;

		GsPointPtr pt = ptrFea->Geometry();
		double x = pt->X();
		double y = pt->Y();
		ptrTran->Transformation(x, y);
		vecLocationPoints.emplace_back(x);
		vecLocationPoints.emplace_back(y);

		vecLocationPoints.emplace_back(pt->Z() > 0 ? pt->Z() : 100*ptrFea->ValueDouble(GCJCBLIndex));
		minx = x < minx ? x : minx;
		miny = y < miny ? y : miny;
		minz = vecLocationPoints[vecLocationPoints.size() - 1] < minz ? vecLocationPoints[vecLocationPoints.size() - 1] : minz;
		maxx = x > maxx ? x : maxx;
		maxy = y > maxy ? y : maxy;
		maxz = vecLocationPoints[vecLocationPoints.size() - 1] > maxz ? vecLocationPoints[vecLocationPoints.size() - 1] : maxz;

		double g = ptrFea->ValueDouble(TKACCIndex);
		vecPointsValue.push_back(g);
	} while (ptrCursor->Next(ptrFea));


	GsInterpolationKrigingParameter IDWParams;
	int nwidth = 64;
	int nheight = 64;
	int Depth = 64;
	double dbldx = feaClass->Extent().Width() / nwidth;
	double dbldy = feaClass->Extent().Height() / nheight;
	IDWParams.Bounds = GsBox3D(minx, miny, minz, maxx, maxy, maxz);
	IDWParams.Resolution = dbldx > dbldy ? dbldx : dbldy;
	IDWParams.dfNoDataValue = 0;
	IDWParams.Scale = 1;
	IDWParams.SearchRadius = 256;
	//此值决定是否分块克吕金, 当数据过大可以采用
	IDWParams.MaxPointCountInNode = 20;
	IDWParams.Type = GsInterpolationAlgorithmType::eEmpiricalBayesianKriging3D;

	GsString outtiffile = GsFileSystem::Combine(GsUtf8(fcsFolder.c_str()).Str().c_str(), "voxeltile");
	double dbfGeoTrans[6] = { feaClass->Extent().XMin,dbldx, 0,feaClass->Extent().YMax,0,dbldy };
	int xyz[3] = { nwidth, nheight, Depth };
	InterpolationIO writer(outtiffile.c_str(), IDWParams.Bounds, xyz, 4490, eR4);
	IDWParams.SearchMode = GsInterpolationSearchMode::None;
	GsRasterInterpolationAnalysisPtr ptrRasterInterpolation = new GsRasterInterpolationAnalysis(IDWParams);
	ptrRasterInterpolation->Width(nwidth);
	ptrRasterInterpolation->Height(nheight);
	ptrRasterInterpolation->Depth(Depth);
	ptrRasterInterpolation->OutputData(&writer);
	bool bOK = ptrRasterInterpolation->Interpolate(&vecLocationPoints[0], vecLocationPoints.size(), &vecPointsValue[0]);
}

GS_TEST(GsVoxelMap, GsVoxelMap, chijing, 20210323)
{
	return;
	PointsInterpolation2VoxelSpace(this->MakeInputFolder("voxel_njzk"));
}



using namespace LIB3DTILE_NAME;
struct VoxelMap2Mesh 
{
private:
	GsString  m_Folder;
	int m_ColorIndex = 0;
	//每个数值得mesh用什么颜色, 或者材质自己决定
	std::vector<GsColor> fixedcolor = {
	   GsColor(0,1,128),GsColor(0,21,196),GsColor(0,75,243),GsColor(2,146,253),GsColor(32,211,223),GsColor(92,249,163),
	   GsColor(163,249,92),GsColor(223,211,32),GsColor(253,146,2),GsColor(243,75,0),GsColor(196,21,0)
	};

	void gse2Primitive(geostar::gsePath & gse, GsPrimitiveBody& body)
	{
		int nPt, nTin; float* ptrF; double* ptrD;
		nPt = gse.NumPoint();
		nTin = gse.NumTin();
		body.Positions.resize(nPt);
		ptrF = (float*)&body.Positions[0];
		ptrD = gse.PtrPoint();
		std::copy(ptrD, ptrD + 3 * nPt, ptrF);
		body.Indices.resize(3 * nTin);
		memcpy(&body.Indices[0], gse.PtrTin(), nTin * 12);

		//颜色处理
		body.Colors.resize(3 * nTin * 4);
		float r = 1.0000*fixedcolor[m_ColorIndex].R / 255.0;
		float g = 1.0000*fixedcolor[m_ColorIndex].G / 255.0;
		float b = 1.0000*fixedcolor[m_ColorIndex].B / 255.0;
		float a = 1.0000*fixedcolor[m_ColorIndex].A / 255.0;
		for (int i = 0; (i+4) < 3 * nTin * 4; i += 4)
		{
			body.Colors[i] = r;
			body.Colors[i + 1] = g;
			body.Colors[i + 2] = b;
			body.Colors[i + 3] = a;
		}
		body.Normals.resize(3 * nTin);
		for (int j = 0; j < 3 * nTin; j++)
		{
			body.Normals[j].Set(1,1,1);
		}
	}
	bool ConvertPatch2MeshBody(GsMultiPatch* pPatch, LIB3DTILE_NAME::GsMeshBody& mbody)
	{
		geostar::gsePath gsep(pPatch->GeometryBlobPtr()->GeoSEObject());
		mbody.Primitives.emplace_back();
		gse2Primitive(gsep, mbody.Primitives[0]);
		return true;
	}
	bool ConvertPatch2MeshBody2(GsMultiPatch* pPatch, LIB3DTILE_NAME::GsMeshBody&p)
	{
		int nCount = pPatch->PatchCount();
		for (int i = 0; i < nCount; i++)
		{
			 const double * pcoords= pPatch->PatchVertices(i);
			 int cn = pPatch->PatchVerticesLength(i);
			 const int * pindex =  pPatch->PatchVerticesIndexes(i);
			 int n =pPatch->PatchVerticesIndexesLength(i);
			 p.Primitives.emplace_back();
		
			 //顶点索引
			 p.Primitives[p.Primitives.size() - 1].Indices.resize(n);
			 memcpy(&p.Primitives[p.Primitives.size() - 1].Indices[0], pindex, n);

			 				
			 //坐标直接赋值,double->flaot
			 p.Primitives[p.Primitives.size() - 1].Positions.resize(n);
			 p.Primitives[p.Primitives.size() - 1].Normals.resize(n);
			 for (int j = 0; j < n; j++)
			 {
				 p.Primitives[p.Primitives.size() - 1].Positions[j].
					 Set(
					 (float)*(pcoords + 3*j),
						 (float)*(pcoords + 3*j + 1),
						 (float)*(pcoords + 3 * j + 2));
				 p.Primitives[p.Primitives.size() - 1].Normals[j].Set(0, 0, 0);  
			 }
		}
		return true;
	}
	//内核GsMultiPatch转到obj文件
	bool OnVoxelMap2MeshOutput(GsMultiPatch* pPatch, double fromValue, double toValue)
	{
		GsString strFileobj = m_Folder;
		GsString str =  GsStringHelp::ToString(m_ColorIndex);
		str += ".obj";
		strFileobj  = GsFileSystem::Combine(strFileobj, str);

	
		GsMeshBody meshes;
		GsOBJModelWriter writer(strFileobj.c_str());
		if (ConvertPatch2MeshBody(pPatch, meshes))
		{
			writer.Write(&meshes);
		}
		 
		return true;
	}
public:
	GsVoxelMapPtr ReadRandomVoxelSpace(const char* folder)
	{
		GsKernel::Initialize();
		GsVoxelIOPtr  m_VIO = new GsTileFileVoxelIO(folder);
		GeoStar::Kernel::GsVoxelSpacePtr m_ptrSpace = new GeoStar::Kernel::GsVoxelSpace(m_VIO);
		GsVoxelMapPtr m_ptrCache = new GsVoxelMap();

		int epsg = 4326;
		GsVarType vartype = eR4;

		int cs[3] = {};
		int vcs;
		GsVarType type;
		m_ptrSpace->GetMapInfo(cs, type, vcs);
		GsBox3D box;
		m_ptrSpace->GetGeoBoxInfo(box, epsg);

		m_ptrCache->Create(cs[0], cs[1], cs[2], type, _sizeof(type));
		m_ptrCache->SetMapBox(box);
		
		m_ptrSpace->CopyTo(m_ptrCache);
		return m_ptrCache;
	
	}


	void RandomVoxelSpace2Mesh(const char* folder)
	{
		GsVoxelMapPtr m_ptrCache = ReadRandomVoxelSpace(folder);

		m_Folder = folder;
		//开始构建mesh模型
		GsVoxelAnalysis VA;
		VA.OnVoxelMap2MeshOutput.Add(this, &VoxelMap2Mesh::OnVoxelMap2MeshOutput);
		m_ColorIndex = 0;
		VA.VoxelMap2Mesh(m_ptrCache, 1, 10);
		m_ColorIndex = 1;
		VA.VoxelMap2Mesh(m_ptrCache, 10, 20);
		m_ColorIndex = 2;
		VA.VoxelMap2Mesh(m_ptrCache, 20, 50);
	}


	void RandomVoxelSpace(const char* folder)
	{
		GsKernel::Initialize();
		GsVoxelIOPtr m_VIO = 0;
		std::vector<GsColor> fixedcolor = {
		   GsColor(1,1,128),GsColor(0,21,196),GsColor(0,75,243),GsColor(2,146,253),GsColor(32,211,223),GsColor(92,249,163),
		   GsColor(163,249,92),GsColor(223,211,32),GsColor(253,146,2),GsColor(243,75,0),GsColor(196,21,0)
		};
		GsFixedGradsColorPtr ptrColor = new GsFixedGradsColor(&fixedcolor[0], fixedcolor.size());
		GsVoxelSpacePtr m_ptrSpace = new GsVoxelSpace();
		GsVoxelMapPtr m_ptrCache = new GsVoxelMap();
		int m_mCount = 0;
		int spaceXYZ[3] = { 32,32,32 };
		GsBox3D box(108.9584962, 34.2180599, 400, 108.9601436, 34.2208697, 500);
		int xyz[3] = { 32,32,32 };
		int epsg = 4326;
		GsVarType vartype = eR4;
		m_ptrSpace->SetVoxelIO(m_VIO = new GsTileFileVoxelIO(folder));
		m_ptrSpace->SetGeoBoxInfo(box, epsg);
		m_ptrSpace->SetMapInfo(spaceXYZ, vartype, _sizeof(vartype));
		GsTileFileVoxelIOPtr ptrTileIO = m_VIO;
		if (ptrTileIO)
			ptrTileIO->ColorRamp(ptrColor);
		m_ptrCache->Create(xyz[0], xyz[1], xyz[2], vartype, _sizeof(vartype));
		m_ptrCache->SetMapBox(box);
		int centerx = 29;
		int centery = 29;
		int centerz = 29;
		int centerx1 = 10;
		int centery1 = 15;
		int centerz1 = 30;


		for (int x = 0; x < 32; x++)
			for (int y = 0; y < 32; y++)
				for (int z = 0; z < 32; z++)
				{
					void* pValue = m_ptrCache->At(x, y, z);
					float* g = (float*)pValue;
					float tmp = sqrt(pow((x - centerx), 2) + pow((y - centery), 2) + pow((z - centerz), 2));
					float tmp2 = sqrt(pow((x - centerx1), 2) + pow((y - centery1), 2) + pow((z - centerz1), 2));
					float tmp3 = tmp < tmp2 ? tmp : tmp2;
					*g = 28 - tmp3;
					if (*g < 0)
						*g = 1;
				}



		m_ptrSpace->Update(m_ptrCache);
		m_ptrSpace->Flush();
	}
};
GS_TEST(GsVoxelMap, RandomGsVoxelMap, chijing, 20210323)
{
	VoxelMap2Mesh vm2m;
	vm2m.RandomVoxelSpace(this->MakeInputFolder("voxel_njzk_r"));
}
GS_TEST(GsVoxelMap, ReadRandomGsVoxelMap, chijing, 20210323)
{
	return;
	VoxelMap2Mesh vm2m;
	vm2m.ReadRandomVoxelSpace(this->MakeInputFolder("voxel_njzk_r"));
}
GS_TEST(GsVoxelMap, RandomVoxelSpace2Mesh, chijing, 20210323)
{
	VoxelMap2Mesh vm2m;
	vm2m.RandomVoxelSpace2Mesh(this->MakeInputFolder("voxel_njzk_r"));
}



```

