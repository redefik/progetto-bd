package it.uniroma2.dicii.bd.progetto.star;

import java.util.ArrayList;

public class StarsIntoRegion {

	private ArrayList<StarBean> internalStars;
	private ArrayList<StarBean> externalStars;
	
	public StarsIntoRegion(ArrayList<StarBean> internalStars, ArrayList<StarBean> externalStars) {
		this.internalStars = internalStars;
		this.externalStars = externalStars;
	}
	
	public ArrayList<StarBean> getInternalStars() {
		return internalStars;
	}
	public void setInternalStars(ArrayList<StarBean> internalStars) {
		this.internalStars = internalStars;
	}
	public ArrayList<StarBean> getExternalStars() {
		return externalStars;
	}
	public void setExternalStars(ArrayList<StarBean> externalStars) {
		this.externalStars = externalStars;
	}
	
	public Double getPercentageInternalStar() {
		
		if (internalStars.size() == 0) {
			return 0.0;
		}
		return ((internalStars.size() * 100.0) / (internalStars.size() + externalStars.size()));
	}
	
	public Double getPercentageExternalStar() {
		
		if (externalStars.size() == 0) {
			return 0.0;
		}
		
		return ((externalStars.size() * 100.0) / (internalStars.size() + externalStars.size()));

	}
	
	public int getAllStarNumber() {
		return (internalStars.size() + externalStars.size());
	}
}
