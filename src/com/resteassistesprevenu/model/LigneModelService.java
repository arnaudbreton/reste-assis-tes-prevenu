package com.resteassistesprevenu.model;


/**
 * Modèle de ligne, au sens du service
 * @author Arnaud
 *
 */
public class LigneModelService {
	public static final String TYPE_LIGNE_RER = "RER";	
	public static final String TYPE_LIGNE_METRO = "Métro";	
	public static final String TYPE_LIGNE_TRANSILIEN = "Transilien";	
	public static final String TYPE_LIGNE_BUS = "Bus";	
	public static final String TYPE_LIGNE_TRAMWAY = "Tramway";
	
	/**
	 * Type de ligne
	 */
	protected String mTypeLigne;
	
	/**
	 * Numéro de ligne
	 */
	protected String mNumLigne;

	/**
	 * Constructeur par défaut
	 * @param typeLigne Type de ligne (RER, Métro, ...
	 * @param numLigne Numéro de ligne
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
	 * Type Ligne Numéro ligne
	 */
	@Override
	public String toString() {
		return getTypeLigne().concat(" ").concat(getNumLigne());
	}
	
	/**
	 * Deux incidents sont égaux s'ils ont le même type et le même numéro
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
	
	public static String getTypeLigneImage(String typeLigne) {
		String typeLigneDrawable;
		
		if(typeLigne.equals(TYPE_LIGNE_METRO) || typeLigne.equals("Metro")) {
			typeLigneDrawable = "metro";
		}
		else {
			typeLigneDrawable = typeLigne.toLowerCase();
		}

		return "ic_" + typeLigneDrawable;
	}
	
	public static String getNumLigneImage(String typeLigne, String numLigne) {
		String typeLigneDrawable;
		
		if(typeLigne.equals(TYPE_LIGNE_METRO) || typeLigne.equals("Metro")) {
			typeLigneDrawable = "metro";
		}
		else if(typeLigne.equals(TYPE_LIGNE_BUS)) {
			return "";
		}
		else {
			typeLigneDrawable = typeLigne.toLowerCase();
		}
		
		return "ic_" + typeLigneDrawable + "_" +  numLigne.toLowerCase();
	}
}
