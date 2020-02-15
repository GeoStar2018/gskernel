GsFeatureLayer空间参考获取方法说明：

```c
/// \brief 图层的空间参考
virtual GsSpatialReference* SpatialReference();
/// \brief 获取强制投影时源空间参考
virtual GsSpatialReference* SourceSpatialReference();
/// \brief 设置强制投影时源空间参考
virtual void SpatialReference(GsSpatialReference* soruce);
```


以下为错误用法：
    
```c++
GsSpatialReference ptrSr(eWGS84);
GsFeatureLayerPtr layer = new GsFeatureLayer(ShpFeatureClass);
GsString strPro = layer->SpatialReference()->Name();
GsString strSpa = ptrSr.Name();
//用于设置图层本身的空间参考
layer->SpatialReference(&ptrSr);
//错误使用，SpatialReference()用于获取数据的空间参考，也就是ShpFeatureClass的空间参考
GsString str = layer->SpatialReference()->Name();
```

正确使用方法：

```c
GsSpatialReference ptrSr(eWGS84);
GsFeatureLayerPtr layer = new GsFeatureLayer(ShpFeatureClass);
GsString strPro = layer->SpatialReference()->Name();
GsString strSpa = ptrSr.Name();
//用于设置图层本身的空间参考
layer->SpatialReference(&ptrSr);
//正确使用，SourceSpatialReference()用于获取图层本身的空间参考，也就是用户设置的空间参考
GsString str = layer->SourceSpatialReference()->Name();
```

