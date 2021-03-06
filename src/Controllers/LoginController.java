package Controllers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;

import Models.Appointment;
import Utilities.ConnectDB;
import Utilities.DateTime;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Aaron Kummer
 */
public class LoginController implements Initializable {

    public static Integer userID;
    @FXML
    private TextField UsernameTextField;
    @FXML
    private Label LoginUsernameLabel;
    @FXML
    private PasswordField PasswordTextField;
    @FXML
    private Label LoginPasswordLabel;
    @FXML
    private Button LoginButton;
    @FXML
    private Label LoginLabel;
    @FXML
    private Label timeZoneLabel;
    /**
     * initialize controller
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            System.out.println(Locale.getDefault().getLanguage());
            timeZoneLabel.setText("( " + ZoneId.systemDefault().getDisplayName( TextStyle.FULL_STANDALONE , Locale.US ) + " )");
            rb = ResourceBundle.getBundle("ViewProperties.login", Locale.getDefault());
            LoginLabel.setText(rb.getString("title"));
            LoginUsernameLabel.setText(rb.getString("username"));
            UsernameTextField.setPromptText(rb.getString("username"));
            LoginPasswordLabel.setText(rb.getString("password"));
            PasswordTextField.setPromptText(rb.getString("password"));
            LoginButton.setText(rb.getString("signin"));
        } catch (MissingResourceException e) {
            System.out.println("Missing resource");
        }
    }

    /**
     * login button
     */
    @FXML
    private void LoginButtonHandler(ActionEvent event) throws SQLException, IOException {
        var usernameInput = UsernameTextField.getText();
        var passwordInput = PasswordTextField.getText();
        this.userID = null;
        Parent root;
        Stage stage;

        var userId = CheckPassword(usernameInput, passwordInput);
        if (userId != null) {
            this.logLoginSuccess(usernameInput);
            this.checkAppointments(userId);
            root = FXMLLoader.load(getClass().getResource("../Views/Main.fxml"));
            stage = (Stage) LoginButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            this.logLoginAttempt(usernameInput);
            Alert noMatch = new  Alert(Alert.AlertType.ERROR);
            if(Locale.getDefault().getLanguage().equals("en")){
                noMatch.setContentText("The username and password doesn't match.");
            }
            if(Locale.getDefault().getLanguage().equals("es")){
                noMatch.setContentText("El nombre de usuario y la contrase??a no coinciden.");
                noMatch.setHeaderText("credenciales no v??lidas");
                noMatch.setContentText("usuario no encontrado");
            }
            if(Locale.getDefault().getLanguage().contains("fr")){
                noMatch.setContentText("v??rifier les informations d'identification et r??essayer");
                noMatch.setHeaderText("Les informations d'identification invalides");
                noMatch.setContentText("utilisateur non trouv??");
            } else {
                noMatch.setHeaderText("Invalid Credentials");
                noMatch.setContentText("User not found, or incorrect password.");
            }
            this.userID = null;
            noMatch.setTitle("");

            Optional<ButtonType> result = noMatch.showAndWait();
        }
    }

    /**
     * checks for appointments within 15 min
     */
    private void checkAppointments(Integer userId) {
        var allAppointments = Appointment.getAppointmentList();
        var noUrgerntAppointments = true;
        for (Appointment apt : allAppointments) {
            // check if same date as today
            var aptDate = apt.getDateAsLocalDate();
            if (DateTime.isToday(aptDate)) {

                var localTime = LocalDateTime.parse(apt.getStart(), DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a"));
                long minutes = ChronoUnit.MINUTES.between(LocalTime.now(DateTime.getZoneId()), localTime);
                // check for appointments within 15 minutes from now
                if (minutes <= 15 && minutes > 0) {
                    noUrgerntAppointments = false;
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("");
                    alert.setHeaderText("Appointment Reminder");
                    alert.setContentText("You have an upcoming appointment in " + minutes + " minutes");
                    alert.setContentText("\nAppointment ID = " + apt.getAppointmentID() + " at " + apt.getStartDateTime());
                    Optional<ButtonType> result = alert.showAndWait();
                }
            }
        }
        if (noUrgerntAppointments) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("");
            alert.setHeaderText("No urgent appointments");
            alert.setContentText("You don't have any appointments within then next fifteen minutes.");
            Optional<ButtonType> result = alert.showAndWait();
        }
    }

    /**
     * logs login success
     */
    public void logLoginSuccess(String user) {
        try {
            String fileName = "login_activity.txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
            writer.append(DateTime.getNowTimeStamp() + " " + user + " login successful " + "\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * logs login attempt
     */
    public void logLoginAttempt(String user) {
        try {
            String fileName = "login_activity.txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
            writer.append(DateTime.getNowTimeStamp() + " " + user + " login failed " + "\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * checks pw
     */
    private Integer CheckPassword(String username, String password) throws SQLException {
        ResultSet result;

        try {
            Statement statement = ConnectDB.makeConnection().createStatement();
            String sqlStatement = "SELECT Password, User_ID FROM users WHERE User_Name ='" + username + "'";
            result = statement.executeQuery(sqlStatement);
            while (result.next()) {
                this.userID = result.getInt("User_ID");
                if (password.equals(result.getString("Password"))) {
                    return result.getInt("User_ID");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }System.out.println("reutrning false");
        return null;
    }
}
