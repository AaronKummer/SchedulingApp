package Controllers;

import Models.*;
import Utilities.ConnectDB;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Aaron Kummer
 */
public class CustomerController implements Initializable {

    @FXML
    private AnchorPane CustomerAddLabel;
    @FXML
    private Label CustomerLabel;
    @FXML
    private TableView<Customer> CustomersTable;
    @FXML
    private TableColumn<Customer, Integer> CustomerID;
    @FXML
    private TableColumn<Customer, String> CustomerName;
    @FXML
    private TableColumn<Customer, String> CustomerPhone;
    @FXML
    private TextField CustomerCustomerIDTextField;
    @FXML
    private Label CustomerCustomerIDLabel;
    @FXML
    private Label CustomerCustomerNameLabel;
    @FXML
    private Label CustomerAddressLabel;
    @FXML
    private Label CustomerAddress2Label;
    @FXML
    private Label CustomerCityLabel;
    @FXML
    private Label CustomerCountryLabel;
    @FXML
    private Label CustomerPostalCodeLabel;
    @FXML
    private Label CustomerPhoneLabel;
    @FXML
    private Label NewCustomerMessage;
    @FXML
    private ComboBox<String> CustomerCountryComboBox;
    @FXML
    private Button CustomerSaveButton;
    @FXML
    private Button CustomerCancelButton;
    @FXML
    private Button AddButton;
    @FXML
    private Button DeleteButton;
    @FXML
    private TextField CustomerCustomerNameTextField;
    @FXML
    private TextField CustomerAddressTextField;
    @FXML
    private TextField CustomerAddress2TextField;
    @FXML
    private TextField CustomerPostalCodeTextField;
    @FXML
    private TextField CustomerPhoneTextField;
    @FXML
    private ComboBox<String> CustomerDivisionComboBox;
    @FXML
    private RadioButton CustomerActiveRadioButton;
    @FXML
    private RadioButton CustomerInactiveRadioButton;
    @FXML
    private ToggleGroup RadioButtonToggleGroup;
    @FXML
    private Button Back;
    private Integer selectedCustomerID;
    private List<Division> divisions;

    /**
     * Initializes Customers page
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.setupController();
    }

    /**
     * initialize data
     */
    public void setupController() {
        this.selectedCustomerID = null;
        this.clearFields();
        this.divisions = Division.getAllDivisions();
        PropertyValueFactory<Customer, Integer> customerIDFactory = new PropertyValueFactory<>("CustomerID");
        PropertyValueFactory<Customer, String> customerNameFactory = new PropertyValueFactory<>("CustomerName");
        PropertyValueFactory<Customer, String> customerPhoneFactory = new PropertyValueFactory<>("CustomerPhone");

        CustomerID.setCellValueFactory(customerIDFactory);
        CustomerName.setCellValueFactory(customerNameFactory);
        CustomerPhone.setCellValueFactory(customerPhoneFactory);

        CustomersTable.setItems(FXCollections.observableArrayList(Customer.getAllCustomers()));

        ObservableList<String> divisionsList = FXCollections.observableArrayList();
        for (Division division: this.divisions) {
            divisionsList.add(division.getDivisionName());
        }
        CustomerDivisionComboBox.setItems(divisionsList);
        CustomerDivisionComboBox.isDisabled();
        CustomersTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    try {
                        customerListener(newValue);
                    } catch (Exception ex) {
                        // TODO
                    }
                });
    }

    /**
     * listener for click on customer row
     */
    public void customerListener(Customer customer) throws SQLException, Exception {
        CustomerLabel.setText("");
        this.selectedCustomerID = customer.getCustomerID();
        CustomerCustomerNameTextField.setText(customer.getCustomerName());
        CustomerAddressTextField.setText(customer.getCustomerAddress());
        CustomerPostalCodeTextField.setText(customer.getCustomerPostalCode());
        CustomerPhoneTextField.setText(customer.getCustomerPhone());

        var selectedDivision = this.divisions.stream()
                .filter(d -> d.getDivisionID() == customer.getDivisionID()).findAny().orElse(null);
        CustomerDivisionComboBox.getSelectionModel().select(selectedDivision.getDivisionName());

    }

    public void updateCustomerTable() throws SQLException {

    }

    @FXML
    private void Save(ActionEvent event) throws Exception {
        /*System.out.println("CustomerAdd: " + customerAdd);
        System.out.println("CustomerUpdate: " + customerUpdate);
        if (CustomerCustomerNameTextField.getText() != null && customerAdd || customerUpdate) {
            if (validCustomer()) {
                if (customerAdd) {
                    saveCustomer();
                    clearTextFields();
                    updateCustomerTable();
                } else if (customerUpdate) {
                    updateCustomer();
                    clearTextFields();
                    updateCustomerTable();
                }
            }
        } else {
            System.out.println("No customer selected to save!");
        }*/

    }


    private void deleteCustomer(Customer customer) throws Exception {
        this.selectedCustomerID = null;
        System.out.println("***** Begin Delete Customer *****");
        try {
            PreparedStatement ps = ConnectDB.makeConnection().prepareStatement("DELETE customer.*, address.* from customer, address WHERE customer.customerId = ? AND customer.addressId = address.addressId");
            System.out.println("Delete CustomerID: " + customer.getCustomerID());
            ps.setInt(1, customer.getCustomerID());
            int result = ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Delete Customer SQL statement contains an error!");
        }

        updateCustomerTable();
        System.out.println("***** End Delete Customer *****");
    }
    
    //allows inputs to add/update customer
    public void enableCustomerFields(){
        CustomerActiveRadioButton.setDisable(false);
        CustomerInactiveRadioButton.setDisable(false);
        CustomerCustomerIDTextField.setDisable(false);
        CustomerCustomerIDTextField.setEditable(false);
        CustomerCustomerNameTextField.setDisable(false);
        CustomerAddressTextField.setDisable(false);
        CustomerAddress2TextField.setDisable(false);
        CustomerCountryComboBox.setDisable(false);
        CustomerPostalCodeTextField.setDisable(false);
        CustomerPhoneTextField.setDisable(false);
        CustomerSaveButton.setDisable(false);
        CustomerCancelButton.setDisable(false);

    }

    private void clearFields() {
        CustomerCustomerNameTextField.setText("");
        CustomerAddressTextField.setText("");
        CustomerPostalCodeTextField.setText("");
        CustomerPhoneTextField.setText("");
    }


    @FXML
    private void AddButtonHandler(ActionEvent event) throws SQLException {
        this.selectedCustomerID = null;
        this.clearFields();
        CustomerLabel.setText("Create a new customer");
    }

    @FXML
    private void DeleteButtonHandler(ActionEvent event) {
        Customer.deleteCustomer(this.selectedCustomerID);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("");
        alert.setHeaderText("Success");
        alert.setContentText("Customer was successfully deleted");
        Optional<ButtonType> result = alert.showAndWait();
        this.setupController();
    }

    @FXML
    private void BackButtonHandler(ActionEvent event) throws IOException {
        var root = FXMLLoader.load(getClass().getResource("../Views/Main.fxml"));
        var stage = (Stage) Back.getScene().getWindow();
        Scene scene = new Scene((Parent) root);

        stage.setScene(scene);
        stage.show();
    }

    public void SaveButtonHandler(ActionEvent actionEvent) {
        var validInput = false;

        var selectedDivision = this.divisions.stream()
                .filter(d -> d.getDivisionName() == CustomerDivisionComboBox.getValue()).findAny().orElse(null);
        var customerId = this.selectedCustomerID;
        var customerName = CustomerCustomerNameTextField.getText();
        var address = CustomerAddressTextField.getText();
        var phone = CustomerPhoneTextField.getText();
        var postalcode = CustomerPostalCodeTextField.getText();
        var divisionId = selectedDivision.getDivisionID();

        // Update customer
        if (this.selectedCustomerID != null) {
            Customer.updateCustomer(customerId, customerName,divisionId,address,postalcode,phone);
        }
        // Create new customer
        else {
            Customer.createCustomer(customerId, customerName,divisionId,address,postalcode,phone);
        }
        this.setupController();
    }
}
