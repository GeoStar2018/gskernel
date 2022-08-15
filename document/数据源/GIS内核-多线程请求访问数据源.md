栅格数据集，读取指定范围的图片数据。
```
GS_TEST(PGDtaabse, TestMutilSelect, chijing, 20220811)
{
	GsFeatureClassPtr ptrFeaClassPG;
	{
		GsPostGISGeoDatabaseFactory pgFcy;
		GsConnectProperty cp;
		cp.DataSourceType = ePostgreSQL;
		cp.Server = "127.0.0.1";
		cp.Database = "postgres";
		cp.Port = 5432;
		cp.Password = "chijing";
		cp.User = "postgres";
		GsGeoDatabasePtr ptrDb = pgFcy.Open(cp);
		ptrFeaClassPG = ptrDb->OpenFeatureClass("FC_Z");
	}
	GsQueryFilterPtr ptrSelectFilter;
	{
		GsString strWhereClause = u8"\"OID\" <= 1001";
		ptrSelectFilter = new GsQueryFilter();
		ptrSelectFilter->WhereClause(strWhereClause.c_str());
	}


	ThreadPool<> thread;
	GsString name;
	std::mutex m_mutex;
	std::map<int, long long> task_time;
	auto print_totalinfo = [&task_time, &thread]() {
		thread.WaitEmpty();
		thread.Stop();
		long long max = -1;
		long long min = 100000;
		double total = 0;
		double avg = 0;
		for each (auto var in task_time)
		{
			total += var.second;
			max = max < var.second ? var.second : max;
			min = min > var.second ? var.second : min;
		}
		avg = total / task_time.size();

		std::cout << "min " << min << std::endl;
		std::cout << "max " << max << std::endl;
		std::cout << "avg " << avg << std::endl;
		std::cout << "total " << total << std::endl;
	};
	GsLock lock;
	for (int i = 1; i < 1000; i++)
	{
		thread.enqueue([&,i]()
		{
			GsStopWatch wath;
			{	
				std::lock_guard<std::mutex> locl(m_mutex);
				wath.Start();
			}

			GsGMLOGCWriter GMLWriter;
			GsString str;
			auto pCursor =  ptrFeaClassPG->Search();
			auto pFea = pCursor->Next();
			do
			{
				if (!pFea)
					break;
				GMLWriter.Write(pFea->Geometry());
				GsString str = GMLWriter.GML();

			} while (pCursor->Next(pFea));
			{
				std::lock_guard<std::mutex> locl(m_mutex);
				task_time[i]= wath.EscapedMillisecond();
			}
			
		});
	}
	print_totalinfo();
}
```