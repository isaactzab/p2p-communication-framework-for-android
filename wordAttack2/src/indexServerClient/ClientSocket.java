package indexServerClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

public class ClientSocket {
	private Socket socket = null;
	private PrintWriter out = null;
    private BufferedReader in = null;
    
    //constructor
    public ClientSocket(){
    }
    

    
    //open a socket to indexServerClientConfig.serverIP at port 9
    public boolean listenSocket(){
    	
    	boolean success =true;
    	//Log.i(Constants.WifiLog, "inside: ClientSocket.listenSocket() before try block.");
    	try {	
    		socket=new Socket(IndexServerClientConfig.getServerIP(), IndexServerClientConfig.getServerPortNumber());
    		//Log.e(Constants.WifiLog, "inside: ClientSocket.listenSocket() after command");
			//Log.e("Socket","Server is at "+socket.getInetAddress());
			
			out = new PrintWriter(socket.getOutputStream(),true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			return success;
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			//Log.e(Constants.WifiLog, "unknown host");
			e.printStackTrace();
			success=false;
			return success;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//Log.e(Constants.WifiLog,"IO Exception");
			e.printStackTrace();
			success=false;
			return success;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//Log.e(Constants.WifiLog, "exception");
			e.printStackTrace();
			success=false;
			return success;
			
		}
    }
    
    
    //write line to server
    public void writeLineToServer(String line){
        out.println(line);
        //Log.i(Constants.WifiLog,"Write Line to Server: "+line);
    }
    
    
    //returns a String that is read from the server
    public String readLineFromServer(){
    	String line = null;
    	try {
			line= in.readLine();
			//Log.i(Constants.WifiLog, "Read Line from Server: "+line);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return line;
    }  
}
