package com.android.resteassistesprevenu.content_provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class DefaultContentProvider extends ContentProvider {

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		Context context = getContext();  
	    DatabaseHelper dbHelper = new DatabaseHelper(context);  
	    this.ratpDB = dbHelper.getWritableDatabase();  
	    return (this.ratpDB == null) ? false : true;  
	}

	@Override
	public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3,
			String arg4) {
		return null;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		return 0;
	}

	private SQLiteDatabase ratpDB;
	private static final String DATABASE_NAME = "ResteAssisTesPrevenu";
	private static final int DATABASE_VERSION = 1;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE type_ligne" +
					"(id INTEGER PRIMARY KEY AUTOINCREMENT," +
					"type_ligne VARCHAR2 NOT NULL);");
			
			db.execSQL("CREATE TABLE lignes" +
					"(id INTEGER PRIMARY KEY AUTOINCREMENT," +
					" nom VARCHAR2 NOT NULL," +
					" type_ligne VARCHAR2 REFERENCES type_ligne(type_ligne));");
			
			db.execSQL("CREATE TABLE terminus" +
					"(id INTEGER PRIMARY KEY AUTOINCREMENT," +
					" id_ligne INTEGER REFERENCES lignes(id)," +
					" terminus VARCHAR2 NOT NULL);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("Content provider database",
					"Upgrading database from version " + oldVersion + " to "
							+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS titles");
			onCreate(db);

		}

	}
}
