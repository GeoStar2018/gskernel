#include "table.h"
#include <QStyle>
#include <QStyleFactory>
#include <QApplication>
#include "CMap.h"

table::table( QWidget *parent) :QDockWidget(parent)
{
	ui.setupUi(this);
}

table::~table()
{

}
