package bf22wk.brocku.translatorapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class DataHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "translator_app.db";
    public static final int DATABASE_VERSION = 3;
    public static final String TABLE_RECENT = "recent_translations";
    public static final String TABLE_FAVOURITE = "favourite_translations";
    public static final String ID = "_id";
    public static final String ORIGINTEXT= "origintext";
    public static final  String SOURCE_LANGUAGE= "source_language";
    public static final String TARGET_LANGUAGE = "target_language";
    public static final String TRANSLATED_TEXT= "text";
    public static final  String CREATERECENT =
            "CREATE TABLE " + TABLE_RECENT + "( " +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SOURCE_LANGUAGE + " TEXT, " +
                    ORIGINTEXT + " TEXT, " +
                    TARGET_LANGUAGE + " TEXT, " +
                    TRANSLATED_TEXT + " TEXT) ";

    public static final String CREATEFAVOURITES =

            "CREATE TABLE " + TABLE_FAVOURITE + "( " +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SOURCE_LANGUAGE + " TEXT, " +
                    ORIGINTEXT + " TEXT, " +
                    TARGET_LANGUAGE + " TEXT, " +
                    TRANSLATED_TEXT + " TEXT) ";

    public DataHelper( Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create Both Tables
        db.execSQL(CREATERECENT);
        db.execSQL(CREATEFAVOURITES);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITE);
        onCreate(db);
    }

    public void removeAllRecents() {
        SQLiteDatabase db =this.getWritableDatabase();
        db.delete(TABLE_RECENT, null, null);
    }

    public void insertRECENT(String source, String target, String text, String translated_text){

        SQLiteDatabase db =this.getWritableDatabase();

        ContentValues inputValues = new ContentValues();
        inputValues.put(DataHelper.SOURCE_LANGUAGE,source);
        inputValues.put(DataHelper.ORIGINTEXT,text);
        inputValues.put(DataHelper.TARGET_LANGUAGE,target);
        inputValues.put(DataHelper.TRANSLATED_TEXT,translated_text);
        long stauts = db.insert(TABLE_RECENT,null,inputValues);
        System.out.println(stauts);
        db.close();

    }

    public void insertFAVOURITE(String source, String target, String text, String translated_text){

        SQLiteDatabase db =this.getWritableDatabase();

        ContentValues inputValues = new ContentValues();
        inputValues.put(DataHelper.SOURCE_LANGUAGE,source);
        inputValues.put(DataHelper.ORIGINTEXT,text);
        inputValues.put(DataHelper.TARGET_LANGUAGE,target);
        inputValues.put(DataHelper.TRANSLATED_TEXT,translated_text);
        db.insert(TABLE_FAVOURITE,null,inputValues);
        db.close();
    }

    public List<translate> FetchRecent(){
        List< translate> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM "  + TABLE_RECENT, null);


        try {
            if(cursor.moveToFirst())
                do {
                    @SuppressLint("Range") translate t = new translate(
                            cursor.getString(cursor.getColumnIndex(SOURCE_LANGUAGE)),
                            cursor.getString(cursor.getColumnIndex(TARGET_LANGUAGE)),
                            cursor.getString(cursor.getColumnIndex(ORIGINTEXT)),
                            cursor.getString(cursor.getColumnIndex(TRANSLATED_TEXT))

                    );
                    list.add(t);
            } while (cursor.moveToNext());
        } finally {
            cursor.close();
        }

        return list;
    }

    public List<translate> FetchFavourite(){
        List< translate> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM "  + TABLE_FAVOURITE, null);

        try {
            if(cursor.moveToFirst())
                do {
                    @SuppressLint("Range") translate t = new translate(
                            cursor.getString(cursor.getColumnIndex(SOURCE_LANGUAGE)),
                            cursor.getString(cursor.getColumnIndex(TARGET_LANGUAGE)),
                            cursor.getString(cursor.getColumnIndex(ORIGINTEXT)),
                            cursor.getString(cursor.getColumnIndex(TRANSLATED_TEXT))

                    );
                    list.add(t);
                } while (cursor.moveToNext());
        } finally {
            cursor.close();
        }

        return list;
    }
}


