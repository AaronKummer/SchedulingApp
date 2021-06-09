package Controllers;

import Models.Appointment;
import Utilities.ConnectDB;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
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
    private Button Back;

    private ToggleGroup RadioButtonToggleGroup;
    private boolean isWeekly;
    public static Appointment selectedAppointment;

    ObservableList<Appointment> appointments = FXCollections.observableArrayList();

    private final DateTimeFormatter datetimeDTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    //private final ZoneId localZoneID = ZoneId.of("UTC-8");
    //private final ZoneId localZoneID = ZoneId.systemDefault();
    //private final ZoneId utcZoneID = ZoneId.of("UTC");

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
        AppointmentWeekRadioButton.setSelected(true);
        AppointmentMonthRadioButton.setSelected(false);

        isWeekly = true;

        try {
            GetAppointments();
        } catch (SQLException ex) {
            System.out.println(ex);
        }

    }

    public void GetAppointments() throws SQLException {
        appointments.clear();
        appointments = Appointment.getAppointmentList();
        //AppointmentsTable.setItems(Appointment.getAppointmentList());
        //filter appointments by week or month
        if (isWeekly) {
            filterAppointmentsByWeek(appointments);
        } else {
            filterAppointmentsByMonth(appointments);
        }
    }

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

    @FXML
    private void DeleteAppointmentButton(ActionEvent event) throws Exception {
        this.selectedAppointment = AppointmentsTable.getSelectionModel().getSelectedItem();
        try {
            PreparedStatement statement = ConnectDB.makeConnection().prepareStatement("DELETE FROM appointments WHERE appointments.Appointment_ID = " + this.selectedAppointment.getAppointmentID());
            int result = statement.executeUpdate();
        } catch (SQLException e) {

        }

        this.selectedAppointment = null;
        this.GetAppointments();
    }

    @FXML
    private void AppointmentWeekRadioButton(ActionEvent event) throws SQLException, Exception {
        isWeekly = true;
        this.GetAppointments();
    }

    @FXML
    private void AppointmentMonthRadioButton(ActionEvent event) throws SQLException, Exception {
        isWeekly = false;
        GetAppointments();

    }

    public void filterAppointmentsByMonth(ObservableList appointments) throws SQLException {
        var now = LocalDate.now();
        var nowPlus1Month = now.plusMonths(1);

        FilteredList<Appointment> filteredData = new FilteredList<>(appointments);
        filteredData.setPredicate(row -> {

            LocalDate rowDate = LocalDate.parse(row.getStart(), datetimeDTF);

            return rowDate.isAfter(now.minusDays(1)) && rowDate.isBefore(nowPlus1Month);
        });
        AppointmentsTable.setItems(filteredData);
    }

    public void filterAppointmentsByWeek(ObservableList appointments) {
        LocalDate now = LocalDate.now();
        LocalDate nowPlus1Week = now.plusWeeks(1);

        FilteredList<Appointment> filteredData = new FilteredList<>(appointments);
        filteredData.setPredicate(row -> {

            LocalDate rowDate = LocalDate.parse(row.getStart(), datetimeDTF);

            return rowDate.isAfter(now.minusDays(1)) && rowDate.isBefore(nowPlus1Week);
        });
        AppointmentsTable.setItems(filteredData);
    }

    @FXML
    private void Back(ActionEvent event) throws IOException {
        var root = FXMLLoader.load(getClass().getResource("../Views/Main.fxml"));
        var stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root);
        stage.setScene(scene);
        stage.show();
    }

}
