# 地形数据的渲染显示 #

目前内核还没有支持地形数据的渲染显示，但是Desktop是支持的。

## 需求分析 ##
地形数据的渲染显示，
地形数据：这里指的是*.Tile数据，普通的Tile数据是有多个图象数据组成，地形的Tile数据，指的是经过地形压缩的Tile数据。
需要解压缩后，才可以当成普通图像数据绘制。

流程图： 主要增加的流程就是判断是否压缩，如果存在压缩的话，根据压缩类型解压数据。最后显示。

![](picture/terrain1.png)

## 类图 ##
![](picture/terrain2.png)



## 接口函数说明 ##
创建地形数据转换类

```
	/// \brief 根据pTileClass生成颜色转换器, 需要外部析构对象
	/// \param pTileClass 瓦片数据集
	/// \param pCancel 取消对象
	/// \param nSamples 返回样点数量
	/// \return 返回新创建的颜色转换对象的指针
	static GsColorToRGBA* GsColorToRGBA::CreateColorToRGBA(GsTileClass* pTileClass, GsTrackCancel* pCancel, int& nSamples);
```

将单个Tile数据转换为图片，转换过程中，会根据Tile的压缩方式来解压缩数据。

```
bool TerrainToRGBA<T>::Translate(GsTile * pTile, Utility::GsImage * pImg);
```


## 使用说明及示例伪代码 ##
```
	GsFileGeoDatabaseFactory fac;
	GsConnectProperty conn("G:/didi/IMG"); // twdx.tile文件所在的文件夹
	GsGeoDatabasePtr ptrGDB = fac.Open(conn);
	GsTileClassPtr ptrTileClass = ptrGDB->OpenTileClass("twdx");
    得到TileClass后可以创建TileLayer
	得到Layer后，可以创建map加载进去。
    创建了map，添加了Layer，就可以出图。
```