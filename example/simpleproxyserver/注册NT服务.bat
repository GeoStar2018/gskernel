@ echo off
%1 %2
ver|find "5.">nul&&goto :st
mshta vbscript:createobject("shell.application").shellexecute("%~s0","goto :st","","runas",1)(window.close)&goto :eof

:st
set p=%~dp0
%p%\simpleproxyserver.exe -iGsProxyServer
sc start GsProxyServer
pause
:eof