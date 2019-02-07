package com.hirantha.controllers.admin.admins;

import animatefx.animation.FadeIn;
import com.hirantha.quries.admins.AdminQueries;
import com.hirantha.fxmls.FXMLS;
import com.hirantha.models.data.admins.Admin;
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

public class AdminsController implements Initializable {

    @FXML
    private AnchorPane basePane;

    @FXML
    private VBox rowsContainer;

    @FXML
    private AnchorPane profileContainer;

    @FXML
    private TextField txtSearch;

    @FXML
    private Label btnNewAdmin;
    
    @FXML
    private TableView<Admin> tableAdmins;

    @FXML
    private TableColumn<Admin, String> clmnName;

    @FXML
    private TableColumn<Admin, String> clmnUsername;

    @FXML
    private TableColumn<Admin, String> clmnRole;

   
    @FXML
    private Label btnRelaod;

    private NewAdminController newAdminController;
    private AnchorPane newAdminPane;

    private List<Admin> admins;
    List<Admin> temp = new ArrayList<>();

    private AnchorPane profilePane;
    private AdminProfileController adminProfileController;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        clmnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        clmnUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        clmnRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        tableAdmins.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.PRIMARY ){
                    adminProfileController.init(tableAdmins.getSelectionModel().getSelectedItem());
                }
            }
            
        });
        
        try {
            //load and get the controller for profile showing pane
            FXMLLoader profileFxmlLoader = new FXMLLoader(getClass().getResource(FXMLS.Admin.Admins.ADMIN_PROFILE));
            profilePane = profileFxmlLoader.load();
            profileContainer.getChildren().add(profilePane);
            adminProfileController = profileFxmlLoader.getController();
            adminProfileController.setAdminsController(AdminsController.this);


            //new customer view
            FXMLLoader newAdminFxmlLoader = new FXMLLoader(getClass().getResource(FXMLS.Admin.Admins.NEW_ADMIN));
            newAdminPane = newAdminFxmlLoader.load();
            newAdminController = newAdminFxmlLoader.getController();
            newAdminController.setAdminsController(AdminsController.this);

            readRows();

        } catch (IOException e) {

        }


        btnNewAdmin.setOnMouseClicked(e -> showNewCustomer());
        btnRelaod.setOnMouseClicked(e -> {
            try {
                readRows();
            } catch (IOException ex) {
                Logger.getLogger(AdminsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }

    List<Admin> readRows() throws IOException {

        admins = AdminQueries.getInstance().getAdmins();
        setRowViews(admins);

        return admins;
    }

    private void setRowViews(List<Admin> admins) throws IOException {
//        rowsContainer.getChildren().clear();
//        for (Admin admin : admins) {
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXMLS.Admin.Admins.ADMIN_ROW));
//            AnchorPane row = fxmlLoader.load();
//            fxmlLoader.<AdminRowController>getController().init(admin, adminProfileController);
//            rowsContainer.getChildren().add(row);
//        }

        tableAdmins.getItems().clear();
        tableAdmins.getItems().addAll(admins);

        if (admins.size() == 0) {
            profileContainer.getChildren().clear();
        } else {
            profileContainer.getChildren().clear();
            profileContainer.getChildren().add(profilePane);
            adminProfileController.init(admins.get(0));
        }
    }

    private void showNewCustomer() {
        if (!((StackPane) basePane.getParent()).getChildren().contains(newAdminPane)) {
            ((StackPane) basePane.getParent()).getChildren().add(newAdminPane);
        }
        newAdminPane.toFront();
        FadeIn animation = new FadeIn(newAdminPane);
        animation.setSpeed(3);
        animation.play();
    }

    void showUpdateAdmin(Admin admin) {
        showNewCustomer();
        newAdminController.initToUpdate(admin);
    }
}
