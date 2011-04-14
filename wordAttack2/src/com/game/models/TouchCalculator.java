package com.game.models;

public class TouchCalculator {
	private int screenWidth;
	private int numberOfBlocks = 4;
	
	public TouchCalculator(){
		this.screenWidth = 0;
	}
	
	public TouchCalculator(int screenWidth){
		this.screenWidth = screenWidth;
	}
	
	public int getTouchIndex(float x){
		float screenBlockSize = (float)screenWidth /  numberOfBlocks;
		int segmentChoice = 0;
		if(x < screenBlockSize){
			segmentChoice = 0;
		} else if (x < screenBlockSize * 2){
			segmentChoice = 1;
		} else if (x < screenBlockSize * 3){
			segmentChoice = 2;
		} else {
			segmentChoice = 3;
		}
		return segmentChoice;
		
	}
}
