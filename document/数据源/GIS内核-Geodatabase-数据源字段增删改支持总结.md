

内核GsFeatureClass可修改字段接口如下:

	/// \brief 增加字段
	/// \param field 添加的字段
	/// \return 是否添加成功
	virtual bool AddField(const GsField& field);

	/// \brief 删除字段
	/// \param name 需要删除的字段名称
	/// \return 是否删除成功
	virtual bool DeleteField(const char* name);

	/// \brief 修改字段
	/// \param nIndex 被修改的字段的索引
	/// \param field 修改的目的字段
	/// \return 是否修改成功
	virtual bool AlterField(int nIndex, const GsField& field);

	/// \brief 设置字段别名
	/// \param nIndex 字段索引
	/// \param strAliasName 别名
	/// \return 是否设置成功
	virtual bool FieldAliasName(const int nIndex, const char* strAliasName);

目前GsFeatureClass上各数据源得支持字段增删改支持情况如下表:

sqlite       支持: 增加,删除,修改
shp          支持: 增加,删除,修改
oracle       支持: 增加,删除,修改
mysql        支持: 增加,删除,修改
PG:          支持: 增加,删除,修改​​
ES支持:      支持:​ 增加,删除,修改
gdb          支持: 增加,删除,
gpkg         支持: 增加,删除,
mongodb      支持: 增加,删除
Kingbase     支持: 增加,删除
GBase        支持: 增加,删除
Memory       支持: 增加,删除
dameng       支持: 增加,删除