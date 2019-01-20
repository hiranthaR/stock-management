package com.hirantha.controllers.admin.financial;

import com.hirantha.controllers.admin.admins.AdminsController;
import com.hirantha.controllers.admin.income.InvoiceFullViewController;
import com.hirantha.controllers.admin.outgoing.OutGoingInvoiceFullViewController;
import com.hirantha.fxmls.FXMLS;
import com.hirantha.models.data.financial.IncommingTabel;
import com.hirantha.models.data.financial.OutGoingTabel;
import com.hirantha.models.data.invoice.Invoice;
import com.hirantha.models.data.outgoing.Bill;
import com.hirantha.quries.invoice.InvoiceQueries;
import com.hirantha.quries.outgoing.OutgoingQueries;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FinancialController implements Initializable {

    @FXML
    private TableView<OutGoingTabel> tableOut;

    @FXML
    private TableColumn<OutGoingTabel, String> clmnOutID;

    @FXML
    private TableColumn<OutGoingTabel, String> clmnOutDate;

    @FXML
    private TableColumn<OutGoingTabel, String> clmnOutCustomerName;

    @FXML
    private TableColumn<OutGoingTabel, Double> clmnOutAmount;

    @FXML
    private TableView<IncommingTabel> tableIn;

    @FXML
    private TableColumn<IncommingTabel, String> clmnInID;

    @FXML
    private TableColumn<IncommingTabel, String> clmnInDate;

    @FXML
    private TableColumn<IncommingTabel, String> ClmnInSupplierName;

    @FXML
    private TableColumn<IncommingTabel, Double> clmnInAmount;

    private List<OutGoingTabel> outGoingTabelRows;
    private List<OutGoingTabel> tempOutGoingTabelRows;
    private List<IncommingTabel> incomeTabelRows;
    private List<IncommingTabel> tempIncomeTabelRows;

    @FXML
    private DatePicker dateFrom;

    @FXML
    private DatePicker dateTo;

    @FXML
    private Label lblOut;

    @FXML
    private Label lblIn;

    @FXML
    private Label btnReload;

    @FXML
    private Label lblBalance;

    private double amountOut;
    private double amountIn;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        outGoingTabelRows = new ArrayList<>();
        tempOutGoingTabelRows = new ArrayList<>();
        incomeTabelRows = new ArrayList<>();
        tempIncomeTabelRows = new ArrayList<>();

        clmnOutID.setCellValueFactory(new PropertyValueFactory<>("_id"));
        clmnOutDate.setCellValueFactory(new PropertyValueFactory<>("dateString"));
        clmnOutCustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        clmnOutAmount.setCellValueFactory(new PropertyValueFactory<>("totalBillCost"));

        clmnInID.setCellValueFactory(new PropertyValueFactory<>("_id"));
        clmnInDate.setCellValueFactory(new PropertyValueFactory<>("dateString"));
        ClmnInSupplierName.setCellValueFactory(new PropertyValueFactory<>("name"));
        clmnInAmount.setCellValueFactory(new PropertyValueFactory<>("billCost"));

        OutgoingQueries.getInstance().getBills().forEach(e -> {
            outGoingTabelRows.add(new OutGoingTabel(e));
        });

        InvoiceQueries.getInstance().getInvoices().forEach(e -> {
            incomeTabelRows.add(new IncommingTabel(e));
        });

        dateFrom.getEditor().textProperty().addListener((observableValue, s, t1) -> {
            String date = dateFrom.getValue().format(DateTimeFormatter.ofPattern("dd/MMM/yyyy"));
            dateFrom.getEditor().setText(date);
        });
        dateFrom.setValue(LocalDate.now().minusMonths(1));

        dateTo.getEditor().textProperty().addListener((observableValue, s, t1) -> {
            String date = dateTo.getValue().format(DateTimeFormatter.ofPattern("dd/MMM/yyyy"));
            dateTo.getEditor().setText(date);
        });
        dateTo.setValue(LocalDate.now());

        dateFrom.setOnAction(e -> {
            filter();
        });
        dateTo.setOnAction(e -> {
            filter();
        });

        filter();
        
        tableIn.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2){
                showIncomeDetails((Invoice)tableIn.getSelectionModel().getSelectedItem());
            }
        });
        
        tableOut.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2){
                showOutgoingDetails((Bill)tableOut.getSelectionModel().getSelectedItem());
            }
        });
        
        btnReload.setOnMouseClicked(e -> {
            readRows();
        });
    }
    
    public void readRows(){
        
        outGoingTabelRows.clear();
        incomeTabelRows.clear();
        
        OutgoingQueries.getInstance().getBills().forEach(e -> {
            outGoingTabelRows.add(new OutGoingTabel(e));
        });

        InvoiceQueries.getInstance().getInvoices().forEach(e -> {
            incomeTabelRows.add(new IncommingTabel(e));
        });
    } 

    private void filter() {

        amountIn = 0;
        amountOut = 0;

        tempOutGoingTabelRows.clear();
        tableOut.getItems().clear();

        tempIncomeTabelRows.clear();
        tableIn.getItems().clear();

        outGoingTabelRows.forEach(e -> {
            if (e.getDate().getTime() >= java.sql.Date.valueOf(dateFrom.getValue()).getTime() && e.getDate().getTime() <= java.sql.Date.valueOf(dateTo.getValue()).getTime()) {
                tempOutGoingTabelRows.add(e);
                amountOut += e.getTotalBillCost();
            }
        });

        incomeTabelRows.forEach(e -> {
            if (e.getDate().getTime() >= java.sql.Date.valueOf(dateFrom.getValue()).getTime() && e.getDate().getTime() <= java.sql.Date.valueOf(dateTo.getValue()).getTime()) {
                tempIncomeTabelRows.add(e);
                amountIn += e.getBillCost();
            }
        });

        tableOut.getItems().addAll(tempOutGoingTabelRows);
        tableIn.getItems().addAll(tempIncomeTabelRows);

        lblOut.setText(String.valueOf(amountOut));
        lblIn.setText(String.valueOf(amountIn));
        lblBalance.setText(String.valueOf(amountOut - amountIn));
    }

    void showIncomeDetails(Invoice invoice) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXMLS.Admin.Income.INVOICE_FULL_VIEW));
            Parent root1 = (Parent) fxmlLoader.load();
            InvoiceFullViewController controller = fxmlLoader.getController();
            controller.init(invoice);
            controller.hidePanel();
            Stage stage = new Stage();
            stage.setTitle(invoice.getName() + " @ " + sdf.format(invoice.getDate()));
            stage.setScene(new Scene(root1));
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (IOException e) {
        }
    }
    
    void showOutgoingDetails(Bill bill) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXMLS.Admin.Outgoing.OUTGOING_BILL_FULL_VIEW));
            Parent root1 = (Parent) fxmlLoader.load();
            OutGoingInvoiceFullViewController controller = fxmlLoader.getController();
            controller.init(bill);
            Stage stage = new Stage();
            stage.setTitle(bill.getCustomerName()+ " @ " + sdf.format(bill.getDate()));
            stage.setScene(new Scene(root1));
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (IOException e) {
        }
    }
}
