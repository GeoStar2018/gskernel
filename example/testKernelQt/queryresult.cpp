#include "queryresult.h"
#include "ui_queryresult.h"
#include <QMainWindow>
#include <QStyle>
#include <QStyleFactory>
using namespace GeoStar::Utility::Data;

QueryResult::QueryResult(QWidget *parent) :
    QDialog(parent),
    ui(new Ui::QueryResult)
{
    ui->setupUi(this);
    ui->treeWidget->setHeaderHidden(true);
    //设置树图的式样为windows式样
    QStyle* style = QStyleFactory::create(tr("Windows"));
    QStringList keys = QStyleFactory::keys() ;
    ui->treeWidget->setStyle(style);

    m_nCount = 0;
    m_bInner = false;
    connect(&m_Timer,SIGNAL(timeout()),
                    this,SLOT(timeout()));

}

void QueryResult::timeout()
{
    m_nCount--;
    //m_ptrMap->Paint();
    if(m_nCount <= 0)
          m_Timer.stop();
}

QueryResult::~QueryResult()
{
    UnAdvise();
    delete ui;
}
void QueryResult::BindMap(GsMap* pMap)
{
    UnAdvise();
    m_ptrMap = pMap;
    Advise();
}
void QueryResult::UnAdvise()
{
    if(!m_ptrMap)
        return;
    m_ptrMap->ScreenDisplay()->OnBeforeEndDrawing.Remove(this,&QueryResult::OnTrackerDraw);
}


void QueryResult::OnTrackerDraw(GsDisplay* pDisp)
{
    if(m_nCount <=0)
        return;
    if((m_nCount % 2) == 0)
        return;
    if(!m_ptrSymbol || !m_ptrGeometry)
        return;
    m_ptrSymbol->StartDrawing(pDisp->Canvas(),pDisp->DisplayTransformation());
    m_ptrSymbol->Draw(m_ptrGeometry);
    m_ptrSymbol->EndDrawing();

}
void QueryResult::Advise()
{
    if(!m_ptrMap)
        return;
    m_ptrMap->ScreenDisplay()->OnBeforeEndDrawing.Add(this,&QueryResult::OnTrackerDraw);

}
QTreeWidgetItem* QueryResult::CreateTreeItem(GsSelectionSet* pSet,GsFeatureLayer* pFeaLyr)
{
    QStringList list;
    list<<QString::fromStdString(pFeaLyr->Name());
    QTreeWidgetItem* pItem = new QTreeWidgetItem(list);
    GsEnumIDsPtr ptrEnum = pSet->EnumIDs();
    long long nID = ptrEnum->Next();
    while(nID >=0)
    {
        list.clear();
        list<<QString::number(nID);
        QTreeWidgetItem* c = new QTreeWidgetItem(pItem,list);
        nID = ptrEnum->Next();
    }
    return pItem;
}

void QueryResult::Add(GsSelectionSet* pSet,GsFeatureLayer* pFeaLyr)
{
    ui->treeWidget->addTopLevelItem(CreateTreeItem(pSet,pFeaLyr));
}

void QueryResult::ShowResult(std::vector<GsFeatureLayerPtr>& vec,GsSpatialQueryFilter* pQF)
{
    m_bInner = true;
    ui->treeWidget->clear();
    ui->listWidget->clear();
    std::vector<GsFeatureLayerPtr>::iterator it = vec.begin();
    for(;it != vec.end();it++)
    {
        GsSelectionSetPtr ptrSel =(*it)->FeatureClass()->Select(pQF);
        Add(ptrSel,*it);
    }
    m_vec = vec;
    m_bInner = false;
    if(!this->isVisible())
    {
        this->setWindowFlags(Qt::WindowStaysOnTopHint);
        this->show();
    }

}
void QueryResult::ShowProperty(GsFeature* pFea)
{
    ui->listWidget->clear();
    if(NULL == pFea)
        return;

    GsFields fs = pFea->FeatureClass()->Fields();
    new QListWidgetItem(QString("OID\t") + QString::number(pFea->OID()),ui->listWidget);
    for(int i =2;i<fs.Fields.size();i++)
    {
        GsField f = fs.Fields[i];
        QString strField = QString::fromStdString( f.Name);
        strField+="\t";
        switch(f.Type)
        {
        case eErrorType:
            strField+="Empty";
            break;
        /// \brief BOOL类型
        case eBoolType:
            strField+="Empty";
            break;
        /// \brief 32位的整型
        case eIntType:
            strField+=QString::number(pFea->ValueInt(i));
            break;
        /// \brief 32位的无符号整型
        case eUIntType:
            strField+=QString::number(pFea->ValueUInt(i));
            break;
        /// \brief 64位的整型
        case eInt64Type:
            strField+=QString::number(pFea->ValueInt64(i));
            break;
        /// \brief 64位的无符号整型
        case eUInt64Type:
            strField+=QString::number(pFea->ValueUInt64(i));
            break;
        /// \brief 字符串类型
        case eStringType:
        {
            GeoStar::Utility::GsString str = pFea->ValueString(i);
            strField+=QString::fromUtf8(str.c_str());
            break;
        }
        /// \brief 二进制类型
        case eBlobType:
            strField+="Blob";
            break;
        /// \brief 浮点型
        case eFloatType:
            strField+=QString::number(pFea->ValueFloat(i));
            break;
        /// \brief 双精度浮点型
        case eDoubleType:
            strField+=QString::number(pFea->ValueDouble(i));
            break;
        /// \brief 几何类型
        case eGeometryType:
            strField+="Geometry";
            break;
        }
        new QListWidgetItem(strField,ui->listWidget);

    }
}

void QueryResult::HighLight(GsFeature* pFea)
{
    ShowProperty(pFea);
    if(!pFea)
        return;
    m_ptrGeometry = pFea->Geometry();
    if(!m_ptrGeometry)
        return;

    if(!m_ptrPointSymbol)
        m_ptrPointSymbol = new GsSimplePointSymbol(GsColor(GsColor::Red),4);
    if(!m_ptrLineSymbol)
        m_ptrLineSymbol = new GsSimpleLineSymbol(GsColor(GsColor::Red),1);
    if(!m_ptrFillSymbol)
        m_ptrFillSymbol = new GsSimpleFillSymbol(GsColor(GsColor::Red));
    int nDim = GsGeometry::GeometryTypeDimension(m_ptrGeometry->GeometryType());
    if(nDim ==0)
        m_ptrSymbol = m_ptrPointSymbol.p;
    else if(nDim ==1)
        m_ptrSymbol = m_ptrLineSymbol.p;
    else if(nDim ==2)
        m_ptrSymbol = m_ptrFillSymbol.p;

    m_nCount = 4;
    m_Timer.start(100);


}

GsFeatureLayerPtr QueryResult::FindLayer(const QString& str)
{
    std::vector<GsFeatureLayerPtr>::iterator it = m_vec.begin();
    for(;it != m_vec.end();it++)
    {
        QString strName = QString::fromStdString((*it)->Name());
        if(strName.compare(str,Qt::CaseInsensitive) ==0)
            return *it;
    }
    return 0;
}

void QueryResult::on_treeWidget_itemClicked(QTreeWidgetItem *item, int column)
{
    if(m_bInner)
        return ;
    ui->listWidget->clear();
    if(item == NULL)
        return;
    if(item->parent() == NULL)
        return;
    QTreeWidgetItem* pParent = item->parent();
    GsFeatureLayerPtr ptrFeaLyr = FindLayer(pParent->text(0));
    if(!ptrFeaLyr)
        return ;
    long long oid = item->text(0).toLongLong();
    GsFeaturePtr pFea =  ptrFeaLyr->FeatureClass()->Feature(oid);
    HighLight(pFea);
}
