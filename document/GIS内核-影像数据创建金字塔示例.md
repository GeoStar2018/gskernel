## GIS内核-影像数据创建金字塔示例 ##

void CPyramidThread(const char* strFile)  
{  
  
      GsFileGeoDatabaseFactoryPtr fcsFac = new GsFileGeoDatabaseFactory();  
      GsGeoDatabaseFactoryPtr fac = fcsFac;  
      GsConnectProperty cp;  
      cp.DataSourceType = GsDataSourceType::eFile;  
  
      GsString str = strFile;  
      GsFile vmpFile(str.c_str());  
      GsDir  dir=vmpFile.Parent();  
      cp.Server = dir.FullPath();  
      if(vmpFile.Exists())  
      {  
        GsGeoDatabasePtr db = fac->Open(cp);  
        GsRasterClassPtr pRaster = db->OpenRasterClass(vmpFile.Name().c_str());  
        pRaster->OnProgress.Add(this,&CPyramidThread::OnProgress);  
        pRaster->CreatePyramid(eNearestNeighbour,4);  
      }  
  
  }  
  
}  
  
  
bool OnProgress(const char *str, double dblProgress)  
{  
    double iprogress=dblProgress*100;  
    const char* caption=str;  
    qDebug("***********start********************");  
    qDebug("current progress is :%f",iprogress);  
    qDebug("current caption is :%s",caption);  
    return true;  
}  
