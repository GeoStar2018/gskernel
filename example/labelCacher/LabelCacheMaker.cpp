#include "stdafx.h"
#include "LabelCacheMaker.h"
#include "geomathse.cpp"
using namespace UTILITY_NAME;
using namespace KERNEL_NAME; 
void ParseRules(const GsConfig& config, std::vector<TileRule> &rules)
{
	GsVector<GsConfig> vec = config.Children();
	GsVector<GsConfig>::iterator it = vec.begin();
	for (; it != vec.end(); it++)
	{
		rules.emplace_back(GsCRT::_atoi64(it->Name().c_str()),GsCRT::_atof( it->Value().c_str()));
	}
	if (rules.empty())
	{
		//设置要切的级别，以及没级切的长度，单位米
		rules.emplace_back(14, 1000);
		rules.emplace_back(15, 500);
		rules.emplace_back(16, 250);
		rules.emplace_back(17, 125);
		rules.emplace_back(18, 50);
		rules.emplace_back(19, 25);
		rules.emplace_back(20, 12);
		rules.emplace_back(21, 5);
		rules.emplace_back(22, 2);
	}
}
UTILITY_NAME::GsString& FeatureClassConfig::ClassFieldName()
{
	return m_strClassFieldName;
}
UTILITY_NAME::GsString& FeatureClassConfig::Name()
{
	return m_strName;
}
UTILITY_NAME::GsString& FeatureClassConfig::LabelFieldName()
{
	return m_strLabelFieldName;
}
std::vector<int>& FeatureClassConfig::DefaultLevels()
{
	return m_DefaultLevels;
}
std::vector<int> g_emptyLevels;
const std::vector<int>& FeatureClassConfig::QueryClassLevels(const char* value)
{
	if (GsStringHelp::IsNullOrEmpty(m_strClassFieldName))
		return m_DefaultLevels;

	std::map<UTILITY_NAME::GsString, std::vector<int> >::iterator it = m_Levels.find(value);
	if (it != m_Levels.end())
		return it->second;
	if (m_EnableDefaultLevels)
		return m_DefaultLevels;
	return g_emptyLevels;
}
void ParseLevel(std::vector<int>& vecLevel, const char* str)
{
	GsVector<GsString> levels = GsStringHelp::Split(str, ",");
	GsVector<GsString>::iterator itl = levels.begin();
	for (; itl != levels.end(); itl++)
	{
		vecLevel.emplace_back(GsCRT::_atoi64(itl->c_str()));
	}

}
FeatureClassConfig::FeatureClassConfig(const UTILITY_NAME::GsConfig& config)
{
	m_strName = config["Name"].StringValue("");
	m_strLabelFieldName = config["LabelFieldName"].StringValue("");
	m_strClassFieldName = config["ClassFieldName"].StringValue("");
	m_EnableDefaultLevels = config["EnableDefaultLevels"].BoolValue(true);

	GsString strDefault = config["DefautLevels"].StringValue("");
	if (!GsStringHelp::IsNullOrEmpty(strDefault.c_str()))
		ParseLevel(m_DefaultLevels, strDefault.c_str());


	GsVector<GsConfig> vec = config["Classes"].Children();
	GsVector<GsConfig>::iterator it = vec.begin();
	for (; it != vec.end(); it++)
	{
		GsString strVal = (*it)["Value"].StringValue("");
		GsString strLevel = (*it)["Level"].StringValue("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20");
		std::vector<int>& vecLevel = m_Levels[strVal];
		ParseLevel(vecLevel, strLevel.c_str());
	}

}
TileRuleFile::TileRuleFile(const char* file)
{
	GsConfig config(file);
	ParseRules(config["Levels"], m_Rules);

	std::vector<TileRule>::iterator i = m_Rules.begin();
	for (; i != m_Rules.end(); i++)
		m_DefaultLevels.push_back(i->Level);


	GsVector<GsConfig> vec = config["FeatureClasses"].Children();
	GsVector<GsConfig>::iterator it = vec.begin();
	for (; it != vec.end(); it++)
	{
		m_FeaClass.emplace_back(new FeatureClassConfig(*it));
		if (m_FeaClass.back()->DefaultLevels().empty())
			m_FeaClass.back()->DefaultLevels() = m_DefaultLevels;
		
	}

	GsConfig configDefault = config["DefaultConfig"];
	if (configDefault)
	{
		m_ptrDefault.reset(new FeatureClassConfig(configDefault));
		if(m_ptrDefault->DefaultLevels().empty())
			m_ptrDefault->DefaultLevels() = m_DefaultLevels;
	}

	
}
FeatureClassConfigPtr TileRuleFile::FindClassConfig(const char* name)
{

	std::vector<FeatureClassConfigPtr>::iterator it = m_FeaClass.begin();
	for (; it != m_FeaClass.end(); it++)
	{
		if (GsCRT::_stricmp((*it)->Name().c_str(), name) == 0)
			return *it;
	}
	return m_ptrDefault;

}
std::vector<TileRule>& TileRuleFile::Rules()
{
	return m_Rules;
}
LabelTile::LabelTile(const UTILITY_NAME::GsQuadKey& key)
{
	m_Key = key;

}
LabelTile::~LabelTile()
{
	 
}



void JsonLabelTile::WriteTo(KERNEL_NAME::GsTile* tile)
{
	tile->Level(m_Key.Level);
	tile->Row(m_Key.Row);
	tile->Col(m_Key.Col);
	GsString strJson = Json::FastWriter().write(m_TileData);

	tile->TileData((const unsigned char*)strJson.data(), strJson.size());
}
void JsonLabelTile::Add(GsPath* path, const GsRawPoint3D& ct,const char* name)
{
	//添加一个feature,以及对应的中点，作为标注点，实际标注的时候可以使用中点标注也可以沿线标注
	Json::Value &value = m_TileData.append(Json::Value());
	Json::Value &feature = value["Feature"];

	GsGeoJSONOGCWriter w; 
	w.Attribute("Name", GsAny(name));

	w.Write(path);
	GsString strJson = w.GeoJSON();

	Json::Reader().parse(strJson, feature);
	value["ID"] = GsMath::NewGUID().c_str();
	Json::Value &label = value["LabelPoint"];
	 
	label["X"] = ct.X;
	label["Y"] = ct.Y;
	if(path->CoordinateDimension() ==3)
		label["Z"] = ct.Z;
}


JsonLabelTile::JsonLabelTile(  const UTILITY_NAME::GsQuadKey& key) :LabelTile(key)
{ 
}
JsonLabelTile::JsonLabelTile(const char* json, const UTILITY_NAME::GsQuadKey& key):LabelTile(key)
{ 
	Json::Reader().parse(json, m_TileData);
}


PBFLabelTile::PBFLabelTile(const UTILITY_NAME::GsQuadKey& key) : LabelTile(key)
{
	m_TileData.TileKey() = GsTile::ToKey(key.Level, key.Row, key.Col);
	m_TileData.Version() = 336;
}

PBFLabelTile::PBFLabelTile(const unsigned char* data,int nLen, const UTILITY_NAME::GsQuadKey& key) :LabelTile(key),  m_TileData(data,nLen)
{ 

}

void PBFLabelTile::WriteTo(KERNEL_NAME::GsTile* tile)
{
	tile->Level(m_Key.Level);
	tile->Row(m_Key.Row);
	tile->Col(m_Key.Col);

	std::string strData;
	m_TileData.Save(strData);
	tile->TileData((const unsigned char*)strData.data(), strData.size());
	tile->TileType(eProtobuffType);
}
void PBFLabelTile::Add(GsPath* path, const GsRawPoint3D& ct,const char* name)
{
	m_TileData.emplace_back();
	POI &poi = m_TileData.back();
	GsGeometryBlob* blob = path->GeometryBlobPtr();
	poi.m_Coordinates.insert(poi.m_Coordinates.begin(), blob->Coordinate(), blob->Coordinate() + blob->CoordinateLength());
	poi.m_GeometryType = eMultiLineString;
	poi.m_Name = name;
} 



LabelCacheMaker::LabelCacheMaker(const char* config, const char* folder,const char* dem, const char* format) :
	m_Geodesic(GsSpatialReferencePtr(new GsSpatialReference(eWGS84))),
	m_TileCache(format,10000), DEMSupport(dem),m_TileRuleFile(config)
{
	m_ptrPyramid = new GsPyramid();
	m_Folder = folder;
	GsConnectProperty conn;
	conn.Server = folder;
	GsGeoDatabasePtr ptrGDB =  GsSqliteGeoDatabaseFactory().Open(conn);
	GsTileClassPtr ptrTile = ptrGDB->OpenTileClass("LabelCache");
	if (ptrTile)ptrTile->Delete();
	ptrTile.Release();

	GsTileColumnInfo col;
	col.FeatureType = eDlgTileFeature;

	ptrTile = ptrGDB->CreateTileClass("LabelCache", new GsSpatialReference(eWGS84),
		m_ptrPyramid, col);

	m_TileCache.m_ptrOutputTileClass = ptrTile;
	m_ptrOutputTileClass = ptrTile;

	m_bJsonFormat = GsStringHelp::Compare(format, "JSON", true) == 0;

}


LabelCacheMaker::~LabelCacheMaker()
{
	////存储元数据。
	//Json::Value value;
	//Json::Value &details = value["LevelDetails"];
	//std::vector<TileRule>::iterator it = m_Rules.begin();
	//for(;it != m_Rules.end();it++)
	//{
	//	Json::Value &detail = details.append(Json::Value());
	//	detail["Level"] = it->Level;
	//	detail["Delta"] = it->Delta;
	//}
	//value["Format"] = m_bJsonFormat?"JSON":"PBF";

	//Json::Value &extent = value["Extent"];
	//extent["XMin"] = m_Extent.XMin;
	//extent["YMin"] = m_Extent.YMin;
	//extent["XMax"] = m_Extent.XMax;
	//extent["YMax"] = m_Extent.YMax;

	//GsFile metaFile = GsFileSystem::Combine(m_Folder.c_str(), "labelcache.json");
	//metaFile.WriteAllText(value.toStyledString().c_str());
}
 
//将多个Geometry串接为一个Geometry
KERNEL_NAME::GsGeometryPtr LabelCacheMaker::Combine(
	UTILITY_NAME::GsVector<KERNEL_NAME::GsGeometryPtr>& vecGeo)
{
	if (vecGeo.empty())
		return 0;
	if (vecGeo.size() == 1)
		return vecGeo.front();
	GsPolylinePtr ptrMultiLine;
	GsVector<GsGeometryPtr>::iterator it = vecGeo.begin();
	ptrMultiLine = *it;
	if (ptrMultiLine)
		it++;
	else
		ptrMultiLine = new GsPolyline();
	ptrMultiLine->CoordinateDimension(2);
	while (it != vecGeo.end())
	{
		(*it)->CoordinateDimension(2);
		if ((*it)->GeometryType() == eGeometryTypePath)
			ptrMultiLine->Add(*it);
		else if ((*it)->GeometryType() == eGeometryTypePolyline)
			ptrMultiLine->AddCollection(GsGeometryCollectionPtr(*it));

		it++;
	}
	//将合并的折线转换为Geomathse的对象
	geostar::gobjptr ptrGeo = ptrMultiLine->GeometryBlobPtr()->ToGeoSEObject();
	ptrGeo.attach(_ga().line_combine(ptrGeo, FLT_EPSILON, FLT_EPSILON));

	ptrMultiLine->GeometryBlobPtr()->FromGeoSEObject(ptrGeo, true);
	return ptrMultiLine;
} 
GsString ReadStringValue(GsFeature* fea, int nPos, Data::GsFieldType eType)
{
	if (nPos < 0)
		return GsString();
	switch (eType)
	{
	case GeoStar::Utility::Data::eErrorType:
	case GeoStar::Utility::Data::eBlobType:
	case GeoStar::Utility::Data::eGeometryType:
		return GsString();
	case GeoStar::Utility::Data::eBoolType:
		return fea->ValueInt(nPos) ? "true" : "false";
	case GeoStar::Utility::Data::eIntType:
	case GeoStar::Utility::Data::eUIntType:
		return GsStringHelp::ToString(fea->ValueInt(nPos));
	case GeoStar::Utility::Data::eInt64Type:
	case GeoStar::Utility::Data::eUInt64Type:
		return GsStringHelp::ToString(fea->ValueInt64(nPos));
	case GeoStar::Utility::Data::eStringType:
		return fea->ValueString(nPos);
	case GeoStar::Utility::Data::eFloatType:
		return GsStringHelp::ToString(fea->ValueFloat(nPos));
	case GeoStar::Utility::Data::eDoubleType:
		return GsStringHelp::ToString(fea->ValueDouble(nPos));
	case GeoStar::Utility::Data::eDateType:
		return fea->ValueDateTime(nPos).ToString();
	default:
		break;
	}
	return GsString();
}
void LabelCacheMaker::AddFeatureClass(GsFeatureClass* feaClass)
{

	m_ptrConfig = m_TileRuleFile.FindClassConfig(feaClass->Name().c_str());
	if (!m_ptrConfig)
	{
		GS_E << "can't find featureclass config or config file is error";
		return;
	}
	GsString strClassField = m_ptrConfig->ClassFieldName();
	GsString strLabelField = m_ptrConfig->LabelFieldName();
	GsQueryFilterPtr ptrQF = new GsQueryFilter();

	std::stringstream ss;
	ss << strLabelField.c_str()
		<< " is not null and length(" 
		<< strLabelField.c_str()
		<< ")>0 order by " << strLabelField.c_str() << " asc";

	ptrQF->WhereClause(ss.str().c_str());

	long long n = feaClass->FeatureCount(ptrQF);

	Progress progress(GsLogger::Default(), n);
	int nPos = feaClass->Fields().FindField(strLabelField.c_str());
	if (nPos < 0)
	{
		GS_E << "name field " << strLabelField.c_str() << " is not exist";
		return;
	}
	Data::GsFieldType eType = feaClass->Fields().Fields[nPos].Type;

	Data::GsFieldType eClassType = Data::eErrorType;
	//分类字段。
	int nClassPos = -1;
	if(!GsStringHelp::IsNullOrEmpty(strClassField))
		nClassPos = feaClass->Fields().FindField(strClassField.c_str());
	if (nClassPos < 0)
	{
		GS_W << "class field is invalid ";
		return;
	}
	else
		eClassType = feaClass->Fields().Fields[nClassPos].Type;

	GsFeatureCursorPtr ptrCursor = feaClass->Search(ptrQF);
	GsFeaturePtr ptrFea = ptrCursor->Next();
	if (!ptrFea)
		return;
	
	do
	{
		progress.Add();
		GsGeometryPtr ptrGeo = ptrFea->Geometry();
		GsString strVal = ReadStringValue(ptrFea,nPos, eType);
		if (GsStringHelp::IsNullOrEmpty(strVal))
			continue;
		GsString strValClass;
		if (nClassPos > 0)
			strValClass = ReadStringValue(ptrFea, nClassPos, eClassType);
		

		AddGeometry(strValClass.c_str(),strVal.c_str(), ptrGeo);
	} while (ptrCursor->Next(ptrFea));

	CommitCache();
}
void LabelCacheMaker::CommitCache()
{
	GsString strLabel = m_strVal;
	GsString strClass = m_strValClass;
	m_strVal.clear();
	m_strValClass.clear();
	if (m_vecCacheGeo.empty())
		return;

	GsGeometryPtr ptrMerge = Combine(m_vecCacheGeo);
	m_vecCacheGeo.clear();
	
	AddFeature(strClass.c_str(),strLabel.c_str(), ptrMerge);
}
void LabelCacheMaker::AddGeometry(const char* strClass, const char* label, KERNEL_NAME::GsGeometry* geo)
{
	if (!m_strVal.empty())
	{
		if (strcmp(label, m_strVal.c_str()) != 0)
		{
			CommitCache();
		}
	}
	if (m_strVal.empty())
	{
		m_strVal = label;
		m_strValClass = strClass;
	}
	m_vecCacheGeo.push_back(geo);
} 

GsVector<GsPathPtr> ToPathArray(KERNEL_NAME::GsGeometry* geo)
{
	GsVector<GsPathPtr> vec;
	GsPathPtr ptrPath = geo;
	if (ptrPath)
	{
		vec.push_back(ptrPath);
	}
	else
	{
		GsPolylinePtr ptrPolyline = geo;
		for (int i = 0; i < ptrPolyline->Count(); i++)
		{
			vec.emplace_back(ptrPolyline->Geometry(i));
		}
	}
	return vec;
}
void LabelCacheMaker::AddFeature(const TileRule& rule, const char* label, GsPath* path)
{
	/*int nDim = path->CoordinateDimension();
	int nSegCount = path->PointCount();
	double* pCoord = path->GeometryBlobPtr()->Coordinate();
	int ptCount = path->GeometryBlobPtr()->CoordinateLength();
	double len = path->GeoDesicLength();
	*/

	GsGeometryPtr ptrBreak = path->GeodesicInterpolate(rule.Delta);
	TraverseSegment(rule, ptrBreak, label);
}
void LabelCacheMaker::TraverseSegment(const TileRule& rule, GsPath* path, const char* label)
{
	GsGeometryBlob* blob = path->GeometryBlobPtr();
	double* pCoord = blob->Coordinate();
	std::vector<GsRawPoint> vecPoint;
	int nDim = blob->CoordinateDimension();
	double dblLen = 0;
	//从第一个点开始
	vecPoint.emplace_back(pCoord[0], pCoord[1]);
	pCoord += nDim;
	for (int i = 1; i < blob->PointCount(); i++, pCoord += nDim)
	{
		dblLen += m_Geodesic.Inverse(vecPoint.back().X, vecPoint.back().Y,
			pCoord[0], pCoord[1]);
		vecPoint.emplace_back(pCoord[0], pCoord[1]);
		//如果找到了一段。
		if (dblLen >= rule.Delta)
		{
			AddFeature(rule.Level, label, &vecPoint[0], vecPoint.size());
			dblLen = 0;
			vecPoint.clear();
			vecPoint.emplace_back(pCoord[0], pCoord[1]);
		}
	}
	//如果剩余的线段大于最小段的一半则添加之
	if (dblLen > rule.Delta /  2)
	{
		AddFeature(rule.Level, label, &vecPoint[0], vecPoint.size());
	}
}
GsRawPoint LabelCacheMaker::MiddlePoint(double dblLen, KERNEL_NAME::GsRawPoint* pt, int nLen)
{
	GsLinePtr ptrLine = new GsLine();
	double len = 0;
	for (int i = 1; i < nLen; i++)
	{
		double l = sqrt(pt[i - 1].Distance2(pt[i]));
		if (len + l < dblLen)
		{
			len += l;
			continue;
		}
		//如果正好大于一半的长度则找到了中点线段。
		ptrLine->Set(pt[i - 1], pt[i]);
		return ptrLine->Value((dblLen - len) / l);
	}

	//如果运行到这里那肯定是有问题了。
	return GsRawPoint();
}

void LabelCacheMaker::AssignDEM(int l,KERNEL_NAME::GsPath* path)
{
	if (!HasDEM())
		return ;
	if (path->CoordinateDimension() != 3)
		path->CoordinateDimension(3);
	
	GsRawPoint3D* ptr = path->GeometryBlobPtr()->PointHead<GsRawPoint3D>();
	for (int i = 0; i < path->GeometryBlobPtr()->PointCount(); i++)
	{
		AssignDEMValue(l, ptr[i]);
	}
} 
void LabelCacheMaker::AddFeature(int level, const char* label, KERNEL_NAME::GsRawPoint* pt, int nLen)
{
	GsPathPtr ptrPath = new GsPath();
	ptrPath->Set(pt, nLen);
	AssignDEM(level,ptrPath);
	 
	
	GsRawPoint ptMiddle = MiddlePoint(ptrPath->Length() / 2, pt, nLen);
	int row, col;
	m_ptrPyramid->TileIndex(level, ptMiddle.X, ptMiddle.Y, &row, &col);

	AddToTile(level, row, col, ptrPath, ptMiddle, label);
}
LabelTilePtr LabelCacheMaker::OpenTile(const UTILITY_NAME::GsQuadKey& key)
{
	bool bHas = false;
	if (m_ptrOutputTile)
		bHas  = m_ptrOutputTileClass->Tile(key.Level, key.Row, key.Col, m_ptrOutputTile);
	else
	{
		m_ptrOutputTile = m_ptrOutputTileClass->Tile(key.Level, key.Row, key.Col);
		if (m_ptrOutputTile)
			bHas = true;
	}
	LabelTilePtr ptrTile;
	if (bHas)
	{
		if (m_bJsonFormat)
		{
			const char* data = (const char*)m_ptrOutputTile->TileDataPtr();
			std::string str(data, data + m_ptrOutputTile->TileDataLength());
			ptrTile.reset(new JsonLabelTile(str.c_str(), key));
		}
		else
		{
			ptrTile.reset(new PBFLabelTile(m_ptrOutputTile->TileDataPtr(), m_ptrOutputTile->TileDataLength(),key));
		}
	}
	else
	{
		if (m_bJsonFormat)
			ptrTile.reset(new JsonLabelTile(key));
		else
			ptrTile.reset(new PBFLabelTile(key));

	}
	return ptrTile;
}
void LabelCacheMaker::AddToTile(int l, int r, int c, KERNEL_NAME::GsPath* path, const KERNEL_NAME::GsRawPoint& middle, const char* name)
{
	GsQuadKey key(l, r, c);
	LabelTilePtr ptrTile = m_TileCache.Query(key);
	if (!ptrTile)
	{
		ptrTile = OpenTile(key);
		m_TileCache.Add(key, ptrTile);
	}
	if (HasDEM())
	{
		GsRawPoint3D pt3(middle);
		AssignDEMValue(l, pt3);
		ptrTile->Add(path, pt3,name);
	}
	else
		ptrTile->Add(path, middle,name);
}
void LabelCacheMaker::TraverseSegment(const TileRule& rule, GsGeometry* geo, const char* label)
{
	GsPathPtr ptrPath = geo;
	if (ptrPath)
	{
		TraverseSegment(rule, ptrPath, label);
		return;
	}
	GsPolylinePtr ptrLine = geo;
	for (int i = 0; i < ptrLine->Count(); i++)
	{
		TraverseSegment(rule, GsPathPtr(ptrLine->Geometry(i)), label);
	}
}
void LabelCacheMaker::AddFeature(const TileRule& rule, const char* label, UTILITY_NAME::GsVector<GsPathPtr>& vec)
{
	GsVector<GsPathPtr>::iterator it = vec.begin();
	for (; it != vec.end(); it++)
	{
		AddFeature(rule, label, *it);
	}
}
bool ExistLevel(std::vector<int>& vecl, int n)
{
	std::vector<int>::iterator it = vecl.begin();
	for (; it != vecl.end(); it++)
	{
		if (*it == n)
			return true;
	}
	return false;
}
std::vector<TileRule> LabelCacheMaker::SelectRules(std::vector<int>& vecl)
{
	std::vector<TileRule> vecRules;
	std::vector<TileRule> ::iterator it = m_TileRuleFile.Rules().begin();
	for (; it != m_TileRuleFile.Rules().end(); it++)
	{
		if (ExistLevel(vecl, it->Level))
			vecRules.push_back(*it);
	}

	return vecRules;
}
void LabelCacheMaker::AddFeature(const char* strClass, const char* label, KERNEL_NAME::GsGeometry* geo)
{
	m_Extent.Union(geo->Envelope());
	//将多线转换为多个子线
	GsVector<GsPathPtr> vec = ToPathArray(geo);
	
	std::vector<int> vecLevels = m_ptrConfig->QueryClassLevels(strClass);
	if (vecLevels.empty())
		return;
	std::vector<TileRule> vecRules = SelectRules(vecLevels);

	std::vector<TileRule>::iterator it = vecRules.begin();
	for (; it != vecRules.end(); it++)
	{
		AddFeature(*it, label, vec);
	}
}