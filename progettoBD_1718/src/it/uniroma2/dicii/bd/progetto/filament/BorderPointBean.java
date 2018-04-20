package it.uniroma2.dicii.bd.progetto.filament;

import java.util.ArrayList;

public class BorderPointBean {
	
	private double latitude;
	private double longitude;
	private String satellite;
	private ArrayList<String> filamentNames = new ArrayList<String>();
	
	public BorderPointBean() {}
	
	public BorderPointBean(double latitude, double longitude, String satellite, ArrayList<String> filamentNames) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.satellite = satellite;
		this.filamentNames = filamentNames;
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
	public String getSatellite() {
		return satellite;
	}
	public void setSatellite(String satellite) {
		this.satellite = satellite;
	}
	public ArrayList<String> getFilamentNames() {
		return filamentNames;
	}
	public void setFilamentNames(ArrayList<String> filamentNames) {
		this.filamentNames = filamentNames;
	}
	
	@Override
	public String toString() {
		String string = "Punto del contorno a [" + this.latitude + "],[" + this.longitude + "] \n Filamenti a cui appartiene: ";
		for (String f : this.getFilamentNames()) {
			string = string + "\n - " + f ;
		}
		string = string + "\n";
		return string;
	}
	
}
