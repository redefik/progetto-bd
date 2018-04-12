package it.uniroma2.dicii.bd.progetto.satellite;

import java.util.ArrayList;
import java.util.Date;

public class Satellite {
	
	private String name;
	private Date beginDate;
	private Date endDate;
	private ArrayList<Instrument> instruments;
	
	public Satellite(SatelliteBean satelliteBean) {
		
		this.name = satelliteBean.getName();
		this.beginDate = satelliteBean.getBeginDate();
		this.endDate = satelliteBean.getEndDate();
		this.instruments = new ArrayList<Instrument>();
		
		for (InstrumentBean instrumentBean : satelliteBean.getInstrumentBeans()) {
			Instrument instrument = new Instrument(instrumentBean);
			instruments.add(instrument);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public ArrayList<Instrument> getInstruments() {
		return instruments;
	}

	public void setInstruments(ArrayList<Instrument> instruments) {
		this.instruments = instruments;
	}
	
	
}
