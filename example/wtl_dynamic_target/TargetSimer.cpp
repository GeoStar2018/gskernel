#include "stdafx.h"
#include "TargetSimer.h"
#include <atlstr.h>
using namespace GeoStar::Kernel;
using namespace GeoStar::Utility;
template<class T>
T Random(T minval, T maxval)
{
	int n = rand();
	return minval + 1.0 * (maxval - minval) * n / RAND_MAX;
}

CTarget::CTarget(const char* name)
{
	m_Name = name;
	m_nStartTime = 0;
	m_Angle = 0;
	m_EscapeTime = 0;
	m_dblDistance = 0;
	m_nWarning = 0;

	m_Parameters[eOil] = Random(0, 100);
	m_Parameters[eSpeed] = Random(0, 100);
	m_Parameters[eTemp] = Random(0, 100);
	m_UpdateCounter = 0;
}

const char* CTarget::Name()
{
	return m_Name.c_str();
}
void CTarget::GeneratorNext()
{
	m_nStartTime = GetTickCount();
	//持续10秒到20秒转换一下方向
	m_EscapeTime = Random(10000, 20000);
	//旋转的角度
	m_Angle = GeoStar::Utility::GsMath::ToRadian(Random(0, 360));
	m_Base = m_Position;
	m_dblDistance = Random(0.001, 0.1);
}
GeoStar::Kernel::GsRawPoint CTarget::GeneratorNextPoint()
{
	int t = GetTickCount();
	
	//经过多少秒。
	double sec = (t - m_nStartTime) / 1000;
	double dblTotal = m_dblDistance * sec;
	
	return GeoStar::Kernel::GsRawPoint(m_Base.X + dblTotal * cos(m_Angle),
		m_Base.Y + dblTotal * sin(m_Angle));
}
void CTarget::UpdateSim()
{
	if (m_EscapeTime <= 0 || m_dblDistance <= 0)
		GeneratorNext();
	else
	{
		int t = GetTickCount();
		if(t  - m_nStartTime > m_EscapeTime)
			GeneratorNext();
	}
	//产生下一个位置。
	Position(GeneratorNextPoint());
}

void CTarget::Position(const GeoStar::Kernel::GsRawPoint& pt)
{
	GS_LOCK_IT(this);
	m_Position = pt;
	m_UpdateCounter ++; 
}
int CTarget::UpdateCounter()
{
	return m_UpdateCounter + m_nWarning;
}
bool CTarget::IsWarning(ParameterName name, int val)
{
	switch (name)
	{
	case eOil:
		return val < 20;
	case eSpeed:
		return val > 60;
	case eTemp:
		return val > 80;
		break;
	}
	return false;
}
int CTarget::Parameter(ParameterName name)
{
	GS_RLOCK_IT(this);
	return m_Parameters[name];

}
void CTarget::Parameter(ParameterName name, int now)
{
	GS_LOCK_IT(this);
	int &ori = m_Parameters[name];
	if (ori == now)
		return;
	bool bW = IsWarning(name, ori);
	ori = now;
	//如果之前不是警告现在警告了则+1
	if (!bW && IsWarning(name, now))
		m_nWarning++;
	//如果之前是警告，而现在不是警告则-1
	else if (bW && !IsWarning(name, now))
		m_nWarning--;
	
	//如果出现了警告则开始计时。
	if (m_nWarning == 1)
		m_AnimationStart = GetTickCount();

}
void CTarget::Draw(GeoStar::Kernel::GsDisplay* disp)
{
	 
	GS_LOCK_IT(this);

	GsCanvasPtr ptrCanvas = disp->Canvas();
	GeoStar::Utility::GsPTF pt = disp->DisplayTransformation()->FromMap(m_Position);
	int s = 5;
	GeoStar::Kernel::GsBrushPtr ptrBrush = disp->Canvas()->CreateSolidBrush(GeoStar::Kernel::GsColor::Red);
	disp->Canvas()->FillEllipse(GsRect(pt.X - s, pt.Y - s, s*2, s*2), ptrBrush);
	
	std::vector<string> vecLabels;
	std::vector<bool> vecWarning;
	std::string str = "名称："; str += m_Name;
	vecLabels.push_back(GsUtf8(str.c_str()).Str());
	vecWarning.push_back(false);

	str = GsStringHelp::Format(1024, "油量：%d", m_Parameters[eOil]);
	vecLabels.push_back(GsUtf8(str.c_str()).Str());
	vecWarning.push_back(IsWarning(eOil, m_Parameters[eOil]));

	str = GsStringHelp::Format(1024, "速度：%d", m_Parameters[eSpeed]);
	vecLabels.push_back(GsUtf8(str.c_str()).Str());
	vecWarning.push_back(IsWarning(eSpeed, m_Parameters[eSpeed]));

	str = GsStringHelp::Format(1024, "温度：%d", m_Parameters[eTemp]);
	vecLabels.push_back(GsUtf8(str.c_str()).Str());
	vecWarning.push_back(IsWarning(eTemp, m_Parameters[eTemp]));
	
	std::stringstream ss;
	std::vector<std::string>::iterator it = vecLabels.begin();
	for (; it != vecLabels.end(); it++)
	{
		if (it != vecLabels.begin())
			ss << std::endl;
		ss << *it;
	}

	GsStringFormatPtr ptrFormat = ptrCanvas->CreateStringFormat();
	ptrFormat->Font(GsUtf8("微软雅黑").Str().c_str());
	ptrFormat->FontSize(10);
	GsRectF layout(0,0,1000,1000);
	ptrCanvas->MeasureString(ss.str().c_str(), ss.str().size(), layout, ptrFormat);
	float w = layout.Width() * 1.2f;
	
	ptrBrush = ptrCanvas->CreateSolidBrush(GeoStar::Kernel::GsColor(GeoStar::Kernel::GsColor::White,128)).p;
	GsRectF billboard(pt.X - w / 2, pt.Y - layout.Height() - s, w, layout.Height());

	ptrCanvas->FillRectangle(billboard, ptrBrush);
	GsPenPtr ptrPen = ptrCanvas->CreatePen(GsColor::Green, 1);
	ptrCanvas->DrawRectangle(billboard, ptrPen);
	int t = GetTickCount();
	int nCount = ((t - m_AnimationStart) / 500) % 2;
	ptrBrush = ptrCanvas->CreateSolidBrush(GsColor::Blue).p;
	GsBrushPtr ptrBrushWarning = ptrCanvas->CreateSolidBrush(GsColor::Red).p;
	//逐条绘制
	float h = layout.Height() / 4;
	int x = billboard.Left;
	int y = billboard.Top;
	for (int i = 0; i < vecLabels.size(); i++)
	{
		if(vecWarning[i] && nCount ==0)
			ptrCanvas->DrawString(vecLabels[i].c_str(), vecLabels[i].length(), ptrBrushWarning, GsPT(x, y), ptrFormat);
		else
			ptrCanvas->DrawString(vecLabels[i].c_str(), vecLabels[i].length(), ptrBrush, GsPT(x, y), ptrFormat);

		y += h;
	}
	/*
	if (m_nWarning == 0 || nCount)
		ptrCanvas->DrawString(strUtf8.c_str(), strUtf8.length(), ptrBrush, billboard.Location(), ptrFormat);
	else
	{
		//逐条绘制
		float h = layout.Height() / 4;
		int x = billboard.Left;
		int y = billboard.Top;
		//绘制名称
		strUtf8 = GsUtf8(strName.GetBuffer()).Str();
		ptrCanvas->DrawString(strUtf8.c_str(), strUtf8.length(), ptrBrush, GsPT(x,y), ptrFormat);
		y += h;

		GsBrushPtr ptrBrushWarning = ptrCanvas->CreateSolidBrush(GsColor::Red).p;
		int nVal = m_Parameters[eOil];

		strUtf8 = GsUtf8(strOil.GetBuffer()).Str();
		if(IsWarning(eOil,nVal))
			ptrCanvas->DrawString(strUtf8.c_str(), strUtf8.length(), ptrBrushWarning, GsPT(x, y), ptrFormat);
		else
			ptrCanvas->DrawString(strUtf8.c_str(), strUtf8.length(), ptrBrush, GsPT(x, y), ptrFormat);
		y += h;

		nVal = m_Parameters[eSpeed];
		strUtf8 = GsUtf8(strSpeed.GetBuffer()).Str();
		if (IsWarning(eSpeed, nVal))
			ptrCanvas->DrawString(strUtf8.c_str(), strUtf8.length(), ptrBrushWarning, GsPT(x, y), ptrFormat);
		else
			ptrCanvas->DrawString(strUtf8.c_str(), strUtf8.length(), ptrBrush, GsPT(x, y), ptrFormat);

		y += h;

		nVal = m_Parameters[eTemp];
		strUtf8 = GsUtf8(strTemp.GetBuffer()).Str();
		if (IsWarning(eTemp, nVal))
			ptrCanvas->DrawString(strUtf8.c_str(), strUtf8.length(), ptrBrushWarning, GsPT(x, y), ptrFormat);
		else
			ptrCanvas->DrawString(strUtf8.c_str(), strUtf8.length(), ptrBrush, GsPT(x, y), ptrFormat);

	}
	*/
	m_UpdateCounter = 0;


}
int CTarget::Warning()
{
	return m_nWarning;

}

CTargetSimer::CTargetSimer()
{
	srand(time(NULL));
}


CTargetSimer::~CTargetSimer()
{
}
void CTargetSimer::EndSim()
{
	GS_LOCK_IT(this);
	m_vecTarget.clear();

}
GeoStar::Kernel::GsRawPoint RandomPoint(const GeoStar::Kernel::GsBox& box)
{
	return GeoStar::Kernel::GsRawPoint(Random(box.XMin, box.XMax), Random(box.YMin, box.YMax));
}
void CTargetSimer::CreateTarget(int nCount)
{
	GS_LOCK_IT(this);
	m_vecTarget.clear();
	GeoStar::Kernel::GsBox box = Extent();

	for (int i = 0; i < nCount; i++)
	{
		std::stringstream ss;
		ss << "目标" << (i + 1);
		CTargetPtr ptrTarget = std::make_shared<CTarget>(ss.str().c_str());
		ptrTarget->Position(RandomPoint(box));
		m_vecTarget.push_back(ptrTarget);
	}
}
CTargetPtr CTargetSimer::Target(int i)
{
	if (i < 0)
		return 0;

	GS_RLOCK_IT(this);
	if (i >= m_vecTarget.size())
		return 0;
	return m_vecTarget[i];

}
int CTargetSimer::Count()
{
	GS_RLOCK_IT(this);
	return m_vecTarget.size();
}
