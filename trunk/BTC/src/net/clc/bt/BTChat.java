package net.clc.bt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BTChat extends Activity implements Callback {
    public static String EXTRA_SELECTED_ADDRESS = "btaddress";
	
    public static final String TAG = "BTChat";

    private String chatName;
    
    private BTChat self;

    private int mType; // 0 = server, 1 = client

    private Connection mConnection;
    
    private String ConnectedDevice = "";
    
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_CHANGE_DEVICE = 3;
    private static final int SEARCH_NETWORK_DEVICE = 4;
    private static final int CAMERA_APP = 5;
    
    private static final boolean T = false;
    private static final boolean D = true;
    
    /* BTChat Variables */
    // Layout Views
    private TextView mTitle;
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;
    private ArrayAdapter<String> mConversationArrayAdapter;    
    private PhotoAdapter pAdapter;
    private HashMap<String,OutputStream> outFileArray;

    private HashMap<String,String> outFilenameArray;
    private HashMap<String,Object> locks;
    private HashMap<String,String> networkDevicesAddresses;
    
    
    final Handler mHandler = new Handler(){
 	
    	
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
    

    private OnMessageReceivedListener dataReceivedListener = new OnMessageReceivedListener() {
        @Override
		public void OnMessageReceived(String device, byte[] message) {
        		if (T) Log.e(TAG,"DataSize:"+message.length);
        		String type = new String(message,0,3);
        		if(type.compareTo("msg")==0){
	        		String msg = new String(message,0,message.length);
	        		if (true) Log.e(TAG,"Message Received:"+msg);
	            mHandler.obtainMessage(0, msg+".").sendToTarget();
        		}
        		else if(type.compareTo("pic")==0){
        			//if (true) Log.e(TAG,"Total no of frame:"+length);
				int resultCode = (int) System.currentTimeMillis();
				String outFilename = String.format("%d.jpg", resultCode);
				outFilenameArray.put(device,outFilename);
				locks.put(device, new Object());
				try {
					//OutputStream outFile = new BufferedOutputStream(new FileOutputStream("/sdcard/"+outFilename));
					synchronized(locks.get(device)){
						OutputStream outFile = new FileOutputStream("/sdcard/"+outFilename);
						outFileArray.put(device, outFile);
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        				
        		}
        		else if(type.compareTo("end")==0){
    				try {
						//outFile.close();
    						synchronized(locks.get(device)){
							outFileArray.get(device).flush();    					
							outFileArray.get(device).close();
    						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				mHandler.obtainMessage(0, "end"+outFilenameArray.get(device)).sendToTarget();
        			
        		}
        		else{
					try {
						//outFile.write((byte[]) message);
						synchronized(locks.get(device)){
							outFileArray.get(device).write(message)	;
							outFileArray.get(device).flush();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}        			
        		}
        }
    };

//    private OnByteReceivedListener bytesReceivedListener = new OnByteReceivedListener() {
//        public void OnByteReceived(String device, Byte[] data) {       		        	
//            Log.e(TAG,"Message Received:"+data.toString());
//            //mHandler.obtainMessage(0, message).sendToTarget();
//         
//        }
//    };
    
    private OnMaxConnectionsReachedListener maxConnectionsListener = new OnMaxConnectionsReachedListener() {
        @Override
		public void OnMaxConnectionsReached() {
            Log.e(TAG, "Max connections reached!");
        }
    };

    private OnIncomingConnectionListener connectedListener = new OnIncomingConnectionListener() {
        @Override
		public void OnIncomingConnection(String device) {       		       		
        		Log.e(TAG,"Incoming Connections from !"+device);
        		ConnectedDevice = device;

        }
    };

    private OnConnectionLostListener disconnectedListener = new OnConnectionLostListener() {
        @Override
		public void OnConnectionLost(String device) {
            Log.e(TAG,"Connection Lost");
            if(networkDevicesAddresses.containsKey(device)){
            		networkDevicesAddresses.remove(device);
            }

        }
    };

    private OnConnectionServiceReadyListener serviceReadyListener = new OnConnectionServiceReadyListener() {
        @Override
		public void OnConnectionServiceReady() {
            if (mType == 1) {
            		Log.e(TAG,"Starting Normal Connection");
                mConnection.startServer(7, connectedListener, maxConnectionsListener,
                        dataReceivedListener, disconnectedListener, false);
            }
            else{
            		Log.e(TAG,"Starting AutoConnect Mode");
                mConnection.startServer(7, connectedListener, maxConnectionsListener,
                        dataReceivedListener, disconnectedListener, true);
            }
        			chatName = mConnection.getName();
        			
        			//mConnection.setNickName("Test test test");
        			
//                self.setTitle("BTChat: " + mConnection.getName() + "-" + mConnection.getAddress());

//            } else {
////                Intent serverListIntent = new Intent(self, ServerListActivity.class);
////                startActivityForResult(serverListIntent, SERVER_LIST_RESULT_CODE);
//            }
        }
    };
    
    private void setupChat() {
    		if (D) Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
    		outFileArray =new HashMap<String,OutputStream>();
    		outFilenameArray = new HashMap<String,String>();
    		locks = new HashMap<String,Object>();
    		networkDevicesAddresses = new HashMap<String,String>();
    		
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
        pAdapter = new PhotoAdapter(this);
        mConversationView = (ListView) findViewById(R.id.in);
        //mConversationView.setAdapter(mConversationArrayAdapter);
        mConversationView.setAdapter(pAdapter);
        
        // Initialize the compose field with a listener for the return key
        mOutEditText = (EditText) findViewById(R.id.edit_text_out);
        //mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton = (Button) findViewById(R.id.button_send);
        mSendButton.setOnClickListener(new OnClickListener() {
        		
            @Override
			public void onClick(View v) {
                // Send a message using content of the edit text widget
                TextView view = (TextView) findViewById(R.id.edit_text_out);
                String message = view.getText().toString();
                String messageSend = "msg"+chatName+": "+message;
                if (D)  Log.e(TAG,"button pressed!");
                if(mConnection.sendMessage(ConnectedDevice, messageSend.getBytes()) ==Connection.FAILURE){
                		if (D) Log.e(TAG,"Send Message Failed!");
                }
                else{
                		if (D) Log.e(TAG,"Send Message Success!");
					//mConversationArrayAdapter.add("me: "+message);
                		pAdapter.add("me: "+message);

					mOutEditText.setText("");
                }                
	                
            }

        });
        
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        self = this;

        Intent startingIntent = getIntent();
        mType = startingIntent.getIntExtra("TYPE", 0);
        //self.setTitle("BTChat: " + mConnection.getName() + "-" + mConnection.getAddress());
        setContentView(R.layout.chat);
//        mSurface = (SurfaceView) findViewById(R.id.surface);
//        mHolder = mSurface.getHolder();

        	setupChat();
        
        	mConnection = new Connection(this, serviceReadyListener);
                	
    }
   
    public void showMessageReceived(String msg){
        mConversationArrayAdapter.add(msg);    
    }

    @Override
    protected void onDestroy() {
        if (mConnection != null) {
            mConnection.shutdown();
        }
        super.onDestroy();
    }

    @Override
	public void surfaceCreated(SurfaceHolder holder) {
//        pLoop = new PhysicsLoop();
//        pLoop.start();
    }


    @Override
	public void surfaceDestroyed(SurfaceHolder holder) {
//        try {
//            pLoop.safeStop();
//        } finally {
//            pLoop = null;
//        }
    }


    @Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
    		switch(requestCode){
	        
    		case REQUEST_CONNECT_DEVICE:
    			if (resultCode == Activity.RESULT_OK) {
	        		String device = data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
	        		if (D) Log.e(TAG,"Connectiing to:"+device);
	        		int connectionStatus = mConnection.connect(device, dataReceivedListener,
	                    disconnectedListener);
	            if (connectionStatus != Connection.SUCCESS) {
	                Toast.makeText(self, "Unable to connect; please try again.", 1).show();
	            }
	            else{            
		            ConnectedDevice = device;
		            self.setTitle("BTChat: Connected to: "+ConnectedDevice);
		            if (D) Log.e(TAG,"Connecting to: "+ConnectedDevice);        
	            }
	            return;
	        }
    			break;
    			
            case SEARCH_NETWORK_DEVICE:
            if (resultCode == Activity.RESULT_OK) {
            		String device = data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
    	            ConnectedDevice = device;
    	            self.setTitle("BTChat: Connected to: "+ConnectedDevice);
    	            if (D) Log.e(TAG,"Connecting to: "+ConnectedDevice);  
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
	            	
	            	String Target = ConnectedDevice;
	            	
	            	if(mConnection.sendMessage(Target, messageSend.getBytes()) ==Connection.SUCCESS){
	            	
		            	pAdapter.add("Sending PicSize: "+noOfPacket);
		            	pAdapter.add("photo:"+"BTChat_"+resultCode+".jpg");
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
    
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.option_menu, menu);
//
////        Intent serverListIntent = new Intent(self, ServerListActivity.class);
////        startActivityForResult(serverListIntent, SERVER_LIST_RESULT_CODE);    	
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//        case R.id.scan:
//            // Launch the DeviceListActivity to see devices and do scan
////            Intent serverIntent = new Intent(this, DeviceListActivity.class);
////            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
//            return true;
//        case R.id.discoverable:
//            // Ensure this device is discoverable by others
//            //ensureDiscoverable();
//        		Log.e(TAG,"make discoverable");
//            mConnection.startServer(7, connectedListener, maxConnectionsListener,
//                    dataReceivedListener, disconnectedListener);
//            self.setTitle("BTChat: " + mConnection.getName() + "-" + mConnection.getAddress());
//        	
//            return true;
//        }
//        return false;
//    }
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
        		if (D) Log.e(TAG,"Connect to other device!");
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            serverIntent.putExtra("TYPE", REQUEST_CONNECT_DEVICE);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
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
        		Intent camera = new Intent(this,MainActivity.class);
        		startActivityForResult(camera, CAMERA_APP);
        		return true;
        }
        return false;
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

}
