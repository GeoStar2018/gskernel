#ifndef QUERYRESULT_H
#define QUERYRESULT_H

#include <QDialog>
#include <map.h>
#include <QTreeWidgetItem>

#include <QTimer>
using namespace GeoStar::Kernel;

namespace Ui {
class QueryResult;
}

class QueryResult : public QDialog
{
    Q_OBJECT

    GsSimplePointSymbolPtr m_ptrPointSymbol;
    GsSimpleLineSymbolPtr m_ptrLineSymbol;
    GsSimpleFillSymbolPtr m_ptrFillSymbol;
    QTimer m_Timer;
    GsSymbolPtr m_ptrSymbol;
    GsGeometryPtr m_ptrGeometry;
    volatile int m_nCount;
    GsMapPtr m_ptrMap;
    volatile bool m_bInner;
    std::vector<GsFeatureLayerPtr> m_vec;
    GsFeatureLayerPtr FindLayer(const QString& str);
    void Advise();
    void HighLight(GsFeature* pFea);
    QTreeWidgetItem* CreateTreeItem(GsSelectionSet* pSet,GsFeatureLayer* pFeaLyr);
    void Add(GsSelectionSet* pSet,GsFeatureLayer* pFeaLyr);
    void OnTrackerDraw(GsDisplay* pDisp);
    void ShowProperty(GsFeature* pFea);
public:
    explicit QueryResult(QWidget *parent = 0);
    ~QueryResult();
    void UnAdvise();
    void BindMap(GsMap* pMap);
    void ShowResult(std::vector<GsFeatureLayerPtr>& vec,GsSpatialQueryFilter* pQF);

private slots:
    void on_treeWidget_itemClicked(QTreeWidgetItem *item, int column);

    void timeout();
private:
    Ui::QueryResult *ui;
};

#endif // QUERYRESULT_H
