package it.uniroma2.dicii.bd.progetto.satellite;

import java.util.ArrayList;
import java.util.Date;

public class SatelliteBean {

	private String name;
	private Date beginDate;
	private Date endDate;
	private ArrayList<InstrumentBean> instrumentBeans;
	
	public SatelliteBean(String name, Date beginDate, Date endDate) {
		
		this.name = name;
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.instrumentBeans = new ArrayList<InstrumentBean>();
	}

	public SatelliteBean(Satellite satellite) {
			
			this.name = satellite.getName();
			this.beginDate = satellite.getBeginDate();
			this.endDate = satellite.getEndDate();
			this.instrumentBeans = new ArrayList<InstrumentBean>();
			
			for (Instrument instrument : satellite.getInstruments()) {
				InstrumentBean instrumentBean = new InstrumentBean(instrument);
				instrumentBeans.add(instrumentBean);
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

	public ArrayList<InstrumentBean> getInstrumentBeans() {
		return instrumentBeans;
	}

	public void setInstrumentBeans(ArrayList<InstrumentBean> instrumentBeans) {
		this.instrumentBeans = instrumentBeans;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
