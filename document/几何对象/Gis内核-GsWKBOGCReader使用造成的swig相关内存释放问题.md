## java代码中GsWKBOGCReader读取数据造成内存不断增加的解决方案 ##

首先利用GsWKBOGCReader读取一块内存，然后返回读取到的GsGeometry类型，如果while循环包裹下的代码，会造成内存泄露。


```
        byte[] bytes = {0, 0, 0, 0, 1, 63, -16, 0, 0, 0, 0, 0, 0, 63, -16, 0, 0, 0, 0, 0, 0};
        GsGrowByteBuffer buffer = new GsGrowByteBuffer(bytes,bytes.length);

        GsWKBOGCReader baseReader = new GsWKBOGCReader(buffer);
        GsGeometry gsGem = baseReader.Read();
        if(gsGem ==null)
        {
            System.out.println(i + "--gsGem is null");
        }
        else
        {
            System.out.println(i + "--time is ok" + gsGem.RefCount());    
            gsGem.delete();
            gsGem = null;
        }
        baseReader.delete();
        baseReader = null;//必须

        buffer.delete();
        buffer = null;
           
```

解决方案就是：代码中的非对象类的类型均需要调用delete函数，来释放内存。
例如：
在GsGrowByteBuffer生成的buffer，buffer要及时的调用delete函数，并且赋值为null。
baseReader 也是需要及时的调用delete()函数，并且赋值为null。
如果是GsGeometry类型，也是需要释放内存的。

小技巧：
凡是c++代码没有问题，但是java代码有问题的情况需要考虑以下几个地方。

1：考虑java代码有没有产生内存泄露的问题。也就是考虑内存释放的问题，也就是delete函数有没有调用。

2：考虑java代码的用法问题，因为swig封装了部分接口。

3：调试看是否是回调引起的内存释放问题。需要托管内存为C++管理内存。
