package com.resteassistesprevenu.provider;

import android.database.Cursor;

import com.resteassistesprevenu.model.LigneModel;

/**
 * Description des colonnes de la table lignes
 * @author Arnaud
 *
 */
public final class LigneBDDHelper {
	public static final String ID = "idLigne";
	public static final String NOM_TABLE = "lignes"; 
	public static final String COL_NOM_LIGNE = "nom"; 
	public static final String COL_ID_TYPE_LIGNE = "id_type_ligne"; 
	public static final String COL_IS_FAVORIS = "isFavoris"; 
	
	public static LigneModel cursorToLigneModel(Cursor c) {
		return new LigneModel(c.getInt(c.getColumnIndex(ID)), c.getString(c.getColumnIndex(TypeLigneBDDHelper.COL_TYPE_LIGNE)), c.getString(c.getColumnIndex(COL_NOM_LIGNE)), c.getInt(c.getColumnIndex(COL_IS_FAVORIS)) == 0 ? false : true);
	}
}
