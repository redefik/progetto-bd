package it.uniroma2.dicii.bd.progetto.filament;

import java.util.ArrayList;

// Gli oggetti della classe incapsulano le informazioni derivate di un filamento(coordinate del centroide, estensione del contorno
// e numero di segmenti)
public class FilamentInfo {
	
	private String name;
	private double centroidLatitude;
	private double centroidLongitude;
	private double borderLength;
	private int numberOfSegments;
	
	public FilamentInfo() {
		
	}
	
	public FilamentInfo(double centroidLatitude, double centroidLongitude,  double borderLength, int numberOfSegments) {
		this.centroidLatitude = centroidLatitude;
		this.centroidLongitude = centroidLongitude;
		this.borderLength = borderLength;
		this.numberOfSegments = numberOfSegments;
	}
	
	public String getName() {
		return this.name;
	}

	public double getCentroidLatitude() {
		return centroidLatitude;
	}

	public double getCentroidLongitude() {
		return centroidLongitude;
	}

	public double getBorderLength() {
		return borderLength;
	}

	public int getNumberOfSegments() {
		return numberOfSegments;
	}
	
	// il metodo calcola la coordinata i del centroide come media delle coordinate i dei punti del contorno
	public void setCentroid(ArrayList<BorderPointFilament> borderPoints) {
		
		double latitudeSum = 0;
		double longitudeSum = 0;
		int numOfPoints = borderPoints.size();
		
		for (BorderPointFilament borderPoint : borderPoints) {
			latitudeSum += borderPoint.getPointLatitude();
			longitudeSum += borderPoint.getPointLongitude();
		}
		
		double avgLatitude = latitudeSum / numOfPoints;
		double avgLongitude = longitudeSum / numOfPoints;
		this.centroidLatitude = avgLatitude;
		this.centroidLongitude = avgLongitude;
	}
	
	// L'estensione del contorno è calcolata come distanza tra il minimo e il massimo delle posizioni longitudinali e tra il minimo
	// e il massimo delle posizioni longitudinali
	public void setBorderLength(ArrayList<BorderPointFilament> borderPoints) {
		double minLatitude = borderPoints.get(0).getPointLatitude();
		double maxLatitude = borderPoints.get(0).getPointLatitude();
		double minLongitude = borderPoints.get(0).getPointLongitude();
		double maxLongitude = borderPoints.get(0).getPointLongitude();
		
		for (int i = 1; i < borderPoints.size(); ++i) {
			double latitude = borderPoints.get(i).getPointLatitude();
			double longitude = borderPoints.get(i).getPointLongitude();
			if (latitude < minLatitude) {
				minLatitude = latitude;
			}
			if (latitude > maxLatitude) {
				maxLatitude = latitude;
			}
			if (longitude < minLongitude) {
				minLongitude = longitude;
			}
			if (longitude > maxLongitude) {
				maxLongitude = longitude;
			}
		}
		
		this.borderLength = Math.sqrt(Math.pow(maxLatitude - minLatitude, 2) + Math.pow(maxLongitude - minLongitude, 2));
	}
	
	public void setNumberOfSegments(int numberOfSegments) {
		this.numberOfSegments = numberOfSegments;
	}

	public void setName(String name) {
		this.name = name;
		
	}
}
