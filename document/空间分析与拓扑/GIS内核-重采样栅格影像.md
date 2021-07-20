GIS内核-重采样栅格影像

```java
		// 获取Iiff文件路径
        File file = new File("E:\\测试数据\\DEM\\DEM.tif");
        if (file != null) {
            // 读取Tiff
            GsFileGeoDatabaseFactory factory = new GsFileGeoDatabaseFactory();
            GsConnectProperty property = new GsConnectProperty();
            property.setServer(file.getParent());
            property.setDataSourceType(GsDataSourceType.eFile);
            GsGeoDatabase geoDB = factory.Open(property);
            GsRasterClass rasterClass = geoDB.OpenRasterClass(file.getName()
                    .substring(0, file.getName().lastIndexOf(".")));
            //计算写出的像素范围
            GsRasterColumnInfo info = rasterClass.RasterColumnInfo();
            double[] srcd = info.getGeoTransform();
            double srcCell = srcd[1];
            int srcW = info.getWidth();
            int srcH = info.getHeight();
            //xyCell 是要输出的像素分辨率
            int newW = (int) (srcW / (xyCell / srcCell));
            int newH = (int) (srcH / (xyCell / srcCell));

            srcd[1] = xyCell;
            srcd[5] = -xyCell;
            GsRasterColumnInfo columnInfo = info;
            columnInfo.setGeoTransform(srcd);
            columnInfo.setWidth(newW);
            columnInfo.setHeight(newH);
            GsRasterClass rasterClassDst = geoDB.CreateRasterClass("new.tif",
                    GsRasterCreateableFormat.eGTiff, columnInfo, rasterClass.SpatialReference());

            GsBox boundary = rasterClass.Extent();
            GsRect rect = rasterClass.ExtentToRange(boundary);
            GsRasterCursor cursor = rasterClass.Search(rect, new GsSize(newW, newH), GsRasterResampleAlg.eNearestNeighbour);
            while (cursor.Next(this.innerRaster)) {
                rasterClassDst.WriteRaster(innerRaster);
            }
        }
```

