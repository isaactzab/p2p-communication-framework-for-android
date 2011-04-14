package com.game.factories;

import com.game.models.Team;

public class TeamFactory {
	
	private PlayerFactory pf;
	private int teamSize = 5;
	
	public TeamFactory(){
		pf = new PlayerFactory();
	}
	
	
	public Team createFirstTeam(){
		return createRandomTeam();
	}
	
	public Team createSecondTeam(){
		return createRandomTeam();
	}
	
	private Team createRandomTeam(){
		Team t = new Team();
		
			t.join(pf.createPlayer());
		return t;
	}
}
