package com.example.Geoscribe.comms;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webservice.WebService;
import com.webservice.objects.AuthenticateArgs;
import com.webservice.objects.CheckInArgs;
import com.webservice.objects.CreateBoardArgs;
import com.webservice.objects.FetchAllBoardsResult;
import com.webservice.objects.FetchBoardsArgs;
import com.webservice.objects.FetchBoardsResult;
import com.webservice.objects.FetchMsgArgs;
import com.webservice.objects.FetchMsgResult;
import com.webservice.objects.GetBoardNearByArgs;
import com.webservice.objects.GetBoardNearByResult;
import com.webservice.objects.GetUserNearByArgs;
import com.webservice.objects.GetUserNearByResult;
import com.webservice.objects.PostMsgArgs;
import com.webservice.objects.RegisterArgs;
import com.webservice.objects.RegisterDeviceArgs;
import com.webservice.objects.RegisterDeviceResult;
import com.webservice.objects.deviceConnectedArgs;
import com.webservice.objects.deviceLoginedArgs;
import com.webservice.objects.isDeviceLoginedResult;

public class GeoscribeComms2 {
	
	public final static String TAG = "GeoscribeComms";
	//public final static String webServiceURL = "http://137.132.145.205/geoscribeServices/";
	//public final static String webServiceURL ="http://10.0.2.2/geoscribeService/";
	public final static String webServiceURL = "http://www.geoscribe.sg/GeoscribeService/";

	public boolean insertDeviceLogined(String hardwareId, double geoX, double geoY, int cluster){
		deviceLoginedArgs arg = new deviceLoginedArgs();
		arg.hardwareId = hardwareId;
		arg.geoX = geoX;
		arg.geoY = geoY;
		arg.cluster = cluster;
		
		Gson gson = new Gson();
        
        String argJSON = gson.toJson(arg);
        
		
		WebService webService = new WebService(webServiceURL);

		//Pass the parameters if needed , if not then pass dummy one as follows
        Map<String, String> params = new HashMap<String, String>();
        params.put("arg", argJSON);
  
        //Get JSON response from server the "" are where the method name would normally go if needed example
        // webService.webGet("getMoreAllerts", params);
        String response = webService.webGet("insertDeviceLogined4.php", params);
        
        Log.e("insertDeviceLogined",response);
        
        try {
        boolean result = gson.fromJson(response, boolean.class);
			return result;
        } catch (Exception e) {
        		Log.e(TAG,"insertDeviceLogined error");
        		return false;
        }		
	}
	
	public isDeviceLoginedResult isDeviceLogined(String hardwareId){
		String arg = hardwareId;
		
		Gson gson = new Gson();
        
        String argJSON = gson.toJson(arg);
        
		
		WebService webService = new WebService(webServiceURL);

		//Pass the parameters if needed , if not then pass dummy one as follows
        Map<String, String> params = new HashMap<String, String>();
        params.put("arg", argJSON);
  
        //Get JSON response from server the "" are where the method name would normally go if needed example
        // webService.webGet("getMoreAllerts", params);
        String response = webService.webGet("isDeviceLogined5.php", params);
        
        Log.e("isDeviceLogined2",response);
        
        try {
        		isDeviceLoginedResult result = gson.fromJson(response, isDeviceLoginedResult.class);
        		
        		Log.e("isDeviceLogined2",result.hardwareId);
        		
			return result;
        } catch (Exception e) {
        		Log.e(TAG,"isDeviceLogined2 error");
        		return null;
        }
	}	
	
	public Boolean insertDeviceEntry(String serverId,String deviceId){
		deviceConnectedArgs arg = new deviceConnectedArgs();
		arg.clientId = deviceId;
		arg.serverId = serverId;
		
		
		
		Gson gson = new Gson();
        
        String argJSON = gson.toJson(arg);
        
		
		WebService webService = new WebService(webServiceURL);

		//Pass the parameters if needed , if not then pass dummy one as follows
        Map<String, String> params = new HashMap<String, String>();
        params.put("arg", argJSON);
  
        //Get JSON response from server the "" are where the method name would normally go if needed example
        // webService.webGet("getMoreAllerts", params);
        String response = webService.webGet("insertDeviceEntry.php", params);
        
        Log.e("insertDeviceEntry",response);
        
        try {
        boolean result = gson.fromJson(response, boolean.class);
			return result;
        } catch (Exception e) {
        		Log.e(TAG,"insertDeviceEntry error");
        		return false;
        }
	}
	
	public Boolean removeDeviceLogined(String hardwareId){		
		Gson gson = new Gson();
        
        String argJSON = gson.toJson(hardwareId);
        
		
		WebService webService = new WebService(webServiceURL);

		//Pass the parameters if needed , if not then pass dummy one as follows
        Map<String, String> params = new HashMap<String, String>();
        params.put("arg", argJSON);
  
        //Get JSON response from server the "" are where the method name would normally go if needed example
        // webService.webGet("getMoreAllerts", params);
        String response = webService.webGet("removeDeviceLogined.php", params);
        
        Log.e("removeDeviceLogined.php",response);
        
        try {
        boolean result = gson.fromJson(response, boolean.class);
			return result;
        } catch (Exception e) {
        		Log.e(TAG,"removeDeviceLogined.php error");
        		return false;
        }
	}
	
	
	public Boolean removeDeviceEntry(String serverId,String deviceId){
		deviceConnectedArgs arg = new deviceConnectedArgs();
		arg.clientId = deviceId;
		arg.serverId = serverId;
		
		
		
		Gson gson = new Gson();
        
        String argJSON = gson.toJson(arg);
        
		
		WebService webService = new WebService(webServiceURL);

		//Pass the parameters if needed , if not then pass dummy one as follows
        Map<String, String> params = new HashMap<String, String>();
        params.put("arg", argJSON);
  
        //Get JSON response from server the "" are where the method name would normally go if needed example
        // webService.webGet("getMoreAllerts", params);
        String response = webService.webGet("removeDeviceEntry.php", params);
        
        Log.e("removeDeviceEntry",response);
        
        try {
        boolean result = gson.fromJson(response, boolean.class);
			return result;
        } catch (Exception e) {
        		Log.e(TAG,"removeDeviceEntry error");
        		return false;
        }
	}
	
	//report to server an connection has been made
	public Boolean deviceConnected(String serverId,String deviceId){
		deviceConnectedArgs arg = new deviceConnectedArgs();
		arg.clientId = deviceId;
		arg.serverId = serverId;
		
		Gson gson = new Gson();
        
        String argJSON = gson.toJson(arg);
        
		
		WebService webService = new WebService(webServiceURL);

		//Pass the parameters if needed , if not then pass dummy one as follows
        Map<String, String> params = new HashMap<String, String>();
        params.put("arg", argJSON);
  
        //Get JSON response from server the "" are where the method name would normally go if needed example
        // webService.webGet("getMoreAllerts", params);
        String response = webService.webGet("deviceConnected.php", params);
        
        Log.e("deviceConnected.php",response);
        
        try {
        boolean result = gson.fromJson(response, boolean.class);
			return result;
        } catch (Exception e) {
        		Log.e(TAG,"deviceConnected.php error");
        		return false;
        }
	}

	public Boolean deviceEntryExist(String serverId,String deviceId){
		deviceConnectedArgs arg = new deviceConnectedArgs();
		arg.clientId = deviceId;
		arg.serverId = serverId;
		
		Gson gson = new Gson();
        
        String argJSON = gson.toJson(arg);
        
		
		WebService webService = new WebService(webServiceURL);

		//Pass the parameters if needed , if not then pass dummy one as follows
        Map<String, String> params = new HashMap<String, String>();
        params.put("arg", argJSON);
  
        //Get JSON response from server the "" are where the method name would normally go if needed example
        // webService.webGet("getMoreAllerts", params);
        String response = webService.webGet("deviceEntryExist.php", params);
        
        Log.e("deviceEntryExist",response);
        
        try {
        boolean result = gson.fromJson(response, boolean.class);
        		
			return result;
        } catch (Exception e) {
        		Log.e(TAG,"deviceEntryExist error");
        		return false;
        }
	}
	
	//report to server an connection Lost
	public String[] deviceConnectionLost(String clientId){
	
		Gson gson = new Gson();
        
        String argJSON = gson.toJson(clientId);
        
		
		WebService webService = new WebService(webServiceURL);

		//Pass the parameters if needed , if not then pass dummy one as follows
        Map<String, String> params = new HashMap<String, String>();
        params.put("arg", argJSON);
  
        //Get JSON response from server the "" are where the method name would normally go if needed example
        // webService.webGet("getMoreAllerts", params);
        String response = webService.webGet("deviceConnectionLost2.php", params);
        
        Log.e("deviceConnectionLost2.php",response);
        
        try {
        	//Type listType = new TypeToken<ArrayList<String>>(){}.getType();
        	//ArrayList<String> result = gson.fromJson(response, listType);
        	
        	String result[] = gson.fromJson(response, String[].class); 
        		
			return result;
        } catch (Exception e) {
        		Log.e(TAG,"deviceConnectionLost2 error");
        		return null;
        }
	}
	
	public RegisterDeviceResult[] registerDevice(String hardwareId,double geoX,double geoY){
		RegisterDeviceArgs arg = new RegisterDeviceArgs();
		arg.hardwareId = hardwareId;
		arg.geoX = geoX;
		arg.geoY = geoY;
		
		Gson gson = new Gson();
        
        String argJSON = gson.toJson(arg);
        
		
		WebService webService = new WebService(webServiceURL);

		//Pass the parameters if needed , if not then pass dummy one as follows
        Map<String, String> params = new HashMap<String, String>();
        params.put("arg", argJSON);
  
        //Get JSON response from server the "" are where the method name would normally go if needed example
        // webService.webGet("getMoreAllerts", params);
        String response = webService.webGet("registerDevice.php", params);
        
        Log.e("registerDevice.php",response);
        
        try {
        RegisterDeviceResult[] result = gson.fromJson(response, RegisterDeviceResult[].class);
        		
			return result;
        } catch (Exception e) {
        		Log.e(TAG,"registerDevice.php error");
        		return null;
        }
	}	
	
	//register(String username, String password, String email, string gender, string age, string dateOfBirth, String hometown);
	//-Return [success or error code]
//	function register($arg)
//	    $username = $arg["username"];
//	    $password = $arg["password"];
//	    $email = $arg["email"];
//	    $gender = $arg["gender"];
//	    $age = $arg["age"];
//	    $dateOfBirth = $arg["dateOfBirth"];
//	    $hometown = $arg["hometown"];
	public Boolean register(String username, String password, String email, String gender, String age, String dateOfBirth, String hometown){
		RegisterArgs args = new RegisterArgs();
		args.username = username;
		args.password = password;
		args.email = email;
		args.gender = gender;
		args.age = age;
		args.dateOfBirth = dateOfBirth;
		args.hometown = hometown;
		
        Gson gson = new Gson();
               
        String argJSON = gson.toJson(args);
        
		
		WebService webService = new WebService(webServiceURL);

		//Pass the parameters if needed , if not then pass dummy one as follows
        Map<String, String> params = new HashMap<String, String>();
        params.put("arg", argJSON);
  
        //Get JSON response from server the "" are where the method name would normally go if needed example
        // webService.webGet("getMoreAllerts", params);
        String response = webService.webGet("register.php", params);
        try {
        boolean result = gson.fromJson(response, boolean.class);
		return result;
        } catch (Exception e) {
        	Log.e("GeoscribeComms2", e.getMessage());
        	return false;
        }
	}
		
	
//	//register2(String username, String password, String email, string gender, string age, string dateOfBirth, String hometown);
//	//-Return [false or SessionKey on Success]
//	function register2($arg){
//	    $username = $arg["username"];
//	    $password = $arg["password"];
//	    $email = $arg["email"];
//	    $gender = $arg["gender"];
//	    $age = $arg["age"];
//	    $dateOfBirth = $arg["dateOfBirth"];
//	    $hometown = $arg["hometown"];
	public String register2(String username, String password, String email, String gender, String age, String dateOfBirth, String hometown){
		RegisterArgs args = new RegisterArgs();
		args.username = username;
		args.password = password;
		args.email = email;
		args.gender = gender;
		args.age = age;
		args.dateOfBirth = dateOfBirth;
		args.hometown = hometown;
		
        Gson gson = new Gson();
               
        String argJSON = gson.toJson(args);
        
		
		WebService webService = new WebService(webServiceURL);

		//Pass the parameters if needed , if not then pass dummy one as follows
        Map<String, String> params = new HashMap<String, String>();
        params.put("arg", argJSON);
  
        //Get JSON response from server the "" are where the method name would normally go if needed example
        // webService.webGet("getMoreAllerts", params);
        String response = webService.webGet("register2.php", params);
        try {
        String result = gson.fromJson(response, String.class); 	
		return result;
        } catch (Exception e) {
        	Log.e("GeoscribeComms2", e.getMessage());
        	return "";
        }
	}
	
	
	
//	//Authenticate(String email, String password)
//	//We login in with email instead of username
//	//Return [false or SessionKey on Success]
//	function Authenticate($arg){
//	    $password = $arg["password"];
//	    $email = $arg["email"];
	public String Authenticate(String password, String email){
		AuthenticateArgs args = new AuthenticateArgs();
		args.email = email;
		args.password = password;
		
        Gson gson = new Gson();
        
        String argJSON = gson.toJson(args);
        
		
		WebService webService = new WebService(webServiceURL);

		//Pass the parameters if needed , if not then pass dummy one as follows
        Map<String, String> params = new HashMap<String, String>();
        params.put("arg", argJSON);
  
        //Get JSON response from server the "" are where the method name would normally go if needed example
        // webService.webGet("getMoreAllerts", params);
        String response = webService.webGet("Authenticate.php", params);
        try {
        String result = gson.fromJson(response, String.class); 	
		return result;
        } catch (Exception e) {
        	Log.e("GeoscribeComms2", e.getMessage());
        	return "";
        }
	}
	
	
//
//	//getBoardNearby(String xcoord, String ycoord)
//	//-return [array of boards with their coordinates, say within 100m?]
//	//Ð the logic will be implemented in the server, but will include the same function in the client side as well for the framework.
//	//geoX - longtitude, geoY - latitude, radius is in metres
//	//-return array of boards with geoX,geoY, landMark id, landMark name, address and exact distance apart
//	function getBoardNearBy($arg){
//	    $geoX = $arg["geoX"];
//	    $geoY = $arg["geoY"];
//	    $radius = $arg["radius"];

	public GetBoardNearByResult[] getBoardNearBy(float geoX, float geoY,float radius){
		GetBoardNearByArgs args = new GetBoardNearByArgs();
		args.geoX = geoX;
		args.geoY = geoY;
		args.radius = radius;
		
		GetBoardNearByResult[] result2 = null;
		
        Gson gson = new Gson();
        
        String argJSON = gson.toJson(args);
        
		
		WebService webService = new WebService(webServiceURL);

		//Pass the parameters if needed , if not then pass dummy one as follows
        Map<String, String> params = new HashMap<String, String>();
        params.put("arg", argJSON);
  
        //Get JSON response from server the "" are where the method name would normally go if needed example
        // webService.webGet("getMoreAllerts", params);
        String response = webService.webGet("getBoardNearBy.php", params);
        try {
        GetBoardNearByResult[] result = gson.fromJson(response, GetBoardNearByResult[].class);    	
		return result;			
        } catch (Exception e) {
        	Log.e("GeoscribeComms2", e.getMessage());
        	return result2;
        }
	}

	
//	//getUserNearby(String xcoord, String ycoord)
//	//-return [array of app users with their coordinates, say within 100m?]
//	//Ð the logic will be implemented in the server, but will include the same function in the client side as well for the framework, similar to the function above.
//	function getUserNearBy($arg){
//	    $geoX = $arg["geoX"];
//	    $geoY = $arg["geoY"];
//	    $radius = $arg["radius"];
//
	public GetUserNearByResult[] getUserNearBy(float geoX, float geoY,float radius){
		GetUserNearByArgs args = new GetUserNearByArgs();
		args.geoX = geoX;
		args.geoY = geoY;
		args.radius = radius;
		
		GetUserNearByResult[] result2 = null;
		
        Gson gson = new Gson();
        
        String argJSON = gson.toJson(args);
        
		
		WebService webService = new WebService(webServiceURL);

		//Pass the parameters if needed , if not then pass dummy one as follows
        Map<String, String> params = new HashMap<String, String>();
        params.put("arg", argJSON);
  
        //Get JSON response from server the "" are where the method name would normally go if needed example
        // webService.webGet("getMoreAllerts", params);
        String response = webService.webGet("getUserNearBy.php", params);
        try {
        GetUserNearByResult[] result = gson.fromJson(response, GetUserNearByResult[].class);       	
		return result;			
        } catch (Exception e) {
        	Log.e("GeoscribeComms2", e.getMessage());
        	return result2;
        }
	}
	
	
//	//fetchAllBoards()
//	//-return [array of coordinates], FetchAllBoardsResult objs
//	function fetchAllBoards(){

	public FetchAllBoardsResult[] fetchAllBoards(){
        Gson gson = new Gson();
        
        FetchAllBoardsResult[] result2 = null;
		
		WebService webService = new WebService(webServiceURL);

		//Pass the parameters if needed , if not then pass dummy one as follows
        Map<String, String> params = new HashMap<String, String>();
        params.put("arg", "1");
  
        //Get JSON response from server the "" are where the method name would normally go if needed example
        // webService.webGet("getMoreAllerts", params);
        String response = webService.webGet("fetchAllBoards.php", params);
        try {
        FetchAllBoardsResult[] result = gson.fromJson(response, FetchAllBoardsResult[].class);     	
		return result;	
        } catch (Exception e) {
        	Log.e("GeoscribeComms2", e.getMessage());
        	return result2;
        }
		
	}
	
//	//fetchBoard(String boardID)
//	//-return [name of board, date created, name of creator], FetchBoardsResult Obj
//	//Only have last update date
//	function fetchBoards($arg){
//	    $boardId = $arg["boardId"];
	public FetchBoardsResult fetchBoards(int boardId){
		FetchBoardsArgs args = new FetchBoardsArgs();
		args.boardId = boardId;
		
		FetchBoardsResult result2 = null;
		
        Gson gson = new Gson();
        
        String argJSON = gson.toJson(args);
        
		
		WebService webService = new WebService(webServiceURL);

		//Pass the parameters if needed , if not then pass dummy one as follows
        Map<String, String> params = new HashMap<String, String>();
        params.put("arg", argJSON);
  
        //Get JSON response from server the "" are where the method name would normally go if needed example
        // webService.webGet("getMoreAllerts", params);
        String response = webService.webGet("fetchBoards.php", params);
        try {
        FetchBoardsResult result = gson.fromJson(response, FetchBoardsResult.class); 	
		return result;	
        } catch (Exception e) {
        	Log.e("GeoscribeComms2", e.getMessage());
        	return result2;
        }
	}
	
	
//
//	//fetchMsg(String boardID)
//	//-return [array of messages, name of poster, time] Ð the result maybe say within a single day?
//	function fetchMsg($arg){
//	    $boardId = $arg["boardId"];
	public FetchMsgResult[] fetchMsg(int boardId){
		FetchMsgArgs args = new FetchMsgArgs();
		args.boardId = boardId;
        Gson gson = new Gson();
        
        FetchMsgResult[] result2 = null;
        
        String argJSON = gson.toJson(args);
        
		
		WebService webService = new WebService(webServiceURL);

		//Pass the parameters if needed , if not then pass dummy one as follows
        Map<String, String> params = new HashMap<String, String>();
        params.put("arg", argJSON);
  
        //Get JSON response from server the "" are where the method name would normally go if needed example
        // webService.webGet("getMoreAllerts", params);
        String response = webService.webGet("fetchMsg.php", params);
        try {
        	FetchMsgResult[] result = gson.fromJson(response, FetchMsgResult[].class);
   		return result;	
        } catch (Exception e) {
        	Log.e("GeoscribeComms2", e.getMessage());
        	return result2;
        }
	}
	
//
//	//postMsg(String name of poster, String message, String boardID)
//	//-return [success or error code]
//	//Session Key is all i need for userinfo
//	function postMsg($arg){
//	        $msg = $arg["msg"];
//	        $boardId = $arg["boardId"];
//	        $sessionKey = $arg["sessionKey"];
	public boolean postMsg(String msg, int boardId, String sessionKey){
		PostMsgArgs args = new PostMsgArgs();
		args.msg = msg;
		args.boardId = boardId;
		args.sessionKey = sessionKey;
		
        Gson gson = new Gson();
        
        String argJSON = gson.toJson(args);
        
		
		WebService webService = new WebService(webServiceURL);

		//Pass the parameters if needed , if not then pass dummy one as follows
        Map<String, String> params = new HashMap<String, String>();
        params.put("arg", argJSON);
  
        //Get JSON response from server the "" are where the method name would normally go if needed example
        // webService.webGet("getMoreAllerts", params);
        String response = webService.webGet("postMsg.php", params);
        try {
        boolean result = gson.fromJson(response, boolean.class);
        return result;	
        } catch (Exception e) {
        	Log.e("GeoscribeComms2", e.getMessage());
        	return false;
        } 
	}
	
//	//createBoard(String nameOfPoster, string nameOfBoard, String addressOfBoard, String xcoordinate, String ycoordinate)
//	//-return [success or error code]
//	//Session Key too
//	function createBoard($arg){
//	        $boardName = $arg["boardName"];
//	        $address = $arg["address"];
//	        $geoX = $arg["geoX"];
//	        $geoY = $arg["geoY"];
//	        $sessionKey = $arg["sessionKey"];
	public boolean createBoard(String boardName, String address, float geoX, float geoY, String sessionKey){
		CreateBoardArgs args = new CreateBoardArgs();
		args.boardName = boardName;
		args.address = address;
		args.geoX = geoX;
		args.geoY = geoY;
		args.sessionKey = sessionKey;
		
        Gson gson = new Gson();
        
        String argJSON = gson.toJson(args);
        
		
		WebService webService = new WebService(webServiceURL);

		//Pass the parameters if needed , if not then pass dummy one as follows
        Map<String, String> params = new HashMap<String, String>();
        params.put("arg", argJSON);
  
        //Get JSON response from server the "" are where the method name would normally go if needed example
        // webService.webGet("getMoreAllerts", params);
        String response = webService.webGet("createBoard.php", params);
        try {
        	boolean result = gson.fromJson(response, boolean.class);
        	return result;
        } catch (Exception e) {
        	Log.e("GeoscribeComms2", "createBoard() error.");
        	return false;
        }
		//return result;			
	}
//
//	function checkIn($arg){
//	    $hardwareId = $arg["hardwareId"];
//	    $geoX = $arg["geoX"];
//	    $geoY = $arg["geoY"];

	public boolean checkIn(String hardwareId, float geoX, float geoY){
		CheckInArgs args = new CheckInArgs();
		args.geoX = geoX;
		args.geoY = geoY;
		args.hardwareId = hardwareId;
		
        Gson gson = new Gson();
        
        String argJSON = gson.toJson(args);
        
		
		WebService webService = new WebService(webServiceURL);
		Log.d(getClass().getSimpleName(), "After webservice."+ " GeoX:" + geoX + " GeoY:" + geoY + " hardwareID:" + hardwareId);
		//Pass the parameters if needed , if not then pass dummy one as follows
        Map<String, String> params = new HashMap<String, String>();
        params.put("arg", argJSON);
        Log.d(getClass().getSimpleName(), "After paramput.");
        //Get JSON response from server the "" are where the method name would normally go if needed example
        // webService.webGet("getMoreAllerts", params);
        String response = webService.webGet("checkIn.php", params);
        
        Log.e("GEOSCRIBECOMM2",response);
        
        Log.d(getClass().getSimpleName(), "After using webget.");
        try {
        	boolean result = gson.fromJson(response, boolean.class);
        	Log.d(getClass().getSimpleName(), "Before return from checkin.");	
        	return result;
        } catch (Exception e) {
        	Log.e("GeoscribeComms2", e.getMessage());
        	return false;
        }
        
		//return result;			
	}
	
}
