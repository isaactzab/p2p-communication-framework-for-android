package com.game.factories;

import java.util.ArrayList;
import java.util.Collections;

import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

import com.game.models.Stage;

public class StageFactory {
	public StageFactory(){

	}
	
	public Stage createStage(Integer rows, Integer columns){
		
		ArrayList<ArrayList <Integer> > map = new ArrayList<ArrayList <Integer> >();
		for(int i = 0; i < rows; i++){			
			ArrayList<Integer> row = new ArrayList<Integer>(
				    Collections.nCopies(columns, 0)); 
			map.add(row);
		}
		
		return new Stage(map);
	}
	
	public  Stage createDefaultStage() {
		return createStage(4, 10);
	}
}
