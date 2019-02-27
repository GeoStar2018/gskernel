#include "stdafx.h"
#include "DEMSupport.h"
using namespace KERNEL_NAME;
using namespace UTILITY_NAME;
void DEMTile::Bind(bool bShort, int nSamples, double InvalidValue)
{
	m_bShortType = bShort;
	if (m_bShortType)
		m_ShortDEM.Attach(Data.PtrT<short>(), Extent, nSamples, InvalidValue);
	else
		m_FloatDEM.Attach(Data.PtrT<float>(), Extent, nSamples, InvalidValue);

}
double DEMTile::DEMValue(double x, double y, double z)
{
	if (m_bShortType)
		return m_ShortDEM.Elevation(x, y);
	return m_FloatDEM.Elevation(x, y);
}

DEMSupport::DEMSupport(const char* dem)
{
	m_nSamples = 150;
	m_InvalidValue = -1;
	m_bShortType = false;

	GsConnectProperty conn;
	GsFile f(dem);
	conn.Server = f.Parent().FullPath();
	GsGeoDatabasePtr ptrGDB = GsSqliteGeoDatabaseFactory().Open(conn);
	if (!ptrGDB)
	{
		GS_E << "open dem database faild";
		return;
	}
	m_ptrDEMTile = ptrGDB->OpenTileClass(f.Name(false));
	if (!m_ptrDEMTile)
	{
		GS_E << "open dem dataset faild";
		return;
	}

	m_Col = m_ptrDEMTile->TileColumnInfo();
	m_ptrPyramid = m_ptrDEMTile->Pyramid();

	m_nSamples = GsCRT::_atoi64(m_ptrDEMTile->MetadataItem("DEM", "Samples").c_str());
	if (m_nSamples <= 0) m_nSamples = 150;

	m_InvalidValue = GsCRT::_atof(m_ptrDEMTile->MetadataItem("DEM", "InvalidValue").c_str());

	GsString strType = m_ptrDEMTile->MetadataItem("DEM", "ValueType");
	if (GsCRT::_stricmp(strType.c_str(), "int16") == 0)
		m_bShortType = true;
	else
		m_bShortType = false;
}

//准备地形瓦片
DEMTile* DEMSupport::EnsureTile(const UTILITY_NAME::GsQuadKey& key)
{
	//小于瓦片最小级别了则不遍历了。
	if (key.Level < m_Col.ValidTopLevel)
		return NULL;

	std::map<UTILITY_NAME::GsQuadKey, std::list<DEMTile>::iterator>::iterator it = m_MapKey.find(key);
	if (it != m_MapKey.end())
	{
		if (it->second != m_CacheDEM.begin())
			m_CacheDEM.splice(m_CacheDEM.begin(), m_CacheDEM, it->second);
		return &(*it->second);
	}
	bool bHasData = false;
	//如果没有找到则从数据集中读取。
	if (m_ptrDEM)
		bHasData = m_ptrDEMTile->Tile(key.Level, key.Row, key.Col, m_ptrDEM);
	else
	{
		m_ptrDEM = m_ptrDEMTile->Tile(key.Level, key.Row, key.Col);
		if (m_ptrDEM)
			bHasData = true;
	}

	//没有数据则获取父。
	if (!bHasData)
		return EnsureTile(key.Parent());

	m_CacheDEM.emplace_front();
	DEMTile& tile = m_CacheDEM.front();
	tile.Key = key;
	m_ptrPyramid->TileExtent(key.Level, key.Row, key.Col, &tile.Extent.XMin, &tile.Extent.YMin, &tile.Extent.XMax, &tile.Extent.YMax);

	unsigned char* data = m_ptrDEM->TileDataPtr();
	unsigned int nLen = m_ptrDEM->TileDataLength();
	if (GsZLib::IsZlibCompressed(data, nLen))
	{
		m_ZipBuffer.Allocate(0);
		GsZLib::Uncompress(data, nLen, &m_ZipBuffer);
		data = m_ZipBuffer.BufferHead();
		nLen = m_ZipBuffer.BufferSize();
	}
	else if (GsGZipFile::IsGZipCompressed(data, nLen))
	{
		m_ZipBuffer.Allocate(0);
		GsGZipFile::Decompress(data, nLen, &m_ZipBuffer);
		data = m_ZipBuffer.BufferHead();
		nLen = m_ZipBuffer.BufferSize();
	}

	tile.Data.Append(data, nLen);
	tile.Bind(m_bShortType, m_nSamples, m_InvalidValue);

	m_MapKey[key] = m_CacheDEM.begin();
	if (m_CacheDEM.size() > 10240)
	{
		m_MapKey.erase(m_CacheDEM.back().Key);
		m_CacheDEM.pop_back();
	}
	return &m_CacheDEM.front();

}

DEMSupport::~DEMSupport()
{
}
