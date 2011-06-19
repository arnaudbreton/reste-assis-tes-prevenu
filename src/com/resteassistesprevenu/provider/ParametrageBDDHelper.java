package com.resteassistesprevenu.provider;

import android.database.Cursor;

import com.resteassistesprevenu.model.ParametreModel;

public class ParametrageBDDHelper {
	public static final String ID = "idParametre";
	public static final String NOM_TABLE = "parametrage"; 	
	public static final String COL_CLE = "cle";	
	public static final String COL_VALEUR = "valeur";
	
	public static final String FREQUENCE_SYNC_PARAM = "frequence_sync";
	
	public static ParametreModel cursorToParametreModel(Cursor c) {
		if(c.moveToFirst()) {
			return new ParametreModel(c.getString(c.getColumnIndex(COL_CLE)), c.getString(c.getColumnIndex(COL_VALEUR)));
		}
		else {
			return null;
		}
	}
}
