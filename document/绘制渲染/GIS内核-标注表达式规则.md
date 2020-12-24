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





Lua例子

```lua
	
	Lua
	function max([Value]) 
		if (value > 0) then 
			result = value - 273.15; 
		else 
			result = value; 
		end 
		return result; 
	end 
	LabelValue =string.format("%f",max(value))
	
```

