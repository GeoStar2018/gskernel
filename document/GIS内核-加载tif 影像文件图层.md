# GIS内核-加载tif 影像文件图层 #

    void AddFileLayer()  
    {  
    GsFileGeoDatabaseFactoryPtr fcsFac = new GsFileGeoDatabaseFactory();  
    GsGeoDatabaseFactoryPtr fac = fcsFac;  
    GsConnectProperty cp;  
    cp.DataSourceType = GsDataSourceType::eFile;  
      
    GsString str = "../tests/testdata/tif";  
    GsFile modelFile(GsFileSystem::ModuleFileName().c_str());  
    GsString fullPath = GsFileSystem::Combine(modelFile.Parent().FullPath().c_str(), str.c_str());  
    cp.Server = fullPath;  
    GsGeoDatabasePtr db = fac->Open(cp);  
      
    GsRasterClassPtr Raster = db->OpenRasterClass("image");  
    GsRasterLayerPtr layer = newGsRasterLayer(Raster);  
    space->layerBox().addLayer(layer);  
    }  