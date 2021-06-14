package Controllers;

import Models.Appointment;
import Models.Contact;
import Models.Customer;
import java.io.IOException;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import Models.User;
import Utilities.DateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Aaron Kummer
 */
public class AppointmentAddController implements Initializable {

    @FXML
    private Label AppointmentUserLabel;
    @FXML
    private Label AppointmentCustomerLabel;
    @FXML
    private Label AppointmentTitleLabel;
    @FXML
    private Label AppointmentDescriptionLabel;
    @FXML
    private Label AppointmentContactLabel;
    @FXML
    private Label AppointmentTypeLabel;
    @FXML
    private Label AppointmentLocationLabel;
    @FXML
    private Label AppointmentDateLabel;
    @FXML
    private Label AppointmentStartTimeLabel;
    @FXML
    private Label AppointmentEndTimeLabel;

    @FXML
    private ComboBox<String> AppointmentUserComboBox;
    @FXML
    private ComboBox<String> AppointmentCustomerComboBox;
    @FXML
    private TextField AppointmentTitleTextField;
    @FXML
    private TextField AppointmentDescriptionTextField;
    @FXML
    private ComboBox<String> AppointmentContactComboBox;
    @FXML
    private DatePicker AppointmentDatePicker;
    @FXML
    private TextField AppointmentTypeTextField;

    @FXML
    private ComboBox<String> AppointmentLocationComboBox;
    @FXML
    private TextField AppointmentStartTextField;
    @FXML
    private TextField AppointmentEndTextField;
    @FXML
    private Button Save;
    @FXML
    private Button Cancel;
    @FXML
    private TableColumn<Customer, Integer> AppointmentCustomerTableCustomerIDColumn;
    @FXML
    private TableColumn<Customer, String> AppointmentCustomerTableCustomerNameColumn;

    @FXML
    private Label AddUpdateLabel;

    private List<Customer> customers;

    private List<Contact> contacts;

    private List<User> users;

    @FXML
    private Button AppointmentBackButton;

    /**
     * initialize controller
     * This Lambda expression is used to find users name by ID in a list of users
     * this will hopefully improve readability of the code and demonstrate the student's understanding of how to implement a lambda expression.
     */
    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        setUserOptions();
        setContactOptions();
        setLocationOptions();
        setCustomerOptions();
        // Updating existing appointment
        if (AppointmentsMainController.selectedAppointment != null) {

            AppointmentTitleTextField.setText(AppointmentsMainController.selectedAppointment.getTitle());
            AppointmentDescriptionTextField.setText(AppointmentsMainController.selectedAppointment.getDescription());
            AppointmentTypeTextField.setText(AppointmentsMainController.selectedAppointment.getType());
            AddUpdateLabel.setText("Update Appointment");

            var customerName = Customer.getCustomerNameById(AppointmentsMainController.selectedAppointment.getCustomerID());

            AppointmentCustomerComboBox.getSelectionModel().select(customerName);
            AppointmentLocationComboBox.getSelectionModel().select(AppointmentsMainController.selectedAppointment.getLocation());
            AppointmentContactComboBox.getSelectionModel().select(AppointmentsMainController.selectedAppointment.getContactName());

            var selectedUser = this.users.stream()
                    .filter(c -> AppointmentsMainController.selectedAppointment.getUserID().equals(c.getNonStaticUserID()))
                    .findAny().orElse(null);
            var userName = selectedUser.getUserName();

            AppointmentUserComboBox.getSelectionModel().select(userName);
            AppointmentDatePicker.setValue(LocalDate.parse(AppointmentsMainController.selectedAppointment.getDate()));
            AppointmentStartTextField.setText(AppointmentsMainController.selectedAppointment.getStartTimeASLocalDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("h:mm a")));
            AppointmentEndTextField.setText(AppointmentsMainController.selectedAppointment.getEndTimeASLocalDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("h:mm a")));

        } else {
            // Creating new appointment
            AddUpdateLabel.setText("Add Appointment");
            setContactOptions();
            setLocationOptions();
            setCustomerOptions();
            setUserOptions();
        }
    }


    /**
     * set contact options
     */
    private  void setUserOptions() {
        this.users = User.getAllUsers();
        ObservableList<String> usersList = FXCollections.observableArrayList();
        for (User user: this.users) {
            usersList.add(user.getUserName());
        }
        AppointmentUserComboBox.setItems(usersList);
    }

    /**
     * set contact options
     */
    private  void setContactOptions() {
        this.contacts = Contact.getAllContacts();
        ObservableList<String> contactsList = FXCollections.observableArrayList();
        for (Contact contact: this.contacts) {
            contactsList.add(contact.getName());
        }
        AppointmentContactComboBox.setItems(contactsList);
    }

    /**
     * set customer options
     */
    private  void setCustomerOptions() {
        this.customers = Customer.getAllCustomersLite();
        ObservableList<String> customersList = FXCollections.observableArrayList();
        for (Customer customer: this.customers) {
            customersList.add(customer.getCustomerName());
        }
        AppointmentCustomerComboBox.setItems(customersList);
    }

    /**
     * set location options for combo box
     */
    private void setLocationOptions() {
        ObservableList<String> locationList = FXCollections.observableArrayList();
        locationList.addAll("Phoenix", "Arizona", "White Plains", "New York", "Montreal", "Canada", "London");
        AppointmentLocationComboBox.setItems(locationList);
    }

    /**
     *
     * @param start
     * @param end
     * @return
     */
    public static Boolean appointmentExists(LocalDateTime start, LocalDateTime end){
        Boolean returnValue = false;
        ObservableList<Appointment> appointments = Appointment.getAppointmentList();

        for(Appointment a : appointments) {
            LocalDateTime startTime = a.getStartTimeASLocalDateTime();
            LocalDateTime endEnd = a.getEndTimeASLocalDateTime();
            if (AppointmentsMainController.selectedAppointment != null) {
                if (AppointmentsMainController.selectedAppointment.getAppointmentID() == a.getAppointmentID()) {
                    return false;
                } else
                if(start.isAfter(startTime) && start.isBefore(endEnd) || end.isAfter(startTime) && end.isBefore(endEnd)) {
                    returnValue = true;
                }
            }
        }
        return returnValue;
    }

    /**
     * Saves appointment
     * discussion of lambda
     * * This Lambda expression is used to find a selected contact in a list.
     * this will hopefully improve readability of the code and demonstrate the student's understanding of how to implement a lambda expression.
     */
    @FXML
    private void Save() throws Exception {
        // Validate time input boxes
        LocalDate date = AppointmentDatePicker.getValue();
        var startTime = DateTime.parseTimeTextField(AppointmentStartTextField.getText(), date);
        var endTime = DateTime.parseTimeTextField(AppointmentEndTextField.getText(), date);

        if (!this.appointmentIsWithinBusinessHours(startTime, endTime, date)) {
            Alert noMatch = new  Alert(Alert.AlertType.ERROR);
            noMatch.setTitle("Invalid date");
            noMatch.setHeaderText("Please select a date and time within business hours");
            Optional<ButtonType> result = noMatch.showAndWait();
            return;
        }

        if(this.appointmentExists(startTime, endTime)){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Appointment cannot overlap another appointment.");
            alert.show();
            return;
        }

        if (startTime != null && endTime != null) {
            try {
                if (AppointmentsMainController.selectedAppointment == null) {
                    var location = AppointmentLocationComboBox.getValue();
                    var description = AppointmentDescriptionTextField.getText();
                    var type = AppointmentTypeTextField.getText();
                    var title = AppointmentTitleTextField.getText();

                    /**
                     * This Lambda expression is used to find a selected contact in a list.
                     * this will hopefully improve readability of the code and demonstrate the student's understanding of how to implement a lambda expression.
                     */
                    var selectedContact = this.contacts.stream()
                            .filter(c -> AppointmentContactComboBox.getValue().equals(c.getName()))
                            .findAny().orElse(null);
                    var contactID = selectedContact.getId();

                    /**
                     * This Lambda expression is used to find a selected contact in a list.
                     * this will hopefully improve readability of the code and demonstrate the student's understanding of how to implement a lambda expression.
                     */
                    var selectedCustomer = this.customers.stream()
                            .filter(c -> AppointmentCustomerComboBox.getValue().equals(c.getCustomerName()))
                            .findAny().orElse(null);
                    var customerID = selectedCustomer.getCustomerID();

                    /**
                     * This Lambda expression is used to find a selected contact in a list.
                     * this will hopefully improve readability of the code and demonstrate the student's understanding of how to implement a lambda expression.
                     */
                    var selectedUser = this.users.stream()
                            .filter(c -> AppointmentUserComboBox.getValue().equals(c.getUserName()))
                            .findAny().orElse(null);

                    var userId = selectedUser.getNonStaticUserID();

                    Appointment.createAppointment(type,title,description,location,contactID,customerID,startTime,endTime, userId);
                }
                else {
                    /**
                     * This Lambda expression is used to find a selected contact in a list.
                     * this will hopefully improve readability of the code and demonstrate the student's understanding of how to implement a lambda expression.
                     */
                    var selectedContact = this.contacts.stream()
                            .filter(c -> AppointmentContactComboBox.getValue().equals(c.getName()))
                            .findAny().orElse(null);
                    var contactID = selectedContact.getId();

                    /**
                     * This Lambda expression is used to find a selected contact in a list.
                     * this will hopefully improve readability of the code and demonstrate the student's understanding of how to implement a lambda expression.
                     */
                    var selectedCustomer = this.customers.stream()
                            .filter(c -> AppointmentCustomerComboBox.getValue().equals(c.getCustomerName()))
                            .findAny().orElse(null);
                    var customerID = selectedCustomer.getCustomerID();

                    /**
                     * This Lambda expression is used to find a selected contact in a list.
                     * this will hopefully improve readability of the code and demonstrate the student's understanding of how to implement a lambda expression.
                     */
                    var selectedUser = this.users.stream()
                            .filter(c -> AppointmentUserComboBox.getValue().equals(c.getUserName()))
                            .findAny().orElse(null);

                    var userId = selectedUser.getNonStaticUserID();

                    try {
                        var location = AppointmentLocationComboBox.getValue();
                        var description = AppointmentDescriptionTextField.getText();
                        var type = AppointmentTypeTextField.getText();
                        var id = AppointmentsMainController.selectedAppointment.getAppointmentID();
                        var title = AppointmentTitleTextField.getText();
                        if (selectedContact != null && selectedCustomer != null) {
                            // Update existing appointment

                            try {
                                Appointment.updateAppointment(
                                        id,
                                        type,
                                        title,
                                        description,
                                        location,
                                        contactID,
                                        customerID,
                                        startTime,
                                        endTime,
                                        userId
                                );
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            } catch(Exception e) {
            }
        }
        this.Back();
    }

    private boolean appointmentIsWithinBusinessHours(LocalDateTime startTime, LocalDateTime endTime, LocalDate date) {
        try {
            var isOutsideOfBusinessHours = false;
            if (startTime.toLocalTime().isBefore(LocalTime.parse("08:30:00")) ||
                    startTime.toLocalTime().isAfter(LocalTime.parse("22:00:00")) ||
                    endTime.toLocalTime().isBefore(LocalTime.parse("08:30:00"))  ||
                    endTime.toLocalTime().isAfter(LocalTime.parse("22:00:00"))) {
                    isOutsideOfBusinessHours = true;
            }
            var isWeekend = false;
            if (EnumSet.of( DayOfWeek.SATURDAY , DayOfWeek.SUNDAY ).contains(date.getDayOfWeek())) {
                isWeekend = true;
            }
            if (isOutsideOfBusinessHours || isWeekend) {
                return false;
            }
        } catch (Exception e) {
            Alert noMatch = new  Alert(Alert.AlertType.ERROR);
            noMatch.setTitle("Invalid date or time");
            noMatch.setHeaderText("Please select a date and time within business hours");
            noMatch.setHeaderText("Enter times as 8:30 am, 4:30pm etc");
            Optional<ButtonType> result = noMatch.showAndWait();
            return false;
        }


        return true;
    }

    /**
     * cancel button
     */
    @FXML
    private void Cancel(ActionEvent event) throws IOException {

    }

    /**
     * back button
     */
    @FXML
    private void Back() throws IOException {
        var root = FXMLLoader.load(getClass().getResource("../Views/AppointmentsMain.fxml"));
        var stage = (Stage) AppointmentBackButton.getScene().getWindow();
        Scene scene = new Scene((Parent) root);

        stage.setScene(scene);
        stage.show();
    }
}
