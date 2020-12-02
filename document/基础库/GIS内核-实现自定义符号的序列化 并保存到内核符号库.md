GIS内核-实现自定义符号 并保存到符号库

```c++
#include <gstest.h>  
#include "geodatabase.h"
#include "layoutelement.h"
#include "layout.h"
using namespace  GeoStar::Kernel;
using namespace  GeoStar::Utility::Data;
using namespace  GeoStar::Utility;
#include "serialize.h"

class GsMySymbol :public GsSymbol2D, public GsSerialize
{

public:
	GsString Str1;
	int		 n2;
	GsColor col;
	GsMySymbol() 
	{

	}
	// 通过 GsSymbol2D 继承
	virtual GsSymbolType Type() override
	{
		return e3DSymbol;
	}

	// 通过 GsSerialize 继承
	virtual bool Serialize(GsSerializeStream * pSerStream) override
	{
		if (!pSerStream)
		{
			return false;
		}
		pSerStream->Save("s1", Str1);
		pSerStream->Save("n2", n2);
		pSerStream->Save("c3", col.ToHtml());
		return true;
	}
	virtual bool DeSerialize(GsSerializeStream * pSerStream) override
	{
		if (!pSerStream)
		{
			return false;
		}
		Str1 = pSerStream->LoadStringValue("s1", "");
		n2 = pSerStream->LoadIntValue("n2", 0);
		GsString gcol = pSerStream->LoadStringValue("c3", "");
		col = col.FromCSS(gcol.c_str());
		return true;
	}
	virtual GsString ClassName() override
	{
		return "GsMySymbol";
	}
	DECLARE_CLASS_NAME(GsMySymbol);
};
GS_SMARTER_PTR(GsMySymbol);
DECLARE_CLASS_CREATE(GsMySymbol);
DECLARE_CLASS_CREATE_IMP(GsMySymbol);

//实现自定义符号库的保存XML到symx文件中
class MySymbolLibrary :public GsSymbolLibrary
{
public:
	MySymbolLibrary()
	{

	}
	MySymbolLibrary(const char* strSymbolLib, bool bFileName = true):GsSymbolLibrary(strSymbolLib,bFileName)
	{

	}
	virtual bool OnSymbolRead(GsSymbol* pSymbol, UTILITY_NAME::GsSerializeStream* pStream)
	{
		GsMySymbolPtr g = pSymbol;
		if (!g)
			return false;
		g->DeSerialize(pStream);
		return true;
	}
	// 执行写符号操作
	virtual bool OnSymbolWrite(GsSymbol* pSymbol, UTILITY_NAME::GsSerializeStream* pStream)
	{
		GsMySymbolPtr g = pSymbol;
		if (!g)
			return false;
		pStream->Save("ClassName", g->FullClassName());
		g->Serialize(pStream);
		return true;
	}

};

//符号库保存  符号库内部用xml序列化
GS_TEST(GsSerialize, CustomSymbol_GsSerialize_SR_ALL, chijing, 20201201)
{
    //注册符号
	REG_CLASS_CREATE(GsMySymbol);
	REG_CLASS_CREATE_ALIAS(GsMySymbol, "7095BC37-E9B1-444A-BBB1-A53830A6646B");
	REG_CLASS_CREATE_ALIAS(GsMySymbol, "GeoStarCore.GsmySymbol.1");
	REG_CLASS_CREATE_ALIAS(GsMySymbol, "GeoStarCore.GsmySymbol");
	REG_CLASS_CREATE_ALIAS(GsMySymbol, "GsMySymbol");

	MySymbolLibrary b;
	GsMySymbolPtr sym = new GsMySymbol();
	sym->col = GsColor::Green;
	sym->Str1 = "string";
	sym->n2 = 3;
	b.Symbols()->push_back(sym.p);
	b.Save(u8"D:\\a.xml");

	MySymbolLibrary c(u8"D:\\a.xml");
	c.Symbols();
}

//单独序列化和保存
GS_TEST(GsSerialize, CustomSymbol_XMLjson_GsSerialize_SR_ALL, chijing, 20201201)
{


	GsMySymbolPtr sym = new GsMySymbol();
	sym->col = GsColor::Green;
	sym->Str1 = "string";
	sym->n2 = 3;

	GsXMLSerializeStreamPtr ptrSaveXMLSerialize = new GsXMLSerializeStream("");
	GsJSONSerializeStreamPtr  ptrSaveJSONSerialize = new GsJSONSerializeStream("");
	ptrSaveXMLSerialize->Save("sym", sym.p);
	ptrSaveJSONSerialize->Save("sym", sym.p);
	GsString saveXML =  ptrSaveXMLSerialize->XML();
	GsString SaveJSON =  ptrSaveJSONSerialize->JSON();
	GsXMLSerializeStreamPtr ptrReadXMLSerialize = new GsXMLSerializeStream(saveXML);
	GsJSONSerializeStreamPtr  ptrReadJSONSerialize = new GsJSONSerializeStream(SaveJSON);
	GsMySymbolPtr p1 = ptrReadXMLSerialize->LoadObjectValue("sym");
	GsMySymbolPtr p2 =  ptrReadJSONSerialize->LoadObjectValue("sym");
}

```

