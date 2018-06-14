# GIS内核-查询数据集数据并保存到新数据集, 转换中做动态投影 #
    GsConnectProperty conn;  
    conn.Server ="C:\";  
    GsSqliteGeoDatabaseFactory fac;  
    GsGeoDatabasePtr ptrGDB = fac.Open(conn);  
      
    GsFeatureClassPtr ptrFeaOri = ptrGDB->OpenFeatureClass("countries");  
    GsGeometryColumnInfo col;  
    col.GeometryType = eGeometryTypePolygon;  
    GsSpatialReferencePtr ptrSR = new GsSpatialReference(eWebMercator);  
      
    GsFeatureClassPtr ptrFeaTarget = ptrGDB->CreateFeatureClass("MKT",ptrFeaOri->Fields(),  
    col,ptrSR);  
    //原始的查询一个  
    GsFeatureCursorPtr ptrCursor = ptrFeaOri->Search();  
      
      
    GsSpatialReferencePtr ptrWGS84 = new GsSpatialReference(eWGS84);  
    GsProjectCoordinateTransformationPtr proj = new GsProjectCoordinateTransformation(ptrWGS84,ptrSR);  
    SimpleTrans trans(proj);  
    //创建目标的feature  
    GsFeaturePtr ptrFea = ptrFeaTarget->CreateFeature();  
    while(ptrCursor->Next(ptrFea))  
    {  
    ptrFea->GeometryBlob()->Transform(&trans);  
      
      
    ptrFea->OID(-1);  
    ptrFea->Store();  
    }  
    ptrFeaTarget->CreateSpatialIndex();  
    [cpp] view plain copy
    class SimpleTrans:public GsCoordinateTransformation  
    {   
    GsProjectCoordinateTransformationPtr m_proj;  
    double m_Max;  
    public:  
    SimpleTrans(GsProjectCoordinateTransformation *proj)  
    {  
    m_Max = 85.05112877980659;  
    m_proj = proj;  
    }  
      
    /// \brief 对x数组和y数组以及Z数组分别转换  
    virtual bool Transformation(double* pX,double *pY,double *pZ,int nPointCount,int nPointOff)  
    {  
    for(int i =0;i<nPointCount;i++)  
    {  
    int index = i * nPointOff;  
     if(pY[index] > m_Max)  
     pY[index] = m_Max;  
     else if(pY[index] < (-1 * m_Max))  
     pY[index] = -1 * m_Max;  
    }  
    m_proj->Transformation(pX,pY,pZ,nPointCount,nPointOff);  
    return true;  
    }  
      
    };  