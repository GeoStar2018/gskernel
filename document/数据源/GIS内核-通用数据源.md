通用数据源伪代码
```
通用数据源就是类似JDBC或者ADO类似的数据源访问接口

//直接内核打开就是以下代码
GsPostGISGeoDatabaseFactory fac;//此是pg数据源工厂,可以换其他数据源
//c++ 直接通过GsGeoDatabase对象拿到GsDatabase对象操作
GsDatabase* ptrGDBS = fac.Open(connProperty)->Database();
查询数据使用示例
std::ostringstream ss1;
ss1 << "select  table_name, index_name,column_name from pg_class \
where t.id = $1 order by t.relname,	i.relname;";
//如果需要多次执行语句必须复用GsStatement对象,不然某些数据源内存暴涨,这是基本的操作
GsStatement* stmt = ptrGDBS->CreateStatement();
stmt->Prepare(ptrGDBS, ss1.str().c_str());
stmt->Bind(0, m_tableName.c_str());
if (stmt->ExecuteQuery() <= 0)
{
    delete stmt;
	delete ptrGDBS;
	return;
}
 	
while (stmt->MoveNext())
{
    GsString strIndex = stmt->StringValue(0);
    GsString strColName = stmt->StringValue(1);
}
delete stmt; //需要释放 或者使用GsSharePtr接收GsStatement
写入数据使用示例

ss1.str("");
ss1 << "insert into testtablename(id,xxname)values($1,$2)";
GsSharePtr<GsStatement> insertStmt = = ptrGDBS->CreateStatement();
InsertStmt->Prepare(ptrGDBS, ss1.str().c_str());
InsertStmt->BindValue(0, 1);
InsertStmt->BindValue(1, u8"chijing");
 
if (insertStmt.Execute() <= 0)
{
	delete ptrGDBS;
	return false;
}
delete ptrGDBS;//需要释放 或者使用GsSharePtr接收GsDatabase
return true;

```