package com.game.factories;

import java.util.ArrayList;
import java.util.Arrays;

import com.game.models.PlayerCredential;

public class PlayerCredentialFactory {
	
	ArrayList<String> emails;
	Integer currentIndex;
	
	public PlayerCredentialFactory(){
		String[] words = {"ace@gmail", "boom@gmail.com", "crew@gmail.com", "dog@gmail.com", "eon@gmail.com", "ace2@gmail", "boom2@gmail.com", "crew2@gmail.com", "dog2@gmail.com", "eon2@gmail.com", ""};  	   
		emails = new ArrayList<String>();
		
		for(int i = 0; i >= 0; i++){
			String curr = words[i];
			if(curr == "")
				break;
			emails.add(curr);
		}
		currentIndex = 0;
	}
	
	public PlayerCredential generateCredential(){
		currentIndex = currentIndex % 10;
		PlayerCredential credential = new PlayerCredential(emails.get(currentIndex));
		currentIndex++;
		return credential;
	}
}
