using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace QuadTreeViewer
{
    /// <summary>
    /// MainWindow.xaml 的交互逻辑
    /// </summary>
    public partial class MainWindow : Window
    {
        public MainWindow()
        {
            InitializeComponent();
            m_GeoSpace = new GeoStar.Core.Control.WPF.GeoSpaceView2D();
            MapContainer.Children.Add(m_GeoSpace);
            m_GeoSpace.MouseMove += M_GeoSpace_MouseMove;
            GeoStar.Core.GlobeConifg.Singleton.Child("GeoSpaceView2D\\ShowTileBound").Value = "false";

            LoadTMS("Tianditu", "http://t${Odd-Even7}.tianditu.com/DataServer?T=vec_c&x=${Col}&y=${Row}&l=${Level}");
            string path = System.IO.Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "quadtree.csv");
            m_Watch = new WatchQuadTree(path);
            m_GeoSpace.LayerBox.AddLayer(m_Watch);
            m_Watch.OnUpdate += M_Watch_OnUpdate;
            m_GeoSpace.ViewFullMap();

        }

        private void M_Watch_OnUpdate()
        {
            m_GeoSpace.NeedUpdate();
            SetTitle(string.Format("显示瓦片数据{0}", m_Watch.TileCount));
        }
        void SetTitle(string str)
        {
            if (this.Dispatcher.Thread.ManagedThreadId != System.Threading.Thread.CurrentThread.ManagedThreadId)
            {
                this.Dispatcher.Invoke(new Action<string>(SetTitle), str);
                return;
            }
            Title = str;
        }

        WatchQuadTree m_Watch;

        private void M_GeoSpace_MouseMove(object sender, MouseEventArgs e)
        {
            Point pt = e.GetPosition(m_GeoSpace);
            GeoStar.Core.RawPoint mappt = m_GeoSpace.Display.DT.ToMap(pt);
            CoordinateLabel.Content = string.Format("x={0},y={1}", mappt.X, mappt.Y);

        }
        void LoadTMS(string strName, string str)
        {
            GeoStar.Core.WebGeoDatabaseFactory fac = new GeoStar.Core.WebGeoDatabaseFactory();
            GeoStar.Core.GeoDatabase gdb = fac.Open(new GeoStar.Core.ConnectProperties());
            GeoStar.Core.SpatialReference sr = new GeoStar.Core.SpatialReference(GeoStar.Core.WellKnownSpatialReference.eWebMercator);
            GeoStar.Core.Pyramid pyramid = 
                GeoStar.Core.Pyramid.WellknownedPyramid(
                    GeoStar.Core.WellknownPyramid.e360DegreePyramid);
            GeoStar.Core.TileColumnInfo col = new GeoStar.Core.TileColumnInfo();
            col.FeatureType = GeoStarCore.geoFEATURETYPE.GEO_FT_TILE_DYNVECTOR;
            col.ValidTopLevel = 0;
            col.ValidBottomLevel = 20;
            col.XYDomain = new GeoStar.Core.Box(pyramid.XMin, pyramid.YMin, pyramid.XMax, pyramid.YMax);
            GeoStar.Core.TMSTileClass tileclass = gdb.CreateTileClass(strName, sr, pyramid, col) as GeoStar.Core.TMSTileClass;
            tileclass.UrlTemplate = str;

            CacheTileClass(tileclass);

            GeoStar.Core.Control.WPF.TileClassGraphicsLayer tilelayer =
                new GeoStar.Core.Control.WPF.TileClassGraphicsLayer(tileclass);
            tilelayer.VectorTile = false;
            
            m_GeoSpace.LayerBox.AddLayer(tilelayer);
        }
        void CacheTileClass(GeoStar.Core.TMSTileClass tms)
        {
            GeoStar.Core.ConnectProperties connn = new GeoStar.Core.ConnectProperties();
            connn.Server = System.IO.Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "TileCache");
            if (!System.IO.Directory.Exists(connn.Server))
                System.IO.Directory.CreateDirectory(connn.Server);
            GeoStar.Core.SqliteDatabaseFactory fac = new GeoStar.Core.SqliteDatabaseFactory();
            GeoStar.Core.GeoDatabase gdb = fac.Open(connn);
            GeoStar.Core.TileClass tileclass = gdb.OpenTileClass(tms.Name) as GeoStar.Core.TileClass;
            if (null == tileclass)
                tileclass = gdb.CreateTileClass(tms.Name, tms.SpatialReference, tms.Pyramid, tms.TileColumnInfo) as GeoStar.Core.TileClass;

            tms.Cache = tileclass;
        }
        GeoStar.Core.Control.WPF.GeoSpaceView2D m_GeoSpace;
    }
}
