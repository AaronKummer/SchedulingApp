package Models;

import java.util.ArrayList;
import java.util.List;

public class User {

    private static int userID; //auto incremented in database
    private static String username;
    private static String password;
    

    public User() {
        userID = 0;
        username = null;
        password = null;
    }

    public User(int userID, String username, String password) {
        this.userID = userID;
        this.username = username;
        this.password = password;
    }

    public static int getUserID() {
        return userID;
    }

    public static String  getUsername() {
        return username;
    }
     
    public String getPassword() {
        return this.password;
    }

    //setters
    public static void setUserID(int userID) {
        User.userID = userID;
    }

    public static void setUsername(String username) {
        User.username = username;
    }

    public static void setPassword(String password) {
        User.password = password;
    }



}
