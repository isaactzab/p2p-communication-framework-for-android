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
import com.game.factories.GameFactory;
import com.game.interfaces.GameCreatorInterface;
import com.game.models.Game;
import com.game.models.Player;
import com.game.models.PlayerCredential;
import com.game.models.Team;
import com.game.models.TouchCalculator;
import com.gui.app.ShapeView;
import com.gui.app.SoundManager;
import com.networking.game.PlayerNetworkingIntent;

public class GameServer extends Activity implements GameCreatorInterface,
		GameObserverInterface, OnTouchListener {
	
	private boolean D = true;

	// Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_CHANGE_DEVICE = 3;
    private static final int SEARCH_NETWORK_DEVICE = 4;
    private static final int CAMERA_APP = 5;
    private HashMap<String,String> networkDevicesAddresses;
    private PhotoAdapter pAdapter;

    private HashMap<String, OutputStream> outFileArray;
	private HashMap<String, String> outFilenameArray;
	private HashMap<String, Object> locks;
    
    
    private static final int LEVEL1 = 1;
	
	private int expectedNumberOfPlayers;
	private int numberOfConnectedPlayers;
	private ArrayList<Player> players;
	private Connection mConnection;
	private GameCreatorInterface gci;
	private final int T = 0;

	Button btn1;
	ImageButton ibtn1, ibtn2;
	EditText inputbox;
	String GameRoomName;
	private static final String TAG = "MyActivity";
	private SoundManager mSoundManager;
	private Game game;
	private ShapeView gameView;
	private Player player;
	private Player player2;

	private PlayerNetworkingIntent pni;

	private TouchCalculator touchCalcultor;
	
	private ListView mConversationView;
	private ArrayAdapter<String> mConversationArrayAdapter;

	private static final int DRAW = 9;
	private static final int PLAYER_INTENT = 1;
	private static final int START_GAME = 2;
	private static final int REGISTER_USER = 3;
	private static final int TELL_TEAM = 4;
	private static final int MSG_TO_ROOM = 5;
	private static final int SCORE = 6;
	private static final int END_GAME = 7;
	private static final int PRIVATE_CHAT = 8;
	
	private final static int CREATING_GAME = 0;
	private final static int PLAYING_GAME = 1;
	private final static int GAME_END = 3;
	
	
	private String privateDevice = "all";
	
	private ArrayList<String> chatDevices;
	private List<onlineUser> devices;
	private String room;
	private String nickname;

	Team teamOne = new Team();
	Team teamTwo = new Team();
	GameFactory gf = new GameFactory();
	
	private int currentState;

    final Handler mHandler2 = new Handler(){
 	
    	
		/* (non-Javadoc)
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			//super.handleMessage(msg);
			String type = msg.obj.toString().substring(0, 3);
			if (D) Log.e(TAG,"Type:"+type);
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

			switch (msg.what) {
			case DRAW:
				//Log.i("Tag", "attempting draw");
				gameView.setDrawState((ArrayList<ArrayList<Integer>>) msg.obj);
				break;
			case PLAYER_INTENT:
				String data = (String) msg.obj;
				//Log.e("PlayerCredential", data.substring(1, data.length()));
				//Log.e("Row Number", data.substring(0, 1));
				pni.triggerWithCredentials(new PlayerCredential(data.substring(
						2, data.length())), data.substring(1, 2));
				break;
			case REGISTER_USER:				
				String email = (String) msg.obj;
				//Log.e("Registering user", email);
				Toast.makeText(getApplicationContext(), email +  " joined", Toast.LENGTH_LONG).show();
				registerUser(email);
				break;
			case MSG_TO_ROOM:
				String str = msg.obj.toString();
				sendMessageToGameRoom(str);
				break;
			case PRIVATE_CHAT:
				String str2 = msg.obj.toString();
				pAdapter.add(str2);
				break;
			case END_GAME:
				String str3 = msg.obj.toString();
				Log.e("Error", "Game Over");
				Toast.makeText(getApplicationContext(), "Team: " + str3 + " wins", Toast.LENGTH_LONG).show();
				mConnection.shutdown();
				finish();
				break;
			}
		}
	};
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		touchCalcultor = new TouchCalculator(getWindowWidth());
		pni = new PlayerNetworkingIntent();
		chatDevices = new ArrayList<String>();
		player = joinTeam(teamOne, GameRoomLogin.iSC.getUser().getEmail());
		nickname = GameRoomLogin.iSC.getUser().getUserName();
		mConnection = new Connection(this, serviceReadyListener);
		outFileArray = new HashMap<String, OutputStream>();
		outFilenameArray = new HashMap<String, String>();
		locks = new HashMap<String, Object>();
		
		gameStartScreen();
	}
	
	
	private void startGame() {
		currentState = PLAYING_GAME;
		mSoundManager = new SoundManager();
		mSoundManager.initSounds(getBaseContext());
		mSoundManager.addSound(1, R.raw.collision);
		
		Intent startingIntent = getIntent();
		room = startingIntent.getStringExtra("ChatRoomHost");
		Log.e("Joining Room", room);
		
		devices = GameRoomLogin.iSC.getChatRoom(room).getUserList();
		
		List<String> devs = getDevicesToConnectTo();
		for(String dev: devs){
			Log.e("Connecting to device", dev);
		}
		Log.e("Starting Game", "Devices = " + devices.size());
		
		game = gf.createDefaultGame();
		game.register(this);
		game.setOne(teamOne);
		game.setTwo(teamTwo);

		
		Display display = getWindowManager().getDefaultDisplay();
		gameView = new ShapeView(this, display.getHeight(), display.getWidth());
		gameView.setOnTouchListener(this);

		setContentView(gameView);
		game.start();
		
	}

	private int getWindowHeight(){
		Display display = getWindowManager().getDefaultDisplay();
		return display.getHeight();
	}
	
	private int getWindowWidth(){
		Display display = getWindowManager().getDefaultDisplay();
		return display.getWidth();		
	}
	
	private void sendMessageToGameRoom(String msg){
		for(String device : chatDevices){
			String frame = "" + MSG_TO_ROOM + msg;
			mConnection.sendMessage(device, frame.getBytes());
		}
		pAdapter.add(msg);
	}
	
	int exp = -1;
	private int getExp(){
		exp = exp == -1 ? GameRoomLogin.iSC.getExp(player.getPc().getEmail()) : exp ;
		return exp;
	}
	
	private void gameStartScreen() {
        setTitle("WordAttack, ToAll");
		currentState = CREATING_GAME;
		setContentView(R.layout.chat_host);
		btn1 = (Button) findViewById(R.id.button_send);
		ibtn1 = (ImageButton) findViewById(R.id.imageButton1);
		ibtn2 = (ImageButton) findViewById(R.id.imageButton2);
		Intent i = getIntent();
		GameRoomName = i.getStringExtra("ChatRoomHost");
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
				if(privateDevice.equalsIgnoreCase("all")){
					String message = nickname + " SAYS:" + inputbox.getText().toString();
					sendMessageToGameRoom(message);
				} else {
					
					if(getExp() > LEVEL1){
						String message = "" + PRIVATE_CHAT + nickname + " SAYS:" + inputbox.getText().toString();
						Log.e("Private Message", message);
						mConnection.sendMessage(privateDevice, message.getBytes());
						mHandler.obtainMessage(PRIVATE_CHAT, nickname + " SAYS:" + inputbox.getText().toString()).sendToTarget();
					}else {
						Toast.makeText(GameServer.this, "You need to have at least " + LEVEL1 + " exp to whisper", Toast.LENGTH_SHORT).show();
					}
				}
				inputbox.setText("");
			}
		});

		ibtn2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.d(TAG, "Button Exit is click");
				if (GameRoomLogin.iSC.leaveChatRoom(GameRoomName)) {
					Log.d(TAG, "Button Exit is click");
					Toast.makeText(getApplicationContext(),
							"You leave Game Room " + GameRoomName,
							Toast.LENGTH_SHORT).show();
					Intent j = new Intent(GameServer.this, GameRoomMain.class);
					startActivity(j);
				}
			}
		});

		ibtn1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startGame();
				startRemoteGames();
				informPlayersOfTeams();
			}
		});
	}

	private void informPlayersOfTeams(){
		
	}
	private void startRemoteGames() {
		Log.e("Sending", "Sending");

//		if (mConnection.broadcastMessage(("" + START_GAME + mConnection
//				.getAddress()).getBytes(), "") == Connection.FAILURE) {
//			Log.e("Connection ", "Failure");
//		} else {
//			Log.e("Connection ", "Success");
//		}
		
		String message = ("" + START_GAME + mConnection.getAddress());
		//List<String> devices = 	getDevicesToConnectTo();
		//ArrayList<String> devices
		//Log.e("Starting Game", message);
		//sendToAllDevices(devices, message);
		sendMessageToRoomWithTag(START_GAME, mConnection.getAddress());

	}
	
	private void sendToAllDevices(List<String> devicesToSend, String message){
		for (String device : devicesToSend) {
			if (device.equalsIgnoreCase(mConnection.getAddress()) || device == null) {
				continue;
			}
			Log.e("Starting Game", device);
			mConnection.sendMessage(device, message.getBytes());
		}
	} 

	private void registerUser(String email){
		if(teamOne.getPlayers().size() <= teamTwo.getPlayers().size()){
			Log.e("Joining Team", "TeamOne " + email);
			joinTeam(teamOne, email);
		}else{
			Log.e("Joining Team", "TeamTwo " + email);
			joinTeam(teamTwo, email);
		}
	}
	
	private boolean playerExistsInTeam(Team t, String email){
		boolean exists = false;
		for(Player p : t.getPlayers()){
			if(p.getPc().getEmail().equalsIgnoreCase(email)){
				exists = true;
			}
		}
		return exists;
	}
	
	private Player joinTeam(Team t, String email) {
		PlayerCredential pc = new PlayerCredential(email);
		Player player = new Player(pc);
		t.join(player);
		pni.addPlayer(player);
		return player;
	}

	@Override
	public void playersCreated(ArrayList<Player> players) {
		this.players = players;
	}

	private OnMessageReceivedListener dataReceivedListener = new OnMessageReceivedListener() {
		@Override
		public void OnMessageReceived(String device, byte[] message) {
			
			String msg = new String(message, 0, message.length);
			//Log.e("Message Received", msg);
			String code = msg.substring(0, 1);
			
			
			String type ="";
			if(message.length > 2){
				type = new String(message, 0, 3);
			}
			
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
			} else if(code.equalsIgnoreCase("" + REGISTER_USER)){
				chatDevices.add(device);
				mHandler.obtainMessage(REGISTER_USER, msg.substring(1, msg.length())).sendToTarget();
				
				if(teamOne.getPlayers().size() <= teamTwo.getPlayers().size()){
					mConnection.sendMessage(device, ("" + TELL_TEAM + "2").getBytes());
				}else{
					mConnection.sendMessage(device, ("" + TELL_TEAM + "1").getBytes());
				}
				Log.e("NewPlayer has Arrived", device);
			}else if(code.equalsIgnoreCase("" + PLAYER_INTENT)){
				mHandler.obtainMessage(PLAYER_INTENT, msg).sendToTarget();
				
			}else if(code.equalsIgnoreCase("" + MSG_TO_ROOM)){
				String msg2 = msg.substring(1,msg.length());
				//Log.e("Message", msg2);
				mHandler.obtainMessage(MSG_TO_ROOM, msg2).sendToTarget();
			}else if(code.equalsIgnoreCase("" + SCORE)){
				
			}else if(code.equalsIgnoreCase("" + PRIVATE_CHAT)){
				String msg2 = msg.substring(1,msg.length());
				mHandler.obtainMessage(PRIVATE_CHAT, msg2).sendToTarget();
			}
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
			//chatDevices.add(device);
			//Log.e("Device", "number of devices " + chatDevices.size());
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
		}

	};
	
	private ArrayList<String> getDevicesToConnectTo() {
		ArrayList<String> list = new ArrayList<String>();
		for (onlineUser u : devices) {
			list.add(u.BTadd);
		}
		return list;
	}
	
	private String gameStateToString(ArrayList<ArrayList<Integer>> gameState) {
		String msg = new String();
		for (ArrayList<Integer> list : gameState) {
			for (Integer myNum : list) {
				msg = msg + myNum.toString();
			}
		}
		return msg;
	}

	
	@Override
	public boolean onTouch(View arg0, MotionEvent touch) {
		//Log.e("Touch", "I have been touched");
		pni.triggerWithCredentials(player.getPc(), "" + touchCalcultor.getTouchIndex(touch.getX()));
		mSoundManager.playSound(1);
		return false;
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		Log.e("Paused", "I am being paused");
		super.onPause();
	}

	private void sendMessageToRoomWithTag(int tag, String msg){
		if (mConnection != null) {
			ArrayList<String> list = getDevicesToConnectTo();
			for(String device :list){
				mConnection.sendMessage(device, ("" + tag + "" + msg).getBytes());
				//Log.e("message", msg + "" + device);
			}			
		} else {

		}
	}
	
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
    		switch(requestCode){
    		case REQUEST_CONNECT_DEVICE:
    			privateDevice = "all";
    			setTitle("WordAttack, ToAll");
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
            	if (D) Log.e(TAG, "Time to display pic");
            	
            	File file = new File("/sdcard","BTChat_"+resultCode+".jpg");
            	if(file.exists()){
	      	
	            	long fileSize = file.length();
	            	if (D) Log.e(TAG,"fileSize:"+fileSize);
	            	if (D) Log.e(TAG,"frameSize:"+DataPacket.DATA_MAX_SIZE);
	            	long noOfPacket = (fileSize/DataPacket.DATA_MAX_SIZE);
	            	
	            	Log.e(TAG,"NoOfPackets:"+noOfPacket);
	            
	            	String messageSend ="pic";
	            	
	            	String Target = privateDevice;
	            	
	            	if(mConnection.sendMessage(Target, messageSend.getBytes()) ==Connection.SUCCESS){
	            	
		            	//pAdapter.add("Sending PicSize: "+noOfPacket);
		            	//pAdapter.add("photo:"+"BTChat_"+resultCode+".jpg");
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
							            	  if (D) Log.e(TAG,"byteRead:"+byteRead);
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
//        		if (D) Log.e(TAG,"Connect to other device!");
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
    		if (D) Log.e(TAG,"Starting Camera App");
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
	


	@Override
	public void gameHasEnded(Game game, int winner) {
		if(currentState != END_GAME){
		Log.e("End Game", "" + winner + " is the winner");
		
			sendMessageToRoomWithTag(END_GAME, "" + winner);
			Team one = game.getOne();
			Team two = game.getTwo();
			submitTeamScores(one);
			submitTeamScores(two);
			GameRoomLogin.iSC.leaveChatRoom(room);
			currentState = END_GAME;
			
			mHandler.obtainMessage(END_GAME, ""+winner).sendToTarget();
		}
	}
	
	private void submitTeamScores(Team team){
		ArrayList<Player> players = team.getPlayers();
		for(Player player : players){
			//Log.e("Score", "Sending score: " + player.getScore() + " for Player : " + player.getPc().getEmail());
			GameRoomLogin.iSC.updateScore(player.getPc().getEmail(), player.getScore());
		}
	}

	@Override
	public void sendScores(int health, int totalScore) {
		//Log.e("SendScores", "" + health + "" + totalScore);
		sendMessageToRoomWithTag(SCORE, ""+health+""+totalScore);
		gameView.setScore("" + totalScore);
		gameView.setHearts(health);
	}


	
	@Override
	public void onGameStateChanged(ArrayList<ArrayList<Integer>> gameState,
			int health, int score) {
		String msg = gameStateToString(gameState);
		Log.e("Lengt", "" + msg.length());
		drawToAllConnectedDevices(msg + "" + health + "" + score);
		mHandler.obtainMessage(DRAW, gameState).sendToTarget();
		gameView.setScore("" + score);
		gameView.setHearts(health);
	}	
	
	private void drawToAllConnectedDevices(String string) {
		if (mConnection != null) {
			ArrayList<String> list = getDevicesToConnectTo();
			
			for(String device :list){
				String msg = ("" + DRAW + string);
				if(device.compareTo(mConnection.getAddress()) != 0){
					Log.e("Sending", msg);
					mConnection.sendMessage(device, msg.getBytes());
				}
			}
		
		} else {
		}
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
        			if (D) Log.e(TAG,"adding:"+value);
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
