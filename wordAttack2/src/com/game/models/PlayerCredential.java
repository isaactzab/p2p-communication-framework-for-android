package com.game.models;

public class PlayerCredential {
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	String email;
	
	public PlayerCredential(String email){
		setEmail(email);
	}
	
	public Boolean equals(PlayerCredential pc){
		return pc.getEmail().equals(this.getEmail());
	}
	
	public String toString(){
		return email;
	}
}
