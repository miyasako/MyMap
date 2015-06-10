package com.example.sec.mymap;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;


public class DejaVuContentProvider extends ContentProvider{
    private DatabaseHelper database;

    private static final int DEJAVUS = 10;
    private static final int DEJAVU_ID = 20;
    private static final String AUTHORITY = "com.example.sec.mymap.DejaVuContentProvider";

    private static final String BASE_PATH = "dejavus";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/dejavus";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/dejavu";


    private static final UriMatcher urimatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        urimatcher.addURI(AUTHORITY,BASE_PATH,DEJAVUS);
        urimatcher.addURI(AUTHORITY,BASE_PATH + "/#",DEJAVU_ID);
    }

    //コンテンツプロバイダの作成
    @Override
    public boolean onCreate() {
        database = new DatabaseHelper(getContext());
        return  true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(DatabaseHelper.TABLE_DEJAVU);

        int uriType = urimatcher.match(uri);
        switch (uriType){
            case DEJAVUS:
                break;
            case DEJAVU_ID:
                queryBuilder.appendWhere(DatabaseHelper.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db,projection,selection, selectionArgs,null,null,sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = urimatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        long id = 0;
        switch (uriType){
            case DEJAVUS :
            id = sqlDB.insert(DatabaseHelper.TABLE_DEJAVU,null,values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
