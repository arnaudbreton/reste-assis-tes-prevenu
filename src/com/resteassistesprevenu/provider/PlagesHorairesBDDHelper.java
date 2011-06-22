package com.resteassistesprevenu.provider;

import android.content.ContentValues;
import android.database.Cursor;

import com.resteassistesprevenu.model.PlageHoraireModel;

public class PlagesHorairesBDDHelper {
	public static final String ID = "idPlageHoraire";
	public static final String NOM_TABLE = "plagesHoraires";
	public static final String COL_HEURE_DEBUT = "heure_debut";
	public static final String COL_MINUTE_DEBUT = "minute_debut";
	public static final String COL_HEURE_FIN = "heure_fin";
	public static final String COL_MINUTE_FIN = "minute_fin";

	public static PlageHoraireModel cursorToPlageHoraireModel(Cursor c) {
		if(c.moveToFirst()) {
			return new PlageHoraireModel(c.getInt(c.getColumnIndex(COL_HEURE_DEBUT)), 
					c.getInt(c.getColumnIndex(COL_MINUTE_DEBUT)), 
					c.getInt(c.getColumnIndex(COL_HEURE_FIN)),
					c.getInt(c.getColumnIndex(COL_MINUTE_FIN)));
		}
		else {
			return null;
		}
	}
	
	public static ContentValues plageHoraireModelToCursor(PlageHoraireModel plage) {
		ContentValues values = new ContentValues();
		
		values.put(COL_HEURE_DEBUT, plage.getHeureDebut());
		values.put(COL_MINUTE_DEBUT, plage.getMinuteDebut());
		values.put(COL_HEURE_FIN, plage.getHeureFin());
		values.put(COL_MINUTE_FIN, plage.getMinuteFin());
		
		return values;
	}
}
