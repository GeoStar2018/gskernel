GIS内核 -GsConfig 多组配置合并

```c++
GsConfig myconfig;
myconfig.Child("ggd").Child("Metadata").Child("Name").Value("gsgridtest");
myconfig.Child("ggd").Child("Metadata").Child("Unit").Value("D");

GsConfig myconfig2;
myconfig2.Child("ggd1").Child("Metadata3").Child("Name3").Value("gsgridtest3");
myconfig2.Child("ggd1").Child("Metadata3").Child("Unit3").Value("D333");
//Apepend 不能直接加myconfig,  因为myconfig是根节点/
myconfig2.Child("ggd2").Append(myconfig.Child("ggd"));
myconfig2.Save("D:\\a.xml");
```