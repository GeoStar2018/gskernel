GIS内核- 正规化where SQL字句

```c++
class WhereClauseRegularHelp
{
public:
	GsString WhereClauseRegular(const char* strWherSql, const GsFields& pfds, GsDataSourceType eType = eGDB)
	{
		GsWhereClauseRegularExpressionWriterPtr pt = new GsWhereClauseRegularExpressionWriter(pfds);
		switch (eType)
		{
			case GeoStar::Kernel::eUnknownDataSource:
			case GeoStar::Kernel::eSqliteFile:
			case GeoStar::Kernel::eShapeFile:
			case GeoStar::Kernel::eGeoPackage:
			case GeoStar::Kernel::ePostgreSQL:
			case GeoStar::Kernel::eFile:
			case GeoStar::Kernel::eDameng:
			case GeoStar::Kernel::eWeb:
			case GeoStar::Kernel::eRvdb:
			case GeoStar::Kernel::eOGR:
			case GeoStar::Kernel::ePostgreSQL2:
			case GeoStar::Kernel::eCAD:
				return strWherSql;
				break;
			case GeoStar::Kernel::eOracleSpatial:
			case GeoStar::Kernel::eOracleSpatial2:
				pt->RegularRule(GsWhereClauseRegularRule::eAddTimeFuncation, true);
				break;
			case GeoStar::Kernel::eMySQL:
				pt->RegularRule(GsWhereClauseRegularRule::eAddFieldQuotationMarks, false);
				break;
			case GeoStar::Kernel::eGDB:
				pt->RegularRule(GsWhereClauseRegularRule::eIsNULL2NullCharacter, true);
				break;

			default:
				return strWherSql;
		}
		//设置规则
		pt->RegularRule(GsWhereClauseRegularRule::eAddFieldQuotationMarks, true);
		pt->RegularRule(GsWhereClauseRegularRule::eIsNULL2NullCharacter, true);
		GsWhereClauserParserPtr ptrparser = new GsWhereClauserParser();
		ptrparser->Fields(pfds);
		GsString whereStr = strWherSql;
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

