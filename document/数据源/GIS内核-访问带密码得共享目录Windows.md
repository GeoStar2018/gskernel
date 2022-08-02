##访问带密码的共享目录


	GsKernel::Initialize();
	GsPCGeoDatabase::Initialize();
	GsNoSQLDatabaseSource::Initialize();
	KERNEL_NAME::GsFileSystemDataSourceFactoryPtr  fac = new GsFileSystemDataSourceFactory();
	GsConnectProperty conn;
	conn.Server = u8"\\\\192.168.31.40\\技术规划周报";
	conn.User = "chijing";
	conn.Password = "1232456";
	conn.Database = u8"技术规划";

	auto fs = fac->OpenLocal(conn);
	auto gh =  fs->RootFolder()->EnumerateFile();