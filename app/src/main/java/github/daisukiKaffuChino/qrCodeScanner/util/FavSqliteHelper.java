package github.daisukiKaffuChino.qrCodeScanner.util;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import github.daisukiKaffuChino.qrCodeScanner.bean.FavBean;

public class FavSqliteHelper extends SQLiteOpenHelper {
    private final SQLiteDatabase sqLiteDatabase = getWritableDatabase();

    @Override
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
    }

    public FavSqliteHelper(Context context) {
        super(context, "FAV_DB", (SQLiteDatabase.CursorFactory) null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("create table data(id integer primary key autoincrement,title text,content text,img text,time long)");
    }

    public boolean insertData(String str, String str2, String str3, long str4) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", str);
        contentValues.put("content", str2);
        contentValues.put("img", str3);
        contentValues.put("time", str4);
        return this.sqLiteDatabase.insert("data", null, contentValues) > 0;
    }

    public boolean deleteData(String str) {
        return this.sqLiteDatabase.delete("data", "id=?", new String[]{String.valueOf(str)}) > 0;
    }

    public void deleteAllData(){
        sqLiteDatabase.delete("data", null, null);
    }

    @SuppressLint("Range")
    public ArrayList<FavBean> query() {
        ArrayList<FavBean> arrayList = new ArrayList<>();
        Cursor query = this.sqLiteDatabase.query("data", null, null, null, null, null, "id desc");
        if (query != null) {
            while (query.moveToNext()) {
                FavBean bean = new FavBean();
                bean.setId(String.valueOf(query.getInt(query.getColumnIndex("id"))));
                bean.setTitle(query.getString(query.getColumnIndex("title")));
                bean.setContent(query.getString(query.getColumnIndex("content")));
                bean.setImg(query.getString(query.getColumnIndex("img")));
                bean.setTime(query.getLong(query.getColumnIndex("time")));
                arrayList.add(bean);
            }
            query.close();
        }
        return arrayList;
    }
}
