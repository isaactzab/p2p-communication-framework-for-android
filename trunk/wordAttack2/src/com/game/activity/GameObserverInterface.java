package com.game.activity;

import java.util.ArrayList;

import com.game.models.Game;

public interface GameObserverInterface {
	public void onGameStateChanged(ArrayList<ArrayList<Integer> > gameState, int i, int j);

	public void gameHasEnded(Game game, int winner);

	public void sendScores(int health, int totalScore);
}
