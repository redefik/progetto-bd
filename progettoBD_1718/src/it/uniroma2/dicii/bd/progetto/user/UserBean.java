package it.uniroma2.dicii.bd.progetto.user;

public class UserBean {
	
	private String username;
	private String password;
	private String nome; 
	private String cognome;
	private int type; 
	private String mail;
	
	public UserBean() {}
	
	public UserBean(User user) {
		this.username = user.getUsername();
		this.password = user.getPassword();
		this.nome = user.getNome();
		this.cognome = user.getCognome();
		this.type = user.getType();
		this.mail = user.getMail();
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
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getCognome() {
		return cognome;
	}
	public void setCognome(String cognome) {
		this.cognome = cognome;
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
