## 空间参考- urn串的组成和详细剖析 ##

## Urn串的参考文档 ##
1. ogc的官方文档：07-092r3_Definition_identifier_URNs_in_OGC_namespace.pdf 
[https://portal.ogc.org/files/?artifact_id=30575](https://portal.ogc.org/files/?artifact_id=30575 "点击下载")
2. ogc的官方文档：06-166_Draft_proposal_for_OGC_URN_for_IANA_consideration.pdf 
[https://portal.ogc.org/files/?artifact_id=18747](https://portal.ogc.org/files/?artifact_id=18747 "点击下载")
3. ogc的官方文档：06-042_OpenGIS_Web_Map_Service_WMS_Implementation_Specification.pdf 
[https://portal.ogc.org/files/?artifact_id=14416](https://portal.ogc.org/files/?artifact_id=14416 "点击下载")


## 一、Urn串的组成部分 ##

## 1.1 Urn串的样式 ##
```
组成：urn:ogc:def:objectType:authority:version:code
示例：urn:ogc:def:crs:EPSG::4526
示例：urn:ogc:def:crs:OGC:1.3:CRS84
```
Urn名字由六个冒号组成。其中前缀`urn:ogc:def`是固定格式。

## 1.2	“ogc” 部分 ##

“ogc”部分应该是ogc使用的所有urn的注册名称空间授权。
“ogc”部分表示URN中的命名空间权限，使用的值应该向IANA注册。在向IETF/IANA正式注册OGC方案之前，应该使用值“x-OGC”，其中“x”表示实验性的名称空间。

## 1.3	“def” 部分 ##
“def”部分应为固定类别标签，用于标识引用对象定义的所有OGC urn。
“def”部分是{类别.标签}[OGC 06-166]中规定的零件。
剩下的`objectType:authority:version:code`部分应该为是在[ogc06-166]中指定的{ResourceSpecificString}部分的值。

## 1.4	“objectType” 部分 ##
“objectType”部分应为OGC指定的引用定义类型的唯一标识符。
“objectType”部分应为由URN标识的概念类型的OGC指定标记。OGC URN方案表示的对象类型集目前在本文档和修订版的表2中规范性地指定。今后，预计这一机制将被动态注册表所取代。
所需的“objectType”部分标识引用定义的类型，允许的“objectType”值应包括表2中列出的值。
其中表2 在参考文档1中的第18页

## 1.5	“authority” 部分 ##

“authority”部分应为OGC指定的、规定了引用定义的机构的缩写。表1规范性地规定了为OGC URN计划而认可的“authority”集合。今后，预计这一机制将被【动态注册表】所取代。
当引用的定义没有“version”，并且引用的定义不是特定于“authority”版本时，可以省略这些urn的“version”部分。
当包括“version”时，应按照“authority”规定的格式记录。“version”格式有时是“N.N.N”或“N.N”，其中每个“N”代表一个整数。
如果“authority”没有提供其他版本标识，则可以使用年份或其他日期。

其中：要求的“authority”部分表示OGC认可的权限，允许的“authority”值应包括表1中列出的值。在此表中，第三列引用每个“authority”值的规范。
当使用相应的“code”和“version”值时，包含此“authority”值的所有URN应准确表示该规范的含义。

其中 动态注册表 = dynamic registry.

authority 部分的值一般为 EDCS EPSG OGC SI UCUM.


## 1.6	“version” 部分 ##

可选的“version”部分应为参考定义的“authority”或“code”的版本。
其中“version”部分。不应包括“v”或其他前缀。


当 authority 部分的值一般为 EPSG. 的时候。version部分一般省略。 如果不省略有可能会引发解析混乱。

## 1.7	 “code” 部分 ##
“code”部分是在[ogc06-166]中指定的{ResourceSpecificString}部分的值。
“code”部分应为引用定义的唯一标识符，如引用机构所规定。“code”部分可以是人类容易理解的部分，但是应该遵守一个原则就是，它对于该“authority”、“version”和“objectType”唯一的。其中：在这种情况下，URN值的所有文本部分都不区分大小写。


## 1.8	EPSG形式 ##
有一种特殊形式，epsg形式。专门针对情况，引用European Petroleum Survey Group（EPSG）数据库中一个对象的anyURI的URN值应具有以下形式：
`urn:ogc:def:objectType:EPSG::code`
在这种情况下，组成中的“authority”部分应为“EPSG”。
URN的“code”部分应该是引用定义的EPSG“code”唯一标识符。也就是epsg号。或者，URN的“code”部分可以是EPSG“name”唯一标识符。

建议省略“version”，因为在EPSG数据集中标识引用的记录时不需要使用“version”，甚至可能导致混淆。

## 1.9	ogc defined形式 ##
OGC定义的坐标参考系（CRS）的定义应使用表3中列出的URN。显示为“99”和“8888”的URN部件表示应替换指定参数值的数字。

| URN | CRS name |  Definition reference
| :-----:| ---: |  :---: 
| urn:ogc:def:crs:OGC:1.3:CRS1 | Map CS |  B.2 in OGC 06-042
| urn:ogc:def:crs:OGC:1.3:CRS84 | WGS 84 longitude-latitude | B.3 in OGC 06-042
| urn:ogc:def:crs:OGC:1.3:CRS83 | NAD83 longitude-latitude | B.4 in OGC 06-042
| urn:ogc:def:crs:OGC:1.3:CRS27 | NAD27 longitude-latitude | B.5 in OGC 06-042 
| urn:ogc:def:crs:OGC:1.3:CRS88 | NAVD 88 | B.6 in OGC 06-042
| urn:ogc:def:crs:OGC:1.3:AUTO42001:99:8888 | Auto universal transverse mercator | B.7 in OGC 06-042
| urn:ogc:def:crs:OGC:1.3:AUTO42002:99:8888 | Auto transverse mercator | B.8 in OGC 06-042
| urn:ogc:def:crs:OGC:1.3:AUTO42003:99:8888 | Auto orthographic | B.9 in OGC 06-042
| urn:ogc:def:crs:OGC:1.3:AUTO42004:99:8888 | Auto equirectangular | B.10 in OGC 06-042
| urn:ogc:def:crs:OGC:1.3:AUTO42005:99 | Auto Mollweide | B.11 in OGC 06-042

## 二、Urn串的解析 ##

  在内核代码中，构造空间参考对象的时候，可以传入urn串来创建空间参考。
## 三、Urn串的导出 ##

  空间参考对象包含导出urn串的方法。接口名字为GsSpatialReference::ExportToUrn
