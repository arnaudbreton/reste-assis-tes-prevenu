package com.resteassistesprevenu.provider;

import android.database.Cursor;
import android.provider.BaseColumns;

import com.resteassistesprevenu.model.LigneModel;

/**
 * Description des colonnes de la table lignes
 * @author Arnaud
 *
 */
public final class LigneBDDHelper implements BaseColumns {
	public static final String NOM_TABLE = "lignes"; 
	public static final String COL_NOM_LIGNE = "nom"; 
	public static final String COL_ID_TYPE_LIGNE = "id_type_ligne"; 
	public static final String COL_IS_FAVORIS = "isFavoris"; 
	
	public static final int NUM_COL_ID= 0; 
	public static final int NUM_COL_NOM_LIGNE = 1; 
	public static final int NUM_COL_ID_TYPE_LIGNE = 2; 
	public static final int NUM_COL_IS_FAVORIS = 3; 
	
	public static LigneModel cursorToLigneModel(Cursor c) {
		return new LigneModel(c.getInt(NUM_COL_ID), c.getString(c.getColumnIndex(TypeLigneBDDHelper.TYPE_LIGNE)), c.getString(NUM_COL_NOM_LIGNE), c.getInt(NUM_COL_IS_FAVORIS) == 0 ? false : true);
	}
}
