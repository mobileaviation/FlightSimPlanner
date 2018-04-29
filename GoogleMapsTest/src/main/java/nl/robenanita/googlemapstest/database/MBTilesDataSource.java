package nl.robenanita.googlemapstest.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import nl.robenanita.googlemapstest.MBTiles.MBTile;
import nl.robenanita.googlemapstest.MBTiles.MBTileType;

public class MBTilesDataSource {
    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String TAG = "GooglemapsTest";
    private Context context;

    public MBTilesDataSource(Context context) {
        dbHelper = DBHelper.getInstance(context);
        this.context = context;
    }

    public void open() {
        try
        {
            dbHelper.createDataBase();
            database = dbHelper.openDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        dbHelper.close();
    }

    public ArrayList<MBTile> GetMBTilesByType(MBTileType type, ArrayList<MBTile> tiles)
    {
        String query = "SELECT * FROM " + DBHelper.MBTILES_TABLE_NAME + " WHERE " +
                DBHelper.C_type + "=?;";
        String[] args = {type.toString()};
        Cursor cursor = database.rawQuery(query, args);

        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            tiles.add(CursorToTile(cursor));
            cursor.moveToNext();
        }
        return tiles;
    }

    private MBTile CursorToTile(Cursor cursor)
    {
        MBTile tile = new MBTile(this.context);

        tile._id = cursor.getInt(cursor.getColumnIndex("_id"));
        tile.name = cursor.getString(cursor.getColumnIndex(DBHelper.C_name));
        tile.region = cursor.getString(cursor.getColumnIndex("region"));
        tile.mbtileslink = cursor.getString(cursor.getColumnIndex(DBHelper.C_mbtileslink));
        tile.xmllink = cursor.getString(cursor.getColumnIndex(DBHelper.C_xmllink));
        tile.version = cursor.getInt(cursor.getColumnIndex(DBHelper.C_version));
        tile.setStartValidity(cursor.getInt(cursor.getColumnIndex(DBHelper.C_startValidity)));
        tile.setEndValidity(cursor.getInt(cursor.getColumnIndex(DBHelper.C_endValidity)));
        tile.setType(cursor.getString(cursor.getColumnIndex(DBHelper.C_type)));

        return tile;
    }
}
