package com.hirantha.controllers.admin.outgoing;

import animatefx.animation.FadeIn;
import com.hirantha.admins.CurrentAdmin;
import com.hirantha.admins.Permissions;
import com.hirantha.controllers.admin.admins.AdminsController;
import com.hirantha.quries.outgoing.OutgoingQueries;
import com.hirantha.fxmls.FXMLS;
import com.hirantha.models.data.outgoing.Bill;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class OutGoingController implements Initializable {

    @FXML
    private AnchorPane basePane;

    @FXML
    private ScrollPane customerRowScrollPane;

    @FXML
    private VBox rowsContainer;

    @FXML
    private AnchorPane invoiceContainer;

    @FXML
    private TextField txtSearch;

    @FXML
    private ComboBox<String> cmbSearch;

    @FXML
    private Label btnNewInvoice;

    @FXML
    private Label btnReload;
    
       @FXML
    private TableView<Bill> tableOutgoing;

    @FXML
    private TableColumn<Bill, String> clmnId;

    @FXML
    private TableColumn<Bill, String> clmnName;

    @FXML
    private TableColumn<Bill, Date> clmnDate;

    @FXML
    private TableColumn<Bill, Double> clmnAmount;

    private List<Bill> bills;

    //TODO: implement search
    List<Bill> tempBill = new ArrayList<>(); //for search

    private NewOutGoingInvoiceController newOutGoingInvoiceController;
    private AnchorPane newOutGoingView;

    private AnchorPane outgoingFullViewPane;
    private OutGoingInvoiceFullViewController outGoingInvoiceFullViewController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        clmnId.setCellValueFactory(new PropertyValueFactory<>("_id"));
        clmnName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        clmnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        clmnAmount.setCellValueFactory(new PropertyValueFactory<>("totalBillCost"));
        
           tableOutgoing.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.PRIMARY ){
                    outGoingInvoiceFullViewController.init(tableOutgoing.getSelectionModel().getSelectedItem());
                }
            }
            
        });
        btnReload.setOnMouseClicked(e -> {
            try {
                readRows();
            } catch (IOException ex) {
                Logger.getLogger(AdminsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        cmbSearch.getItems().addAll("Search by customer name", "Search by company Invoice Id");
        cmbSearch.valueProperty().addListener((observableValue, s, t1) -> {
            txtSearch.setPromptText(t1);
            txtSearch.setText("");
            try {
                if (bills != null) {
                    setRowViews(bills);
                }
            } catch (IOException ex) {
                Logger.getLogger(OutGoingController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        cmbSearch.getSelectionModel().select(0);

        txtSearch.setOnKeyReleased(keyEvent -> {

            tempBill.clear();
            for (Bill bill : bills) {
                if (cmbSearch.valueProperty().getValue().equals("Search by customer name")) {
                    if (bill.getCustomerName().toLowerCase().contains(txtSearch.getText().toLowerCase())) {
                        tempBill.add(bill);
                    }
                }

                if (cmbSearch.valueProperty().getValue().equals("Search by company Invoice Id")) {
                    if (bill.get_id().toLowerCase().contains(txtSearch.getText().toLowerCase())) {
                        tempBill.add(bill);
                    }
                }
            }
            try {
                setRowViews(tempBill);
                if (txtSearch.getText().isEmpty()) {
                    setRowViews(bills);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        try {
            FXMLLoader outgoingFullViewFxmlLoader = new FXMLLoader(getClass().getResource(FXMLS.Admin.Outgoing.OUTGOING_BILL_FULL_VIEW));
            outgoingFullViewPane = outgoingFullViewFxmlLoader.load();
            if (Permissions.checkPermission(CurrentAdmin.getInstance().getCurrentAdmin().getLevel(), Permissions.ROLE_ADMIN)) {
                invoiceContainer.getChildren().add(outgoingFullViewPane);
            }
            outGoingInvoiceFullViewController = outgoingFullViewFxmlLoader.getController();
            outGoingInvoiceFullViewController.setOutGoingController(this);

            //new items
            FXMLLoader newInvoiceFxmlLoader = new FXMLLoader(getClass().getResource(FXMLS.Admin.Outgoing.NEW_BILL));
            newOutGoingView = newInvoiceFxmlLoader.load();
            newOutGoingInvoiceController = newInvoiceFxmlLoader.getController();
            newOutGoingInvoiceController.setOutGoingController(OutGoingController.this);

            bills = readRows();

        } catch (IOException e) {
            e.printStackTrace();
        }

        btnNewInvoice.setOnMouseClicked(e -> showNewItemView());
    }

    List<Bill> readRows() throws IOException {

        bills = Permissions.checkPermission(CurrentAdmin.getInstance().getCurrentAdmin().getLevel(), Permissions.ROLE_ADMIN) ? OutgoingQueries.getInstance().getBills() : OutgoingQueries.getInstance().getFBills();
        setRowViews(bills);

        return bills;
    }

    private void setRowViews(List<Bill> bills) throws IOException {
//        rowsContainer.getChildren().clear();
//        for (Bill bill : bills) {
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXMLS.Admin.Outgoing.BILL_ROW));
//            AnchorPane row = fxmlLoader.load();
//            fxmlLoader.<OutGoingInvoiceRowController>getController().init(bill, outGoingInvoiceFullViewController);
//            rowsContainer.getChildren().add(row);
//        }

        tableOutgoing.getItems().clear();
        tableOutgoing.getItems().addAll(bills);

        if (bills.isEmpty()) {
            invoiceContainer.getChildren().clear();
        } else {
            invoiceContainer.getChildren().clear();
            invoiceContainer.getChildren().add(outgoingFullViewPane);
            outGoingInvoiceFullViewController.init(bills.get(0));
        }
    }

    public void showNewItemView() {
        if (!((StackPane) basePane.getParent()).getChildren().contains(newOutGoingView)) {
            ((StackPane) basePane.getParent()).getChildren().add(newOutGoingView);
        }
        newOutGoingView.toFront();
        newOutGoingInvoiceController.loadData();
        FadeIn animation = new FadeIn(newOutGoingView);
        animation.setSpeed(3);
        animation.play();
    }

    void showUpdateInvoice(Bill bill) {
        showNewItemView();
        newOutGoingInvoiceController.initToUpdate(bill);
    }

}
