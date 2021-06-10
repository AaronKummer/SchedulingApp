package Controllers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

    /**
     * initialize controller
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            rb = ResourceBundle.getBundle("ViewProperties.login", Locale.getDefault());
            System.out.println("After rb");
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

            this.checkAppointments(userId);
            root = FXMLLoader.load(getClass().getResource("../Views/Main.fxml"));
            stage = (Stage) LoginButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            this.userID = null;
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("");
            alert.setHeaderText("Invalid Credentials");
            alert.setContentText("User not found, or incorrect password.");
            Optional<ButtonType> result = alert.showAndWait();
        }
    }

    /**
     * checks for appointments within 15 min
     */
    private void checkAppointments(Integer userId) {
        var appointmentsForUser = Appointment.getAppointmentsForUser(userId);
        for (Appointment apt : appointmentsForUser) {
            // check if same date as today
            var aptDate = apt.getDateAsLocalDate();

            if (DateTime.isToday(aptDate)) {
                var localTime = apt.getStartTimeASLocalTime();
                long minutes = ChronoUnit.MINUTES.between(LocalTime.now(DateTime.getZoneId()), localTime);
                // check for appointments within 15 minutes from now
                if (minutes <= 15 && minutes > 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("");
                    alert.setHeaderText("Appointment Reminder");
                    alert.setContentText("You have an upcoming appointment in " + minutes + " minutes");
                    alert.setContentText("\nAppointment ID = " + apt.getAppointmentID() + " at " + apt.getStartDateTime());
                    Optional<ButtonType> result = alert.showAndWait();
                }
            }
        }


    }

    /**
     * logs login attempt
     */
    public void logLoginAttempt(String user) {
        try {
            String fileName = "loginLogs.txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
            writer.append(DateTime.getNowTimeStamp() + " " + user + " " + "\n");
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
        this.logLoginAttempt(username);
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
