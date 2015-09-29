package com.android.launcher20.activity;

import android.net.Uri;
import android.provider.BaseColumns;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper implements IProivderMetaData {

    private static final String TAG = "DBHelper";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String TABLESQL = "create table if not exists "
                + BookTableMetaData.TABLE_NAME + " ("
                + BookTableMetaData.POSI_ID + " integer primary key,"
                + BookTableMetaData.POSI_NAME + " varchar,"
                + BookTableMetaData.POSI_STYLE + " varchar)"
                + BookTableMetaData.POSI_RODE + " VARCHAR";
        db.execSQL(TABLESQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
        onCreate(db);
    }

}


interface IProivderMetaData {

    // 定义外部访问的Authority
    public static final String AUTHORITY = "com.android.launcher20.posiprovider";
    // 数据库名称
    public static final String DB_NAME = "posi.db";
    // 数据库版本
    public static final int VERSION = 1;

    public interface BookTableMetaData extends BaseColumns {
        // 表名
        public static final String TABLE_NAME = "posi";
        // 外部程序访问本表的uri地址
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + AUTHORITY + "/" + TABLE_NAME);

        // book表列名
        public static final String POSI_ID = "_id";
        public static final String POSI_NAME = "name";
        public static final String POSI_STYLE = "style";
        public static final String POSI_RODE = "road";

        //默认排序
        public static final String SORT_ORDER = "_id desc";
        //得到book表中的所有记录
        public static final String CONTENT_LIST = "vnd.android.cursor.dir/vnd.posiprovider.book";
        //得到一个表信息
        public static final String CONTENT_ITEM = "vnd.android.cursor.item/vnd.posiprovider.book";
    }
}