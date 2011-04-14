package com.game.factories;

import com.game.models.Player;

public class PlayerFactory {
	public PlayerFactory(){
		
	}
	
	public Player createPlayer(){
		PlayerCredentialFactory cf = new PlayerCredentialFactory();
		Player p = new Player(cf.generateCredential());
		return p;
	}
}
