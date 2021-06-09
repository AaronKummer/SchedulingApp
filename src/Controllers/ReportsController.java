package Controllers;

import Models.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private TableView<MonthReportPoco> ReportsByMonthAndTypeTableView;
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

    private void generateCountriesReport() {
    }

    private void generateScheduleReport() {

    }

    private void setReportsContactsByMonthTable() throws SQLException, Exception {
        var appointments = Appointment.getAppointmentList();

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
     */
    private void generateMonthTypeReport() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        var distinctAppointmentsByType = this.appointments.stream().filter(distinctByKey(Appointment::getType));
        List<MonthReportPoco> monthsReport = new ArrayList();
        for (Appointment appointment : distinctAppointmentsByType.collect(Collectors.toList())) {
            var monthNum = LocalDateTime.parse(appointment.getStart(),formatter).getMonth().getValue();
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

    @FXML
    private void ReportsMainMenuButtonHandler(ActionEvent event) throws IOException {
        var root = FXMLLoader.load(getClass().getResource("../Views/Main.fxml"));
        var stage = (Stage) ReportsMainMenuButton.getScene().getWindow();
        Scene scene = new Scene((Parent) root);
        stage.setScene(scene);
        stage.show();
    }
}
