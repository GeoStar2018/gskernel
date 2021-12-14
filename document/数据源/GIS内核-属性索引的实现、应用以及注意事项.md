# **GIS内核-Geodatabase-属性索引的设计与实现** #
## 需求分析以及设计范围 ##
为提高数据库查询效率，需要实现根据属性创建相应的索引。

**设计范围：实现数据库的索引创建、删除以及判断索引是否存在。**

**实现范围：目前仅实现了sqlite和mysql，之后会根据数据库是否支持索引（shapefile和内存数据库不支持）来实现此功能。**

## 接口类图关系 ##

内核支持的所有数据库都继承同一个类，继承层次完全一致，因此，此次只提供sqlite的接口类图。

##详细接口类声明##
没有重新设计整个类，仅仅是添加几个接口，因此省略其他接口。

GsFeatureClass新增接口：

    class GS_API GsFeatureClass:public GsGeoDataRoom
	{
	public:

		...
	
		/// \brief 创建属性索引
		/// \brief strFeildNames	字段名列表
		/// \brief bUnique			是否是唯一索引
		/// \brief strIndexName		索引名称
		virtual bool CreateAttributeIndex(Utility::GsVector<Utility::GsString>& strFeildNames, bool bUnique, const char* strIndexName);
	
		/// \brief 创建属性索引
		/// \brief strFeildIndexes	字段索引列表
		/// \brief bUnique			是否是唯一索引
		/// \brief strIndexName		索引名称
		virtual bool CreateAttributeIndex(Utility::GsVector<int>& FeildIndexes, bool bUnique, const char* strIndexName);
	
		/// \brief 删除属性索引
		/// \brief strIndexName		属性索引名称
		virtual bool DeleteAttributeIndex(const char* strIndexName);
	
		struct AttributeIndexContext {
			Utility::GsString	strIndexName;
			Utility::GsVector<Utility::GsString>	vecFeildNames;
			bool bUnique;
		};
		/// \brief 获取索引信息列表
		virtual void AttributeIndex(Utility::GsVector<GsFeatureClass::AttributeIndexContext>& vecIndexContext);
	};

GsQueryFilter新增或修改接口：

	class GS_API GsQueryFilter:public Utility::GsRefObject
	{
	protected:

		...

		Utility::GsString  m_strIndexName;
	public:
		/// \brief 从where子句构造
		///\param strWhere SQL查询的where子句，不包含where
		GsQueryFilter(const char* strWhere = NULL, const char* strIndexBy = NULL);
	
		/// \brief 获取IndexBy子句
		/// \biref 返回IndexBy子句字符串
		virtual Utility::GsString IndexByClause();
	
		/// \brief 设置index by子句
		/// \param strIndexBy SQL查询的index by子句
		virtual void IndexByClause(const char* strIndexBy);	

		...
	};

##关键SQL语句##
###创建属性索引###
括号代表可选

	create (unique) index index_name on table_name(feild_name1, feild_name2 ...)

###删除属性索引###

####sqlite语法####

	drop index index_name

####mysql语法####

	drop index index_name on table_name

###查看属性索引是否存在###

####sqlite语法####

	select * from sqlite_master where type ='index' and tbl_name = table_name and name = index_name

####mysql语法####

	SELECT * FROM information_schema.statistics WHERE table_schema = 'database_name' AND table_name = 'table_name' AND index_name = 'index_name'

##使用方法##

sqlite在使用索引查询时，需要设置index by子句，mysql不需要，因为mysql会自动使用索引。

###代码示例###

	GeoStar::Kernel::GsSqliteGeoDatabaseFactory sqliteFac;
	GeoStar::Kernel::GsConnectProperty vConn;
	vConn.Server = ...													//path;
	GsGeoDatabasePtr ptrGDB = sqliteFac.Open(vConn);

	GsFeatureClassPtr feaClass = ptrGDB->OpenFeatureClass(...);
	const char* index[1] = { "FNODE_" };	
	GsVector<GsString> vec;							//字段名称列表
	vec.push_back("test_feild");
	if (!feaCleas->CreateAttributeIndex(vec, false, "test_index");
		...
	if (!feaClass->DeleteAttributeIndex("test_index"))
		...
	GsVector<GsFeatureClass::AttributeIndexContext> vecIndex;//获取到的索引春如vecIndex
	feaClass->AttributeIndex(vecIndex);

	GsQueryFilterPtr filter = new GsQueryFilter("FNODE_ < 10", "FNODE_INDEX");  // mysql不需要设置indexby子句
	filter->IndexByClause("FNODE_INDEX");
	GsFeatureCursorPtr cursor = feaClass->Search(filter);
	cursor->Next();

##注意事项##

1、空间查询时，不支持属性索引

2、sqlite必须指定使用索引查询，并且where子句里面必须用建立索引的字段作为条件，mysql会自动使用索引，无需指定



		