GIS内核-渲染-分式标注示例

图层启用标注
	void AddLabeltoFeatureLayer(GsLayer* pLyr)
	{
		GsFeatureLayerPtr pFlyr = pLyr;
		if (pFlyr)
		{
	
			GsAdvancedLabelContainerPtr ptr = new GsAdvancedLabelContainer();
			m_GeoSpace2D->Map()->ScreenDisplay()->LabelContainer(ptr);
			int cuont = pFlyr->Renderer()->RenditionCount();
			pFlyr->Renderer()->RenditionMode(eMultiRendition);
	
			GsMultiLabelRenditionPtr ptrMultiLabelsRendtion = new GsMultiLabelRendition();
			GsLabelPropertyPtr ptr2 = 0;
			if (pFlyr->FeatureClass()->Fields().FindField("NAME") >= 0)
				ptr2 = addLabelRendition(pFlyr, 2, QString(u8"<CLR red='255'green='0' blue='0'>&[NAME]&</CLR>&GSNEWLINE&———— &[PINYIN]&GSNEWLINE&[GBCODE]"));
			else
				ptr2 = addLabelRendition(pFlyr, 2, QString(u8"<CLR red='255'green='0' blue='0'>&[GEOGLOBE_P]&</CLR>&GSNEWLINE&————&[PINYIN]&GSNEWLINE&[GBCODE]"));*
			
			ptrMultiLabelsRendtion->LabelProperty(0, ptr2);
	
			ptrMultiLabelsRendtion->Visible(true);
	
			m_GeoSpace2D->Map()->ScreenDisplay()->LabelContainer()->RemoveRepeatLabel(true);
			m_GeoSpace2D->Map()->ScreenDisplay()->LabelContainer()->Enabled(true);
			m_GeoSpace2D->Map()->ScreenDisplay()->LabelContainer()->AutoLabel(true);
		}
	
	}

创建一个图层标注渲染器

	GsLabelPropertyPtr MapView::addLabelRendition(GsFeatureLayer*  m_pLayer,int nCount,QString& strLabelField)
	{
		GsTextSymbolPtr ptrSymbol = new GsTextSymbol();
		ptrSymbol->Color(GsColor::Random());
		//ptrSymbol->Width(30);
		//ptrSymbol->Height(30);
		ptrSymbol->Angle(295);
		ptrSymbol->Size(9);
		ptrSymbol->Font(GsUtf8("宋体"));
		GsTextBackgroundPtr ddd = new GsTextBackground();
		GsSimpleFillSymbolPtr pgfhf = new  GsSimpleFillSymbol(GsColor::YellowGreen);
		GsSimpleLineSymbolPtr ptrLine = new GsSimpleLineSymbol(GsColor::Blue,1);
		pgfhf->Outline(ptrLine);
		ddd->FillSymbol(pgfhf);
		ptrSymbol->CalloutBackground(ddd);
		//ptrSymbol->HorizonAlign(eStringAlignmentFar);
		//ptrSymbol->BackgroundColor(GsColor::Random());
		//ptrSymbol->HorizonAlign(eStringAlignmentCenter);
		//ptrSymbol->VerticalAlign(eStringAlignmentCenter);
		ptrSymbol->HaloColor(GsColor::Random());
		ptrSymbol->HaloSize(3);
		//ptrSymbol->Hollow(true);
		ptrSymbol->VerticalExtra(2);
		ptrSymbol->HorizonExtra(3);
	
		//ptrSymbol->ShadowColor(GsColor::Red);
		//ptrSymbol->ShadowOffsetX(5);
		//ptrSymbol->ShadowOffsetY(5);
		GsLabelRenditionPtr ptrCur;
		GsGeometryType geoType = m_pLayer->FeatureClass()->GeometryType();
		//GsTextSymbolPtr ptrTxtSymbol = GsTextSymbolPtr(new GsTextSymbol());
		float fLabelSize = 8;// m_//点标注pLblStyleSample->fontInfo().pointSize();//标注字体大小
		if (geoType == eGeometryTypePoint)
		{
			GsPointLabelPropertyPtr ptrPointLabelProp = GsPointLabelPropertyPtr(new GsPointLabelProperty());
			ptrPointLabelProp->LabelField(strLabelField.toStdString().data()); //"GBCODE"
			ptrPointLabelProp->PointSpaceByLabel(3);
			ptrPointLabelProp->Symbol(ptrSymbol);
			//ptrPointLabelProp->Symbol()->Size(fLabelSize / 2.500);
			//由于底层多标注避让有待完善，暂时采用此法避免标注重叠
			if (nCount == 0)
				ptrPointLabelProp->PlaceOrder(eBottom, eHighPriority);
			else if (nCount == 1)
				ptrPointLabelProp->PlaceOrder(eRight, eHighPriority);
			else if (nCount == 2)
				ptrPointLabelProp->PlaceOrder(eTop, eHighPriority);
			else if (nCount == 3)
				ptrPointLabelProp->PlaceOrder(eLeft, eHighPriority);
			else if (nCount == 4)
				ptrPointLabelProp->PlaceOrder(eTopLeft, eHighPriority);
			else if (nCount == 5)
				ptrPointLabelProp->PlaceOrder(eTopRight, eHighPriority);
			else if (nCount == 6)
				ptrPointLabelProp->PlaceOrder(eBottomLeft, eHighPriority);
			else
				ptrPointLabelProp->PlaceOrder(eBottomRight, eHighPriority);
			ptrPointLabelProp->DrawPointAndLabel(true);
			ptrPointLabelProp->Symbol(ptrSymbol);
			ptrPointLabelProp->LabelPriority(1);
			ptrPointLabelProp->LabelDistance(10);
			ptrPointLabelProp->RemoveRepeatLabel(true);
			return ptrPointLabelProp.p;
			// ptrCur = new GsLabelRendition(ptrPointLabelProp);//标注渲染器
			//ptrCur->Name(strLabelField.toStdString().c_str());//设置标注类名称
			//ptrCur->Visible(true);
			//nCount++;
	
			
		}
		else if (geoType == eGeometryTypePolyline)//线标注
		{
			GsLineLabelPropertyPtr ptrLineLabelProp = GsLineLabelPropertyPtr(new GsLineLabelProperty());
			ptrLineLabelProp->LabelField(strLabelField.toStdString().c_str());
			GsTextSymbolPtr ptrTxtSymbol = ptrSymbol;
			ptrLineLabelProp->Symbol(ptrTxtSymbol);
		
			ptrLineLabelProp->LineLabelType(eAlongLine);
			ptrLineLabelProp->StartPos(eDefaultPos);
			if (nCount == 0)
				ptrLineLabelProp->LabelPos(eAutoPlace);
			else if (nCount == 1)
			{
				ptrLineLabelProp->LabelPos(eAutoPlace);
				ptrLineLabelProp->StartPos(eStartPos);
			}
			else if (nCount == 2)
			{
				ptrLineLabelProp->LabelPos(eAutoPlace);
				ptrLineLabelProp->StartPos(eEndPos);
			}
			else
				ptrLineLabelProp->LabelPos(eAutoPlace);
			//ptrLineLabelProp->Symbol(ptrSymbol);
			//ptrLineLabelProp->LabelPriority(1);
			
	
			//ptrLineLabelProp->UnionLabelField("NAME");
			ptrLineLabelProp->MultiLabel(false);
			ptrLineLabelProp->RemoveRepeatLabel(true);
			ptrLineLabelProp->LabelDistance(10);
			return ptrLineLabelProp.p;
			//ptrCur->Name(strLabelField.toStdString().c_str());//设置标注类名称
			//ptrCur->Visible(true);
			//nCount++;
	
			
		}
		else if (geoType == eGeometryTypePolygon)//面标注
		{
			GsSurfaceLabelPropertyPtr ptrSurfaceLabelProp = GsSurfaceLabelPropertyPtr(new GsSurfaceLabelProperty());
			ptrSurfaceLabelProp->LabelField(strLabelField.toStdString().data());
			GsTextSymbolPtr ptrTxtSymbol = GsTextSymbolPtr(new GsTextSymbol());
			ptrSurfaceLabelProp->Symbol(ptrTxtSymbol);
			ptrSurfaceLabelProp->Symbol()->Size(fLabelSize / 2.500);
			ptrSurfaceLabelProp->SurfaceLabelType(eNormal);
			if (nCount == 0)
				ptrSurfaceLabelProp->LabelPos(eTopLine);
			else if (nCount == 1)
				ptrSurfaceLabelProp->LabelPos(eBottomLine);
			else if (nCount == 2)
				ptrSurfaceLabelProp->LabelPos(eCenterLine);
			else
				ptrSurfaceLabelProp->LabelPos(eAutoPlace);
			ptrSurfaceLabelProp->Symbol(ptrSymbol);
			ptrSurfaceLabelProp->LabelPriority(nCount);
			GsLineLabelPropertyPtr ptrLineLabelPropTmp = ptrSurfaceLabelProp;
			ptrLineLabelPropTmp->MultiLabel(true);
			ptrSurfaceLabelProp->RemoveRepeatLabel(false);
			//ptrCur = new GsLabelRendition(ptrSurfaceLabelProp);//标注渲染器
			//ptrCur->Name(strLabelField.toStdString().c_str());//设置标注类名称
			//ptrCur->Visible(true);
			//nCount++;
			//ptrCur->
			return ptrSurfaceLabelProp.p;
			
		}
	
	
	
		//m_pLayer->Renderer()->AddRendition(ptrCur);
	
	
		return 0;
	}
