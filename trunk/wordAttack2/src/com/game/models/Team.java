package com.game.models;

import java.util.ArrayList;

import com.game.interfaces.PlayerActionReceiver;
import com.game.interfaces.TeamActionReceiver;

import android.util.Log;

public class Team implements PlayerActionReceiver{
	
	ArrayList<Player> players;
	ArrayList<TeamActionReceiver> teamActionInterfaces;
	int health = 5;
	
	/**
	 * @return the health
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * @param health the health to set
	 */
	public void setHealth(int health) {
		if(health >= 0){
			this.health = health;
		}
	}

	/**
	 * @return the players
	 */
	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	public void register(TeamActionReceiver tar){
		teamActionInterfaces.add(tar);
	}

	/**
	 * @param players the players to set
	 */
	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}

	public Team(){
		this.players = new ArrayList<Player>();
		this.teamActionInterfaces = new ArrayList<TeamActionReceiver>();
	}
	
	public void join(Player p){
		players.add(p);
		p.register(this);
	}
	
	public Integer size(){
		return this.players.size();
	}

	@Override
	public void onPlayerAction(Player player, String action) {
		for(TeamActionReceiver teamActionInterface : teamActionInterfaces){
			//Log.i("String", action);
			//Log.i("Integer", "" + Integer.parseInt( action ));
			teamActionInterface.onTeamActionReceived(this, Integer.parseInt( action ), player);
		}
	}

	public int getTotalScore() {
		int total =0;
		for(Player p : players){
			total += p.getScore();
		}
		return total;
	}
}
