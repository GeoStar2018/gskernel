GIS内核-XXXTEA加密解密示例

```c++

	GsXXTEAEncrypt des("dfdfdfd");
	std::string strOri = "192.168.31.135";

	//加密过程 
	GsGrowByteBuffer buff;
	//转base64
	GsGrowByteBuffer hexBuff((unsigned char *)strOri.c_str(), strOri.size());
	GsString strgeostar5 = hexBuff.ToBase64();
	//加密
	des.Encrypt((const unsigned char*)strgeostar5.c_str(), strgeostar5.size(), &buff);
	//加密串再转base64
	GsString strgeostar5_b64 = buff.ToBase64();

	GsString hj = GsUri::EscapeDataString(strgeostar5_b64);
	//http send  hj

	// recv  hj  and  Unescape
	GsString hj2 = GsUri::UnescapeDataString(hj);

	//geostar5的解密过程
	GsGrowByteBuffer inBuff, outbuff;
	//从加密串base64 获取加密串,进行解密
	inBuff.FromBase64((const char *)strgeostar5_b64.c_str());
	//解密
	des.Decrypt(inBuff.BufferHead(), inBuff.BufferSize(), &outbuff);
	//geostar5的解密过程
	GsGrowByteBuffer lastbuff;
	std::string ostr(outbuff.BufferHead(), outbuff.EndPtr());
	//从base64获取str
	inBuff.FromBase64(ostr.c_str());

	std::string strOri_IP(inBuff.BufferHead(), inBuff.EndPtr());
```

