#文字外接矩形框计算方法

某些情况下，需要获取文字外接矩形框，此时可使用*Canvas类计算。例如，使用GsCairoMemoryImageCanvas计算，代码如下：

	//画布尺寸为256 * 256
	GsCairoMemoryImageCanvas canvers(256, 256);
	//创建字体对象
	GsStringFormatPtr format = canvers.CreateStringFormat();
	//设置字体风格，可根据实际需要制定
	GsFontStyle eStyle = eFontStyleRegular;
	eStyle |= eFontStyleBold;
	format->FontStyle(eStyle);
	//设置字体大小，单位磅
	format->FontSize(9);
	//设置字体
	format->Font(GsUtf8("宋体").str());
	//获取字体外接矩形rect
	GsRectF rect(0, 0, 1, 1);
	canvers.MeasureString("TEST",strlen("TEST"), rect, format);
	