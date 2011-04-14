package com.game.models;

import java.util.ArrayList;

import android.util.Log;

import com.game.interfaces.StageInterface;

public class Stage implements StageInterface{
	
	private ArrayList<ArrayList<Integer> > gameMap;
	private ArrayList<StateInterfaceObserver> observers;
		
	
	public void register(StateInterfaceObserver observer){
		if(observers == null){
			observers = new ArrayList<StateInterfaceObserver>();
		}
		observers.add(observer);
	}
	
	/**
	 * @return the gameMap
	 */
	public ArrayList<ArrayList<Integer>> getGameMap() {
		return gameMap;
	}

	public Stage(ArrayList<ArrayList<Integer> > aMap){
		this.gameMap = aMap;
	}
	
	public void add(Integer lineNumber, Integer value){
		ArrayList<Integer> line = gameMap.get(lineNumber);
		line.set(0, value);
	}
	
	public void tick(){
		updateGameMapToNextIteration();
	}

	private void updateGameMapToNextIteration() {
		for(int i = 0; i < gameMap.size(); i++){
			ArrayList<Integer> line = gameMap.get(i);
			for( int j = line.size() - 1; j > 0; j--){
				if(j == line.size() - 1 && line.get(j) == 1){
					objectDestroyed();
				}
				line.set(j, line.get(j - 1));
			}
			line.set(0, 0);
		}
	}
	
	public Integer getRows(){
		return this.gameMap.size();
	}
	
	public Integer getColumns(){
		return this.gameMap.get(0).size();	
	}

	@Override
	public boolean remove(Integer lineNumber) {
		boolean success = false;
		ArrayList<Integer> line = gameMap.get(lineNumber);
		for(int i = line.size() -1; i >= 0; i--){
			if(line.get(i) == 1){
				line.set(i, 0);
				success = true;
				break;
			}
		}
		return success;
	}
	
	public Stage(){
		//Log.e("init", "observer init");
		observers = new ArrayList<StateInterfaceObserver>();
	}
	

	/* (non-Javadoc)
	 * @see com.game.interfaces.StageInterface#objectDestroyed()
	 */
	@Override
	public void objectDestroyed() {
		for(StateInterfaceObserver so : observers){
			so.objectMovedToEnd();
		}
	}

	@Override
	public ArrayList<ArrayList<Integer>> getStageState() {
		return gameMap;
	}

	@Override
	public void reset() {
		for(int i = 0; i < gameMap.size(); i++){
			ArrayList<Integer> line = gameMap.get(i);
			for( int j = line.size() - 1; j > 0; j--){
				line.set(j, 0);
			}
			line.set(0, 0);
		}
	}
}
