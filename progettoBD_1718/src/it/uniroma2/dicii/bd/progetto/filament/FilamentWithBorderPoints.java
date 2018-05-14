package it.uniroma2.dicii.bd.progetto.filament;

public class FilamentWithBorderPoints {
	
	private String satellite;
	private String filament;
	private double latitude;
	private double longitude;
	
	public FilamentWithBorderPoints() {}
	
	public FilamentWithBorderPoints(String satellite, String filament, double latitude, double longitude) {
		this.satellite = satellite;
		this.filament = filament;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public String getSatellite() {
		return satellite;
	}
	public void setSatellite(String satellite) {
		this.satellite = satellite;
	}
	public String getFilament() {
		return filament;
	}
	public void setFilament(String filament) {
		this.filament = filament;
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
	
}
