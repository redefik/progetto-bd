package it.uniroma2.dicii.bd.progetto.satellite;

public class InstrumentBean {
	
	private String name;
	private String listBands;
	
	public InstrumentBean(Instrument instrument) {
		this.name = instrument.getName();
		this.listBands = instrument.getListBands();
	}

	public InstrumentBean(String name, String listBands) {
		this.name = name;
		this.listBands = listBands;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getListBands() {
		return listBands;
	}

	public void setListBands(String listBands) {
		this.listBands = listBands;
	}
	
	public boolean equals(InstrumentBean instrumentBean) {
		return (this.name.equals(instrumentBean.getName()));
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
