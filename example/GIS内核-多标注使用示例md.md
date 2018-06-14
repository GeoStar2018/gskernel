# GIS内核-多标注使用示例 #
    
    //多标注代码  
    void AddFeatureLayerMutilLabel()  
    {  
      
    ////GsShpGeoDatabaseFactory shpFac;  
    //GsSqliteGeoDatabaseFactoryPtr fcsFac = new GsSqliteGeoDatabaseFactory();  
    GsShpGeoDatabaseFactoryPtr shpFac = new GsShpGeoDatabaseFactory();  
    GsGeoDatabaseFactoryPtr fac = shpFac;  
    GsConnectProperty cp;  
    cp.DataSourceType = GsDataSourceType::eShapeFile;  
      
    GsString str = "../tests/testdata/shp/";  
    GsFile modelFile(GsFileSystem::ModuleFileName().c_str());  
    GsString fullPath = GsFileSystem::Combine(modelFile.Parent().FullPath().c_str(), str.c_str());  
    cp.Server =  fullPath;  
      
    GsGeoDatabasePtr db = fac->Open(cp);  
    GsFeatureClassPtr fcs = db->OpenFeatureClass("RES1_4M_P");  
    GsLayer * gsLyr = new GsVectorLayer(fcs);  
    GsFeatureLayerPtr ptest = gsLyr;  
    GsTextSymbolPtr ptrSymbol = new GsTextSymbol();  
    ptrSymbol->Color(GsColor::Red);  
    ptrSymbol->Size(1);  
    ptrSymbol->Height(6);  
    ptrSymbol->Width(6);  
    GsTextSymbolPtr ptrSymbol2 = ptrSymbol->Clone();  
    ptrSymbol2->Color(GsColor::Black);  
    ptrSymbol2->Size(1);  
    ptrSymbol2->Height(6);  
    ptrSymbol2->Width(6);  
    ptest->Renderer()->RenditionMode(eMultiRendition);  
    GsPointLabelPropertyPtr pLabelProp1 = new GsPointLabelProperty();  
    pLabelProp1->PlaceOrder(eTopRight, eHighPriority);  
    pLabelProp1->LabelField("GEOGLOBE_P");  
      
    pLabelProp1->DrawPointAndLabel(true);  
    pLabelProp1->Symbol(ptrSymbol);  
      
    pLabelProp1->PointSpaceByLabel(4);  
    GsLabelRenditionPtr ptrLabR1 = new GsLabelRendition(pLabelProp1);  
      
    GsPointLabelPropertyPtr pLabelProp2 = new GsPointLabelProperty();  
    pLabelProp2->PlaceOrder(eBottomRight, eLowPriority);  
    pLabelProp2->LabelField("GBCODE");  
    pLabelProp2->DrawPointAndLabel(true);  
    pLabelProp2->Symbol(ptrSymbol2);   
    pLabelProp2->PointSpaceByLabel(4);  
    GsLabelRenditionPtr ptrLabR2 = new GsLabelRendition(pLabelProp2);  
      
    m_ptrLabel = new GsSimpleLabelContainer();  
    space->m_ptrGeoMap->ScreenDisplay()->LabelContainer(m_ptrLabel);  
    space->m_ptrGeoMap->ScreenDisplay()->LabelContainer()->Enabled(true);  
    space->m_ptrGeoMap->ScreenDisplay()->LabelContainer()->AutoLabel(false);  
    ptest->Renderer()->AddRendition(ptrLabR1);  
    ptest->Renderer()->AddRendition(ptrLabR2);  
    //添加图层到底图,并全图显示  
    space->layerBox().addLayer(gsLyr);  
    space->update();  
    space->viewFullExtent();  }  