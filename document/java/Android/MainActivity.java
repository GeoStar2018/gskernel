package com.example.chijing.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.geostar.kernel.*;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.loadLibrary("gsjavaport");
        super.onCreate(savedInstanceState);

        setContentView(new MapView(MainActivity.this));
        //setContentView(R.layout.activity_main);

        try {
            testKernel();
        }catch (Exception ex)
        {
throw ex;
        }
    }

    private  void testKernel()
    {
        Log.v("Test GsKernel","fhf");
        GsConnectProperty conn = new GsConnectProperty();
        //  conn.setServer("/mnt/sdcard/GeoGlobe/tmp/");
        conn.setServer("/mnt/sdcard/tmp/");
        GsESRIFileGeoDatabaseFactory pFac = new GsESRIFileGeoDatabaseFactory();

        GsGeoDatabase pDB = pFac.Open(conn);
        GsStringVector v = new GsStringVector();
        pDB.DataRoomNames(GsDataRoomType.eTileClass,v);
        GsTileClass pTcs = pDB .OpenTileClass("img.tpk");
        GsTileColumnInfo colOnfo =pTcs.TileColumnInfo();
        GsTileCursor pCur =  pTcs.Search(14,14);
        GsTile pTile =  pCur.Next();
        int count =0;
        do {
            if(!GISHelp.IsEmptyTilePtr(pTile))
                break;
            count++;
            long l = pTile.Level();
            long r = pTile.Row();
            long c = pTile.Col();

            byte[] pdata = new byte[pTile.TileDataLength()];
            pTile.TileDataPtr(pdata);
            pTile = pCur.Next();
        }while(!GISHelp.IsEmptyTilePtr(pTile));

        GsSpatialReference d =new GsSpatialReference(4326);
        String spStr =  d.toString();
        GsCoordinateSystem f = d.CoordinateSystem();
        GsAny any = new GsAny(1);
        boolean k =  GsFileSystem.Exists("//usr");
    }
}
