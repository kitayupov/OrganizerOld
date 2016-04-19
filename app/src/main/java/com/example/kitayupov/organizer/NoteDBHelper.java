package com.example.kitayupov.organizer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class NoteDBHelper extends SQLiteOpenHelper {

    public static final String NOTES_DB = "notes.db";
    public static final String TABLE_NAME = "Notes";
    public static final int VERSION = 1;

    public NoteDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public NoteDBHelper(Context context) {
        this(context, NOTES_DB, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(NotesTable.CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("drop table if exists %s", TABLE_NAME));
        onCreate(db);
    }

    public static class NotesTable implements BaseColumns {

        public static final String CREATE_QUERY = String.format(
                "create table %s (%s integer primary key autoincrement, %s text, %s text," +
                        " %s numeric, %s integer, %s numeric)",
                TABLE_NAME, _ID, MainActivity.BODY, MainActivity.TYPE,
                MainActivity.DATE, MainActivity.RATING, MainActivity.IS_DONE
        );
    }
}
