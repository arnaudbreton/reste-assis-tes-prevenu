package com.resteassistesprevenu.model;

public class LigneModel {
	private int id;
	private String mTypeLigne;
	private String mNumLigne;
	
	public LigneModel(int id, String typeLigne, String numLigne) {
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
	public String getTypeLigne() {
		return mTypeLigne;
	}

	/**
	 * @return the mNumLigne
	 */
	public String getNumLigne() {
		return mNumLigne;
	}
	
	@Override
	public String toString() {
		return getTypeLigne() + " " + getNumLigne();
	}
}
