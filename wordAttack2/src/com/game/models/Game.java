package com.game.models;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;

import com.game.activity.GameObserverInterface;
import com.game.interfaces.StageInterface;
import com.game.interfaces.TeamActionReceiver;

public class Game extends TimerTask implements TeamActionReceiver, StateInterfaceObserver{
	

	Team one;
	Team two;
	StageInterface stage;
	ArrayList<GameObserverInterface> gameObservers;
	int count;
	int max = 10;
	boolean hasGameEnded = false;
	
	private final int NO_WINNER = 0;
	private final int ONE_WINS = 1;
	private final int TWO_WINS = 2;
	
	Timer timer;
	int scoreIncrement = 1;
	
	/**
	 * @return the one
	 */
	public Team getOne() {
		return one;
	}

	/**
	 * @param one the one to set
	 */
	public void setOne(Team one) {
		this.one = one;
		one.register(this);
	}

	/**
	 * @return the two
	 */
	public Team getTwo() {
		return two;
	}

	/**
	 * @param two the two to set
	 */
	public void setTwo(Team two) {
		this.two = two;
		two.register(this);
	}

	
	/**
	 * @return the stage
	 */
	public StageInterface getStage() {
		return stage;
	}

	/**
	 * @param stage the stage to set
	 */
	public void setStage(StageInterface stage) {
		this.stage = stage;
		stage.register(this);
	}


	
	public Game(){
		gameObservers = new ArrayList<GameObserverInterface>();
		timer = new Timer();
		count = 0;
	}
	
	public void addTeam(Team t){
		if(one == null){
			setOne(t);
		}else{
			setTwo(t);
		}
	}

	public void start() {
		timer.scheduleAtFixedRate(this, 500,200);
	}
		
	@Override
	public void onTeamActionReceived(Team team, Integer lineNumber, Player player) {
		int winner = getWinner();
		if(winner == NO_WINNER){
			if(one == team){
				stage.add(lineNumber, 1);
			}else if(two == team){
				if( stage.remove(lineNumber)){
					player.setScore(player.getScore() + scoreIncrement);
					//Log.e("Score", "Increase in score of Player " + player.getPc().getEmail() + " to " + player.getScore());
				}else{
					
				}
			}else{
				Log.d("Tag", "Invalid Intent");
			}
		}
	}
	
	@Override
	public void run() {
		count++;
		count = count % max;
		if(count == 0 || count == max / 2){
			stage.tick();
		}
		
		int winner = getWinner();
		ArrayList<ArrayList<Integer>>data = stage.getStageState();
		for(GameObserverInterface gameObserver: gameObservers){
			
			if(winner != NO_WINNER){
				stage.reset();
				
				gameObserver.onGameStateChanged(data, two.getHealth(), two.getTotalScore());
				
				gameObserver.gameHasEnded(this, winner);
				hasGameEnded = true;
			} else {
				//Log.e("SEnding", "sending");
				
				
				gameObserver.onGameStateChanged(data, two.getHealth(), two.getTotalScore());
				
				
				
				//gameObserver.sendScores(two.getHealth(), two.getTotalScore());
			}
		}
		
	}


	public int getWinner(){
		int winner = NO_WINNER;
		if(two.getHealth() <= 0){
			winner = ONE_WINS;
		} else {
			int totalScore = 0;
			ArrayList<Player> players = two.getPlayers();
			for(Player player : players){
				totalScore += player.getScore();
			}
			
			if(totalScore > 20){
				winner = TWO_WINS;
			}
		}
		return winner;
	}
	
	public void register(GameObserverInterface observer) {
		gameObservers.add(observer);	
	}

	@Override
	public void objectMovedToEnd() {
		ArrayList<Player> players = one.getPlayers();
		two.setHealth(two.getHealth() - 1);
		for(Player player : players){
			player.setScore(player.getScore() + scoreIncrement);
		}
	}	
}
