
#include "stdafx.h"
#include "NTService.h"
#include "spgetopt.h"
#include <iostream>
#include <sstream>
NTService* g_Service;

static BOOL WINAPI console_ctrl_handler(DWORD dwCtrlType)
{
	return g_Service->OnCtrlHandler(dwCtrlType) ? TRUE : FALSE;
 
}
NTService::NTService(const char* serviceName)
{
	SetConsoleCtrlHandler(console_ctrl_handler, TRUE);

	m_serviceName = serviceName;

	g_Service = this;

	m_sshStatusHandle = NULL;
	memset(&m_ssStatus,0,sizeof(m_ssStatus));

}


NTService::~NTService(void)
{
}
//在控制台中接收到Ctrl+C中断
bool NTService::OnCtrlHandler(DWORD dwCtrlType)
{

	switch (dwCtrlType)

	{

	case CTRL_C_EVENT: // Ctrl+C 
	case CTRL_BREAK_EVENT: // Ctrl+Break
	case CTRL_CLOSE_EVENT: // Closing the consolewindow
	case CTRL_LOGOFF_EVENT: // User logs off. Passed only to services!
	case CTRL_SHUTDOWN_EVENT: // System is shutting down. Passed only to services!
		ServiceStop();
		return true;
		break;
	}



	// Return TRUE if handled this message,further handler functions won't be called.

	// Return FALSE to pass this message to further handlers until default handler calls ExitProcess().

	return true;

}
void NTService::OnParameter(char param, const char* arg)
{

}
std::string NTService::Parameter()
{
	return "i:u:r:c";
}
std::string NTService::HelpString()
{
	//"i:u:r:c"
	std::stringstream ss;
	ss << "-i:name  install service  as name" << std::endl;
	ss << "-u:name  uninstall service by name" << std::endl;
	ss << "-r:name  run service as name" << std::endl;
	ss << "-c:inifile service config file" << std::endl;
	ss << "-? this help " << std::endl;

	return ss.str();
}
void NTService::Log(const char* log)
{

}
 
SERVICE_STATUS_HANDLE& NTService::StatusHandle()
{
	return m_sshStatusHandle;
}
std::string& NTService::ServiceName()
{
	return m_serviceName;
}
SERVICE_STATUS &NTService::Status()
{
	return m_ssStatus;
}


VOID AddToMessageLog(LPTSTR lpszMsg)
{
  
}

 
BOOL ReportStatusToSCMgr(DWORD dwCurrentState,
                         DWORD dwWin32ExitCode,
                         DWORD dwWaitHint)
{
   static DWORD dwCheckPoint = 1;
   BOOL fResult = TRUE;

    if (dwCurrentState == SERVICE_START_PENDING)
		g_Service->Status().dwControlsAccepted = 0;
    else
		g_Service->Status().dwControlsAccepted = SERVICE_ACCEPT_STOP;

	g_Service->Status().dwCurrentState = dwCurrentState;
	g_Service->Status().dwWin32ExitCode = dwWin32ExitCode;
	g_Service->Status().dwWaitHint = dwWaitHint;

    if ( ( dwCurrentState == SERVICE_RUNNING ) ||
        ( dwCurrentState == SERVICE_STOPPED ) )
		g_Service->Status().dwCheckPoint = 0;
    else
		g_Service->Status().dwCheckPoint = dwCheckPoint++;

	 
    fResult = SetServiceStatus(g_Service->StatusHandle(), &g_Service->Status());
    if (!fResult)
    {
        AddToMessageLog(TEXT("SetServiceStatus"));
    } 
   return fResult;
}

void WINAPI service_ctrl(_In_ DWORD dwCtrlCode) throw()
{
	std::stringstream ss;
	ss << "service_ctrl code=" << dwCtrlCode;
	g_Service->Log(ss.str().c_str());
	 // Handle the requested control code.
   //
   switch (dwCtrlCode)
   {
   // Stop the service.
   //
   // SERVICE_STOP_PENDING should be reported before
   // setting the Stop Event - hServerStopEvent - in
   // ServiceStop().  This avoids a race condition
   // which may result in a 1053 - The Service did not respond...
   // error.
   case SERVICE_CONTROL_STOP:
      ReportStatusToSCMgr(SERVICE_STOP_PENDING, NO_ERROR, 0);
	  g_Service->ServiceStop();
      return;

      // Update the service status.
      //
   case SERVICE_CONTROL_INTERROGATE:
      break;

      // invalid control code
      //
   default:
      break;

   }

   ReportStatusToSCMgr(g_Service->Status().dwCurrentState, NO_ERROR, 0);
} 
void WINAPI service_main(DWORD dwArgc, LPTSTR *lpszArgv)
{
	// register our service control handler:
	//
	
	DWORD                   dwErr = 0;
	g_Service->StatusHandle() = RegisterServiceCtrlHandler(g_Service->ServiceName().c_str(), service_ctrl);
	if (g_Service->StatusHandle())
	{
		// SERVICE_STATUS members that don't change in example
		//
		g_Service->Status().dwServiceType = SERVICE_WIN32_OWN_PROCESS;
		g_Service->Status().dwServiceSpecificExitCode = 0;
		ReportStatusToSCMgr(
                               SERVICE_RUNNING,
                               dwErr,
                               0);
		dwErr = g_Service->ServiceStart(true);
		g_Service->Log("after call ServiceStart");
	}  

	if (dwErr == 1)
	{
		g_Service->ServiceRestart();
	}
	 
   // try to report the stopped status to the service control manager.
   // 
   if (g_Service->StatusHandle())
      (VOID)ReportStatusToSCMgr(
                               SERVICE_STOPPED,
                               dwErr,
                               0);
}
 

bool NTService::Run(const char* strName)
{
	
	SERVICE_TABLE_ENTRY dispatchTable[] =
   {
      {(char*)strName , (LPSERVICE_MAIN_FUNCTION)service_main},
      { NULL, NULL}
   };
   return StartServiceCtrlDispatcher(dispatchTable)?true:false;

}
bool NTService::UnInstall(const char* strName)
{
   SC_HANDLE   schSCManager = OpenSCManager(
							NULL,                   // machine (NULL == local)
							NULL,                   // database (NULL == default)
							SC_MANAGER_CONNECT   // access required
							);
	if ( !schSCManager )
	{
		std::cout<<"Error "<<GetLastError()<<std::endl;
		return false;
	}

   
	SC_HANDLE   schService = OpenService(schSCManager, strName, DELETE | SERVICE_STOP | SERVICE_QUERY_STATUS);
	if (!schService)
	{
		std::cout<<"Error "<<GetLastError()<<std::endl;
		return false;
	}
      

	// try to stop the service
	if ( ControlService( schService, SERVICE_CONTROL_STOP, &g_Service->Status()) )
	{
		std::cout<<"Stopping %s.";
		Sleep( 1000 );

        while ( QueryServiceStatus( schService, &g_Service->Status()) )
        {
            if (g_Service->Status().dwCurrentState == SERVICE_STOP_PENDING )
            {
				std::cout<<".";
                Sleep( 1000 );
            }
            else
                break;
        }

        if (g_Service->Status().dwCurrentState == SERVICE_STOPPED )
			std::cout<<std::endl<<"stopped."<<std::endl;
		else
            std::cout<<std::endl<<"failed to stop."<<std::endl;
         
         // now remove the service
         if ( DeleteService(schService) )
			 std::cout<<strName<<" removed."<<std::endl;
         else
			 std::cout<<strName<<"DeleteService failed -"<<GetLastError()<<std::endl;

         CloseServiceHandle(schService);
	}
	else
	{
		if ( DeleteService(schService) )
			 std::cout<<strName<<" removed."<<std::endl;
         else
			 std::cout<<strName<<"DeleteService failed -"<<GetLastError()<<std::endl;
	}

	CloseServiceHandle(schSCManager);
	
	return true;
}



bool NTService::Install(const char* strName,const char* cmd,const char* depend)
{
	 
  
   
   TCHAR szPath[512];

   if ( GetModuleFileName( NULL, szPath, 512 ) == 0 )
      return false;
   
   SC_HANDLE   schSCManager = OpenSCManager(
                               NULL,                   // machine (NULL == local)
                               NULL,                   // database (NULL == default)
								SC_MANAGER_ALL_ACCESS // access required
                               );
   if ( !schSCManager )
   {
	   std::cout<<"Error "<<GetLastError()<<std::endl;
	   return false;
   }
   std::string strPath = szPath;
   if (cmd && strlen(cmd) > 0)
   {
	   strPath += " ";
	   strPath += cmd;
   }
   SC_HANDLE   schService  = CreateService(
                                schSCManager,               // SCManager database
                                strName,        // name of service
                                strName, // name to display
								SERVICE_ALL_ACCESS	,         // desired access
                                SERVICE_WIN32_OWN_PROCESS,  // service type
                                SERVICE_AUTO_START,       // start type
                                SERVICE_ERROR_NORMAL,       // error control type
								strPath.c_str(),                     // service's binary
                                NULL,                       // no load ordering group
                                NULL,                       // no tag identifier
								depend,       // dependencies
                                NULL,                       // LocalSystem account
                                NULL);                      // no password

	if ( schService )
	{
		SERVICE_FAILURE_ACTIONS sdBuf = { 0 };
		sdBuf.lpRebootMsg = NULL;
		sdBuf.dwResetPeriod = 3600 * 24;
		SC_ACTION action[3];
		action[0].Delay = 60 * 1000;
		action[0].Type = SC_ACTION_RESTART;

		action[1].Delay = 60 * 1000;
		action[1].Type = SC_ACTION_RESTART;
		action[2].Delay = 60 * 1000;
		action[2].Type = SC_ACTION_RESTART;

		sdBuf.cActions = 3;
		sdBuf.lpsaActions = action;
		sdBuf.lpCommand = NULL;

		BOOL B = ChangeServiceConfig2(schService, SERVICE_CONFIG_FAILURE_ACTIONS, &sdBuf);
		if(!B)
			std::cout << "ChangeServiceConfig2 Error " << GetLastError() << std::endl;
		std::cout<<"Install OK"<<std::endl;
		CloseServiceHandle(schService);
	}
	else
		std::cout<<"Error "<<GetLastError()<<std::endl;
	

	CloseServiceHandle(schSCManager);
	return true;
}


bool NTService::Run(int argc,   char* argv[])
{
	bool bServer = false;
	extern char *optarg;
	int c;

	CommandType cmdType = eUnknown;


	std::string strParam = Parameter();

	while ((c = getopt(argc, argv, strParam.c_str())) != EOF) {
		switch (c) {
		case 'i':
			cmdType = eInstall;
			if (optarg) m_serviceName = optarg;
			break;
		case 'u':
			cmdType = eUninstall;
			if (optarg) m_serviceName = optarg;
			break;
		case 'c':
			if (optarg)
				m_iniFile = optarg;
			break;
		case 'r':
			cmdType = eRun;
			if (optarg) m_serviceName = optarg;
			break;
		case '?':
			cmdType = eHelp;
			break;
		default:
			OnParameter(c, optarg);
			break;
		}
	}

	
	if (cmdType == eHelp)
	{
		std::cout << HelpString() << std::endl;
		return true;
	} 
	if (cmdType == eInstall)
	{ 
		std::string strCmd;
		strCmd = "-r ";
		strCmd += m_serviceName;
		bool bok = Install(m_serviceName.c_str(), strCmd.c_str());
		std::stringstream ss;
		if (bok)

			ss << "install " << m_serviceName << " ok";
		else
			ss << "install " << m_serviceName << " faild";

		std::cout << ss.str();
		Log(ss.str().c_str());
		return  true;
	}
	if (cmdType == eUninstall)
	{
		bool bok = UnInstall(m_serviceName.c_str());
		std::stringstream ss;
		if (bok)

			ss << "uninstall " << m_serviceName << " ok";
		else
			ss << "uninstall " << m_serviceName << " faild";

		std::cout << ss.str();
		Log(ss.str().c_str());
		return  true;
	}
	if (cmdType == eUnknown)
	{
		Log("ServiceStart(false);");
		ServiceStart(false);
	}
	else
	{
		std::string str;
		str = "Begin Run service";
		str += m_serviceName;
		Log(str.c_str());
		Run(m_serviceName.c_str());
	}
	return true;
}