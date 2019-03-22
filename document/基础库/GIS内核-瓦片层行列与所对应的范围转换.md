GIS内核-瓦片层行列与所对应的范围转换

	

	//获取金字塔
	GsPyramidPtr py = 	m_Ptls->Pyramid();//GsTileClass m_Ptls
	//查询某box范围对应的层行列号
	py->TileIndexRange(box.XMin, box.YMin, box.XMax, box.YMax, i, &nminRow, &nminCol, &nmaxRow, &nmaxCol);

	//查询层行列号对应的地理范围
	py->TileExtent(i, r, c, &tmpbox.XMin, &tmpbox.YMin, &tmpbox.XMax,tmpbox.YMax);