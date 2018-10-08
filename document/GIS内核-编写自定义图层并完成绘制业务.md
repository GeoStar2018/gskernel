# GIS内核-编写自定义图层并完成绘制业务 #

需求问题:

多个影像文件图层,和其对应的几何范围,需要一起显示,GIS内核只设计了基本的单个影像和单个地物类绘制的图层,请参见前面  

**GIS内核-加载tif 影像文件图层,以及GsFeatureLayer.**

封装一个自定义的图层至少要继承GsLayer 类,并且至少实现基本的基类 虚函数接口.下面是我封装的一个直接撸代码,头文件定义:
    
    class  GsCoustomRasterLayer :public GsLayer  
    {  
    public:  
        GsCoustomRasterLayer();  
        ~GsCoustomRasterLayer();  
      
      
    private:  
        GsVector<GsRasterLayerPtr> m_Lys;  
        GsFeatureLayerPtr m_ptrfcsLayer;  
        GsString m_ClipGeometryField;  
        virtual bool InnerDraw(GsDisplay* pDisplay, GsTrackCancel* pCancel, GsDrawPhase eDrawPhase);  
        //根据特定规则找到每个影像图层对应的数据集裁切几何的对象  
        GsGeometry* FindGeometry(const char *strLyrName);  
    public:  
        // 通过 GsLayer 继承  
        virtual GsBox Extent(GsSpatialReference * pTargetSR = 0) override;      
      
        virtual bool HasSelection() override;  
      
      
        virtual GsSmarterPtr<GsLayer> Clone() override;  
      
      
        void AddRasterLayer(GsRasterLayer *pLayer);  
        void RemoveRasterLayer(const char * strLyrName);  
        void ClearLayers();  
        GsVector<GsRasterLayerPtr >* RasterLayers();  
        GsFeatureLayer* FeatureLayer();  
        void FeatureLayer(GsFeatureLayer* pFeaLyr);  
    };  
    GS_SMARTER_PTR(GsCoustomRasterLayer);  


cpp文件实现:
    
    GsCoustomRasterLayer::GsCoustomRasterLayer()  
    {  
    }  
      
    GsCoustomRasterLayer::~GsCoustomRasterLayer()  
    {  
    }  
      
    bool GsCoustomRasterLayer::InnerDraw(GsDisplay * pDisplay, GsTrackCancel * pCancel, GsDrawPhase eDrawPhase)  
    {  
        std::vector<GsRasterLayerPtr >::iterator it = m_Lys.begin();  
        for (; it != m_Lys.end(); it++)  
        {  

            GsGeometryPtr pGeo = FindGeometry((*it)->Name().c_str());  
            (*it)->ClipGeometry(pGeo);  
            (*it)->Draw(pDisplay, pCancel, eDrawPhase);  
        }  
        m_ptrfcsLayer->Draw(pDisplay, pCancel, eDrawPhase);  
        return false;  
    }  
      
    GsGeometry * GsCoustomRasterLayer::FindGeometry(const char * strLyrName)  
    {  
         return nullptr;  
    }  
            
    GsBox GsCoustomRasterLayer::Extent(GsSpatialReference * pTargetSR)  
    {  
        std::vector<GsRasterLayerPtr >::iterator it = m_Lys.begin();  
        GsBox boxret;  
        for (; it != m_Lys.end(); it++)  
        {  
            GsBox box = (*it)->Extent();  
            if (box.IsValid())  
            boxret.Union(box);  
        }  
      
        return boxret;  
    }  
      
    bool GsCoustomRasterLayer::HasSelection()  
    {  
        return false;  
    }  
      
    GsSmarterPtr<GsLayer> GsCoustomRasterLayer::Clone()  
    {  
        std::vector<GsRasterLayerPtr >::iterator it = m_Lys.begin();  
        GsCoustomRasterLayerPtr ptrColne = new GsCoustomRasterLayer();  
        for (; it != m_Lys.end(); it++)  
            ptrColne->RasterLayers()->push_back((*it)->Clone());  
        GsFeatureLayerPtr pLyr = m_ptrfcsLayer->Clone();  
        ptrColne->FeatureLayer(pLyr);  

        //基础属性  
        GsLayerPtr pLayer = ptrColne;  
        pLayer->Name(Name().c_str());  
        pLayer->Visible(Visible());  
        pLayer->AliasName(AliasName().c_str());  
        pLayer->MinScale(MinScale());  
        pLayer->MaxScale(MaxScale());  
        pLayer->ReferenceScale(ReferenceScale());  
        pLayer->Tag(Tag());  

        return ptrColne;  
    }  
      
    void  GsCoustomRasterLayer::AddRasterLayer(GsRasterLayer * pLayer)  
    {  
        m_Lys.push_back(pLayer);  
    }  
      
    void GsCoustomRasterLayer::RemoveRasterLayer(const char * strLyrName)  
    {  
        std::vector<GsRasterLayerPtr >::iterator it = m_Lys.begin();  
        for (; it != m_Lys.end(); it++)  
        {  
            if (GsCRT::_stricmp((*it)->Name().c_str(), strLyrName))  
            {  
                   m_Lys.erase(it);  
                   break;  
            }  
        }  
    }  
      
    void GsCoustomRasterLayer::ClearLayers()  
    {  
        m_Lys.clear();  
    }  
      
    GsVector<GsRasterLayerPtr>* GsCoustomRasterLayer::RasterLayers()  
    {  
        return &m_Lys;  
    }  
      
    GsFeatureLayer * GsCoustomRasterLayer::FeatureLayer()  
    {  
        return m_ptrfcsLayer.p;  
    }  
      
    void GsCoustomRasterLayer::FeatureLayer(GsFeatureLayer * pFeaLyr)  
    {  
    m_ptrfcsLayer = pFeaLyr;  
    }  


这个图层的使用 就是填充两个	GsVector<GsRasterLayerPtr> m_Lys和	GsFeatureLayerPtr m_ptrfcsLayer; 其中有个Clipgeometry  对象 这个需要根据业务自身去定义,目前这里接口定义好的位根据名称去FetureLayer内部找Geometry .
