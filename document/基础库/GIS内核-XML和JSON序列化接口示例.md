GIS内核-XML和JSON序列化接口示例

```c++
#include <gstest.h>  
#include "geodatabase.h"
#include "layoutelement.h"
#include "layout.h"
using namespace  GeoStar::Kernel;
using namespace  GeoStar::Utility::Data;
using namespace  GeoStar::Utility;
#include "serialize.h"

GS_TEST(GsSerialize, JSON_GsSerialize_SR_ALL, chijing, 20201201)
{
    GsKernel::Initialize();
    GsSimplePointSymbolPtr ptrSym = new GsSimplePointSymbol(GsColor::Black, 1.0);
    GsJSONSerializeStreamPtr ptrXmlStream = new GsJSONSerializeStream("");
    GsAny Obj(ptrSym.p);
    bool bRes = ptrXmlStream->Save("Point", ptrSym.p);
    ASSERT_EQ(bRes, true);
    ptrXmlStream->Save("int", (int)1);
    ptrXmlStream->Save("uint", (unsigned int)1);
    ptrXmlStream->Save("int64", (long long)1);
    ptrXmlStream->Save("uint64", (unsigned long long)1);
    ptrXmlStream->Save("float", (float)1.0);
    ptrXmlStream->Save("double", (double)1.00000000000000);
    ptrXmlStream->Save("string", "string");
    ptrXmlStream->Save("date", GsDateTime::Now());
    GsGrowByteBuffer buff;
    buff.Append("34erwer");
    ptrXmlStream->Save("blob", buff.BufferHead(), buff.BufferSize());

    GsString str = ptrXmlStream->JSON();
    GsFile file(u8"D:\\a.json");
    file.AppendLine(str.c_str());
    bRes = GsStringHelp::IsNullOrEmpty(str.c_str());
    ASSERT_EQ(bRes, false);

    //Load一个对象
    GsJSONSerializeStreamPtr ptrXmlStreamLoad = new GsJSONSerializeStream(str.c_str());
    ASSERT_TRUE(ptrXmlStreamLoad);
    GsAny Out;
    GsRefObject* g = ptrXmlStreamLoad->LoadObjectValue("Point");
    ASSERT_TRUE(g);

    GsSimplePointSymbolPtr ptrSymtmp = GsSimplePointSymbolPtr(g);
    ASSERT_TRUE(ptrSymtmp);
    int a = ptrXmlStream->LoadIntValue("int", (int)0);
    unsigned int b = ptrXmlStream->LoadUIntValue("uint", (unsigned int)0);
    long long c = ptrXmlStream->LoadInt64Value("int64", (long long)0);
    unsigned long long uc = ptrXmlStream->LoadUInt64Value("uint64", (unsigned long long)0);
    float f = ptrXmlStream->LoadFloatValue("float", (float)0);
    double d = ptrXmlStream->LoadDoubleValue("double", (double)0);
    GsString strg  =ptrXmlStream->LoadStringValue("string", "0");
    GsDateTime vt = ptrXmlStream->LoadDateTimeValue("date", GsDateTime::Now());
    GsAny blobvalue;
    GsAny blobret =  ptrXmlStream->LoadBlobValue("blob", blobvalue);
}

GS_TEST(GsSerialize, GsSerialize_Point, yulei, 20180917)
{
	GsSimplePointSymbolPtr ptrSym = new GsSimplePointSymbol(GsColor::Black, 1.0);
	GsXMLSerializeStreamPtr ptrXmlStream = new GsXMLSerializeStream("");
	GsAny Obj(ptrSym.p);
	bool bRes = ptrXmlStream->SaveProperty("Point", Obj);
	ASSERT_EQ(bRes, true);
	GsString str = ptrXmlStream->XML();
	bRes = GsStringHelp::IsNullOrEmpty(str.c_str());
	ASSERT_EQ(bRes, false);

	//Load一个对象
	GsXMLSerializeStreamPtr ptrXmlStreamLoad = new GsXMLSerializeStream(str.c_str());
	ASSERT_TRUE(ptrXmlStreamLoad);
	GsAny Out;
	GsRefObject* g = ptrXmlStreamLoad->LoadObjectValue("Point");
	ASSERT_TRUE(g);
	GsSimplePointSymbolPtr ptrSymtmp = GsSimplePointSymbolPtr(g);
	ASSERT_TRUE(ptrSymtmp);
}

```

