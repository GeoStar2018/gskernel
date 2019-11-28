GIS内核 -GsConfig 读取配置

```c++
GsString fcsFolder = this->MakeInputFolder(GsEncoding::ToUtf8("rasteranalysis"));
GsString ggdFile = GsFileSystem::Combine(GsUtf8(fcsFolder.c_str()).Str().c_str(), "DISK201909190845.ggd");
GsConfig mConfig(ggdFile);
if (!mConfig.Good())
	return;
GsString Name = mConfig.Child("ggd").Child("Metadata").Child("Name").StringValue("");
GsString eUnit = mConfig.Child("ggd").Child("Metadata").Child("Unit").StringValue("D");
double m_dblTransformation[6] = {};
memset(m_dblTransformation, 0, sizeof(double) * 6);
m_dblTransformation[0] = mConfig.Child("ggd").Child("Metadata").Child("Xo").FloatValue(55.007045578917086);
m_dblTransformation[3] = mConfig.Child("ggd").Child("Metadata").Child("Yo").FloatValue(60.003258640002308);
m_dblTransformation[1] = mConfig.Child("ggd").Child("Metadata").Child("DX").FloatValue(0.040010681113390865);
m_dblTransformation[5] = mConfig.Child("ggd").Child("Metadata").Child("DY").FloatValue(0.040010681113390865);
m_dblTransformation[2] = 0;
m_dblTransformation[4] = 0;
int m_Width = mConfig.Child("ggd").Child("Metadata").Child("Width").IntValue(2001);
int m_Height = mConfig.Child("ggd").Child("Metadata").Child("Height").IntValue(2376);
GsString m_strWKT = mConfig.Child("ggd").Child("Metadata").Child("SpatialReference").StringValue("4326").c_str();
int m_BandCount = mConfig.Child("ggd").Child("Metadata").Child("BandCount").IntValue(2);
GsString m_DatasetDataFunc = 	GsString fcsFolder = this->MakeInputFolder(GsEncoding::ToUtf8("rasteranalysis"));
	GsString ggdFile = GsFileSystem::Combine(GsUtf8(fcsFolder.c_str()).Str().c_str(), "DISK201909190845.ggd");
	GsConfig mConfig(ggdFile);
	if (!mConfig.Good())
		return;
	GsString Name = mConfig.Child("ggd").Child("Metadata").Child("Name").StringValue("");
	GsString eUnit = mConfig.Child("ggd").Child("Metadata").Child("Unit").StringValue("D");
	double m_dblTransformation[6] = {};
	memset(m_dblTransformation, 0, sizeof(double) * 6);
	m_dblTransformation[0] = mConfig.Child("ggd").Child("Metadata").Child("Xo").FloatValue(55.007045578917086);
	m_dblTransformation[3] = mConfig.Child("ggd").Child("Metadata").Child("Yo").FloatValue(60.003258640002308);
	m_dblTransformation[1] = mConfig.Child("ggd").Child("Metadata").Child("DX").FloatValue(0.040010681113390865);
	m_dblTransformation[5] = mConfig.Child("ggd").Child("Metadata").Child("DY").FloatValue(0.040010681113390865);
	m_dblTransformation[2] = 0;
	m_dblTransformation[4] = 0;
	int m_Width = mConfig.Child("ggd").Child("Metadata").Child("Width").IntValue(2001);
	int m_Height = mConfig.Child("ggd").Child("Metadata").Child("Height").IntValue(2376);
	GsString m_strWKT = mConfig.Child("ggd").Child("Metadata").Child("SpatialReference").StringValue("4326").c_str();
	int m_BandCount = mConfig.Child("ggd").Child("Metadata").Child("BandCount").IntValue(2);
	GsString m_DatasetDataFunc = mConfig.Child("ggd").Child("Metadata").Child("DataFunc").StringValue("");
	int m_RasterDataType = (GsRasterDataType)mConfig.Child("ggd").Child("Metadata").Child("BandDataType").IntValue(1);
	bool m_bColumnTransposition = mConfig.Child("ggd").Child("Metadata").Child("ColumnTransposition").BoolValue(false);
	for (int i = 0; i < m_BandCount; i++)
	{
		GsString BandName = "Band";
		BandName += GsStringHelp::Format(10, "%d", i);;
		GsString Band1Path = mConfig.Child("ggd").Child(BandName.c_str()).Child("BandFile").StringValue("");
		int sindex = mConfig.Child("ggd").Child(BandName.c_str()).Child("DataStartIndex").IntValue(0);
		int eindex = mConfig.Child("ggd").Child(BandName.c_str()).Child("DataEndIndex").IntValue(0);
		GsString strfunc = mConfig.Child("ggd").Child(BandName.c_str()).Child("DataFunc").StringValue("");
	}mConfig.Child("ggd").Child("Metadata").Child("DataFunc").StringValue("");
int m_RasterDataType = (GsRasterDataType)mConfig.Child("ggd").Child("Metadata").Child("BandDataType").IntValue(1);
bool m_bColumnTransposition = mConfig.Child("ggd").Child("Metadata").Child("ColumnTransposition").BoolValue(false);
for (int i = 0; i < m_BandCount; i++)
{
	GsString BandName = "Band" + i;
	GsString Band1Path = mConfig.Child("ggd").Child(BandName).Child("BandFile").StringValue("");
	int sindex = mConfig.Child("ggd").Child(BandName).Child("DataStartIndex").IntValue(0);
	int eindex = mConfig.Child("ggd").Child(BandName).Child("DataEndIndex").IntValue(0);
	GsString strfunc = mConfig.Child("ggd").Child(BandName).Child("DataFunc").StringValue("");
}
```