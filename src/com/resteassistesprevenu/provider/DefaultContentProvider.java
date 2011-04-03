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

/**
 * Provider fournissant les données contenues dans la base
 * @author Arnaud
 *
 */
public class DefaultContentProvider extends ContentProvider {
	private final static String TAG_PROVIDER = "DefaultContentProvider";
	
	/**
	 * Nom du provider
	 */
	public static final String PROVIDER_NAME = "com.resteassistesprevenu.provider";
	
	/**
	 * URL du provider
	 */
	public static final String CONTENT_URI = "content://" + PROVIDER_NAME;

	/**
	 * Identifiant de l'URL de récupération des types de ligne
	 */
	private static final int TYPE_LIGNES = 1;
	
	/**
	 * Identifiant de l'URL de récupération des lignes
	 */
	private static final int LIGNES = 2;
	
	/**
	 * Identifiant de l'URL de récupération d'une ligne
	 */
	private static final int LIGNES_ID = 3;
	
	/**
	 * Identifiant de l'URL de récupération des favoris
	 */
	private static final int FAVORIS = 4;
	
	/**
	 * Identifiant de l'URL de récupération d'enregistrement ou de suppression d'un favoris
	 */
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

	/**
	 * Helper pour la base
	 */
	private DatabaseHelper dbHelper;

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		throw new UnsupportedOperationException("Not implemented yet");
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
		throw new UnsupportedOperationException("Not implemented yet");
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
			Log.d(getContext().getString(R.string.log_tag_name) + " " + TAG_PROVIDER, "Début récupération type lignes");
			qb.setTables("type_ligne");
			break;
		case LIGNES:
			Log.d(getContext().getString(R.string.log_tag_name) + " " + TAG_PROVIDER, "Début récupération lignes");
			qb.setTables("lignes INNER JOIN type_ligne ON (lignes.id_type_ligne=type_ligne._id)");
			break;
		case LIGNES_ID:
			Log.d(getContext().getString(R.string.log_tag_name) + " " + TAG_PROVIDER, "Début récupération d'une ligne");
			qb.setTables("lignes INNER JOIN type_ligne ON (lignes.id_type_ligne=type_ligne._id)");
			qb.appendWhere("lignes._id = " + uri.getPathSegments().get(1));
			break;
		case FAVORIS:
			Log.d(getContext().getString(R.string.log_tag_name) + " " + TAG_PROVIDER, "Début récupération favoris");
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
			Log.d(getContext().getString(R.string.log_tag_name) + " " + TAG_PROVIDER, "Début mise à jour d'un favoris : " + uri.getPathSegments().get(1));
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
	
	/**
	 * Nom de la base
	 */
	private static final String DATABASE_NAME = "ResteAssisTesPrevenu";
	
	/**
	 * Version de la base
	 */
	private static final int DATABASE_VERSION = 3;

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
			Log.i(getContext().getString(R.string.log_tag_name) + " " + TAG_PROVIDER, "Début peuplement de la base.");
			String[] reqsInsertTypeLignes = getContext().getString(
					R.string.req_insert_type_ligne).split(";");
			for (String reqInsertTypeLigne : reqsInsertTypeLignes) {
				Log.d(getContext().getString(R.string.log_tag_name) + " " + TAG_PROVIDER, "Exécution de : " + reqInsertTypeLigne);
				db.execSQL(reqInsertTypeLigne);
			}

			String[] reqsInsertLignes = getContext().getString(
					R.string.req_insert_lignes).split(";");
			for (String reqInsertLigne : reqsInsertLignes) {
				Log.d(getContext().getString(R.string.log_tag_name) + " " + TAG_PROVIDER, "Exécution de : " + reqInsertLigne);
				db.execSQL(reqInsertLigne);
			}
			Log.i(getContext().getString(R.string.log_tag_name) + " " + TAG_PROVIDER, "Fin peuplement de la base.");
		}

		private void createTables(SQLiteDatabase db) {
			Log.i(getContext().getString(R.string.log_tag_name) + " " + TAG_PROVIDER, "Début création de la base.");
			db.execSQL(getContext().getString(
					R.string.req_create_table_type_ligne));

			Log.d(getContext().getString(R.string.log_tag_name) + " " + TAG_PROVIDER, "Exécution de la requête : " + getContext().getString(R.string.req_create_table_lignes));
			db.execSQL(getContext().getString(R.string.req_create_table_lignes));

			Log.d(getContext().getString(R.string.log_tag_name) + " " + TAG_PROVIDER, "Exécution de la requête : " + getContext().getString(R.string.req_create_table_terminus));
			db.execSQL(getContext().getString(
					R.string.req_create_table_terminus));
			
			Log.i(getContext().getString(R.string.log_tag_name) + " " + TAG_PROVIDER, "Fin création de la base.");
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
			Log.i(getContext().getString(R.string.log_tag_name) + " " + TAG_PROVIDER, "Début suppression de la base.");
			db.execSQL("DROP TABLE IF EXISTS terminus");
			db.execSQL("DROP TABLE IF EXISTS lignes");
			db.execSQL("DROP TABLE IF EXISTS type_ligne");
			Log.i(getContext().getString(R.string.log_tag_name) + " " + TAG_PROVIDER, "Fin suppression de la base.");
		}

	}
}
