package com.gui.app;

import java.util.ArrayList;

import net.clc.bt.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.view.View;

public class ShapeView extends View {
    private ShapeDrawable[] mDrawables;
    private Drawable[] lives;
    
    private ArrayList<ArrayList<ShapeDrawable> >map;
    
    private final int RED = 0xFFFF0000;
    private final int BLUE = 0xFF0000FF;
    private final int LIGHT_BLUE = 0xFF0FF0FF;
    private final int GREEN =  0xFF00FF00;
    
    private final int NUMBER_OF_COLUMNS = 4;
    private final int NUMBER_OF_ROWS = 8;
    
    private final int X = 20;
    private final int Y = 0;
    
    private final int WIDTH = 35;
    private final  int HEIGHT = 523;
    
    private final int f2 = 29;
    private final int g2 = 120;
    private int width3 = 35;
    private int height3 = (int) 35;
    
    private Context cont;
    
    private ArrayList<ArrayList<Integer>> drawState;
    
    private int windowWidth;
    private int windowHeight;
    private Context context;
    private int hearts = 5;
    private String score;
    /**
	 * @return the hearts
	 */
	public int getHearts() {
		return hearts;
	}

	/**
	 * @param hearts the hearts to set
	 */
	public void setHearts(int hearts) {
		this.hearts = hearts;
	}
	
	public String getScore() {
		return score;
	}

	/**
	 * @param hearts the hearts to set
	 */
	public void setScore(String score) {
		this.score = score;
	}

	public ArrayList<ArrayList<Integer>> getDrawState() {
		return drawState;
	}

	public void setDrawState(ArrayList<ArrayList<Integer>> drawState) {
		this.drawState = drawState;
		//Log.i("Tag", "invalidating");
		 invalidate();
	}

	public ShapeView(Context context) {
        super(context);
        
        //this.windowHeight = HEIGHT;
        //this.windowWidth = WIDTH;
        
       // setFocusable(true);         
        //initializeCells();
       // initializeBackground();
       // drawState = new ArrayList<ArrayList<Integer> >();
    }  
	
	public ShapeView(Context context, int windowHeight, int windowWidth) {
        super(context);
        
        this.context = context;
        this.windowHeight = windowHeight;
        this.windowWidth = windowWidth;
        
        
        Resources res = context.getResources();
        score = "";
        
        setFocusable(true);         
        initializeCells();
        initializeBackground();
        
        setHearts(5);
        lives  = new Drawable[5];
        for(int i = 0; i < hearts; i ++){
        	lives[i] = res.getDrawable(R.drawable.heart);
        }
        drawState = new ArrayList<ArrayList<Integer> >();
        
    }  

	
	int count = 0;
    @Override 
    protected void onDraw(Canvas canvas) {     
        //mDrawables4 for cells
    	//Log.d("Tag", "drawing " + count);
    	count++;
    	setTurnedOnLights();
    	drawBackgroundLines(canvas, X, (windowHeight/10), windowWidth / 8, windowHeight); 
        drawCells(canvas, f2, width3, height3);
        drawHearts(canvas);
    }
    
   
    
	private void drawHearts(Canvas canvas){
		int numberOfHearts = getHearts();
		String score = getScore();
		Paint paint = new Paint(); 
		//paint.setColor(Color.WHITE); 
		//paint.setStyle(Style.FILL); 
		//canvas.drawPaint(paint); 

		paint.setColor(Color.WHITE); 
		paint.setTextSize(20); 
		canvas.drawText(score, 10, 25, paint); 
		
		
		
		//	Paint p = new Paint();
		//p.setTextSize(16);
		//canvas.drawText("Aayush is the best", 0, 0, p);
		for (int i = 0; i < numberOfHearts; i++){
			Drawable dr = lives[i];
			
    		int topLeftX = (windowWidth/8)*3 + (windowWidth/8)*i;
    		int topLeftY = (windowHeight/10) - ((windowHeight/10)/2);
    		int bottomRightX = topLeftX + (WIDTH);
    		int bottomRightY = topLeftY + (height3);
    		dr.setBounds(topLeftX, topLeftY, bottomRightX, bottomRightY);
			dr.draw(canvas); 
		}
	}

	private int drawCells(Canvas canvas, int f2, int width3, int height3) {
		for(int i = 0; i < NUMBER_OF_COLUMNS; i++){
        	for(int j = 0; j < NUMBER_OF_ROWS; j++){
        		
        		Drawable dr = map.get(i).get(j);
        	
        		int topLeftX = f2;
        		int topLeftY = ((int)80 *j) + (windowHeight/10);
        		int bottomRightX = topLeftX + (WIDTH);
        		int bottomRightY = topLeftY + (height3);
        		//Log.d("Coords", " "+ topLeftX+" " +topLeftY+" " +bottomRightX+ " " +bottomRightY);
                dr.setBounds(topLeftX, topLeftY, bottomRightX, bottomRightY);
                dr.draw(canvas);            
         
        	}
        	f2 = f2 + (windowWidth/8)*2;
        	
        }
		return f2;
	}


	
	

	
	private void drawBackgroundLines(Canvas canvas, int x, int y, int width,
			int height) {
		for (Drawable dr : mDrawables) {
            dr.setBounds(x, y, x + width, y + height);
            dr.draw(canvas);                
            x += width ;
        }
		
	}
	private void initializeBackground() {
		mDrawables = new ShapeDrawable[7];
        mDrawables[0] = new ShapeDrawable(new RectShape());
        mDrawables[1] = new ShapeDrawable(new RectShape());
        mDrawables[2] = new ShapeDrawable(new RectShape());
        mDrawables[3] = new ShapeDrawable(new RectShape());
        mDrawables[4] = new ShapeDrawable(new RectShape());
        mDrawables[5] = new ShapeDrawable(new RectShape());
        mDrawables[6] = new ShapeDrawable(new RectShape());
        
        mDrawables[0].getPaint().setColor(0xFFFF0000);
        mDrawables[1].getPaint().setColor(0x00000000);
        mDrawables[2].getPaint().setColor(0xFF0000FF);
        mDrawables[3].getPaint().setColor(0x00000000);
        mDrawables[4].getPaint().setColor(0xFF0FF0FF);
        mDrawables[5].getPaint().setColor(0x00000000);
        mDrawables[6].getPaint().setColor(0xFF00FF00);
	}
	
	private void initializeCells() {
		int[] initialColours = {RED, BLUE, LIGHT_BLUE, GREEN   };
        map = new ArrayList<ArrayList<ShapeDrawable> >();
    
        for(int i = 0; i < NUMBER_OF_COLUMNS; i++){
        	ArrayList<ShapeDrawable>line = new ArrayList<ShapeDrawable>();
        	for(int j = 0; j < NUMBER_OF_ROWS; j++){
        		ShapeDrawable sd =  new ShapeDrawable(new OvalShape());
        		sd.getPaint().setColor(initialColours[i]);
        		line.add(sd);
        	}
        	map.add(line);
        }
	}

	private void setTurnedOnLights(){
		if (drawState.isEmpty()) return;
		
		int[] turnedOnLights = { BLUE,RED,  GREEN,  LIGHT_BLUE };
		int[] backGroundColours = {RED, BLUE, LIGHT_BLUE, GREEN };
		
        for(int i = 0; i < NUMBER_OF_COLUMNS; i++){
        	for(int j = 0; j < NUMBER_OF_ROWS; j++){
        		
        		
        		if(drawState.get(i).get(j) == 1){
        			lightUp(j,i,turnedOnLights[i]);
        		} else {
        			lightUp(j,i,backGroundColours[i]);
        		}
        		
        		
        	}
        }
	}
	private void lightUp(int row, int column, int colour){
		map.get(column).get(row).getPaint().setColor(colour);
	}

}



