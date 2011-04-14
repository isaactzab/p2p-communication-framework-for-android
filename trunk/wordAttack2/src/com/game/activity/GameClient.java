package com.game.activity;

import indexServerClient.ChatRoom.onlineUser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.clc.bt.Connection;
import net.clc.bt.DataPacket;
import net.clc.bt.DeviceListActivity;
import net.clc.bt.MainActivity;
import net.clc.bt.R;
import net.clc.bt.BTChat.PhotoAdapter;
import net.clc.bt.Connection.OnConnectionLostListener;
import net.clc.bt.Connection.OnConnectionServiceReadyListener;
import net.clc.bt.Connection.OnIncomingConnectionListener;
import net.clc.bt.Connection.OnMaxConnectionsReachedListener;
import net.clc.bt.Connection.OnMessageReceivedListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.EE4210.GameRoom.GameRoomLogin;
import com.EE4210.GameRoom.GameRoomMain;
import com.game.models.PlayerCredential;
import com.game.models.TouchCalculator;
import com.gui.app.ShapeView;
import com.gui.app.SoundManager;
import com.networking.game.PlayerProxy;

public class GameClient extends Activity implements OnTouchListener {

	private static final int LEVEL1 = 1;
	
	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_CHANGE_DEVICE = 3;
    private static final int SEARCH_NETWORK_DEVICE = 4;
    private static final int CAMERA_APP = 5;
    private HashMap<String,String> networkDevicesAddresses;
    private PhotoAdapter pAdapter;
    private final boolean T = true;
    
	private PlayerProxy playerProxy;
	private String host;
	Connection mConnection;
	private TouchCalculator touchCalcultor;
	
	private ListView mConversationView;
	private ArrayAdapter<String> mConversationArrayAdapter;
	
	private String myTeam;

	private static final int DRAW = 9;
	private static final int PLAYER_INTENT = 1;
	private static final int START_GAME = 2;
	private static final int REGISTER_USER = 3;
	private static final int TELL_TEAM = 4;
	private static final int MSG_TO_ROOM = 5;
	private static final int SCORE = 6;
	private static final int END_GAME = 7;
	private static final int PRIVATE_CHAT = 8;

	private List<String> connectToWho;
	private List<onlineUser> usersInRoom;
	ShapeView gameView;

	
	
	private HashMap<String, OutputStream> outFileArray;

	private HashMap<String, String> outFilenameArray;
	private HashMap<String, Object> locks;
	
	
	private SoundManager mSoundManager;

	private final static int CREATING_GAME = 0;
	private final static int PLAYING_GAME = 1;
	int currentState;

	
	private String privateDevice = "all";
	
	
	private static final int HOST_INDEX = 0;

	final Handler mHandler = new Handler() {

		
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			//Log.i("msg", "attempting msg call");
			switch (msg.what) {
			case DRAW:
				
				//Log.i("Tag", "attempting draw");
				gameView.setDrawState((ArrayList<ArrayList<Integer>>) msg.obj);
				break;
			case START_GAME:
				//Log.i("Start Client", "Starting Game");
				startGame();
				break;
			case TELL_TEAM:
				myTeam = "" + msg.obj.toString();
				break;
			case MSG_TO_ROOM:
				String str = msg.obj.toString();
				pAdapter.add(str);
				break;	
			case SCORE:
				
				String scores = msg.obj.toString();
				String hearts = scores.substring(0,1);
				String score = scores.substring(1,scores.length());
				
				//Log.e("Hearts", hearts);
				//Log.e("score", score);
				gameView.setScore(score);
				gameView.setHearts(Integer.parseInt(hearts));		
				break;
			case PRIVATE_CHAT:
				String str2 = msg.obj.toString();
				Log.e("Message adapter", str2);
				pAdapter.add(str2);
				break;
			case END_GAME:
				String str3 = msg.obj.toString();
				//onGameEnd(str3);
				
				try{
					GameRoomLogin.iSC.leaveChatRoom(ChatRoomName);
				} catch (Exception c){
									
				}
				
				try{
					Toast.makeText(GameClient.this, "Team: " + str3 + " wins", Toast.LENGTH_LONG).show();
				mConnection.shutdown();
				} catch (Exception e){
					finish();
				}
				finish();
				break;
			}
		}

	};
	
    final Handler mHandler2 = new Handler(){
 	
    	
		/* (non-Javadoc)
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			//super.handleMessage(msg);
			String type = msg.obj.toString().substring(0, 3);
			if (T) Log.e(TAG,"Type:"+type);
    			if(type.compareTo("msg")==0){
    				String message = msg.obj.toString().substring(3);
				pAdapter.add(message);
    			}
    			else if(type.compareTo("end")==0){
    				String filename =  msg.obj.toString().substring(3);
				pAdapter.add("photo:"+filename);
			
    			}
    			else{
    				
    			}
		}
    		
    
    };

	
	
	private String makeRed(String msg){
		return "font color='red'>" + msg + "</font>";
	}
	
	Button btn1;
	ImageButton btn2;
	EditText inputbox;
	String GameRoomName;
	private static final String TAG = "GameClient";
	private String ChatRoomName;
	private String nickname;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		outFileArray = new HashMap<String, OutputStream>();
		outFilenameArray = new HashMap<String, String>();
		locks = new HashMap<String, Object>();

		
		connectToWho = GameRoomLogin.iSC.connectToWho();
		currentState = CREATING_GAME;

		nickname = GameRoomLogin.iSC.getUser().getUserName();
		mConnection = getConnection();
		joinGameScreen();

		usersInRoom = GameRoomLogin.iSC.getChatRoom(ChatRoomName).getUserList();
		// startGame();
	}

	private Connection getConnection() {
		return new Connection(this, serviceReadyListener);
	}

	int exp = -1;
	private int getExp(){
		exp = exp == -1 ? GameRoomLogin.iSC.getExp(getPlayerCredential().getEmail()) : exp ;
		return exp;
	}
	
	private void sendMessageToGameRoom(String msg){
		
			String frame = "" + MSG_TO_ROOM + msg;
			if(frame == null){
				Log.e("Message", "Null");
			}
			if(host == null){
				Log.e("Host", "NULL");
			}
			
			if (!(host == null && frame == null)){
				Log.e("Message", frame + " " + host);
				mConnection.sendMessage(host, frame.getBytes());
			}
	}
	
	private void joinGameScreen() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setTitle("WordAttack, ToAll");
		setContentView(R.layout.gameroomchat);
		btn1 = (Button) findViewById(R.id.button_send);
		btn2 = (ImageButton) findViewById(R.id.iBexit);
		Intent startingIntent = getIntent();
		ChatRoomName = startingIntent.getStringExtra("GameRoomToJoinFrom");
		inputbox = (EditText) findViewById(R.id.chatcontent);
//		mConversationView = (ListView) findViewById(R.id.in);
//		mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
//		mConversationView.setAdapter(mConversationArrayAdapter);
        pAdapter = new PhotoAdapter(this);
        mConversationView = (ListView) findViewById(R.id.in);
        //mConversationView.setAdapter(mConversationArrayAdapter);
        mConversationView.setAdapter(pAdapter);

        
		btn1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				if (host != null){
					String message = nickname + " SAYS: " + inputbox.getText().toString();
					
					if(privateDevice.equalsIgnoreCase("all")){
						sendMessageToGameRoom(message);
					} else {
						
						if(getExp() > LEVEL1){
							String message2 = "" + PRIVATE_CHAT + nickname + " SAYS:" + inputbox.getText().toString();
							mConnection.sendMessage(privateDevice, message2.getBytes());
							mHandler.obtainMessage(PRIVATE_CHAT, nickname + " SAYS:" + inputbox.getText().toString()).sendToTarget();
						}else {
							Toast.makeText(GameClient.this, "You need to have at least " + LEVEL1 + " exp to whisper", Toast.LENGTH_SHORT).show();
						}

					}
					inputbox.setText("");
				} else {
					String registerUser = "" + REGISTER_USER
					+ GameRoomLogin.iSC.getUser().getEmail();
					Log.e("Sending Credentials", registerUser);

					List<String> devicesToConnectTo = getDevicesToConnectTo();
					sendToAllDevices(devicesToConnectTo, registerUser);
				}
			}
		});

		btn2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (GameRoomLogin.iSC.leaveChatRoom(ChatRoomName)) {
					Log.d(TAG, "Button Exit is click");
					Toast.makeText(getApplicationContext(),
							"You leave Game Room " + ChatRoomName,
							Toast.LENGTH_SHORT).show();
					Intent j = new Intent(GameClient.this, GameRoomMain.class);
					startActivity(j);
				}
			}
		});

		getView().setOnTouchListener(this);
	}
	
	private void startGame() {
		Log.e("Starting Game", "We are starting the game");
		Toast.makeText(this, myTeam, Toast.LENGTH_SHORT).show();
		this.setTitle("Word Attack Team: " + myTeam);
		mSoundManager = new SoundManager();
		mSoundManager.initSounds(getBaseContext());
		mSoundManager.addSound(1, R.raw.collision);

		gameView = new ShapeView(this);

		touchCalcultor = new TouchCalculator(getWindowWidth());
		PlayerCredential pc = getPlayerCredential();
		host = getHost();

		
		gameView = (ShapeView) getView();
		gameView.setOnTouchListener(this);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(gameView);

		Log.e("Host Name", host);
		playerProxy = new PlayerProxy(pc, mConnection, host);
		gameView.setOnTouchListener(this);

		currentState = PLAYING_GAME;
	}
	
	

	@Override
	public boolean onTouch(View v, MotionEvent touch) {
		if (currentState == PLAYING_GAME) {
			Log.e("Touch", "" + PLAYER_INTENT + touchCalcultor.getTouchIndex(touch.getX()));
			playerProxy.sendMessageToServer("" + PLAYER_INTENT + touchCalcultor.getTouchIndex(touch.getX()));
			mSoundManager.playSound(1);
		}

		return false;
	}

	
	private View getView() {
		Display display = getWindowManager().getDefaultDisplay();
		return new ShapeView(this, display.getHeight(), display.getWidth());
	}
	
	private int getWindowHeight(){
		Display display = getWindowManager().getDefaultDisplay();
		return display.getHeight();
	}
	
	private int getWindowWidth(){
		Display display = getWindowManager().getDefaultDisplay();
		return display.getWidth();		
	}

	private String getHost() {
		return host;
	}

	private PlayerCredential getPlayerCredential() {
		String email = GameRoomLogin.iSC.getUser().getEmail();
		return new PlayerCredential(email);
	}
 
	
	   @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
   			if (T) Log.e(TAG,"onActivityResult:"+requestCode);
	    		switch(requestCode){
	    		
	    		case REQUEST_CONNECT_DEVICE:
//	    			privateDevice = "all";
//	    			setTitle("WordAttack, ToAll");
	    		break;
	            case SEARCH_NETWORK_DEVICE:
	            if (resultCode == Activity.RESULT_OK) {
	            		String device = data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
	            		String name = data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_NAME);
	    	            privateDevice = device;
	    	            setTitle("WordAttack, Whisper: "+name);
	            }
	            break;
	            
	            case CAMERA_APP:
	            	
	            	if (T) Log.e(TAG, "Time to display pic");
	            	
	            	File file = new File("/sdcard","BTChat_"+resultCode+".jpg");

	            	if(file.exists()){
	            		
	            		if (T) Log.e(TAG,"CAME HERE");
		            	
		            	long fileSize = file.length();
		            	if (T) Log.e(TAG,"fileSize:"+fileSize);
		            	if (T) Log.e(TAG,"frameSize:"+DataPacket.DATA_MAX_SIZE);
		            	long noOfPacket = (fileSize/DataPacket.DATA_MAX_SIZE);
		            	
		            	Log.e(TAG,"NoOfPackets:"+noOfPacket);
		            
		            	String messageSend ="pic";
		            	
		            	String Target = privateDevice;

		            	if(T) Log.e(TAG,"Adding pic");
		            	pAdapter.add("Sending PicSize: "+noOfPacket);
		            	pAdapter.add("photo:"+"BTChat_"+resultCode+".jpg");
		            	if(T) Log.e(TAG,"Added");
		            	
		            	if(mConnection.sendMessage(Target, messageSend.getBytes()) ==Connection.SUCCESS){
		            	
			            	InputStream in = null;
			            
			            	
			            	try {
			            			byte[] sendBuffer = new byte[DataPacket.DATA_MAX_SIZE];
								in = new BufferedInputStream(new FileInputStream(file));
								int byteRead =0;
								while(true){
									try {
										byteRead = in.read(sendBuffer);
										
							              if (byteRead != -1) {
							            	  	//ensure DATAMAXSIZE Byte is read.
								            	  if (T) Log.e(TAG,"byteRead:"+byteRead);
								            	  int bytesRead2 = byteRead;
								            	  int bufferSize = DataPacket.DATA_MAX_SIZE;
								            	  while(bytesRead2 != bufferSize){
								            		  bufferSize = bufferSize - bytesRead2;
								            		  bytesRead2 = in.read(sendBuffer,byteRead,bufferSize);
								            		  if(bytesRead2 == -1){
								            			  break;
								            		  }
								            		  byteRead = byteRead+bytesRead2;
								            	  }
							              }
										
										
										
										if(byteRead<DataPacket.DATA_MAX_SIZE){
											byte[] lastbyte = new byte[byteRead];
											for(int i=0;i<byteRead;i++){
												lastbyte[i] = sendBuffer[i];
											}
											mConnection.sendMessage(Target, lastbyte);									
											mConnection.sendMessage(Target, "end".getBytes());
											break;
										}
										else{
											mConnection.sendMessage(Target, sendBuffer);	
										}
										
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										try {
											in.close();
										} catch (IOException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
										
									}
								}
								
								
								
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
						}
		            	}
			   
		            	
		            	break;
		    		}
	    		}
	    }
		
	

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.scan:
            // Launch the DeviceListActivity to see devices and do scan
//        		if (T) Log.e(TAG,"Connect to other device!");
//            Intent serverIntent = new Intent(this, DeviceListActivity.class);
//            serverIntent.putExtra("TYPE", REQUEST_CONNECT_DEVICE);
//            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			privateDevice = "all";
			setTitle("WordAttack, ToAll");
			return true;
        case R.id.target:
            // Ensure this device is discoverable by others
//            mConnection.startServer(7, connectedListener, maxConnectionsListener,
//                    dataReceivedListener, disconnectedListener);
            Intent serverIntent2 = new Intent(this, DeviceListActivity.class);
            serverIntent2.putExtra("TYPE", SEARCH_NETWORK_DEVICE);
            
            if(networkDevicesAddresses == null){
            		networkDevicesAddresses = new HashMap<String,String>();
            }
            
            	if(networkDevicesAddresses.size()>0){
	            serverIntent2.putExtra("SIZE", networkDevicesAddresses.size());
	            
	            int i = 0;
	            
	            for ( Iterator<String> iter = networkDevicesAddresses.keySet().iterator(); iter.hasNext(); ){
	            		
	            		
	            		String Addr = iter.next();
	            		String name = networkDevicesAddresses.get(Addr);
	            		
	            		Log.e(TAG,"DEVICEADDR"+i+":"+Addr);
	            		Log.e(TAG,"DEVICENAME"+i+":"+name);
	            		serverIntent2.putExtra("DEVICEADDR"+i, Addr);
	            		serverIntent2.putExtra("DEVICENAME"+i, name);
	            		
	            		i++;
	            }
            	}
            
            
            startActivityForResult(serverIntent2, SEARCH_NETWORK_DEVICE);        	       	
        		
            TimerTask task = new TimerTask(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					networkDevicesAddresses = (HashMap<String, String>) mConnection.getDevices();
				}
            	
            };
            
            Timer timer = new Timer();
            
            timer.schedule(task, 1000);
        	
            return true;
        case R.id.camera:
    		if (T) Log.e(TAG,"Starting Camera App");
    		if(getExp() > 3){
    			if(!privateDevice.equalsIgnoreCase("app")){
    				Intent camera = new Intent(this,MainActivity.class);
    				startActivityForResult(camera, CAMERA_APP);
    			}else {
    				Toast.makeText(this, "Please select a player to send to", Toast.LENGTH_SHORT).show();
    			}
    		} else {
    			Toast.makeText(this, "you need 50 exp to send photos" , Toast.LENGTH_SHORT).show();
    		}
    		return true;
        }
        return false;
    }
	
	
	private OnMessageReceivedListener dataReceivedListener = new OnMessageReceivedListener() {
		@Override
		public void OnMessageReceived(String device, byte[] message) {
			String msg = new String(message, 0, message.length);
//			Log.e("MESSAGE", "I HAVE RECEIVED A MESSAGE " + msg);

			switch (currentState) {
			case PLAYING_GAME:
				String code2 = msg.substring(0, 1);
				if(code2.equalsIgnoreCase("" + DRAW)){				
					String health = msg.substring(41,42);
					String state = msg.substring(1, 41);
					String score = msg.substring(42,msg.length());
					Log.e("Connected", "Received");
					Log.e("state", state);
					Log.e("health", health);
					Log.e("score", score);
					drawView(state);
					gameView.setHearts(Integer.parseInt(health));
					gameView.setScore(score);
				
					
				}else if(code2.equalsIgnoreCase("" + SCORE)){				
					String msg2 = msg.substring(1,msg.length());
					//Log.e("SCORE", msg2);
					mHandler.obtainMessage(SCORE, msg2).sendToTarget();
				}else if(code2.equalsIgnoreCase("" + END_GAME)){
					
					String msg2 = msg.substring(1,msg.length());
					//Log.e("END_GAME", msg2);
					mHandler.obtainMessage(END_GAME, msg2).sendToTarget();
				}	
				
				break;
				
			case CREATING_GAME:
				
				
				String type ="";
				if(message.length > 2){
					type = new String(message, 0, 3);
				}
				String code = msg.substring(0, 1);
				//Log.e("msg", msg);
				
				
				
				if (type.compareTo("msg") == 0) {
					String msg2 = new String(message, 0, message.length);
					if (true) 	Log.e(TAG, "Message Received:" + msg2);
					mHandler2.obtainMessage(0, msg2 + ".").sendToTarget();
				} else if (type.compareTo("pic") == 0) {
					Log.e(TAG,"Pic start");
					// if (true) Log.e(TAG,"Total no of frame:"+length);
					int resultCode = (int) System.currentTimeMillis();
					String outFilename = String.format("%d.jpg", resultCode);
					outFilenameArray.put(device, outFilename);
					
					locks.put(device, new Object());
					
					try {
						// OutputStream outFile = new BufferedOutputStream(new
						// FileOutputStream("/sdcard/"+outFilename));
						synchronized (locks.get(device)) {
							//Log.e(TAG,"Create Stream");
							OutputStream outFile = new FileOutputStream("/sdcard/"
									+ outFilename);
							outFileArray.put(device, outFile);
						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else if (type.compareTo("end") == 0) {
					try {
						// outFile.close();
						Log.e(TAG,"Pic ends");
						if(outFileArray == null){
							Log.e(TAG,"array is null");
						}
						if(device == null){
							Log.e(TAG,"device is null");
						}
						
						if(locks == null){
							Log.e(TAG,"device is null");
						}

						synchronized (locks.get(device)) {
							//outFileArray.get(device).flush();
							outFileArray.get(device).close();
							outFileArray.remove(device);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mHandler2.obtainMessage(0, "end" + outFilenameArray.get(device))
							.sendToTarget();
					
					
				} else if(outFileArray.containsKey(device)) {
					try {
						// outFile.write((byte[]) message);
						synchronized (locks.get(device)) {
							outFileArray.get(device).write(message);
							outFileArray.get(device).flush();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (code.equalsIgnoreCase("" + START_GAME)) {
					//Log.e("Start Game", "I have received instructions to start game");
					host = msg.substring(1, msg.length());
					mHandler.obtainMessage(START_GAME, "").sendToTarget();
				} else if (code.equalsIgnoreCase("" + REGISTER_USER)) {

				} else if (code.equalsIgnoreCase("" + PLAYER_INTENT)) {

				} else if (code.equalsIgnoreCase("" + TELL_TEAM)){
					String team = msg.substring(1, 2);
					Log.e("Team Number", team);
					host = device;
					mHandler.obtainMessage(TELL_TEAM, team).sendToTarget();
				} else if(code.equalsIgnoreCase("" + MSG_TO_ROOM)){
					String msg2 = msg.substring(1,msg.length());
					Log.e("Message", msg2);
					mHandler.obtainMessage(MSG_TO_ROOM, msg2).sendToTarget();
				} else if(code.equalsIgnoreCase("" + PRIVATE_CHAT)){
					String msg2 = msg.substring(1,msg.length());
					Log.e("Private Message", msg2);
					mHandler.obtainMessage(PRIVATE_CHAT, msg2).sendToTarget();
				}
				break;
			}
		}
 
	
		
		
		private void drawView(String msg) {
			ArrayList<ArrayList<Integer>> gameState = new ArrayList<ArrayList<Integer>>();

			for (int i = 0; i < 4; i++) {
				ArrayList<Integer> line = new ArrayList<Integer>();
				for (int count = 0; count < 10; count++) {
					Integer curr = Integer.parseInt(""
							+ (char) msg.charAt(i * 10 + count));
					line.add(curr);
				}
				gameState.add(line);
//				Log.e("LINE", gameState.toString());
			}
			mHandler.obtainMessage(DRAW, gameState).sendToTarget();
		}
	};

	private OnMaxConnectionsReachedListener maxConnectionsListener = new OnMaxConnectionsReachedListener() {
		@Override
		public void OnMaxConnectionsReached() {

		}
	};

	private OnIncomingConnectionListener connectedListener = new OnIncomingConnectionListener() {
		@Override
		public void OnIncomingConnection(String device) {

		}
	};

	private OnConnectionLostListener disconnectedListener = new OnConnectionLostListener() {
		@Override
		public void OnConnectionLost(String device) {

		}
	};

	private OnConnectionServiceReadyListener serviceReadyListener = new OnConnectionServiceReadyListener() {
		@Override
		public void OnConnectionServiceReady() {
			mConnection.startServer(7, connectedListener,
					maxConnectionsListener, dataReceivedListener,
					disconnectedListener,false);

			mConnection.setNickName(nickname);
			List<String> devicesToConnectTo = getDevicesToConnectTo();
			while(connectToDevices(devicesToConnectTo) == 0){};

			String registerUser = "" + REGISTER_USER
					+ GameRoomLogin.iSC.getUser().getEmail();
			Log.e("Sending Credentials", registerUser);

			sendToAllDevices(devicesToConnectTo, registerUser);
			// mConnection.broadcastMessage(("" + REGISTER_USER +
			// GameRoomLogin.iSC.getUser().getEmail()).getBytes() , "");
		}

		private int connectToDevices(List<String> devicesToConnectTo) {
			int isConnected = 0;
			int offset = 0;
			if(devicesToConnectTo.size() > 3){
				offset = 1;
			}
			for (int i = devicesToConnectTo.size() - 1 - offset; i >= 0; i--) {
				String device = devicesToConnectTo.get(i);

				if (device.equalsIgnoreCase(mConnection.getAddress()) || device == null) {
					continue;
				}
				
				Log.e("Connecting To", device);
				int connectionStatus = mConnection.connect(device,
						dataReceivedListener, disconnectedListener);
				if (connectionStatus == Connection.SUCCESS) {
					Log.e("Connected to", "Connected to " + device);
					isConnected = 1;
					break;
				} else {
					Log.e("Cannot Connect to", "Cannot Connect to " + device);
				}
			}
			return isConnected;
		}		


	};
	
	
	private void sendToAllDevices(List<String> devicesToSend, String message) {
		
		for (String device : devicesToSend) {
			Log.e("Attempting To REgister", device );
			if (device.equalsIgnoreCase(mConnection.getAddress())) {
				continue;
			}

			mConnection.sendMessage(device, message.getBytes());
		}
	}

	
	private ArrayList<String> getDevicesToConnectTo() {
		ArrayList<String> list = new ArrayList<String>();
		for (onlineUser u : usersInRoom) {
			list.add(u.BTadd);
		}
		return list;
	}

    public class PhotoAdapter extends BaseAdapter {

        private Integer[] mPhotoPool = {
                R.drawable.sample_thumb_0, R.drawable.sample_thumb_1, R.drawable.sample_thumb_2,
                R.drawable.sample_thumb_3, R.drawable.sample_thumb_4, R.drawable.sample_thumb_5,
                R.drawable.sample_thumb_6, R.drawable.sample_thumb_7};

        private ArrayList<String> mPhotos = new ArrayList<String>();
        private Context mContext;
        
        public PhotoAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mPhotos.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        
        public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

	        	int width = bm.getWidth();
	
	        	int height = bm.getHeight();
	
	        	float scaleWidth = ((float) newWidth) / width;
	
	        	float scaleHeight = ((float) newHeight) / height;
	
	        	// create a matrix for the manipulation
	
	        	Matrix matrix = new Matrix();
	
	        	// resize the bit map
	
	        	matrix.postScale(scaleWidth, scaleHeight);
	
	        	// recreate the new Bitmap
	
	        	Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
	
	        	return resizedBitmap;

        	}        

        
        
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make an ImageView to show a photo
        		String value = mPhotos.get(position);
        		
        		if(value.startsWith("photo:")){
        			String filename = value.substring(6);
        			Bitmap bMap = BitmapFactory.decodeFile("/sdcard/"+filename);
        			
        			if(bMap!=null){
		            ImageView i = new ImageView(mContext);
		            Display display = getWindowManager().getDefaultDisplay(); 
		            int width = display.getWidth();
		            int height = bMap.getHeight()/(bMap.getWidth()/width);
		            bMap = getResizedBitmap(bMap,height,width);
		            
		            //i.setImageResource(mPhotos.get(position));
		            //i.setImageResource(1);
		            i.setImageBitmap(bMap);
		            i.setAdjustViewBounds(true);
		            i.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.WRAP_CONTENT,
		                    LayoutParams.WRAP_CONTENT));
		            // Give it a nice background
		            i.setBackgroundResource(R.drawable.picture_frame);
		            
		            //clean up
		            bMap = null;
		            System.gc();
		            
		            return i;
        			}
        			else{
        				TextView t  = new TextView(mContext);
        				t.setTextSize(2,18);
        				t.setPadding(5, 5, 5, 5);
        				t.setText("unable to open"+filename);
        				return t;
        				
        			}
        		}
        		else{
    				TextView t  = new TextView(mContext);
    				t.setTextSize(2,18);
    				t.setPadding(5, 5, 5, 5);
    				t.setText(mPhotos.get((position)));
    				return t;
        		}
        }


//        public void clearPhotos() {
//            mPhotos.clear();
//            notifyDataSetChanged();
//        }
        
//        public void addPhotos() {
//            int whichPhoto = (int)Math.round(Math.random() * (mPhotoPool.length - 1));
//            int newPhoto = mPhotoPool[whichPhoto];
//            mPhotos.add(newPhoto);
//            notifyDataSetChanged();
//        }
        		
        		public void add(String value){
        			if (T) Log.e(TAG,"adding:"+value);
        			mPhotos.add(value);
        			notifyDataSetChanged();
        		}
        

    }


	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
