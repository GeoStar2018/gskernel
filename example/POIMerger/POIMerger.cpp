// POIMerger.cpp : 定义控制台应用程序的入口点。
//

#include "stdafx.h"
#include <utility.h>
//#include "mbvtstyle.h"
using namespace UTILITY_NAME;

#include "Merger.h"

void PrintHelp()
{
	std::cout << "POIMerger   inputFolder  outputTfgFile   [demtilepath]" << std::endl;

}
int main(int argc, char **argv)
{
	/*GsFile f("E:\\02-Soft\\Z-OpenSource\\mapbox\\style\\streetv9.json");
	KERNEL_NAME::GsMBVTStylePtr ptrStyle = new KERNEL_NAME::GsMBVTStyle(f.ReadAll().c_str());*/

	if (argc < 3)
	{
		PrintHelp();
		return 0;
	}
	GsString strInput = GsUtf8(argv[1]).Str();
	GsString strOutput = GsUtf8(argv[2]).Str();

	GsFile file(strOutput.c_str());
	GsString strLog = file.Parent().FullPath();
	GsDateTime now = GsDateTime::Now();
	std::stringstream ss;
	ss << now.Year() << "-" << now.Month() << "-" << now.Day() << " " << now.Hour() << ":" << now.Minute() << ":" << now.Second() << ".log";
	strLog = GsFileSystem::Combine(strLog.c_str(), ss.str().c_str());

	GsFileCustomLogOutput logFile(strLog.c_str(),true,true);
	GsLogger::Default().CustomOutput(&logFile);
	GsLogger::Default().LogLevel(eLOGALL);
	GsStopWatch watch;
	watch.Start();
	GS_T << "begin to merge poidataset";
	if (argc > 3)
	{
		GsString strDEMTile = GsUtf8(argv[3]).Str();
		GS_T << "dem dataset " << strDEMTile;
		AssignDEMMerger merge(strInput.c_str(), strDEMTile.c_str());

		GS_T << "merge data to " << strOutput;

		merge.Merge(strOutput.c_str());
	}
	else
	{
		Merger merge(strInput.c_str());

		GS_T << "merge data to " << strOutput;
		merge.Merge(strOutput.c_str());
	}
	
	double sec = watch.EscapedSecond();
	if(sec <600)
		GS_T << "merge finished cost " << sec<<" seconds";
	else if (sec <7200)
		GS_T << "merge finished cost " << int(sec / 60) << " minutes";
	else
		GS_T << "merge finished cost " << (sec / 3600) << " hours";

	std::cout << "press any key to quit";
	getchar();
	return 0;
}

