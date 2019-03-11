# GIS内核-读取符号库,把符号绘制成图片 #



#include "stdafx.h"
#include <utility.h>
#include <kernel.h>


using namespace GeoStar;
using namespace GeoStar::Kernel;
using namespace GeoStar::Utility;
using namespace GeoStar::Utility::Data;

#ifdef _WIN32 
#ifdef _DEBUG
#pragma comment(lib,"gsgeodatabased.lib")
#pragma comment(lib,"gssymbold.lib")
#pragma comment(lib,"gsgeometryd.lib")
#pragma comment(lib,"gsutilityd.lib") 
#pragma comment(lib,"gsspatialreferenced.lib")  
#pragma comment(lib,"gsmapd.lib")
#else
#pragma comment(lib,"gsspatialreference.lib") 
#pragma comment(lib,"gsgeodatabase.lib")
#pragma comment(lib,"gssymbol.lib")
#pragma comment(lib,"gsgeometry.lib")
#pragma comment(lib,"gsutility.lib")  
#pragma comment(lib,"gsmap.lib")
#endif

#endif

void SavePng(const char* pathsym)
{


	GsKernel::Initialize();

	int i = 1;
	std::string path = pathsym;// u8"D:\\售后\\li.symx";
	std::string strSrcSymxPath = path;// GsEncoding::ToUtf8(argv[i++]);
	GsSymbolLibrary load(strSrcSymxPath.c_str());

	UTILITY_NAME::GsVector<GsSymbolPtr> pVecs = load.Symbols(eFillSymbol);
	for each (GsSymbolPtr var in pVecs)
	{
		GsMultiFillSymbolPtr ptrFillSymbol = var;
		GsMultiFillSymbolPtr CloneSym = var;// ptrFillSymbol->Clone();
		for (int i = 0; i < CloneSym->Count(); i++) {
			CloneSym->ElementAt(i)->Outline()->Color(CloneSym->ElementAt(i)->FillColor());
			CloneSym->ElementAt(i)->Outline()->Width(0.0);
		}
		int width = 256;
		GsMemoryImageCanvasPtr  ptrCanvas = new GsMemoryImageCanvas(width, width);
	//	GsRect clip(1, 1, width - 2, width - 2);
		//ptrCanvas->Clip(clip);
		//ptrCanvas->Clear(0);
		GsEnvelopePtr pEnv = new GsEnvelope(0, 0, width, width);
		GsDisplayTransformationPtr pDT = new GsDisplayTransformation(pEnv->Envelope(), GsRect(0, 0, width, width));
		
		CloneSym->StartDrawing(ptrCanvas, pDT);
		CloneSym->Draw(pEnv);
		CloneSym->EndDrawing();

		GsString pout = u8"D:\\1\\"+var->Name()+u8".png";

		ptrCanvas->Image()->SavePNG(pout.c_str());
	}
	//GsMultiFillSymbolPtr ptrFillSymbol = load.SymbolByName(u8"基本农田保护区");

}



int main(int argc, char **argv)
{
	SavePng(u8"D:\\售后\\国标1：5万，1：10万符号库.symx");
	//SavePng(u8"D:\\售后\\gui.symx");
	return 0;
}