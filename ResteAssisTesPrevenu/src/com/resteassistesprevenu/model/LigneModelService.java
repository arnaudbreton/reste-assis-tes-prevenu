package com.resteassistesprevenu.model;

public class LigneModelService {
	protected String mTypeLigne;
	protected String mNumLigne;

	public LigneModelService(String typeLigne, String numLigne) {
		this.mTypeLigne = typeLigne;
		this.mNumLigne = numLigne;
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
		return getTypeLigne().concat(" ").concat(getNumLigne());
	}
	
	@Override
	public boolean equals(Object o) {
		LigneModelService ligne = (LigneModelService) o;
		
		if(ligne != null) {
			return mTypeLigne.equals(ligne.getTypeLigne()) && mNumLigne.equals(ligne.getNumLigne());
		}
		else {
			return false;
		}
		
	}
}
