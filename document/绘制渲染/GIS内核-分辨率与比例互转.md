比例尺分辨率转换

	double Scale2Res(double dScale, GsSpatialReference * pSpa)
	{
		// 长半轴, m_ptrRef 为空间参考
		double dRadius = pSpa->EquatorialRadiusA();
		// 米转换度因子
		double dFactor = 2.0 * dRadius * GsMath::Pi() / 360.0;
	
		if (pSpa->IsGeographic())
		{
			// 地理坐标系先将比例尺单位转为度
			dScale = dScale / dFactor;
		}
		double dRes = dScale / (100 * 96 / 2.54);
		return dRes; 
	}
	
	double Res2Scale(double res, GsSpatialReference * pSpa)
	{
		// 长半轴, m_ptrRef 为空间参考
		double dRadius = pSpa->EquatorialRadiusA();
		// 米转换度因子
		double dFactor = 2.0 * dRadius * GsMath::Pi() / 360.0;
		res *= (100 * 96 / 2.54);
		if (pSpa->IsGeographic())
		{
			res *= dFactor;
		}
		return res;
	}