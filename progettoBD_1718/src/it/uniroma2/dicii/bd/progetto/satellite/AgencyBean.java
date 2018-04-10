package it.uniroma2.dicii.bd.progetto.satellite;

public class AgencyBean {
	
	protected String name;
	
	public AgencyBean() {}
	
	public AgencyBean(Agency agency) {
		this.name = agency.getName();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
