package it.uniroma2.dicii.bd.progetto.filament;

import java.util.ArrayList;

public class FilamentWithSegments {
	
	private String name;
	private ArrayList<Integer> segments;
	private int currentNumberOfSegments;
	
	public FilamentWithSegments(String name, int currentNumberOfSegments) {
		this.name = name;
		this.currentNumberOfSegments = currentNumberOfSegments;
		segments = new ArrayList<Integer>();
	}
	
	public void updateSegments(int segment) {
		if (!segments.contains(segment)) {
			segments.add(segment);
			++currentNumberOfSegments;
		}
	}
	
	public int getNumberOfSegments() {
		return currentNumberOfSegments;
	}
	
	public String getName() {
		return this.name;
	}
}
