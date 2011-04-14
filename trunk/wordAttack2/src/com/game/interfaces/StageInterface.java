package com.game.interfaces;

import java.util.ArrayList;

import com.game.models.StateInterfaceObserver;

public interface StageInterface {
	public void add(Integer lineNumber, Integer value);
	public void tick();
	public boolean remove(Integer lineNumber);
	
	public void objectDestroyed();
	public void register(StateInterfaceObserver observer);
	public ArrayList<ArrayList<Integer> > getStageState();
	public void reset();
}
