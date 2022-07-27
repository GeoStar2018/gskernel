GIS内核-上传tif文件到Mongodb并用内核获取像素块.md

	```c++
	GS_TEST(GsRasterClass, AddRasterClass2GRIDFS, chijing, 20220322)
	{
		return;
		GsConnectProperty conn;
		conn.Database = "fs";
		conn.DataSourceType = eMongoDB;
		conn.Port = 27017;
		conn.User = "chijing";
		conn.Password = "1";
		conn.Server = "127.0.0.1";
		GsMongoDBGeoDatabaseFactoryPtr ptrFac = new GsMongoDBGeoDatabaseFactory();
		auto f = ptrFac->Open(conn);
		{
			GsFileSystemDataSourceFactoryPtr ptrFiles = new GsFileSystemDataSourceFactory();
			auto  fs = ptrFiles->OpenMongoDBDataRoomCatalog(conn);

			GsVector<GsString> vec;
			fs->EnumDataRoomName(vec);

			GsFileSystemClassPtr pFileSystem;
			{
				auto root = fs->OpenDataRoom(vec[0].c_str());
				if (!root)
				{
					GsStringMap map;
					root = fs->CreateDataRoom(vec[0].c_str(), map);
				}
				pFileSystem = root;
			}
			GsVirtualFolderPtr folder = pFileSystem->RootFolder();
			GsVirtualFileEnumeratorPtr pfiles = folder->EnumerateFile();
			{
				//直接使用文件接口上传tif文件
				GsString strPath = GlobeEnvironment::CurrentFolder;
				strPath = u8"C:/Users/chijing/Downloads";
				GsConnectProperty cp;
				cp.Server = strPath;
				GsFileSystemClassPtr ptrMongoDBClass1 = GsFileSystemDataSourceFactory().OpenLocal(cp);
				GsVirtualFolderPtr locFolder = ptrMongoDBClass1->RootFolder();
				GsVirtualFilePtr locFile = locFolder->SubFile(u8"626aa59759de8f00070b32e8.tif");
				bool res = pFileSystem->CopyTo(locFile, folder);

				GsVirtualFilePtr locFile2 = locFolder->SubFile(u8"626aa58459de8f00070b32e6.tif");
				bool res2 = pFileSystem->CopyTo(locFile2, folder);
			}
		}
	}

	GS_TEST(GsRasterClass, OpenGRIDFSRasterClass, chijing, 20220322)
	{
		return;
		GsConnectProperty conn;
		conn.Database = "fs";
		conn.DataSourceType = eMongoDB;
		conn.Port = 27017;
		conn.User = "chijing";
		conn.Password = "1";
		conn.Server = "127.0.0.1";
		GsMongoDBGeoDatabaseFactoryPtr ptrFac = new GsMongoDBGeoDatabaseFactory();
		auto f = ptrFac->Open(conn);
		{
			GsFileSystemDataSourceFactoryPtr ptrFiles = new GsFileSystemDataSourceFactory();
			auto  fs = ptrFiles->OpenMongoDBDataRoomCatalog(conn);
			
			GsVector<GsString> vec;
			fs->EnumDataRoomName(vec);
		
			GsFileSystemClassPtr pFileSystem;
			{
				auto root = fs->OpenDataRoom(vec[0].c_str());
				if (!root)
				{
					GsStringMap map;
					root = fs->CreateDataRoom(vec[0].c_str(),map);
				}
				pFileSystem = root;
			}
			GsVirtualFolderPtr folder = pFileSystem->RootFolder();
			GsVirtualFileEnumeratorPtr pfiles = folder->EnumerateFile();

			conn.Server = "GRIDFS://127.0.0.1/gfs"; 
			GsFileGeoDatabaseFactoryPtr pfac = new GsFileGeoDatabaseFactory();
			GsGeoDatabasePtr pGDB = pfac->Open(conn);
			GsRasterClassPtr ptrFeaClass = 0;
			if (pGDB) 
			{
				ptrFeaClass = pGDB->OpenRasterClass("626aa59759de8f00070b32e8.tif");
			}
			auto r = ptrFeaClass->ExtentToRange(ptrFeaClass->Extent());
			auto cursor = ptrFeaClass->Search(r);
			GsRaster ras;
			while (cursor->Next(&ras))
			{
				std::cout << ras.DataLength() << std::endl;
			}
		}
	}
	GS_TEST(GsRasterClass, CreateMongoMosaicRasterClassFromGridFS, chijing, 20220322)
	{
		return;
		GsConnectProperty conn;
		conn.Database = "fs";
		conn.DataSourceType = eMongoDB;
		conn.Port = 27017;
		conn.User = "chijing";
		conn.Password = "1";
		conn.Server = "127.0.0.1";

		{
			conn.Server = "GRIDFS://127.0.0.1/gfs";//这里前缀加GRIDFS, 后缀是vec[0]
			GsFileGeoDatabaseFactoryPtr pfac = new GsFileGeoDatabaseFactory();
			GsGeoDatabasePtr pGDB = pfac->Open(conn);
			GsRasterClassPtr ptrFeaClass1 = 0;
			GsRasterClassPtr ptrFeaClass2 = 0;
			if (pGDB)
			{
				ptrFeaClass1 = pGDB->OpenRasterClass("626aa59759de8f00070b32e8.tif");
				ptrFeaClass2 = pGDB->OpenRasterClass("626aa58459de8f00070b32e6.tif");
			}
			GsMongoDBGeoDatabaseFactoryPtr ptrFac = new GsMongoDBGeoDatabaseFactory();
			auto f = ptrFac->Open(conn);
			GsFields fds;
			fds.Fields.emplace_back("asa", Data::GsFieldType::eStringType);
			auto ptrRowclass = f->CreateRowClass("dd", fds);
			//auto ptrRowclass = f->OpenRowClass("dd");

			GsRasterColumnInfo info =  ptrFeaClass1->RasterColumnInfo();
			info.FeatureType = eMosaicRasterFeature;
			auto fm = f->CreateRasterClass("gridfs_cj2", GsRasterCreateableFormat::eVRT, info, ptrFeaClass1->SpatialReference());
			GsMosaicRasterManagerPtr pManage =  fm->ExtensionData();
			pManage->AddRaster(ptrFeaClass1);
			pManage->AddRaster(ptrFeaClass2);
			auto r = fm->ExtentToRange(fm->Extent());
			auto cursor = fm->Search(r);
			GsRaster ras;
			while (cursor->Next(&ras))
			{
				std::cout << ras.DataLength() << std::endl;
			}

		}
	}
	GS_TEST(GsRasterClass,OpenMongoMosaicRasterClassFromGridFS, chijing, 20220322)
	{
		GsFileSystemDataSourceFactoryPtr ptrFiles = new GsFileSystemDataSourceFactory();
		GsConnectProperty conn;
		conn.Database = "fs";
		conn.DataSourceType = eMongoDB;
		conn.Port = 27017;
		conn.User = "chijing";
		conn.Password = "1";
		conn.Server = "127.0.0.1";
		GsMongoDBGeoDatabaseFactoryPtr ptrFac = new GsMongoDBGeoDatabaseFactory();
		auto f = ptrFac->Open(conn);
		GsFields fds;
		fds.Fields.emplace_back("asa", Data::GsFieldType::eStringType);
		//auto ptrRowclass = f->CreateRowClass("dd", fds);
		//auto ptrRowclass = f->OpenRowClass("dd");
		auto fm = f->OpenRasterClass("gridfs_cj2");// GsRasterCreateableFormat::eVRT, info, ptrFeaClass1->SpatialReference());
		GsMosaicRasterManagerPtr pManage = fm->ExtensionData();
		auto r = fm->ExtentToRange(fm->Extent());
		auto cursor = fm->Search(r);
		GsRaster ras;
		while (cursor->Next(&ras))
		{
			std::cout << ras.DataLength() << std::endl;
		}
	}