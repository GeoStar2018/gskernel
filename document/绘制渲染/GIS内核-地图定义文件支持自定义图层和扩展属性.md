GIS内核-地图定义文件支持自定义图层和扩展属性
需求描述 : 
		1: 内核地图定义文件读写组件需要提供用户自行扩展自定义图层(例如继承于GsLayer的某自定义图层)
        1: 内核地图定义文件读写组件需要提供用户自行扩展自定义属性(例如继承于GsFeatureLayer的某自定义图层)

接口委托添加:

		class GS_API GsMapDefine
		{
			//...省略已有接口 主要包括 SaveMap 和 ParserMap
	

			/// \brief  打开自定义图层及属性, 内部会先用已知内核图层对象打开一个图层
			/// \param  const tinyxml2::XMLElement* pLyrNode, 地图定义文件中一个图层节点 
			/// \param  GsLayer* pGsLayer NULL或者内核解析出来的图层,打开一个全新的自定义图层(此时GsLayer* pGsLayer为空),或在已有图层上扩展属性( GsLayer* pGsLayer此时为FeatureLayer,TileLayer等内核定义的图层)
			/// \return 返回一个图层,此图层会加入地图中
			Utility::GsDelegate<GsLayerPtr(const tinyxml2::XMLElement* pLyrNode, GsLayer* pGsLayer)> OnOpenCustomerLayer;
		
			/// \brief  保存自定义图层及属性,内部会先用已知内核图层保存其所有基本信息
			/// \param tinyxml2::XMLElement* pLayersRootOrKnownLayerNode,如果是全新内核不识别的图层,此值为LayersRoot (所有图层的根节点),如果内核已知此实例化对象将给出LayerNode(当前图层)
			/// \param  GsLayer* pGsLayer NULL或者内核解析出来的图层,打开一个全新的自定义图层(此时GsLayer* pGsLayer为空),或在已有图层上扩展属性( GsLayer* pGsLayer此时为FeatureLayer,TileLayer等内核定义的图层)
			/// \return 返回一个图层,此图层会加入地图中
			Utility::GsDelegate<bool(tinyxml2::XMLElement* pLayersRootOrKnownLayerNode,GsLayer *pLayer )> OnSaveCustomLayer;
		}


实现流程:
![](picture/gmapx.png)
 


使用伪代码:

	class TravelLayer:public GsFeatureLayer
	{
	public:
		int RefreshInterval;
		GsString MarkName;
	};
	
	
	class GsGFSLayer : public GsLayer
	{
	public:
		GsGFSLayer() {}
		enum GFSDrawType
		{
			eNone,
			eGrid,
			eWind,
			eThermodynamic,
			Lighting,
		};
		GFSDrawType m_eGFSDrawType;
	};
	
	bool OnSaveLayer(tinyxml2::XMLElement* pNode, GsLayer* pLyr)
	{
		TravelLayer* pTLayer = dynamic_cast<TravelLayer*> (pLyr);
		GsGFSLayer * pGfsLayer = dynamic_cast<GsGFSLayer*> (pLyr);
		if (pTLayer != NULL)
		{
			NewElement(pNode, "MarkName", pTLayer->MarkName);
			NewElement(pNode, "RefreshInterval", pTLayer->RefreshInterval);
		}
		else if (pGfsLayer != NULL)
		{
			tinyxml2::XMLElement pLayerNode = NewElement(pNode, "GFSLayer", "");
			NewElement(pLayerNode, "GFSDrawType", pGfsLayer->m_eGFSDrawType);
		}
	}
	
	GsLayerPtr OnOpenLayer(tinyxml2::XMLElement* pLyrNode, GsLayer* pLyr)
	{
		GsLayerPtr returnLayer;
		const char* strName = pLyrNode->Name();
		if (GsCRT::_stricmp(strName, "LogicLayer") == 0)
		{
			returnLayer = new GsGFSLayer();
		}
	
		//找自己加的扩展属性
		const char* strVal = StringValue(pLyrNode->FirstChildElement("MarkName"), "");
		if (strVal)
		{
			TravelLayer * pTLayer = new TravelLayer();
		}
	
		return returnLayer;
	}
	GsMapPtr m_ptrMap = new GsMap();
	void SaveCustomerLayer()
	{
		GsMapDefine mapDef;
		mapDef.OnSaveCustomLayer.Add(OnSaveLayer);
		mapDef.SaveMap(m_ptrMap, "/usr/tmp/a.GMAPX");
	}
	
	void OpenCustomerLayer()
	{
		GsMapDefine mapDef;
		mapDef.OnOpenCustomerLayer.Add(OnOpenLayer);
		mapDef.ParserMap("/usr/tmp/a.GMAPX", m_ptrMap);
	}
	 

