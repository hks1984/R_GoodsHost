package com.sarin.prod.drinkwater.item;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PackageDatabaseManager {

    static final String DB_PACKAGE = "Package.db";   //DB이름
    static final String TABLE_PACKAGE = "Package"; //Table 이름
    static final int DB_VERSION = 1;			//DB 버전

    Context myContext = null;

    private static PackageDatabaseManager myDBManager = null;
    private SQLiteDatabase mydatabase = null;

    //MovieDatabaseManager 싱글톤 패턴으로 구현
    public static PackageDatabaseManager getInstance(Context context)
    {
        if(myDBManager == null)
        {
            myDBManager = new PackageDatabaseManager(context);
        }

        return myDBManager;
    }

    private PackageDatabaseManager(Context context)
    {
        myContext = context;

        //DB Open
        mydatabase = context.openOrCreateDatabase(DB_PACKAGE, context.MODE_PRIVATE,null);

        //Table 생성
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PACKAGE +
                "(" + "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "package TEXT);");
    }

    public long insert(ContentValues addRowValue)
    {
        return mydatabase.insert(TABLE_PACKAGE, null, addRowValue);
    }

    public long delete(String pkgName)
    {
        return mydatabase.delete(TABLE_PACKAGE, "package = ?", new String[] {pkgName});
    }

    public boolean select(String pkgName)
    {
        String sqlSelect = "SELECT * FROM " + TABLE_PACKAGE  + " WHERE package ='" +  pkgName + "'";
        Cursor cursor = mydatabase.rawQuery(sqlSelect, null);

        boolean rtn = false;
        while (cursor.moveToNext()) {
            // INTEGER로 선언된 첫 번째 "NO" 컬럼 값 가져오기.
            int no = cursor.getInt(0) ;

            rtn = true;
        }

        return rtn;

    }


}
