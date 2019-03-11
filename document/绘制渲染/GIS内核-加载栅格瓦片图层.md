# GIS内核-加载栅格瓦片图层 #
    void AddTileLayer()  
    {  
    GsSqliteGeoDatabaseFactoryPtr fcsFac = new GsSqliteGeoDatabaseFactory();  
    GsGeoDatabaseFactoryPtr fac = fcsFac;  
    GsConnectProperty cp;  
    cp.DataSourceType = GsDataSourceType::eSqliteFile;  
      
    GsString str = "../tests/testdata/tile";  
    GsFile modelFile(GsFileSystem::ModuleFileName().c_str());  
    GsString fullPath = GsFileSystem::Combine(modelFile.Parent().FullPath().c_str(), str.c_str());  
    cp.Server = fullPath;  
    GsGeoDatabasePtr db = fac->Open(cp);  
    GsTileClassPtr fcs = db->OpenTileClass("jiangxiajinkou");  
    GsTileLayerPtr gsLyr = new GsTileLayer(fcs);  
    GsFeatureLayerPtr ptest = gsLyr;  
    space->layerBox().addLayer(gsLyr);  
    }  