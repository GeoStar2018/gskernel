GIS内核-自定义日志输出,实时刷新

```c++
	GsLogger::Default().LogLevel(GsLogLevel::eLOGALL);
	GsFileCustomLogOutput pFileLogOut("D:\\a.log", true);
	GsLogger::Default().CustomOutput(&pFileLogOut);
	GsLogger::Default().AutoFlush(true);
```

