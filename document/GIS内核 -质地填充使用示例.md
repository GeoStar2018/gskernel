# GIS内核 -质地填充使用示例 #

    void GsUniqueValueRendererTest(GsFeatureLayer * pLayer)  
    {  
    GsUniqueValueRendererPtr pRender = new GsUniqueValueRenderer(new GsSimpleFillSymbol(GsColor::Red));  
    pRender->FieldName("OID");  
    GsSimpleFillSymbolPtr ptrSym1 = new GsSimpleFillSymbol(GsColor::Green);  
    ptrSym1->Outline()->Color(GsColor::Red);  
    ptrSym1->Outline()->Width(1);  
    pRender->Symbol(1, ptrSym1);  
      
    GsSimpleFillSymbolPtr ptrSym2= new GsSimpleFillSymbol(GsColor::Black);  
    ptrSym2->Outline()->Color(GsColor::LightYellow);  
    ptrSym2->Outline()->Width(3);  
    pRender->Symbol(2, ptrSym2);  
      
    GsSimpleFillSymbolPtr ptrSym3 = new GsSimpleFillSymbol(GsColor::Yellow);  
    ptrSym3->Outline()->Color(GsColor::AliceBlue);  
    ptrSym3->Outline()->Width(5);  
    pRender->Symbol(3, ptrSym3);  
      
    pRender->IsUseDefaultSymbol(true);  
    pLayer->Renderer(pRender);  
    }  