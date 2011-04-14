package indexServerClient;

public class User {

	private String userName;
	private String email;
    private String password;

    
    //constructor
    public User(){};
    
    public User(String userName, String email, String password){
        this.userName=userName;
        this.email=email;
        this.password=password;
    }
    
    //setter
    public void setUserName(String userName){
    	this.userName=userName;
    }
    
    public void setEmail(String email){
    	this.email=email;
    }
    
    public void setPassword(String password){
    	this.password=password;
    }
    
    //getter
    public String getUserName(){
        return userName;
    }

    public String getEmail(){
        return email;
    }

     public String getPassword(){
        return password;
    }
	
}
