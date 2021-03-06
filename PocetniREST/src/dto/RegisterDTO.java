package dto;

import java.io.Serializable;


@SuppressWarnings("serial")
public class RegisterDTO implements Serializable{

	private String userName;
	private String password;
	private String name;
	private String surname;
	private String userGender;
	private String userRole;
	
	public RegisterDTO() {
		
	}

	public String getUserRole() {
		return userRole;
	}



	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}



	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getUserGender() {
		return userGender;
	}

	public void setUserGender(String userGender) {
		this.userGender = userGender;
	}

	@Override
	public String toString() {
		return "RegisterDTO [userName=" + userName + ", password=" + password + ", name=" + name + ", surname="
				+ surname + ", userGender=" + userGender + "]";
	}
	
	
}
