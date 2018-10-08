# GIS内核-制图使用示例 #

制图需要将制图对象和视图map绑定起来,并且刷新的时候不能调用map的update,只有退出视图模式才能调用,否则可能产生线程安全问题, 也就是要控制视图状态,基本的使用如下:

当前视图切换为制图视图模式:
    
    GsPageLayout m_pLayout = new GsPageLayout(space->m_ptrGeoMap->ScreenDisplay());  
    m_pLayout->ViewExtent(space->fullExtent());  
    m_pLayout->Page()->PageType(ePageA6);//这里是固定类型的枚举  
    m_pMapEle = new GsMapElement(space->m_ptrGeoMap);//图廓元素  
    m_pMapEle->ShowBorder(true);  
       //边线  
    GsSimpleLineSymbolPtr ptrLine = new GsSimpleLineSymbol(GsColor::Blue, 10);  
    m_pMapEle->BorderSymbol(ptrLine);  
    //添加制图的element  
    space->m_pLayout->ElementContainer()->Add(m_pMapEle);  
    //添加制图的element  
    GsPointPtr point = new GsPoint(100, 36);<span style="white-space:pre;"> </span>  
    GsSimplePointSymbolPtr ptrSymBol = new GsSimplePointSymbol(GsColor::Red,20);<span style="white-space:pre;"> </span>  
    GsGeometyElementPtr pteGeometryEle = new GsGeometyElement(point, ptrSymBol);  
    space->m_pLayout->ElementContainer()->Add(pteGeometryEle);  
    //刷新  
    space->m_isLayOut = true;  
    space->stopRendering();  
    space->update(); 

如果需要加入不同的element 可以通过geometry和symbol 配合使用基本可以达到一般制图效果  

