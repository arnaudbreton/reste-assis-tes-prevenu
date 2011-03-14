package com.android.resteassistesprevenu.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class IncidentModel {
	private String statut;
	private int id;
	private String ligne;
	private int votePlus;
	private int voteMinus;
	private int voteEnded;
	private String reason;
	private Date lastModifiedTime;
	private TypeLigne typeLigne;
	
	public IncidentModel() {
		this.statut = "";
		id = -1;
		votePlus = -1;
		voteMinus = -1;
		voteEnded = -1;
		reason = "";
		lastModifiedTime = null;
	}
	
	public static ArrayList<IncidentModel> deserializeFromArray(String serializedArray) throws JSONException, ParseException {
		JSONArray jsonArray = new JSONArray(serializedArray);
		
		ArrayList<IncidentModel> incidents = new ArrayList<IncidentModel>();
		for(int i=0;i<jsonArray.length();i++) {
			JSONObject incident = jsonArray.getJSONObject(i);
			incidents.add(deserializeFromJSONObj(incident));
		}
		
		return incidents;
	}

	private static IncidentModel deserializeFromJSONObj(JSONObject incidentJSON)
			throws JSONException, ParseException {
		IncidentModel incidentModel = new IncidentModel();
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
		
		incidentModel.id = incidentJSON.getInt("uid");
		incidentModel.statut = incidentJSON.getString("status");
		incidentModel.typeLigne = getType(incidentJSON.getString("line").split(" ")[0]);
		incidentModel.ligne = incidentJSON.getString("line").split(" ")[1];;
		incidentModel.reason = incidentJSON.getString("reason");
		incidentModel.votePlus = incidentJSON.getInt("vote_plus");
		incidentModel.voteMinus = incidentJSON.getInt("vote_minus");
		incidentModel.voteEnded = incidentJSON.getInt("vote_ended");
		incidentModel.lastModifiedTime = sdf.parse(incidentJSON.getString("last_modified_time"));
		
		return incidentModel;
	}

	private static TypeLigne getType(String typeLigne) {
		if(typeLigne.equals("RER")) {
			return TypeLigne.RER;
		}
		else if(typeLigne.equals("Metro")) {
			return TypeLigne.METRO;
		}
		else {
			return TypeLigne.TRANSILIEN;
		}
	}

	/**
	 * @return the statut
	 */
	public String getStatut() {
		return statut;
	}

	/**
	 * @param statut the statut to set
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
	 * @param id the id to set
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
	 * @param votePlus the votePlus to set
	 */
	public void setVotePlus(int votePlus) {
		this.votePlus = votePlus;
	}

	/**
	 * @return the voteMinus
	 */
	public int getVoteMinus() {
		return voteMinus;
	}

	/**
	 * @param voteMinus the voteMinus to set
	 */
	public void setVoteMinus(int voteMinus) {
		this.voteMinus = voteMinus;
	}

	/**
	 * @return the voteEnded
	 */
	public int getVoteEnded() {
		return voteEnded;
	}

	/**
	 * @param voteEnded the voteEnded to set
	 */
	public void setVoteEnded(int voteEnded) {
		this.voteEnded = voteEnded;
	}

	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * @param reason the reason to set
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
	 * @param lastModifiedTime the lastModifiedTime to set
	 */
	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	/**
	 * @return the ligne
	 */
	public String getLigne() {
		return ligne;
	}

	/**
	 * @param ligne the ligne to set
	 */
	public void setLigne(String ligne) {
		this.ligne = ligne;
	}

	public TypeLigne getTypeLigne() {
		return typeLigne;
	}

	public void setTypeLigne(TypeLigne typeLigne) {
		this.typeLigne = typeLigne;
	}
}
