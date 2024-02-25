package com.sarin.prod.goodshost.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.item.ProductAlarmItem;
import com.sarin.prod.goodshost.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class AlarmDatabaseManager {

    private static String TAG = MainApplication.TAG;
    static final String DB_PACKAGE = "alarm.db";   //DB이름
    static final String TABLE_PACKAGE = "DealDive"; //Table 이름
    static final int DB_VERSION = 1;			//DB 버전

    Context myContext = null;

    private static AlarmDatabaseManager myDBManager = null;
    private SQLiteDatabase mydatabase = null;
    private StringUtil sUtil = StringUtil.getInstance();

    public static AlarmDatabaseManager getInstance(Context context)
    {
        if(myDBManager == null)
        {
            myDBManager = new AlarmDatabaseManager(context);
        }

        return myDBManager;
    }

    private AlarmDatabaseManager(Context context)
    {
        myContext = context;

        //DB Open
        mydatabase = context.openOrCreateDatabase(DB_PACKAGE, context.MODE_PRIVATE,null);

        //Table 생성
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PACKAGE +
                "(" + "_id INTEGER PRIMARY KEY AUTOINCREMENT"
                + ", c_date TIMESTAMP "
                + ", product_title TEXT "
                + ", product_name TEXT "
                + ", product_image TEXT "
                + ");");
    }

    public long insert(ProductAlarmItem productAlarmItem)
    {
        // 삽입할 데이터 준비
        ContentValues addRowValue = new ContentValues();
        addRowValue.put("c_date", System.currentTimeMillis()); // 현재 시간을 밀리초 단위로 삽입
        addRowValue.put("product_title", productAlarmItem.getProduct_title()); // 제품 제목 예시
        addRowValue.put("product_name", productAlarmItem.getProduct_name()); // 제품 이름 예시
        addRowValue.put("product_image", productAlarmItem.getProduct_image()); // 제품 URL 예시

        // 실패 시 -1 리턴
        return mydatabase.insert(TABLE_PACKAGE, null, addRowValue);

    }

    public long delete(String pkgName)
    {
        return mydatabase.delete(TABLE_PACKAGE, "package = ?", new String[] {pkgName});
    }

    public List<ProductAlarmItem> selectAll()
    {
        Cursor cursor = mydatabase.rawQuery("SELECT * FROM " + TABLE_PACKAGE + " ORDER BY _id DESC " , null);

        List<ProductAlarmItem> aList = new ArrayList<>();

        while (cursor.moveToNext()) {
            int i = 0;
            int id = cursor.getInt(i++) ;
            String c_date = cursor.getString(i++) ;
            String product_title = cursor.getString(i++) ;
            String product_name = cursor.getString(i++) ;
            String product_image = cursor.getString(i++) ;

            ProductAlarmItem pai = new ProductAlarmItem();
            pai.setC_date(c_date);
            pai.setProduct_title(product_title);
            pai.setProduct_name(product_name);
            pai.setProduct_image(product_image);

            Log.d(TAG, pai.toString());
            aList.add(pai);
        }

        return aList;

    }


}
