package com.resteassistesprevenu.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LigneModel {
	private int id;
	private TypeLigne mTypeLigne;
	private String mNumLigne;
	
	public LigneModel(int id, TypeLigne typeLigne, String numLigne) {
		this.id = id;
		this.mTypeLigne = typeLigne;
		this.mNumLigne = numLigne;
	}
	
	/**
	 * @return the mTypeLigne
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the mTypeLigne
	 */
	public TypeLigne getTypeLigne() {
		return mTypeLigne;
	}

	/**
	 * @return the mNumLigne
	 */
	public String getNumLigne() {
		return mNumLigne;
	}
	
	public static List<LigneModel> deserializeArray(String serializedLignesArray) throws JSONException {
		JSONArray jsonArray = new JSONArray(serializedLignesArray);
		
		List<LigneModel> lignes = new ArrayList<LigneModel>();
		for(int i=0;i<jsonArray.length();i++) {
			JSONObject ligne;
			ligne = jsonArray.getJSONObject(i);
			lignes.add(deserializeFromJSONObj(ligne));
		}
		
		return lignes;
	}

	private static LigneModel deserializeFromJSONObj(JSONObject ligne) throws JSONException {
		int ligneId = ligne.getInt("uid");
		
		String strTypeLigne = ligne.getString("name").split(" ")[0];
		TypeLigne typeLigne;
		
		String numLigne = ligne.getString("name").split(" ")[1];
		
		if(strTypeLigne.equals("Métro")) {
			typeLigne = TypeLigne.METRO;
		}		
		else if(strTypeLigne.equals("RER")) {
			typeLigne = TypeLigne.RER;
		}
		else {
			typeLigne = TypeLigne.TRANSILIEN;
		}			
		
		return new LigneModel(ligneId, typeLigne, numLigne);
	}
}
