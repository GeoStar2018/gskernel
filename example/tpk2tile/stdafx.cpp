// stdafx.cpp : ֻ������׼�����ļ���Դ�ļ�
// tpk2tile.pch ����ΪԤ����ͷ
// stdafx.obj ������Ԥ����������Ϣ

#include "stdafx.h"

// TODO: �� STDAFX.H �������κ�����ĸ���ͷ�ļ���
//�������ڴ��ļ�������

#ifdef _DEBUG
#pragma comment(lib,"gsgeodatabased.lib") 
#pragma comment(lib,"gsgeometryd.lib") 
#pragma comment(lib,"gsutilityd.lib") 
#pragma comment(lib,"gsspatialreferenced.lib")  
#else
#pragma comment(lib,"gsgeodatabase.lib") 
#pragma comment(lib,"gsgeometry.lib") 
#pragma comment(lib,"gsutility.lib") 
#pragma comment(lib,"gsspatialreference.lib")  
#endif