package Models;

import Controllers.LoginController;
import Utilities.ConnectDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static Utilities.DateTime.commonDateFormat;

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

    public Appointment(int appointmentID, String title, String description, String location, String contact, String type, String startTime, String endTime, int customerID, int userID){
        setAppointmentID(appointmentID);
        setTitle(title);
        setDescription(description);
        setLocation(location);
        setType(type);
        setStart(startTime);
        setEnd(endTime);
        setCustomerID(customerID);
        setContactName(contact);
        setUserID(userID);
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
    
    public String getStart(){ return this.startTime; }
    
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

    /**
     *
     * @param customerID
     */
    private void setCustomerID(int customerID){
        this.customerID = customerID;
    }

    /**
     *
     * @param title
     */
    private void setTitle(String title){
        this.title = title;
    }

    /**
     *
     * @param description
     */
    private void setDescription(String description){
        this.description = description;
    }

    /**
     *
     * @param location
     */
    private void setLocation(String location){
        this.location = location;
    }

    /**
     *
     * @param type
     */
    private void setType(String type){
        this.type = type;
    }

    /**
     *
     * @param startTime
     */
    private void setStart(String startTime){
        this.startTime = startTime;
    }

    /**
     *
     * @param endTime
     */
    private void setEnd(String endTime){
        this.endTime = endTime;
    }

    /**
     *
     * @param customerName
     */
    public void setCustomerName(String customerName){
       this.customerName = customerName;
    }

    /**
     *
     * @param contactName
     */
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    /**
     *
     * @param userID
     * @return
     */
    public static List<Appointment> getAppointmentsForUser(int userID) {
        List<Appointment> appointments = new ArrayList<>();
        PreparedStatement statement;
        try {
            System.out.println("trying to query appointments");
            statement = ConnectDB.makeConnection().prepareStatement(
                    "SELECT appointments.Appointment_ID, appointments.Title, "
                            + "appointments.Start, User_ID "
                            + "FROM appointments "
                            + "WHERE appointments.User_ID = " + userID
                            + " ORDER BY 'appointments.Start'");

            ResultSet result = statement.executeQuery();

            while (result.next()) {

                var appointmentID = result.getInt("Appointment_ID");
                var title = result.getString("Title");
                var startUTC = result.getString("Start").substring(0, 19);
                appointments.add(new Appointment(appointmentID, title, "", "", "", "", startUTC, "", 0, userID));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return appointments;
    }

    /**
     *
     * @return
     */
    public static ObservableList<Appointment> getAppointmentList() {
        PreparedStatement statement;
        ObservableList<Appointment> appointmentsList = FXCollections.observableArrayList();

        try {
            System.out.println("trying to query appointments");
            statement = ConnectDB.makeConnection().prepareStatement(
                    "SELECT appointments.Appointment_ID, appointments.Title, appointments.Description, appointments.Location, appointments.Contact_ID, "
                            + "appointments.Type, appointments.Start, appointments.End, appointments.User_ID, appointments.Customer_ID, contacts.Contact_Name "
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
                var start = result.getTimestamp("Start");
                var end = result.getTimestamp("End");
                var customerID = result.getInt("Customer_ID");
                var userID = result.getInt("User_ID");

                ZoneId newzid = ZoneId.systemDefault();

                ZonedDateTime newzdtStart = start.toLocalDateTime().atZone(ZoneId.of("UTC"));
                var newLocalStart = newzdtStart.withZoneSameInstant(newzid).toLocalDateTime()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a"));
                ZonedDateTime newzdtEnd = end.toLocalDateTime().atZone(ZoneId.of("UTC"));
                var newLocalEnd = newzdtEnd.withZoneSameInstant(newzid).toLocalDateTime()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a"));

                appointmentsList.add(new Appointment(appointmentID, title, description, location, contact, type, newLocalStart, newLocalEnd, customerID, userID));
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

    /**
     *
     * @param id
     * @param type
     * @param title
     * @param desc
     * @param loc
     * @param contactID
     * @param customerID
     * @param start
     * @param end
     * @param userID
     */
    public static void updateAppointment(int id, String type, String title, String desc, String loc, int contactID, int customerID, LocalDateTime start, LocalDateTime end, int userID) {
        PreparedStatement statement;

        ZoneId zid = ZoneId.systemDefault();
        ZonedDateTime zdtStart = start.atZone(zid);
        ZonedDateTime utcStart = zdtStart.withZoneSameInstant(ZoneId.of("UTC"));
        var ldtStart = utcStart.toLocalDateTime();
        Timestamp startsqlts = Timestamp.valueOf(ldtStart); //this value can be inserted into database

        ZonedDateTime zdtEnd = end.atZone(zid);
        ZonedDateTime utcEnd = zdtEnd.withZoneSameInstant(ZoneId.of("UTC"));
        var ldtEnd = utcEnd.toLocalDateTime();
        Timestamp endsqlts = Timestamp.valueOf(ldtEnd); //this value can be inserted into database

        try {
            statement = ConnectDB.makeConnection().prepareStatement(
                    "UPDATE appointments" +
                        " SET Type = '" + type +"'" +
                        ", Title = '" + title +"'" +
                        ", Description = '" + desc +"'" +
                        ", Location = '" + loc +"'" +
                        ", Customer_ID = '" + customerID +"'" +
                        ", Contact_ID = '" + contactID +"'" +
                        ", Start = '" + startsqlts  +"'" +
                        ", End = '" + endsqlts +"'" +
                        ", Last_Update = '" + LocalDateTime.now(ZoneId.of("UTC")) +"'" +
                        ", Last_Updated_By = '" + LoginController.userID +"'" +
                        ", User_ID = '" + userID +"'" +
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

    /**
     *
     * @param type
     * @param title
     * @param desc
     * @param loc
     * @param contactID
     * @param customerID
     * @param start
     * @param end
     * @param userID
     */
    public static void createAppointment(String type, String title, String desc, String loc, int contactID, int customerID, LocalDateTime start, LocalDateTime end, int userID) {
        try {
            ZoneId zid = ZoneId.systemDefault();
            ZonedDateTime zdtStart = start.atZone(zid);
            ZonedDateTime utcStart = zdtStart.withZoneSameInstant(ZoneId.of("UTC"));
            var ldtStart = utcStart.toLocalDateTime();
            Timestamp startsqlts = Timestamp.valueOf(ldtStart); //this value can be inserted into database

            ZonedDateTime zdtEnd = end.atZone(zid);
            ZonedDateTime utcEnd = zdtEnd.withZoneSameInstant(ZoneId.of("UTC"));
            var ldtEnd = utcEnd.toLocalDateTime();
            Timestamp endsqlts = Timestamp.valueOf(ldtEnd); //this value can be inserted into database

            PreparedStatement statement = ConnectDB.makeConnection()
                    .prepareStatement("INSERT INTO appointments (Title, Description, Location, Type, Start, End, Create_Date, Created_By, Customer_ID, User_ID, Contact_ID) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, title);
            statement.setString(2, desc);
            statement.setString(3, loc);
            statement.setString(4, type);
            statement.setTimestamp(5,startsqlts);
            statement.setTimestamp(6,endsqlts);
            statement.setTimestamp(7,Timestamp.valueOf(LocalDateTime.now(ZoneId.of("UTC"))));
            statement.setInt(8, LoginController.userID);
            statement.setInt(9, customerID);
            statement.setInt(10, userID);
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

    /**
     *
     * @param appointmentID
     */
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

    /**
     *
     * @return
     */
    public String getDate() {
        return this.getStart().split(" ")[0];
    }

    /**
     *
     * @return
     */
    public LocalTime getStartTimeASLocalTime() {
        return LocalDateTime.parse(this.getStart(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(ZoneId.systemDefault()).toLocalTime();
    }

    /**
     *
     * @return
     */
    public LocalTime getEndTimeASLocalTime() {
        return LocalDateTime.parse(this.getEnd(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(ZoneId.systemDefault()).toLocalTime();
    }

    /**
     *
     * @return
     */
    public LocalDateTime getStartTimeASLocalDateTime() {
        var date = LocalDateTime.parse(this.getStart(), DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a")).atZone(ZoneId.systemDefault()).toLocalDateTime();
        return date;
    }

    /**
     *
     * @return
     */
    public LocalDateTime getEndTimeASLocalDateTime() {
        var date = LocalDateTime.parse(this.getEnd(), DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a")).atZone(ZoneId.systemDefault()).toLocalDateTime();
        return date;
    }

    /**
     *
     * @return
     */
    public String getStartDateTime() {
        return this.getDate() + " - " + this.getStartTime();
    }

    /**
     *
     * @return
     */
    public String getStartTime() {
        return this.getStart().split(" ")[1];
    }

    /**
     *
     * @return
     */
    public String getEndTime() {
        return this.getEnd().split(" ")[1];
    }

    /**
     *
     * @return
     */
    public LocalDate getDateAsLocalDate() {
        var localDate = LocalDate.parse(this.getDate(), commonDateFormat);
        return localDate.atStartOfDay(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     *
     * @return
     */
    public Integer getUserID() {
        return userID;
    }

    /**
     *
     * @param userID
     */
    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getFormattedStartTime() {
        return this.getStartTimeASLocalTime().format(DateTimeFormatter.ofPattern("h:m a"));
    }

    public String getFormattedEndTime() {
        return this.getEndTimeASLocalDateTime().format(DateTimeFormatter.ofPattern("h:m a"));
    }
}