# GIS内核-读取符号库,把符号绘制成图片 #

    std::string  path = u8"C:\\Users\\chijing\\Desktop\\test.symx";  
    GsSymbolLibrary sym(path.c_str());  
    GsSymbolPtr pSimpletttt = sym.SymbolByName(u8"干涸湖");  
      
    GsFillSymbolPtr pSimple = pSimpletttt;// new GsSimpleFillSymbol(GsColor::Blue);  
    GsSimpleLineSymbolPtr pSimplelineym = pSimple->Outline();  
    pSimplelineym->Width(5);  
    pSimplelineym->LineStyle(eDashLine);  
    int w = 256;  
    GsMemoryImageCanvasPtr pCanvas = (new GsMemoryImageCanvas(w, w));  
    pCanvas->Clear(GsColor(0, 0, 0, 0));  
    GsSymbolPtr ptrSymbase = pSimpletttt;  
      
    GsDisplayTransformationPtr pDT = new GsDisplayTransformation(GsBox(0, 0, w, w), GsRect(0, 0, w, w));  
    ptrSymbase->StartDrawing(pCanvas, pDT);  
    GsEnvelopePtr ptrEnv = new GsEnvelope(GsBox(50, 50, w-50, w-50));  
      
    ptrSymbase->Draw(ptrEnv);  
    ptrSymbase->EndDrawing();  
      
    //保存结果。  
    const char* strOutput = "D:\\a.png";  
    pCanvas->Image()->SavePNG(strOutput);  