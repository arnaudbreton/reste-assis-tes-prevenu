package com.resteassistesprevenu.model;

/**
 * Mod�le de ligne, au sens du service
 * @author Arnaud
 *
 */
public class LigneModelService {
	/**
	 * Type de ligne
	 */
	protected String mTypeLigne;
	
	/**
	 * Num�ro de ligne
	 */
	protected String mNumLigne;

	/**
	 * Constructeur par d�faut
	 * @param typeLigne Type de ligne (RER, M�tro, ...
	 * @param numLigne Num�ro de ligne
	 */
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

	
	/**
	 * Type Ligne Num�ro ligne
	 */
	@Override
	public String toString() {
		return getTypeLigne().concat(" ").concat(getNumLigne());
	}
	
	/**
	 * Deux incidents sont �gaux s'ils ont le m�me type et le m�me num�ro
	 */
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
