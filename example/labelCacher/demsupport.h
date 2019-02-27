#pragma once

#include <kernel.h>

template <typename T>
class CDEMTile
{
	const T* m_pHead;

	KERNEL_NAME::GsBox m_Env;
	double m_dblRes;
	long m_nSamplesPerTile;
	//������Чֵ
	float m_fInvalidDem;
public:
	CDEMTile(void)
	{

	}
	~CDEMTile(void)
	{

	}

	void Attach(const T* pHead, const KERNEL_NAME::GsBox& env, long nSample, float fInvalidDem)
	{
		m_pHead = pHead;
		m_Env = env;
		m_dblRes = m_Env.Width() / (nSample - 1);
		m_nSamplesPerTile = nSample;
		m_fInvalidDem = fInvalidDem;
	}
	double Elevation(double dblX, double dblY)
	{
		if (!m_Env.IsContain(dblX, dblY))
			return 0.0;

		// Modify by Justin
		if (m_pHead == NULL)
			return 0.0f;

		//�к��С�
		register int row = (m_Env.YMax - dblY) / m_dblRes;
		register int  col = (dblX - m_Env.XMin) / m_dblRes;
		register double dblScaleX = 1 - (dblX - (m_Env.XMin + m_dblRes * col)) / m_dblRes;
		register double dblScaleY = 1 - ((m_Env.YMax - m_dblRes * row) - dblY) / m_dblRes;
		if (row == (m_nSamplesPerTile - 1))
		{
			row--;
			dblScaleY = 0;
		}
		if (col == (m_nSamplesPerTile - 1))
		{
			col--;
			dblScaleX = 0;
		}

		register double dblWest = 0, dblEast = 0;

		/*
		*-*		*-*
		*-*
		���β�ֵ��Ҫ���ĸ�����в�ֵ����һ�β�ֵ�ֱ�Ե�һ�к͵ڶ��н��в�ֵ���ڶ��β�ֵ�Ե�һ�εĽ�����в�ֵ��
		���β�ֵ�Ĺ������£��������ֵ����Ч����ȡֵΪ��Чֵ����0�������һ����Ч������ȡ��Чֵ��
		�������ֵ����Ч�����в�ֵ
		*/

		//��һ�� �������ֵ����Ч����ȡ��Чֵ��
		//����и�ֵ��Ч��ȡ����һ��ֵ���������Ч�����ֵ
		if (abs((T)m_fInvalidDem - m_pHead[row * m_nSamplesPerTile + col]) <= FLT_EPSILON)
		{
			//����-���� ��Чֵ
			if (abs((T)m_fInvalidDem - m_pHead[(row + 1) * m_nSamplesPerTile + col]) <= FLT_EPSILON)
				dblWest = m_fInvalidDem;
			else
				dblWest = m_pHead[(row + 1) * m_nSamplesPerTile + col];//������Чֵ -������Ч
		}
		else
		{
			if (abs((T)m_fInvalidDem - m_pHead[(row + 1) * m_nSamplesPerTile + col]) <= FLT_EPSILON)
				dblWest = m_pHead[row * m_nSamplesPerTile + col];//������Ч -������Ч
			else
				dblWest = m_pHead[row * m_nSamplesPerTile + col] * dblScaleY + m_pHead[(row + 1) * m_nSamplesPerTile + col] * (1.0 - dblScaleY);
		}

		//�ڶ���
		//����и�ֵ��Ч��ȡ����һ��ֵ���������Ч�����ֵ
		if (abs(((T)m_fInvalidDem - m_pHead[row * m_nSamplesPerTile + col + 1])) <= FLT_EPSILON)
		{
			if (abs((T)m_fInvalidDem - m_pHead[(row + 1) * m_nSamplesPerTile + col + 1]) <= FLT_EPSILON)	//������Ч ������Ч
				dblEast = m_fInvalidDem;
			else
				dblEast = m_pHead[(row + 1) * m_nSamplesPerTile + col + 1];//������Ч��������Ч
		}
		else
		{
			if (abs((T)(m_fInvalidDem)-m_pHead[(row + 1) * m_nSamplesPerTile + col + 1]) <= FLT_EPSILON)//������Ч��������Ч
				dblEast = m_pHead[row * m_nSamplesPerTile + col + 1];
			else
				dblEast = m_pHead[row * m_nSamplesPerTile + col + 1] * dblScaleY + m_pHead[(row + 1) * m_nSamplesPerTile + col + 1] * (1.0 - dblScaleY);
		}

		//�ж� ǰ���� ��ֵ�����
		//�����������Ч ��ֵ�����Ч(0.0 ��)�����һ����Ч,ȡ�����Чֵ��
		//�������Ч��������������ֵ��ֵ
		if (fabs(m_fInvalidDem - (float)dblWest) <= FLT_EPSILON)
			return (fabs(m_fInvalidDem - (float)dblEast) <= FLT_EPSILON) ? 0.0 : dblEast;

		return (fabs(m_fInvalidDem - (float)dblEast) <= FLT_EPSILON) ? dblWest : dblWest * dblScaleX + dblEast * (1.0 - dblScaleX);

	}
};

struct DEMTile
{
	UTILITY_NAME::GsQuadKey Key;
	KERNEL_NAME::GsBox Extent;
	UTILITY_NAME::GsGrowByteBuffer Data;

	CDEMTile<short> m_ShortDEM;
	CDEMTile<float> m_FloatDEM;

	bool m_bShortType;
	void Bind(bool bShort, int nSamples, double InvalidValue);
	double DEMValue(double x, double y, double z);
};


class DEMSupport
{
	std::map<UTILITY_NAME::GsQuadKey, std::list<DEMTile>::iterator> m_MapKey;
	std::list<DEMTile> m_CacheDEM;
	
	KERNEL_NAME::GsPyramidPtr m_ptrPyramid;
	KERNEL_NAME::GsTileColumnInfo m_Col;
	UTILITY_NAME::GsGrowByteBuffer m_ZipBuffer;
	KERNEL_NAME::GsTilePtr m_ptrDEM;
	KERNEL_NAME::GsTileClassPtr m_ptrDEMTile;

	int m_nSamples;
	double m_InvalidValue;
	bool m_bShortType;
protected:
	//׼��������Ƭ
	DEMTile* EnsureTile(const UTILITY_NAME::GsQuadKey& key);
	bool HasDEM();
	bool AssignDEMValue(int nLevel, KERNEL_NAME::GsRawPoint3D& pt);
public:
	DEMSupport(const char* demTile);
	~DEMSupport();
};

