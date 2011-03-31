package com.resteassistesprevenu.model;

/**
 * Mod�le d'une ligne de transport au sens de l'application
 * @author Arnaud
 *
 */
public class LigneModel extends LigneModelService {
	/** 
	 * Id de la ligne
	 */
	private int id;
	
	/**
	 * Indicateur de favoris
	 */
	private boolean isFavoris;	

	/**
	 * Constructeur par d�faut
	 * @param id Id de l'incident 
	 * @param typeLigne Type de ligne (RER, Bus, M�tro, ...)
	 * @param numLigne Num�ro de ligne
	 * @param isFavoris Indicateur de favoris
	 */
	public LigneModel(int id, String typeLigne, String numLigne, boolean isFavoris) {
		super(typeLigne, numLigne);
		
		this.id = id;		
		this.isFavoris = isFavoris;
	}
	
	/**
	 * @return the mTypeLigne
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @return the isFavoris
	 */
	public boolean isFavoris() {
		return isFavoris;
	}

	/**
	 * @param isFavoris the isFavoris to set
	 */
	public void setFavoris(boolean isFavoris) {
		this.isFavoris = isFavoris;
	}

}
