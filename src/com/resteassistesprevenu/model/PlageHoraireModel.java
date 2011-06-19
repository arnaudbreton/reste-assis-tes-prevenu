package com.resteassistesprevenu.model;


public class PlageHoraireModel {
	private int heureDebut;
	private int minuteDebut;
	private int heureFin;
	private int minuteFin;
		
	public PlageHoraireModel(int heureDebut, int minuteDebut, int heureFin, int minuteFin) {
		this.heureDebut = heureDebut;
		this.minuteDebut = minuteDebut;
		
		this.heureFin = heureFin;
		this.minuteFin = minuteFin;
	}
	
	@Override
	public String toString() {		
		return String.format("%d:%d - %d:%d", heureDebut,minuteDebut,heureFin,minuteFin);
	}
}
