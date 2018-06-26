#pragma once
#include <memory>
enum ParameterName
{
	eOil,
	eSpeed,
	eTemp,
};
//动态目标模拟
class CTarget:public GeoStar::Utility::GsRWLock
{
	std::map<ParameterName, int> m_Parameters;
	GeoStar::Kernel::GsRawPoint m_Position, m_Base;
	int m_nWarning, m_UpdateCounter;
	int m_nStartTime;
	int m_Angle;
	int m_EscapeTime;
	int m_AnimationStart;
	std::string m_Name;
	double m_dblDistance;
	bool IsWarning(ParameterName name, int val);
	void GeneratorNext();
	GeoStar::Kernel::GsRawPoint GeneratorNextPoint();
	
public:
	CTarget(const char* name);
	const char* Name();

	void Position(const GeoStar::Kernel::GsRawPoint& pt);
	int Parameter(ParameterName name);
	void Parameter(ParameterName name,int n);
	void Draw(GeoStar::Kernel::GsDisplay* disp);
	int Warning();
	void UpdateSim();
	int UpdateCounter();
};
typedef std::shared_ptr<CTarget> CTargetPtr;

class CTargetSimer:public GeoStar::Utility::GsRWLock
{
	std::vector<CTargetPtr> m_vecTarget;
protected:
	virtual GeoStar::Kernel::GsBox Extent() = 0;
public:
	CTargetSimer();
	~CTargetSimer();
	virtual void BeginSim() = 0;
	virtual void EndSim();
	virtual void CreateTarget(int nCount);
	CTargetPtr Target(int i);
	int Count();

};

