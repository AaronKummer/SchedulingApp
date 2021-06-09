package Models;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Controllers.LoginController;
import Utilities.ConnectDB;
import Utilities.DateTime;

public class Customer {
    private int customerID;
    private String customerName;
    private int active;
    private Date createDate;
    private String createdBy;
    private String address;
    private String city;
    private String postalCode;
    private String phone;
    private String country;
    private Date lastUpdate;
    private String lastUpdateBy;
    private int divisionID;
    private String divisionName;

    public Customer(int customerID, String customerName, int active, String address, String city, String postalCode, String phone, String country, Date lastUpdate, String lastUpdateBy) {
        setCustomerID(customerID);
        setCustomerName(customerName);
        setCustomerActive(active);
        setCustomerAddress(address);
        setCustomerCity(city);
        setCustomerPostalCode(postalCode);
        setCustomerPhone(phone);
        setCustomerCountry(country);
        setCustomerLastUpdate(lastUpdate);
        setCustomerLastUpdateBy(lastUpdateBy);
    }

    public Customer(String customerName, int customerID, String address, String postalCode, String phone, int divisionID) {
        setCustomerID(customerID);
        setCustomerName(customerName);
        setCustomerAddress(address);
        setCustomerPostalCode(postalCode);
        setCustomerPhone(phone);
        setCustomerDivisionID(divisionID);
    }

    public Customer(String customerName, int customerID) {
        setCustomerID(customerID);
        setCustomerName(customerName);
    }

    public Customer() {

    }

    public static void deleteCustomer(Integer selectedCustomerID) {
        Appointment.deleteAppointmentsForCustomerID(selectedCustomerID);
        try {
            PreparedStatement statement = ConnectDB.makeConnection().prepareStatement("DELETE FROM customers WHERE customers.Customer_ID = " + selectedCustomerID);
            statement.executeUpdate();
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int getCustomerID() {
        return customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getCustomerActive() {
        return active;
    }

    public String getCustomerAddress() {
        return address;
    }

    public String getCustomerCity() {
        return city;
    }

    public String getCustomerPostalCode() {
        return postalCode;
    }

    public String getCustomerPhone() {
        return phone;
    }

    public String getCustomerCountry() {
        return country;
    }

    public Date getCustomerLastUpdate() {
        return lastUpdate;
    }

    public String getCustomerLastUpdateBy() {
        return lastUpdateBy;
    }

    public int getDivisionID() {
        return this.divisionID;
    }
    //setters
    
    public void setCustomerID(int customerID) {

        this.customerID = customerID;
    }

    public void setCustomerDivisionID(int divisionID) {
        this.divisionID = divisionID;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerActive(int active) {
        this.active = active;
    }

    public void setCustomerAddress(String address) {
        this.address = address;
    }


    public void setCustomerCity(String city) {
        this.city = city;
    }

    public void setCustomerPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setCustomerPhone(String phone) {
        this.phone = phone;
    }

    public void setCustomerCountry(String country) {
        this.country = country;
    }

    public void setCustomerLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setCustomerLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public static String getCustomerNameById(int id) {
        var customerName = "";
        ResultSet result;
        try (var statement = ConnectDB.makeConnection().prepareStatement(
                "SELECT Customer_ID, Customer_Name from customers WHERE Customer_ID = " + id)) {
            result = statement.executeQuery();
            while (result.next()) {
                return result.getString("Customer_Name");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customerName;

    }

    public static List<Customer> getAllCustomersLite() {
        List<Customer> customers = new ArrayList<>();
        ResultSet result;
        try (var statement = ConnectDB.makeConnection().prepareStatement(
                "SELECT * from customers")) {
            result = statement.executeQuery();
            while (result.next()) {
                customers.add(new Customer(result.getString("Customer_Name"), result.getInt("Customer_ID")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customers;
    }

    public String getFormattedDivisionName(int divisionID) {
        ResultSet result;
        String divisionName = "";
        String countryName = "";
        try (var statement = ConnectDB.makeConnection().prepareStatement(
                "SELECT first_level_divisions.Division, first_level_divisions.Division_ID, countries.COUNTRY_ID, countries.Country FROM first_level_divisions, countries " +
                        "WHERE first_level_divisions.COUNTRY_ID = countries.Country_ID AND first_level_divisions.Division_ID = " + divisionID)) {
            result = statement.executeQuery();
            while (result.next()) {
                countryName = result.getString("Country");
                divisionName = result.getString("Division");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return countryName + " - " + divisionName;
    }

    // gets all, fully hydrated customers
    public static List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        ResultSet result;
        try (var statement = ConnectDB.makeConnection().prepareStatement(
                "SELECT * from customers")) {
            result = statement.executeQuery();
            while (result.next()) {
                customers.add(new Customer(
                        result.getString("Customer_Name"),
                        result.getInt("Customer_ID"),
                        result.getString("Address"),
                        result.getString("Postal_Code"),
                        result.getString("Phone"),
                        result.getInt("Division_ID")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customers;
    }

    public static void updateCustomer(Integer customerId, String customerName, int divisionID, String address, String postalCode, String phone) {
        PreparedStatement statement;

        try {
            statement = ConnectDB.makeConnection().prepareStatement(
                    "UPDATE customers" +
                            " SET Customer_ID = '" + customerId +"'" +
                            ", Customer_Name = '" + customerName +"'" +
                            ", Address = '" + address +"'" +
                            ", Postal_Code = '" + postalCode +"'" +
                            ", Phone = '" + phone +"'" +
                            ", Division_ID = '" + divisionID +"'" +
                            ", Last_Update = '" + LocalDateTime.now() +"'" +
                            ", Last_Updated_By = '" + LoginController.userID +"'" +
                            " WHERE Customer_ID = " + customerId
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

    public static void createCustomer(Integer customerId, String customerName, int divisionID, String address, String postalCode, String phone) {
        try {
            var now = new Date(Calendar.getInstance().getTime().getTime());
            PreparedStatement statement = ConnectDB.makeConnection()
                    .prepareStatement("INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Division_ID, Created_By, Create_Date) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, customerName);
            statement.setString(2, address);
            statement.setString(3, postalCode);
            statement.setString(4, phone);
            statement.setInt(5, divisionID);
            statement.setInt(6, LoginController.userID);
            statement.setDate(7, now);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
