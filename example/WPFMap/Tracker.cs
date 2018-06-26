using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Input;
using GeoStar.Core;
using GeoStar.Core.Control.WPF;

namespace WPFMap
{
    class GeodesicTracker:GeoStar.Core.Control.WPF.NewPolylineTracker
    {
        public GeodesicTracker(GeoStar.Core.Control.WPF.GraphicsDisplay d) : base(d)
        {
            
            m_Sym = new SimpleFillSymbol(System.Drawing.Color.FromArgb(128, System.Drawing.Color.Green),
                System.Drawing.Color.Red,0.1f);
        }
        GeoStar.Core.SimpleFillSymbol m_Sym;
        protected override bool BeforeDraw(GraphicsDisplay disp, RawPoint[] geoline, System.Windows.Point[] line)
        {
            Path p = new Path(geoline);
            Geometry geo  = p.GeodesicBuffer(100000, GeoStar.Core.BufferCapType.eBufferFlatCap, 0.001);
            m_Sym.StartDrawing(disp);
            m_Sym.Draw(geo);
            m_Sym.EndDrawing();
            disp.IncrementCanvasAction();
            return false;
        }
    }
    class NewEnvelopeTracker:GeoStar.Core.Control.WPF.ScreenTracker
    {
        GeoStar.Core.RawPoint m_First = new RawPoint();
        GeoStar.Core.RawPoint m_Second = new RawPoint();
        bool m_bStart = false;
        bool m_bMouseMoved = false;
        public NewEnvelopeTracker(GeoStar.Core.Control.WPF.GraphicsDisplay d):base(d)
        {             
        }
        public override void OnMouseDown(MouseButtonEventArgs e, RawPoint Map, System.Windows.Point screen)
        {
            if (m_bStart) return;
            m_bStart = true;
            m_bMouseMoved = false;
            m_First = Map;
            
        }
        public override void OnMouseMove(MouseEventArgs e, RawPoint Map, System.Windows.Point screen)
        {
            if (!m_bStart) return;
            m_bMouseMoved = true;
            m_Second = Map;
            FireOnNeedUpdate();
        }
        public override void OnDraw(GraphicsDisplay disp)
        {

            if (!m_bStart) return;

            bool bDrawWFP = false;
            if (bDrawWFP)
            {
                //用WPF绘制
                Rect rect = disp.DT.FromMapWPF(Result);
                disp.DrawingContext.DrawRectangle(null, new System.Windows.Media.Pen(System.Windows.Media.Brushes.Blue, 1), rect);
            }
            else
            {
                //如果用内核绘制
                SimpleFillSymbol f = new SimpleFillSymbol(System.Drawing.Color.Transparent, System.Drawing.Color.Blue, 1);
                Canvas c = disp.Canvas;
                f.StartDrawing(disp);
                f.Draw(new Envelope(Result));
                f.EndDrawing();
                disp.IncrementCanvasAction();
            }

        }
        public override void OnMouseUp(MouseButtonEventArgs e, RawPoint Map, System.Windows.Point screen)
        {
            if (!m_bStart) return;
            if (!m_bMouseMoved) return;

            if (null != OnTrackEnvelope)
                OnTrackEnvelope(Result);
            m_bStart = false;
            m_bMouseMoved = false;

        }
        Box Result
        {
            get
            {
                return new Box(Math.Min(m_First.X, m_Second.X), Math.Min(m_First.Y, m_Second.Y),
                            Math.Max(m_First.X, m_Second.X), Math.Max(m_First.Y, m_Second.Y));
            }
        }
        event Action<Box> OnTrackEnvelope;
    }
}
