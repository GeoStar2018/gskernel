GIS内核-加密界面字符串

	GS_TEST(GsEncrypt, DesEnssscrypt, CJ, 20170703) 
	{
		GsDESEncrypt des("Geor");
		std::string strOri = "test";
		
		//geostar5的加密过程 
		GsGrowByteBuffer buff;
		//加密
		des.Encrypt((const unsigned char*)strOri.c_str(), strOri.size(), &buff);
		GsString byteStr(buff.ToBase64());
	
		GsGrowByteBuffer buff2;
		buff2.FromBase64(byteStr);
		GsGrowByteBuffer outbuff2;
		//解密
		des.Decrypt(buff2.BufferHead(), buff2.BufferSize(), &outbuff2);
		std::string ostr(outbuff2.BufferHead(), outbuff2.EndPtr());
		std::cout << ostr << std::endl;
	}