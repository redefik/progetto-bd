package it.uniroma2.dicii.bd.progetto.user;

public class User {

	private String username;
	private String password;
	private String firstName; 
	private String lastName;
	private int type; 
	private String mail;
	
	public User() {}
	
	public User(String username, String password, String nome, String cognome, String mail, int type) {
		this.username = username;
		this.password = password; 
		this.firstName = nome;
		this.lastName = cognome; 
		this.type = type;
		this.mail = mail; 
		
	}
	
	public User(UserBean userBean) {
		this.username = userBean.getUsername();
		this.password = userBean.getPassword();
		this.firstName = userBean.getFirstName();
		this.lastName = userBean.getLastName();
		this.type = userBean.getType();
		this.mail = userBean.getMail();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}
	
	
	
}
