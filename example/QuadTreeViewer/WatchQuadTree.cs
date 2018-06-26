using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using GeoStar.Core;
using GeoStar.Core.Control.WPF;
using GeoStarCore;

namespace QuadTreeViewer
{
    class WatchQuadTree:GeoStar.Core.Control.WPF.GraphicsLayer
    { 
        string m_strFile;
        public WatchQuadTree(string strFile)
        { 
            m_strFile = strFile;
            PrepareWatch(m_strFile);
            if(System.IO.File.Exists(m_strFile))
                UpdateQuadTree(m_strFile);

        }
        public event Action OnUpdate;
        public int TileCount
        {
            get
            {
                lock(this)
                {
                    return m_TileList.Count;
                }
            }
        }
        protected override void InnerDraw(GraphicsDisplay display, GeoStar.Core.TrackCancel cancel)
        {
             if (m_TileList.Count == 0)
                return;

            List<QuadTile> vList = new List<QuadTile>();
            lock (this)
            {
                vList.AddRange(m_TileList);
            }
            System.Windows.Media.DrawingContext dc = display.DrawingContext;
            foreach (var item in vList)
            {
                Box box = m_Pyramid.TileExtent(item.Level, item.Row, item.Col);
                System.Windows.Rect rect = display.DT.FromMapWPF(box);

                dc.DrawRectangle(null,
                    new System.Windows.Media.Pen(System.Windows.Media.Brushes.Red, 1), rect);

                System.Windows.Media.FormattedText   formattedText = new System.Windows.Media.FormattedText(
                 string.Format(" {0},{1},{2}",item.Level,item.Row,item.Col),
                 System.Globalization.CultureInfo.CurrentCulture,
                 System.Windows.FlowDirection.LeftToRight,
                 new  System.Windows.Media.Typeface("微软雅黑"),
                 12,
                 System.Windows.Media.Brushes.Black);
                

                dc.DrawText(formattedText, rect.Location);

            }

        }
        protected override void InnerDraw(GeoStarCore.geoDrawPhase drawPhase, GeoStarCore.IDisplay pDisplay, GeoStarCore.ITrackCancel pTrack)
        { 
             
            if (m_TileList.Count == 0)
                return;
            List<QuadTile> vList = new List<QuadTile>();
            lock (this)
            {
                vList.AddRange(m_TileList);
            }
            
            GeoStar.Core.SimpleFillSymbol sym = new GeoStar.Core.SimpleFillSymbol(
                System.Drawing.Color.Transparent,
                System.Drawing.Color.Red, 0.1f);
            sym.StartDrawing(pDisplay as GeoStar.Core.Display);
            GeoStar.Core.Envelope env = new GeoStar.Core.Envelope();
            double xmin = 0, ymin = 0, xmax = 0, ymax = 0;
            GeoStar.Core.TextSymbol txt = new GeoStar.Core.TextSymbol();
            GeoStar.Core.Point pt = new GeoStar.Core.Point();
            txt.Width = 5;
            txt.Height = 5;

            txt.StartDrawing(pDisplay as GeoStar.Core.Display);
           
            foreach (var item in vList)
            {
                m_Pyramid.TileExtent(item.Level, item.Row, item.Col, ref xmin, ref ymin, ref xmax, ref ymax);
                env.put_Coords(xmin, ymin, xmax, ymax);
                sym.Draw(env);
                pt.put_Coords(xmin , (ymin + ymax) / 2);
                txt.Text = item.ToString();
                txt.Draw(pt);

            }
            txt.EndDrawing();
            sym.EndDrawing(); 
        }
        public override IEnvelope Extent
        {
            get
            {
                return new GeoStar.Core.Envelope(-180, -90, 180, 90);
            }
        }
        void PrepareWatch(string strFile)
        {
            System.IO.FileInfo f = new System.IO.FileInfo(strFile);

            m_Watch = new System.IO.FileSystemWatcher(
                f.Directory.FullName,f.Name);

            m_Watch.Changed += M_Watch_Changed;
            m_Watch.Created += M_Watch_Changed;
            //m_Watch.WaitForChanged(System.IO.WatcherChangeTypes.All);
            m_Watch.EnableRaisingEvents = true;
        }

        private void M_Watch_Changed(object sender, System.IO.FileSystemEventArgs e)
        {
            UpdateQuadTree(m_strFile);
        }
        System.IO.StreamReader SafeOpen(string str)
        {
            System.IO.StreamReader r = null;
            try
            {
                r = new System.IO.StreamReader(str, true);
                return r;
            }
            catch
            {
                return null;
            }

        }
        List<QuadTile> SafeRead(string strFile)
        {
            System.IO.StreamReader r = null;
            while (true)
            {
                r = SafeOpen(strFile);
                if (r != null)
                    break;
            }
            using(r)
            {

                List<QuadTile> vList = new List<QuadTile>();
                while (!r.EndOfStream)
                {
                    string line = r.ReadLine();
                    if (string.IsNullOrEmpty(line))
                        continue;
                    string[] arr = line.Split(',');
                    if (arr.Length < 3)
                        continue;
                    int l, row, c;
                    if (!int.TryParse(arr[0], out l) ||
                        !int.TryParse(arr[1], out row) ||
                        !int.TryParse(arr[2], out c))
                        continue;
                    vList.Add(new QuadTile(l, row, c));
                }
                return vList;
            }
        }
        void UpdateQuadTree(string strFile)
        {
            m_TileList.Clear();
            if (!System.IO.File.Exists(strFile))
                return;
            List<QuadTile> vList = SafeRead(strFile);
            if (vList.Count == 0)
                return;
            lock (this)
            {
                m_TileList = vList;
            }
            if (null != OnUpdate)
                OnUpdate();
        }
        List<QuadTile> m_TileList = new List<QuadTile>();

        GeoStar.Core.Pyramid m_Pyramid = new GeoStar.Core.MultiPyramid();
         
        System.IO.FileSystemWatcher m_Watch;

    }
    struct QuadTile
    {
        public int Level, Row, Col;
        public QuadTile(int l,int r,int c)
        {
            Level = l;
            Row = r;
            Col = c;
        }
        public override string ToString()
        {
            return string.Format("{0},{1},{2}", Level, Row, Col);
        }
    }
}
