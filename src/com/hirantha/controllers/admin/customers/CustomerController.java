package com.hirantha.controllers.admin.customers;

import animatefx.animation.FadeIn;
import com.hirantha.controllers.admin.DashboardController;
import com.hirantha.controllers.admin.admins.AdminsController;
import com.hirantha.quries.customers.CustomerQueries;
import com.hirantha.fxmls.FXMLS;
import com.hirantha.models.data.customer.Customer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class CustomerController implements Initializable {


    public AnchorPane basePane;

    @FXML
    private VBox rowsContainer;

    @FXML
    private AnchorPane profileContainer;

    @FXML
    private TextField txtSearch;

    @FXML
    private Label btnNewCustomer;

    @FXML
    private Label btnReload;

    private List<Customer> customers;
    List<Customer> temp = new ArrayList<>();
    
    
    @FXML
    private TableView<Customer> tableCustomer;

    @FXML
    private TableColumn<Customer, String> clmnId;

    @FXML
    private TableColumn<Customer, String> clmnName;

    @FXML
    private TableColumn<Customer, Integer> clmnRank;


    private AnchorPane newCustomerPane;
    private AnchorPane profilePane;
    private CustomerProfileController customerProfileController;
    private NewCustomerController newCustomerController;

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {

            //load and get the controller for profile showing pane
            FXMLLoader profileFxmlLoader = new FXMLLoader(getClass().getResource(FXMLS.Admin.Customers.CUSTOMER_PROFILE));
            profilePane = profileFxmlLoader.load();
            profileContainer.getChildren().add(profilePane);
            customerProfileController = profileFxmlLoader.getController();
            customerProfileController.setCustomerController(CustomerController.this);

            //new customer view
            FXMLLoader newCustomerFxmlLoader = new FXMLLoader(getClass().getResource(FXMLS.Admin.Customers.NEW_CUSTOMER));
            newCustomerPane = newCustomerFxmlLoader.load();
            newCustomerController = newCustomerFxmlLoader.getController();
            newCustomerController.setCustomerController(CustomerController.this);

            //add rows
            customers = readRows();

        } catch (IOException e1) {
            e1.printStackTrace();
        }

        clmnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        clmnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        clmnRank.setCellValueFactory(new PropertyValueFactory<>("rank"));
        
        tableCustomer.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.PRIMARY ){
                    customerProfileController.init(tableCustomer.getSelectionModel().getSelectedItem());
                }
            }
            
        });
        
        txtSearch.setOnKeyReleased(keyEvent -> {

            temp.clear();
            for (Customer customer : customers)
                if (customer.getName().toLowerCase().contains(txtSearch.getText().toLowerCase())) temp.add(customer);
            try {
                setRowViews(temp);
                if (txtSearch.getText().isEmpty()) readRows();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        btnNewCustomer.setOnMouseClicked(e -> showNewCustomer());
        btnReload.setOnMouseClicked(e -> {
            try {
                readRows();
            } catch (IOException ex) {
                Logger.getLogger(AdminsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    List<Customer> readRows() throws IOException {

        customers = CustomerQueries.getInstance().getRankedCustomers();
        setRowViews(customers);

        return customers;
    }

    private void setRowViews(List<Customer> customers) throws IOException {
//        rowsContainer.getChildren().clear();
//        for (Customer customer : customers) {
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXMLS.Admin.Customers.CUSTOMER_ROW));
//            AnchorPane row = fxmlLoader.load();
//            fxmlLoader.<CustomerRowController>getController().init(customer, customerProfileController);
//            rowsContainer.getChildren().add(row);
//        }

        tableCustomer.getItems().clear();
        tableCustomer.getItems().addAll(customers);

        if (customers.isEmpty()) {
            profileContainer.getChildren().clear();
        } else {
            profileContainer.getChildren().clear();
            profileContainer.getChildren().add(profilePane);
            customerProfileController.init(customers.get(0));
        }
    }

    private void showNewCustomer() {
        if (!DashboardController.stackPane.getChildren().contains(newCustomerPane)) {
            DashboardController.stackPane.getChildren().add(newCustomerPane);
        }
        newCustomerPane.toFront();
        FadeIn animation = new FadeIn(newCustomerPane);
        animation.setSpeed(3);
        animation.play();
    }

    void showUpdateCustomer(Customer customer) {
        showNewCustomer();
        newCustomerController.initToUpdate(customer);
    }
}
