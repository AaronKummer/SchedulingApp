package Models;

import Utilities.ConnectDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * poco object for user
 */
public class User {

    /**
     * object properties
     */
    private static int userID; //auto incremented in database
    private static String username;
    private static String password;
    private String userName;
    private int nonStaticUserID;
    /**
     * default constructor
     */
    public User() {
        userID = 0;
        username = null;
        password = null;
    }

    /**
     * constructor
     */
    public User(int userID, String username, String password) {
        this.userID = userID;
        this.username = username;
        this.password = password;
    }

    /**
     * constructor
     */
    public User(int userID, String username)  {
        this.nonStaticUserID = userID;
        this.userName = username;
    }

    /**
     * gets user id
     */
    public static int getUserID() {
        return userID;
    }

    /**
     * gets user name
     */
    public static String  getUsername() {
        return username;
    }

    /**
     * gets password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * sets user id
     */
    public static void setUserID(int userID) {
        User.userID = userID;
    }

    /**
     * sets user name
     */
    public static void setUsername(String username) {
        User.username = username;
    }

    /**
     * sets password
     */
    public static void setPassword(String password) {
        User.password = password;
    }

    /**
     * gets all users
     * @return
     */
    public static ObservableList<User> getAllUsers() {
        PreparedStatement statement;
        ObservableList<User> users = FXCollections.observableArrayList();

        try {
            statement = ConnectDB.makeConnection().prepareStatement("SELECT * from users");

            ResultSet result = statement.executeQuery();

            while (result.next()) {

                var userID = result.getInt("User_ID");
                var userName = result.getString("User_Name");
                users.add(new User(userID, userName));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getNonStaticUserID() {
        return nonStaticUserID;
    }

    public void setNonStaticUserID(int nonStaticUserID) {
        this.nonStaticUserID = nonStaticUserID;
    }
}
