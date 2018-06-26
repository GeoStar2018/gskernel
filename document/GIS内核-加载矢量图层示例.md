# GIS内核-加载矢量图层示例 #

    void MainWindow::AddLayerMutilLabel()  
    {   GsShpGeoDatabaseFactoryPtr shpFac = new GsShpGeoDatabaseFactory(); GsGeoDatabaseFactoryPtr fac = new GsFileGeoDatabaseFactory();  GsConnectProperty cp;  
    cp.DataSourceType = GsDataSourceType::eShapeFile;  
    GsString str = "../tests/testdata/shp";  
    GsFile modelFile(GsFileSystem::ModuleFileName().c_str());  
     GsString fullPath = GsFileSystem::Combine(modelFile.Parent().FullPath().c_str(), str.c_str());  
     cp.Server =  fullPath;  
    GsGeoDatabasePtr db = fac->Open(cp);  }
>


    GsFeatureClassPtr fcs = db->OpenFeatureClass("COUL_Lite");  
>

    GsFeatureLayerPtr ptest = new GsFeatureLayer(fcs);  


>

    space->layerBox().addLayer(ptest); 