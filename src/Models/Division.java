package Models;

import Utilities.ConnectDB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Division {
    private int divisionID;
    private String divisionName;
    private int countryID;

    /**
     *
     * @param divisionName
     * @param divisionID
     * @param countryID
     */
    public Division(String divisionName, int divisionID, int countryID) {
        this.divisionName = divisionName;
        this.divisionID = divisionID;
        this.countryID = countryID;
    }

    public String getDivisionName() {
        return this.divisionName;
    }

    public int getDivisionID() {
        return this.divisionID;
    }

    /**
     *
     * @return
     */
    public static List<Division> getAllDivisions() {
        List<Division> contacts = new ArrayList<>();
        ResultSet result;
        try (var statement = ConnectDB.makeConnection().prepareStatement(
                "SELECT * from first_level_divisions")) {
            result = statement.executeQuery();
            while (result.next()) {
                contacts.add(new Division(result.getString("Division"), result.getInt("Division_ID"), result.getInt("COUNTRY_ID")));
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

    public int getCountryID() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }
}
