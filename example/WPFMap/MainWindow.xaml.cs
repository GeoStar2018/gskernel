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

namespace WPFMap
{  
    /// <summary>
    /// MainWindow.xaml 的交互逻辑
    /// </summary>
    public partial class MainWindow : Window
    {
        public MainWindow()
        {
            InitializeComponent();



            GeoStar.Core.Kernel.Initialize();
            double[] arr = new double[] {
            108.562396006656,25.3444259567388,
            115.71846921797,28.1557404326123,
            114.35540765391,31.2226289517471,
            108.647587354409,32.6708818635607,
                108.051247920133,30.2855241264559};

            GeoStar.Core.GeodesicPolygon p = new GeoStar.Core.GeodesicPolygon(new GeoStar.Core.SpatialReference(GeoStar.Core.WellKnownSpatialReference.eWGS84));
           
            GeoStar.Core.Ring r = new GeoStar.Core.Ring(arr, 2);
            double a = r.GeodesicArea(p);

            p = new GeoStar.Core.GeodesicPolygon(new GeoStar.Core.SpatialReference(GeoStar.Core.WellKnownSpatialReference.eWGS84));
            for (int i = 0; i < arr.Length; i += 2)
            {
                p.AddPoint(arr[i], arr[i + 1]);
            }
            double area = 0;
            uint n = p.Compute(false, true, ref area);
            
            m_GeoSpace = new GeoStar.Core.Control.WPF.GeoSpaceView2D();
            MapContainer.Children.Add(m_GeoSpace);
            LoadMap(@"d:\400w\400wfull.GMAPX");
            // //LoadMap(@"D:\400w\配图和数据\徐州配图_fcs_4326\xz.GMAPX");

            // //LoadMap();
            // //LoadFCS(@"D:\400w\gadmfcs",true,false);
            // //LoadFCS(@"D:\400w\400fcs",false,false);

            // //LoadTMS("OSM", "http://${ThirdChar}.tile.openstreetmap.org/${Level}/${Col}/${Row}.png");
            //// m_GeoSpace.ViewFullMap();
            m_GeoSpace.MouseMove += M_GeoSpace_MouseMove;
        }
        protected override void OnKeyDown(KeyEventArgs e)
        {
            base.OnKeyDown(e);
        }
        void LoadTMS(string strName,string str)
        {
            GeoStar.Core.WebGeoDatabaseFactory fac = new GeoStar.Core.WebGeoDatabaseFactory();
            GeoStar.Core.GeoDatabase gdb = fac.Open(new GeoStar.Core.ConnectProperties());
            GeoStar.Core.SpatialReference sr = new GeoStar.Core.SpatialReference(GeoStar.Core.WellKnownSpatialReference.eWebMercator);
            GeoStar.Core.Pyramid pyramid = GeoStar.Core.Pyramid.WellknownedPyramid(GeoStar.Core.WellknownPyramid.eWebMercatorPyramid);
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
            m_GeoSpace.LayerBox.AddLayer(tilelayer);
        }
        MediaPlayer player;
        VideoDrawing aVideoDrawing;
        protected override void OnRender(DrawingContext drawingContext)
        {

            if (null == player)
            {
                player = new MediaPlayer();

                player.Open(new Uri(@"file://e:\new.wmv", UriKind.Absolute));

                aVideoDrawing = new VideoDrawing();

                aVideoDrawing.Rect = new Rect(0, 0, 100, 100);

                aVideoDrawing.Player = player;

                // Play the video once.
                player.Play();
            }
            base.OnRender(drawingContext);
           /// drawingContext.DrawDrawing(aVideoDrawing);


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
        private void M_GeoSpace_MouseMove(object sender, MouseEventArgs e)
        {
            Point pt = e.GetPosition(m_GeoSpace);
            double mapx = 0, mapy = 0;
            m_GeoSpace.Display.DT.ToMap(Convert.ToSingle(pt.X), Convert.ToSingle(pt.Y), ref mapx, ref mapy);
            CoordinateLabel.Content = string.Format("x-={0},y={1}", mapx, mapy);
        }
        
        protected override void OnRenderSizeChanged(SizeChangedInfo sizeInfo)
        { 
            base.OnRenderSizeChanged(sizeInfo);
        }
        void LoadFCS(string str,bool bSingle,bool bVec)
        {
            StringBuilder hashStr = new StringBuilder();
            hashStr.Append(str);

            GeoStar.Core.Map map = new GeoStar.Core.Map();
            GeoStar.Core.SqliteDatabaseFactory fac = new GeoStar.Core.SqliteDatabaseFactory();
            GeoStar.Core.ConnectProperties conn = new GeoStar.Core.ConnectProperties();
            conn.Server = str;
            GeoStar.Core.GeoDatabase gdb = fac.Open(conn);
            foreach (var item in gdb.DataRoomName(GeoStarCore.geoDataRoomType.GEO_DATAROOM_FEATURECLASS))
            {
                if(bSingle)
                {
                    hashStr = new StringBuilder();
                    hashStr.Append(str);
                    map = new GeoStar.Core.Map();
                }
                GeoStar.Core.FeatureClass fcs = gdb.OpenFeatureClass(item) as GeoStar.Core.FeatureClass;
                GeoStar.Core.FeatureLayer lyr = new GeoStar.Core.FeatureLayer(fcs);
                map.AddLayer(lyr);
                hashStr.Append(item);
                if(bSingle)
                {
                    GeoStar.Core.Control.WPF.BaseMapGraphicsLayer basem = new GeoStar.Core.Control.WPF.BaseMapGraphicsLayer(
                map, new GeoStar.Core.Pyramid());
                    basem.VectorTile = bVec;
                    basem.DataHash = hashStr.ToString().GetHashCode();
                    m_GeoSpace.LayerBox.AddLayer(basem);
                }
            }
            if (bSingle)
                return;

            GeoStar.Core.Control.WPF.BaseMapGraphicsLayer baseMap = new GeoStar.Core.Control.WPF.BaseMapGraphicsLayer(
                map, new GeoStar.Core.Pyramid());
            baseMap.VectorTile = bVec;
            baseMap.DataHash = hashStr.ToString().GetHashCode();
            m_GeoSpace.LayerBox.AddLayer(baseMap);
        }
        void LoadMap(string str)
        {
            GeoStar.Core.Map mp = new GeoStar.Core.Map(str);
            GeoStar.Core.Control.WPF.BaseMapGraphicsLayer baseMap = new GeoStar.Core.Control.WPF.BaseMapGraphicsLayer(
                mp, new GeoStar.Core.Pyramid());
            baseMap.VectorTile = false;
            baseMap.DataHash = str.GetHashCode();
            m_GeoSpace.LayerBox.AddLayer(baseMap);
        }
        void LoadMap()
        {
            GeoStar.Core.SqliteDatabaseFactory fac = new GeoStar.Core.SqliteDatabaseFactory();
            GeoStar.Core.ConnectProperties conn = new GeoStar.Core.ConnectProperties();
            conn.Server = @"D:\02-Work\8888-GeoStarKernel\GPS\data";
            GeoStar.Core.GeoDatabase gdb = fac.Open(conn);
            GeoStar.Core.TileClass tileclass = gdb.OpenTileClass("shenzhenImage")  as GeoStar.Core.TileClass;
            GeoStar.Core.Control.WPF.TileClassGraphicsLayer tilelayer = 
                new GeoStar.Core.Control.WPF.TileClassGraphicsLayer(tileclass);
            m_GeoSpace.LayerBox.AddLayer(tilelayer);

        }
        GeoStar.Core.Control.WPF.GeoSpaceView2D m_GeoSpace;
        private void Button_Open(object sender, RoutedEventArgs e)
        {
            using (System.Windows.Forms.FolderBrowserDialog f = new System.Windows.Forms.FolderBrowserDialog())
            {
                if (f.ShowDialog() != System.Windows.Forms.DialogResult.OK)
                    return;

                LoadFCS(f.SelectedPath, false, false);

            }
            m_GeoSpace.ViewFullMap();

        }
        private void Button_Click(object sender, RoutedEventArgs e)
        {

            //m_GeoSpace.ScreenDrawBox.PushTracker(new NewEnvelopeTracker(m_GeoSpace.Display));

            m_GeoSpace.ScreenDrawBox.PushTracker(new  GeoStar.Core.Control.WPF.NewPolylineTracker(m_GeoSpace.Display));

            //m_GeoSpace.ViewZoomEnv();

        }
    }  
     
}
