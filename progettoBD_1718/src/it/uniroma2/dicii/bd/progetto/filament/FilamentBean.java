package it.uniroma2.dicii.bd.progetto.filament;

public class FilamentBean {
	
	private String name;
	private int number;
	private int numberOfSegments;
	private double ellipticity;
	private double contrast;
	private String instrumentName;
	
	public FilamentBean() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getNumberOfSegments() {
		return numberOfSegments;
	}

	public void setNumberOfSegments(int numberOfSegments) {
		this.numberOfSegments = numberOfSegments;
	}

	public double getEllipticity() {
		return ellipticity;
	}

	public void setEllipticity(double ellipticity) {
		this.ellipticity = ellipticity;
	}

	public double getContrast() {
		return contrast;
	}

	public void setContrast(double contrast) {
		this.contrast = contrast;
	}

	public String getInstrumentName() {
		return instrumentName;
	}

	public void setInstrumentName(String instrumentName) {
		this.instrumentName = instrumentName;
	}
	
	
	
}
