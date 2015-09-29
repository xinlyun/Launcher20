package com.android.launcher20.activity;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;


public class PosiContentProvider extends ContentProvider {

    private static final String TAG="BookContentProvider";
    private static UriMatcher uriMatcher = null;
    private static final int BOOKS = 1;
    private static final int BOOK = 2;

    private DBHelper dbHelper;
    private SQLiteDatabase db;
    /**
     * 这部分就相当于为外部程序准备好一个所有地址匹配集合
     */
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(IProivderMetaData.AUTHORITY,
                IProivderMetaData.BookTableMetaData.TABLE_NAME, BOOKS);
        uriMatcher.addURI(IProivderMetaData.AUTHORITY,
                IProivderMetaData.BookTableMetaData.TABLE_NAME+"/#", BOOK);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return (dbHelper == null) ? false : true;
    }

    // 取得数据的类型,此方法会在系统进行URI的MIME过滤时被调用。
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case BOOKS:
                return IProivderMetaData.BookTableMetaData.CONTENT_LIST;
            case BOOK:
                return IProivderMetaData.BookTableMetaData.CONTENT_ITEM;
            default:
                throw new IllegalArgumentException("This is a unKnow Uri"
                        + uri.toString());
        }
    }

    //增加
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (uriMatcher.match(uri)) {
            case BOOKS:
                db = dbHelper.getWritableDatabase();//取得数据库操作实例

                //执行添加，返回行号，如果主健字段是自增长的，那么行号会等于主键id
                long rowId = db.insert(
                        IProivderMetaData.BookTableMetaData.TABLE_NAME,
                        IProivderMetaData.BookTableMetaData.POSI_ID, values);
                Uri insertUri = Uri.withAppendedPath(uri, "/" + rowId);
                Log.i(TAG, "insertUri:" + insertUri.toString());
                //发出数据变化通知(book表的数据发生变化)
                getContext().getContentResolver().notifyChange(uri, null);
                return insertUri;
            default:
                //不能识别uri
                throw new IllegalArgumentException("This is a unKnow Uri"
                        + uri.toString());
        }
    }

    //查询
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        db = dbHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case BOOKS:
                return db
                        .query(IProivderMetaData.BookTableMetaData.TABLE_NAME,
                                projection, selection, selectionArgs, null, null,
                                sortOrder);
            case BOOK:
                long id = ContentUris.parseId(uri);
                String where = "_id=" + id;
                if (selection != null && !"".equals(selection)) {
                    where = selection + " and " + where;
                }
                return db.query(IProivderMetaData.BookTableMetaData.TABLE_NAME, projection, where, selectionArgs, null,
                        null, sortOrder);
            default:
                throw new IllegalArgumentException("This is a unKnow Uri"
                        + uri.toString());
        }
    }

    //删除
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case BOOKS:
                return db.delete(IProivderMetaData.BookTableMetaData.TABLE_NAME, selection, selectionArgs);
            case BOOK:
                long id = ContentUris.parseId(uri);
                String where = "_id=" + id;
                if (selection != null && !"".equals(selection)) {
                    where = selection + " and " + where;
                }
                return db.delete(IProivderMetaData.BookTableMetaData.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("This is a unKnow Uri"
                        + uri.toString());
        }
    }

    //更新
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case BOOKS:
                return db.update(IProivderMetaData.BookTableMetaData.TABLE_NAME,
                        values, null, null);
            case BOOK:
                long id = ContentUris.parseId(uri);
                String where = "_id=" + id;
                if (selection != null && !"".equals(selection)) {
                    where = selection + " and " + where;
                }
                return db.update(IProivderMetaData.BookTableMetaData.TABLE_NAME,
                        values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("This is a unKnow Uri"
                        + uri.toString());
        }
    }

}
