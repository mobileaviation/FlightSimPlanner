package nl.robenanita.googlemapstest.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import nl.robenanita.googlemapstest.MBTiles.MBTile;
import nl.robenanita.googlemapstest.MBTiles.MBTileType;

public class MBTilesLocalDataSource {
    private SQLiteDatabase database;
    private UserDBHelper dbHelper;
    private String TAG = "GooglemapsTest";
    private Context context;

    public MBTilesLocalDataSource(Context context) {
        this.context = context;
        dbHelper = UserDBHelper.getInstance(context);
    }

    public void open(){
        try {
            database = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void close() {
        dbHelper.close();
    }

    public List<MBTile> getAllLocalTiles(ArrayList<MBTile> tiles, MBTileType type)
    {
        String query = "SELECT * FROM " + UserDBHelper.MBTILES_LOCAL_TABLE_NAME
                + " WHERE " + UserDBHelper.C_type + "=?";
        Cursor cursor = database.rawQuery(query, new String[]{type.toString()});

        if (tiles==null) tiles = new ArrayList<>();

        if (cursor.moveToFirst())
        {
            while (!cursor.isAfterLast()) {
                MBTile tile = CursorToTile(cursor);
                if (!tiles.contains(tile)) {
                    tiles.add(tile);
                }
                cursor.moveToNext();
            }
        }
        return tiles;
    }

    public ArrayList<MBTile> getVisibleTiles()
    {
        String query = "SELECT * FROM " + UserDBHelper.MBTILES_LOCAL_TABLE_NAME
                + " WHERE " + UserDBHelper.C_visible_order + ">-1 "
                + " ORDER BY " + UserDBHelper.C_visible_order + " ASC" ;
        Cursor cursor = database.rawQuery(query, null);

        ArrayList<MBTile> tiles = new ArrayList<>();

        if (cursor.moveToFirst())
        {
            while (!cursor.isAfterLast()) {
                MBTile tile = CursorToTile(cursor);
                if (!tiles.contains(tile)) {
                    tiles.add(tile);
                }
                cursor.moveToNext();
            }
        }
        return tiles;
    }

    public MBTile getTileByName(MBTile tile)
    {
        Cursor cursor = m_GetCursorByName(tile);

        MBTile m_tile = null;
        if (cursor.moveToFirst()) m_tile = CursorToTile(cursor);

        return m_tile;
    }

    public Boolean checkTile(MBTile tile)
    {
        return (m_GetCursorByName(tile).getCount()>0);
    }

    private Cursor m_GetCursorByName(MBTile tile)
    {
        String query = "SELECT * FROM " + UserDBHelper.MBTILES_LOCAL_TABLE_NAME + " WHERE "
                + UserDBHelper.C_name + "=?";
        String[] args = {tile.name};
        Cursor cursor = database.rawQuery(query, args);
        return cursor;
    }

    public Integer checkVisibleStatusTile(MBTile tile)
    {
        String query = "SELECT " + UserDBHelper.C_visible_order + " FROM " + UserDBHelper.MBTILES_LOCAL_TABLE_NAME + " WHERE "
                + UserDBHelper.C_name + "=?";
        String[] args = {tile.name};
        Cursor cursor = database.rawQuery(query, args);

        if (cursor.getCount()>0)
        {
            cursor.moveToFirst();
            return cursor.getInt(cursor.getColumnIndex(UserDBHelper.C_visible_order));
        }
        else return -1;
    }

    public void insertUpdateTile(MBTile tile)
    {
        if (checkTile(tile))
        {
            database.update(UserDBHelper.MBTILES_LOCAL_TABLE_NAME,
                    TileToContentValue(tile),
                    UserDBHelper.C_name + " =?",
                    new String[]{tile.name});
        }
        else
        {
            database.insert(UserDBHelper.MBTILES_LOCAL_TABLE_NAME, null,
                    TileToContentValue(tile));
        }

    }

    public void updateVisibility(MBTile tile)
    {
        ContentValues values = new ContentValues();
        values.put(UserDBHelper.C_visible_order, tile.visible_order);
        database.update(UserDBHelper.MBTILES_LOCAL_TABLE_NAME,
                values, "_id = ?",
                new String[]{Integer.toString(tile._id)});
    }

    public void updateAvailable(MBTile tile)
    {
        ContentValues values = new ContentValues();
        values.put(UserDBHelper.C_available, tile.available);
        database.update(UserDBHelper.MBTILES_LOCAL_TABLE_NAME,
                values, "_id = ?",
                new String[]{Integer.toString(tile._id)});
    }

    public void updateLocalFile(MBTile tile)
    {
        ContentValues values = new ContentValues();
        values.put(UserDBHelper.C_local_file, tile.local_file);
        database.update(UserDBHelper.MBTILES_LOCAL_TABLE_NAME,
                values, "_id = ?",
                new String[]{Integer.toString(tile._id)});
    }

    public void removeUnknown(List<MBTile> tiles, Boolean remainLocalAvailable, MBTileType type)
    {
        String inTiles = "";
        for (MBTile tile : tiles)
        {
            inTiles = inTiles + "'" + tile.name + "'" + ",";
        }
        if (inTiles.length()>0) {
            inTiles = inTiles.substring(0, inTiles.length()-1);
            String query = "DELETE FROM " + UserDBHelper.MBTILES_LOCAL_TABLE_NAME +
                    " WHERE name not in (" + inTiles + ") AND type = '" + type.toString() + "'";
            if (remainLocalAvailable) query = query + " AND available=0";
            database.execSQL(query);
        }
    }


    private MBTile CursorToTile(Cursor cursor)
    {
        MBTile tile = new MBTile(this.context);

        tile._id = cursor.getInt(cursor.getColumnIndex("_id"));
        tile.name = cursor.getString(cursor.getColumnIndex(UserDBHelper.C_name));
        tile.mbtileslink = cursor.getString(cursor.getColumnIndex(UserDBHelper.C_url));
        tile.version = cursor.getInt(cursor.getColumnIndex(UserDBHelper.C_version));
        tile.setStartValidity(cursor.getInt(cursor.getColumnIndex(UserDBHelper.C_start_validity)));
        tile.setEndValidity(cursor.getInt(cursor.getColumnIndex(UserDBHelper.C_end_validity)));
        tile.setType(cursor.getString(cursor.getColumnIndex(DBHelper.C_type)));
        tile.visible_order = cursor.getInt(cursor.getColumnIndex(UserDBHelper.C_visible_order));
        tile.local_file = cursor.getString(cursor.getColumnIndex(UserDBHelper.C_local_file));
        tile.available = (cursor.getInt(cursor.getColumnIndex(UserDBHelper.C_available))>0);

        return tile;
    }

    private ContentValues TileToContentValue(MBTile tile)
    {
        ContentValues values = new ContentValues();


        values.put(UserDBHelper.C_name, tile.name);
        values.put(UserDBHelper.C_url, tile.mbtileslink);
        values.put(UserDBHelper.C_version, tile.version);
        values.put(UserDBHelper.C_type, tile.type.toString());
        values.put(UserDBHelper.C_start_validity, (long)tile.startValidity.getTime()/1000);
        values.put(UserDBHelper.C_end_validity, (long)tile.endValidity.getTime()/1000);
        values.put(UserDBHelper.C_visible_order, tile.visible_order);
        values.put(UserDBHelper.C_local_file, tile.local_file);
        values.put(UserDBHelper.C_available, tile.available);

        return values;
    }
}
