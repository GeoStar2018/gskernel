// simpleproxyserver.cpp : 定义控制台应用程序的入口点。
//

#include "stdafx.h"
#include "NTService.h"
#include "windows.h"
#include "utility.h"
class FakeResult
{
	UTILITY_NAME::GsString m_strPath;
	UTILITY_NAME::GsString m_strContent;
	UTILITY_NAME::GsString m_strType;
	UTILITY_NAME::GsStringMap m_Headers;
public:
	FakeResult(const UTILITY_NAME::GsConfig& config)
	{
		m_strPath = config["Path"].StringValue("");
		m_strType = config["Type"].StringValue("File");
		m_strContent = config["Content"].Value();
		if (UTILITY_NAME::GsCRT::_stricmp(m_strType.c_str(), "Base64") == 0)
		{
			UTILITY_NAME::GsGrowByteBuffer buff;
			buff.FromBase64(m_strContent.c_str());
			m_strContent.clear();
			m_strContent.insert(m_strContent.begin(),
				buff.PtrT<char>(), buff.EndPtrT<char>());
		}
		else if (UTILITY_NAME::GsCRT::_stricmp(m_strType.c_str(), "File") == 0)
		{
			TCHAR buff[1024];
			GetModuleFileName(NULL, buff, 1024);
			UTILITY_NAME::GsString strPath = UTILITY_NAME::GsUtf8( buff).Str();
			strPath  = UTILITY_NAME::GsFile(strPath.c_str()).Parent().FullPath();
			strPath = UTILITY_NAME::GsFileSystem::Combine(strPath.c_str(), m_strContent.c_str());
			if (!UTILITY_NAME::GsFile(strPath.c_str()).Exists())
			{
				//处理文件名的相对路径为绝对路径。
				if (!UTILITY_NAME::GsFileSystem::IsPathRooted(m_strContent.c_str()))
					m_strContent = UTILITY_NAME::GsFileSystem::MakeExistFullPath(m_strContent.c_str());
			}
			else
				m_strContent = strPath;

		}
		

		UTILITY_NAME::GsVector<UTILITY_NAME::GsConfig> vec = config["Headers"].Children();
		UTILITY_NAME::GsVector<UTILITY_NAME::GsConfig>::iterator it = vec.begin();
		for (; it != vec.end(); it++)
		{
			m_Headers[(*it).Name()] = it->Value();
		}
	}
	bool IsMatch(const char* pathQuery)
	{
		return UTILITY_NAME::GsCRT::_stricmp(pathQuery, m_strPath.c_str()) == 0;
	}

	void FillContent(UTILITY_NAME::GsHttpResponse* response)
	{
		response->Headers() = m_Headers;
		if (UTILITY_NAME::GsCRT::_stricmp(m_strType.c_str(), "File")==0)
		{
			UTILITY_NAME::GsFile f(m_strContent.c_str());
			UTILITY_NAME::GsGrowByteBuffer buff;
			f.ReadAllBytes(&buff);
			response->Content(buff.BufferHead(), buff.BufferSize());
		}
		else
			response->Content((const unsigned char*)m_strContent.data(), m_strContent.size());
	}


};
class ProxyServerService :public NTService, UTILITY_NAME::GsHttpServer
{ 
protected:
	//重新启动
	virtual void ServiceRestart()
	{

	}
	virtual int ServiceStart(bool bNT)
	{
		GS_I << "Run Server";
		RunServer();
		GS_I << "Run Server Finish";

		return 0;
	}
	virtual void ServiceStop()
	{
		GS_I << "ServiceStop";

		Shutdown();
		GS_I << "Service shutdown";

	}

	virtual void Log(const char* log)
	{ 
		GS_I << log;
	}
	UTILITY_NAME::GsConfig m_Config;
	UTILITY_NAME::GsString m_strQueryString;
	UTILITY_NAME::GsString m_strHost;
	UTILITY_NAME::GsVector<FakeResult> m_FakeResult;
	UTILITY_NAME::GsStlMap<UTILITY_NAME::GsString, UTILITY_NAME::GsString> m_Headers;
protected:
	UTILITY_NAME::GsString RewriteUrl(const UTILITY_NAME::GsUri& uri)
	{
		UTILITY_NAME::GsString query = uri.PathAndQuery();
		std::stringstream ss;
		ss << m_strHost.c_str() << query.c_str();
		if (query.find_first_of('?', 0) == UTILITY_NAME::GsString::npos)
			ss << "?";
		else
			ss << "&";
		ss << m_strQueryString.c_str();
		return ss.str();
	}
	bool ProcessFakeRequest(UTILITY_NAME::GsUri& reqUri , UTILITY_NAME::GsHttpRequest* request,
		UTILITY_NAME::GsHttpResponse* response)
	{
		if (m_FakeResult.empty())
			return false;
		UTILITY_NAME::GsString strQuery = reqUri.PathAndQuery();
		UTILITY_NAME::GsVector<FakeResult>::iterator it = m_FakeResult.begin();
		for (; it != m_FakeResult.end(); it++)
		{
			if (it->IsMatch(strQuery.c_str()))
			{
				it->FillContent(response);
				return true;
			}

		}
		return false;
	}
	virtual bool OnHttpRequest(UTILITY_NAME::GsHttpRequest* request, UTILITY_NAME::GsHttpResponse* response)
	{
		UTILITY_NAME::GsUri reqUri = request->Uri();
		if (ProcessFakeRequest(reqUri, request, response))
			return true;

		UTILITY_NAME::GsString uri = RewriteUrl(reqUri);
		
		UTILITY_NAME::GsHttpClient client;
		client.RequestHeaders() = request->Headers();
		UTILITY_NAME::GsStlMap<UTILITY_NAME::GsString, UTILITY_NAME::GsString>::iterator it = m_Headers.begin();
		for (; it != m_Headers.end(); it++)
		{
			client.RequestHeaders()[it->first] = it->second;
		}

		UTILITY_NAME::GsGrowByteBuffer buff;
		UTILITY_NAME::GsHttpStatus code = client.Download(uri.c_str(), &buff);
		response->Content(buff.BufferHead(), buff.BufferSize());
		response->Headers() = client.RequestHeaders();
		GS_I << "Url:" << reqUri.ToString().c_str() << "  rewrite to " << uri.c_str() <<
			"   return code " << code;

		return true;
	}
public:
	ProxyServerService(const UTILITY_NAME::GsConfig& config) :NTService(config["Name"].StringValue("GsProxyServer").c_str()), m_Config(config),
		UTILITY_NAME::GsHttpServer(m_Config["Port"].IntValue(80))
	{
		GS_I << "proxy server Start on " << m_Config["Port"].IntValue(80);
		m_strHost = m_Config["Host"].StringValue("http://t0.tianditu.gov.cn");
		m_strQueryString = m_Config["QueryString"].StringValue("tk=cf0c873fe3e7dd6919caf307e46140d6");

		UTILITY_NAME::GsVector<UTILITY_NAME::GsConfig> vec = m_Config["Headers"].Children();
		UTILITY_NAME::GsVector<UTILITY_NAME::GsConfig>::iterator it = vec.begin();
		for (; it != vec.end(); it++)
		{
			m_Headers[(*it).Name()] = it->Value();
		}

		vec = m_Config["FakeResult"].Children();
		it = vec.begin();
		for (; it != vec.end(); it++)
		{
			m_FakeResult.emplace_back(*it);
		}

		
	}
};

int main(int argc, char* argv[])
{
	TCHAR buff[1024];
	GetModuleFileName(NULL, buff, 1024);
	UTILITY_NAME::GsFile fileExe(UTILITY_NAME::GsUtf8( buff).Str().c_str());

	UTILITY_NAME::GsDateTime tm;
	std::stringstream ss;
	ss << "gsproxyserver" << tm.Year() << "_" << tm.Month() << "_" << tm.Day() << "_" << tm.Hour() << "_" << tm.Second() << ".log";
	UTILITY_NAME::GsString str = fileExe.Parent().FullPath();
	str = UTILITY_NAME::GsFileSystem::Combine(str.c_str(), ss.str().c_str());
	
	UTILITY_NAME::GsFileCustomLogOutput file(str.c_str(), true, true);
	UTILITY_NAME::GsLogger::Default().CustomOutput(&file);
	UTILITY_NAME::GsLogger::Default().LogLevel(UTILITY_NAME::eLOGALL);
	UTILITY_NAME::GsLogger::Default().AutoFlush(true);

	str = fileExe.Parent().FullPath();
	str =  UTILITY_NAME::GsFileSystem::Combine(str.c_str(), "config.xml");
	
	UTILITY_NAME::GsConfig config(str.c_str());
	ProxyServerService server(config);
	server.Run(argc, argv);
    return 0;
}

