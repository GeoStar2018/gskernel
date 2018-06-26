#ifndef TABLE_SHOW
#define TABLE_SHOW

#include <QDockWidget>
#include <QTreeView>
#include "ui_table.h"

class table : public QDockWidget
{
	Q_OBJECT
public:
	table(QWidget *parent = 0);
	~table();
public:
		Ui::fieldstable ui;
};

#endif // TABLE_SHOW
