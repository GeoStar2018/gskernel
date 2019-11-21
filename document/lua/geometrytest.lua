--os.execute('sh /srv/NginxProxy/subsys/openresty/nginx/extends/check-giskernel.sh')
--fun = package.loadlib('libgsluaport51.so','luaopen_kernel')
--fun = package.loadlib('D:\\source\\kernel\\debugx64\\gsluaport51.dll','luaopen_kernel')

--""

--


local dllpath = ''
if string.sub(_VERSION,1,7) == 'Lua 5.1' then
	dllpath = 'C:\\Users\\chijing\\.vscode\\extensions\\actboy168.lua-debug-1.19.1\\runtime\\win64\\lua51\\gsluaport51.dll'
elseif string.sub(_VERSION,1,7) == 'Lua 5.3' then
	-- statements
	--package.cpath= 'C:\\Users\\chijing\\.vscode\\extensions\\actboy168.lua-debug-1.19.1\\runtime\\win64\\lua53\\*.dll;;';
	dllpath = 'C:\\Users\\chijing\\.vscode\\extensions\\actboy168.lua-debug-1.19.1\\runtime\\win64\\lua53\\gsluaport.dll'
end

if string.sub(_VERSION,1,7)=='Lua 5.1' then
	local  fun = package.loadlib(dllpath,'luaopen_kernel')--   or package.loadlib('gsluaport51'..'.so','luaopen_kernel')
	fun();
	--require('kernel')
	kernel.GsKernel.Initialize();
	print("GsKernel_Version ",kernel.GsKernel.Version());

	wkt = 'Polygon  ((0 0,300 0,300 300,0 300,0 0),(100 100, 100 200, 200 200, 200 100, 100 100))'
	--创建wktreader
	local reader = kernel.GsWKTOGCReader(wkt)
	--读取创建geometry
	local geo = reader:Read()
	--索引可以加速比较过程
	indexgeo = kernel.GsIndexGeometry(geo)

	local pyramid = kernel.GsMultiPyramid()
	print( type(pyramid:ToString()))

else
	-- lua 5.3 does
	--require 'kernel'
	--require 'gsluaport'
	local  fun = package.loadlib(dllpath,'luaopen_kernel')--   or package.loadlib('gsluaport51'..'.so','luaopen_kernel')
	fun();
	--require('kernel')
	kernel.GsKernel.Initialize();
	print("GsKernel_Version ",kernel.GsKernel.Version());

	wkt = 'Polygon  ((0 0,300 0,300 300,0 300,0 0),(100 100, 100 200, 200 200, 200 100, 100 100))'
	--创建wktreader
	local reader = kernel.GsWKTOGCReader(wkt)
	--读取创建geometry
	local geo = reader:Read()
	--索引可以加速比较过程
	indexgeo = kernel.GsIndexGeometry(geo)

	local pyramid = kernel.GsMultiPyramid()
	print( type(pyramid:ToString()))

end

-- Create some objects

