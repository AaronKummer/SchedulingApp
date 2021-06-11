package Models;

import Utilities.ConnectDB;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Country {
    private int countryID;
    private String country;
    private Date createDate;
    private String createdBy;
    private Timestamp lastUpdate;
    private String lastUpdatedBy;

    /**
     *
     * @param countryID
     * @param country
     * @param createDate
     * @param createdBy
     * @param lastUpdate
     * @param lastUpdatedBy
     */
    public Country(int countryID, String country, Date createDate, String createdBy, Timestamp lastUpdate, String lastUpdatedBy){
        setCountryID(countryID);
        setCountry(country);
        setCreateDate(createDate);
        setCreatedBy(createdBy);
        setLastUpdate(lastUpdate);
        setLastUpdatedBy(lastUpdatedBy);
    }

    /**
     *
     * @param countryID
     * @param country
     */
    public Country(int countryID, String country) {
        setCountryID(countryID);
        setCountry(country);
    }

    /**
     *
     * @return
     */
    public int getCountryID(){
        return countryID;
    }

    /**
     *
     * @return
     */
    public String getCountry(){
        return country;        
    }

    /**
     *
     * @return
     */
    public Date getCreateDate(){
        return createDate;
    }

    /**
     *
     * @return
     */
    public String getCreatedBy(){
        return createdBy;
    }

    /**
     *
     * @return
     */
    public Timestamp getLastUpdate(){
        this.lastUpdate = Timestamp.valueOf(LocalDateTime.of(lastUpdate.toLocalDateTime().toLocalDate(), lastUpdate.toLocalDateTime().toLocalTime()));
        return this.lastUpdate;
    }

    /**
     *
     * @return
     */
    public String getLastUpdatedBy(){
        return lastUpdatedBy;
    }
    
    //setters

    /**
     *
     * @param countryID
     */
    private void setCountryID(int countryID){
        this.countryID = countryID;
    }

    /**
     *
     * @param country
     */
    private void setCountry(String country){
        this.country = country;
    }

    /**
     *
     * @param createDate
     */
    private void setCreateDate(Date createDate){
        this.createDate = createDate;
    }

    /**
     *
     * @param createdBy
     */
    private void setCreatedBy(String createdBy){
        this.createdBy = createdBy;
    }

    /**
     *
     * @param lastUpdate
     */
    private void setLastUpdate(Timestamp lastUpdate){
        this.lastUpdate = lastUpdate;
    }

    /**
     *
     * @param lastUpdatedBy
     */
    private void setLastUpdatedBy(String lastUpdatedBy){
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * gets all countries
     * @return list of countries
     */
    public static List<Country> getAllCountries() {
        List<Country> countries = new ArrayList<>();
        ResultSet result;
        try (var statement = ConnectDB.makeConnection().prepareStatement(
                "SELECT * from countries")) {
            result = statement.executeQuery();
            while (result.next()) {
                countries.add(new Country( result.getInt("Country_ID"), result.getString("Country")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return countries;
    }
}
