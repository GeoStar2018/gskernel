GIS内核 -GsConfig 保存配置



```C++
	GsConfig myconfig;
	myconfig.Child("ggd").Child("Metadata").Child("Name").Value("gsgridtest");
	myconfig.Child("ggd").Child("Metadata").Child("Unit").Value("D");
	myconfig.Child("ggd").Child("Metadata").Child("Xo").Value(55.007045578917086);
	myconfig.Child("ggd").Child("Metadata").Child("Yo").Value(60.003258640002308);
	myconfig.Child("ggd").Child("Metadata").Child("DX").Value(0.040010681113390865);
	myconfig.Child("ggd").Child("Metadata").Child("DY").Value(-0.040010681113390865);
	myconfig.Child("ggd").Child("Metadata").Child("Width").Value(2001);
	myconfig.Child("ggd").Child("Metadata").Child("Height").Value(2376);
	myconfig.Child("ggd").Child("Metadata").Child("BandCount").Value(2);
	myconfig.Child("ggd").Child("Metadata").Child("BandDataType").Value(6);
	myconfig.Child("ggd").Child("Metadata").Child("SpatialReference").Value(4326);

	std::string fcsFolder = this->MakeInputFolder(GsEncoding::ToUtf8("rasteranalysis"));
	GsString BandFile = GsFileSystem::Combine(GsUtf8(fcsFolder.c_str()).Str().c_str(), "DISK201909190845.bin");
	GsString ggdFile = GsFileSystem::Combine(GsUtf8(fcsFolder.c_str()).Str().c_str(), "DISK201909190845.ggd");
	myconfig.Child("ggd").Child("Band0").Child("BandFile").Value(BandFile.c_str());
	myconfig.Child("ggd").Child("Band0").Child("DataFunc").Value(u8"value = value - 273.15; ");
	myconfig.Child("ggd").Child("Band1").Child("BandFile").Value(BandFile.c_str());
	myconfig.Child("ggd").Child("Band1").Child("DataFunc").Value(u8"value = (value -65)/2; ");
	myconfig.Save(ggdFile);
```