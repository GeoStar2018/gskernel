#include "poitile.h"
#include <iostream>
#include <protozero/pbf_reader.hpp>
#include <protozero/pbf_writer.hpp>
POI::POI()
{
	// 解释串类型，对于MultiLineString，Polygon等复杂类型有效。
	m_Interate = 0;
	//符号ID
	m_SymbolID = 0;
	//显示的高度，缺省32像素
	m_DisplayHeight = 32;
	//光晕颜色
	m_ShiningColor = 0;
	//字体名称的索引
	m_FontNameIndex = 0;
	//字体大小
	m_FontSize = 18;
	//字体颜色。
	m_FontColor = 0;

	m_ZCoordType = eRelativelyGround;
}
POI::POI(const unsigned char* data, size_t n)
{
	protozero::pbf_reader r(protozero::data_view((const char*)data,n));

	//解释串类型，对于MultiLineString，Polygon等复杂类型有效。
	m_Interate = 0;
	//符号ID
	m_SymbolID = 0;
	//显示的高度，缺省32像素
	m_DisplayHeight = 32;
	//光晕颜色
	m_ShiningColor = 0;
	//字体名称的索引
	m_FontNameIndex = 0;
	//字体大小
	m_FontSize = 18;
		//字体颜色。
	m_FontColor = 0;
	 
	m_ZCoordType = eRelativelyGround;

	while (r.next())
	{
		int tag = r.tag();
		if (tag == 1)
			m_OID = r.get_uint64();
		else if (tag == 2)
			m_Name = r.get_string();
		else if (tag == 3)
		{
			auto val = r.get_packed_double();
			protozero::const_fixed_iterator<double> it = val.begin();
			for (; it != val.end(); it++)
				m_Coordinates.emplace_back(*it);
		}
		else if (tag == 4)
			m_GeometryType = (enumGeometryType)r.get_enum();
		else if (tag == 5)
			m_Interate = r.get_int32();
		else if (tag == 10)
			m_SymbolID = r.get_int32();
		else if (tag == 11)
			m_DisplayHeight = r.get_double();
		else if (tag == 12)
			m_ShiningColor = r.get_uint32();
		else if (tag == 13)
			m_FontNameIndex = r.get_uint32();
		else if (tag == 14)
			m_FontSize = r.get_uint32();
		else if (tag == 15)
			m_FontColor = r.get_uint32();
		else if (tag == 16)
			m_ZCoordType = (enumZCoordType)r.get_enum();
		else
			r.skip();

	}
} 

//序列化数据
void POI::Save(std::string& data)const
{
	protozero::pbf_writer w(data);
	int  i = 1;
	w.add_uint64(i++, m_OID);
	w.add_string(i++, m_Name);
	w.add_packed_double(i++, m_Coordinates.begin(), m_Coordinates.end());
	w.add_enum(i++, m_GeometryType);
	if(m_Interate != 0)
		w.add_int32(i++, m_Interate);
	
	i = 10;
	w.add_int32(i++, m_SymbolID);
	w.add_double(i++, m_DisplayHeight);
	w.add_uint32(i++, m_ShiningColor);
	w.add_uint32(i++, m_FontNameIndex);
	w.add_uint32(i++, m_FontSize);
	w.add_uint32(i++, m_FontColor);
	w.add_int32(i++, m_ZCoordType);
}
POITile::POITile()
{

}
POITile::POITile(const unsigned char* data, size_t nLen)
{
	protozero::data_view view((const char*)data,nLen);
	protozero::pbf_reader r(view);
	while (r.next())
	{
		if (r.tag() == 1)
			m_Version = r.get_int64();
		else if (r.tag() == 2)
			m_TileKey = r.get_int64();
		else if (r.tag() == 3)
		{
			protozero::pbf_reader rr = r.get_message();
			while (rr.next())
			{
				if (rr.tag() == 1)
					m_StringTable.emplace_back(rr.get_string());
				else
					r.skip();
			}
		}
		else if (r.tag() == 4)
		{
			protozero::data_view v = r.get_view();

			this->emplace_back((const unsigned char*)v.data(), v.size());
		}
		else 
			r.skip();
	}

}


POITile::~POITile()
{
}
//序列化数据
void POITile::Save(std::string& str)const
{
	protozero::pbf_writer w(str);
	w.add_int64(1, m_Version);
	w.add_int64(2, m_TileKey);
	
	if(m_StringTable.size() >0)
	{
		std::string strTab;
		{
			protozero::pbf_writer wtb(strTab);
			std::vector<std::string>::const_iterator it = m_StringTable.begin();
			for (; it != m_StringTable.end(); it++)
			{
				wtb.add_string(1, it->c_str());
			}
		}
		w.add_message(3, strTab);
	}
	

	std::vector<POI>::const_iterator it = this->begin();

	std::string sub;
	for (; it != this->end(); it++)
	{
		sub.clear();
		it->Save(sub);
		w.add_message(4, sub);
	}


}
long long& POITile::Version()
{
	return m_Version;
}
long long& POITile::TileKey()
{
	return m_TileKey;
}
std::vector<std::string>& POITile::StringTable()
{
	return m_StringTable;
}
