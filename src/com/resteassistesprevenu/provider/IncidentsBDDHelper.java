package com.resteassistesprevenu.provider;

import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.resteassistesprevenu.model.IncidentModel;

/**
 * Description des colonnes de la table des incidents
 * @author Arnaud
 *
 */
public final class IncidentsBDDHelper implements BaseColumns {
	public static final String NOM_TABLE = "incidents"; 
	public static final String COL_ID_LIGNE = "id_ligne";
	public static final String COL_RAISON = "raison";
	public static final String COL_NB_VOTE_PLUS = "nb_vote_plus";
	public static final String COL_NB_VOTE_MINUS = "nb_vote_minus";
	public static final String COL_NB_VOTE_ENDED = "nb_vote_ended";
	public static final String COL_LAST_MODIFIED_TIME = "last_modified_time";
	public static final String COL_STATUT = "statut";
	
	public static final int NUM_COL_ID = 0;
	public static final int NUM_COL_ID_LIGNE = 1;
	public static final int NUM_COL_RAISON = 2;
	public static final int NUM_COL_NB_VOTE_PLUS = 3;
	public static final int NUM_COL_NB_VOTE_MINUS = 4;
	public static final int NUM_COL_NB_VOTE_ENDED = 5;
	public static final int NUM_COL_LAST_MODIFIED_TIME = 6;
	public static final int NUM_COL_STATUT = 7;
	
	/**
	 * Retourne le contentvalues associé à l'incident
	 * @param incident Le modèle d'incident
	 * @return Un contentValues
	 */
	public static ContentValues getContentValues(IncidentModel incident) {
		ContentValues cv = new ContentValues();
		
		cv.put(_ID, incident.getId());
		
		return cv;
	}
	
	public static IncidentModel getIncidentModelFromCursor(Cursor c) {
		IncidentModel incident = new IncidentModel();
		
		incident.setId(c.getInt(NUM_COL_ID));
		incident.setReason(c.getString(NUM_COL_RAISON));
		incident.setLastModifiedTime(new Date(c.getInt(NUM_COL_LAST_MODIFIED_TIME)));
		incident.setStatut(c.getString(NUM_COL_STATUT));
		
		return incident;
	}
}
