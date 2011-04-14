package com.example.Geoscribe.comms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.util.Log;

public class GeoscribeComms extends Activity {
	
	//private DataHelper dh;
	
	   public String Authenicate(String sessionKey) {  
		    // Create a new HttpClient and Post Header  
		    HttpClient httpclient = new DefaultHttpClient();  
		    HttpPost httppost = new HttpPost("http://137.132.145.205/Geoscribe/test.php");  
	        // Add your data  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
	        nameValuePairs.add(new BasicNameValuePair("sessionKey", sessionKey));  
	        //nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));  

	       String msg =null; 
	        
		    try {  
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
		        Log.d("myapp", "works till here. 2"); 
		        // Execute HTTP Post Request  
		        HttpResponse response = httpclient.execute(httppost);
		        //Logging will cause error for some reason
		       // Log.d("myapp", "response " + EntityUtils.toString(response.getEntity()));
		        
		        try{
		        	
		        		msg =  EntityUtils.toString(response.getEntity());
		        }
		        catch (IOException e){
		        		Log.d("myapp", "ERROR " + e);
		        }
		    } catch (ClientProtocolException e) {  
		        // TODO Auto-generated catch block  
		    		Log.d("myapp2", "response " + e);
				msg =  "ClientProtocolException";
		    } catch (IOException e) {  
		        // TODO Auto-generated catch block  
		    		Log.d("myapp", "response " + e);
		    	
				msg =  "IOException";
		    }

		    return msg;
		    
		}    
	   
	   public String Authenicate(String email, String password) {  
		    // Create a new HttpClient and Post Header  
		    HttpClient httpclient = new DefaultHttpClient();  
		    HttpPost httppost = new HttpPost("http://137.132.145.205/Geoscribe/login.php");  
	       // Add your data  
	       List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
	       nameValuePairs.add(new BasicNameValuePair("username", email));
	       nameValuePairs.add(new BasicNameValuePair("password", password));  
	       //nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));  

	      String msg =null; 
	       
		    try {  
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
		        Log.d("myapp", "works till here. 2"); 
		        // Execute HTTP Post Request  
		        HttpResponse response = httpclient.execute(httppost);
		        //Logging will cause error for some reason
		       // Log.d("myapp", "response " + EntityUtils.toString(response.getEntity()));
		        
		        try{
		        	
		        		msg =  EntityUtils.toString(response.getEntity());
		        }
		        catch (IOException e){
		        		Log.d("myapp", "ERROR " + e);
		        }
		    } catch (ClientProtocolException e) {  
		        // TODO Auto-generated catch block  
		    		Log.d("myapp2", "response " + e);
				msg =  "ClientProtocolException";
		    } catch (IOException e) {  
		        // TODO Auto-generated catch block  
		    		Log.d("myapp", "response " + e);
		    	
				msg =  "IOException";
		    }

		    return msg;
		    
		}   
	   
	   //Return SessionKey on Success.
	   public String Register(String email, String password, String facebookLogin,
			      String username, String gender, String age, String DOB, String hometown) {  
		    // Create a new HttpClient and Post Header  
		    HttpClient httpclient = new DefaultHttpClient();  
		    HttpPost httppost = new HttpPost("http://137.132.145.205/Geoscribe/register.php");  
	      // Add your data  
	      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
	      nameValuePairs.add(new BasicNameValuePair("email", email));
	      nameValuePairs.add(new BasicNameValuePair("password", password));  
	      nameValuePairs.add(new BasicNameValuePair("facebookLogin", facebookLogin));
	      nameValuePairs.add(new BasicNameValuePair("username", username));
	      nameValuePairs.add(new BasicNameValuePair("gender", gender));
	      nameValuePairs.add(new BasicNameValuePair("age", age));
	      nameValuePairs.add(new BasicNameValuePair("DOB", DOB));
	      nameValuePairs.add(new BasicNameValuePair("hometown", hometown));  
	      //nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));  

	     String msg =null; 
	      
		    try {  
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
		        Log.d("myapp", "works till here. 2"); 
		        // Execute HTTP Post Request  
		        HttpResponse response = httpclient.execute(httppost);
		        //Logging will cause error for some reason
		       // Log.d("myapp", "response " + EntityUtils.toString(response.getEntity()));
		        
		        try{
		        	
		        		msg =  EntityUtils.toString(response.getEntity());
		        }
		        catch (IOException e){
		        		Log.d("myapp", "ERROR " + e);
		        }
		    } catch (ClientProtocolException e) {  
		        // TODO Auto-generated catch block  
		    		Log.d("myapp2", "response " + e);
				msg =  "ClientProtocolException";
		    } catch (IOException e) {  
		        // TODO Auto-generated catch block  
		    		Log.d("myapp", "response " + e);
		    	
				msg =  "IOException";
		    }
		    
//		    if((msg =="Error in registration, repeated entries?") || (msg =="Missing Parameters")){
//		    			    	
//		    }
//		    else{
//		 	   Log.d("EXAMPLE", "Before GEOCOMM DataHelper :");  			
//		    		this.dh = new DataHelper(this);
//		    		   Log.d("EXAMPLE", "After GEOCOMM DataHelper :");  			
//		    		this.dh.insert(email,password);
//		    		   Log.d("EXAMPLE", "AFTER INSERT DataHelper :");  			
//		    }

		    return msg;
		    
		}
	   
	   //addingLandMark
	   public boolean addLandMark(String sessionKey, String landMarkName, 
			   String geoX, String geoY, String address, String ignore){
			HttpClient httpclient = new DefaultHttpClient();  
			HttpPost httppost = new HttpPost("http://137.132.145.205/Geoscribe/addLandMark.php");  
			  // Add your data  
			  List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
			  nameValuePairs.add(new BasicNameValuePair("sessionKey", sessionKey));
			  nameValuePairs.add(new BasicNameValuePair("landMarkName", landMarkName));
			  nameValuePairs.add(new BasicNameValuePair("geoX", geoX));  
			  nameValuePairs.add(new BasicNameValuePair("geoY", geoY));
			  nameValuePairs.add(new BasicNameValuePair("address", address));
			  nameValuePairs.add(new BasicNameValuePair("ignore", ignore));
			  
			  String msg;
			  
			  try {  
			        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
			        Log.d("myapp", "works till here. 2"); 
			        // Execute HTTP Post Request  
			        HttpResponse response = httpclient.execute(httppost);
			        //Logging will cause error for some reason
			       // Log.d("myapp", "response " + EntityUtils.toString(response.getEntity()));
			        
			        try{
			        	
			        		msg =  EntityUtils.toString(response.getEntity());
			        		
					    if(msg.compareTo("Successful")==0){
					    		Log.d("No error Succesfful",msg);
				    			return true;
					    }
					    else if(msg.compareTo("nearby")==0){
					    		//more info need to be return. Should computation be done on server or client side?
			        		Log.d("myapp", "ERROR " + msg);
					    		return false;
					    }
					    else if(msg.compareTo("4001")==0){
					    		//return relogin.
					    		
					    		//need to do something here
					 
					    		Log.d("myapp", "ERROR " + msg);
					    	
					    		return false;
					    }
					    else{
					    		//should no reach here.
					    	
					    		Log.d("This should not be reached!!!", msg);

					    		return false;
					    }
			        }
			        catch (IOException e){
			        		Log.d("myapp", "ERROR " + e);
			        		
			        		return false;
			        }
			    } catch (ClientProtocolException e) {  
			        // TODO Auto-generated catch block  
			    		Log.d("myapp2", "response " + e);
					msg =  "ClientProtocolException";
					
					return false;
			    } catch (IOException e) {  
			        // TODO Auto-generated catch block  
			    		Log.d("myapp", "response " + e);
			    	
					msg =  "IOException";
					
					return false;
			    }
			    
	   }
	   
	   //addingLandMark
	   public boolean postMessage(String sessionKey, String message){
			HttpClient httpclient = new DefaultHttpClient();  
			HttpPost httppost = new HttpPost("http://137.132.145.205/Geoscribe/postMessage.php");  
			  // Add your data  
			  List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
			  nameValuePairs.add(new BasicNameValuePair("sessionKey", sessionKey));
			  nameValuePairs.add(new BasicNameValuePair("boardId", "1"));
			  //nameValuePairs.add(new BasicNameValuePair("geoX", geoX));  
			  //nameValuePairs.add(new BasicNameValuePair("geoY", geoY));
			  nameValuePairs.add(new BasicNameValuePair("message", message));
			  //nameValuePairs.add(new BasicNameValuePair("ignore", ignore));
			  
			  String msg;
			  
			  try {  
			        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
			        Log.d("myapp", "works till here. 2"); 
			        // Execute HTTP Post Request  
			        HttpResponse response = httpclient.execute(httppost);
			        //Logging will cause error for some reason
			       // Log.d("myapp", "response " + EntityUtils.toString(response.getEntity()));
			        
			        try{
			        		msg =  EntityUtils.toString(response.getEntity());
			        		
					    if(msg.compareTo("Successful")==0){
					    		Log.d("No error Succesfful",msg);
				    			return true;
					    }
					    else if(msg.compareTo("nearby")==0){
					    		//more info need to be return. Should computation be done on server or client side?
			        		Log.d("myapp", "ERROR " + msg);
					    		return false;
					    }
					    else if(msg.compareTo("4001")==0){
					    		//return relogin.
					    		
					    		//need to do something here
					 
					    		Log.d("myapp", "ERROR " + msg);
					    	
					    		return false;
					    }
					    else{
					    		//should no reach here.
					    	
					    		Log.d("This should not be reached!!!", msg);

					    		return false;
					    }
			        }
			        catch (IOException e){
			        		Log.d("myapp", "ERROR " + e);
			        		
			        		return false;
			        }
			    } catch (ClientProtocolException e) {  
			        // TODO Auto-generated catch block  
			    		Log.d("myapp2", "response " + e);
					msg =  "ClientProtocolException";
					
					return false;
			    } catch (IOException e) {  
			        // TODO Auto-generated catch block  
			    		Log.d("myapp", "response " + e);
			    	
					msg =  "IOException";
					
					return false;
			    }
			    
	   }
	   
}
