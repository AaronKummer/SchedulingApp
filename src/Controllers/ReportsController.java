package Controllers;

import Models.*;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Aaron Kummer
 */
public class ReportsController implements Initializable {

    @FXML
    private AnchorPane Reports;
    @FXML
    private Tab ReportsByMonth;
    @FXML
    private Tab ReportsSchedule;
    @FXML
    private TableView<CountryReportPoco> ReportsAppointmentsByCountryTableView;
    @FXML
    private TableView<MonthReportPoco> ReportsByMonthAndTypeTableView;
    @FXML
    private TableView<Appointment> ReportsScheduleTableView;
    @FXML
    private Tab ReportsContactByMonth;
    @FXML
    private Button ReportsMainMenuButton;

    private ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    private final DateTimeFormatter datetimeDTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    //private final ZoneId localZoneID = ZoneId.of("UTC-8");
    private final ZoneId localZoneID = ZoneId.systemDefault();
    private final ZoneId utcZoneID = ZoneId.of("UTC");

    @FXML
    private TableColumn<MonthReportPoco,String> ReportsByMonthAndTypeMonthColumn;
    @FXML
    private TableColumn<MonthReportPoco,String> ReportsByMonthAndTypeTypeColumn;
    @FXML
    private TableColumn<MonthReportPoco, Integer> ReportsByMonthAndTypeNumberColumn;

    @FXML
    private TableColumn<Appointment,String> ReportsScheduleContactNameColumn;
    @FXML
    private TableColumn<Appointment,Integer> ReportsScheduleAppointmentIDColumn;
    @FXML
    private TableColumn<Appointment,String> ReportsScheduleTitleColumn;
    @FXML
    private TableColumn<Appointment,String> ReportsScheduleTypeColumn;
    @FXML
    private TableColumn<Appointment,String> ReportsScheduleDescriptionColumn;
    @FXML
    private TableColumn<Appointment,String> ReportsScheduleStartColumn;
    @FXML
    private TableColumn<Appointment,String> ReportsScheduleEndColumn;
    @FXML
    private TableColumn<Appointment,Integer> ReportsScheduleCustomerIDColumn;

    @FXML
    private TableColumn<CountryReportPoco,String> ReportsAppointmentsByCountryCountryNameColumn;
    @FXML
    private TableColumn<CountryReportPoco,Integer> ReportsAppointmentsByCountryNumberColumn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.appointments = Appointment.getAppointmentList();

        PropertyValueFactory<MonthReportPoco, String> monthlyTypeReportMonthFactory = new PropertyValueFactory<>("MonthName");
        PropertyValueFactory<MonthReportPoco, String> monthlyTypeReportTypeFactory = new PropertyValueFactory<>("Type");
        PropertyValueFactory<MonthReportPoco, Integer> monthlyTypeReportCountFactory = new PropertyValueFactory<>("AppointmentsNum");

        ReportsByMonthAndTypeMonthColumn.setCellValueFactory(monthlyTypeReportMonthFactory);
        ReportsByMonthAndTypeTypeColumn.setCellValueFactory(monthlyTypeReportTypeFactory);
        ReportsByMonthAndTypeNumberColumn.setCellValueFactory(monthlyTypeReportCountFactory);


        generateMonthTypeReport();
        generateScheduleReport();
        generateCountriesReport();



    }

    /**
     * Generates countries report
     */
    private void generateCountriesReport() {
        PropertyValueFactory<CountryReportPoco, String> ReportsScheduleContactNameFactory = new PropertyValueFactory<>("CountryName");
        PropertyValueFactory<CountryReportPoco, Integer> ReportsScheduleAppointmentIDFactory = new PropertyValueFactory<>("NumberOfCustomers");

        ReportsAppointmentsByCountryCountryNameColumn.setCellValueFactory(ReportsScheduleContactNameFactory);
        ReportsAppointmentsByCountryNumberColumn.setCellValueFactory(ReportsScheduleAppointmentIDFactory);

        var customerCountryReportObjects = Customer.getAllCustomersWithCountryName();
        var pocosForReport = new ArrayList<CountryReportPoco>();

        var distinctCountries = customerCountryReportObjects.stream().filter(distinctByKey(CountryReportPoco::getCountryName));

        for (CountryReportPoco country: distinctCountries.collect(Collectors.toList())) {
            var pocoForReport = new CountryReportPoco();
            var numOfCustomersWithCountry = 0;

            for (CountryReportPoco crp: customerCountryReportObjects) {
                var crpName = crp.getCountryName();
                var countryName = country.getCountryName();
                if (crpName.equals(countryName) ) {
                    numOfCustomersWithCountry ++;
                }
            }

            pocoForReport.setCountryName(country.getCountryName());
            pocoForReport.setNumberOfCustomers((int) numOfCustomersWithCountry);
            pocosForReport.add(pocoForReport);
        }
        ReportsAppointmentsByCountryTableView.setItems(FXCollections.observableList(pocosForReport));

    }

    /**
     * Generates Schedule report
     */
    private void generateScheduleReport() {
        PropertyValueFactory<Appointment, String> ReportsScheduleContactNameFactory = new PropertyValueFactory<>("ContactName");
        PropertyValueFactory<Appointment, Integer> ReportsScheduleAppointmentIDFactory = new PropertyValueFactory<>("AppointmentID");
        PropertyValueFactory<Appointment, String> ReportsScheduleTitleFactory = new PropertyValueFactory<>("Title");
        PropertyValueFactory<Appointment, String> ReportsScheduleTypeFactory = new PropertyValueFactory<>("Type");
        PropertyValueFactory<Appointment, String> ReportsScheduleDescriptionFactory = new PropertyValueFactory<>("Description");
        PropertyValueFactory<Appointment, String> ReportsScheduleStartFactory = new PropertyValueFactory<>("Start");
        PropertyValueFactory<Appointment, String> ReportsScheduleEndFactory = new PropertyValueFactory<>("End");
        PropertyValueFactory<Appointment, Integer> ReportsScheduleCustomerIDFactory = new PropertyValueFactory<>("CustomerID");

        ReportsScheduleContactNameColumn.setCellValueFactory(ReportsScheduleContactNameFactory);
        ReportsScheduleAppointmentIDColumn.setCellValueFactory(ReportsScheduleAppointmentIDFactory);
        ReportsScheduleTitleColumn.setCellValueFactory(ReportsScheduleTitleFactory);
        ReportsScheduleTypeColumn.setCellValueFactory(ReportsScheduleTypeFactory);
        ReportsScheduleDescriptionColumn.setCellValueFactory(ReportsScheduleDescriptionFactory);
        ReportsScheduleStartColumn.setCellValueFactory(ReportsScheduleStartFactory);
        ReportsScheduleEndColumn.setCellValueFactory(ReportsScheduleEndFactory);
        ReportsScheduleCustomerIDColumn.setCellValueFactory(ReportsScheduleCustomerIDFactory);

        ReportsScheduleTableView.setItems(this.appointments);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<ContactReportPoco> contactsReport = new ArrayList();
    }

    /**
     * Helper method to group list by property type
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    /**
     * Generates Month/Type report
     * discussion of lambda
     */
    private void generateMonthTypeReport() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        var distinctAppointmentsByType = this.appointments.stream().filter(distinctByKey(Appointment::getType));
        List<MonthReportPoco> monthsReport = new ArrayList();
        for (Appointment appointment : distinctAppointmentsByType.collect(Collectors.toList())) {
            var monthNum = appointment.getDateAsLocalDate().getMonth().getValue();
            var monthReportPoco = new MonthReportPoco(monthNum, appointment.getType());
            long numOfAptsWithType = this.appointments.stream()
                    .filter(c -> appointment.getType().equals(c.getType()))
                    .count();
            monthReportPoco.setAppointmentsNum((int) numOfAptsWithType);
            System.out.println(appointment.getType());
            monthsReport.add(monthReportPoco);
        }
        ReportsByMonthAndTypeTableView.setItems(FXCollections.observableList(monthsReport));
    }

    /**
     * Handles back button
     */
    @FXML
    private void ReportsMainMenuButtonHandler(ActionEvent event) throws IOException {
        var root = FXMLLoader.load(getClass().getResource("../Views/Main.fxml"));
        var stage = (Stage) ReportsMainMenuButton.getScene().getWindow();
        Scene scene = new Scene((Parent) root);
        stage.setScene(scene);
        stage.show();
    }
}
