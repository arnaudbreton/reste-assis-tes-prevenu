package com.resteassistesprevenu.model;

public class LigneModel extends LigneModelService {
	private int id;
	private boolean isFavoris;	

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
