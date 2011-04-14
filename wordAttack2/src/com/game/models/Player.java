package com.game.models;

import java.util.ArrayList;

import com.game.interfaces.PlayerActionReceiver;

import android.util.Log;

public class Player {
	
	private ArrayList<PlayerActionReceiver> playerActionReceivers = new ArrayList<PlayerActionReceiver>();
	
	private PlayerCredential pc;
	private int score = 0;
	
	
	/**
	 * @return the score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(int score) {
		this.score = score;
	}

	public void register(PlayerActionReceiver playerActionReceiver){
		playerActionReceivers.add(playerActionReceiver);
	}
	
	
	/**
	 * @return the pc
	 */
	public PlayerCredential getPc() {
		return pc;
	}

	
	/**
	 * @param pc the pc to set
	 */
	public void setPc(PlayerCredential pc) {
		this.pc = pc;
	}
	
	public Player(PlayerCredential pc){
		this.pc = pc;
	}
	
	public Player(){
		
	}

	public boolean equals(Player p) {
		return pc.equals(p.getPc());
	}
	
	public void sendMessage(String message){
		for(PlayerActionReceiver par : playerActionReceivers){
			par.onPlayerAction(this, message);
		}
	}
}
