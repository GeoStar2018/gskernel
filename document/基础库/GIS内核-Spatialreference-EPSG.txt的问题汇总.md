## EPSG.txt文件找不到，导致空间参考创建失败的情况汇总 ##

## EPSG.txt的位置： ##
首先在内核代码(40_Source)中 EPSG.txt 在文件夹 /40_Source/data/coordinatesystem/EPSG.txt
但是依赖于内核做二次开发的工程都有自己相应的EPSG.txt文件存放。

## EPSG.txt什么时候使用到？ ##
1. 在创建空间参考的时候，例如传入的是epsg号，内核会根据这个epsg号去EPSG.txt文件中查找相应的wkt串，然后根据wkt串来构造空间参考
2. 在已经创建好的空间参考，想要查询到他的epsg号，也会根据现在的空间参考来查询和对比EPSG.txt文件中的空间参考来获取对应的epsg号

结论：
- 即使创建了空间参考，但是空间参考不一定是创建成功的，可以使用获取epsg号或者查看IsNull函数来测试是否真正创建成功。


## 如何认定为问题的原因是EPSG.txt找不到导致的？ ##
```
	GsSpatialReferencePtr ptrSpa = new GsSpatialReference(4490);
	bool bl = ptrSpa->IsNull();
```
上述代码，如果bl的值为false，那么基本可以认定，内核无法找到epsg.txt，导致创建的空间参考为空。


## 内核是如何查找EPSG.txt的？ ##

在讲述上述代码之前，我们先看另外一个代码

```
	GsSpatialReferenceManager srMgr;
	auto str = srMgr.DataFolder();
```
上面代码主要是查找EPSG.txt文件所在的路径。会有三个步骤。
1. 首先会查找全局配置：`Kernel/SpatialReference/DataFolder`
2. 再查找这个路径。GsFileSystem::WorkingFolder() + “../data/coordinatesystem”
3. 然后再查找路径。GsFileSystem::ModuleFileName() + Parent()（上级目录）

GsFileSystem::WorkingFolder() 是获取当前工作路径，windows上本质是_wgetcwd函数 。linux上本质是getcwd函数
GsFileSystem::ModuleFileName()是运行模块的绝对路径，
   如果是内核在跑测试用例，那么就是unittest.exe的绝对路径。
   如果是Geomap在跑程序，那么就是applicationframe.exe的绝对路径。
   如果是java环境，那么就是java虚拟机javaw.exe的绝对路径。

### 内核的全局配置库是什么？如何配置？如何获取？ ###
```
	获取全局配置
	// 在全局配置中读取configPath的值，如果读取不到，则返回默认值path。
	const char * configPath = "Kernel/SpatialReference/DataFolder";
	GsString strFolder = GsGlobalConfig::Instance()[configPath].StringValue(path);
```
设置全局配置

```
	java代码设置全局配置
    String path = "F:\\40_Source.xiaobo\\data\\coordinatesystem";
    GsGlobeConfig.Instance().Child("Kernel/SpatialReference/DataFolder").Value(path);
```

## EPSG.txt文件的查找 ##
1. 首先会查找全局配置：`Kernel/SpatialReference/EPSG`
2. 如果找不到使用GsSpatialReferenceManager的1 2 3 查找方式，查找文件夹，然后遍历再定位epsg.txt
3. 然后再查找路径。GsFileSystem::ModuleFileName() + “../data/coordinatesystem”



## 还有什么要注意的，看了这么多我到底该怎么办？ ##
上述查找步骤，如果任何一步查找成功，则不会继续查找。

很简单，在发生EPSG.txt文件找不到的时候，只需要配置一下全局配置即可。
`Kernel/SpatialReference/DataFolder`
`Kernel/SpatialReference/EPSG`

最好是配置绝对路径，如果配置的是相对路径，会相对于GsFileSystem::WorkingFolder()路径来查找。如果未找到，会其他方式查找。

