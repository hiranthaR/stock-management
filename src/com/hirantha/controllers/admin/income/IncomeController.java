package com.hirantha.controllers.admin.income;

import animatefx.animation.FadeIn;
import com.hirantha.admins.CurrentAdmin;
import com.hirantha.admins.Permissions;
import com.hirantha.controllers.admin.admins.AdminsController;
import com.hirantha.quries.invoice.InvoiceQueries;
import com.hirantha.fxmls.FXMLS;
import com.hirantha.models.data.invoice.Invoice;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
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

public class IncomeController implements Initializable {

    @FXML
    private AnchorPane basePane;

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

    private List<Invoice> invoices;
    //TODO:Implement search
    List<Invoice> tempInvoices = new ArrayList<>(); //for search

    private NewInvoiceController newInvoiceController;
    private AnchorPane newInvoiceView;

    private AnchorPane invoiceFullViewPane;
    private InvoiceFullViewController invoiceFullViewController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        cmbSearch.getItems().addAll("Search by supplier name", "Search by supplier invoice Id","Search by company Invoice Id");
        cmbSearch.valueProperty().addListener((observableValue, s, t1) -> {
            txtSearch.setPromptText(t1);
            txtSearch.setText("");
        });
        cmbSearch.getSelectionModel().select(0);

        txtSearch.setOnKeyReleased(keyEvent -> {

            tempInvoices.clear();
            for (Invoice invoice: invoices) {
                if (cmbSearch.valueProperty().getValue().equals("Search by supplier name")){
                    if (invoice.getName().toLowerCase().contains(txtSearch.getText().toLowerCase())) tempInvoices.add(invoice);
                }
                if (cmbSearch.valueProperty().getValue().equals("Search by supplier invoice Id")){
                    if (invoice.getInvoiceNumber().toLowerCase().contains(txtSearch.getText().toLowerCase())) tempInvoices.add(invoice);
                }
                if (cmbSearch.valueProperty().getValue().equals("Search by company Invoice Id")){
                    if (invoice.get_id().toLowerCase().contains(txtSearch.getText().toLowerCase())) tempInvoices.add(invoice);
                }
            }
                try {
                setRowViews(tempInvoices);
                if (txtSearch.getText().isEmpty()) setRowViews(invoices);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        try {
            FXMLLoader incomeFullViewFxmlLoader = new FXMLLoader(getClass().getResource(FXMLS.Admin.Income.INVOICE_FULL_VIEW));
            invoiceFullViewPane = incomeFullViewFxmlLoader.load();
            if(Permissions.checkPermission(CurrentAdmin.getInstance().getCurrentAdmin().getLevel(),Permissions.ROLE_ADMIN))invoiceContainer.getChildren().add(invoiceFullViewPane);
            invoiceFullViewController = incomeFullViewFxmlLoader.getController();
            invoiceFullViewController.setIncomeController(IncomeController.this);

            //new items
            FXMLLoader newInvoiceFxmlLoader = new FXMLLoader(getClass().getResource(FXMLS.Admin.Income.NEW_INVOICE));
            newInvoiceView = newInvoiceFxmlLoader.load();
            newInvoiceController = newInvoiceFxmlLoader.getController();
            newInvoiceController.setIncomeController(IncomeController.this);

            invoices = readRows();

        } catch (IOException e) {
            e.printStackTrace();
        }

        btnNewInvoice.setOnMouseClicked(e -> showNewItemView());
        btnReload.setOnMouseClicked(e -> {
            try {
                readRows();
            } catch (IOException ex) {
                Logger.getLogger(AdminsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }

    List<Invoice> readRows() throws IOException {

        invoices = Permissions.checkPermission(CurrentAdmin.getInstance().getCurrentAdmin().getLevel(),Permissions.ROLE_ADMIN) ? InvoiceQueries.getInstance().getInvoices() : InvoiceQueries.getInstance().getFInvoices();
        setRowViews(invoices);

        return invoices;
    }

    private void setRowViews(List<Invoice> invoices) throws IOException {
        rowsContainer.getChildren().clear();
        for (Invoice invoice : invoices) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXMLS.Admin.Income.INVOICE_ROW));
            AnchorPane row = fxmlLoader.load();
            fxmlLoader.<InvoiceRowController>getController().init(invoice, invoiceFullViewController);
            rowsContainer.getChildren().add(row);
        }

        if (invoices.size() == 0) {
            invoiceContainer.getChildren().clear();
        } else {
            invoiceContainer.getChildren().clear();
            invoiceContainer.getChildren().add(invoiceFullViewPane);
            invoiceFullViewController.init(invoices.get(0));
        }
    }

    public void showNewItemView() {
        if (!((StackPane) basePane.getParent()).getChildren().contains(newInvoiceView)) {
            ((StackPane) basePane.getParent()).getChildren().add(newInvoiceView);
        }
        newInvoiceView.toFront();
        newInvoiceController.loadData();
        FadeIn animation = new FadeIn(newInvoiceView);
        animation.setSpeed(3);
        animation.play();
    }

    void showUpdateInvoice(Invoice invoice) {
        showNewItemView();
        newInvoiceController.initToUpdate(invoice);
    }

}
