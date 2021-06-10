package Controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Aaron Kummer
 */
public class MainController implements Initializable {

    @FXML
    private AnchorPane MainScreenLabel;
    @FXML
    private Button Customers;
    @FXML
    private Button Appointments;
    @FXML
    private Button Reports;
    @FXML
    private Button Exit;

    Parent root;
    Stage stage;

    /**
     * init controller
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("initializing the main screen");

    }

    /**
     * goes to customers view
     */
    @FXML
    private void Customers(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("../Views/Customers.fxml"));
        stage = (Stage)Customers.getScene().getWindow();
        stage.setX(400);
        stage.setY(200);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * goes to apts view
     */
    @FXML
    private void Appointments(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("../Views/AppointmentsMain.fxml"));
        stage = (Stage)Appointments.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * goes to reports view
     */
    @FXML
    private void Reports(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("../Views/Reports.fxml"));
        stage = (Stage)Reports.getScene().getWindow();
        stage.setX(400);
        stage.setY(100);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * exits program
     */
    @FXML
    private void Exit(ActionEvent event) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Required");
        alert.setHeaderText("Confirm Exit");
        alert.setContentText("Are you sure you want to exit?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            //ConnectDB.closeConnection();
            System.out.println("Program Exit.");
            System.exit(0);
        }
        else{
            System.out.println("Exit canceled.");
        }
    }

}
