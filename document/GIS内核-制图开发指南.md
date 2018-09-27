## 制图总体介绍 ##
当用户切换到制图的状态时系统将自动用户为生成一个制图区域、一个页面和一幅基本地图，并吧当前二维视图中的数据显示在制图的地图区域中。通过在页面上排布和组织各种地图元素构成制图。常见的地图元素包括一个或多个图层、比例尺、指北针、地图标题、描述文本和符号图例。

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
	ptrEle->Geometry(new GsEnvelope(100, 100, 200, 200));//设置图廓的大小。
	m_PageLayout->ElementContainer()->Add(ptrEle);
	
```
## GsNorthArrow 指北针##
指北针目前有几种样式。
```
/// \brief 指北针类型
enum GsNorthArrowType
{
	/// \brief 指北针，上方没有N标识
	eNorthArrowAllNoText,
	/// \brief 左边是白色的指北针，上方有N标识
	eNorthArrowLeft,
	/// \brief 右边是白色的指北针，上方有N标识
	eNorthArrowRight,
	/// \brief 全部黑色的指北针，上方有N标识
	eNorthArrowAll,
	/// \brief 四个方向的指北针，上方有N标识
	eNorthArrowFour,
	/// \brief 基于点符号得指北针,//geostar.ttf code:21-27
	eNorthArrowByPointSymbol,
};
```
```
	GsNorthArrow(double x, double y, double w, double h, GsNorthArrowType Type);
	GsNorthArrow(double x, double y, double w, double h);// x,y是坐标, w,h是宽高。
```
对于第一种构造函数需要设置相应的指北针的样式。第二个构造函数是默认没有N标识的指北针。
其中对于第一种geometry是GsPoint类型， symbol是GsTextPointSymbol类型；
第二种geometry是GsRing类型， symbol是GsSimpleFillSymbol类型；
```
	/// \brief 颜色 
	virtual GsColor Color();
	/// \brief 设置颜色
	virtual void Color(const GsColor& c);
	/// \brief 范围
	virtual GsBox Box();
	/// \brief 设置范围
	virtual void Box(const GsBox& bbox);
	/// \brief 旋转角度
	virtual double Angle();
	/// \brief 旋转角度
	virtual void Angle(double Angle);
	/// \brief 获取文字"N"的大小
	virtual double LabelTextHeight();
	/// \brief 设置文字"N"的大小
	virtual void LabelTextHeight(double height);
	/// \brief 获取指北针的样式
	GsNorthArrowType NorthArrowType();
	/// \brief 设置指北针的样式
	void NorthArrowType(GsNorthArrowType type);
```
1.  Color：是对symbol的颜色的修改。
2.  Box：是对范围的修改。
3.  Angle：是对角度的修改,单位是度。
4.  LabelTextHeight：设置指北针上方N的高度。
5.  NorthArrowType：修改指北针的样式。
```
	GsNorthArrowPtr ptrNNA = new GsNorthArrow(40, 170,30,30, GsNorthArrowType::eNorthArrowFour);
	GsNorthArrowPtr ptrNA = new GsNorthArrow(8, 18, 10, 10);
	//ptrNA->Angle(46);
```
## GsPictureElement 图片制图##
图片制图是将图片文件(或者可以转为GsImage类的图片数据)，在制图中显示。
图片制图的构造方式有三种，可以从图片对象构造，可以从内存块构造，也可以从文件构造。
```
	/// \brief 从图片对象构造
	GsPictureElement(double x, double y,GeoStar::Utility::GsImage * pImg);
	/// \brief 从图片内存块构造
	GsPictureElement(double x, double y, const unsigned char *pBlob, int nLen);
	/// \brief 从图片文件构造
	GsPictureElement(double x, double y, const char* pImageFile);
```
GsPictureElement制图的接口包含，得到图片的高度和宽度，得到图片中心点位置。
```
	/// \brief 得到图片的高度
	double Height();
	/// \brief 设置图片的高度
	void Height(double dHeight);
	
	/// \brief 得到图片的宽度
	double Width();
	/// \brief 得到设置的宽度
	void Width(double dWidth);

	/// \brief 得到图片的中心点位置
	GsRawPoint CenterPoint();
	/// \brief 设置图片的中心点位置
	void CenterPoint(const GsRawPoint& pt);
```
1.  Height：设置或获取图片显示制图中高度。
2.  Width：设置或获取图片显示的制图中的宽度。
3.  CenterPoint：设置或获取图片中心点的位置。
以que.bmp文件创建的图片制图ptrPic，示例代码如下：
```
    GsString strNameFile = GsFileSystem::Combine(GsFileSystem::WorkingFolder().c_str(),"..\\..\\testdata\\image\\que.bmp");
	GsPictureElementPtr ptrPic = new GsPictureElement(50, 150, strNameFile);
	m_PageLayout->ElementContainer()->Add(ptrPic);
```
## GsJoinMapTableElement 接幅表##
接幅表是三行三列的九宫格形式。
```
	/// \brief 接幅表构造函数，传入相应的x,y
	GsJoinMapTableElement(double x, double y);
```
```
	/// \brief 接幅表宽度
	double Width( );
	/// \brief 设置接幅表宽度
	void Width(double Width);
	/// \brief 接幅表高度
	double Height();
	/// \brief 设置接幅表高度
	void Height(double Height);
	/// \brief 设置某一位置的值,row和col的取值为0,1,2
	void Value(int row, int col, const char* text);
	/// \brief 获取某一位置的值，row和col的取值为0,1,2
	UTILITY_NAME::GsString Value(int row, int col);
	/// \brief 接幅表中间面填充符号,默认斜线填充
	GsFillSymbol* FillSymbol();
	/// \brief 接幅表中间面填充符号,默认斜线填充
	void FillSymbol(GsFillSymbol *pFillSymbol);
	/// \brief 接幅表边线绘制符号
	GsLineSymbol* FrameLineSymbol();
	/// \brief 接幅表边线绘制符号
	void FrameLineSymbol(GsLineSymbol *pLineSymbol);
	/// \brief 接幅表文字绘制符号
	GsTextSymbol* TextSymbol();
	/// \brief 接幅表文字绘制符号
	void TextSymbol(GsTextSymbol *pTextSymbol);
```
1.  Width：设置宽。
2.  Height：设置高。
3.  Value：给特定位置设置相应的值。
4.  FillSymbol：九宫格正中间的面填充符号，默认使用的是斜线填充面。
5.  FrameLineSymbol：绘制过程中边线符号。
6.  TextSymbol：绘制九宫格中文字的符号。
```
    /// 测试用例，接幅表
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
	GsLineSymbolPtr ptrLine = new GsSimpleLineSymbol(GsColor::Red);
	ptrJoinMap->FrameLineSymbol(ptrLine);
```
##GsScaleBarElement 比例尺制图##
比例尺制图是可以显示地图的比例。
```
	GsScaleBarElement(double x, double y);// 设置起始位置，
	GsScaleBarElement(GsMap* pMap,double x, double y);//设置起始位置，同时绑定map
```
接口如下
```
	/// \brief  获取底部线符号
	GsSymbolPtr BottomLineSymbol();
	/// \brief  获取底部线符号
	void BottomLineSymbol(GsSymbol* symbol);

	/// \brief  返回级别数量
	int Level();
	/// \brief  设置级别数量
	void Level(int nLevel);
	/// \brief  返回单个级别被切分的数量
	int Division();
	/// \brief  设置单个级别被切分的数量
	void Division(int division);

	/// \brief 设置比例尺的单位 
	void Unit(const char* unit);
	/// \brief 获取比例尺的单位
	Utility::GsString Unit();
	/// \brief 设置比例尺的单位 
	void UnitType(GsUnits eUnit);
	/// \brief 获取比例尺的单位
	GsUnits UnitType();


	/// \brief 设置比例尺中最小刻度
	void MinScale(int nMinScale);
	/// \brief 获取比例尺中最小刻度
	int MinScale();
	/// \brief 设置比例尺中最大刻度
	void MaxScale(int nMaxScale);
	/// \brief 获取比例尺中最大刻度
	int MaxScale();
	
	/// \brief 设置比例尺绘制的长度(底部水平线的长度)
	void ScaleLength(double len);
	/// \brief 获取比例尺绘制的长度(底部水平线的长度)
	double ScaleLength();

	/// \brief 获取单位左边的间隙
	double Gap();
	/// \brief 设置单位左边的间隙
	void Gap(double gap);
	/// \brief 获取绑定的Map
	GsMapPtr Map();
	/// \brief 设置绑定的Map
	void Map(GsMap* map);


	/// \brief 设置显示的刻度文字符号
	void TextSymbol(GsTextSymbol* ptrTextSym);	
	/// \brief 获取显示的刻度文字符号
	GsTextSymbolPtr TextSymbol();
	/// \brief 设置单位的文字符号
	void TextSymbolUnit(GsTextSymbol* ptrTextSym);
	/// \brief 获取单位的文字符号
	GsTextSymbolPtr TextSymbolUnit();
	/// \brief 设置文字高度
	void TextHeight(double height);
	/// \brief 获取文字高度
	double TextHeight();
```
接口详细说明：
1.  Level：是设置比例尺的级别。就是总长度被分为Level段。
2.  Division：是每个一小段又被分为Division小段。
3.  Unit：是指比例尺上面显示单位的样式 例如 KM 千米 kilometer 等样式。
4.  UnitType：是指用户想要显示刻度上面数值的真实单位。例如显示的10000米可以表示为10 千米。10000米 100000厘米等等方式。(这个单位影响到比例尺上面的刻度数值)。
5.  MinScale：最小刻度，显示最小刻度的数值。
6.  MaxScale：最大刻度，显示最大刻度的数值。
7.  ScaleLength：底部线符号的长度。也就是最小刻度和最大刻度之间的绘制的实际长度。
8.  Gap：单位与最大刻度两个文本符号之间的间隙。
9.  Map：绑定的map。
10.  TextSymbol：绘制刻度的文字符号。可以相应的设置文字的样式，颜色字体等等属性。
11.  TextSymbolUnit：绘制比例尺中单位的文字符号。
12.  TextHeight：设置文字高度。
```
    // 测试用例，比例尺
	GsScaleBarElementPtr ptrScaleBar = new GsScaleBarElement(110, 250);
	GsSymbolPtr ptrSymbol = new GsSimpleLineSymbol(GsColor::Red);
	ptrScaleBar->BottomLineSymbol(ptrSymbol);
	ptrScaleBar->Level(3);
	m_PageLayout->ElementContainer()->Add(ptrScaleBar);
```
##GsThreeNorthElement 三北方向制图##
```
GsThreeNorthElement(double x, double y);//传入绘制起始点(左下角点)的坐标。
```
```
/// \brief 设置三个子午线的线符号。
	void LineSymbol(GsSymbol * pSym);
	/// \brief 获取三个子午线的线符号。
	GsSymbolPtr LineSymbol();

	/// \brief 设置三个子午线的线段长度，单位mm
	void LineLength(double dfLength);
	/// \brief 获取三个子午线的线段长度，单位mm
	double LineLength();

	/// \brief 磁偏角。(标注显示的角度值)
	void MagnetAngle(double dfAngle);
	double MagnetAngle();
	
	/// \brief 子午线收敛角。(标注显示的角度值)
	void MeridianAngle(double dfAngle);
	double MeridianAngle();
	
	/// \brief 绘制三北方向时的夹角。(因为三北方向线在绘制时的夹角是示意图，并不是实际的角度值),设置磁子午线和真子午线绘图的夹角，同时设置坐标纵线和真子午线绘图的夹角，两个夹角相等。
	void GraphAngle(double dfAngle);
	/// \brief 获取三北方向时的夹角,获取磁子午线和真子午线绘图的夹角。
	double GraphAngle();

	/// \brief 设置五角星外接圆的半径。
	void AsteriskSize(double dfSize);
	/// \brief 获取五角星外接圆的半径。
	double AsteriskSize();

	/// \brief 设置箭头的长度
	void ArrowSize(double dfSize);
	/// \brief 获取箭头的长度
	double ArrowSize();

	/// \brief 设置拱形虚线符号。
	void ArcDottedLine(GsCartographicLineSymbol * pSym);
	/// \brief 获取拱形虚线符号。
	GsCartographicLineSymbolPtr ArcDottedLine();

	/// \brief 设置名称标注的符号。
	void NameLabelSymbol(GsTextSymbol * pSym);
	/// \brief 获取名称标注的符号。
	GsTextSymbolPtr NameLabelSymbol();

	/// \brief 设置角读标注的符号。
	void AngleLabelSymbol(GsTextSymbol * pSym);
	/// \brief 获取角读标注的符号。
	GsTextSymbolPtr AngleLabelSymbol();

	/// \brief 获取大小
	virtual Utility::GsSizeF Size();
	/// \brief 设置大小
	virtual void Size(const Utility::GsSizeF & sizef);
```
1.  LineSymbol：绘制三个子午线的线符号。
2.  LineLength：绘制三个子午线的线长度。
3.  MagnetAngle：磁偏角。影响到绘制中显示的文字，不影响实际绘制角度。
4.  MeridianAngle：子午线收敛角。影响到绘制中显示的文字，不影响实际绘制角度。
5.  GraphAngle：实际绘制的角度。
6.  AsteriskSize：绘制的五角星的大小。
7.  ArrowSize：绘制的箭头的大小。
8.  ArcDottedLine：拱形虚线符号。
9.  NameLabelSymbol：绘制的文字使用的符号。
10.  AngleLabelSymbol：绘制的角度使用的符号。
11.  Size：设置三北方向的大小。

```
    //测试用例，三北方向
	GsThreeNorthElementPtr ptrThreeNorth = new GsThreeNorthElement(20, 230);
	ptrThreeNorth->NameLabelSymbol()->Color(GsColor::Random());
	ptrThreeNorth->AngleLabelSymbol()->Color(GsColor::Random());
```
##GsTextElement 文字制图##
```
/// \brief 锚点的位置
enum GsTextAnchors
{
	//用户传入的锚点位置为坐标点
	//如旋转，开始绘制等都以锚点为参考
	//*    *    *
	//*    *    *
	//*    *    *

	/// \brief 第一行左边点
	eTextTopLeft,
	/// \brief 第一行中间点
	eTextTopCenter,
	/// \brief 第一行右边点
	eTextTopRight,
	/// \brief 第二行左边点
	eTextCenterLeft,
	/// \brief 第二行中间点
	eTextCenterCenter,
	/// \brief 第二行右边点
	eTextCenterRight,
	/// \brief 第三行左边点
	eTextBottomLeft,
	/// \brief 第三行中间点
	eTextBottomCenter,
	/// \brief 第三行右边点
	eTextBottomRight
};
```
在了解文本制图之前，需要了解一下锚点的概念，在平面上制图绘制的文本可以理解为有外接矩形框的，在外接矩形框上面的8个点加上正中心的一个点，就可以理解为有三排三列的9个点。也就是上面所谓的锚点的概念。
下面是文字制图的接口。
```
/// \brief 根据字符串来设置外接矩形的宽高，然后根据锚点来设置外接矩形的坐标值
	GsTextElement(double pX, double pY, const char* pStr = "", GsTextAnchors pTextAnchors = eTextBottomLeft);
	/// \brief 根据文本符号来设置外接矩形的宽高，然后根据锚点来设置外接矩形的坐标值
	GsTextElement(double pX, double pY, GsTextSymbol* pText, GsTextAnchors pTextAnchors = eTextBottomLeft);
	~GsTextElement();

	/// \brief 返回文本符号
	GsTextSymbolPtr TextSymbol();
	/// \brief 设置文本符号
	void TextSymbol(GsTextSymbol* pTextSym);

	/// \brief 返回锚点所在坐标
	GsRawPoint TextAnchorsPoint();
	/// \brief 设置锚点坐标
	void TextAnchorsPoint(const GsRawPoint& p);
	/// \brief 设置锚点坐标
	void TextAnchorsPoint(const GsRawPoint& p, GsTextAnchors pTextAnchors);

	/// \brief 返回锚点类型
	GsTextAnchors TextAnchors();
	/// \brief 设置锚点类型
	void TextAnchors(GsTextAnchors pTextAnchors);

	/// \brief 返回文本框的宽
	double Width();
	/// \brief 设置文本框的宽
	void Width(double pWidth);

	/// \brief 返回文本框的高
	double Height();
	/// \brief 设置文本框的高
	void Height(double pHeight);


	/// \brief 返回文本框旋转角度，单位为度
	double Angel();
	/// \brief 设置文本框旋转角度，单位为度
	void Angel(double pAngel);

	/// \brief 返回文本框的字符串
	Utility::GsString Text();
	/// \brief 设置文本框的字符串
	void Text(const char* str);

	/// \brief 返回外接矩形框的轮廓
	GsBox Envelope();
	/// \brief 设置外接矩形框的轮廓
	/// \param pBox 外接矩形框的位置和大小
	/// \param bIsStrectch 字体是否按照矩形框拉伸，
	/// \details 
	/// bIsStrectch true-字体全部填充矩形框
	/// bIsStrectch false-宽度不大于高度的基础上，宽度根据字体数量和宽计算单个字体的宽度，高度根据行数和矩形框高度来计算
	void Envelope(const GsBox& pBox, bool bIsStrectch = true);
	/// \brief 根据传入的字符串计算新的外接矩形，用以显示左下角和宽高
	GsBox Envelope(const char* strText);

	/// \brief 根据传入新的矩形，设置文字居中显示
	void EnvelopeCenter(const GsBox& pBox);

	/// \brief 重新设置文字的宽设置，然后box也会发生同比例变化，但是锚点位置不变。因为box是同比例变化所以文字不拉伸。
	void SetTextWidth(double width);
	/// \brief 重新设置文字的高设置，然后box也会发生同比例变化，但是锚点位置不变。因为box是同比例变化所以文字不拉伸。
	void SetTextHeight(double height);
	/// \brief 克隆
	virtual Utility::GsSmarterPtr<GsElement> Clone();
```
1.  TextSymbol：可以设置文字符号等相关文字问题。
2.  TextAnchors：可以获取设置锚点的类型。
3.  TextAnchorsPoint：可以同时设置锚点的位置和类型。
4.  Width：设置文字的宽，高不变，自动拉伸。
5.  Height：设置文字的高，宽不变，自动拉伸。
6.  Angel：设置或获取文字制图的旋转，(绕锚点逆时针旋转)。
7.  Text：设置获取文字制图中的文字。
8.  Envelope：获取文字制图的外接框。
9.  Envelope(GsBox,bool)：传入GsBox，文字中GsBox中显示，bool参数控制是否拉伸。
10.  Envelope(const char *)：传入文字，获取到文字所占用的GsBox空间。
11.  EnvelopeCenter：传入文字的Envlope，文字中Envlope居中显示。
12.  SetTextWidth：设置文字的宽，高自动比例调整。
13.  SetTextHeight：设置文字的高，宽自动比例调整。
```
    //测试用例,文字制图
    GsTextElementPtr ptrText = new GsTextElement(20, 50, "text", eTextBottomCenter);
    //ptrText的构造是点(20,50)作为锚点，锚点类型是BottomCenter，绘制的文字是text。
    
	GsTextSymbolPtr ptrTextSym = new GsTextSymbol();
	ptrTextSym->Text("text");
	GsTextElementPtr ptrText1 = new GsTextElement(20, 50, ptrTextSym, eTextBottomCenter);
	//ptrText1和ptrText的构造方式不同，但是完全一致。
	
	ptrText->Text("abcdef")；
	ptrText->Angle(30);
```
##GsGridElement 格网，方里网##
格网制图，是基于GsMap基础之上的。在MapElement上显示格网，以标注经纬度等信息。
```
/// \brief 按照Map范围构造
	/// \param ptrMap 绑定一个GsMap
	/// \param box 格网的box(设置建议和map制图的范围一致)
	GsGridElement(GsMap* ptrMap, GsBox box);
	/// \brief 构造格网制图
	/// \param double dblXOrigin x轴起始点(经纬度)
	/// \param double dblYOrigin y轴起始点(经纬度)
	/// \param double XIntervalSize x轴起间距(经纬度)
	/// \param double YIntervalSize  y轴起间距(经纬度)
	/// \param ptrMap 绑定一个GsMap
	/// \param box 格网的box(设置建议和map制图的范围一致)
	GsGridElement(double dblXOrigin, double dblYOrigin, double XIntervalSize, double YIntervalSize, GsMap* ptrMap, GsBox box);

	/// \brief 克隆
	virtual Utility::GsSmarterPtr<GsElement> Clone();
	/// \brief 格网线的符号
	GsLineSymbol* LineSymbol();
	/// \brief 外边框符号
	GsLineSymbol* BorderSymbol();
	/// \brief 文字标注符号
	GsTextSymbol* TextSymbol();
	/// \brief label分段显示文字标注符号
	GsTextSymbol* DecimalDivisionalTextSymbol();

	/// \brief 设置格网线的符号
	void LineSymbol(GsLineSymbol* pLineSymbol);
	/// \brief 设置外边框符号
	void BorderSymbol(GsLineSymbol* pLineSymbol);
	/// \brief 设置文字标注符号
	void TextSymbol(GsTextSymbol* pTextSymBol);
	/// \brief label分段显示的文字标注符号
	void DecimalDivisionalTextSymbol(GsTextSymbol* pTextSymbol);
	///// \brief label分段显示符号位置
	//int TextBreakPoint();

	/// \brief 设置格网显示风格
	void GridLineShowType(GsGridLineShowType eGridLineType);
	/// \brief 获取格网显示风格
	GsGridLineShowType GridLineShowType();
	/// \brief 设置四个方向是否显示标注
	void GridLabelAxes(GsGridOrientation eGridLabelAxes);
	/// \brief 获取四个方向是否显示标注
	GsGridOrientation GridLabelAxes();
	/// \brief 设置文字显示方向
	void GridLabelOrientation(GsGridLabelOrientation eGridLabelAxes);
	/// \brief 获取文字显示方向
	GsGridLabelOrientation GridLabelOrientation();
	/// \brief 设置文字的样式
	void GridLabelStringFormat(GsGridLabelStringFormat eGridLabelAxes);
	/// \brief 获取文字的样式
	GsGridLabelStringFormat GridLabelStringFormat();

	/// \brief 设置是否根据Map范围变化调整格网绘制原点,默认位false,如果为true,地图范围变化将根据地图范围动态改变原点
	void FixedOrigin(bool bFixedOrigin);
	/// \brief 获取是否根据Map范围变化调整格网绘制原点,默认位false,如果为true,地图范围变化将根据地图范围动态改变原点
	bool FixedOrigin();

	/// \brief 设置四边是否绘制主刻度
	void MainScalePosition(GsGridScalePosition ePos);
	/// \brief 获取四边是否绘制主刻度
	GsGridScalePosition MainScalePosition();
	/// \brief 设置主刻度的位置，格网外还是格网内
	void MainScaleOrientation(GsGridOrientation eOri);
	/// \brief 获取主刻度的位置，格网外还是格网内
	GsGridOrientation MainScaleOrientation();
	/// \brief 主刻度的长度
	double MainScaleLength();
	/// \brief 主刻度的长度
	void MainScaleLength(double len);

	/// \brief 设置四边是否绘制分刻度
	void DivisionScalePosition(GsGridScalePosition ePos);
	/// \brief 获取四边是否绘制分刻度
	GsGridScalePosition DivisionScalePosition();
	/// \brief 设置分刻度的位置，格网外还是格网内
	void DivisionScaleOrientation(GsGridOrientation eOri);
	/// \brief 获取分刻度的位置，格网外还是格网内
	GsGridOrientation DivisionScaleOrientation();
	/// \brief 分刻度的长度
	double DivisionScaleLength();
	/// \brief 分刻度的长度
	void DivisionScaleLength(double len);
	/// \brief 获取分刻度将主刻度切分的数量
	int DivisionScaleCount();
	/// \brief 设置分刻度将主刻度切分的数量
	void DivisionScaleCount(int count);

	/// \brief 获取x间距
	double XIntervalSize();
	/// \brief 设置x间距
	void XIntervalSize(double size);
	/// \brief 设置x间距
	void XIntervalSize(double degree, double minute, double second);
	/// \brief x原点
	double XOrigin();
	/// \brief x原点
	void XOrigin(double origin);

	/// \brief 获取Y间距
	double YIntervalSize();
	/// \brief 设置Y间距
	void YIntervalSize(double size);
	/// \brief 设置Y间距
	void YIntervalSize(double degree, double minute, double second);
	/// \brief Y原点
	double YOrigin();
	/// \brief Y原点
	void YOrigin(double origin);

	/// \brief 获取文字的大小(Height)
	double TextHeight();
	/// \brief 设置文字的大小(Height)
	void TextHeight(double height);
```
1.  LineSymbol：设置基本的线符号。
2.  BorderSymbol：设置边框的线符号。
3.  TextSymbol：设置文字的符号。
4.  DecimalDivisionalTextSymbol：设置(方里网)经纬度小数部分的文字符号。
5.  GridLineShowType：绘制格网的风格， 格网线，十字形，不绘制三种。
6.  GridLabelAxes：控制四个方向上是否绘制文字。
7.  GridLabelOrientation：控制四个方向上绘制文字的方向。
8.  GridLabelStringFormat：控制绘制文字的样式，度小数，还是度分秒等样式。
9.  FixedOrigin：是否根据Map范围变化调整格网绘制原点,默认位false,如果为true,地图范围变化将根据地图范围动态改变原点。
10.  MainScalePosition：控制四边是否绘制主刻度。
11.  MainScaleOrientation：控制主刻度的位置，格网外还是格网内。
12.  MainScaleLength：主刻度的长度。
13.  DivisionScalePosition：控制四边是否绘制分刻度。
14.  DivisionScaleOrientation：控制分刻度的位置，格网外还是格网内。
15.  DivisionScaleLength：分刻度的长度。
16.  DivisionScaleCount：主刻度被分刻度切分的数量。
17.  XIntervalSize：获取x间距(经纬度)。
18.  XOrigin：原点的经纬度x。
19.  YIntervalSize：获取y间距(经纬度)。
20.  YOrigin：原点的经纬度y。
21.  TextHeight：文字的高度。
```
    //测试用例，格网，方里网
    GsMapPtr pMap;
    GsBox box(10,10,200,290);
	GsGridElementPtr ptrGrid = new GsGridElement(10, 10, 10, 10, pMap,box); 
	GsGridElementPtr ptrGrid1 = new GsGridElement(pMap, box);
```
##GsSlopeRulerElement 坡度尺制图##

用于根据地形图上等高线的平距，确定相应的地面坡度或其逆过程的一种图解曲线尺。
```
/// \brief 坡度尺的类型
enum GsSlopeRulerType
{
	/// \brief 一种是绘相邻六条等高线时，从第五度开始绘制
	eSlopeRulerTypeFive = 0,

	/// \brief 另一种从第十度开始绘制，对应的描述信息的位置都会随着改变
	eSlopeRulerTypeTen,
};
```
```
    /// \brief 得到尺子的横向分隔值
	double IntervalGap();
	/// \brief 设置尺子的横向分隔值
	void IntervalGap(double dIntervalGap);

	/// \brief 得到尺子的横向分隔值
	double TitleGap();
	/// \brief 设置尺子的横向分隔值
	void TitleGap(double dTitleGap);

	/// \brief 得到标注和尺子之间的间隔距离
	double LabelRulerGap();
	/// \brief 设置标注和尺子之间的间隔距离
	void LabelRulerGap(double dLabelRulerGap);

	/// \brief 得到等高距
	double ContourDist();
	/// \brief 设置等高距
	void ContourDist(double dContourDist);

	/// \brief 得到是否显示正切标注
	bool ShowTanlabel();
	/// \brief 设置是否显示正切标注
	void ShowTanlabel(bool bShowTanlabel);

	/// \brief 得到坡度尺的类型
	GsSlopeRulerType SlopeRulerType();
	/// \brief 设置坡度尺的类型
	void SlopeRulerType(GsSlopeRulerType eType);

	/// \brief 得到说明信息字体高度
	double RulerInfoHeight();
	/// \brief 得到说明信息字体高度
	void RulerInfoHeight(double dRulerInfoHeight);

	/// \brief 得到标题文字高度
	double TitleNameHeight();
	/// \brief 设置标题文字高度
	void  TitleNameHeight(double dTitleNameHeight);

	/// \brief 得到标注文字高度
	double LabelTextHeight();
	/// \brief 设置标注文字高度
	void LabelTextHeight(double dLabelTextHeight);

	/// \brief 得到绘制线的符号Symbol
	GsLineSymbol* LineSymbol();
	/// \brief 设置绘制线的符号Symbol
	void LineSymbol(GsLineSymbol* pLineSym);
```
1.  IntervalGap：尺子的横向分隔值。
2.  TitleGap：尺子的横向分隔值。
3.  LabelRulerGap：标注和尺子之间的间隔距离。
4.  ContourDist：等高距。
5.  ShowTanlabel：是否显示正切标注。
6.  SlopeRulerType：坡度尺的类型。
7.  RulerInfoHeight：说明信息字体高度。
8.  TitleNameHeight：标题文字高度。
9.  LabelTextHeight：得到标注文字高度。
10.  LineSymbol：绘制线的符号。
```
	///测试用例
	GsSlopeRulerElementPtr ptrSlopeRulerElement = new GsSlopeRulerElement(220, 10);
	ptrSlopeRulerElement->IntervalGap(3);
	ptrSlopeRulerElement->LabelTextHeight(4);
```

## GsLegendsElement 动态图例 ##
图例是集中于地图一角或一侧的地图上各种符号和颜色所代表内容与指标的说明，有助于更好的认识地图。它具有双重任务，在编图时作为图解表示地图内容的准绳，用图时作为必不可少的阅读指南。

成员变量：
```
	Utility::GsVector<GsLegendGroupPtr > m_lstLegendGroup;
	/// \brief  当新的层添加到映射时是否应添加新项
	bool m_bAutoAdd;
	/// \brief  图例项是否应保持与图层相同的顺序
	bool m_bAutoReorder;
	/// \brief  表明如果items should be shown只有当相关层是可见的。
	bool m_bAutoVisibility;
	/// \brief  保留以备将来使用。
	bool m_bFlowRight;
	/// \brief  样式信息描述
	GsLegendsFormatPtr m_ptrLegendsFormat;
	/// \brief  题图map
	GsMapPtr m_ptrMap;
	/// \brief  图例标题
	Utility::GsString m_strTitle;
	/// \brief  图例标题高度
	double m_dTitleHeight;
	/// \brief  图例标题宽度
	double m_dTitleWidth;
	/// \brief  画图例的起始点位置
	GsRawPoint m_ptRawPoint;
	/// \brief 外框
	GsGeometryPtr m_ptrFrameGeometry; 
	/// \brief 画外框符号
	GsSymbolPtr m_ptrFrameSym; 
	/// \brief  图例所有的element
	Utility::GsVector<GsElementPtr> m_vecElements;	
```
构造函数：
```
	/// \brief 图例的构造函数
	/// \param x 图例矩形框的左上角x坐标
	/// \param y 图例矩形框的左上角y坐标
	/// \param pMap 显示动态图例的map 
	GsLegendsElement(double x,double y,GsMap* pMap);
```

接口：
```
	/// \brief  得到GsLegendGroup的集合
	Utility::GsVector<GsLegendGroupPtr >* ListLegendGroup();

	/// \brief  得到GsLegendFormat为GsLegend样式描述
	GsLegendsFormat* Format();
	void Format(GsLegendsFormat* pFormat);

	/// \brief  得到Map
	GsMap* Map();
	void Map(GsMap *pMap);

	/// \brief  图例的标题
	Utility::GsString Title();
	void Title(const char * strTitle);

	/// \brief 外框符号
	void FrameSymbol(GsSymbol* pLineSym);
	GsSymbol* FrameSymbol();

	/// \brief  当新的层添加是否应添加新项,同时图例与图层顺序相同
	bool AutoAdd();
	void AutoAdd(bool pAutoAdd);

```


## GsLegendsFormat 图例样式信息描述 ##
GsLegendsFormat为图例对象的各种信息描述，内包含所有绑定地图的图例信息和对象，使用时直接从GsLegendsElement图例对象得到，然后对其进行设置即可。

成员变量：

```
	/// \brief  legendgroup直接的垂直距离
	double m_dGroupGap;
	/// \brief  标题和图例之间的垂直距离
	double m_dHeadingGap;
	/// \brief  图例的水平距离，用于不止1列的图例
	double m_dHorizontalItemGap;
	/// \brief  图例画的符号与文字之间的水平距离
	double m_dHorizontalPatchGap;
	/// \brief  层名称与图示图形之间的垂直距离
	double m_dLayerNameGap;
	/// \brief  标题是否是显示
	bool m_bShowTitle;
	/// \brief  标签和描述之间的水平距离。labels and descriptions.
	double m_dTextGap;
	/// \brief 标题和第一个GsLegendGroup之间的垂直距离。
	double m_dTitleGap;
	/// \brief  标题的位置
	GsRectanglePosition m_eRectanglePosition;
	/// \brief  标题文本所使用的GsTextSymbol
	GsTextSymbolPtr m_ptrTitleSymbol;
	/// \brief  GsLegendGroup之间的垂直距离
	double m_dVerticalItemGap;
	/// \brief  图块之间的垂直距离。LegendClass之间的距离
	double m_dVerticalPatchGap;
	/// \brief  图例显示区域
	GsGeometryPtr m_ptrDefaultAreaPatch;
	/// \brief  图例显示路径
	GsGeometryPtr m_ptrDefaultLinePatch;
	/// \brief  图块的高度
	double m_dDefaultPatchHeight;
	/// \brief  图块的宽度
	double m_dDefaultPatchWidth;	
	/// \brief  单列图例的宽度
	double m_dRowWidth;
```

接口：

```
	/// \brief  legendgroup之间的距离
	double GroupGap();
	void  GroupGap(double dblGap);
	/// \brief  标题和图例之间的垂直距离
	double HeadingGap();
	void  HeadingGap(double dblGap);
	/// \brief  图例的水平距离，用于不止1列的图例
	double HorizontalItemGap();
	void  HorizontalItemGap(double dblGap);
	/// \brief  图例画的符号与文字之间的水平距离
	double HorizontalPatchGap();
	void  HorizontalPatchGap(double dblGap);

	/// \brief  层名称与图示图形之间的垂直距离
	double LayerNameGap();
	void  LayerNameGap(double dblGap);
	/// \brief  标题是否是显示
	bool ShowTitle();
	void  ShowTitle(bool bShowTitle);

	/// \brief  标签和描述之间的水平距离。labels and descriptions.
	double TextGap();
	void  TextGap(double dblGap);

	/// \brief 标题和第一个GsLegendGroup之间的垂直距离。
	double TitleGap();
	void  TitleGap(double dblGap);
	/// \brief  标题的位置
	GsRectanglePosition TitlePosition();
	void TitlePosition(GsRectanglePosition eType);
	/// \brief  标题文本所使用的GsTextSymbol
	GsTextSymbol* TitleSymbol();
	void TitleSymbol(GsTextSymbol* pSym);

	/// \brief  GsLegendGroup之间的垂直距离
	double VerticalItemGap();
	void  VerticalItemGap(double dblGap);
	/// \brief  图块之间的垂直距离。LegendClass之间的距离
	double VerticalPatchGap();
	void  VerticalPatchGap(double dblGap);

	/// \brief  图块的高度
	double DefaultPatchHeight();
	void DefaultPatchHeight(double dHeight);
	/// \brief  图块的宽度
	double DefaultPatchWidth();
	void DefaultPatchWidth(double dWidth);
	/// \brief  单列图例的宽度
	double RowWidth();
	void RowWidth(double dRowWidth);
	/// \brief 克隆
```


## GsLegendGroup 图例图层组 ##
每个图层有一个GsLegendGroup，记录当前图层需要显示的图例信息

成员变量：

```
	/// \brief 图例Heading名字
	Utility::GsString m_strHeading;
	/// \brief 图例图层layer名字
	Utility::GsString m_strLayerName;
	/// \brief 图例显示默认符号的名字
	Utility::GsString m_strDefautSymName;
	/// \brief 图例图层是否显示
	bool m_bVisible;
	/// \brief 样式是否与指定的层兼容
	bool m_bCanDisplay;
	/// \brief 当前LegendGroup的列数
	int m_iColumns;
	/// \brief 显示的图例组的索引
	int m_iGroupIndex;
	/// \brief 图例Heading符号
	GsTextSymbolPtr m_ptrHeadingSymbol;
	/// \brief 图例layer符号
	GsTextSymbolPtr m_ptrLayerNameSymbol;
	/// \brief 图例组默认格式信息
	GsLegendClassFormatPtr m_ptrLegendClassFormat;
	/// \brief 获取是否全部GroupClass是否显示为一列
	bool m_bKeepTogether;

	/// \brief 是否在图例中启动新列
	bool m_bNewColumn;
	/// \brief 描述信息是否可见的
	bool m_bShowDescriptions;
	/// \brief Heading是否可见的
	bool m_bShowHeading;
	/// \brief Labels是否可见的
	bool m_bShowLabels;
	/// \brief LayerName是否可见的
	bool m_bShowLayerName;
	/// \brief 图例组样式
	GsLegendItemStyle m_eLegendItemStyle;
```

接口：

```
	/// \brief GsLegendClass个数
	int Count();

	/// \brief Index位置的GsLegendClass
	GsLegendClass* Legend(int Index);

	/// \brief 图例Heading名字
	GeoStar::Utility::GsString Heading();
	void Heading(const char* strHeading);
	
	/// \brief 图例图层layer名字
	GeoStar::Utility::GsString LayerName();
	void LayerName(const char* strHeading);
	
	/// \brief 图例显示默认符号的名字
	GeoStar::Utility::GsString DefautSymName();
	void DefautSymName(const char* strHeading);

	/// \brief 图例图层是否显示
	bool Visible();
	void Visible(bool bVisible);

	/// \brief 指示样式是否与指定的层兼容
	bool CanDisplay();
	/// \brief 图例项中的列数
	int Columns();
	void Columns(int nCols);
	/// \brief 这个项目所显示的图例组的零基索引。使用-1来显示使用这个项目的所有图例组
	int GroupIndex();
	void GroupIndex(int nGroupIndex);
	/// \brief Heading文本符号
	GsTextSymbol* HeadingSymbol();
	void HeadingSymbol(GsTextSymbol* pTextSymbol);
	/// \brief LayerName文本符号
	GsTextSymbol* LayerNameSymbol();
	void LayerNameSymbol(GsTextSymbol* pTextSymbol);

	/// \brief 图例类的默认格式信息。渲染器可以重写。
	GsLegendClassFormat* LegendClassFormat();
	void LegendClassFormat(GsLegendClassFormat* pLegendClassFormat);

	/// \brief 获取是否全部GroupClass是否显示为一列
	/// \return 返回bool
	bool KeepTogether();

	/// \brief 设置是否全部GroupClass显示在一起
	/// \param bool bkeepTogether 是否显示为一列
	void KeepTogether(bool bkeepTogether);

	/// \brief 指示项目是否在图例中启动新列
	bool NewColumn();
	void NewColumn(bool bNewColum);

	/// \brief 描述信息是否可见的
	bool ShowDescriptions();
	void ShowDescriptions(bool bNewColum);
	/// \brief Heading是否可见的
	bool ShowHeading();
	void ShowHeading(bool bNewColum);
	/// \brief Labels是否可见的
	bool ShowLabels();
	void ShowLabels(bool bNewColum);
	/// \brief LayerName是否可见的
	bool ShowLayerName();
	void ShowLayerName(bool bNewColum);

	/// \brief 样式
	GsLegendItemStyle Style();
	void Style(GsLegendItemStyle eStyle);
```

## GsLegendClass 单个图例 ##
表示由单个符号和文字组成的一组图例信息块。多个GsLegendClass组成一个GsLegendGroup。

成员变量：

```
	/// \brief  图例信息样式
	GsLegendClassFormatPtr m_ptrLegendClassFormat;
	/// \brief  描述文本内容
	Utility::GsString m_strDescription;
	/// \brief  标签文本内容
	Utility::GsString m_strLabel;
	/// \brief  使用同一符号的feature个数
	int m_iFeatureCount;
```

接口：
```
	/// \brief  得到描述文本内容
	Utility::GsString Description();
	/// \brief  设置描述文本内容
	void Description(const char *strDescription);

	/// \brief  得到标签文本内容
	Utility::GsString Label();
	/// \brief  设置标签文本内容
	void Label(const char *strLabel);

	/// \brief  得到图例信息样式
	GsLegendClassFormat* Format();
	/// \brief  设置图例信息样式
	void Format(GsLegendClassFormat* pFormat);
	/// \brief  使用同一符号的feature个数
	void FeatureCount(int nCount);
	int FeatureCount();
```

## GsLegendClassFormat 单个图例块信息样式 ##
GsLegendClassFormat是描述GsLegendClass的信息样式。

成员变量：

```
	/// \brief 描述信息所用的文本符号 
	GsTextSymbolPtr m_ptrDescriptionSymbol;
	/// \brief 标签所用的文本符号 
	GsTextSymbolPtr m_ptrLabelSymbol;
	/// \brief 单个图例图块符号 
	GsSymbolPtr m_ptrPatchSymbol;
```

接口：
```
	/// \brief 得到描述信息所用的文本符号
	GsTextSymbol* DescriptionSymbol();
	/// \brief 设置描述信息所用的文本符号
	void DescriptionSymbol(GsTextSymbol *pTextSymbol);

	/// \brief 得到标签所用的文本符号 
	GsTextSymbol* LabelSymbol();
	/// \brief 设置标签所用的文本符号 
	void LabelSymbol(GsTextSymbol *pTextSymbol);

	/// \brief 得到图块符号
	GsSymbol* PatchSymbol();
	/// \brief 设置图块符号
	void PatchSymbol(GsSymbol* pSym);
```

我们在用的过程中，实际只用了m_ptrPatchSymbol的成员变量。m_ptrDescriptionSymbol和m_ptrLabelSymbol变量是预备给将来使用，现阶段暂时无效果。

**ByAutor:yulei and wuyongbo**