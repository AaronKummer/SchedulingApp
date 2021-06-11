package Controllers;

import Models.Appointment;
import Utilities.ConnectDB;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

import Utilities.DateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Aaron Kummer
 */
public class AppointmentsMainController implements Initializable {

    @FXML
    private Button AddAppointment;
    @FXML
    private TableView<Appointment> AppointmentsTable;
    @FXML
    private TableColumn<Appointment, Integer> AppointmentID;
    @FXML
    private TableColumn<Appointment, String> AppointmentStart;
    @FXML
    private TableColumn<Appointment, String> AppointmentEnd;
    @FXML
    private TableColumn<Appointment, String> AppointmentTitle;
    @FXML
    private TableColumn<Appointment, String> AppointmentDescription;
    @FXML
    private TableColumn<Appointment, String> AppointmentType;
    @FXML
    private TableColumn<Appointment, Integer> AppointmentCustomerID;
    @FXML
    private TableColumn<Appointment, String> AppointmentContactName;
    @FXML
    private TableColumn<Appointment, String> AppointmentLocation;
    @FXML
    private RadioButton AppointmentWeekRadioButton;
    @FXML
    private RadioButton AppointmentMonthRadioButton;
    @FXML
    private RadioButton AppointmentAllRadioButton;
    @FXML
    private Button Back;
    private ToggleGroup RadioButtonToggleGroup;
    private boolean isWeekly;
    private boolean isMonthly;
    public static Appointment selectedAppointment;

    ObservableList<Appointment> appointments = FXCollections.observableArrayList();

    private final DateTimeFormatter datetimeDTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * init controller
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.selectedAppointment = null;
        PropertyValueFactory<Appointment, Integer> appointmentIDFactory = new PropertyValueFactory<>("AppointmentID");
        PropertyValueFactory<Appointment, String> appointmentTitleFactory = new PropertyValueFactory<>("Title");
        PropertyValueFactory<Appointment, String> appointmentDescriptionFactory = new PropertyValueFactory<>("Description");
        PropertyValueFactory<Appointment, String> appointmentLocationFactory = new PropertyValueFactory<>("Location");
        PropertyValueFactory<Appointment, String> appointmentContactFactory = new PropertyValueFactory<>("ContactName");
        PropertyValueFactory<Appointment, String> appointmentTypeFactory = new PropertyValueFactory<>("Type");
        PropertyValueFactory<Appointment, String> appointmentStartFactory = new PropertyValueFactory<>("Start");
        PropertyValueFactory<Appointment, String> appointmentEndFactory = new PropertyValueFactory<>("End");
        PropertyValueFactory<Appointment, Integer> appointmentCustomerFactory = new PropertyValueFactory<>("CustomerID");

        AppointmentID.setCellValueFactory(appointmentIDFactory);
        AppointmentTitle.setCellValueFactory(appointmentTitleFactory);
        AppointmentDescription.setCellValueFactory(appointmentDescriptionFactory);
        AppointmentLocation.setCellValueFactory(appointmentLocationFactory);
        AppointmentContactName.setCellValueFactory(appointmentContactFactory);
        AppointmentType.setCellValueFactory(appointmentTypeFactory);
        AppointmentStart.setCellValueFactory(appointmentStartFactory);
        AppointmentEnd.setCellValueFactory(appointmentEndFactory);
        AppointmentCustomerID.setCellValueFactory(appointmentCustomerFactory);

        RadioButtonToggleGroup = new ToggleGroup();
        AppointmentWeekRadioButton.setToggleGroup(RadioButtonToggleGroup);
        AppointmentMonthRadioButton.setToggleGroup(RadioButtonToggleGroup);
        AppointmentAllRadioButton.setToggleGroup(RadioButtonToggleGroup);
        AppointmentWeekRadioButton.setSelected(false);
        AppointmentMonthRadioButton.setSelected(false);
        AppointmentAllRadioButton.setSelected(true);
        isWeekly = false;
        isMonthly = false;

        try {
            GetAppointments();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    /**
     * gets appointments and filters
     */
    public void GetAppointments() throws SQLException {
        appointments.clear();
        appointments = Appointment.getAppointmentList();

        //filter appointments by week or month
        if (isWeekly) {
            filterAppointmentsByWeek(appointments);
        } else
        if (isMonthly) {
            filterAppointmentsByMonth(appointments);
        }
        else {
            AppointmentsTable.setItems(appointments);
        }
    }

    /**
     * goes to add appointment view
     */
    @FXML
    private void AddAppointment(ActionEvent event) throws IOException {
        this.selectedAppointment = null;
        Parent root;
        root = FXMLLoader.load(getClass().getResource("../Views/AppointmentAdd.fxml"));
        var stage = (Stage) AddAppointment.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * goes to update appointment view
     */
    @FXML
    private void UpdateAppointment(ActionEvent event) throws IOException {
        this.selectedAppointment = AppointmentsTable.getSelectionModel().getSelectedItem();
        if (this.selectedAppointment != null) {
            try {
                var root = FXMLLoader.load(getClass().getResource("../Views/AppointmentAdd.fxml"));
                var stage = (Stage)((Button)event.getSource()).getScene().getWindow();
                Scene scene = new Scene((Parent) root);
                stage.setScene(scene);
                stage.show();
            } catch(Exception e) {
                System.out.println(e.getMessage());
            }

        } else {
            System.out.println("No appointment has been selected.");
        }
    }

    /**
     * deletes appointment
     */
    @FXML
    private void DeleteAppointmentButton(ActionEvent event) throws Exception {
        this.selectedAppointment = AppointmentsTable.getSelectionModel().getSelectedItem();
        try {
            PreparedStatement statement = ConnectDB.makeConnection().prepareStatement("DELETE FROM appointments WHERE appointments.Appointment_ID = " + this.selectedAppointment.getAppointmentID());
            int result = statement.executeUpdate();
        } catch (SQLException e) {

        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("");
        alert.setHeaderText("Appointment deleted");
        alert.setContentText("You have deleted appointment ID " + this.selectedAppointment.getAppointmentID() + "of type" + this.selectedAppointment.getType());
        Optional<ButtonType> result = alert.showAndWait();

        this.selectedAppointment = null;
        this.GetAppointments();
    }

    /**
     * week radio
     */
    @FXML
    private void AppointmentWeekRadioButton(ActionEvent event) throws SQLException, Exception {
        isWeekly = true;
        isMonthly = false;
        this.GetAppointments();
    }

    /**
     * all radio
     */
    @FXML
    private void AppointmentAllRadioButton(ActionEvent event) throws SQLException, Exception {
        isWeekly = false;
        isMonthly = false;
        this.GetAppointments();
    }

    /**
     * month radio
     */
    @FXML
    private void AppointmentMonthRadioButton(ActionEvent event) throws SQLException, Exception {
        isWeekly = false;
        isMonthly = true;
        GetAppointments();

    }

    /**
     * filters based on radio
     */
    public void filterAppointmentsByMonth(ObservableList appointments) throws SQLException {
        var now = LocalDate.now(DateTime.getZoneId());
        var nowPlus1Month = now.plusMonths(1);

        FilteredList<Appointment> filteredData = new FilteredList<>(appointments);
        filteredData.setPredicate(row -> {

            LocalDate rowDate = LocalDate.parse(row.getStart(), datetimeDTF);

            return rowDate.isAfter(now.minusDays(1)) && rowDate.isBefore(nowPlus1Month);
        });
        AppointmentsTable.setItems(filteredData);
    }

    /**
     * filters
     */
    public void filterAppointmentsByWeek(ObservableList appointments) {
        LocalDate now = LocalDate.now(DateTime.getZoneId());
        LocalDate nowPlus1Week = now.plusWeeks(1);

        FilteredList<Appointment> filteredData = new FilteredList<>(appointments);
        filteredData.setPredicate(row -> {

            LocalDate rowDate = LocalDate.parse(row.getStart(), datetimeDTF);

            return rowDate.isAfter(now.minusDays(1)) && rowDate.isBefore(nowPlus1Week);
        });
        AppointmentsTable.setItems(filteredData);
    }

    /**
     * back button
     */
    @FXML
    private void Back(ActionEvent event) throws IOException {
        var root = FXMLLoader.load(getClass().getResource("../Views/Main.fxml"));
        var stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root);
        stage.setScene(scene);
        stage.show();
    }

}
