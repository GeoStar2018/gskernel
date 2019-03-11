## 制图总体介绍 ##
当用户切换到制图的状态时系统将自动用户为生成一个制图区域、一个页面和一幅基本地图，并把当前视图中的数据显示在制图的地图区域中。
常见的地图元素包括图例、比例尺、指北针、地图元素、文本元素等。

制图有以下元素：
1. GsShapeElement 基于特定形状的元素

	```
基类：GsElement   
制图图形为几何矢量图形的制图元素
	```
	
2. GsEnvelopeElement 矩形元素

	```
基类：GsGeometryElement
制图图形为矩形的制图元素
	```
	
3. GsNorthArrow 指北针

	```
基类：GsGeometryElement
特定为指北针形状的制图对象
	```
	
4. GsPictureElement 图片制图

	```
基类：GsGeometryElement
图片制图对象，可加载任意的png，jpg等格式的图片显示
	```
	
5. GsJoinMapTableElement 接图表

	```
基类：GsGeometryElement
图表是表示各图幅间相互位置的图表

	```
	
6. GsScaleBarElement 比例尺制图

	```
基类：GsGeometryElement
比例尺是表示图上一条线段的长度与地面相应线段的实际长度之比

	```
	
7. GsTextElement 文字制图

	```
基类：GsGeometryElement
可在制图中插入一段描述文本文字内容
	```
	
8. GsSlopeRulerElement 坡度尺制图

	```
基类：GsGeometryElement
用于根据地形图上等高线的平距,确定相应的地面坡度或其逆过程的一种图解曲线尺

	```
	
9. GsGridElement 格网，方里网

	```
基类：GsGeometryElement
方里网是由平行于投影坐标轴的两组平行线所构成的方格网。由每隔整公里绘出坐标纵线和坐标横线绘制而成

	```
	
10. GsLegendsElement 动态图例

	```
基类：GsGeometryElement
图例是集中于地图一角或一侧的地图上各种符号和颜色所代表内容与指标的说明，有助于更好的认识地图。
动态图例会根据图层元素的变化，在制图时，图例会自动发生相应改变。
	```
	
11. GsMapElement 地图元素

	```
基类：GsGeometryElement
用于绘制地图的制图元素
	```
	
12. GsPageLayout 制图页面类

	```
基类：GsGeometryElement
页面制图类，存储了所有制图的信息，有出图显示的功能
	```


示例代码：

```
void create()
{
	CRect winRect;
	GetClientRect(&winRect);

	if (winRect.Width()<=0 || winRect.Height()<=0)
		return;

	if (m_PageLayout)
		return;


	GsRect rect(0,0,winRect.Width(),winRect.Height());
	GsString sFile = GsFileSystem::Combine(GsFileSystem::WorkingFolder().c_str(), "..\\..\\testdata\\400sqlite");

	GsString strNameFile23 = GsUtf8("C:\\Users\\Administrator\\Desktop\\测试数据\\测试数据\\jun1.GMAPX").Str();

	GsMapPtr pMap1 = CreateMapFromData(sFile.c_str(), "BOU2_4M_S");
	GsPaintDevicePtr ptrDevice = GsPaintDevice::CreatePaintDevice(eWin32HwndDeviceD2D, m_hWnd);
	GsDisplayTransformationPtr ptrDT = new GsDisplayTransformation(pMap1->ViewExtent(), rect);

	

	m_ptrDisplay = new GsScreenDisplay(ptrDevice, ptrDT);
	m_PageLayout = new GsPageLayout(m_ptrDisplay);


	GsMapElementPtr ptrEle = new GsMapElement(pMap1);
	ptrEle->Geometry(new GsEnvelope(100, 100, 200, 200));
 	m_PageLayout->ElementContainer()->Add(ptrEle);
	
	ptrEle = new GsMapElement(CreateMapFromData(sFile.c_str(),"RAI_4M_L"));
	m_PageLayout->ElementContainer()->Add(ptrEle);
	ptrEle->ShowBorder(false);
	
	
	
	GsRingPtr ptrRing = new GsRing(GsRawPoint(60,100),40);
	
	GsShapeElementPtr ptrShape = new GsShapeElement(ptrRing);
	m_PageLayout->ElementContainer()->Add(ptrShape);

	GsCircleArcPtr ptrPath = new GsCircleArc(GsRawPoint(85, 5), GsRawPoint(75, 30), GsRawPoint(100, 20));

	GsShapeElementPtr ptrShapeArc = new GsShapeElement(ptrPath);
	m_PageLayout->ElementContainer()->Add(ptrShapeArc);

	GsNorthArrowPtr ptrNA = new GsNorthArrow(100, 222, 10,10);
	ptrNA->Angle(46);
	m_PageLayout->ElementContainer()->Add(ptrNA);

	GsNorthArrowPtr ptrNA2 = new GsNorthArrow(100, 222, 10, 10);
	ptrNA2->Color(GsColor::Red);
	ptrNA2->Angle(0);
	m_PageLayout->ElementContainer()->Add(ptrNA2);
	
	GsString strNameFile = GsFileSystem::Combine(GsFileSystem::WorkingFolder().c_str(), "..\\..\\testdata\\image\\que.bmp");
	GsPictureElementPtr ptrPic = new GsPictureElement(350, 150, strNameFile.c_str());
	m_PageLayout->ElementContainer()->Add(ptrPic);

 	strNameFile = GsFileSystem::Combine(GsFileSystem::WorkingFolder().c_str(), "..\\..\\testdata\\400sqlite\\123.GMAPX");


	GsMapDefine mapDefine(strNameFile.c_str());
	GsMapPtr pMap = new GsMap();
	mapDefine.ParserMap(pMap);
	
	GsScaleBarElementPtr ptrScaleBar = new GsScaleBarElement(110, 250);
	GsSymbolPtr ptrSymbol = new GsSimpleLineSymbol(GsColor::Red);
	ptrScaleBar->BottomLineSymbol(ptrSymbol);
	ptrScaleBar->Level(3);
	m_PageLayout->ElementContainer()->Add(ptrScaleBar);
	
	GsSlopeRulerElementPtr ptrSlopeRulerElement = new GsSlopeRulerElement(250, 10);
	m_PageLayout->ElementContainer()->Add(ptrSlopeRulerElement);

	GsString strNameFileele = GsFileSystem::Combine(GsFileSystem::WorkingFolder().c_str(), "..\\..\\testdata\\400sqlite\\123.GMAPX");
	GsMapDefine mapDefineEle(strNameFile.c_str());
	GsMapPtr pMapEle = new GsMap();
	mapDefineEle.ParserMap(pMapEle);

	GsMapElementPtr ptrEleEle = new GsMapElement(pMapEle);
	ptrEleEle->Geometry(new GsEnvelope(0, 100, 100, 200));
	m_PageLayout->ElementContainer()->Add(ptrEleEle);
	pMapEle->ViewExtent(pMapEle->Layers()->at(0)->Extent());


	GsLegendsElementPtr ptrLegendElement = new GsLegendsElement(-100, 250, pMapEle);
	m_PageLayout->ElementContainer()->Add(ptrLegendElement);


	GsJoinMapTableElementPtr ptrJoinMap = new GsJoinMapTableElement(110, 60);
	m_PageLayout->ElementContainer()->Add(ptrJoinMap);
	ptrJoinMap->Value(0, 0, "1");
	ptrJoinMap->Value(0, 1, "2");
	ptrJoinMap->Value(0, 2, "3");
	ptrJoinMap->Value(1, 0, "4");
	ptrJoinMap->Value(1, 1, "5");
	ptrJoinMap->Value(1, 2, "6");
	ptrJoinMap->Value(2, 0, "7");
	ptrJoinMap->Value(2, 1, "8");
	ptrJoinMap->Value(2, 2, "9");
}
```
	


# GIS内核-制图使用示例 #

制图需要将制图对象和视图map绑定起来,并且刷新的时候不能调用map的update,只有退出视图模式才能调用,否则可能导致线程安全问题, 也就是要控制视图状态,基本的使用如下:

当前视图转为制图视图:
```
    GsPageLayout m_pLayout = new GsPageLayout(space->m_ptrGeoMap->ScreenDisplay());  
    m_pLayout->ViewExtent(space->fullExtent());
    m_pLayout->Page()->PageType(ePageA6);//这里是固定类型的枚举 
    m_pMapEle = new GsMapElement(space->m_ptrGeoMap);//图廓元素  
    m_pMapEle->ShowBorder(true);  
    //边线
    GsSimpleLineSymbolPtr ptrLine = new GsSimpleLineSymbol(GsColor::Blue, 10);  
    m_pMapEle->BorderSymbol(ptrLine);  
    //添加制图的element
    space->m_pLayout->ElementContainer()->Add(m_pMapEle);  
    //添加制图的element
    GsPointPtr point = new GsPoint(100, 36);  
    GsSimplePointSymbolPtr ptrSymBol = new GsSimplePointSymbol(GsColor::Red,20);
    GsGeometyElementPtr pteGeometryEle = new GsGeometyElement(point, ptrSymBol);  
    space->m_pLayout->ElementContainer()->Add(pteGeometryEle);
    space->m_pLayout->ReferenceScale(10000000);
    //刷新
    space->m_isLayOut = true;  
    space->stopRendering();  
    space->update();
```
如果需要加入不同的element 可以通过geometry和symbol 配合使用基本可以达到一般制图效果
## GsShapeElement 基于特定形状的元素 ##
GsShapeElement的构造方法有如下两种。
```
	GsShapeElement(GsGeometry* geo, GsSymbol* sym);
	GsShapeElement(GsGeometry* geo);
```

1.  设置必要的geometry和必要的symbol。
2.  设置必要的geometry。而 symbol会根据geometry的维度来自动确定symbol是点，线，面等类型。
使用的时候，例如：

```
	GsCircleArcPtr ptrPath = new GsCircleArc(GsRawPoint(85, 5), GsRawPoint(75, 30), GsRawPoint(100, 20));// 创建三点圆弧。
	GsShapeElementPtr ptrShapeArc = new GsShapeElement(ptrPath);
```

```
	GsRingPtr ptrRing = new GsRing(GsRawPoint(60, 100), 40);
	// 构造一个二维的圆形，圆心和半径是(60,100)，40。 然后作为geometry来构造GsShapeElement
	GsSimpleLineSymbolPtr ptrSymbol = GsSimpleLineSymbol(GsColor::Red);
	GsShapeElementPtr ptrShape = new GsShapeElement(ptrRing, ptrSymbol);
	ptrShape->Position(GsRawPoint(0, 0)); // 基类的接口，设置制图左下角点的位置。
	ptrShape->Size(GsSizeF(50,50));//基类的接口，将元素的宽和高设置为50,50
```
而对于GsShapeElement的公共接口有对Symbol的操作，而对于基类的公共接口也是可以应用的。
```
	virtual GsSymbol * Symbol();//获取已经设置的Symbol。
	virtual void Symbol(GsSymbol*);//重新设置Symbol。
```
## GsEnvelopeElement 矩形元素 ##
矩形制图元素的构造方法可以通过box构造，或者通过GsEnvelope构造。
```
	GsEnvelopeElement(const GsBox & box);// 通过box构造。
	GsEnvelopeElement(GsEnvelope* pEnv);//通过GsEnvelope构造
```
GsEnvelopeElement 中的geometry是GsEnvelope类型。其中symbol默认设置的是简单线类型。
同样的，GsenvelopeElement是GsShapeElement子类，父类的接口也是可以调用使用的。
```
    ///测试用例
    GsBox box(10, 10, 20, 20);
    GsEnvelopeElementPtr ptrEnv = new GsEnvelopeElement(box);
    // 同样的也可以通过父类的接口来调整位置(Position)或设置大小(Size)。
```


## GsMapElement 地图元素##
地图元素：是以制图视窗的模式下将地图元素绘制到画布上。
```
	GsMapElement(GsMap* pMap);//构造的时候设置相应的Map，但是Map必须设置相应的空间参考。
```
```
/// \brief 图廓符号
	GsSymbol* BorderSymbol();
	/// \brief 图廓符号
	void BorderSymbol(GsSymbol* sym);

	/// \brief 是否显示图廓
	bool ShowBorder();
	/// \brief 是否显示图廓
	void ShowBorder(bool bShow);

	/// \brief 是否裁切纸张
	bool ClipPage();
	/// \brief 是否裁切纸张
	void ClipPage(bool bClip);

	/// \brief 获取是否锁定比例尺
	bool IsFixedScale();
	/// \brief 设置是否锁定比例尺
	void IsFixedScale(bool bFixedScale);

	/// \brief 获取锁定得比例尺, 此比例尺为PageLauout得DT得来, 不得自行设置
	double FixedScale();
```
1.  BorderSymbol：轮廓线符号。绘制地图元素的时候会有一个基本的图廓(矩形)，地图的绘制是不会超过这个矩形范围的。
2.  ShowBorder：可以设置是否显示图廓。
3.  ClipPage：设置是否裁切纸张外的地图,也就是说，仅仅绘制纸张内部的地图。
4.  IsFixedScale：在操作调整轮廓大小的时候是否锁定比例尺。
5.  FixedScale：可以获取到比例尺。
```
    //测试用例
	///构建GsMap，同时设置空间参考。
	GsMapPtr CreateMapFromData(const char * folder, const char * name)
	{
		GsDisplayTransformationPtr ptrDT = new GsDisplayTransformation(GsBox(0, 0, 100,100),GsRect(0,0,100,100));
		GsScreenDisplayPtr ptrScreen = new GsScreenDisplay(NULL, ptrDT);

		GsMapPtr ptrMap = new GsMap(ptrScreen);

		GsConnectProperty conn(folder);
		GsGeoDatabasePtr ptrGDB = GsSqliteGeoDatabaseFactory().Open(conn);
		GsFeatureClassPtr ptrFeaClass = ptrGDB->OpenFeatureClass(name);

		///添加地物类的空间参考到PtrMap
		GsSpatialReferencePtr sptial = ptrFeaClass->SpatialReference();
		ptrScreen->DisplayTransformation()->SpatialReference(sptial);

		GsFeatureLayerPtr ptrLyr = new GsFeatureLayer(ptrFeaClass);
		ptrMap->Layers()->push_back(ptrLyr);
		ptrMap->ViewExtent(ptrFeaClass->Extent());
		return ptrMap;
	}	

	///构建pMap
	GsString sFile = GsFileSystem::Combine(GsFileSystem::WorkingFolder().c_str(), "..\\..\\testdata\\400sqlite");
	GsMapPtr pMap = CreateMapFromData(sFile.c_str(), "BOU2_4M_S");

	GsMapElementPtr ptrEle = new GsMapElement(pMap);
	ptrEle->Geometry
