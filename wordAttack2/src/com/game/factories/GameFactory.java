package com.game.factories;

import com.game.models.Game;
import com.game.models.Stage;
import com.game.models.Team;

public class GameFactory {
	public GameFactory(){
		
	}
	
	public Game createDefaultGame(){
		TeamFactory tf = new TeamFactory();
		StageFactory sf = new StageFactory();
		
		Stage stage = sf.createDefaultStage();
		Team teamOne = tf.createFirstTeam();
		Team teamTwo = tf.createSecondTeam();
		
		Game g = new Game();
		
		g.setStage(stage);
		g.addTeam(teamOne);
		g.addTeam(teamTwo);
		
		return g;
	}
}
