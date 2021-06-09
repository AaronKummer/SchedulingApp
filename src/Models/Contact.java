package Models;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Utilities.ConnectDB;

public class Contact {
    private int contactID;
    private String contactName;
    private String contactEmail;

    public String getName() {
        return contactName;
    }
    public String getEmail() {
        return contactEmail;
    }
    public int getId() {
        return contactID;
    }


    public Contact(String name, String email, int id) {
        this.contactEmail = email;
        this.contactName = name;
        this.contactID = id;
    }

    public static List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();
        ResultSet result;
        try (var statement = ConnectDB.makeConnection().prepareStatement(
                "SELECT * from contacts")) {
            result = statement.executeQuery();
            while (result.next()) {
                contacts.add(new Contact(result.getString("Contact_Name"), result.getString("Email"), result.getInt("Contact_ID")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contacts;
    }




}
