package nl.robenanita.googlemapstest.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import nl.robenanita.googlemapstest.MBTiles.MBTile;

public class MBTilesLocalDataSource {
    private SQLiteDatabase database;
    private UserDBHelper dbHelper;
    private String TAG = "GooglemapsTest";
    private Context context;

    public MBTilesLocalDataSource(Context context) {
        this.context = context;
        dbHelper = new UserDBHelper(context);
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

    public List<MBTile> getAllLocalTiles()
    {
        String query = "SELECT * FROM " + UserDBHelper.MBTILES_LOCAL_TABLE_NAME + ";";
        Cursor cursor = database.rawQuery(query, null);
        ArrayList<MBTile> tiles = new ArrayList<>();

        if (cursor.moveToFirst())
        {
            MBTile tile = CursorToTile(cursor);
            tiles.add(tile);
            cursor.moveToNext();
        }
        return tiles;
    }

    public Boolean checkTile(MBTile tile)
    {
        String query = "SELECT * FROM " + UserDBHelper.MBTILES_LOCAL_TABLE_NAME + " WHERE "
                + UserDBHelper.C_name + "=?;";
        String[] args = {tile.name};
        Cursor cursor = database.rawQuery(query, args);

        return (cursor.getCount()>0);
    }

    public void insertUpdateTile(MBTile tile, Boolean available)
    {
        String query = "";
        String[] args = {};
        if (checkTile(tile))
        {
            query = "UPDATE " + UserDBHelper.MBTILES_LOCAL_TABLE_NAME
                    + " SET " + UserDBHelper.C_url + "=? "
                    + ", " + UserDBHelper.C_version + "=? "
                    + ", " + UserDBHelper.C_start_validity + "=? "
                    + ", " + UserDBHelper.C_end_validity + "=? "
                    + ", " + UserDBHelper.C_available + "=? "
                    + " WHERE " + UserDBHelper.C_name + "=?";

            args = new String[]{tile.mbtileslink, tile.version.toString(), tile.};

        }
        else
        {

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

        return tile;
    }
}
