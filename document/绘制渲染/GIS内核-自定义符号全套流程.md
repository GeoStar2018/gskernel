GIS内核-自定义符号全套流程

本文只对整体流程说明.  例子以实现一个自定义符号为例;

需求: 线图层对于大于100m得线不得绘制

1. ##### 符号代码实现流程

   编写符号类绘制实现

   编写序列化代码

   继承并扩展一个符号库对象

   编写注册函数

   ```c++
   
   /// \brief 符号实现
   class GS_API GsLineTruncationSymbol : public GsSymbol, public UTILITY_NAME::GsSerialize
   {
   	GsSimpleLineSymbolPtr m_SimpleLineSym;
   	double m_dblThreshold;
   public:
   
   
   	/// \brief 绘制几何对象
   	/// \param pGeo 
   	/// \return 
   	virtual bool Draw(GsGeometry* pGeo)
   	{
   		if (!pGeo)
   			return false;
   		GeoStar::Kernel::GsPolylinePtr ptrLine = pGeo;
   		if (ptrLine->Length() > 100)
   			return true;
   		return m_SimpleLineSym->Draw(pGeo);
   	}
   	GsLineTruncationSymbol()
   	{
   
   	}
   	GsLineTruncationSymbol(double dblThreshold,double linewidth,const GsColor& color)
   	{
   		m_dblThreshold = dblThreshold;
   		m_SimpleLineSym = new GsSimpleLineSymbol(color,linewidth);
   	}
   	// 通过 GsSymbol 继承
   	virtual bool HasStartDrawing() override
   	{
   		return m_SimpleLineSym->HasStartDrawing();
   	}
   	virtual bool EndDrawing() override
   	{
   		return m_SimpleLineSym->EndDrawing();
   	}
   	virtual GsSymbolType Type() override
   	{
   		return GsSymbolType::eCustomSymbol;
   	}
   
   	// 通过 GsSerialize 继承
   	virtual bool Serialize(GsSerializeStream * pSerStream) override
   	{
   		pSerStream->Save("DBL_Threshold", m_dblThreshold);
   		return pSerStream->Save("LineTruncationSymbol_Line", m_SimpleLineSym.p);
   	}
   	virtual bool DeSerialize(GsSerializeStream * pSerStream) override
   	{
   		m_dblThreshold = pSerStream->LoadDoubleValue("DBL_Threshold",1);
   		return m_SimpleLineSym = pSerStream->LoadObjectValue("LineTruncationSymbol_Line");
   
   	}
   	virtual GsString ClassName() override
   	{
   		return "GsLineTruncationSymbol";
   	}
   	DECLARE_CLASS_NAME(GsLineTruncationSymbol);
   };
   /// \brief 智能指针定义
   GS_SMARTER_PTR(GsLineTruncationSymbol);
   /// \brief 注册定义
   DECLARE_CLASS_CREATE(GsLineTruncationSymbol);
   DECLARE_CLASS_CREATE_IMP(GsLineTruncationSymbol);
   
   /// \brief 符号库定义
   class GS_API GsCustomSymbolLibrary : public GsSymbolLibrary
   {
   public:
   	GsCustomSymbolLibrary()
   	{
   
   	}
   
   	GsCustomSymbolLibrary(const char* strSymbolLib, bool bFileName = true) : GsSymbolLibrary(strSymbolLib, bFileName)
   	{
   
   	}
   
   protected:
   
   	// 执行读符号操作
   	bool OnSymbolRead(GsSymbol* pSymbol, UTILITY_NAME::GsSerializeStream *pStream)
   	{
   		GsLineTruncationSymbolPtr ptrLTSym = pSymbol;
   		if (!ptrLTSym || !pStream)
   			return false;
   
   		return ptrLTSym->DeSerialize(pStream);
   	}
   
   	// 执行写符号操作
   	bool OnSymbolWrite(GsSymbol* pSymbol, UTILITY_NAME::GsSerializeStream *pStream)
   	{
   		GsLineTruncationSymbolPtr ptrLTSym = pSymbol;
   		if (!ptrLTSym || !pStream)
   			return "";
   
   		pStream->Save("ClassName", ptrLTSym->ClassName());
   		return ptrLTSym->Serialize(pStream);
   	}
   };
   
   /// \brief 注册调用, 需要在系统初始化时候调用
   static void Symbol3Init()
   {
   	REG_CLASS_CREATE(GsLineTruncationSymbol);
   	REG_CLASS_CREATE_ALIAS(GsLineTruncationSymbol, "{1DE845A8-700B-409C-928A-F0723FD55FD2}");
   	REG_CLASS_CREATE_ALIAS(GsLineTruncationSymbol, "GeoStarCore.GsLineTruncationSymbol.1");
   	REG_CLASS_CREATE_ALIAS(GsLineTruncationSymbol, "GeoStarCore.GsLineTruncationSymbol");
   	REG_CLASS_CREATE_ALIAS(GsLineTruncationSymbol, "LineTruncationSymbol");
   }
   ```

   

      

2.  ##### 符号调用方式

   1.  添加到图层,保存到地图定义文件

   2.  从地图定义文件获取

   3. 符号保存到符号库单独文件

      ```C++
      //这里是数据源,参考数据源实现
      GsFeatureClassPtr OpenFeatureClass(const char* path)
      {
      	GsSqliteGeoDatabaseFactory fac;
      	GsConnectProperty conn;
      	GsFile file(path);
      
      	conn.Server = file.FullPath().c_str();
      	return fac.Open(conn)->OpenFeatureClass("BOU1_4M_L.fcs");
      
      }
      
      //符号添加到图层保存到地图定义文件
      void UserSymbolSaveToGMAPX(const GsString & strFIle1,const GsString & strFIle)
      {
      	Symbol3Init();
      	
      	GsMapPtr map = new GsMap();
      	GsFeatureClassPtr pFcs = OpenFeatureClass(strFIle1.c_str());
      	GsFeatureLayerPtr layer = new GsFeatureLayer(pFcs);
      	GsSimpleFeatureRendererPtr ptrrender = new GsSimpleFeatureRenderer();
      	GsLineTruncationSymbolPtr ptrSym = new GsLineTruncationSymbol(100, 2, GsColor(1, 1, 1));
      	ptrrender->Symbol(ptrSym);
      	layer->Renderer(ptrrender);
      	map->LayerCollection()->Add(layer);
      
      	GsMapDefine MapDefine;
      	GsCustomSymbolLibrary* lib = new GsCustomSymbolLibrary();
      	MapDefine.SymbolLibrary(lib);
      	MapDefine.SaveMap(map, strFIle.c_str());
      	delete lib;
      }
      
      //从地图定义文件读取符号
      void GMAPXToUserSymbol(const GsString & strFIle)
      {
      	Symbol3Init();
      
      	GsMapPtr ptrMap = new GsMap();
      	GsMapDefine MapDefine;
      	GsCustomSymbolLibrary lib;
      	MapDefine.SymbolLibrary(&lib);
      	MapDefine.ParserMap(strFIle.c_str(), ptrMap);
      	GsFeatureLayerPtr ptrlayer =  ptrMap->Layers()->at(0);
      	GsSimpleFeatureRendererPtr ptrRender =  ptrlayer->Renderer();
      	GsLineTruncationSymbolPtr symbol = ptrRender->Symbol();
      
      }
      
      //符号保存为符号库和单独使用符号
      void UserSymbolSaveToSymbolLibrary(GsString & strFolder)
      {
      	Symbol3Init();
      
      	GsString f1 =  GsFileSystem::Combine(strFolder.c_str(), "a.symx");
      	GsString f2 = GsFileSystem::Combine(strFolder.c_str(), "b.GSYMX");
      	GsLineTruncationSymbolPtr ptrSym = new GsLineTruncationSymbol(2, 2, GsColor(1, 1, 1));
      	ptrSym->Name("mysymbol");
      	//保存符号到符号库
      	GsCustomSymbolLibrary lib(f1.c_str());
      	lib.Symbols()->push_back(ptrSym);
      	lib.Save();
      
      	//从符号库读取符号
      	GsCustomSymbolLibrary lib2(f1.c_str());
      	GsLineTruncationSymbolPtr nmk =	lib2.SymbolByName("mysymbol");
      
      
      
      	//保存符号到符号库
      	GsCustomSymbolLibrary lib3(f2.c_str());
      	lib3.Symbols()->push_back(ptrSym);
      	lib3.Save();
      
      	//从符号库读取符号
      	GsCustomSymbolLibrary lib4(f2.c_str());
      	GsLineTruncationSymbolPtr nmk4 = lib4.SymbolByName("mysymbol");
      
      
      	//直接保存一个符号为字符串xml
      	GsXMLSerializeStreamPtr ptrS = new GsXMLSerializeStream();
      	ptrSym->Serialize(ptrS);
      	GsString  strxml = ptrS->XML();
      
      	//直接保存一个符号为字符串json
      	GsJSONSerializeStreamPtr ptrJ = new GsJSONSerializeStream();
      	ptrSym->Serialize(ptrJ);
      	GsString  strjson = ptrJ->JSON();
      
      	//从xml初始化GsLineTruncationSymbol符号
      	GsLineTruncationSymbolPtr ptrR = new GsLineTruncationSymbol();
      	GsXMLSerializeStreamPtr ptrSS = new GsXMLSerializeStream(strxml);
      	ptrR->DeSerialize(ptrSS);
      	//从json初始化GsLineTruncationSymbol符号
      	GsJSONSerializeStreamPtr ptrJJ = new GsJSONSerializeStream(strjson);
      	ptrR->DeSerialize(ptrJJ);
      }
      
      
      
      GS_TEST(GsCustomSymbolLibrary, SR, chijing, 20211118)
      {
      	GsString strString =  MakeOutputFolder("../testdata/symbol");
      	UserSymbolSaveToSymbolLibrary(strString);
      }
      
      GS_TEST(GsCustomSymbolLibrary, SR1, chijing, 20211118)
      {
      	GsString strString = MakeOutputFile("../testdata/symbol/abc.GMAPX");
      	GsString strFile = MakeInputFolder("..\\testdata\\400sqlite");
      	UserSymbolSaveToGMAPX(strFile, strString);
      }
      
      GS_TEST(GsCustomSymbolLibrary, SR2, chijing, 20211118)
      {
      	GsString strString = MakeOutputFile("../testdata/symbol/abc.GMAPX");
      	GMAPXToUserSymbol(strString);
      }
      ```
   
      