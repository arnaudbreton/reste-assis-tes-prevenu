package com.resteassistesprevenu.model;


/**
 * Mod�le de ligne, au sens du service
 * @author Arnaud
 *
 */
public class LigneModelService {
	public static final String TYPE_LIGNE_RER = "RER";	
	public static final String TYPE_LIGNE_METRO = "M�tro";	
	public static final String TYPE_LIGNE_TRANSILIEN = "Transilien";	
	public static final String TYPE_LIGNE_BUS = "Bus";	
	public static final String TYPE_LIGNE_TRAMWAY = "Tramway";
	
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
