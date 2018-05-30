package it.uniroma2.dicii.bd.progetto.user;

public class UserBean {
	
	private String username;
	private String password;
	private String firstName; 
	private String lastName;
	private int type; 
	private String mail;
	
	public UserBean() {}
	
	public UserBean(User user) {
		this.username = user.getUsername();
		this.password = user.getPassword();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
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
	
	@Override
	public boolean equals(Object otherObject) {
		if (otherObject == null) {
			return false;
		}
		if (this == otherObject) {
			return true;
		}
		if (getClass() != otherObject.getClass()) {
			return false;
		}
		UserBean otherUser = (UserBean) otherObject;
		boolean equalityCondition = 
				otherUser.getUsername().equals(this.username) && otherUser.getFirstName().equals(this.firstName) &&
				otherUser.getLastName().equals(this.lastName) && otherUser.getMail().equals(this.mail) &&
				otherUser.getPassword().equals(this.password) && otherUser.getType() == this.type;
		return equalityCondition;
	}
	
}
