GIS内核-自动标注输出计算的标注位置

```c++

//通过绘制缓存计算标注的位置
GS_TEST(LayoutFonts, LayoutFonts, chijing, 20220818)
{
			GsFontFamilyPtr m_pFontFamilyPtr = GsFontFamily::CreateFontFamily(u8"C:\\Users\\chijing\\Desktop\\CESI_FS_GB2312.TTF");
		GsString name = m_pFontFamilyPtr->FontName();//CESI_FS_GB2312
		GsStringFormatPtr format = new GsStringFormat();
		format->Font(name);
		format->FontSize(12);
		format->FontStyle(eFontStyleRegular);

		//m_pFontFamilyPtr->Reset();
		GsWString fcode;
		GsGlobalConfig::Instance().Child("Kernel").Child("Canvas").Child("Agg").Child("Antialias").Value(false);
		GsAggMemoryImageCanvasPtr pImageCanvas = new GsAggMemoryImageCanvas(1024, 1024);
		int istepx = 0,istepy = 0;
		pImageCanvas->Clear(GsColor::White);
		GsSolidBrushPtr brush = new GsSolidBrush(GsColor(0, 0, 0));
		while (m_pFontFamilyPtr->Next(fcode))
		{
			GsString text = GsEncoding::ToUtf8(fcode.c_str());
			if (GsString::IsNullOrEmpty(text))
				continue;

			GsRectF rect(0, 0, 30, 30);
			pImageCanvas->MeasureString(text.c_str(), text.size(), rect, format);
			int w = rect.Width();
			int h = rect.Height();
			if (w <= 0 || h <= 0)
				continue;
			istepx += w;
			if (istepx > 1024)
			{
				istepx = 0;
				istepy += h;
			}
			GsRectF rect2(istepx, istepy, w, h);
			pImageCanvas->DrawString(text.c_str(), text.size(), brush, rect2, format);

		}
		pImageCanvas->Image()->SavePNG("D:\\tmp\\a.png");
}
```

