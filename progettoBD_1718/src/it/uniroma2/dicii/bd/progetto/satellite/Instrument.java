package it.uniroma2.dicii.bd.progetto.satellite;

public class Instrument {

	private String name;
	private String listBands;
	
	public Instrument(String name, String listBands) {
		this.name = name;
		this.listBands = listBands;
	}
	
	public Instrument(InstrumentBean instrumentBean) {
		this.name = instrumentBean.getName();
		this.listBands = instrumentBean.getListBands();
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
	
	
}
