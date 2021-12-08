#克吕金插值简介
&emsp;&emsp;空间插值是通过已知点或分区的数据，推求任意点或分区数据的方法，常用于根据离散的采样点来获取连续的表面，包括空间内插和外推两种方法。空间内插提供一种从有限的已知样本点中估计同一区域中其他任意位置的值的方法，外推则是通过已知区域的数据来推求其他区域数据的方法。插值分析可以由有限的采样点数据，估计周围的数值情况，从而掌握研究区域内数据的总体分布状况，使得离散的采样点不仅仅反映其所在位置的数值情况，而且可以反映区域的数值分布。

&emsp;&emsp;克吕金是依据协方差函数对随机过程/随机场进行空间建模和预测（插值）的回归算法。在特定的随机过程，例如固有平稳过程中，克吕金能够给出最优线性无偏估计（Best Linear Unbiased Prediction, BLUP），因此在地统计学中也被称为空间最优无偏估计器（spatial BLUP）。

&emsp;&emsp;内核目前实现了普通克吕金和简单克吕金两种算法。

#算法简介

##1、普通克吕金
&emsp;&emsp;普通克里金法是使用最普通和广泛的克里金方法。该方法假定用于插值的字段值的期望（平均值）未知且恒定。

* 普通克里金法使用的数据应符合数据变化成正态分布的前提假设。
* 普通克里金插值最大的特色不仅是提供一个最小估计误差的预测值，并且可明确的指出误差值的大小。
* 普通克里金法采用两种方式来获取参与插值的采样点，进而获得相应位置点的预测值：
	  1. 在待计算预测值位置点周围一定范围内，获取该范围内的所有采样点，通过特定的插值计算公式获得该位置点的预测值；
	  2. 另一个是在待计算预测值位置点周围获取一定数目的采样点，通过特定的插值计算公式获得该位置点的预测值。 
	  	

##2、简单克吕金
&emsp;&emsp;简单克里金法使用的数据应符合数据变化成正态分布的前提假设。

* 简单克吕金也是常用的克吕金插值方法之一，该方法假定用于插值的字段值的期望（平均值）已知的某一常数。 
* 简单克吕金假设样点的分布趋于二阶平稳，即局部区域内变量的分布不会因为某一空间点发生位移而改变。
* 简单克吕金插值方法不适用于对具有局部趋势情况的样点数据进行插值。 

#参数说明

	enum GsInterpolationAlgorithmType
	{
	
		/// \brief 距离反比权值（Inverse Distance Weighted）插值法。
		/// \brief 该方法通过计算附近区域离散点群的平均值来估算单元格的值，生成格网数据集。这是一种简单有效的数据内插方法，运算速度相对较快。距离离散中心越近的点，其估算值越受影响。
		eIDW,
		/// \brief 简单克吕金（Simple Kriging）插值法。
		/// \brief 简单克吕金是常用的克吕金插值方法之一，该方法假定用于插值的字段值的期望（平均值）已知的某一常数。
		eSimpleKriging,
		/// \brief 普通克吕金（Ordinary Kriging）插值法。
		/// \brief 最常用的克吕金插值方法之一。该方法假定用于插值的字段值的期望（平均值）未知且恒定。它利用一定的数学函数，通过对给定的空间点进行拟合来估算单元格的值，生成格网数据集。它不仅可以生成一个表面，还可以给出预测结果的精度或者确定性的度量。因此，此方法计算精度较高，常用于社会科学及地质学。
		eKriging,
		/// \brief 泛克吕金（Universal Kriging）插值法。
		/// \brief 泛克吕金也是常用的克吕金插值方法之一，该方法假定用于插值的字段值的期望（平均值）未知的变量。在样点数据中存在某种主导趋势，并且该趋势可以通过某一个确定的函数或者多项式进行拟合的情况下适用泛克吕金插值法。
		eUniversalKriging,
		///// \brief 径向基函数（Radial Basis Function）插值法。
		///// \brief 该方法假设变化是平滑的，它有两个特点：1表面必须精确通过数据点；2表面必须有最小曲率。该插值在创建有视觉要求的曲线和等高线方面有优势。
		//eRBF,
		///// \brief 点密度（Density）插值法
		//eDENSITY,
		/// \brief 障碍克吕金插值法。
		/// \brief 该方法允许插值分析时设置障碍数据集，插值过程将绕过障碍区域。
		eBarrierKriging
	}


	struct GS_API GsInterpolationParameter
	{
		GsInterpolationParameter();
		GsInterpolationParameter(const GsInterpolationParameter& ip);
		virtual ~GsInterpolationParameter();
		virtual GsInterpolationParameter* Clone() const;
		virtual GsInterpolationParameter& operator = (const GsInterpolationParameter& other);
		/// \brief 插值分析的范围，用于确定运行结果所得到的栅格数据集的范围。
		GsBox	Bounds;
		/// \brief        获取或设置期望参与插值运算的点数。当查找方式为定长查找时，表示期望参与运算的最少样点数；当查找方式为变长查找时，表示期望参与运算的最多样点数。
		int	ExpectedCount;
		/// \brief 获取或设置在块查找时，最多参与插值的点数。注意，该值必须大于零。
		int	MaxPointCountForInterpolation;
		/// \brief 获取或设置在块查找时，单个块内最多查找点数。注意，该值必须大于零。
		int	MaxPointCountInNode;
		/// \brief 获取或设置插值运算所获得的栅格数据的分辨率。该值不能超过待分析数据集的 Bounds 范围的边长
		double	Resolution;
		/// \brief 获取或设置在插值运算时，查找参与运算点的方式。
		GsInterpolationSearchMode	SearchMode;
		/// \brief 获取或设置查找参与运算点的查找半径。单位与用于插值的点数据集（或记录集所属的数据集）的单位相同。查找半径决定了参与运算点的查找范围，当计算某个位置的未知数值时，会以该位置为圆心，以该属性设置的值为半径，落在这个范围内的采样点都将参与运算，即该位置的预测值由该范围内采样点的数值决定。
		double	SearchRadius;
		/// \brief 插值类型
		GsInterpolationAlgorithmType	Type;
	
		/// \brief  设置执行插值分析的缩放比
		double Scale;
		/// \brief  无效采样点值
		double  dfNoDataValue;
	};


	struct GS_API GsInterpolationKrigingParameter :public GsInterpolationParameter
	{
		GsInterpolationKrigingParameter();
		GsInterpolationKrigingParameter(const GsInterpolationKrigingParameter& other)
		GsInterpolationKrigingParameter& operator=(const GsInterpolationKrigingParameter& other);
		~GsInterpolationKrigingParameter();
		GsInterpolationKrigingParameter* Clone()const;
		/// \brief获取或设置克吕金算法中旋转角度值。此角度值指示了每个查找邻域相对于水平方向逆时针旋转的角度。
		double	Angle;	
		/// \brief获取或设置用于插值的样点数据中趋势面方程的阶数，可选有 1 阶和 2 阶。详情请参见 Exponent 类。此属性只适用于泛克吕金方法。
		int Exponent;
		/// \brief获取或设置插值字段的平均值，即采样点插值字段值总和除以采样点数目。此属性只适用于简单克吕金方法。
		double	Mean;
		/// \brief获取或设置块金效应值。
		double	Nugget;
		/// \brief获取或设置自相关阈值。自相关阈值是指当一个半变函数在达到一定距离的时候，曲线的趋势不能够再增长，即趋于水平，那么曲线最初开始水平的距离称为自相关阈值。
		double	Range;
		/// \brief获取或设置基台值
		double	Sill;
		/// \brief权值计算因子，目前只支持两个
		UTILITY_NAME::GsVector<double> WeightFactors;
	};


&emsp;&emsp;目前实现的克吕金算法主要用到如下参数：
	
	GsInterpolationParameter::Type
&emsp;&emsp;取值eSimpleKriging和eKriging

	UTILITY_NAME::GsVector<double> WeightFactors;
&emsp;&emsp;用于计算权值的参数

&emsp;&emsp;例如内核目前只支持a+b*x这种计算公式。那么WeightFactors里面存储的就是a和b的值。


#主要接口

	class GS_API GsRasterInterpolationAnalysis :public Utility::GsRefObject
	{
		void * m_Interpolate;
		GsInterpolationParameter* m_IP;
		//输入给定范围
		GsBox	m_Bounds;
		//输出格网大小
		int		m_nWidth;
		int		m_nHeight;
	public:
		/// \brief  插值分析构造函数
		GsRasterInterpolationAnalysis(const GsInterpolationParameter & InterParams);
		~GsRasterInterpolationAnalysis();
	
		/// \brief 设置矢量输出图层, 此图层将格网一行一行输出
		void  OutputData(GsAnalysisDataIO *  hLayer);
	
		/// \brief  插值算法参数
		/// \return 返回设置的插值算法参数
		GsInterpolationParameter InterpolationParameter()const ;
		void InterpolationParameter(GsInterpolationParameter IParam);
	
		/// \brief 设置分析数据起始点x坐标
		void SrcX(double dblSrcX);
		/// \brief 获取分析数据起始点x坐标
		double SrcX()const;
		/// \brief 获取分析数据起始点Y坐标
		double SrcY()const;
		/// \brief 设置分析数据起始点Y坐标
		void SrcY(double dblSrcY);
	
		/// \brief 设置分析数据X方向的分辨率
		void ResolutionX(double dblResX);
		/// \brief 获取分析数据X方向的分辨率
		double ResolutionX()const;
		/// \brief 设置分析数据Y方向的分辨率
		double ResolutionY()const;
		/// \brief 获取分析数据Y方向的分辨率
		void ResolutionY(double dblResY);
		/// \brief 设置输出格网宽度
		int Width()const;
		/// \brief 获取输出格网宽度
		void Width(int nW);
		/// \brief 获取输出格网高度
		int Height()const ;
		/// \brief 设置输出格网高度
		void Height(int nH);
	
		/// \brief  执行插值分析
		/// \param	double* pInPutPoints 分析的点
		/// \param int pInPutPointsLength 点长度
		/// \param  double * InputValues 点的观测值
		bool Interpolate(double* pInPutPoints, int pInPutPointsLength, double * InputValues);
	};

&emsp;&emsp;目前主要使用如下接口：

	void  OutputData(GsAnalysisDataIO *  hLayer);
	int Width()const;
	void Width(int nW);
	int Height()const ;
	void Height(int nH);
	bool Interpolate(double* pInPutPoints, int pInPutPointsLength, double * InputValues);

&emsp;&emsp;参数说明

InputValues：就是上文提到的a+b*x公式的x的值。克吕金算法通过此采样值，估算pInPutPoints点对应的采样值

#代码示例

	class InterpolationGrid : public GsAnalysisDataIO
	{
		GsRasterClassPtr ptrRsaterCls = 0;
		GsRasterPtr ptrRaster = new GsRaster();
	public:
		InterpolationGrid(const char* tiffile, int rowsize, int colsize, double Geo[6], GsBox bbox, GsSpatialReference* pSp)
		{
			GsFileGeoDatabaseFactoryPtr ptrFile = new GsFileGeoDatabaseFactory();
			GsConnectProperty conn;
			GsFile file(GsEncoding::ToUtf8(tiffile).c_str());
			conn.Server = file.Parent().FullPath();
			GsGeoDatabasePtr ptrGeo = ptrFile->Open(conn);
			GsRasterColumnInfo info;
			info.BlockHeight = 1;
			info.BlockWidth = rowsize;
			info.Width = rowsize;
			info.Height = colsize;
			info.FeatureType = eRasterFeature;
			memcpy(info.GeoTransform, Geo, sizeof(double) * 6);
			info.DataType = eFloat64RDT;
			info.BandTypes.push_back(eUndefinedBand);
			info.XYDomain = bbox;
	
			if (file.Exists())
				file.Delete();
			ptrRsaterCls = ptrGeo->CreateRasterClass(file.Name(false), GsRasterCreateableFormat::eGTiff, info, pSp);
			ptrRaster->Height(1);
			ptrRaster->Width(rowsize);
	
		}
	
		bool Writer(int i, int j, const  unsigned char* pHead, int nlen)
		{
			ptrRaster->OffsetX(i);
			ptrRaster->OffsetY(j);
			ptrRaster->DataPtr(pHead, nlen);
			return ptrRsaterCls->WriteRaster(ptrRaster);
	
		}
	
		virtual int OnData(GsFeatureBuffer* pData)
		{
			if (!pData)
				return 1;
			int i = pData->IntValue(2);
			int j = pData->IntValue(3);
			const unsigned char *pD = pData->BlobValue(4);
			int nLen = pData->BlobValueLength(4);
			Writer(i, j, pD, nLen);
			return 0;
		}
	};
	
	
	double coords[] = {
		10.1, 10.11,		10.0, 10.3,		10.2, 10.5,
		10.101, 10.4,		10.1, 10.2,		10.1, 10.6,
		10.1, 10.6,		10.1, 10.3,		10.0, 10.4
	};
	
	double value[] = {
		1.05, 1.2, 1.1
	};
	
	GS_TEST(SimpleKriging, InterpolateValue, SL, 20200624)
	{
		GsInterpolationKrigingParameter parameter;
		parameter.WeightFactors.push_back(1);
		parameter.WeightFactors.push_back(2);
		parameter.Type = eSimpleKriging;
		GsRasterInterpolationAnalysisPtr Analyser = new GsRasterInterpolationAnalysis(parameter);
		Analyser->Height(3);
		Analyser->Width(3);
		ASSERT_TRUE(Analyser->Interpolate(coords, sizeof(coords) / sizeof(double), value));
	
		GsMatrixD mat;
		InterpolationGrid grid(MakeOutputFile("SimpleKriging.tif"), 3, 3, mat.Ptr(), GsBox(0, 0, 2.0, 2.0), nullptr);
		Analyser->OutputData(&grid);
	}

##注意：

&emsp;&emsp;此代码所用到的参数coords和value都是随意编造，没有按照简单克吕金和普通克吕金的要求，因此得到的结果不一定正确，仅仅是为了演示如何使用此算法。