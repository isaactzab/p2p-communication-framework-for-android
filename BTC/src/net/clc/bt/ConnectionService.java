/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
   
package net.clc.bt;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.example.Geoscribe.UseGPS;
import com.example.Geoscribe.comms.GeoscribeComms2;
import com.webservice.objects.RegisterDeviceResult;

public class ConnectionService extends Service {
	public static final boolean T = false;
	
	public static final boolean D = true;
	
    public static final String TAG = "net.clc.bt.ConnectionService";
    
    public static final String NEW_NETWORK_DEVICE_FOUND = TAG+"DEVICEFOUND";    
    
    public static final String DEVICE_ADDR = TAG+"DEVICEADDRESS";
    
    public static final String DEVICE_NAME = TAG+"DEVICENAME";
    
    static final int bufferSize = 1024;

    private ArrayList<UUID> mUuid;

    private ConnectionService mSelf;
    
	private Context mContext;

    private String mApp; // Assume only one app can use this at a time; may

    // change this later

    private IConnectionCallback mCallback;

    private ArrayList<String> mBtDeviceAddresses;
    
    private HashMap<String,String> networkDevices;

    private HashMap<String, BluetoothSocket> mBtSockets;

    private HashMap<String, Thread> mBtStreamWatcherThreads;
    
    private HashMap<String, Long> packetWatcher;
    
//    private HashMap<String, Byte> seqNoWatcher; 
//    
//    private HashMap<String, Byte> recievedSeqNoWatcher; 
//    
//    private Object seqNoWatcherLock;
//    
//    private Object recievedSeqNoWatcherLock;
    
    private Object mBtLock;

    private String DeviceNickName;
    
    private BluetoothAdapter mBtAdapter;
    
    private String mDeviceAddress;
    
    private String mDeviceName;
    
    private Runtime r;
    
    private Location mGeoCoordinate;
    
    private String mMasterDevice="";
    
    private boolean mAutoConnect = false;
    
    private boolean recoveryMode = false;
    
    private ArrayList<String> recoveryDevicesList;
    
    private GeoscribeComms2 webService;
    
    final Handler mHandler = new Handler(){
 	
    	
		/* (non-Javadoc)
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			//super.handleMessage(msg);
			String message = msg.obj.toString();
			
			Toast.makeText(mSelf, message, 1).show();			
		}
    		
    
    };
    
    //private final BlockingQueue<DataStream> packetQueue;
    
    public static final byte[] shortToByteArray(short value) {
        	        	
        	if (value < 0) return new byte[]{0,0};
    	
    		return new byte[] {
                (byte)(value >>> 8),
                (byte)value};
    }
    
    public static final short byteArrayToShort(byte [] b) {
        return (short)(((b[0] & 0xFF) << 8)
                + (b[1] & 0xFF));
    }    
    
    public static DataPacket decrypt(byte[] buffer){
    		  DataPacket pkt = new DataPacket();
  	  	  pkt.Ctr = buffer[0];
	  	  pkt.Src = new String(buffer,1,17);
	  	  pkt.Dest = new String(buffer,18,17);
	  	  pkt.PktId = new String(buffer,35,21);
	  	  pkt.HopCount = buffer[56];
	  	  byte[] dataSize = new byte[]{buffer[57],buffer[58]};
	  	  pkt.dataSize = byteArrayToShort(dataSize);
	  	  pkt.SeqNo = buffer[59];
	  	  pkt.data = new byte[pkt.dataSize];
	  	  
	  	  for(int i = 0; i<pkt.dataSize;i++){
	  		  pkt.data[i] = buffer[i+60];
	  	  }
	  	  
	  	  dataSize = null;
	  	  buffer = null;

    	  	  return pkt;
    }
    
    /* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mContext = this;		
	}

	public static byte[] encrypt(DataPacket pkt){
        	
    		byte header[] = new byte[1];
        	header[0] = pkt.Ctr;
        	byte packetAsBytes[] = joinByteArray(header,pkt.Src.getBytes());		            	
        	packetAsBytes = joinByteArray(packetAsBytes,pkt.Dest.getBytes());

        	packetAsBytes = joinByteArray(packetAsBytes,pkt.PktId.getBytes());
        	byte hop[] = new byte[1];
        	//hop[0] = (byte) (pkt.HopCount+1);
        	hop[0] = pkt.HopCount;
        	packetAsBytes = joinByteArray(packetAsBytes,hop);
        	
        	packetAsBytes = joinByteArray(packetAsBytes,shortToByteArray(pkt.dataSize));
        	
        	byte seq[] = new byte[1];
        	seq[0] = pkt.SeqNo;
        	packetAsBytes = joinByteArray(packetAsBytes,seq);
        	
        	packetAsBytes = joinByteArray(packetAsBytes,pkt.data);
//        	//add a stop
//        	packetAsBytes = joinByteArray(packetAsBytes," ".getBytes());
//        	packetAsBytes[packetAsBytes.length-1] = 0;
    		
        	header = null;
        	hop = null;
        	seq = null;
        	pkt = null;
        	
    		return packetAsBytes;
    }
   
    
    public static byte[] joinByteArray(byte[] a, byte[] b){
    
	    	int count = a.length + b.length;
	    	byte[] result = new byte[count];
	    	for(int i =0; i<a.length; i++){
	    		result[i] = a[i];
	    	}
	    	for(int i =0; i<b.length; i++){
	    		result[a.length+i] = b[i];
	    	}
    	
	    	return result;
    }
    
    public static String generatePacketId(String mDeviceAddress){
    		
    		String id = Integer.toString( (int)(Calendar.getInstance().getTimeInMillis()%(10*1000)));
    		    		
    		int length = id.length();
    		if(length<0) length = 0;
    		for(int i = length; i<4;i++){
    			id = "0"+id;
    		}
    		    		
    		return mDeviceAddress+id;
    }
    
    public synchronized boolean isPacketRepeated(DataPacket pkt){
        for ( Iterator<Long> iter = packetWatcher.values().iterator(); iter.hasNext(); ){
        		Long time = iter.next();
        		if ( (Calendar.getInstance().getTimeInMillis() - time )> 1000){
        			// remove from any state with a space in its long name.
        			iter.remove();// avoids ConcurrentModificationException
            }
        		//Log.e(TAG,""+time);
        }    	
        
        //Clean up memory
	    	r.gc();
	    	
	    	if(packetWatcher.containsKey(pkt.PktId)){
	    		return true;
	    	}
	    	else{
	    		packetWatcher.put(pkt.PktId, Calendar.getInstance().getTimeInMillis());
	    		return false;
	    	}
    }
    
    public ConnectionService() {
        mSelf = this;
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        mDeviceAddress = mBtAdapter.getAddress();
        mDeviceName = mBtAdapter.getName();
        DeviceNickName = mDeviceName;
        mApp = "";
        mBtSockets = new HashMap<String, BluetoothSocket>();
        mBtDeviceAddresses = new ArrayList<String>();
        mBtStreamWatcherThreads = new HashMap<String, Thread>();
        packetWatcher = new HashMap<String,Long>();
//        seqNoWatcher = new HashMap<String,Byte>();
//        recievedSeqNoWatcher = new HashMap<String,Byte>();
//        recievedSeqNoWatcherLock = new Object();
//        seqNoWatcherLock = new Object();
        mBtLock = new Object();
                
        webService = new GeoscribeComms2();        
                
        
        
        //packetQueue = new LinkedBlockingQueue<DataStream>();
        
        r = Runtime.getRuntime();
        if (T) Log.e(TAG, "Total Memory at Creation: "+r.totalMemory());
        if (T) Log.e(TAG, "Free Memory at Creation: "+r.freeMemory());
        
        mUuid = new ArrayList<UUID>();
        // Allow up to 7 devices to connect to the server
        mUuid.add(UUID.fromString("a60f35f0-b93a-11de-8a39-08002009c666"));
        mUuid.add(UUID.fromString("503c7430-bc23-11de-8a39-0800200c9a66"));
        mUuid.add(UUID.fromString("503c7431-bc23-11de-8a39-0800200c9a66"));
        mUuid.add(UUID.fromString("503c7432-bc23-11de-8a39-0800200c9a66"));
        mUuid.add(UUID.fromString("503c7433-bc23-11de-8a39-0800200c9a66"));
        mUuid.add(UUID.fromString("503c7434-bc23-11de-8a39-0800200c9a66"));
        mUuid.add(UUID.fromString("503c7435-bc23-11de-8a39-0800200c9a66"));
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    private class BtStreamWatcher implements Runnable {
        private String address;
        private PriorityBlockingQueue pktQueue;
        private Thread pktRouter;
        
        public BtStreamWatcher(String deviceAddress) {
            address = deviceAddress;
            pktQueue = new PriorityBlockingQueue();
            pktRouter = new Thread(new PacketRouter(pktQueue));
            pktRouter.start();
        }

      @Override
	public void run() {
	      //byte[] buffer = new byte[DataPacket.HEADER_SIZE];
    	  	  BluetoothSocket bSock;
    	  	  synchronized(mBtLock){
    	  		  bSock = mBtSockets.get(address);
    	  	  }
	      try {
	          InputStream instream = new BufferedInputStream(bSock.getInputStream());
	          int bytesRead = -1;

	          while (true) {
//	        	  	  DataStream dataStream = new DataStream();
	        	  	  byte[] dataBuffer = new byte[DataPacket.PACKET_SIZE];
	              bytesRead = instream.read(dataBuffer,0,DataPacket.HEADER_SIZE);
	              if (bytesRead != -1) {
	            	  	  //ensure HEADERSIDE Byte is read.
		            	  if (T) Log.e(TAG,"HeaderbyteRead:"+bytesRead);
		            	  int bytesRead2 = bytesRead;
		            	  int bufferSize = DataPacket.HEADER_SIZE;
		            	  while(bytesRead2 != bufferSize){
		            		  bufferSize = bufferSize - bytesRead2;
		            		  bytesRead2 = instream.read(dataBuffer,bytesRead,bufferSize);
		            		  bytesRead = bytesRead+bytesRead2;
		            	  }
		            	  
		        	  	  byte[] temp = new byte[]{dataBuffer[57],dataBuffer[58]};
		        	  	  short dataSize = byteArrayToShort(temp);
		        	  	  if (T) Log.e(TAG,"decoded DataSize:"+dataSize);
		        	  	  
		        	  	  //THIS IS A HACK????
		        	  	  
		        	  	  if(dataSize > DataPacket.DATA_MAX_SIZE || dataSize <0){
		        	  		  dataSize = DataPacket.DATA_MAX_SIZE;
		        	  		  Log.e(TAG,"ERROR DATASIZE!!!! :"+dataSize);
		        	  	  }
		        	  	  
		        	  	  
	            	  	  if(dataSize>0){
		            	  	  
		            	  	  bytesRead = instream.read(dataBuffer,DataPacket.HEADER_SIZE,dataSize);
		            	  	  if (bytesRead != -1) {
				            	  if (T) Log.e(TAG,"DatabyteRead:"+bytesRead);
				            	  bytesRead2 = bytesRead;
				            	  bufferSize = dataSize;
				            	  while(bytesRead2 != bufferSize){
				            		  bufferSize = bufferSize - bytesRead2;
				            		  bytesRead2 = instream.read(dataBuffer,DataPacket.HEADER_SIZE+bytesRead,bufferSize);
				            		  if(T) Log.e(TAG,"insufficient bytes, reading:"+bytesRead2);
				            		  bytesRead = bytesRead+bytesRead2;
				            	  }		            	  	  
		            	  	  }
		            	  	  
		            	  	  if(dataBuffer[0] != 1){
			            	  	  DataStream dataStream = new DataStream(address,dataBuffer);
			            	  	  	            	  	  
			            	  	  pktQueue.put(dataStream);
			            	  	  
			            	  	  dataStream = null;
		            	  	  }
		            	  	  else{
			            	  	  DataStream dataStream = new DataStream(address,dataBuffer,0);
	  	  	            	  	  
			            	  	  pktQueue.put(dataStream);
		            	  		  
			            	  	  dataStream = null;
		            	  	  }
		            	  	  
	            	  	  }
	              }
		          
		          //Clean up
	              dataBuffer = null;
		          r.gc();
	              
	          }          
	      } catch (IOException e) {
	          Log.i(TAG,
	                  "IOException in BtStreamWatcher - probably caused by normal disconnection",
	                  e);
	      }
	      // Getting out of the while loop means the connection is dead.
	      try {
	    	  	  synchronized(mBtLock){
		          mBtDeviceAddresses.remove(address);
		          mBtSockets.remove(address);
		          mBtStreamWatcherThreads.remove(address);
	    	  	  }
//	          synchronized (recievedSeqNoWatcherLock){
//	        	  		recievedSeqNoWatcher.remove(address);
//	          }
//	          
//	          synchronized (seqNoWatcherLock){
//	        	  		seqNoWatcher.remove(address);
//	          }
	          
	          	//to do inform other in network of connection lost
				DataPacket packet = new DataPacket();
				packet.Dest = mDeviceAddress;
				packet.Src = mDeviceAddress;
				packet.Ctr = 3;
				packet.SeqNo = 125;
				packet.PktId = generatePacketId(mDeviceAddress);
				
				packet.data = address.getBytes();
				packet.dataSize = (short) packet.data.length;
				
				//get Current network device List
				//reseting
				networkDevices = new HashMap<String,String>();
				
				DataPacket queryPacket = new DataPacket();
				queryPacket.Dest = mDeviceAddress;
				queryPacket.Src = mDeviceAddress;
				queryPacket.Ctr = 1;
				queryPacket.SeqNo = 125;
				queryPacket.PktId = generatePacketId(mDeviceAddress);				
				
				synchronized(mBtLock){
					for(int i=0; i< mBtDeviceAddresses.size(); i++){
						sendPkt(packet,mBtDeviceAddresses.get(i));
						sendPkt(queryPacket,mBtDeviceAddresses.get(i));
					}
				}
				
				//inform webservice
				String[] results = null;
				
				if(mAutoConnect) results =webService.deviceConnectionLost(address);
				
				if(mMasterDevice.compareTo(address)==0){
					mMasterDevice = "";
					recoveryMode = true;
					if(mAutoConnect) recoveryDevicesList = new ArrayList(Arrays.asList(results));
				}
								
				
	          mCallback.connectionLost(address);
	      } catch (RemoteException e) {
	          Log.e(TAG, "RemoteException in BtStreamWatcher while disconnecting", e);
	      }
	  }

		public int sendPkt(DataPacket pkt,String target)
        throws RemoteException {

				try {
					String destination = target;
					BluetoothSocket myBsock;
					synchronized(mBtLock){
						myBsock = mBtSockets.get(destination);
					}
			        if (myBsock != null) {
			            OutputStream outStream = myBsock.getOutputStream();	            
			            	
			            //pkt.HopCount = (byte) (pkt.HopCount+1);
			            
			            byte[] packetAsBytes = encrypt(pkt);
			            			            
			            outStream.write(packetAsBytes);  
			            if (T) Log.e(TAG,"sendPkt To:"+target+" Ctr:"+pkt.Ctr+" Src:"
	            	  			  +pkt.Src+" Dest:"+pkt.Dest+" HopCount:"+pkt.HopCount+" ID:"+pkt.PktId);		            
			            
			        }
			        else{
			        		return Connection.FAILURE;
			        }
			    } catch (IOException e) {
			        Log.i(TAG, "IOException in Forwarding packets",
			                e);
			    }

		    return Connection.SUCCESS;
		}      

      
       
    }
    
    private class PacketRouter implements Runnable{
    		private BlockingQueue pktQueue;
    		
        private ArrayList<DataPacket> queue;
        
        private	Comparator<DataPacket> comperator = new Comparator<DataPacket>(){
			@Override
			public int compare(DataPacket object1, DataPacket object2) {
				// TODO Auto-generated method stub
				if(object1.SeqNo == object2.SeqNo){
					return 0;
				}
				else if((object1.SeqNo - object2.SeqNo) <0){ 
					return -1;
				}
				else{
					return 1;
				}
			}
    			
    		};

    		
    		PacketRouter(BlockingQueue q){
    			pktQueue = q;
    			queue = new ArrayList<DataPacket>();
    			
    			Log.e(TAG,"Creating Router");
    		}
    		
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				 try {
					//Log.e(TAG,"Starting Router Route");
					DataStream dataStream = (DataStream) pktQueue.take();
					
					if (T) Log.e(TAG,"Read one dataStream :"+dataStream.seqNum);
					
					DataPacket pkt = new DataPacket();
									
					pkt = decrypt(dataStream.data);
					
					routePacket(pkt,dataStream.address);
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}
		
		private void routePacket(DataPacket pkt, String address) throws RemoteException {
			if(!isPacketRepeated(pkt)){
				  if (T) Log.e(TAG,"ACCEPTED From:"+address+" Ctr:"+pkt.Ctr+" Src:"
			  			  +pkt.Src+" Dest:"+pkt.Dest+" HC:"+pkt.HopCount+" ID:"+pkt.PktId+" Size:"+pkt.dataSize+" SN:"+pkt.SeqNo);	            	  		  
				  
			  byte request = 1;
			  byte reply = 2;
			  byte connectionLost = 3;
				  
			  if(mDeviceAddress.compareTo(pkt.Dest) ==0){
				  if (T) Log.e(TAG,"Packet reach Dest!");

					  if(pkt.Ctr==reply){
						  if (T) Log.e(TAG,"Control packet Received!");

						  if(!networkDevices.containsKey(pkt.Src)){
							  if (T) Log.e(TAG,"Found device:"+pkt.Src);
							  String name = new String(pkt.data,0,pkt.data.length);
							  networkDevices.put(pkt.Src, name);
							  Intent i = new Intent(ConnectionService.NEW_NETWORK_DEVICE_FOUND);
							  i.putExtra(ConnectionService.DEVICE_ADDR, pkt.Src);
							  i.putExtra(ConnectionService.DEVICE_NAME, name);
							  Log.e(TAG,"SendBroadCast");
							  sendBroadcast(i);
						  }
							            	  				              	  				  
					  }
					  else{
						  byte missingPkt;
//						  synchronized (recievedSeqNoWatcherLock){
//							  if(recievedSeqNoWatcher.containsKey(pkt.Src)){
//								  missingPkt = recievedSeqNoWatcher.get(pkt.Src);
//							  }
//							  else{
//								  missingPkt = 0;
//								  recievedSeqNoWatcher.put(pkt.Src, missingPkt);
//							  }
//						  }
				
						  
//		            	  	  if(pkt.SeqNo != missingPkt){
//		            	  		  Log.e(TAG,pkt.Src+": "+missingPkt+" MISSING!, adding"+pkt.SeqNo);
//		            	  		  queue.add(pkt);
//		            	  		  Collections.sort(queue,comperator);
//		            	  	  }
//		            	  	  else{
//		            	  		  Log.e(TAG,pkt.Src+": "+missingPkt+" Found!");
		            	  		  mCallback.messageReceived(pkt.Src, pkt.data);
		            	  		  
//		            	  		  missingPkt++;
//		            	  		  if(missingPkt>100){
//		            	  			  missingPkt = 0;
//		            	  		  }
		            	  		  
//  		            	  		  synchronized (recievedSeqNoWatcherLock){
//  		            	  			  recievedSeqNoWatcher.remove(pkt.Src);
//  		            	  			  recievedSeqNoWatcher.put(pkt.Src, missingPkt);
//		            	  		  }
		            	  		  
//		            	  		  int index = queue.size();
//		            	  		  	            	  		  	            	  		  
//		            	  		  for(int i = 0; i<queue.size();i++){
//		            	  			  if(queue.get(i).SeqNo == missingPkt){
//		            	  				  index =i;
//		            	  			  }
//		            	  		  }
		            	  		  //if not found index = queue.size;
		            	  		  
//		            	  		  while(true){
//			            	  		  if(index == queue.size()){ //missing pkt not found
//			            	  			  break;
//			            	  		  }            	  			  
//			            	  		  Log.e(TAG,pkt.Src+": "+missingPkt+" Release Found!");
//		            	  			  missingPkt = (byte) (queue.get(index).SeqNo+1);
//			            	  		  if(missingPkt>100){
//			            	  			  missingPkt = 0;
//			            	  		  }
//
//			            	  		  synchronized (recievedSeqNoWatcherLock){
//	  		            	  			  recievedSeqNoWatcher.remove(pkt.Src);
//	  		            	  			  recievedSeqNoWatcher.put(pkt.Src, missingPkt);
//			            	  		  }
//			            	  		  
//			            	  		
//			            	  		  DataPacket temp = queue.remove(index);
//			            	  		  
//			            	  		  mCallback.messageReceived(temp.Src,temp.data);
//			            	  		  
//			            	  		  index = queue.size();
//			            	  		  for(int i = 0; i<queue.size();i++){
//			            	  			  if(queue.get(i).SeqNo == missingPkt){
//			            	  				  index =i;
//			            	  			  }
//			            	  		  }	            	  		  
//			            	  		  		            	  		  
//		            	  		  }
		            	  		  
//		            	  	  }
					  }
				  }
				  else{

					if(pkt.Ctr==request || pkt.Ctr == connectionLost){
						
						if (T) Log.e(TAG,"Control packet Received!,"+pkt.Ctr);
						DataPacket packet = new DataPacket();						
						
						if(pkt.Ctr == request){
							//Send message to dest.
							packet.Dest = pkt.Dest;
							packet.Src = mDeviceAddress;
							packet.PktId = generatePacketId(mDeviceAddress);
							packet.Ctr = reply;
							packet.dataSize = (short)DeviceNickName.length();
							packet.data =  DeviceNickName.getBytes();
							packet.HopCount = pkt.HopCount;
							
							boolean contains;
							
							synchronized (mBtLock){
								contains = mBtDeviceAddresses.contains(packet.Dest);
							}
							
							if(contains){
								if (T) Log.e(TAG,"Reply connected");
								sendPkt(packet,packet.Dest);
							}
							else{
								if (T) Log.e(TAG,"Reply to Source!");
					            	sendPkt(packet,address);	            	  				
							}
						
						}
						else{
							String deviceLost =new String(pkt.data,0,pkt.dataSize); 
							
							//no need to check for mMasterDevice lost.
							
							mCallback.connectionLost(deviceLost);
							
//							synchronized (recievedSeqNoWatcherLock){
//								if(recievedSeqNoWatcher.containsKey(deviceLost)){
//										recievedSeqNoWatcher.remove(deviceLost);
//								}
//							}
//							
//							synchronized (seqNoWatcherLock){
//								if(seqNoWatcher.containsKey(deviceLost)){
//									seqNoWatcher.remove(deviceLost);
//								}
//							}

						}
						
						synchronized (mBtLock){
							if (T) Log.e(TAG,"mBtDeviceAddress size:"+mBtDeviceAddresses.size());
							if (T) Log.e(TAG,"mBtDeviceAddress Addr:"+mBtDeviceAddresses.get(0));
							if (T) Log.e(TAG,"mBtDeviceAddress Addr:"+mBtDeviceAddresses.get(0).compareTo(address));
							if (T) Log.e(TAG,"mBtDeviceAddress Addr:"+mBtDeviceAddresses.get(0).compareTo(pkt.Dest));
							//forward to all peers other than the one who u just replied to.
							for (int i = 0; i < mBtDeviceAddresses.size(); i++) {
								if(mBtDeviceAddresses.get(i).compareTo(address) != 0 && 
								   mBtDeviceAddresses.get(i).compareTo(pkt.Dest) != 0){
									//nodes that u have not replied to
									if (T) Log.e(TAG,"Forwarding to Peer:");
									sendPkt(pkt,mBtDeviceAddresses.get(i));
								}
								
							}
						}
						
						//clean up 
						packet = null;
						
					}
					else{
						if(pkt.HopCount < 10){ 	  	
							  forwardPkt(pkt,address);
						}
						else{
							  //do nothing, dun forward.
						}
					}
				  }
	              
			  }
			  else{
				  if (T) Log.e(TAG,"Rejected From:"+address+" Ctr:"+pkt.Ctr+" Src:"
			  			  +pkt.Src+" Dest:"+pkt.Dest+" HC:"+pkt.HopCount+" ID:"+pkt.PktId+" Size:"+pkt.dataSize+" SN:"+pkt.SeqNo);	            	  		  
			  }
		}
		
		public int sendPkt(DataPacket pkt,String target)
        throws RemoteException {

				try {
					String destination = target;
					BluetoothSocket myBsock;
					synchronized (mBtLock){
						myBsock = mBtSockets.get(destination);
					}
			        if (myBsock != null) {
			            OutputStream outStream = myBsock.getOutputStream();	            
			            	
			            pkt.HopCount = (byte) (pkt.HopCount+1);
			            
			            byte[] packetAsBytes = encrypt(pkt);
			            			            
			            outStream.write(packetAsBytes);  
			            if (T) Log.e(TAG,"sendPkt To:"+target+" Ctr:"+pkt.Ctr+" Src:"
	            	  			  +pkt.Src+" Dest:"+pkt.Dest+" HopCount:"+pkt.HopCount+" ID:"+pkt.PktId);		            
			            
			        }
			        else{
			        		return Connection.FAILURE;
			        }
			    } catch (IOException e) {
			        Log.i(TAG, "IOException in Forwarding packets",
			                e);
			    }

		    return Connection.SUCCESS;
		}      
      
		public int forwardPkt(DataPacket pkt, String Source)
        throws RemoteException {
			synchronized (mBtLock){
            for (int i = 0; i < mBtDeviceAddresses.size(); i++) {
				try {
					if(mBtDeviceAddresses.get(i).compareTo(Source)!=0){ //dun forward back to source
						String destination = mBtDeviceAddresses.get(i);
						BluetoothSocket myBsock = mBtSockets.get(destination);
				        if (myBsock != null) {
				            OutputStream outStream =myBsock.getOutputStream();	            
			            		
				            pkt.HopCount++;
				            
				            byte[] packetAsBytes = encrypt(pkt);
				            
				           DataPacket test = decrypt(packetAsBytes);
				            
				           if (T) Log.e(TAG,"Forwarding To:"+destination+" Ctr:"+test.Ctr+" Src:"
		            	  			  +test.Src+" Dest:"+test.Dest+" HopCount:"+test.HopCount+" ID:"+test.PktId+" dataSize:"+test.dataSize);
				           
				            
				            outStream.write(packetAsBytes);
				            if (T) Log.e(TAG,"Forwarding To:"+destination+" Ctr:"+pkt.Ctr+" Src:"
		            	  			  +pkt.Src+" Dest:"+pkt.Dest+" HopCount:"+pkt.HopCount+" ID:"+pkt.PktId+" dataSize:"+pkt.dataSize);
				            
				            //Clean up
				            packetAsBytes = null;
				            //pkt = null;
				            r.gc();
				            
				        }
				        else{
			        			return Connection.FAILURE;
				        }	
					}
			    } catch (IOException e) {
			        Log.i(TAG, "IOException in Forwarding packets",
			                e);
			    }
			}
		    return Connection.SUCCESS;
			}
		}		
		
    		
    }

    private class ConnectionWaiter implements Runnable {
        private String srcApp;

        private int maxConnections;

        public ConnectionWaiter(String theApp, int connections) {
            srcApp = theApp;
            maxConnections = connections;
        }

        @Override
		public void run() {
            try {
                for (int i = 0; i < Connection.MAX_SUPPORTED && maxConnections > 0; i++) {
                    BluetoothServerSocket myServerSocket = mBtAdapter
                            .listenUsingRfcommWithServiceRecord(srcApp, mUuid.get(i));
                    BluetoothSocket myBSock = myServerSocket.accept();
                    String address = myBSock.getRemoteDevice().getAddress();
                    
                    boolean contains;
                    synchronized (mBtLock){
                    		contains = mBtSockets.containsKey(address);
                    }
                    
                    while(contains){
                    		Log.e(TAG,address+" Already connected");
                    		mHandler.obtainMessage(0, address+" Already Connected").sendToTarget();
                    		myBSock = myServerSocket.accept();
                    }
                                        
                    myServerSocket.close(); // Close the socket now that a proper
                    // connection has been made.
                    
                    synchronized (mBtLock){
                    mBtSockets.put(address, myBSock);
                    mBtDeviceAddresses.add(address);
                    }
                    
                    Thread mBtStreamWatcherThread = new Thread(new BtStreamWatcher(address));
                    mBtStreamWatcherThread.start();

                    synchronized (mBtLock){
                    mBtStreamWatcherThreads.put(address, mBtStreamWatcherThread);
                    if (T) Log.e(TAG, "Listen Add btDevice:"+address+" Device count:"+mBtDeviceAddresses.size());
                    if (T) Log.e(TAG, "Listen Add BtStreamThread:"+address+" BtStreamThread count:"+mBtStreamWatcherThreads.size());
                    }
                    
                    maxConnections = maxConnections - 1;
                    
                    //inform webservice
                    if(mAutoConnect) webService.deviceConnected(mDeviceAddress, address);
                    
                    if (mCallback != null) {
                        mCallback.incomingConnection(address);
                    }
                }
                if (mCallback != null) {
                    mCallback.maxConnectionsReached();
                }
            } catch (IOException e) {
                Log.i(TAG, "IOException in ConnectionService:ConnectionWaiter", e);
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException in ConnectionService:ConnectionWaiter", e);
            }
        }
    }

    private class GPSListener implements Runnable {
        private UseGPS GPS;
        
        private Location GeoCoordinate;

        public GPSListener() {
            GPS = new UseGPS(mSelf);
            GeoCoordinate = null;
        }

        @Override
		public void run() {
        		while(true){
	        		try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        		
	        		GeoCoordinate = GPS.returnCoordinates();
	        		if(D) Log.e(TAG,"AutoConnect, getting GPS Coordinate");
	        		if(GeoCoordinate!=null){
	        			mGeoCoordinate = GeoCoordinate;
	        			if(D) Log.e(TAG,"Got Reading:"+GeoCoordinate.getLatitude()+" ,"+GeoCoordinate.getLongitude());
	        			
	        			if(mAutoConnect && mMasterDevice=="" &&!recoveryMode){
	        				mHandler.obtainMessage(0, "Attempting to AutoConnect, Searching...").sendToTarget();
		    	    			Log.e(TAG,"LAT:"+GeoCoordinate.getLatitude());
		    	    			double geoY = GeoCoordinate.getLatitude();
		    	    			Log.e(TAG,"LONG:"+GeoCoordinate.getLongitude());
		    	    			double geoX = GeoCoordinate.getLongitude();
		    	    			
		    	        		RegisterDeviceResult[] result = webService.registerDevice(mDeviceAddress, geoX, geoY);
		    	            if(mAutoConnect){
		    		            		
		    		            		boolean connectSucceed = false;
		    		            		
		    		            		if(result !=null){
		    		            			
		    		            			for( RegisterDeviceResult value: result){
		    		            				
		    		            				if(value.hardwareId.compareTo(mDeviceAddress)!=0){
			    				            		Log.e(TAG,"Auto connecting to "+value.hardwareId);
			    				            		
			    				            		mHandler.obtainMessage(0, "Connecting to "+value.hardwareId).sendToTarget();
			    				            		//Toast.makeText(mSelf, "Connecting to "+value.hardwareId, 1).show();
			    				            		
			    				            		try {
			    										if(connect("AutoConnect",value.hardwareId)==Connection.SUCCESS){
			    											mHandler.obtainMessage(0, "Connected to "+value.hardwareId).sendToTarget();
			    											connectSucceed = true;
			    											webService.deviceConnected(value.hardwareId, mDeviceAddress);
			    											mMasterDevice = value.hardwareId;
			    											break;
			    										}
			    									} catch (RemoteException e) {
			    										// TODO Auto-generated catch block
			    										e.printStackTrace();
			    									}
			    		            				
			    		            			}
		    		            			}
		    		            		}
		    		            		else{
		    		            			mHandler.obtainMessage(0, "Unable to a response from webservice").sendToTarget();
		    		            			//Toast.makeText(mSelf, "Unable to a response from webservice", 1).show();
		    		            		}
		    		            		
		    		            		if(!connectSucceed){
		    		            			if((result.length==0) || (result[0].hardwareId.compareTo(mDeviceAddress)==0 && result.length==1 ) ){
		    		            				mHandler.obtainMessage(0, "No available Cluster found").sendToTarget();
		    		            				//Toast.makeText(mSelf, "No available Cluster found", 1).show();
		    		            			}
		    		            			else{
		    		            				mHandler.obtainMessage(0, "AutoConnection Unsucessful").sendToTarget();
		    		            				//Toast.makeText(mSelf, "AutoConnection Unsucessful", 1).show();
		    		            			}
		    		            		}
		    		            		
		    		            		//try again 1 min later.
		    		            		try {
											Thread.sleep(1*60*1000);
		    		            		} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
		    		            		}
		    	            	}
	        				
	        			}
	        			
	        			if(recoveryMode && mAutoConnect){
	        				//Recovering from lost Masterconnection
	        				for( String value: recoveryDevicesList){
	        					if(!networkDevices.containsKey(value) && value!=null){
	        						//not in current networkDevices
				            		Log.e(TAG,"Recovery connecting to "+value);
				            		
				            		mHandler.obtainMessage(0, "Recovery Connecting to "+value).sendToTarget();
				            		//Toast.makeText(mSelf, "Connecting to "+value.hardwareId, 1).show();
				            		
				            		try {
										if(connect("AutoConnect",value)==Connection.SUCCESS){
											if(mAutoConnect) webService.deviceConnected(value, mDeviceAddress);
											mMasterDevice = value;
											break;
										}
									} catch (RemoteException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
	        						
	        					}
	        					
	        				}        				
	        				recoveryMode = false;
	        			}
	        			
	        		}
        		}
        }

		public int connect(String srcApp, String device) throws RemoteException {
//          if (mApp.length() > 0) {
//          		Log.e(TAG, "Return failure");
//              return Connection.FAILURE;
//          }
			
		  synchronized (mBtLock){
		  if(mBtSockets.containsKey(device)){
			  mHandler.obtainMessage(0, "Already connected to "+device).sendToTarget();
			  return Connection.FAILURE;
		  }
		  }
			
          mApp = srcApp;
          BluetoothDevice myBtServer = mBtAdapter.getRemoteDevice(device);
          BluetoothSocket myBSock = null;

          for (int i = 0; i < Connection.MAX_SUPPORTED && myBSock == null; i++) {
              for (int j = 0; j < 3 && myBSock == null; j++) {
                  myBSock = getConnectedSocket(myBtServer, mUuid.get(i));
                  if (myBSock == null) {
                      try {
                          Thread.sleep(200);
                      } catch (InterruptedException e) {
                          Log.e(TAG, "InterruptedException in connect", e);
                      }
                  }
              }
          }
          if (myBSock == null) {
              return Connection.FAILURE;
          }
          synchronized (mBtLock){
          mBtSockets.put(device, myBSock);
          mBtDeviceAddresses.add(device);
          if (T) Log.e(TAG, "Connect Add btDevice:"+device+" Devicesss count:"+mBtDeviceAddresses.size());
          }
          
          Thread mBtStreamWatcherThread = new Thread(new BtStreamWatcher(device));
          mBtStreamWatcherThread.start();
          
          synchronized (mBtLock){
          mBtStreamWatcherThreads.put(device, mBtStreamWatcherThread);
          if(T) Log.e(TAG,"Adding new threads");
          if (T) Log.e(TAG, "Listen Add BtStreamThread:"+device+" BtStreamThread count:"+mBtStreamWatcherThreads.size());
          }
          
         mMasterDevice = device;
          
          return Connection.SUCCESS;
      }
        
        
    }
    
    
    private BluetoothSocket getConnectedSocket(BluetoothDevice myBtServer, UUID uuidToTry) {
        BluetoothSocket myBSock;
        try {
            myBSock = myBtServer.createRfcommSocketToServiceRecord(uuidToTry);
            myBSock.connect();
            return myBSock;
        } catch (IOException e) {
            Log.i(TAG, "IOException in getConnectedSocket", e);
        }
        return null;
    }
    
    

    private final IConnection.Stub mBinder = new IConnection.Stub() {
        @Override
		public int startServer(String srcApp, int maxConnections, boolean AutoConnect) throws RemoteException {
//            if (mApp.length() > 0) {
//                return Connection.FAILURE;
//            }
//            mApp = srcApp;
        		if(AutoConnect) Log.e(TAG,"mAutoConnect is True");
        		mAutoConnect = AutoConnect;  
            (new Thread(new ConnectionWaiter(srcApp, maxConnections))).start();
            (new Thread(new GPSListener())).start();
            
            //GPS
            
            
//            //start discoverable 
//            Intent i = new Intent();
//            i.setClass(mSelf, StartDiscoverableModeActivity.class);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(i);
            return Connection.SUCCESS;
        }
        
        

        @Override
		public int connect(String srcApp, String device) throws RemoteException {
//            if (mApp.length() > 0) {
//            		Log.e(TAG, "Return failure");
//                return Connection.FAILURE;
//            }
        		synchronized (mBtLock){
			if(mBtSockets.containsKey(device)){
				mHandler.obtainMessage(0, "Already connected to "+device).sendToTarget();
				return Connection.FAILURE;
			}
        		}
        	
            mApp = srcApp;
            BluetoothDevice myBtServer = mBtAdapter.getRemoteDevice(device);
            BluetoothSocket myBSock = null;

            for (int i = 0; i < Connection.MAX_SUPPORTED && myBSock == null; i++) {
                for (int j = 0; j < 3 && myBSock == null; j++) {
                    myBSock = getConnectedSocket(myBtServer, mUuid.get(i));
                    if (myBSock == null) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            Log.e(TAG, "InterruptedException in connect", e);
                        }
                    }
                }
            }
            if (myBSock == null) {
                return Connection.FAILURE;
            }

            synchronized (mBtLock){
            mBtSockets.put(device, myBSock);
            mBtDeviceAddresses.add(device);
            if (T) Log.e(TAG, "Connect Add btDevice:"+device+" Device count:"+mBtDeviceAddresses.size());
            }
            
            Thread mBtStreamWatcherThread = new Thread(new BtStreamWatcher(device));
            mBtStreamWatcherThread.start();
            
            synchronized (mBtLock){
            mBtStreamWatcherThreads.put(device, mBtStreamWatcherThread);
            if (T) Log.e(TAG, "Listen Add BtStreamThread:"+device+" BtStreamThread count:"+mBtStreamWatcherThreads.size());
            }
            
            mMasterDevice = device;
            
            return Connection.SUCCESS;
        }

        @Override
		public int broadcastMessage(String srcApp,String destination, byte[] message) throws RemoteException {
//            if (!mApp.equals(srcApp)) {
//                return Connection.FAILURE;
        	
        		if (T) Log.e(TAG,"BroadCasting");
        		byte zero = 0;
        		String dest;
        		byte seqNo = 0;
        		
        		synchronized (mBtLock){
            for (int i = 0; i < mBtDeviceAddresses.size(); i++) {
            		if(destination == ""){
            			dest = mBtDeviceAddresses.get(i);
            		}
            		else{
            			dest = destination;
            		}
            		//for broadcasting, seqNo should not increase.
            		
            		if(destination ==""){
            			//sending to everyone
	            		if(sendByte(zero,mBtDeviceAddresses.get(i),dest,message,false)==Connection.FAILURE){
	            			return Connection.FAILURE;
	            		}            			            			
            		}
            		else{
            			//sendint to a specific target
                		if(i ==0){//first packet
		            		if(sendByte(zero,mBtDeviceAddresses.get(i),dest,message,false)==Connection.FAILURE){
			            			return Connection.FAILURE;
			            	}
		            			
		            	}
		            	else{//subsequence packet is the same seqNo
		            		if(sendByte(zero,mBtDeviceAddresses.get(i),dest,message,true)==Connection.FAILURE){
		            			return Connection.FAILURE;
		            		}
		            	}
            			
            		}
            		
//            		if(i ==0){
//	            		if(sendByte(zero,mBtDeviceAddresses.get(i),dest,message,false)==Connection.FAILURE){
//	            			return Connection.FAILURE;
//	            		}
//            			
//            		}
//            		else{
//	            		if(sendByte(zero,mBtDeviceAddresses.get(i),dest,message,true)==Connection.FAILURE){
//	            			return Connection.FAILURE;
//	            		}
//            		}

            	}
        		}
            return Connection.SUCCESS;
        }
        
        public int broadcastPacket(DataPacket pkt) throws RemoteException{
        		if (T) Log.e(TAG,"PacketBroadCasting");
        		
        		synchronized (mBtLock){
            for (int i = 0; i < mBtDeviceAddresses.size(); i++) {
            		if(sendPacket(pkt,mBtDeviceAddresses.get(i))==Connection.FAILURE){
            			return Connection.FAILURE;
            		}
            }
        		}
            
        		return Connection.SUCCESS;
        }

        @Override
		public String getConnections(String srcApp) throws RemoteException {
            if (!mApp.equals(srcApp)) {
                return "";
            }
            String connections = "";
            synchronized (mBtLock){
            for (int i = 0; i < mBtDeviceAddresses.size(); i++) {
                connections = connections + mBtDeviceAddresses.get(i) + ",";
            }
            }
            return connections;
        }

        @Override
		public int getVersion() throws RemoteException {
            try {
                PackageManager pm = mSelf.getPackageManager();
                PackageInfo pInfo = pm.getPackageInfo(mSelf.getPackageName(), 0);
                return pInfo.versionCode;
            } catch (NameNotFoundException e) {
                Log.e(TAG, "NameNotFoundException in getVersion", e);
            }
            return 0;
        }

        @Override
		public int registerCallback(String srcApp, IConnectionCallback cb) throws RemoteException {
//            if (!mApp.equals(srcApp)) {
//                return Connection.FAILURE;
//            }
            mCallback = cb;
            return Connection.SUCCESS;
        }

        public int sendPacket(DataPacket pkt,String target)
        			throws RemoteException {
			try {
				String destination = target;
				BluetoothSocket myBsock;
				synchronized (mBtLock){
					myBsock = mBtSockets.get(destination);
				}			
				
		        if (myBsock != null) {
		            OutputStream outStream = myBsock.getOutputStream();	            
		            pkt.HopCount = (byte) (pkt.HopCount + 1);

		            byte[] packetAsBytes = encrypt(pkt);
		            
		            outStream.write(packetAsBytes);
		            
		            if (T) Log.e(TAG,"SendPacket To:"+destination+" Ctr:"+pkt.Ctr+" Src:"
          	  			  +pkt.Src+" Dest:"+pkt.Dest+" HC:"+pkt.HopCount+" ID:"+pkt.PktId+" SN:"+pkt.SeqNo);

		            //Clean up
		            packetAsBytes = null;
		            pkt = null;
		            r.gc();
		            
		            
		        		return Connection.SUCCESS;
		        }
		    } catch (IOException e) {
		        Log.i(TAG, "IOException in Forwarding packets",
		                e);
		    }
		    	
		    return Connection.FAILURE;
        }
        
        //Only 1mb of data can be send including 57byte of header, 17src, 17dest,21 pktId, 2 hop count, 967 data
		public int sendByte(byte Ctr,String target ,String destination, byte[] data, boolean offSeqNo)
		        throws RemoteException {
		    try {
		    		if (D) Log.e(TAG,"Free memory at SendByte: "+r.freeMemory());
		    	
		    		if(data.length > DataPacket.DATA_MAX_SIZE){
		    			Log.e(TAG,"Data should not exceed"+DataPacket.DATA_MAX_SIZE+"byte!");
		    			return Connection.FAILURE;
		    		}
		    	
		        BluetoothSocket myBsock = mBtSockets.get(target);
		        if (myBsock != null) {
		            OutputStream outStream = myBsock.getOutputStream();	            
		            	DataPacket packet = new DataPacket();
		            	
		            	byte seqNo =0;
		            	
		            	if(Ctr != 0){
		            		seqNo = 125;
		            	}
		            	else{
//		            		synchronized(seqNoWatcherLock){
//			            		if(seqNoWatcher.containsKey(destination)){
//			            			seqNo = seqNoWatcher.get(destination);
//			            			
//			            			if(!offSeqNo){
//				            			seqNo = (byte)(seqNo +1);
//				            			
//				            			if(seqNo >100){
//				            				seqNo = 0;
//				            			}
//				            			
//				            			seqNoWatcher.remove(destination);
//				            			seqNoWatcher.put(destination, seqNo);
//			            			}
//			            		}
//			            		else{
//			            			seqNoWatcher.put(destination, seqNo);
//			            		}
//		            		}
		            	}
		            	
		            	packet.SeqNo = seqNo;
		            	
		            	packet.Ctr = Ctr;
		            	packet.Src = mDeviceAddress;	            	
		            	packet.Dest = destination;
		            	packet.PktId = generatePacketId(mDeviceAddress);
		            	//packet.PktId = mDeviceAddress+"1234";
		            	packet.dataSize = (short) data.length;
		            	//packet.data = joinByteArray(data," ".getBytes());
		            	packet.data = data;
		            	
		            	byte[] packetAsBytes = encrypt(packet);
		            	

	            		outStream.write(packetAsBytes);

		            
		            if (T) Log.e(TAG,"SendBtye To:"+target+" Ctr:"+packet.Ctr+" Src:"
          	  			  +packet.Src+" Dest:"+packet.Dest+" HC:"+packet.HopCount+" ID:"+packet.PktId+" SN:"+packet.SeqNo);
		            
			          
			        //Clean up
		            packetAsBytes = null;
			        packet = null;
			        data = null;
			        destination = null;
			        target = null;
			        outStream.flush();
			        outStream =null;
			        myBsock = null;
			        r.gc();
		            
		            return Connection.SUCCESS;
		        }
		    } catch (IOException e) {
		        Log.i(TAG, "IOException in sendMessage - Dest:" + destination + ", Msg:" + data.toString(),
		                e);
		    }
		    return Connection.FAILURE;
		}

        @Override
		public int sendMessage(String srcApp, String destination, byte[] message)
		        throws RemoteException {
        			
        			if(message ==null){
        				Log.e(TAG,"Unable to Send NULL byte Message");
        				return Connection.FAILURE;
        			}
        	
//		    		Location test = GPS.returnCoordinates();
//		    		
//		    		if(mGeoCoordinate != null) {
//		    			Log.e(TAG,"LAT:"+mGeoCoordinate.getLatitude());
//		    			Log.e(TAG,"LONG:"+mGeoCoordinate.getLongitude());
//		    		}
//		    		else
//		    		{
//		    			Log.e(TAG,"location is null");
//		    		}
        	
        	
        			byte zero = 0;
        			
        			synchronized (mBtLock){
        			if(mBtDeviceAddresses.size() == 0) return Connection.FAILURE;
        			}
        			
        			boolean contains;
        			synchronized (mBtLock){
        				contains = mBtDeviceAddresses.contains(destination);
        			}
        			
        			if(contains){
        				
        				return sendByte(zero,destination,destination,message,false);
        			}
        			else{
        				return broadcastMessage("",destination,message);
        			}
        			
        }
        
        
        @Override
		public void shutdown(String srcApp) throws RemoteException {
            try {
            	synchronized (mBtLock){
                for (int i = 0; i < mBtDeviceAddresses.size(); i++) {
                    BluetoothSocket myBsock = mBtSockets.get(mBtDeviceAddresses.get(i));
                    myBsock.close();
                }
                mBtSockets = new HashMap<String, BluetoothSocket>();
                mBtStreamWatcherThreads = new HashMap<String, Thread>();
                mBtDeviceAddresses = new ArrayList<String>();
                mApp = "";
                mAutoConnect = false;
                mMasterDevice ="";
                recoveryMode = false;
            	}
            } catch (IOException e) {
                Log.i(TAG, "IOException in shutdown", e);
            }
            
        }

        @Override
		public int unregisterCallback(String srcApp) throws RemoteException {
//            if (!mApp.equals(srcApp)) {
//                return Connection.FAILURE;
//            }
            mCallback = null;
            return Connection.SUCCESS;
        }

		@Override
		public HashMap<String,String> getDevices(){
			//List<String> devices = new ArrayList<String>();
			if (T) Log.e(TAG,"Getting devices!");

			//reseting
			networkDevices = new HashMap<String,String>();
			
			//fire signal to get reply
			DataPacket packet = new DataPacket();
			packet.Dest = mDeviceAddress;
			packet.Src = mDeviceAddress;
			packet.Ctr = 1;
			packet.SeqNo = 125;
			packet.PktId = generatePacketId(mDeviceAddress);
			
			try {
				broadcastPacket(packet);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			//Clean up
			packet = null;
			
			return networkDevices;
//			try
//			{
//				Thread.sleep(3000); // do nothing for 1000 miliseconds (3 second)
//			}
//				catch(InterruptedException e)
//			{
//					e.printStackTrace();
//			}		
//			
//			return networkDevicesAddresses;
		
		}
        
        @Override
		public String getAddress() throws RemoteException {
            return mBtAdapter.getAddress();
        }
        
        @Override
		public String getNickName() throws RemoteException {
            return DeviceNickName;
        }
        
        @Override
		public void setNickName(String nickName) throws RemoteException {
        		DeviceNickName = nickName;
        }        
        
        @Override
		public String getName() throws RemoteException {
            return mBtAdapter.getName();
        }
                
    };

}