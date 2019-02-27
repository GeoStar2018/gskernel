// tpk2tile.cpp : �������̨Ӧ�ó������ڵ㡣
//

#include "stdafx.h"
#include <iostream>
#include <filesystem.h>
#include <crthelp.h>
#include <geodatabase.h>
#include <esrigeodatabase.h>

#include <timer.h>
using namespace UTILITY_NAME;
using namespace KERNEL_NAME;

void Translate(GsTileClass* ptrESRITile, GsTileClass* ptrTile ,const char* name)
{ 
	std::cout << "prepare to translate tile from tpk to tile name="<< name << std::endl;
	long long nTotal = ptrESRITile->TileCount();
	long long nNow = 0;
	GsTilePtr ptrTileOutput = ptrTile->CreateTile();
	GsTileCursorPtr ptrCusor = ptrESRITile->Search();
	GsTilePtr ptrTileInput = ptrCusor->Next();
	if (!ptrTileInput)
	{
		std::cout << "tpk tile class is empty" << std::endl;
		return ;
	}

	GsStopWatch watch;
	watch.Start();
	int nCommit = 0;
	GeoStar::Utility::Data::GsTransactionPtr pTrans = ptrTile->Transaction();
	do
	{
		ptrTileOutput->OID(ptrTileInput->OID());
		ptrTileOutput->TileType(ptrTileInput->TileType());
		ptrTileOutput->TileData(ptrTileInput->TileDataPtr(), ptrTileInput->TileDataLength());
		if (nCommit == 0 && pTrans.p != nullptr)
			pTrans->StartTransaction();
		nNow++;
		nCommit++;
		ptrTileOutput->Store();
		if (nCommit > 10000 && pTrans.p != nullptr)
		{
			nCommit = 0;
			pTrans->CommitTransaction();

			std::cout << nNow << "/" << nTotal << "  " << (int)(100.0 * nNow / nTotal) << "%" << std::endl;
		}

	} while (ptrCusor->Next(ptrTileInput));

	if (nCommit > 0 && pTrans.p != nullptr)
	{
		nCommit = 0;
		pTrans->CommitTransaction();
	}

	std::cout << "translate finished total tile:"<< nNow<<" time cost:" << watch.EscapedSecond() << "second" << std::endl;

}

int main(int argc, char **argv)
{
	if (argc < 3)
	{
		std::cout << "tpk2tile  input.tpk output.tile" << std::endl;
		std::cout << "tpk2tile  inputTPKfolder  outputfolder" << std::endl;
		return 0;
	}
	GsString strTPK = GsUtf8(argv[1]).Str();
	GsString strTile = GsUtf8(argv[2]).Str();
	
	GsFile tpkFile(strTPK.c_str());
	GsString strExt = tpkFile.Extension();

	GsString strTPKFolder;
	GsVector<GsString> vecDS;
	//�����չ��Ϊtpk����ô��ʾֱ���������tpk���ļ�
	if (GsCRT::_stricmp(strExt.c_str(), "tpk") == 0)
	{
		//����ļ���������Ϊ�����·������װ����·��
		if (!tpkFile.Exists())
			tpkFile = GsFileSystem::MakeExistFullPath(strTPK.c_str());

		//���·����Ȼ����������Ϊ����·����Ч
		if (!tpkFile.Exists())
		{
			std::cout << "invalid input tpk file" << std::endl;
			return 0;
		}
		strTPKFolder = tpkFile.Parent().FullPath();
		vecDS.push_back(tpkFile.Name(false));
	}
	else //������������ļ�����Ϊ�������Ŀ¼
	{
		GsDir dir(strTPK.c_str());
		if(!dir.Exists())
			dir = GsFileSystem::MakeExistFullPath(strTPK.c_str());
		
		//���Ŀ¼������������ΪĿ¼��Ч
		if (!dir.Exists())
		{
			std::cout << "invalid input tpk folder" << std::endl;
			return 0;
		}
		strTPKFolder = strTPK;
	}
	GsString strOutputFolder;
	GsString strOutputName;
	//��������������ݼ�����Ϊת��һ��
	if (!vecDS.empty())
	{
		if (!GsFileSystem::IsPathRooted(strTile.c_str()))
			strTile = GsFileSystem::MakeExistFullPath(strTile.c_str());

		GsFile tileFile(strTile.c_str());
		if (tileFile.Exists())
			tileFile.Delete();
	
		strExt = tileFile.Extension();
		if (GsCRT::_stricmp(strExt.c_str(), "tile") == 0)
		{
			strOutputFolder = tileFile.Parent().FullPath();
			strOutputName = tileFile.Name(false);
		}
		else
		{
			strOutputFolder = strTile;
			GsDir dir(strTile.c_str());
			if (!dir.Exists())
				dir.Create();
		}
	}
	else //�������·����������Ŀ¼
	{
		if(!GsFileSystem::IsPathRooted(strTile.c_str()))
			strTile = GsFileSystem::MakeExistFullPath(strTile.c_str());
		 
		strOutputFolder = strTile;
		GsDir dir(strTile.c_str());
		if (!dir.Exists())
			dir.Create();
	}

	GsConnectProperty conn;
	conn.Server = strTPKFolder;
	GsGeoDatabasePtr ptrESRIGDB = GsESRIFileGeoDatabaseFactory().Open(conn);
	if (!ptrESRIGDB)
	{
		std::cout << "open esri database faild" << std::endl;
		return 0;
	}
	if (vecDS.empty())
		ptrESRIGDB->DataRoomNames(eTileClass, vecDS);
	if (vecDS.empty())
	{
		std::cout << "there is no tpk dataset exists" << std::endl;
		return 0;
	}
	
	conn.Server = strOutputFolder;
	GsGeoDatabasePtr ptrTileGDB = GsSqliteGeoDatabaseFactory().Open(conn);
	if (!ptrTileGDB)
	{
		std::cout << "open sqlite database faild" << std::endl;
		return 0;
	}
	GsStopWatch watch;
	watch.Start();

	GsVector<GsString>::iterator it = vecDS.begin();
	for (; it != vecDS.end(); it++)
	{
		std::cout << "translate "<<it->c_str() << std::endl;
		GsTileClassPtr ptrESRITile = ptrESRIGDB->OpenTileClass(it->c_str());
		if (!ptrESRITile)
		{
			std::cout << "open esri tpk faild" << std::endl;
			continue;
		}
		GsString strOutName = strOutputName.empty() ? (*it) : strOutputName;
		strOutName = GsStringHelp::Replace(strOutName.c_str(), ".tpk", "");

		GsTileClassPtr ptrTile = ptrTileGDB->OpenTileClass(strOutName.c_str());
		if (ptrTile)
		{
			std::cout << "output dataset exist delete it first" << std::endl;
			ptrTile->Delete();
		}

		ptrTile = ptrTileGDB->CreateTileClass(strOutName.c_str(), ptrESRITile->SpatialReference(), ptrESRITile->Pyramid(),
			ptrESRITile->TileColumnInfo());
		if (!ptrTile)
		{
			std::cout << "create output tile faild " << std::endl;
			continue;
		}
		Translate(ptrESRITile, ptrTile,it->c_str());
	}


	std::cout << "translate finished time cost:" << watch.EscapedSecond() << "second" << std::endl;
    return 0;
}

