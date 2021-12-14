## 目录树的测试用例 ##

支持地图定义的目录信息输出到样式文件的元信息(metadata)中。
```
	const char * str = "C:/Users/Administrator/Desktop/xuzhouPT/xuzhouPT.GMAPX";///地图自定义文件的路径
	GsPyramidPtr pPy = GsPyramid::WellknownPyramid(GsWellknownPyramid::e360DegreePyramid);//金字塔信息
	GsStyleTableFactory fac;
	GsStyleTablePtr ptrStyle = fac.OpenFromMapDefine(str, pPy);//创建样式表信息
	EXPECT_TRUE(!ptrStyle);
	const char * strout = "C:/Users/Administrator/Desktop/xuzhouPT/style.zip";//输出路径
	ptrStyle->SaveJson(strout);//保存本地。
```
其中需要说明的是：输出文件格式实际是zip格式，需要解压后查看相应的style.style文件，这个文件内部是保存的json数据