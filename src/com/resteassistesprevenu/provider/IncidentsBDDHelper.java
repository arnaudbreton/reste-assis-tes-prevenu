package com.resteassistesprevenu.provider;

import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;

import com.resteassistesprevenu.model.IncidentModel;

/**
 * Description des colonnes de la table des incidents
 * @author Arnaud
 *
 */
public final class IncidentsBDDHelper {
	public static final String ID = "idIncident";
	public static final String NOM_TABLE = "incidents"; 
	public static final String COL_ID_LIGNE = "id_ligne";
	public static final String COL_RAISON = "raison";
	public static final String COL_NB_VOTE_PLUS = "nb_vote_plus";
	public static final String COL_NB_VOTE_MINUS = "nb_vote_minus";
	public static final String COL_NB_VOTE_ENDED = "nb_vote_ended";
	public static final String COL_LAST_MODIFIED_TIME = "last_modified_time";
	public static final String COL_STATUT = "statut";
	
	/**
	 * Retourne le contentvalues associé à l'incident
	 * @param incident Le modèle d'incident
	 * @param ligneId l'identifiant de la ligne
	 * @return Un contentValues
	 */
	public static ContentValues getContentValues(IncidentModel incident, int ligneId) {
		ContentValues cv = new ContentValues();

		cv.put(ID, incident.getId());
		cv.put(COL_RAISON, incident.getReason());
		cv.put(COL_STATUT, incident.getStatut());
		cv.put(COL_NB_VOTE_PLUS, incident.getVotePlus());
		cv.put(COL_NB_VOTE_MINUS, incident.getVoteMinus());
		cv.put(COL_NB_VOTE_ENDED, incident.getVoteEnded());
		cv.put(COL_LAST_MODIFIED_TIME, incident.getLastModifiedTime().getTime());
		cv.put(COL_ID_LIGNE, ligneId);
				
		return cv;
	}
	
	public static IncidentModel getIncidentModelFromCursor(Cursor c) {
		IncidentModel incident = new IncidentModel();
		
		incident.setId(c.getInt(c.getColumnIndex(ID)));
		incident.setReason(c.getString(c.getColumnIndex(COL_RAISON)));
		incident.setLastModifiedTime(new Date(c.getInt(c.getColumnIndex(COL_LAST_MODIFIED_TIME))));
		incident.setStatut(c.getString(c.getColumnIndex(COL_STATUT)));
		incident.setVotePlus(c.getInt(c.getColumnIndex(COL_NB_VOTE_PLUS)));
		incident.setVoteMinus(c.getInt(c.getColumnIndex(COL_NB_VOTE_MINUS)));
		incident.setVoteEnded(c.getInt(c.getColumnIndex(COL_NB_VOTE_ENDED)));

		incident.setLigne(LigneBDDHelper.cursorToLigneModel(c));
		
		return incident;
	}
}
