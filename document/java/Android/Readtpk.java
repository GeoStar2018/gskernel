package com.example.chijing.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.geostar.kernel.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.loadLibrary("gsjavaport");
        Log.v("loaderr","fhf");



        GsConnectProperty conn = new GsConnectProperty();
        conn.setServer("/mnt/sdcard/tmp/");
        GsESRIFileGeoDatabaseFactory pFac = new GsESRIFileGeoDatabaseFactory();

        GsGeoDatabase pDB = pFac.Open(conn);
        GsStringVector v = new GsStringVector();
        pDB.DataRoomNames(GsDataRoomType.eTileClass,v);
        GsTileClass pTcs = pDB .OpenTileClass("img.tpk");
        GsTileColumnInfo colOnfo =pTcs.TileColumnInfo();
        GsTileCursor pCur =  pTcs.Search();
        GsTile pTile =  pCur.Next();
        int count =0;
       do {
           if(pTile == null)
               break;
           count++;
           long l = pTile.Level();
           long r = pTile.Row();
           long c = pTile.Col();
       }while(pCur.Next(pTile));

        GsSpatialReference d =new GsSpatialReference(4326);
        String spStr =  d.toString();
        GsCoordinateSystem f = d.CoordinateSystem();
        GsAny any = new GsAny(1);
        boolean k =  GsFileSystem.Exists("//usr");
    }
}
