# GIS内核-Geodatabase-镶嵌数据集功能总结 #

# 创建 #

    GsGeoDatabasePtr ptrGDB = GsPostGISGeoDatabaseFactory().Open(conn);	
	GsRasterCreateableFormat eFormat = eVRT;
	GsRasterColumnInfo oColumnInfo;	
	GsSpatialReference* pSR;
	GsSpatialReferencePtr ptrSr = new GsSpatialReference(4326);
	GsFields fls;
	fls.Fields.push_back(GsField("test", Data::GsFieldType::eBoolType));
	GsConfig config;
	config.Child("Options/FieldsInfo").Value(fls.ToXml()); // 将指定字段写入strOptions中
	GsString strOptions = config.Save();
	ptrRasterCls = ptrGDB->CreateRasterClass("tm04", eVRT, oColumnInfo, ptrSr, strOptions.c_str());

针对CreateRasterClass(const char* strName,GsRasterCreateableFormat eFormat,const GsRasterColumnInfo& oColumnInfo,GsSpatialReference* pSR,const char* strOptions = NULL)接口的参数介绍

1. GsRasterCreateableFormat eFormat 
-	栅格镶嵌数据集直接给eVRT类型
2. GsSpatialReference* pSR 
-	可为空，此时不显示栅格
-	不为空，可以融合与非融合两种方式显示栅格数据
3. strOptions 参数选项
-	config.Child("Options/FieldsInfo").Value(fls.ToXml());用来设置eVRT镶嵌数据集指定需要创建的字段，创建后的表将有本身镶嵌数据集的OID Geometry  Raster RasterMeta DataName五个字段外，还有传入的test字段（PS：config.Child("Options/NoDataValue").Value(0)用来设置GsFileGeoDatabaseFactory创建eGTiff eIMG等栅格格式设置无效值，以前字符串分割创建方式仍然有效，两种方式都可生效）

# 显示方式 #
1. 融合显示

    	GsRasterClassPtr ptrRasterCls = ptrGDB->OpenRasterClass("tm04");
    	if (ptrRasterCls)
    	{
    		ptrRasterCls->MetadataItem("extension", "RasterViewMode", "eMergeView");
    	}

此方式添加的栅格数据必须相同空间参考，相同波段，相同类型
2. 非融合显示

		GsRasterClassPtr ptrRasterCls = ptrGDB->OpenRasterClass("tm04");
		if (ptrRasterCls)
		{
		    ptrRasterCls->MetadataItem("extension", "RasterViewMode", "eSingleView");
		}

可添加任意的栅格数据


# 增删改字段 #
镶嵌数据集支持直接用rowclass打开其数据表，故操作字段时，可以使用rowclass操作字段的方式对字段进行增删改，但是需要注意，不要动OID Geometry  Raster RasterMeta DataName五个字段

# 添加栅格数据 #

	ptrRasterCls = ptrGDB->OpenRasterClass("tm04");

	if (!ptrRasterCls)
		return;

	GsMosaicRasterManagerPtr ptrMgr = ptrRasterCls->ExtensionData();
	if (!ptrMgr)
		return;
	ptrMgr->AddRaster("G:\\rasterdata\\R9.TIF");
	ptrMgr->AddRaster("G:\\rasterdata\\R10.TIF");
	ptrMgr->AddRaster("G:\\rasterdata\\R11.TIF");

# MosaicRasterManager功能介绍 #

	/// \brief 添加Raster
	virtual bool AddRaster(GsRasterClass *pRaster);
	/// \brief 移除Raster
	virtual bool RemoveRaster(GsRasterClass *pRaster);
	/// \brief 添加Raster
	virtual bool AddRaster(const char* pRasterPath) ;
	/// \brief 移除Raster
	virtual bool RemoveRaster(const char* pRasterName);
	/// \brief 清空Raster
	virtual bool Clear() ;
	/// \brief 获取相关联的所有栅格
	virtual UTILITY_NAME::GsVector< Utility::GsSmarterPtr<GsRasterClass>> RasterClasses();
	/// \brief 获取相关联的所有栅格名称
	virtual UTILITY_NAME::GsVector<UTILITY_NAME::GsString> RasterClassesName();
	/// \brief 通过存储的ID查询
	virtual Utility::GsSmarterPtr<GsRasterClass> RasterClass(int id);
	/// \brief 通过名称查询
	virtual Utility::GsSmarterPtr<GsRasterClass> RasterClass(const char* strName);	
	/// \brief 获取RasterMeta的属性
	/// \param 获取的raster的RasterMeta属性
	virtual Utility::GsString RasterMeta(GsRasterClass* ptrRaster);
	/// \brief 设置RasterMeta的属性
	/// \param 设置的raster的RasterMeta属性
	/// \param strMeta 属性值
	virtual void RasterMeta(GsRasterClass* ptrRaster, const char* strMeta);
	/// \breif 构建raster的缩略图
	///\param nWidth 缩略图宽
	///\param nHeigh 缩略图高
	///\param bForce 是否强制按宽高获取，false 为以nWidth为准，根据raster实际大小等比出图，true为强制按给定nWidth，nHeigh出图
	virtual void BuildThumbnail(int nWight, int nHeigh, bool bForce = false)；
	/// \brief 构建镶嵌数据集的缩略图
	/// \param nWight 缩略图的宽
	/// \param nHeigh 缩略图的高
	virtual Utility::GsSmarterPtr<Utility::GsImage> Thumbnail()；
	/// \brief 构建过滤的镶嵌数据集
	virtual void BuildFilter(GsQueryFilter* pFilter)；
	/// \brief 得到构建过滤的镶嵌数据集的条件
	virtual Utility::GsSmarterPtr<GsQueryFilter> BuildFilter() ；
	/// \brief 查询符合条件的栅格
	virtual UTILITY_NAME::GsVector< Utility::GsSmarterPtr<GsRasterClass>> Search(GsQueryFilter* pFilter)

# 镶嵌概视图显示 #
概视图显示需要有缩略图，也就是镶嵌数据集创建了缩略图才能显示概视图

概视图的显示由配置控制控制

GsGlobalConfig::Instance().Child("Kernel/GeoDatabase/MosaicRasterClass/IsDrawThumbnail").BoolValue(false);

默认是不显示概视图，只有设置了参数IsDrawThumbnail为true，才会显示概视图

# 镶嵌数据集支持的数据源 #
目前镶嵌数据集仅支持数据库的数据源，支持的数据源有：DM数据源，mysql数据源，oracle数据源，postgis数据源，ODBC数据源
