package it.uniroma2.dicii.bd.progetto.satellite;

import javafx.scene.control.CheckBox;

public class SelectableAgencyBean extends AgencyBean{
	
	private CheckBox checkBox;
	
	public SelectableAgencyBean() {}
	
	public SelectableAgencyBean(String name) {
		super();
		super.setName(name);
		checkBox = new CheckBox();
	}

	public CheckBox getCheckBox() {
		return checkBox;
	}

	public void setCheckBox(CheckBox isSelected) {
		this.checkBox = isSelected;
	}

	
	
}
