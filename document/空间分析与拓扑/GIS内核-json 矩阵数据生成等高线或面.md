# GIS内核-json 矩阵数据生成等高线或面 #
<!-- wp:paragraph -->
<p>数据输入:</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p>json数据 内部是501*412 的点阵数据, 按列存储, 且起始点坐标为左下108.61524498800009, 18.193182648400135</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p>水平和垂直分辨率 0.00476953125, 0.00476953125,  </p>
<!-- /wp:paragraph -->

<!-- wp:image {"id":276} -->
<figure class="wp-block-image"><img src="http://192.168.37.21/geomap/wp-content/uploads/2019/09/image-1024x699.png" alt="" class="wp-image-276"/><figcaption><br>此数据二值采样图为:</figcaption></figure>
<!-- /wp:image -->

<!-- wp:image {"id":278} -->
<figure class="wp-block-image"><img src="http://192.168.37.21/geomap/wp-content/uploads/2019/09/image-1.png" alt="" class="wp-image-278"/></figure>
<!-- /wp:image -->

<!-- wp:paragraph -->
<p>需要的结果输出如下图:</p>
<!-- /wp:paragraph -->

<!-- wp:image {"id":279} -->
<figure class="wp-block-image"><img src="http://192.168.37.21/geomap/wp-content/uploads/2019/09/image-2-1024x716.png" alt="" class="wp-image-279"/></figure>
<!-- /wp:image -->

<!-- wp:paragraph -->
<p>此功能可以使用内核接口GsRasterContour::Contour  搞定, </p>
<!-- /wp:paragraph -->

<!-- wp:enlighter/codeblock {"language":"cpp"} -->
<pre class="EnlighterJSRAW" data-enlighter-language="cpp" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group=""></pre>
<!-- /wp:enlighter/codeblock -->

<!-- wp:paragraph -->
<p>我们可以看到 我们只要准备一块w*h高的数据块, 给X,Y方向的分辨率,起始点, 采样距离就可以生成等值线.  但是图中数据的数据读取出来的二值图像是一个按列存储的, 并且起算点为左下, 与接口预定义的左上不符合,所以我们要将该接口的 dblDy  值指定为 正数(默认应该负数,默认是指从tif,img,dem 等高程数据读出的数据), 还有一部要做就是将输入的数据按照行列组织, 列行转行列组织主要代码如下:</p>
<!-- /wp:paragraph -->

<!-- wp:image {"id":280} -->
<figure class="wp-block-image"><img src="http://192.168.37.21/geomap/wp-content/uploads/2019/09/image-3-1024x450.png" alt="" class="wp-image-280"/></figure>
<!-- /wp:image -->

<!-- wp:enlighter/codeblock -->
<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">/*
     7, 4, 1
     8, 5, 2
     9, 6, 3
     13,12,11
 &lt;code>    ||     &lt;/code>
     &lt;code>\/ &lt;/code>
   &lt;code>1,2,3 ,11&lt;/code>
&lt;code>   4,5,6 ,12&lt;/code>
&lt;code>   7,8,9 ,13&lt;/code>
 */
 //矩阵行列转换
 void RowColumnTransposition(double * pData, int &amp;w, int &amp;h)
 {
     double * pdbltmp = (double&lt;em>)malloc(sizeof(double)&lt;/em>w&lt;em>h);     memcpy(pdbltmp, pData, sizeof(double)&lt;/em>w&lt;em>h);    &lt;/em>
&lt;em> for (int i = 0; i &lt; w; i++)     &lt;/em>
&lt;em>{         &lt;/em>
&lt;em>for (int j = 0; j &lt; h; j++)        &lt;/em>
&lt;em>  {            &lt;/em>
  &lt;em> pData[i&lt;/em>h+j] = pdbltmp[j*w + i];
  }
}
     free(pdbltmp);
     int tmp = w;
     w = h;
     h = tmp;
 }</pre>
<!-- /wp:enlighter/codeblock -->

<!-- wp:paragraph -->
<p>完整的调用代码如下:</p>
<!-- /wp:paragraph -->

<!-- wp:enlighter/codeblock -->
<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">include "spatialanalysis.h"
include "spatialanalysishelp.h"
include 
include "geodatabase.h"
include "json.h"
using namespace  GeoStar::Kernel;
using namespace  GeoStar::Utility::Data;
using namespace  GeoStar::Utility;

GS_TEST(GsRasterAnalysis, GRIDGeoJson, chijing, 20190905)
{
	//创建fcs 用于保存等值线分析结果
	std::string fcsFolder = this->MakeInputFolder(GsEncoding::ToUtf8("rasteranalysis"));
	GsSqliteGeoDatabaseFactoryPtr fcsfac = new GsSqliteGeoDatabaseFactory();
	GsConnectProperty conn;
	conn.Server = GsUtf8(fcsFolder.c_str()).Str().c_str();
	conn.DataSourceType = eSqliteFile;
	GsGeoDatabasePtr pteDB = fcsfac->Open(conn);
	GsFeatureClassPtr pFcs = pteDB->OpenFeatureClass("gridrw");
	if (pFcs)
		pFcs->Delete();

	GsFields fds;
	fds.Fields.emplace_back("id", GsFieldType::eIntType);
	fds.Fields.emplace_back("h", GsFieldType::eDoubleType);
	GsGeometryColumnInfo geoInfo;
	geoInfo.FeatureType = eSimpleFeature;
	geoInfo.GeometryType = eGeometryTypePolyline;
	//geoInfo.GeometryType = eGeometryTypePolygon;
	geoInfo.XYDomain = GsBox(-180, -90, 180, 90);
	pFcs = pteDB->CreateFeatureClass("gridrw", fds, geoInfo, new GsSpatialReference(4326));
	if (!pFcs)
		return;
	FeatureClassWrtier FeatureIO(pFcs.p);

	//读取高程
	std::vector&lt;double> pbuff;
	Json::Reader *pJsonParser = new Json::Reader();
	GsFile file("C:\\Users\\chijing\\Desktop\\grid.json");
	GsString strJson = file.ReadAll();

	Json::Value tempVal;

	if (!pJsonParser->parse(strJson.c_str(), tempVal)) {
		return;
	}

	double max = 0, min = 0;
	int w = 0;
	int h = tempVal.size();
	GsSimpleBitmapPtr bitmap = new GsSimpleBitmap(412, 501);

	bool flag = true;
	for (int i = 0; i &lt; tempVal.size(); i++) {
		w = tempVal[i].size();
		unsigned char* pRow = (unsigned char*)bitmap->Row(i);
		for (int j = 0; j &lt; tempVal[i].size(); j++)
			if (tempVal[i][j].isNull())
			{
				pbuff.emplace_back(-10);
				pRow[4 * j] = (char)0;
				pRow[4 * j + 1] = (char)0;
				pRow[4 * j + 2] = (char)0;
				pRow[4 * j + 3] = (char)255;
			}
			else
			{
				double db = tempVal[i][j].asDouble();
				if (flag) {
					max = min = db;
					flag = false;
				}
				if (db > max) max = db;
				if (db &lt; min) min = db;

				pbuff.emplace_back(db);
				pRow[4 * j] = (char)125;
				pRow[4 * j + 1] = (char)125;
				pRow[4 * j + 2] = (char)125;
				pRow[4 * j + 3] = (char)125;
			}
	}
	//查看数据的二值图像
	bitmap->SavePNG("D:\\a.png");
	double dfContourInterval = (max - min) / 10 / 2;
	double *pHead = &amp;pbuff[0];
	//行列转置
	RowColumnTransposition(&amp;pbuff[0], w, h);
	//构造栅格分析类
	GsRasterContourPtr ptrRaserAna = new GsRasterContour();
	ptrRaserAna->ContourInterval(dfContourInterval);

	ptrRaserAna->ResolutionX(0.00476953125);
	ptrRaserAna->ResolutionY(0.00476953125);
	ptrRaserAna->OutputData(&amp;FeatureIO);
	ptrRaserAna->GeometryDimType(1);

	ptrRaserAna->SrcX(108.61524498800009);
	ptrRaserAna->SrcY(18.193182648400135);
	ptrRaserAna->Contour(&amp;pbuff[0], w, h);
}</pre>
<!-- /wp:enlighter/codeblock -->

<!-- wp:paragraph -->
<p>最终如果面和线配图得到如下效果:</p>
<!-- /wp:paragraph -->

<!-- wp:image {"id":286} -->
<figure class="wp-block-image"><img src="http://192.168.37.21/geomap/wp-content/uploads/2019/09/image-4-1024x477.png" alt="" class="wp-image-286"/></figure>
<!-- /wp:image -->