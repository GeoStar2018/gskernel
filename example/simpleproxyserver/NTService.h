#pragma once

#include <windows.h>
#include <iostream>
enum  CommandType
{
	eUnknown,
	eInstall,
	eUninstall,
	eRun,
	eHelp,
};

class NTService
{
protected:
	SERVICE_STATUS_HANDLE   m_sshStatusHandle;
	std::string				m_serviceName;
	std::string				m_iniFile;
	SERVICE_STATUS          m_ssStatus;       // current status of the service
public:
	virtual void ServiceRestart() = 0;
	virtual int ServiceStart(bool bNT) = 0;
	virtual void ServiceStop() = 0;
	virtual std::string HelpString();
	virtual std::string Parameter();
	virtual void OnParameter(char param, const char* arg);
	//在控制台中接收到Ctrl+C中断
	virtual bool OnCtrlHandler(DWORD dwCtrlType);


	SERVICE_STATUS_HANDLE& StatusHandle();
	std::string& ServiceName();
	SERVICE_STATUS &Status();
	virtual void Log(const char* log);

protected:
	NTService(const char* defaultServiceName = "NTService");
	

private:
	bool Install(const char* strName, const char* cmd, const char* depend = 0);
	bool UnInstall(const char* str);
	bool Run(const char* strName);
public:
	~NTService(void);

	
	bool Run(int argc,   char* argv[]);
};

