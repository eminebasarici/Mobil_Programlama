package com.emine.arasinav;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class Sqlite extends SQLiteOpenHelper {
    public Sqlite(Context context){
        super(context, "rehber.db", null, 1);
    } // veritabnı tanımlama
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table KISILER(k_id integer primary key autoincrement,k_isim text,k_tel text,k_grup text);");// veritabanına tablo tanımı
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS KISILER");
        onCreate(sqLiteDatabase);

    }
}
