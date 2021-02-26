GIS内核-标注表达式规则

目前的表达式规则如下:

依赖选项:

依赖 gslua51port.dll或者gspythonport.dll

任何应用需要动态链接这两个库其中的一个(用什么脚本链接对应库即可),  并且在使用GsLabelProperty 设置LabelField属性前调用注册函数:	GsLuaScriptHost::RegisterClassFactory(); 或者	GsPythonScriptHost::RegisterClassFactory();

表达式书写规则

```markdown
脚本名称(Lua 或者 Python)

具体脚本 : (由函数或者直接脚本组成,传参是从GsFeature对象获取,  需要指定计算的列 ,使用[]  包起来)

LabelValue =  计算值 (此值LabelValue将当作字段值用来标注, 这里需要调用定义的脚本函数给LabelValue赋值即可)
```


```
/// \brief 标注文本表达式的类型
enum class GsLabelExpressionType
{
	/// \brief 字段名
	eLabelExpressionField,

	/// \brief 标签表达式
	/// \detailes [NAME]&GSBASELINE&[NAME]
	eLabelExpressionHypertext,

	/// \brief lua脚本表达式
	/// \details			lua 脚本 demo 
	/// \details		function strstr(value, value2)
	/// \details       		value2 = string.format("%d", value2)
	/// \details  			result = value..'--'..value2
	/// \details  	   		return result;
	/// \details  		end
	/// \details  		LabelValue = strstr([GEOGLOBE_P], [ADCODE93])
	eLabelExpressionLua,

	/// \brief python脚本表达式
	/// \details			python 脚本 demo 
	/// \details		def strstr(value, value2) :
	/// \details  			ret = str(value) + '-' + str(value2)
	/// \details  			return ret
	/// \details		LabelValue = strstr([GEOGLOBE_P], [ADCODE93])
	eLabelExpressionPython,
};
```
首先需要调用函数：
/// \brief 设置LabelField的文本类型
/// \param type 类型。
void GsLabelProperty::LabelExpressionType(GsLabelExpressionType type)
设置文本类型。
然后再设置文本内容

//要标注的字段名称
void GsLabelProperty::LabelField(const char* strField)

具体Demo见枚举中的注释信息





