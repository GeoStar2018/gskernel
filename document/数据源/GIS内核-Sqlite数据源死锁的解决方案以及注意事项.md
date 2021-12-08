#问题描述
对一个FeatureClass做空间查询，并且查询了至少一条数据以后，再对该FeatureClass进行插入、删除、更新操作会失败

#原因分析
内核使用SQLITE的rtree类型的虚表建立空间索引。rtree结构决定了，一旦有修改，整个结构都可能发生改变。因此在空间查询时，SQLITE会给空间索引加锁。而内核的实现是通过触发器或者手动的在插入、更新和删除数据时更新空间索引。
	
当内核通过手动的方式更新空间索引时，会导致数据写入成功，但是空间索引更新失败。

当内核通过触发器的方式更新空间索引时，会导致写入数据与触发器更新空间索引成为一个整体，此时触发器执行失败导致数据也同样不会写入。

#解决方案

从官方文档来看，此问题无解，但是官方依然提供了一个思路，可以将数据先缓存，在适当的时候在做相应的操作。因此，内核的实现方法如下：
	
	
1. 不在更新数据时手动更新空间索引，统一采用触发器
2. 修改触发器，在触发器里面调用自定义函数
3. 自定义函数里面实现更新空间索引，当更新失败时不向sqlite抛出错误（此方法可以保证空间索引虽然更新失败，但是数据依然成功写入），直接将当前失败的地物信息记录在内存。
4. 新增两个接口，用于获取挂起任务的个数以及重新执行挂起的任务。

#新增接口定义
    /// \brief 获取挂起的任务个数
	/// \return 个数
	virtual int GsFeatureClass::SuspendedOperationCount() const;
	/// \brief  执行所有挂起的任务
	/// \return  是否执行成功
	virtual bool GsFeatureClass::ResumeSuspendedOperation();

##注意
这两个接口只有fcs和GeoPackage数据源实现

#使用方法
	
    GS_TEST(GsGeoDatabase, eGeoPackage, SL, 20210226)
	{	
		GsGeoPackageGeoDatabaseFactory fac;
		GsGeoDatabasePtr db = fac.Open("C:/users/shenlei/desktop/test.gpkg");
		GsFeatureClassPtr feaClass = db->OpenFeatureClass("test");
		feaClass->DeleteSpatialIndex();
		feaClass->CreateSpatialIndex();
		GsBox b = feaClass->Extent();
		GsEnvelopePtr env = new GsEnvelope(b);
		GsSpatialQueryFilterPtr filter = new GsSpatialQueryFilter(env);
		GsFeatureCursorPtr cursor = feaClass->Search(filter);
		GsFeaturePtr fea = cursor->Next();
		//for (int i = 0; i < 10; i++)
		{
			fea->Delete();//删除成功，但是对应的空间索引删除失败
			fea->OID(-1);
			fea->Store();//插入成功，但是对应的空间索引没有插入
		}
		cursor.Release();
		if (feaClass->SuspendedOperationCount())//此处返回2
			feaClass->ResumeSuspendedOperation();//返回是否执行成功
	}
	

#注意事项

**老版本的数据，依然存在此问题，因为老版本的数据的触发器会导致死锁。**

**因此老版本的数据，需要重建空间索引，才能将新的触发器实现写入数据库**

**此解决方案无法保证同时进行空间查询以及数据入库时，查询的正确性，因为空间索引表被锁住，无法在解锁之前更新空间索引，所以空间查询使用的空间索引依然是未更新之前的**