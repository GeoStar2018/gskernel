#pragma once

#include "memory"
using namespace std;

template <class T>
class CMatix {
public:
	CMatix(int w, int h)
	{
		m_iWidth = m_iStepSize = w;
		m_iHeight = h;
		int len = m_iWidth * m_iHeight * sizeof(T);
		m_pData = new T[len];
		memset(m_pData, 0, len);
	}

	CMatix* sub(int x, int y, int w, int h, int dx = 1, int dy = 1)
	{
		CMatix* matix = new CMatix;
		matix->m_pData = m_pData;
		matix->m_iWidth = w;
		matix->m_iHeight = h;
		matix->m_iX = x;
		matix->m_iY = y;
		matix->m_iStepSize = m_iWidth;
	}

private:
	CMatix();

private:
	int m_iWidth, m_iHeight, m_iStepSize, m_iX = 0, m_iY = 0;
	shared_ptr<T>	m_pData;
};