package com.resteassistesprevenu.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Modèle représentant un incident
 * @author Arnaud
 *
 */
public class IncidentModel {
	/**
	 * Statut de l'incident
	 */
	private String statut;
	
	/**
	 * Id de l'incident
	 */
	private int id;
	
	/**
	 * Nombre de votes positifs
	 */
	private int votePlus;
	
	/**
	 * Nombre de votes négatifs
	 */
	private int voteMinus;
	
	/**
	 * Nombre de votes indiquant un incident clos.
	 */
	private int voteEnded;
	
	/**
	 * Raison de l'incident
	 */
	private String reason;
	
	/**
	 * Dernière modification de l'incident
	 */
	private Date lastModifiedTime;
	
	/**
	 * Ligne de l'incident
	 */
	private LigneModelService ligne;

	/**
	 * Scope "current" du WebService : incidents en cours
	 */
	public static final String SCOPE_JOUR = "day";

	/**
	 * Scope "hour" du WebService : incidents en cours de l'heure
	 */
	public static final String SCOPE_HOUR = "hour";

	/**
	 * Scope "hour" du WebService : incidents en cours des dernières minutes
	 */
	public static final String SCOPE_MINUTE = "minute";

	/**
	 * Constructeur par défaut
	 */
	public IncidentModel() {
		this.statut = "";
		id = -1;
		votePlus = -1;
		voteMinus = -1;
		voteEnded = -1;
		reason = "";
		lastModifiedTime = null;
	}

	/**
	 * Déserialisation via JSON
	 * @param serializedArray Incidents sous forme de tableau JSON
	 * @return Liste d'IncidentModel
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static ArrayList<IncidentModel> deserializeFromArray(
			String serializedArray) throws JSONException, ParseException {
		JSONArray jsonArray = new JSONArray(serializedArray);

		ArrayList<IncidentModel> incidents = new ArrayList<IncidentModel>();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject incident = jsonArray.getJSONObject(i);
			incidents.add(deserializeFromJSONObj(incident));
		}

		return incidents;
	}

	/**
	 * Déserialise un incident au format JSON
	 * @param incidentJSON L'incident au format JSON
	 * @return IncidentModel
	 * @throws JSONException
	 * @throws ParseException
	 */
	private static IncidentModel deserializeFromJSONObj(JSONObject incidentJSON)
			throws JSONException, ParseException {
		IncidentModel incidentModel = new IncidentModel();
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

		incidentModel.id = incidentJSON.getInt("uid");
		incidentModel.statut = incidentJSON.getString("status");	
		incidentModel.reason = incidentJSON.getString("reason");
		incidentModel.votePlus = incidentJSON.getInt("vote_plus");
		incidentModel.voteMinus = incidentJSON.getInt("vote_minus");
		incidentModel.voteEnded = incidentJSON.getInt("vote_ended");
		incidentModel.lastModifiedTime = sdf.parse(incidentJSON
				.getString("last_modified_time"));

		incidentModel.ligne = new LigneModelService(incidentJSON.getString(
		"line").split(" ")[0], incidentJSON.getString("line")
		.split(" ")[1]);
		
		return incidentModel;
	}

	/**
	 * @return the statut
	 */
	public String getStatut() {
		return statut;
	}

	/**
	 * @param statut
	 *            the statut to set
	 */
	public void setStatut(String statut) {
		this.statut = statut;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the votePlus
	 */
	public int getVotePlus() {
		return votePlus;
	}

	/**
	 * @param votePlus
	 *            the votePlus to set
	 */
	public void addVotePlus() {
		this.votePlus++;
	}

	/**
	 * @return the voteMinus
	 */
	public int getVoteMinus() {
		return voteMinus;
	}

	/**
	 * @param voteMinus
	 *            the voteMinus to set
	 */
	public void addVoteMinus() {
		this.voteMinus++;
	}

	/**
	 * @return the voteEnded
	 */
	public int getVoteEnded() {
		return voteEnded;
	}

	/**
	 * @param voteEnded
	 *            the voteEnded to set
	 */
	public void addVoteEnded() {
		this.voteEnded++;
	}

	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * @param reason
	 *            the reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * @return the lastModifiedTime
	 */
	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}

	/**
	 * @param lastModifiedTime
	 *            the lastModifiedTime to set
	 */
	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}


	public LigneModelService getLigne() {
		return this.ligne;
	}	
}
