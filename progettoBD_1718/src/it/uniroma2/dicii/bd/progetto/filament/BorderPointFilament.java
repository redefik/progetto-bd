package it.uniroma2.dicii.bd.progetto.filament;

// la classe matcha la tabella PUNTOCONTORNOFILAMENTO presente nel db
public class BorderPointFilament {
	
	private double pointLatitude;
	private double pointLongitude;
	private String filamentName;
	private String satelliteName;
	
	public BorderPointFilament() {}

	public BorderPointFilament(double pointLatitude, double pointLongitude, String filamentName,
			String satelliteName) {
		this.pointLatitude = pointLatitude;
		this.pointLongitude = pointLongitude;
		this.filamentName = filamentName;
		this.satelliteName = satelliteName;
	}


	public double getPointLatitude() {
		return pointLatitude;
	}


	public void setPointLatitude(double pointLatitude) {
		this.pointLatitude = pointLatitude;
	}


	public void setPointLongitude(double pointLongitude) {
		this.pointLongitude = pointLongitude;
	}


	public void setFilamentName(String filamentName) {
		this.filamentName = filamentName;
	}


	public void setSatelliteName(String satelliteName) {
		this.satelliteName = satelliteName;
	}


	public double getPointLongitude() {
		return pointLongitude;
	}


	public String getFilamentName() {
		return filamentName;
	}


	public String getSatelliteName() {
		return satelliteName;
	}
	
	
	
	
	
}
