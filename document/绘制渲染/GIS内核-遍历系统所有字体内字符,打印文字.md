GIS内核-遍历系统所有字体内字符,打印文字
	
	GS_TEST(FontDraw, SimSunDraw, chijing, 20190529)
	{
	
		GsFontCollection Families;
		GsVector<GsFontFamilyPtr> f = Families.Families();
		int a = Families.FamilyCount();
		for (int i = 0; i < a; i++)
		{
			GsFontFamilyPtr onefont = Families.Familiy(i);
			GsString tmp = onefont->FontName().c_str();
	
			std::cout << GsEncoding::ToLocal(onefont->FontName()) << std::endl;
			onefont->Reset();
			GsWString fcode;
			while (onefont->Next(fcode))
			{
				std::wcout << (fcode).c_str() << std::endl;
				std::wcout << WSToUTF8(fcode.c_str()).c_str() << std::endl;
			}
		}
	
		GsAggMemoryImageCanvasPtr pImageCanvas = new GsAggMemoryImageCanvas(1024, 1024);
		pImageCanvas->Clear(GsColor::Transparent);
		GsSolidBrushPtr brush= new GsSolidBrush(GsColor(0, 0, 0));
		GsRectF rect(0, 0, 30, 30);
	
		
		GsFontFamilyPtr pFamilly = GsFontFamily::CreateFontFamily(GsUtf8("SimSun"), eFontStyleRegular);
		if (!pFamilly)
			return;
	
		GsString name = pFamilly->FontName();
		GsStringFormat format;
		format.Font(name);
		format.FontSize(12);
		format.FontStyle(eFontStyleRegular);
		pFamilly->Reset();
		GsWString fcode;
		int  r = 0, c = 0;
		int wstep = 32, hstep = 32;
		int sumw = 0, sumh = 0;
		while (pFamilly->Next(fcode))
		{
			GsString text = WSToUTF8(fcode);
			if (GsString::IsNullOrEmpty(text))
			{
				continue;
			}
			pImageCanvas->MeasureString(text.c_str(), text.size(),rect, &format);
			int w = rect.Width();
			int h = rect.Height();
			if (w <= 0 || h <= 0)
				continue;
	
			sumw += rect.Width();
			sumh += rect.Height();
	
			if (sumw > 1024)
			{
				r++;
				sumw = 0;
				c = 0;
			}
			else
				c++;
	
			rect.Left = c * hstep;
			rect.Top = r * wstep;
			rect.Bottom = rect.Top + hstep;
			rect.Right = rect.Left + wstep;
			if (rect.Left > 1024 || rect.Top > 1024)
				continue;
			pImageCanvas->DrawString(text.c_str(), text.size(), brush, rect, &format);
	
		}
	
		}