package com.example.chijing.myapplication;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Matrix;
import com.geostar.kernel.*;
import android.util.AttributeSet;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static android.graphics.Bitmap.Config.ARGB_8888;

public class MapView extends AppCompatImageView
{

    GsDisplayTransformation m_DisplayTrans = null;
    GsTileClass m_Tcls = null;
    GsBox m_Box = new GsBox();
    GsSpatialReference m_Spatial = new GsSpatialReference(4326);
    GsPyramid m_Pyramid =  new GsPyramid();

    HashMap<TileKey,GsTile> m_TileCache = null;
    int mParentWidth = 0,mParentHeight = 0;
    Bitmap m_Superbitmap = null;
    Canvas m_Cansvas = null;
    Paint m_Panit = null;


    void Init()
    {
        m_Panit = new Paint();
        m_Panit.setAntiAlias(true);
        InitCache();

    }
    @Override
    protected void onDraw(Canvas canvas) {
        m_Panit.setColor(Color.BLUE);
        m_Cansvas= canvas;
        DrawTiles();
        super.onDraw(canvas);
    }

    private  void InitCache()
    {
        if(m_Tcls !=null)
            return;
        m_TileCache = new HashMap<TileKey,GsTile>();
        GsConnectProperty conn = new GsConnectProperty();
        //conn.setServer("/mnt/sdcard/GeoGlobe/tmp/");
        conn.setServer("/mnt/sdcard/tmp/");
        GsESRIFileGeoDatabaseFactory pFac = new GsESRIFileGeoDatabaseFactory();

        GsGeoDatabase pDB = pFac.Open(conn);
        GsStringVector v = new GsStringVector();
        pDB.DataRoomNames(GsDataRoomType.eTileClass, v);
        m_Tcls = pDB.OpenTileClass("img");

        m_Box = m_Tcls.TileColumnInfo().getXYDomain();
        m_Spatial = m_Tcls.SpatialReference();
        String WKT = m_Spatial.ExportToWKT();
        m_Pyramid = m_Tcls.Pyramid();

        GsTileCursor pCur =  m_Tcls.Search(14,14);
        GsTile pTile =  pCur.Next();
        int count =0;

        do {
            if(GISHelp.IsEmptyTilePtr(pTile))
                break;
            count++;
            long l = pTile.Level();
            long r = pTile.Row();
            long c = pTile.Col();
            TileKey pkey = new TileKey(l,r,c);

            if(!m_TileCache.containsKey(pkey))
                m_TileCache.put(pkey, pTile);
            pTile = pCur.Next();

        }while(!GISHelp.IsEmptyTilePtr(pTile));
    }

    public MapView(Context context) {
    super(context);
        Init();
}


    public MapView(Context context,  AttributeSet attrs) {
        this(context, attrs, 0);
        Init();
    }

    public MapView(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init();
    }

    protected  void DrawTiles()
    {
        ViewGroup mViewGroup = (ViewGroup) getParent();
        if(null != mViewGroup){
            mParentWidth = mViewGroup.getWidth();
            mParentHeight = mViewGroup.getHeight();
        }
        GsRect rc =  new GsRect(0,0,mParentWidth,mParentHeight);
        m_DisplayTrans = new GsDisplayTransformation(m_Box,rc);

        Rect src =new Rect(0,0,256,256);
        RectF dst = new RectF();
        double [] dblarray = new double[4];
        float [] fr = new float[4];
        for (Map.Entry<TileKey,GsTile> item :m_TileCache.entrySet())
        {
            int l = item.getValue().Level();
            int r = item.getValue().Row();
            int c = item.getValue().Col();

            m_Pyramid.TileExtent(l,r,c,dblarray);

            m_DisplayTrans.FromMap(dblarray,4,2,fr);
            dst.left= fr[0];
            dst.top=  fr[3];
            dst.right= fr[2];
            dst.bottom = fr[1];

            Bitmap bmp = GISHelp.Tile2Bitmap(item.getValue());
            m_Cansvas.drawBitmap(bmp,src,dst,m_Panit);
        }
    }
}
