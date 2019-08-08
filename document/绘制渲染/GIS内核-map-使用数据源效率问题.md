# 使用数据源效率问题 #

## 需求分析 ##
上层使用时，经常因为使用错误的方法调用，导致绘制出图效率变慢，或者其他奇奇怪怪的bug产生。也因为给这个开发者讲解相关使用说明后，相关代码进行更改，过一段时间另一个开发者又因为同一个问题，导致出现前面出现过的问题。故写出该文档内容，帮助二次开发者使用相关接口。

## QA分析 ##
1.为什么我调用放大缩小时，绘制会出现卡顿现象，且当前比例的图未绘制完成，无法进行下一步的放大缩小操作等？

答：这个问题是未中断绘制导致，二次开发使用时，在自己的layer图层重写InnerDraw这个函数时，根本就没有考虑到跳出循环，不绘制的情况。下图就是典型的例子。

![](pic/QA1.png)

这里很明显，在调用时，while循环没有中断的情况，故每次绘制，都必须遍历完所有的feature才会进行后续操作。
正确的方式应该在每次循环调用时，判断一次是否需要继续绘制。
如下图：
![](pic/QA2.png)


2.我程序出问题了，运行程序崩溃？
![](pic/QA3.png)

答：二次开发时，经常遇到这样的问题，程序出问题了，程序崩溃了。有时候还不能详细的描述并分析原因到底在什么位置，所有问题都往外抛，而没有考虑到可能是因为自己代码写的不规范，不高效导致。上图的问题很明显，就是递归调用了next导致堆栈溢出出现的问题，笔者猜测，二次开发在写next()函数时，每次next都生成了一个新的feature，然后没找到，就继续next递归调用。猜测可能是这样写的：

	GsFeaturePtr FeatureCursor::Next()
	{
		GsFeaturePtr pFea = m_ptrClass->Feature(oid);
		if(!pFea || // 判断这个pfea是否符号条件）
			// 不符合 return Next()；
		return pFea;
	}

这样写就有可能出现上图中可能的错误。解决方法：

    class GS_API GsFeatureCursor:public Utility::GsRefObject
    {
    public:
    	/// \brief 获取下一个地物对象
    	/// \details 每次回生成一个新的地物对象
    	///\return 返回下一个地物对象的实例或者空
    	virtual GsFeaturePtr Next() = 0;
    	
    	/// \brief 获取下一个地物对象数据而不用产生新的对象
    	/// \details 性能更高的方法，以访问者模式将下一个地物的数据设置到传入的地物对象内部。
    	///\param pFea 一个非空地物对象的指针
    	///\return 返回是否存在下一个地物，如果传入地物对象为空则一定范围false
    	virtual bool Next(GsFeature* pFea) = 0;
    };

上述两个函数重写时遵循规则，Next()里面只生成一个feature，没找到自己需要的就在函数里面使用Next(GsFeature* pFea)递归找，防止堆栈溢出。
    
    GsFeaturePtr GsFeatureCursor::Next()
    {
    	if (m_index >= m_pRS->count()) 
    		return 0;
    	GsFeaturePtr ptrFea =  new GsTMPFeature(m_ptrFeaClass, NULL, NULL);
    	if (Next(ptrFea))
    		return ptrFea;
    	return 0;
    }

按上述方法写。调用时：

	GsCursorPtr ptrCursor;
	GsfeaturePtr pFea = ptrCursor->Next();
	do
	{

		// 你的循环操作；		
	}while(ptrCursor->Next(pFea))
	
这样保证遍历查找过程中，始终只会生成一个feature，不会出现堆栈溢出的现象，同时，还可以提升效率。

