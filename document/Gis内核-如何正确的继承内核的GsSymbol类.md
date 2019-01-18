## 以GsFillSymbol继承内核的GsSymbol为例讲解如何正确继承符号类 ##

问题：如何正确的继承内核的GsSymbol符号基类？
为什么会选择GsFillSymbol  和 GsSimpleFillSymbol这个作为例子？
是因为，
1：涉及到了三层继承。
2：中间这层GsFillSymbol也是可以被创建的。 例如GsFillSymbolPtr ptrFill = new GsFillSymbol()
3: 中间这层包含GsLineSymbolPtr m_ptrLineSymbol对象，并且这个对象也是符号类对象。在使用过程中和释放过程中都需要注意这个地方。


首先将GsSymbol这个符号基类部分重要接口展示出，如下列代码。
```
/// \brief 符号对象基类
class GS_API GsSymbol:public Utility::GsRefObject
{
	/*省略部分接口*/

protected:
	/// \brief 绘制的画布
	GsCanvasPtr m_ptrCanvas;
	/// \brief 坐标转换
	GsDisplayTransformationPtr m_ptrDT;
	
protected:
	/// \brief 当开始绘制的时候发生
	/// \details 子类通过覆盖此方法实现自定义的数据准备
	virtual void OnStartDrawing();
	/// \brief 当结束绘制的时候发生
	/// \details 子类通过覆盖此方法实现自定义的数据回收过程
	virtual void OnEndDrawing();

	/// \brief 缺省构造
	GsSymbol();
public:
	virtual ~GsSymbol();
	
	/// \brief 开始绘制
	virtual bool StartDrawing(GsCanvas* pCanvas, GsDisplayTransformation *pDT);
	/// \brief 结束绘制
	virtual bool EndDrawing() ;

};
 
```
其次，GsFillSymbol类是GsSymbol的派生类。里面包含自己特有的属性，同时包含了一个lineSymbol符号对象的指针。
重点：这个时候可以看到继承了父类的OnStartDrawing() 和 OnEndDrawing(), 其中这两个函数是protected
同时也要注意到 并没有重载 StartDrawing() 和 EndDrawing()，其中这两个函数在父类中是public

```

/// \brief 面符号基类
class GS_API GsFillSymbol:public GsSymbol, public UTILITY_NAME::GsSerialize
{
protected:
	/// \brief 填充颜色
	GsColor m_Color; 
	/// \brief 面的边线符号
	GsLineSymbolPtr m_ptrLineSymbol;
	/// \brief 面的边界线是否转化为线数据
	bool m_GenerateLine;
	 
protected: 
	/// \brief 当开始绘制的时候发生
	virtual void OnStartDrawing();
	/// \brief 当结束绘制时发生
	virtual void OnEndDrawing();
	 
public:
	GsFillSymbol();
	virtual ~GsFillSymbol();
	
};


```
然后这个时候，GsSimpleFillSymbol 继承了 GsFillSymbol，其中包含自己特殊的属性 m_ptrBrush


```
/// \brief 简单面符号
class GS_API GsSimpleFillSymbol:public GsFillSymbol
{
	
	GsSolidBrushPtr m_ptrBrush;
	GsBrushStyle m_Style;
protected:

	virtual void OnDraw(GsGraphicsPath* pPath,GsGeometryBlob * pBlob);

	virtual void OnStartDrawing();
	virtual void OnEndDrawing();
public:
	GsSimpleFillSymbol();
	GsSimpleFillSymbol(const GsColor& c);
	virtual ~GsSimpleFillSymbol();
	
};
```


那么首先，使用方法如下。
重点： ptrSym需要StartDrawing() 然后 Draw()  然后 EndDrawing();
首先 GsSymbol::StartDrawing(*)函数是设置基类的m_ptrDt和m_ptrCanvas

那么这里就需要说明一下流程。
流程：
1：首先ptrSym是GsSimpleFillSymbol类型的，startDrawing()的时候，就会调用 GsSymbol::StartDrawing(*)
这个时候就会初始化GsSymbol的相关信息。

2：在GsSymbol::StartDrawing函数中，会调用OnStartDrawing函数，这个时候，就会调用GsSimpleFillSymbol::OnStartDrawing(*)


```
	GsRingPtr r1 = new GsRing(GsRawPoint(200,200), 50, 20);

	GsSimpleFillSymbolPtr ptrSym = new GsSimpleFillSymbol(GsColor::Red);

	//1、创建环境
	GsImageCanvasPtr ptrMemCanvas =  new GsMemoryImageCanvas(512,512);
	ptrMemCanvas->Clear(GsColor::Yellow);
	GsDisplayTransformationPtr ptrDT = new GsDisplayTransformation(
		GsBox(0,0,512,512),GsRect(0,0,512,512));
	GsCanvasPtr ptrCanvas = ptrMemCanvas.p;

	ptrSym->StartDrawing(ptrMemCanvas, ptrDT);
	ptrSym->Draw(r1);
	ptrSym->EndDrawing();

	ptrMemCanvas->Image()->SavePNG("d:/1.png");
```

```
bool GsSymbol::EndDrawing() 
{
	OnEndDrawing();//利用多态性，调用子类的OnEndDrawing()
	m_ptrCanvas.Release();
	m_ptrDT.Release();
	return true;
}
```


```
void GsFillSymbol::OnEndDrawing()
{
	if(m_ptrLineSymbol)
		m_ptrLineSymbol->EndDrawing();
	GsSymbol::OnEndDrawing();// 释放父类的资源，这行也可以不用写，因为 GsSymbol::OnEndDrawing()函数内什么都没干
}

void GsSymbol::OnEndDrawing()
{

}

```

```
void GsSimpleFillSymbol::OnStartDrawing()
{
	GsFillSymbol::OnStartDrawing();//这个时候必须调用父类的OnStartDrawing(), 因为GsFillSymbol::OnStartDrawing()函数中有些数据需要在绘制之前处理。
	//20150901，去掉m_Style == eEmptyBrush时返回情况，Qt画刷可以设置为eEmptyBrush
	if(m_Color.A ==0)
		return;

	m_ptrBrush = m_ptrCanvas->CreateSolidBrush(m_Color);
	m_ptrBrush->Style(m_Style);
}

void GsSimpleFillSymbol::OnEndDrawing()
{
	m_ptrBrush.Release();
	GsFillSymbol::OnEndDrawing();////这个时候必须调用父类的OnEndDrawing(), 因为GsFillSymbol::OnEndDrawing()函数中有些数据需要在绘制之后释放。
}
```


```
void GsFillSymbol::OnStartDrawing()
{
	if(m_ptrLineSymbol)
		m_ptrLineSymbol->StartDrawing(m_ptrCanvas,m_ptrDT);
}

void GsFillSymbol::OnEndDrawing()
{
	if(m_ptrLineSymbol)
		m_ptrLineSymbol->EndDrawing();
	GsSymbol::OnEndDrawing();
}
```