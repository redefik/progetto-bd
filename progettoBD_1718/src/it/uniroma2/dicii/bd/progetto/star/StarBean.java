package it.uniroma2.dicii.bd.progetto.star;

public class StarBean {

	private String name;
	private int id;
	private double latitude;
	private double longitude; 
	private double flow;
	private String classification; 
	private String satellite;
	
	public String toString() {
		String string;
		string = this.name + " ( " + this.id + " , " + this.satellite + " ); " + "Position: " + this.latitude 
				+ " lat, " + this.longitude + " long ; Flow: " + this.flow +" ; Type: " + this.classification;
		return string;
	}

	public StarBean(String name, int id, double latitude, double longitude, double flow, String classification, String satellite) {

		this.name = name;
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.flow = flow;
		this.classification = classification;
		this.satellite = satellite;

	}
	
	public StarBean(Star star) {
		
		this.name = star.getName();
		this.id = star.getId();
		this.latitude = star.getLatitude();
		this.longitude = star.getLongitude();
		this.flow = star.getFlow();
		this.classification = star.getClassification();
		this.satellite = star.getSatellite();
	}
	
	public StarBean() {}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getFlow() {
		return flow;
	}
	public void setFlow(double flusso) {
		this.flow = flusso;
	}
	public String getClassification() {
		return classification;
	}
	public void setClassification(String classification) {
		this.classification = classification;
	}
	public String getSatellite() {
		return satellite;
	}
	public void setSatellite(String satellite) {
		this.satellite = satellite;
	}
	
	
}
