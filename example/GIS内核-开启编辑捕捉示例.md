# GIS内核-开启编辑捕捉示例 #

内核编辑捕捉需要两个操作,绑定Map和绑定图层,

具体示例如下


    //GsMapPtr m_ptrGeoMap;///< 内核地图对象  
    //map绑定捕捉容器  
    GsSnapContainerPtr ptrSnap = new GsSnapContainer(space->m_ptrGeoMap);  
    //添加节点捕捉算法对象  
    ptrSnap->AddSnaper(new GsNodeSnaper());  
    //GsFeatureLayerPtr gsLyr =new GsFeatureLayer(fcs);  
    //设置图层为多渲染模式,  
    gsLyr->Renderer()->RenditionMode(eMultiRendition);  
    //图层绑定捕捉  
    gsLyr->Renderer()->AddRendition(new GsSnapRendition(ptrSnap));  