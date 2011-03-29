package com.resteassistesprevenu.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.resteassistesprevenu.R;

public class DefaultContentProvider extends ContentProvider {

	public static final String PROVIDER_NAME = "com.resteassistesprevenu.provider";
	public static final String CONTENT_URI = "content://" + PROVIDER_NAME;

	private static final int TYPE_LIGNES = 1;
	private static final int LIGNES = 2;
	private static final int LIGNES_ID = 3;
	private static final int FAVORIS = 4;
	private static final int FAVORIS_ID = 5;

	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(PROVIDER_NAME, "type_lignes", TYPE_LIGNES);
		uriMatcher.addURI(PROVIDER_NAME, "favoris", FAVORIS);
		uriMatcher.addURI(PROVIDER_NAME, "favoris/#", FAVORIS_ID);
		uriMatcher.addURI(PROVIDER_NAME, "lignes", LIGNES);
		uriMatcher.addURI(PROVIDER_NAME, "lignes/#", LIGNES_ID);
	}

	private DatabaseHelper dbHelper;

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case LIGNES:
			return "vnd.android.cursor.dir/vnd.resteassistesprevenu.lignes";
		case LIGNES_ID:
			return "vnd.android.cursor.item/vnd.resteassistesprevenu.lignes";
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		Context context = getContext();
		this.dbHelper = new DatabaseHelper(context);
		this.ratpDB = dbHelper.getWritableDatabase();
		return (this.ratpDB == null) ? false : true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();

		switch (uriMatcher.match(uri)) {
		case TYPE_LIGNES:
			qb.setTables("type_ligne");
			break;
		case LIGNES:
			qb.setTables("lignes INNER JOIN type_ligne ON (lignes.id_type_ligne=type_ligne._id)");
			break;
		case LIGNES_ID:
			qb.setTables("lignes INNER JOIN type_ligne ON (lignes.id_type_ligne=type_ligne._id)");
			qb.appendWhere("lignes._id = " + uri.getPathSegments().get(1));
			break;
		case FAVORIS:
			qb.setTables("lignes INNER JOIN type_ligne ON (lignes.id_type_ligne=type_ligne._id)");
			qb.appendWhere("isFavoris = 1");
			break;
		}

		Cursor c = qb.query(db, projection, selection, null, null, null,
				sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);

		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count = 0;
		switch (uriMatcher.match(uri)) {
		case FAVORIS_ID:
			count = this.dbHelper.getWritableDatabase().update(
					LigneBaseColumns.NOM_TABLE, values,
					LigneBaseColumns._ID + "=" + uri.getPathSegments().get(1),
					selectionArgs);
			break;
		}

		getContext().getContentResolver().notifyChange(uri, null);
		
		return count;
	}

	private SQLiteDatabase ratpDB;
	private static final String DATABASE_NAME = "ResteAssisTesPrevenu";
	private static final int DATABASE_VERSION = 2;

	private class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			createTables(db);

			initializeData(db);
		}

		private void initializeData(SQLiteDatabase db) {
			String[] reqsInsertTypeLignes = getContext().getString(
					R.string.req_insert_type_ligne).split(";");
			for (String reqInsertTypeLigne : reqsInsertTypeLignes) {
				db.execSQL(reqInsertTypeLigne);
			}

			String[] reqsInsertLignes = getContext().getString(
					R.string.req_insert_lignes).split(";");
			for (String reqInsertLigne : reqsInsertLignes) {
				db.execSQL(reqInsertLigne);
			}
		}

		private void createTables(SQLiteDatabase db) {
			db.execSQL(getContext().getString(
					R.string.req_create_table_type_ligne));

			db.execSQL(getContext().getString(R.string.req_create_table_lignes));

			db.execSQL(getContext().getString(
					R.string.req_create_table_terminus));
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("Content provider database",
					"Upgrading database from version " + oldVersion + " to "
							+ newVersion + ", which will destroy all old data");
			dropTables(db);
			onCreate(db);
		}

		private void dropTables(SQLiteDatabase db) {
			db.execSQL("DROP TABLE IF EXISTS terminus");
			db.execSQL("DROP TABLE IF EXISTS lignes");
			db.execSQL("DROP TABLE IF EXISTS type_ligne");
		}

	}
}
