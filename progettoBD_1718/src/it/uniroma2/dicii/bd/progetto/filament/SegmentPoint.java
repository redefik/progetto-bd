package it.uniroma2.dicii.bd.progetto.filament;

public class SegmentPoint {
	
	private double latitude;
	private double longitude;
	private int segmentId;
	private int progNumber;
	private char type;
	private Filament filament;
	
	public SegmentPoint() {
		
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

	public int getSegmentId() {
		return segmentId;
	}

	public void setSegmentId(int segmentId) {
		this.segmentId = segmentId;
	}


	

	public int getProgNumber() {
		return progNumber;
	}

	public void setProgNumber(int progNumber) {
		this.progNumber = progNumber;
	}

	public char getType() {
		return type;
	}

	public void setType(char type) {
		this.type = type;
	}

	public Filament getFilament() {
		return filament;
	}

	public void setFilament(Filament filament) {
		this.filament = filament;
	}
	
	
	
}
