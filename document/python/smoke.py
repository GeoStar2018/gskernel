print("smoke.py start")

import faulthandler
faulthandler.enable()
#上面的这个faulthandler库是为了显示出如果出现错误的时候，显示详细的错误信息

#导入gskernel库，在linux上也就是查找_gskernel.so文件，将其放在python的库目录也可以，放在当前目录也可以，或者放在环境变量中。
#同时_gskernel.so文件依赖的其他so文件也放在相应的位置。或者使得程序能查找到位置。
import gskernel

gskernel.GsKernel.Initialize()
pt = gskernel.GsPoint(1,1)
pt1 = gskernel.GsPoint(2,2)
a = pt.Distance(pt1)
#输出的结果是（根号2）浮点数

print(a)
print("smoke.py end")
