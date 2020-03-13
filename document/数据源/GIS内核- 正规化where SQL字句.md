GIS内核- 正规化where SQL字句

```c++
class WhereClauseRegularHelp
{
public:
	GsString WhereClauseRegular(const char* strWherSql, const GsFields& pfds)
	{
		GsWhereClauseRegularExpressionWriterPtr pt = new GsWhereClauseRegularExpressionWriter(pfds);
		//设置规则
		pt->RegularRule(GsWhereClauseRegularRule::eAddFieldQuotationMarks, true);
		pt->RegularRule(GsWhereClauseRegularRule::eIsNULL2NullCharacter, true);
		GsWhereClauserParserPtr ptrparser = new GsWhereClauserParser();
		ptrparser->Fields(pfds);
		GsString whereStr = "";
		if (ptrparser->Parse(strWherSql))
		{
			pt->Write(ptrparser->Expression());
			whereStr = pt->WhereClause();
		}
		return whereStr;
	}

	void Test()
	{
		GsFields fds;
		fds.Fields.emplace_back("a", GeoStar::Utility::Data::GsFieldType::eIntType);
		fds.Fields.emplace_back("b", GeoStar::Utility::Data::GsFieldType::eStringType);

		GsString whereStr = WhereClauseRegular(" a > 1 and b IS NULL ", fds);
		whereStr = WhereClauseRegular(" ", fds);
		whereStr = WhereClauseRegular(" a BETWEEN 1 AND 5   and b = '45'" ,fds);
	}
};
```

