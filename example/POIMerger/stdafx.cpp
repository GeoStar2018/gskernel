// stdafx.cpp : ֻ������׼�����ļ���Դ�ļ�
// POIMerger.pch ����ΪԤ����ͷ
// stdafx.obj ������Ԥ����������Ϣ

#include "stdafx.h"

// TODO: �� STDAFX.H �������κ�����ĸ���ͷ�ļ���
//�������ڴ��ļ�������
#ifdef _DEBUG 
#pragma comment(lib,"gsgeometryd.lib")
#pragma comment(lib,"gsutilityd.lib")  
#pragma comment(lib,"gsspatialreferenced.lib")
#pragma comment(lib,"gssymbold.lib")
#pragma comment(lib,"gsgeodatabased.lib")
#else 
#pragma comment(lib,"gsgeometry.lib")
#pragma comment(lib,"gsutility.lib") 
#pragma comment(lib,"gsspatialreference.lib")
#pragma comment(lib,"gssymbol.lib")
#pragma comment(lib,"gsgeodatabase.lib")
#endif