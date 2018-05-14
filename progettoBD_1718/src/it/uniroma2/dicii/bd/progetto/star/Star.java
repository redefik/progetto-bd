package it.uniroma2.dicii.bd.progetto.star;

public class Star {

	private String name;
	private int id;
	private double latitude;
	private double longitude; 
	private double flow;
	private String classification; 
	private String satellite;
	
	public Star(StarBean starBean) {

		this.name = starBean.getName();
		this.id = starBean.getId();
		this.latitude = starBean.getLatitude();
		this.longitude = starBean.getLongitude();
		this.flow = starBean.getFlow();
		this.classification = starBean.getClassification();
		this.satellite = starBean.getSatellite();

	}

	public Star() {}

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

	public void setFlow(double flow) {
		this.flow = flow;
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
