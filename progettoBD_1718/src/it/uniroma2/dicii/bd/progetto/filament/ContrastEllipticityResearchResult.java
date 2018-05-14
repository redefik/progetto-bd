package it.uniroma2.dicii.bd.progetto.filament;

import java.util.ArrayList;

public class ContrastEllipticityResearchResult {

	private double minBrightness;
	private double minEllipticity;
	private double maxEllipticity;
	private double suitableFilamentsFraction;
	private ArrayList<FilamentBean> suitableFilaments;
	
	public ContrastEllipticityResearchResult() {
	}
	
	

	public ContrastEllipticityResearchResult(long totalFilaments, double minBrightness, double minElliptcity, double maxEllipticity, 
			ArrayList<FilamentBean> suitableFilaments) {
		super();
		this.minBrightness = minBrightness;
		this.minEllipticity = minElliptcity;
		this.maxEllipticity = maxEllipticity;
		if (totalFilaments != 0) {
			this.suitableFilamentsFraction = (double)suitableFilaments.size() / totalFilaments;
		}
		this.suitableFilaments = suitableFilaments;
	}



	public double getMinBrightness() {
		return minBrightness;
	}

	public void setMinBrightness(double minBrightness) {
		this.minBrightness = minBrightness;
	}

	public double getMinEllipticity() {
		return minEllipticity;
	}

	public void setMinEllipticity(double minElliptcity) {
		this.minEllipticity = minElliptcity;
	}

	public double getMaxEllipticity() {
		return maxEllipticity;
	}

	public void setMaxEllipticity(double maxEllipticity) {
		this.maxEllipticity = maxEllipticity;
	}

	public double getSuitableFilamentsFraction() {
		return suitableFilamentsFraction;
	}

	public void setSuitableFilamentsFraction(double suitableFilamentsFraction) {
		this.suitableFilamentsFraction = suitableFilamentsFraction;
	}

	public ArrayList<FilamentBean> getSuitableFilaments() {
		return suitableFilaments;
	}

	public void setSuitableFilaments(ArrayList<FilamentBean> suitableFilaments) {
		this.suitableFilaments = suitableFilaments;
	}
}
