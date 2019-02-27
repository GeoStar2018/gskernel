
// InternetTestDlg.cpp : 实现文件
//

#include "stdafx.h"
#include "MapOnlineView.h"
#include "MapOnlineViewDlg.h"
#include "afxdialogex.h"

#include "afxinet.h"

#include "OnlineMapInstance.h"
#include "LocalMapInstance.h"
#include "PageLayout.h"


#ifdef _DEBUG
//#define new DEBUG_NEW
#endif


// 用于应用程序“关于”菜单项的 CAboutDlg 对话框

class CAboutDlg : public CDialogEx
{
public:
	CAboutDlg();

// 对话框数据
#ifdef AFX_DESIGN_TIME
	enum { IDD = IDD_ABOUTBOX };
#endif

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV 支持

// 实现
protected:
	DECLARE_MESSAGE_MAP()
};

CAboutDlg::CAboutDlg() : CDialogEx(IDD_ABOUTBOX)
{
}

void CAboutDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialogEx::DoDataExchange(pDX);
}

BEGIN_MESSAGE_MAP(CAboutDlg, CDialogEx)
END_MESSAGE_MAP()


// CMapOnlineViewDlg 对话框



CMapOnlineViewDlg::CMapOnlineViewDlg(CWnd* pParent /*=NULL*/)
	: CDialogEx(IDD_MAPONLINEVIEW_DIALOG, pParent)
	, m_iTileType(0)
{
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
}

void CMapOnlineViewDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialogEx::DoDataExchange(pDX);
	//DDX_Control(pDX, IDC_GEOSPACECTRL1, m_GeoSpace);
	DDX_Control(pDX, IDC_MAP, m_mapLayer);
	DDX_Control(pDX, IDC_LISTTILESRC, m_listTileSrc);
	DDX_Radio(pDX, IDC_RADIO_VECTOR, m_iTileType);
}

BEGIN_MESSAGE_MAP(CMapOnlineViewDlg, CDialogEx)
	ON_WM_SYSCOMMAND()
	ON_WM_PAINT()
	ON_WM_QUERYDRAGICON()
	ON_CBN_SELCHANGE(IDC_LISTTILESRC, &CMapOnlineViewDlg::OnSelectMap)
	ON_BN_CLICKED(IDC_BTN_UP, &CMapOnlineViewDlg::OnBnClickedBtnUp)
	ON_BN_CLICKED(IDC_BTN_DOWN, &CMapOnlineViewDlg::OnBnClickedBtnDown)
	ON_BN_CLICKED(IDC_PAN, &CMapOnlineViewDlg::OnBnClickedPan)
	ON_WM_LBUTTONDOWN()
	ON_WM_LBUTTONUP()
	ON_WM_MOUSEMOVE()
	ON_BN_CLICKED(IDC_RADIO_VECTOR, &CMapOnlineViewDlg::OnBnClickedRadio)
	ON_BN_CLICKED(IDC_RADIO_IMAGE, &CMapOnlineViewDlg::OnBnClickedRadio)

	ON_MESSAGE(WM_MAP_UPDATE, &CMapOnlineViewDlg::OnUpdataMap)
	ON_BN_CLICKED(IDC_TEXTFEATURE, &CMapOnlineViewDlg::OnBnClickedTextfeature)
END_MESSAGE_MAP()


// CMapOnlineViewDlg 消息处理程序

BOOL CMapOnlineViewDlg::OnInitDialog()
{
	CDialogEx::OnInitDialog();

	// 将“关于...”菜单项添加到系统菜单中。

	// IDM_ABOUTBOX 必须在系统命令范围内。
	ASSERT((IDM_ABOUTBOX & 0xFFF0) == IDM_ABOUTBOX);
	ASSERT(IDM_ABOUTBOX < 0xF000);

	CMenu* pSysMenu = GetSystemMenu(FALSE);
	if (pSysMenu != NULL)
	{
		BOOL bNameValid;
		CString strAboutMenu;
		bNameValid = strAboutMenu.LoadString(IDS_ABOUTBOX);
		ASSERT(bNameValid);
		if (!strAboutMenu.IsEmpty())
		{
			pSysMenu->AppendMenu(MF_SEPARATOR);
			pSysMenu->AppendMenu(MF_STRING, IDM_ABOUTBOX, strAboutMenu);
		}
	}

	// 设置此对话框的图标。  当应用程序主窗口不是对话框时，框架将自动
	//  执行此操作
	SetIcon(m_hIcon, TRUE);			// 设置大图标
	SetIcon(m_hIcon, FALSE);		// 设置小图标

	// TODO: 在此添加额外的初始化代码
	GsKernel::Initialize();
	InitPyramids();
	m_mapLayer.GetWindowRect(&m_mapRect);
	m_listTileSrc.AddString("WYB_WMTS");
	//m_listTileSrc.SetCurSel(0);
	//OnSelectMap();
	return TRUE;  // 除非将焦点设置到控件，否则返回 TRUE
}

void CMapOnlineViewDlg::OnSysCommand(UINT nID, LPARAM lParam)
{
	if ((nID & 0xFFF0) == IDM_ABOUTBOX)
	{
		CAboutDlg dlgAbout;
		dlgAbout.DoModal();
	}
	else
	{
		CDialogEx::OnSysCommand(nID, lParam);
	}
}

// 如果向对话框添加最小化按钮，则需要下面的代码
//  来绘制该图标。  对于使用文档/视图模型的 MFC 应用程序，
//  这将由框架自动完成。

void CMapOnlineViewDlg::OnPaint()
{
	if (IsIconic())
	{
		CPaintDC dc(this); // 用于绘制的设备上下文

		SendMessage(WM_ICONERASEBKGND, reinterpret_cast<WPARAM>(dc.GetSafeHdc()), 0);

		// 使图标在工作区矩形中居中
		int cxIcon = GetSystemMetrics(SM_CXICON);
		int cyIcon = GetSystemMetrics(SM_CYICON);
		CRect rect;
		GetClientRect(&rect);
		int x = (rect.Width() - cxIcon + 1) / 2;
		int y = (rect.Height() - cyIcon + 1) / 2;

		// 绘制图标
		dc.DrawIcon(x, y, m_hIcon);
	}
	else
	{
		CDialogEx::OnPaint();
	}
}

//当用户拖动最小化窗口时系统调用此函数取得光标
//显示。
HCURSOR CMapOnlineViewDlg::OnQueryDragIcon()
{
	return static_cast<HCURSOR>(m_hIcon);
}


static CString TileXYToQuadKey(int tileX, int tileY, int levelOfDetail)
{
	CString quadKey;
	for (int i = levelOfDetail; i >0; i--)
	{
		char digit = '0';
		int mask = 1 << (i - 1);
		if ((tileX & mask) != 0)
		{
			digit++;
		}
		if ((tileY & mask) != 0)
		{
			digit++;
			digit++;
		}
		//quadKey.Append(digit);
		quadKey.AppendChar(digit);
	}
	return quadKey;
}

static void QuadKeyToTileXY(CString& quadKey, OUT int& tileX, OUT int& tileY, OUT int& levelOfDetail)
{
	tileX = tileY = 0;
	levelOfDetail = quadKey.GetLength();
	for (int i = levelOfDetail; i > 0; i--)
	{
		int mask = 1 << (i - 1);
		switch (quadKey[levelOfDetail - i])
		{
		case '0':
			break;

		case '1':
			tileX |= mask;
			break;

		case '2':
			tileY |= mask;
			break;

		case '3':
			tileX |= mask;
			tileY |= mask;
			break;

		default:
			break;
			//throw newArgumentException("Invalid QuadKey digit sequence.");
		}
	}
}
CPageLayout* other;
void CMapOnlineViewDlg::InitPyramids()
{
	//天地图
	GsPyramid* pPyramid = new GsPyramid();
	{
		pPyramid->TopLevelIndex = 0;
		pPyramid->BottomLevelIndex = 16;
		pPyramid->ScaleX = 2.0;
		pPyramid->ScaleY = 2.0;
		pPyramid->TileSizeX = 256;
		pPyramid->TileSizeY = 256;
		pPyramid->OriginRowIndex = 0;
		pPyramid->OriginColIndex = 0;

		pPyramid->FromX = -180;
		pPyramid->ToX = 180;
		pPyramid->FromY = 90;
		pPyramid->ToY = -270;

		pPyramid->XMin = -180;
		pPyramid->XMax = 180;
		pPyramid->YMin = -90;
		pPyramid->YMax = 90;

		pPyramid->PAI = 3.1415926535897931;
		pPyramid->Name = "天地图";
		pPyramid->Tolerance = 1.1920928955078125e-07;
		m_mapMapInstance.insert(make_pair("天地图", new COnlineMapInstance(pPyramid, m_mapLayer)));
		m_mapMapInstance["天地图"]->Init("http://t${Odd-Even7}.tianditu.com/DataServer?T=vec_c&x=${Col}&y=${Row}&l=${Level}");
	}

	//高德
	pPyramid = new GsPyramid;
	{
		pPyramid->TopLevelIndex = 0;
		pPyramid->BottomLevelIndex = 20;
		pPyramid->ScaleX = 2.0;
		pPyramid->ScaleY = 2.0;
		pPyramid->TileSizeX = 256;
		pPyramid->TileSizeY = 256;
		pPyramid->OriginRowIndex = 0;
		pPyramid->OriginColIndex = 0;

		pPyramid->FromX = -20037508.3427892;
		pPyramid->ToX = 20037508.3427892;
		pPyramid->FromY = 20037508.3427892;
		pPyramid->ToY = -20037508.3427892;

		pPyramid->XMin = -20037508.3427892;
		pPyramid->XMax = 20037508.3427892;
		pPyramid->YMin = -20037508.3427892;
		pPyramid->YMax = 20037508.3427892;

		pPyramid->PAI = 3.1415926535897931;
		pPyramid->Name = "高德";
		pPyramid->Tolerance = 1.1920928955078125e-07;
		m_mapMapInstance.insert(make_pair("高德", new COnlineMapInstance(pPyramid, m_mapLayer)));
		m_mapMapInstance["高德"]->Init("https://wprd01.is.autonavi.com/appmaptile?lang=zh_cn&size=1&style=7&x=${Col}&y=${Row}&z=${Level}&scl=1&ltype=11");
	}

	//google
	pPyramid = new GsPyramid;
	{
		pPyramid->TopLevelIndex = 0;
		pPyramid->BottomLevelIndex = 19;
		pPyramid->ScaleX = 2.0;
		pPyramid->ScaleY = 2.0;
		pPyramid->TileSizeX = 256;
		pPyramid->TileSizeY = 256;
		pPyramid->OriginRowIndex = 0;
		pPyramid->OriginColIndex = 0;

		pPyramid->FromX = -20037508.3427892;
		pPyramid->ToX = 20037508.3427892;
		pPyramid->FromY = 20037508.3427892;
		pPyramid->ToY = -20037508.3427892;

		pPyramid->XMin = -20037508.3427892;
		pPyramid->XMax = 20037508.3427892;
		pPyramid->YMin = -20037508.3427892;
		pPyramid->YMax = 20037508.3427892;
		
		pPyramid->PAI = 3.1415926535897931;
		pPyramid->Name = "谷歌";
		pPyramid->Tolerance = 1.1920928955078125e-07;
		m_mapMapInstance.insert(make_pair("谷歌", new COnlineMapInstance(pPyramid, m_mapLayer)));
		m_mapMapInstance["谷歌"]->Init("http://mt0.google.cn/maps/vt?lyrs=s&gl=CN&x=${Col}&y=${Row}&z=${Level}");
	}

	//必应
	pPyramid = new GsPyramid;
	{//因为必应用的四叉树键，如果需要支持，必须修改内核，目前无法支持
		pPyramid->TopLevelIndex = 0;
		pPyramid->BottomLevelIndex = 20;
		pPyramid->ScaleX = 2.0;
		pPyramid->ScaleY = 2.0;
		pPyramid->TileSizeX = 256;
		pPyramid->TileSizeY = 256;
		pPyramid->OriginRowIndex = 0;
		pPyramid->OriginColIndex = 0;

		pPyramid->FromX = -180;
		pPyramid->ToX = 180;
		pPyramid->FromY = 85.05112878;
		pPyramid->ToY = -274.94887122;

		pPyramid->XMin = -180;
		pPyramid->XMax = 180;
		pPyramid->YMin = -85.05112878;
		pPyramid->YMax = 85.05112878;

		pPyramid->PAI = 3.1415926535897931;
		pPyramid->Name = "必应";
		pPyramid->Tolerance = 1.1920928955078125e-07;
		m_mapMapInstance.insert(make_pair("必应", new COnlineMapInstance(pPyramid, m_mapLayer)));
		m_mapMapInstance["必应"]->Init("https://dynamic.t1.tiles.ditu.live.com/comp/ch/${key}?it=A,G,L&mkt=zh-cn&og=109&cstl=w4c&ur=CN&n=z");
	}

	//OSM
	pPyramid = new GsPyramid;
	{
		pPyramid->TopLevelIndex = 0;
		pPyramid->BottomLevelIndex = 20;
		pPyramid->ScaleX = 2.0;
		pPyramid->ScaleY = 2.0;
		pPyramid->TileSizeX = 256;
		pPyramid->TileSizeY = 256;
		pPyramid->OriginRowIndex = 0;
		pPyramid->OriginColIndex = 0;

		pPyramid->FromX = -20037508.3427892;
		pPyramid->ToX = 20037508.3427892;
		pPyramid->FromY = 20037508.3427892;
		pPyramid->ToY = -20037508.3427892;

		pPyramid->XMin = -20037508.3427892;
		pPyramid->XMax = 20037508.3427892;
		pPyramid->YMin = -20037508.3427892;
		pPyramid->YMax = 20037508.3427892;

		pPyramid->PAI = 3.1415926535897931;
		pPyramid->Name = "OSM";
		pPyramid->Tolerance = 1.1920928955078125e-07;
		m_mapMapInstance.insert(make_pair("OSM", new COnlineMapInstance(pPyramid, m_mapLayer)));
		m_mapMapInstance["OSM"]->Init("https://c.tile.openstreetmap.org/${Level}/${Col}/${Row}.png");
	}

	//ArcGis
	pPyramid = new GsPyramid;
	{
		pPyramid->TopLevelIndex = 0;
		pPyramid->BottomLevelIndex = 16;
		pPyramid->ScaleX = 2.0;
		pPyramid->ScaleY = 2.0;
		pPyramid->TileSizeX = 256;
		pPyramid->TileSizeY = 256;
		pPyramid->OriginRowIndex = 0;
		pPyramid->OriginColIndex = 0;

		pPyramid->FromX = -20037508.3427892;
		pPyramid->ToX = 20037508.3427892;
		pPyramid->FromY = 20037508.3427892;
		pPyramid->ToY = -20037508.3427892;

		pPyramid->XMin = -20037508.3427892;
		pPyramid->XMax = 20037508.3427892;
		pPyramid->YMin = -20037508.3427892;
		pPyramid->YMax = 20037508.3427892;

		pPyramid->PAI = 3.1415926535897931;
		pPyramid->Name = "ArcGis";
		pPyramid->Tolerance = 1.1920928955078125e-07;
		m_mapMapInstance.insert(make_pair("ArcGis", new COnlineMapInstance(pPyramid, m_mapLayer)));
		m_mapMapInstance["ArcGis"]->Init("http://cache1.arcgisonline.cn/ArcGIS/rest/services/ChinaOnlineStreetGray/MapServer/tile/${Level}/${Row}/${Col}");
	}

	//本地地图数据，目前只支持fcs，地址也写死了，主要是为了以后遇到要看显示效果的bug提供方便
	{
		m_mapMapInstance.insert(make_pair("本地", new CLocalMapInstance(nullptr, m_mapLayer)));
		m_mapMapInstance["本地"]->Init("");
	}

	//WMTS
	{
		const char * strMatrix = "Matrix_jiangxiajinkou_0";
		const char * url = "http://192.168.37.172:9010/jiangxiajinkou/wmts";
		GsWMTSUriParserPtr ptrParse = new GsWMTSUriParser(url);
		bool bl = ptrParse->ParseCapability();
		bool bl2 = ptrParse->CurrentTileMatrixSet(strMatrix);
		GsPyramidPtr pyd = ptrParse->Pyramid();
		GsSpatialReferencePtr ptrSpatial = ptrParse->SpatialReference();
		
		pyd->XMin = 113.90625;
		pyd->XMax = 114.2578125;
		pyd->YMin = 29.8828125;
		pyd->YMax = 30.5859375;
		m_mapMapInstance.insert(make_pair("WYB_WMTS", new COnlineMapInstance(pyd, m_mapLayer)));

		//http://192.168.37.172:9010/jiangxiajinkou/wmts?SERVICE=WMTS&VERSION=1.0.0&REQUEST=GetTile&LAYER=jiangxiajinkou&FORMAT=image/tile&TILEMATRIXSET=Matrix_jiangxiajinkou_0&TILEMATRIX=16&TILECOL=53546&TILEROW=10881
		std::stringstream ss;
		ss << url << "?SERVICE=WMTS" << "&VERSION=1.0.0" << "&REQUEST=GetTile" << "&LAYER=jiangxiajinkou";
		ss << "&FORMAT=image/tile" << "&TILEMATRIXSET=" << strMatrix;
		ss << "&TILEMATRIX=${Level}" << "&TILECOL=${Col}" << "&TILEROW=${Row}";
		m_mapMapInstance["WYB_WMTS"]->Init(ss.str().c_str(), ptrSpatial);
	}
}



void CMapOnlineViewDlg::OnSelectMap()
{
	// TODO: 在此添加控件通知处理程序代码
	UpdateData();
	CString str;
	m_listTileSrc.GetWindowText(str);
	m_pCurrentMap = m_mapMapInstance[GsString(str.GetBuffer())];
	m_pCurrentMap->Desplay();
	//m_pCurrentMap->Cancel();
	//OnBnClickedRadio();
}


void CMapOnlineViewDlg::OnBnClickedBtnUp()
{
	// TODO: 在此添加控件通知处理程序代码
	m_pCurrentMap->SetScale(CMapInstance::ScaleType::ZOOM_in);
}


void CMapOnlineViewDlg::OnBnClickedBtnDown()
{
	// TODO: 在此添加控件通知处理程序代码
	m_pCurrentMap->SetScale(CMapInstance::ScaleType::ZOOM_out);
}


void CMapOnlineViewDlg::OnBnClickedPan()
{
	// TODO: 在此添加控件通知处理程序代码
	m_isPan = !m_isPan;
}



void CMapOnlineViewDlg::OnLButtonDown(UINT nFlags, CPoint point)
{
	// TODO: 在此添加消息处理程序代码和/或调用默认值
	if (m_mapRect.PtInRect(point))
	{
		if (m_isPan)
		{
			m_lBtnDown = true;
			CPoint mapPoint(point.x - m_mapRect.left, point.y - m_mapRect.top);
			m_pCurrentMap->PanStart(mapPoint);
		}
	}
	CDialogEx::OnLButtonDown(nFlags, point);
}


void CMapOnlineViewDlg::OnLButtonUp(UINT nFlags, CPoint point)
{
	// TODO: 在此添加消息处理程序代码和/或调用默认值
	if (m_mapRect.PtInRect(point))
	{
		m_lBtnDown = false;
		m_pCurrentMap->PanStop();
	}
	CDialogEx::OnLButtonUp(nFlags, point);
}


void CMapOnlineViewDlg::OnMouseMove(UINT nFlags, CPoint point)
{
	// TODO: 在此添加消息处理程序代码和/或调用默认值
	if (m_mapRect.PtInRect(point))
	{
		if (m_lBtnDown)
		{
			CPoint mapPoint(point.x - m_mapRect.left, point.y - m_mapRect.top);
			m_pCurrentMap->PanMoveTo(mapPoint);
		}
	}
	CDialogEx::OnMouseMove(nFlags, point);
}


void CMapOnlineViewDlg::OnBnClickedRadio()
{
	// TODO: 在此添加控件通知处理程序代码
	UpdateData();
}

LRESULT CMapOnlineViewDlg::OnUpdataMap(WPARAM , LPARAM)
{
	m_pCurrentMap->Desplay();
	return 0;
}

void CMapOnlineViewDlg::OnBnClickedTextfeature()
{
	// TODO: 在此添加控件通知处理程序代码
	m_pCurrentMap->DrawTextFeature();
	m_pCurrentMap->Desplay();
	//other->Desplay();


	//m_pCurrentMap->DrawSymbolForTest();
}
