GIS内核读取符号库文件(*.SYMX)

	查找code为43的符号
    
	
	GsSymbolLibrary lib("C:\\*.SYMX");
	
	GsSymbolPtr ptrSym = lib.SymbolByCode(43);
	GsSymbolPtr ptrSym = lib.SymbolByName("a");
	//遍历所有符号
	std::vector<GsSymbolPtr>::iterator it = lib.Symbols()->begin();
	for(; it != lib.Symbols()->end(); it++)
	{
		if((*it)->Code() == 43)
		{
			ptrSym = *it;
			break;
		}
	}
	//获取所有点符号
	UTILITY_NAME::GsVector<GsSymbolPtr> vecSyms = lib.Symbols(ePointSymbol);
	//添加符号到符号库,保存符号库文件
	GsSimplePointSymbolPtr ptrSpoint =  new GsSimplePointSymbol();
	lib.Symbols()->push_back(ptrSpoint);
	lib.Save("C:\\2.SYMX");

