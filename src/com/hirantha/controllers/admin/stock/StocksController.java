package com.hirantha.controllers.admin.stock;

import com.hirantha.admins.CurrentAdmin;
import com.hirantha.admins.Permissions;
import com.hirantha.controllers.admin.admins.AdminsController;
import com.hirantha.models.data.item.BillTableItem;
import com.hirantha.quries.invoice.InvoiceQueries;
import com.hirantha.models.data.item.InvoiceTableItem;
import com.hirantha.models.data.item.StockItem;
import com.hirantha.quries.outgoing.OutgoingQueries;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StocksController implements Initializable {

    static public StocksController instance;

    @FXML
    private TextField txtSearch;

    @FXML
    private ComboBox<String> cmbSearch;

    @FXML
    private TableView<StockItem> table;

    @FXML
    private TableColumn<StockItem, String> clmnCode;

    @FXML
    private TableColumn<StockItem, String> clmnName;

    @FXML
    private TableColumn<StockItem, String> clmnUnit;

    @FXML
    private TableColumn<StockItem, Integer> clmnQuantity;

    Map<String, StockItem> stockItemMap = new HashMap<>();
    Map<String, StockItem> tempStockItemMap = new HashMap<>();

    @FXML
    private Label btnReload;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        btnReload.setOnMouseClicked(e -> {
            setStockData();
        });
        
        instance = this;

        //table configurations
        clmnCode.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        clmnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        clmnUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        clmnQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        new Thread(this::setStockData).start();

        cmbSearch.getItems().addAll("Search by name", "Search by Item Code");
        cmbSearch.getSelectionModel().select(0);
        cmbSearch.valueProperty().addListener((observableValue, s, t1) -> {
            txtSearch.setPromptText(t1);
            txtSearch.setText("");
            table.getItems().clear();
            table.getItems().addAll(stockItemMap.values());
        });

        txtSearch.setOnKeyReleased(keyEvent -> {
            if (txtSearch.getText().isEmpty()) {
                table.getItems().clear();
                table.getItems().addAll(stockItemMap.values());
                return;
            }
            tempStockItemMap.clear();
            for (StockItem item : stockItemMap.values()) {
                if (cmbSearch.valueProperty().getValue().equals("Search by name")) {
                    if (item.getName().toLowerCase().contains(txtSearch.getText().toLowerCase())) {
                        tempStockItemMap.put(item.getItemId(), item);
                    }
                } else if (cmbSearch.valueProperty().getValue().equals("Search by Item Code")) {
                    if (item.getItemId().toLowerCase().contains(txtSearch.getText().toLowerCase())) {
                        tempStockItemMap.put(item.getItemId(), item);
                    }
                }
            }
            table.getItems().clear();
            table.getItems().addAll(tempStockItemMap.values());

        });
    }

    public void setStockData() {
        table.getItems().clear();
        stockItemMap.clear();
        List<InvoiceTableItem> invoiceTableItems = Permissions.checkPermission(CurrentAdmin.getInstance().getCurrentAdmin().getLevel(), Permissions.ROLE_ADMIN) ? InvoiceQueries.getInstance().getInvoiceTableItems() : InvoiceQueries.getInstance().getFInvoiceTableItems();
        for (InvoiceTableItem invoiceTableItem : invoiceTableItems) {
            if (stockItemMap.containsKey(invoiceTableItem.getItemId())) {
                StockItem stockItem = stockItemMap.get(invoiceTableItem.getItemId());
                stockItem.setQuantity(stockItem.getQuantity() + invoiceTableItem.getQuantity());
                stockItemMap.put(invoiceTableItem.getItemId(), stockItem);
            } else {
                stockItemMap.put(invoiceTableItem.getItemId(), new StockItem(invoiceTableItem.getItemId(), invoiceTableItem.getName(), invoiceTableItem.getUnit(), invoiceTableItem.getQuantity()));
            }
        }
        List<BillTableItem> billTableItems = Permissions.checkPermission(CurrentAdmin.getInstance().getCurrentAdmin().getLevel(), Permissions.ROLE_ADMIN) ? OutgoingQueries.getInstance().getOutgoingTableItems() : OutgoingQueries.getInstance().getFOutgoingTableItems();
        for (BillTableItem billTableItem : billTableItems) {
            if (stockItemMap.containsKey(billTableItem.getItemId())) {
                StockItem stockItem = stockItemMap.get(billTableItem.getItemId());
                stockItem.setQuantity(stockItem.getQuantity() - billTableItem.getQuantity());
                stockItemMap.put(billTableItem.getItemId(), stockItem);
            } else {
                stockItemMap.put(billTableItem.getItemId(), new StockItem(billTableItem.getItemId(), billTableItem.getName(), billTableItem.getUnit(), billTableItem.getQuantity() * -1));
            }
        }

        table.getItems().addAll(stockItemMap.values());
    }
}