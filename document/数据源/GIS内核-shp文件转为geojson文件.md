GIS内核-shp转为geojson文件	

	void Shp2GeoJsonFile()
	{
		GsConnectProperty vConn;
		vConn.Server = u8"D:\\testdata\\3dbuilding";
		vConn.DataSourceType = GsDataSourceType::eGDB;
		GsShpGeoDatabaseFactoryPtr ptrFac = new GsShpGeoDatabaseFactory();
		
		GsGeoDatabasePtr ptrshpGeoDb = ptrFac->Open(vConn);
		GsFeatureClassPtr ptrfcsdst = ptrshpGeoDb->OpenFeatureClass("LoD2_02");
		GsFeatureCursorPtr ptrCursor = ptrfcsdst->Search();
		int nFeaCount = ptrfcsdst->FeatureCount();
		int writerCount = 0;
		GsFeaturePtr ptrFea = ptrCursor->Next();
		GsGeoJSONOGCWriter writer;
		GsVector< GsField> fds = ptrfcsdst->Fields().Fields;
		FILE* fd = fopen("D:\\f2.geojson", "wb+");
	
		const char* header = "{\"type\": \"FeatureCollection\",\"features\" : [";
		fwrite(header, sizeof(char), strlen(header), fd);
	
	
		do
		{	
	
			writer.Reset();
			if (!ptrFea)
				break;
			writerCount++;
			for(int i = 2; i< fds.size();i++)
			{
				unsigned char* value =  ptrFea->ValuePtr(i);
				double dblHeight = ptrFea->ValueDouble(i);
				
				switch (fds[i].Type)
				{
				case eErrorType:
					continue;
				case eBoolType:
					writer.Attribute(fds[i].Name, *((bool*)value));
					break;
				case eIntType:
					writer.Attribute(fds[i].Name, *((int*)value));
					break;
				case eUIntType:
					writer.Attribute(fds[i].Name, *((unsigned int*)value));
					break;
				case eInt64Type:
					writer.Attribute(fds[i].Name, *((long long*)value));
					break;
				case eUInt64Type:
					writer.Attribute(fds[i].Name, *((unsigned long long*)value));
					break;
				case eStringType:
					writer.Attribute(fds[i].Name, (const char*)value);
					break;
				case eBlobType:
					//m_vecValues[nColumn].Set(value, nSize);
					break;
				case eFloatType:
					writer.Attribute(fds[i].Name, *((float*)value));
					break;
				case eDoubleType:
					writer.Attribute(fds[i].Name, *((double*)value));
					break;
				case eGeometryType:
					//m_vecValues[nColumn] = (GsRefObject*)value;
					break;
				case eDateType:
					writer.Attribute(fds[i].Name,  GsAny(*((long long*)value), true));
				}
				
			}
			writer.Write(ptrFea->Geometry());
	
			GsString str = writer.GeoJSON().c_str();
			fwrite(str,sizeof(char), str.size(),fd);
	
			if(writerCount<nFeaCount)
				fwrite(",\t\n", sizeof(char), strlen(",\t\n"), fd);
	
		}while (ptrCursor->Next(ptrFea));
	
		const char* endstr = "]}";
		fwrite(endstr, sizeof(char), strlen(endstr), fd);
		fclose(fd);
	}