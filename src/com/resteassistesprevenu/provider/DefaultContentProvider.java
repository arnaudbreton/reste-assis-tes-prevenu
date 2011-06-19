package com.resteassistesprevenu.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.resteassistesprevenu.R;

/**
 * Provider fournissant les données contenues dans la base
 * 
 */
public class DefaultContentProvider extends ContentProvider {
	private final static String TAG_PROVIDER = "DefaultContentProvider";

	/**
	 * Nom du provider
	 */
	public static final String PROVIDER_NAME = "com.resteassistesprevenu.provider";

	/**
	 * URI du provider
	 */
	public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME);

	/**
	 * URI pour les types de lignes
	 */
	public static final String TYPE_LIGNES_URI = "type_lignes";

	/**
	 * URI pour les favoris
	 */
	public static final String FAVORIS_URI = "favoris";

	/**
	 * URI pour les lignes
	 */
	public static final String LIGNES_URI = "lignes";

	/**
	 * URI pour les incidents
	 */
	public static final String INCIDENTS_URI = "incidents";
	
	/**
	 * URI pour le paramétrage
	 */
	public static final String PARAMETRAGE_URI = "parametrage";

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
	 * Identifiant de l'URL de récupération d'enregistrement ou de suppression
	 * d'un favoris
	 */
	private static final int FAVORIS_ID = 5;

	/**
	 * Identifiant de l'URI d'ajout des incidents
	 */
	private static final int INCIDENTS = 6;
	
	/**
	 * Identifiant de l'URI de récuparation du paramétrage
	 */
	private static final int PARAMETRAGE_ID = 7;

	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(PROVIDER_NAME, TYPE_LIGNES_URI, TYPE_LIGNES);
		uriMatcher.addURI(PROVIDER_NAME, FAVORIS_URI, FAVORIS);
		uriMatcher.addURI(PROVIDER_NAME, FAVORIS_URI.concat("/#"), FAVORIS_ID);
		uriMatcher.addURI(PROVIDER_NAME, LIGNES_URI, LIGNES);
		uriMatcher.addURI(PROVIDER_NAME, LIGNES_URI.concat("/#"), LIGNES_ID);
		uriMatcher.addURI(PROVIDER_NAME, INCIDENTS_URI, INCIDENTS);
		uriMatcher.addURI(PROVIDER_NAME, PARAMETRAGE_URI.concat("/#"), PARAMETRAGE_ID);
	}

	/**
	 * Helper pour la base
	 */
	private DatabaseHelper dbHelper;

	@Override
	public int delete(Uri uri, String whereClause, String[] whereArgs) {
		int nbRowDeleted = 0;

		switch (uriMatcher.match(uri)) {
		case INCIDENTS:
			Log.d(getContext().getString(R.string.log_tag_name) + " "
					+ TAG_PROVIDER, "Début de suppression des incidents");
			nbRowDeleted = this.dbHelper.getWritableDatabase().delete(
					IncidentsBDDHelper.NOM_TABLE, whereClause, whereArgs);
			Log.d(getContext().getString(R.string.log_tag_name) + " "
					+ TAG_PROVIDER, String.format("%s incidents supprimés", nbRowDeleted));
			break;
		}

		return nbRowDeleted;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case TYPE_LIGNES:
			return "vnd.android.cursor.dir/vnd.resteassistesprevenu.type_lignes";
		case LIGNES:
			return "vnd.android.cursor.dir/vnd.resteassistesprevenu.lignes";
		case LIGNES_ID:
			return "vnd.android.cursor.item/vnd.resteassistesprevenu.lignes";
		case FAVORIS:
			return "vnd.android.cursor.dir/vnd.resteassistesprevenu.favoris";
		case FAVORIS_ID:
			return "vnd.android.cursor.item/vnd.resteassistesprevenu.favoris";
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long rowId = 0;

		Uri uriNewLine = CONTENT_URI;
		
		switch (uriMatcher.match(uri)) {
		case INCIDENTS:
			Log.d(getContext().getString(R.string.log_tag_name) + " "
					+ TAG_PROVIDER, "Début d'insertion de nouveaux incidents");
			rowId = this.dbHelper.getWritableDatabase().insert(
					IncidentsBDDHelper.NOM_TABLE, null, values);
			
			if (rowId > 0) {
				uriNewLine = Uri.withAppendedPath(uriNewLine, INCIDENTS_URI
						+ "/" + rowId);
			}
			
			break;
		case PARAMETRAGE_ID:
			Log.d(getContext().getString(R.string.log_tag_name) + " "
					+ TAG_PROVIDER, "Début d'insertion d'un paramètre");
			rowId = this.dbHelper.getWritableDatabase().insert(
					ParametrageBDDHelper.NOM_TABLE, null, values);
			if (rowId > 0) {
				uriNewLine = Uri.withAppendedPath(uriNewLine, PARAMETRAGE_URI
						+ "/" + rowId);
			}
		}	
		
		if(rowId > 0) {
			return uriNewLine;
		}

		throw new SQLException("Erreur à l'insertion :  " + uri);
	}

	@Override
	public boolean onCreate() {
		Context context = getContext();
		this.dbHelper = new DatabaseHelper(context);
		this.rasstpDB = dbHelper.getWritableDatabase();
		return (this.rasstpDB == null) ? false : true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();

		switch (uriMatcher.match(uri)) {
		case TYPE_LIGNES:
			Log.d(getContext().getString(R.string.log_tag_name) + " "
					+ TAG_PROVIDER, "Début récupération type lignes");
			qb.setTables("type_ligne");
			break;
		case LIGNES:
			Log.d(getContext().getString(R.string.log_tag_name) + " "
					+ TAG_PROVIDER, "Début récupération lignes");
			qb.setTables(String.format("%s INNER JOIN %s ON (%s.%s=%s.%s)",
					LigneBDDHelper.NOM_TABLE,
					TypeLigneBDDHelper.NOM_TABLE,
					LigneBDDHelper.NOM_TABLE, LigneBDDHelper.COL_ID_TYPE_LIGNE,
					TypeLigneBDDHelper.NOM_TABLE, TypeLigneBDDHelper.ID));
			break;
		case LIGNES_ID:
			Log.d(getContext().getString(R.string.log_tag_name) + " "
					+ TAG_PROVIDER, "Début récupération d'une ligne");
			qb.setTables(String.format("%s INNER JOIN %s ON (%s.%s=%s.%s)",
					LigneBDDHelper.NOM_TABLE,
					TypeLigneBDDHelper.NOM_TABLE,
					LigneBDDHelper.NOM_TABLE, LigneBDDHelper.COL_ID_TYPE_LIGNE,
					TypeLigneBDDHelper.NOM_TABLE, TypeLigneBDDHelper.ID));
			qb.appendWhere("lignes.idLigne = " + uri.getPathSegments().get(1));
			break;
		case FAVORIS:
			Log.d(getContext().getString(R.string.log_tag_name) + " "
					+ TAG_PROVIDER, "Début récupération favoris");
			qb.setTables(String.format("%s INNER JOIN %s ON (%s.%s=%s.%s)",
					LigneBDDHelper.NOM_TABLE,
					TypeLigneBDDHelper.NOM_TABLE,
					LigneBDDHelper.NOM_TABLE, LigneBDDHelper.COL_ID_TYPE_LIGNE,
					TypeLigneBDDHelper.NOM_TABLE, TypeLigneBDDHelper.ID));
			qb.appendWhere(LigneBDDHelper.COL_IS_FAVORIS + " = 1");
			break;
		case INCIDENTS:
			Log.d(getContext().getString(R.string.log_tag_name) + " "
					+ TAG_PROVIDER, "Début récupération incidents");
			qb.setTables(String.format("(%s INNER JOIN %s %s ON (%s.%s=%s.%s)) %s INNER JOIN %s ON (%s.%s=%s.%s)",
					IncidentsBDDHelper.NOM_TABLE, LigneBDDHelper.NOM_TABLE, "L1",
					IncidentsBDDHelper.NOM_TABLE,
					IncidentsBDDHelper.COL_ID_LIGNE, "L1",
					LigneBDDHelper.ID,
					"L2",  TypeLigneBDDHelper.NOM_TABLE,
					"L2", LigneBDDHelper.COL_ID_TYPE_LIGNE,
					TypeLigneBDDHelper.NOM_TABLE, TypeLigneBDDHelper.ID
					));
			break;
		case PARAMETRAGE_ID:
			Log.d(getContext().getString(R.string.log_tag_name) + " "
					+ TAG_PROVIDER, "Début récupération paramétrage");
			qb.setTables(String.format("%s", ParametrageBDDHelper.NOM_TABLE));
			qb.appendWhere(ParametrageBDDHelper.COL_CLE + uri.getPathSegments().get(1));
		}		

		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null,
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
			Log.d(getContext().getString(R.string.log_tag_name) + " "
					+ TAG_PROVIDER, "Début mise à jour d'un favoris : "
					+ uri.getPathSegments().get(1));
			count = this.dbHelper.getWritableDatabase().update(
					LigneBDDHelper.NOM_TABLE, values,
					LigneBDDHelper.ID + "=" + uri.getPathSegments().get(1),
					selectionArgs);
			break;
		case PARAMETRAGE_ID:
			Log.d(getContext().getString(R.string.log_tag_name) + " "
					+ TAG_PROVIDER, "Début mise d'un paramètre : "
					+ uri.getPathSegments().get(1));
			count = this.dbHelper.getWritableDatabase().update(
					ParametrageBDDHelper.NOM_TABLE, values,
					ParametrageBDDHelper.COL_CLE + "=" + uri.getPathSegments().get(1),
					selectionArgs);
			break;
		}

		getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}

	private SQLiteDatabase rasstpDB;

	/**
	 * Nom de la base
	 */
	private static final String DATABASE_NAME = "ResteAssisTesPrevenu";

	/**
	 * Version de la base
	 */
	private static final int DATABASE_VERSION = 5;

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
			Log.i(getContext().getString(R.string.log_tag_name) + " "
					+ TAG_PROVIDER, "Début peuplement de la base.");
			String[] reqsInsertTypeLignes = getContext().getString(
					R.string.req_insert_type_ligne).split(";");
			for (String reqInsertTypeLigne : reqsInsertTypeLignes) {
				Log.d(getContext().getString(R.string.log_tag_name) + " "
						+ TAG_PROVIDER, "Exécution de : " + reqInsertTypeLigne);
				db.execSQL(reqInsertTypeLigne);
			}

			String[] reqsInsertLignes = getContext().getString(
					R.string.req_insert_lignes).split(";");
			for (String reqInsertLigne : reqsInsertLignes) {
				Log.d(getContext().getString(R.string.log_tag_name) + " "
						+ TAG_PROVIDER, "Exécution de : " + reqInsertLigne);
				db.execSQL(reqInsertLigne);
			}
			Log.i(getContext().getString(R.string.log_tag_name) + " "
					+ TAG_PROVIDER, "Fin peuplement de la base.");
		}

		private void createTables(SQLiteDatabase db) {
			Log.i(getContext().getString(R.string.log_tag_name) + " "
					+ TAG_PROVIDER, "Début création de la base.");

			// Création de la table des types de lignes
			db.execSQL(String.format("CREATE TABLE %s"
					+ "(%s INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "%s VARCHAR NOT NULL);",
					TypeLigneBDDHelper.NOM_TABLE,
					TypeLigneBDDHelper.ID,
					TypeLigneBDDHelper.COL_TYPE_LIGNE));
	
			// Création de la table des lignes
			db.execSQL(String.format("CREATE TABLE %s"
			+ "(%s INTEGER PRIMARY KEY	AUTOINCREMENT,"
			+ "%s VARCHAR NOT NULL,"
			+ "%s INTEGER REFERENCES %s(%s),"
			+ "%s INTEGER DEFAULT 0);", 
			LigneBDDHelper.NOM_TABLE,
			LigneBDDHelper.ID,
			LigneBDDHelper.COL_NOM_LIGNE,
			LigneBDDHelper.COL_ID_TYPE_LIGNE,
			TypeLigneBDDHelper.NOM_TABLE, TypeLigneBDDHelper.ID,
			LigneBDDHelper.COL_IS_FAVORIS));					

			// Création de la table des incidents
			db.execSQL(String.format("CREATE TABLE %s ("
					+ "%s INTEGER PRIMARY KEY,"
					+ "%s INTEGER REFERENCES lignes(_id),"
					+ "%s VARCHAR NOT NULL,"
					+ "%s VARCHAR NOT NULL,"
					+ "%s INTEGER NOT NULL,"
					+ "%s INTEGER NOT NULL,"
					+ "%s INTEGER NOT NULL,"
					+ "%s DATE NOT NULL" + ");",
					IncidentsBDDHelper.NOM_TABLE,
					IncidentsBDDHelper.ID,
					IncidentsBDDHelper.COL_ID_LIGNE,
					IncidentsBDDHelper.COL_RAISON,
					IncidentsBDDHelper.COL_STATUT,
					IncidentsBDDHelper.COL_NB_VOTE_PLUS,
					IncidentsBDDHelper.COL_NB_VOTE_MINUS,
					IncidentsBDDHelper.COL_NB_VOTE_ENDED,
					IncidentsBDDHelper.COL_LAST_MODIFIED_TIME));

			// Création de la table paramétrage
			db.execSQL(String.format("CREATE TABLE %s ("
					+ "%s INTEGER PRIMARY KEY,"
					+ "%s VARCHAR NOT NULL,"
					+ "%s VARCHAR NOT NULL"
					+ ");",
					ParametrageBDDHelper.NOM_TABLE,
					ParametrageBDDHelper.ID,
					ParametrageBDDHelper.COL_CLE,
					ParametrageBDDHelper.COL_VALEUR));
			
			Log.i(getContext().getString(R.string.log_tag_name) + " "
					+ TAG_PROVIDER, "Fin création de la base.");
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
			Log.i(getContext().getString(R.string.log_tag_name) + " "
					+ TAG_PROVIDER, "Début suppression de la base.");
			db.execSQL("DROP TABLE IF EXISTS terminus");
			db.execSQL("DROP TABLE IF EXISTS lignes");
			db.execSQL("DROP TABLE IF EXISTS type_ligne");
			Log.i(getContext().getString(R.string.log_tag_name) + " "
					+ TAG_PROVIDER, "Fin suppression de la base.");
		}

	}
}
