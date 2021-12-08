## 刷新与符号大小单位转换的问题 ##

例如：当快速的多次创建注记时，调用DT的单位转换有时结果不正确

原因是在绘制过程中，内核会将当前图层的参考比例尺设置到pDisplay.GsDisplayTransformation中，因此，通过pDisplay.GsDisplayTransformation来计算大小是根据当前图层的参考比例尺来计算的，所以得到的结果不正确,内核代码如下：

```
bool GsLayer::Draw(GsDisplay* pDisplay,GsTrackCancel* pCancel,GsDrawPhase eDrawPhase)
{
	//如果不需要绘制则不绘制。
	if (!NeedDraw(pDisplay, eDrawPhase))
		return false;

	//计算参考比例尺。
	double dblScale = 0;
	if (!Utility::GsMath::IsEqual(m_dblReferenceScale, 0))
	{
		dblScale = pDisplay->DisplayTransformation()->ReferenceScale();
		pDisplay->DisplayTransformation()->ReferenceScale(m_dblReferenceScale);
	}

	CanvasClipRecovery tmpClip(pDisplay, this);

	bool b = InnerDraw(pDisplay,pCancel,eDrawPhase);
	
	if(!Utility::GsMath::IsEqual(m_dblReferenceScale,0))
		pDisplay->DisplayTransformation()->ReferenceScale(dblScale);
	return b;

}
```
如果InnerDraw函数还在执行中，上层如下调用PointToMM函数，得到的结果将不正确，代码如下：

```
pDisplay->DisplayTransformation()->PointToMM(9.0) \\绘制中，同时调用这个函数，得到的结果不正确;
```
因此，在绘制过程中，上层不应该直接引用pDisplay.GsDisplayTransformation。


## 正确的使用方法 ##
以上问题，上层可自己维护一个GsDisplayTransformation的对象，专门用于进行PointToMM这类操作。
