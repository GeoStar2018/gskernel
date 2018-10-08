## GsLayoutParser 制图序列化类##
制图序列化类是用户配置的制图样式进行序列化或者反序列化文件的类，用户可保存或者打开序列化的文件。



接口：
```
	/// \brief 解析制图模板为地图
	/// \return 返回值为true，则读取文件成功，否则读取文件失败
	bool Read(const char* strFilePath, GsPageLayout* pLayout);

	/// \brief 保存到文件
	/// \return 返回值为true，则保存文件成功，否则保存文件失败
	bool Save(GsPageLayout* pLayout, const char* strFilePath);

```


## GsLayoutParser 使用示例 ##

保存文件接口使用示例：
```
bool CWtl_showpagelayoutView::testSave()
{
	const char* strFileNAme = "d:\\layoutSave.xml";
	GsPageLayoutPtr ptrPageLayer = m_PageLayout;
	if (!m_PageLayout)
	{
		return false;
	}
	GsLayoutParser parserSave;

	return parserSave.Save(ptrPageLayer, strFileNAme);
}
```
返回值为true，则保存文件成功，否则保存文件失败


读取文件接口使用示例：
```
bool CWtl_showpagelayoutView::testSave()
{
	const char* strFileNAme = "d:\\layoutSave.xml";
	GsPageLayoutPtr ptrPageLayer = m_PageLayout;
	if (!m_PageLayout)
	{
		return false;
	}
	GsLayoutParser parserRead;

	return parserRead.Read(strFileNAme，ptrPageLayer);
}
```
返回值为true，则读取文件成功，否则读取文件失败