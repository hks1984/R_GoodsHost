package com.sarin.prod.goodshost.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.item.DefaultAlarmItem;
import com.sarin.prod.goodshost.item.ProductAlarmItem;
import com.sarin.prod.goodshost.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class DefaultAlarmDatabaseManager {

    private static String TAG = MainApplication.TAG;
    static final String DB_PACKAGE = "dealdive.db";   //DB이름
    static final String TABLE_PACKAGE = "DefaultAlarm"; //Table 이름
    static final int DB_VERSION = 1;			//DB 버전

    Context myContext = null;

    private static DefaultAlarmDatabaseManager myDBManager = null;
    private SQLiteDatabase mydatabase = null;
    private StringUtil sUtil = StringUtil.getInstance();

    public static DefaultAlarmDatabaseManager getInstance(Context context)
    {
        if(myDBManager == null)
        {
            myDBManager = new DefaultAlarmDatabaseManager(context);
        }

        return myDBManager;
    }

    private DefaultAlarmDatabaseManager(Context context)
    {
        myContext = context;

        //DB Open
        mydatabase = context.openOrCreateDatabase(DB_PACKAGE, context.MODE_PRIVATE,null);

        //Table 생성
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PACKAGE +
                "(" + "_id INTEGER PRIMARY KEY AUTOINCREMENT"
                + ", c_date TIMESTAMP "
                + ", default_title TEXT "
                + ", default_body TEXT "
                + ");");
    }

    public long insert(DefaultAlarmItem defaultAlarmItem)
    {
        // 삽입할 데이터 준비
        ContentValues addRowValue = new ContentValues();
        addRowValue.put("c_date", System.currentTimeMillis()); // 현재 시간을 밀리초 단위로 삽입
        addRowValue.put("default_title", defaultAlarmItem.getDefault_title()); // 제품 제목 예시
        addRowValue.put("default_body", defaultAlarmItem.getDefault_body()); // 제품 이름 예시

        // 실패 시 -1 리턴
        return mydatabase.insert(TABLE_PACKAGE, null, addRowValue);

    }

    public long delete(String default_title)
    {
        return mydatabase.delete(TABLE_PACKAGE, "default_title = ?", new String[] {default_title});
    }

    public List<DefaultAlarmItem> selectAll()
    {
        Cursor cursor = mydatabase.rawQuery("SELECT * FROM " + TABLE_PACKAGE + " ORDER BY _id DESC " , null);

        List<DefaultAlarmItem> aList = new ArrayList<>();

        while (cursor.moveToNext()) {
            int i = 0;
            int id = cursor.getInt(i++) ;
            String c_date = cursor.getString(i++) ;
            String default_title = cursor.getString(i++) ;
            String default_body = cursor.getString(i++) ;

            DefaultAlarmItem pai = new DefaultAlarmItem();
            pai.setC_date(c_date);
            pai.setDefault_title(default_title);
            pai.setDefault_body(default_body);

            aList.add(pai);
        }

        return aList;

    }


}
