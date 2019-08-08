package com.example.chijing.myapplication;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.geostar.kernel.GsRow;
import com.geostar.kernel.GsTile;

import static android.graphics.Bitmap.Config.ARGB_8888;

class TileKey
{
    long l = 0;
    long r = 0;
    long c = 0;
    public TileKey(long ll, long rr, long cc) {
        l = ll; r = rr; c =cc;
    }
}
public class GISHelp {

    public static   boolean IsEmptyTilePtr(GsTile pTile)
    {
        if(pTile == null || GsTile.getCPtr(pTile) == 0)
            return false;
        GsRow pRow = pTile;
        return GsRow.getCPtr(pRow) == 0;
    }

    public static Bitmap Tile2Bitmap(GsTile pTile)
    {
        if(pTile ==null && GsTile.getCPtr(pTile)==0)
        {
            return Bitmap.createBitmap(256,256,ARGB_8888);
        }
        byte[] pdata = new byte[pTile.TileDataLength()];
        pTile.TileDataPtr(pdata);
        return BitmapFactory.decodeByteArray(pdata,0,pTile.TileDataLength());
    }
}
