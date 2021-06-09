package Controllers;

import Models.Appointment;
import Models.Contact;
import Models.Customer;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import Utilities.DateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Aaron Kummer
 */
public class AppointmentAddController implements Initializable {

    @FXML
    private Label AppointmentLabel;
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
    private Label AppointmentDateLabel;
    @FXML
    private Label AppointmentLocationLabel;
    @FXML
    private TextField AppointmentTitleTextField;
    @FXML
    private TextField AppointmentDescriptionTextField;
    @FXML
    private DatePicker AppointmentDatePicker;
    @FXML
    private Label AppointmentStartTimeLabel;
    @FXML
    private Label AppointmentUrlLabel;
    @FXML
    private TextField AppointmentUrlTextField;
    @FXML
    private Label AppointmentEndTimeLabel;
    @FXML
    private TextField AppointmentTypeTextField;
    @FXML
    private ComboBox<String> AppointmentContactComboBox;
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
    private ComboBox<String> AppointmentCustomerComboBox;
    @FXML
    private Label AddUpdateLabel;

    private List<Customer> customers;

    private List<Contact> contacts;


    @FXML
    private Button AppointmentBackButton;

    @Override
    public void initialize(URL url, ResourceBundle bundle) {

        SetContactOptions();
        SetLocationOptions();
        SetCustomerOptions();
        // Updating existing appointment
        if (AppointmentsMainController.selectedAppointment != null) {
            System.out.println(AppointmentsMainController.selectedAppointment.getContactName());
            AppointmentTitleTextField.setText(AppointmentsMainController.selectedAppointment.getTitle());
            AppointmentDescriptionTextField.setText(AppointmentsMainController.selectedAppointment.getDescription());
            AppointmentTypeTextField.setText(AppointmentsMainController.selectedAppointment.getType());
            AddUpdateLabel.setText("Update Appointment");
            var customerName = Customer.getCustomerNameById(AppointmentsMainController.selectedAppointment.getCustomerID());
            AppointmentCustomerComboBox.getSelectionModel().select(customerName);
            AppointmentLocationComboBox.getSelectionModel().select(AppointmentsMainController.selectedAppointment.getLocation());
            AppointmentContactComboBox.getSelectionModel().select(AppointmentsMainController.selectedAppointment.getContactName());
            AppointmentDatePicker.setValue(LocalDate.parse(AppointmentsMainController.selectedAppointment.getDate()));
            AppointmentStartTextField.setText(AppointmentsMainController.selectedAppointment.getStartTime());
            AppointmentEndTextField.setText(AppointmentsMainController.selectedAppointment.getEndTime());
        } else {
            // Creating new appointment
            AddUpdateLabel.setText("Add Appointment");
            SetLocationOptions();
            SetCustomerOptions();
        }



    }

    private  void SetContactOptions() {
        this.contacts = Contact.getAllContacts();
        ObservableList<String> contactsList = FXCollections.observableArrayList();
        for (Contact contact: this.contacts) {
            contactsList.add(contact.getName());
        }
        AppointmentContactComboBox.setItems(contactsList);
    }
    
    private  void SetCustomerOptions() {
        this.customers = Customer.getAllCustomersLite();
        ObservableList<String> customersList = FXCollections.observableArrayList();
        for (Customer customer: this.customers) {
            customersList.add(customer.getCustomerName());
        }
        AppointmentCustomerComboBox.setItems(customersList);
    }


    private void SetLocationOptions() {
        ObservableList<String> locationList = FXCollections.observableArrayList();
        locationList.addAll("Phoenix", "Arizona", "White Plains", "New York", "Montreal", "Canada", "London");
        AppointmentLocationComboBox.setItems(locationList);
    }

    @FXML
    private void Save() throws Exception {
        // Validate time input boxes
        LocalDate date = AppointmentDatePicker.getValue();
        var startTime = DateTime.parseTimeTextField(AppointmentStartTextField.getText(), date);
        var endTime = DateTime.parseTimeTextField(AppointmentEndTextField.getText(), date);
        if (startTime != null && endTime != null) {
            try {
                if (AppointmentsMainController.selectedAppointment == null) {
                    var location = AppointmentLocationComboBox.getValue();
                    var description = AppointmentDescriptionTextField.getText();
                    var type = AppointmentTypeTextField.getText();
                    var title = AppointmentTitleTextField.getText();

                    var selectedContact = this.contacts.stream()
                            .filter(c -> AppointmentContactComboBox.getValue().equals(c.getName()))
                            .findAny().orElse(null);
                    var contactID = selectedContact.getId();

                    var selectedCustomer = this.customers.stream()
                            .filter(c -> AppointmentCustomerComboBox.getValue().equals(c.getCustomerName()))
                            .findAny().orElse(null);
                    var customerID = selectedCustomer.getCustomerID();
                    Appointment.createAppointment(type,title,description,location,contactID,customerID,startTime,endTime);
                }
                else {

                    var selectedContact = this.contacts.stream()
                            .filter(c -> AppointmentContactComboBox.getValue().equals(c.getName()))
                            .findAny().orElse(null);
                    var contactID = selectedContact.getId();

                    var selectedCustomer = this.customers.stream()
                            .filter(c -> AppointmentCustomerComboBox.getValue().equals(c.getCustomerName()))
                            .findAny().orElse(null);
                    var customerID = selectedCustomer.getCustomerID();

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
                                        endTime
                                );
                            } catch (Exception e) {
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

    @FXML
    private void Cancel(ActionEvent event) throws IOException {

    }

    @FXML
    private void Back() throws IOException {
        var root = FXMLLoader.load(getClass().getResource("../Views/AppointmentsMain.fxml"));
        var stage = (Stage) AppointmentBackButton.getScene().getWindow();
        Scene scene = new Scene((Parent) root);

        stage.setScene(scene);
        stage.show();
    }
}
