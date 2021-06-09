package Models;

import Controllers.LoginController;
import Utilities.ConnectDB;
import Utilities.DateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Appointment {

    private Integer appointmentID;
    private Integer customerID;
    private Integer userID;
    private Integer contactID;
    private String contactName;
    private Customer customer;
    private String title;
    private String description;
    private String location;
    private String type;
    private String startTime;
    private String endTime;
    private String customerName;    

    public Appointment(int appointmentID, String title, String description, String location, String contact, String type, String startTime, String endTime, int customerID){
        setAppointmentID(appointmentID);
        setTitle(title);
        setDescription(description);
        setLocation(location);
        setType(type);
        setStart(startTime);
        setEnd(endTime);
        setCustomerID(customerID);
        setContactName(contact);
    }
   
    public Appointment(){
    }

    public static void deleteAppointmentsForCustomerID(Integer selectedCustomerID) {
        try {
            PreparedStatement statement = ConnectDB.makeConnection().prepareStatement("DELETE FROM appointments WHERE appointments.Customer_ID = " + selectedCustomerID);
            statement.executeUpdate();
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getAppointmentID(){
        return this.appointmentID;
    }
    
    public int getCustomerID(){
        return this.customerID;
    }



    public String getTitle(){
        return this.title;
    }
    
    public String getDescription(){
        return this.description;
    }
    
    public String getLocation(){
        return this.location;
    }

    public String getType(){
        return this.type;
    }
    
    public String getStart(){
        return this.startTime;
    }
    
    public String getEnd(){
        return this.endTime;
    }

    public String getCustomerName(){
        return this.customerName;
    }

    public String getContactName() {return this.contactName;}

    private void setAppointmentID(int appointmentID){
        this.appointmentID = appointmentID;
    }

    private void setCustomerID(int customerID){
        this.customerID = customerID;
    }

    private void setTitle(String title){
        this.title = title;
    }
    
    private void setDescription(String description){
        this.description = description;
    }
    
    private void setLocation(String location){
        this.location = location;
    }
    
    private void setType(String type){
        this.type = type;
    }

    private void setStart(String startTime){
        this.startTime = startTime;
    }
    
    private void setEnd(String endTime){
        this.endTime = endTime;
    }

    public void setCustomerName(String customerName){
       this.customerName = customerName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public static ObservableList<Appointment> getAppointmentList() {
        PreparedStatement statement;
        ObservableList<Appointment> appointmentsList = FXCollections.observableArrayList();

        try {
            System.out.println("trying to query appointments");
            statement = ConnectDB.makeConnection().prepareStatement(
                    "SELECT appointments.Appointment_ID, appointments.Title, appointments.Description, appointments.Location, appointments.Contact_ID, "
                            + "appointments.Type, appointments.Start, appointments.End, appointments.Customer_ID, contacts.Contact_Name "
                            + "FROM appointments, contacts "
                            + "WHERE appointments.Contact_ID = contacts.Contact_ID "
                            + "ORDER BY 'appointments.Start'");

            ResultSet result = statement.executeQuery();

            while (result.next()) {

                var appointmentID = result.getInt("Appointment_ID");
                var title = result.getString("Title");
                var description = result.getString("Description");
                var location = result.getString("Location");
                var contact = result.getString("Contact_Name");
                var type = result.getString("Type");
                var startUTC = result.getString("Start").substring(0, 19);
                var endUTC = result.getString("End").substring(0, 19);
                var customerID = result.getInt("Customer_ID");
                appointmentsList.add(new Appointment(appointmentID, title, description, location, contact, type, startUTC, endUTC, customerID));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return appointmentsList;
    }

    public static void updateAppointment(int id, String type, String title, String desc, String loc, int contactID, int customerID, LocalDateTime start, LocalDateTime end) {
        PreparedStatement statement;

        try {
            statement = ConnectDB.makeConnection().prepareStatement(
                    "UPDATE appointments" +
                        " SET Type = '" + type +"'" +
                        ", Title = '" + title +"'" +
                        ", Description = '" + desc +"'" +
                        ", Location = '" + loc +"'" +
                        ", Customer_ID = '" + customerID +"'" +
                        ", Contact_ID = '" + contactID +"'" +
                        ", Start = '" + start +"'" +
                        ", End = '" + end +"'" +
                        ", Last_Update = '" + LocalDateTime.now() +"'" +
                        ", Last_Updated_By = '" + LoginController.userID +"'" +
                        " WHERE Appointment_ID = " + id
            );
            statement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createAppointment(String type, String title, String desc, String loc, int contactID, int customerID, LocalDateTime start, LocalDateTime end) {
        try {
            PreparedStatement statement = ConnectDB.makeConnection()
                    .prepareStatement("INSERT INTO appointments (Title, Description, Location, Type, Start, End, Create_Date, Created_By, Customer_ID, User_ID, Contact_ID) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, title);
            statement.setString(2, desc);
            statement.setString(3, loc);
            statement.setString(4, type);
            statement.setTimestamp(5, DateTime.convertToDatabaseColumn(start));
            statement.setTimestamp(6, DateTime.convertToDatabaseColumn(end));
            statement.setTimestamp(7, DateTime.convertToDatabaseColumn(end));
            statement.setInt(8, LoginController.userID);
            statement.setInt(9, customerID);
            statement.setInt(10, LoginController.userID);
            statement.setInt(11, contactID);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Appointment(Integer appointmentID) {
        ResultSet result;
        try (var statement = ConnectDB.makeConnection().prepareStatement(
                "SELECT appointments.Appointment_ID, appointments.Title, appointments.Description, appointments.Location, appointments.Contact_ID, "
                        + "appointments.Type, appointments.Start, appointments.End, appointments.Customer_ID, contacts.Contact_Name, contacts.Contact_ID "
                        + "FROM appointments, contacts "
                        + "WHERE appointments.Contact_ID = contacts.Contact_ID "
                        + "AND appointments.Appointment_ID = " + appointmentID
                        + " ORDER BY 'appointments.Start'")) {

            result = statement.executeQuery();
            while (result.next()) {
                this.appointmentID = result.getInt("Appointment_ID");
                this.title = result.getString("Title");
                this.description = result.getString("Description");
                this.location = result.getString("Location");
                this.contactName = result.getString("Contact_Name");
                this.contactID = result.getInt("Contact_ID");
                this.type = result.getString("Type");
                this.startTime = result.getString("Start").substring(0, 19);
                this.endTime = result.getString("End").substring(0, 19);
                this.customerID = result.getInt("Customer_ID");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDate() {
        return this.getStart().split(" ")[0];
    }

    public String getStartTime() {
        return LocalTime.parse(this.getStart().split(" ")[1]).format(DateTimeFormatter.ofPattern("H:mm a"));
    }

    public String getEndTime() {
        return LocalTime.parse(this.getEnd().split(" ")[1]).format(DateTimeFormatter.ofPattern("H:mm a"));
    }
}