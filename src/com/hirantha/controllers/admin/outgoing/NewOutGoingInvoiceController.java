package com.hirantha.controllers.admin.outgoing;

import animatefx.animation.FadeOut;
import com.hirantha.admins.CurrentAdmin;
import com.hirantha.admins.Permissions;
import com.hirantha.controllers.admin.income.InvoiceFullViewController;
import com.hirantha.controllers.admin.items.NewItemController;
import com.hirantha.controllers.admin.stock.StocksController;
import com.hirantha.fxmls.FXMLS;
import com.hirantha.quries.DBConnection;
import com.hirantha.quries.admins.AdminQueries;
import com.hirantha.quries.customers.CustomerQueries;
import com.hirantha.quries.items.ItemQueries;
import com.hirantha.quries.outgoing.OutgoingQueries;
import com.hirantha.models.data.admins.Admin;
import com.hirantha.models.data.customer.Customer;
import com.hirantha.models.data.item.BillTableItem;
import com.hirantha.models.data.item.Item;
import com.hirantha.models.data.outgoing.Bill;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.commons.lang3.text.WordUtils;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class NewOutGoingInvoiceController implements Initializable {

    @FXML
    private Label btnCancel;

    @FXML
    private DatePicker dpDate;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtAddress;

    @FXML
    private ComboBox<Item> cmbItemCode;

    @FXML
    private ComboBox<Item> cmbItemName;

    @FXML
    private Label tvUnit;

    @FXML
    private TextField txtQuanitiy;

    @FXML
    private TextField txtCostPerItem;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnNewItem;

    @FXML
    private Button btnRemove;
    
    @FXML
    private ComboBox<Integer> cmbRank;

    @FXML
    private TableView<BillTableItem> table;

    @FXML
    private TableColumn<BillTableItem, String> clmnCode;

    @FXML
    private TableColumn<BillTableItem, String> clmnName;

    @FXML
    private TableColumn<BillTableItem, String> clmnUnit;

    @FXML
    private TableColumn<BillTableItem, Double> clmnCostPerItem;

    @FXML
    private TableColumn<BillTableItem, Integer> clmnQuantity;

    @FXML
    private TableColumn<BillTableItem, Double> clmnDiscount;

    @FXML
    private TableColumn<BillTableItem, Double> clmnTotal;

    @FXML
    private TextField txtBillCost;

    @FXML
    private ComboBox<Admin> cmbPrepared;

    @FXML
    private ComboBox<Admin> cmbchecked;

    @FXML
    private ComboBox<Admin> cmbAccepted;

    @FXML
    private TextField txtVehicleNumber;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnPrint;

    @FXML
    private Button btnPrintAndSave;

    private List<Item> items;
    private Item selectedItem;
    private List<Admin> admins;
    private boolean goingToUpdate;
    private Bill currentBill;
    private OutGoingController outGoingController;
    private Set<Customer> customers;
    private Customer selectedCustomer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> txtName.requestFocus());

        //button go back
        btnCancel.setOnMouseClicked(e -> goBack());

        //loading items
        loadData();

        dpDate.getEditor().textProperty().addListener((observableValue, s, t1) -> {
            String date = dpDate.getValue().format(DateTimeFormatter.ofPattern("dd/MMM/yyyy"));
            dpDate.getEditor().setText(date);
        });
        dpDate.setValue(LocalDate.now());

        txtName.setOnKeyTyped(e -> {
            if (!(Character.isAlphabetic(e.getCharacter().charAt(0)) || Character.isSpaceChar(e.getCharacter().charAt(0)))) {
                e.consume();
            }
        });

        txtName.textProperty().addListener((observableValue, s, t1) -> {
            txtName.setText(WordUtils.capitalize(t1));
            Customer temp = customers.stream().filter(i -> i.getName().equalsIgnoreCase(t1)).findFirst().orElse(null);
            if (temp != null) {
                selectedCustomer = temp;
                txtAddress.setText(temp.getAddress());
                cmbRank.getSelectionModel().select(Integer.valueOf(temp.getRank()));
            } else {
                selectedCustomer = null;
            }
            reArrangeDiscount();
        });

        cmbRank.getItems().add(0);
        cmbRank.getItems().add(1);
        cmbRank.getItems().add(2);
        cmbRank.getItems().add(3);
        cmbRank.getSelectionModel().select(Integer.valueOf(0));
        
        cmbRank.getSelectionModel().selectedItemProperty().addListener((observableValue, item, t1) -> {
            if (t1 != null) {
                if (selectedCustomer != null)
                    selectedCustomer.setRank(t1);
                reArrangeDiscount();
            }
        });
        
        //item code settings
        cmbItemCode.setCellFactory(itemListView -> new ListCell<Item>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    setText(item.getItemCode());
                }
            }
        });
        cmbItemCode.setConverter(new StringConverter<Item>() {
            @Override
            public String toString(Item item) {
                if (item == null) {
                    return null;
                } else {
                    return item.getItemCode();
                }
            }

            @Override
            public Item fromString(String s) {
                return items.stream().filter(i -> i.getItemCode().equalsIgnoreCase(s)).findFirst().orElse(null);
            }
        });

        cmbItemCode.getSelectionModel().selectedItemProperty().addListener((observableValue, item, t1) -> {
            if (t1 != null) {
                selectedItem = t1;
                cmbItemName.setValue(t1);
                tvUnit.setText(t1.getUnit());
                txtCostPerItem.setText(String.valueOf(t1.getMarkedPrice()));
            }
        });
        cmbItemCode.getItems().setAll(items);

        //item name settings
        cmbItemName.setCellFactory(itemListView -> new ListCell<Item>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    setText(item.getName());
                }
            }
        });
        cmbItemName.setConverter(new StringConverter<Item>() {
            @Override
            public String toString(Item item) {
                return item == null ? null : item.getName();
            }

            @Override
            public Item fromString(String s) {
                return items.stream().filter(i -> i.getName().equalsIgnoreCase(s)).findFirst().orElse(null);
            }
        });

        cmbItemName.getSelectionModel().selectedItemProperty().addListener((observableValue, item, t1) -> {
            if (t1 != null) {
                cmbItemCode.setValue(t1);
                tvUnit.setText(t1.getUnit());
                selectedItem = t1;
                txtCostPerItem.setText(String.valueOf(t1.getMarkedPrice()));
            }
        });
        cmbItemName.getItems().setAll(items);

        txtQuanitiy.setOnKeyTyped(e -> {
            if (!Character.isDigit(e.getCharacter().charAt(0))) {
                e.consume();
            }
        });
        txtCostPerItem.setTextFormatter(new TextFormatter<>(change -> {
            if (Pattern.compile("-?\\d*(\\.\\d{0,2})?").matcher(change.getControlNewText()).matches()) {
                return change;
            } else {
                return null;
            }
        }));

        //table configurations
        clmnCode.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        clmnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        clmnUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        clmnQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        clmnCostPerItem.setCellValueFactory(new PropertyValueFactory<>("costPerItem"));
        clmnDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));
        clmnTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        btnAdd.setOnMouseClicked(e -> addItemToTable());
        btnAdd.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                addItemToTable();
            }
        });

        btnRemove.setOnMouseClicked(e -> {
            BillTableItem billTableItem = table.getSelectionModel().getSelectedItem();
            table.getItems().remove(table.getSelectionModel().getSelectedIndex());

            if (txtBillCost.getText().isEmpty() || txtBillCost.getText().equals("0")) {
                txtBillCost.setText(String.valueOf(billTableItem.getCostPerItem() * billTableItem.getQuantity()));
            } else {
                txtBillCost.setText(String.valueOf(Double.parseDouble(txtBillCost.getText()) + billTableItem.getDiscount() - billTableItem.getCostPerItem() * billTableItem.getQuantity()));
            }

            if (Double.parseDouble(txtBillCost.getText()) < 0) {
                txtBillCost.setText("0");
            }
        });

        if (!Permissions.checkPermission(CurrentAdmin.getInstance().getCurrentAdmin().getLevel(), Permissions.ADD_ITEM)) {
            btnNewItem.setVisible(false);
        }
        
        btnNewItem.setOnAction(e ->{
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXMLS.Admin.Items.NEW_ITEM_VIEW));
                Parent root1 = (Parent) fxmlLoader.load();
                NewItemController controller = fxmlLoader.getController();
            controller.hidePanel();
                Stage stage = new Stage();
//            stage.setTitle(invoice.getName() + " @ " + sdf.format(invoice.getDate()));
                stage.setScene(new Scene(root1));
                stage.initStyle(StageStyle.UTILITY);
                stage.show();
                System.out.println("Here");
            } catch (IOException r) {
            }
        });
        

        txtBillCost.setTextFormatter(new TextFormatter<>(change -> {
            if (Pattern.compile("-?\\d*(\\.\\d{0,2})?").matcher(change.getControlNewText()).matches()) {
                return change;
            } else {
                return null;
            }
        }));

        cmbPrepared.setCellFactory(adminListView -> new ListCell<Admin>() {
            @Override
            protected void updateItem(Admin item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        cmbPrepared.setItems(FXCollections.observableArrayList(admins));
        cmbPrepared.setConverter(new StringConverter<Admin>() {
            @Override
            public String toString(Admin admin) {
                if (admin == null) {
                    return null;
                } else {
                    return admin.getName();
                }
            }

            @Override
            public Admin fromString(String s) {
                return null;
            }
        });

        cmbAccepted.setCellFactory(adminListView -> new ListCell<Admin>() {
            @Override
            protected void updateItem(Admin item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        cmbAccepted.setItems(FXCollections.observableArrayList(admins));
        cmbAccepted.setConverter(new StringConverter<Admin>() {
            @Override
            public String toString(Admin admin) {
                if (admin == null) {
                    return null;
                } else {
                    return admin.getName();
                }
            }

            @Override
            public Admin fromString(String s) {
                return null;
            }
        });

        cmbchecked.setCellFactory(adminListView -> new ListCell<Admin>() {
            @Override
            protected void updateItem(Admin item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        cmbchecked.setItems(FXCollections.observableArrayList(admins));
        cmbchecked.setConverter(new StringConverter<Admin>() {
            @Override
            public String toString(Admin admin) {
                if (admin == null) {
                    return null;
                } else {
                    return admin.getName();
                }
            }

            @Override
            public Admin fromString(String s) {
                return null;
            }
        });

        cmbchecked.setValue(CurrentAdmin.getInstance().getCurrentAdmin());
        cmbPrepared.setValue(CurrentAdmin.getInstance().getCurrentAdmin());
        cmbAccepted.setValue(CurrentAdmin.getInstance().getCurrentAdmin());

        btnSave.setOnMouseClicked(e -> save());

        btnPrint.setOnMouseClicked(e -> printReport(Integer.parseInt(currentBill.get_id())));

        if (currentBill == null) {
            btnPrint.setVisible(false);
        }

        btnPrintAndSave.setOnMouseClicked(e -> {
            if (save()) {
                printReport(Integer.parseInt(currentBill.get_id()));
            }
        });
    }

    private void printReport(int id) {
        String path = "./reports/outgoing.jrxml";
        try {
            JasperReport report = JasperCompileManager.compileReport(path);
            Map<String, Object> params = new HashMap();
            params.put("id", id);
            JasperPrint print = JasperFillManager.fillReport(report, params, DBConnection.getConnection());
            JasperViewer.viewReport(print, false);
        } catch (JRException e1) {
            e1.printStackTrace();
        }
    }

    private boolean save() {
        if (checkBillProperties()) {
            if (goingToUpdate) {
                OutgoingQueries.getInstance().updateInvoice(createBill());
                goingToUpdate = false;
            } else {
                currentBill = OutgoingQueries.getInstance().insertBill(createBill());
            }
            try {
                outGoingController.readRows();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            goBack();
            StocksController.instance.setStockData();
            return true;
        }
        return false;
    }

    private boolean checkTableItemProperties() {
        boolean status = true;
        if (selectedItem == null || cmbItemCode.getValue() == null || cmbItemName.getValue() == null) {
            new Alert(Alert.AlertType.INFORMATION, "Select an Item!").showAndWait();
            status = false;
        }

        if (txtQuanitiy.getText().isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Enter a Quantity!").showAndWait();
            status = false;
        }

        if (txtCostPerItem.getText().isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Cost per item is empty!").showAndWait();
            status = false;
        }
        return status;
    }

    private void reArrangeDiscount() {
        double totalBill = 0;
        List<BillTableItem> billTableItems = new ArrayList<>(table.getItems());
        for (BillTableItem billTableItem : billTableItems) {
            System.out.println(billTableItem.getTotal());
            billTableItem.setDiscount(discount(billTableItem.getCostPerItem()) * billTableItem.getQuantity());
            billTableItem.setTotal(billTableItem.getTotal());
            totalBill += billTableItem.getCostPerItem() * billTableItem.getQuantity() - billTableItem.getDiscount();
        }
        txtBillCost.setText(String.valueOf(totalBill));
    }

    private void addItemToTable() {
        if (checkTableItemProperties()) {
            BillTableItem billTableItem;
            for (int i = 0; i < table.getItems().size(); i++) {
                BillTableItem tableItem = table.getItems().get(i);
                if (selectedItem.getItemCode().equals(tableItem.getItemId()) && tableItem.getCostPerItem() == Double.parseDouble(txtCostPerItem.getText())) {
                    billTableItem = tableItem;
                    billTableItem.setQuantity(billTableItem.getQuantity() + Integer.parseInt(txtQuanitiy.getText()));
                    double discount = discount(billTableItem.getCostPerItem()) * billTableItem.getQuantity();
                    billTableItem.setDiscount(discount);
                    table.getItems().remove(i);
                    table.getItems().add(billTableItem);
                    txtBillCost.setText(String.valueOf(Double.parseDouble(txtBillCost.getText()) + billTableItem.getCostPerItem() * Integer.parseInt(txtQuanitiy.getText())));
                    clearTableFields();
                    return;
                }
            }
            billTableItem = createTableItem();
            table.getItems().add(billTableItem);
            clearTableFields();

            if (txtBillCost.getText().isEmpty() || txtBillCost.getText().equals("0")) {
                txtBillCost.setText(String.valueOf((billTableItem.getCostPerItem() * billTableItem.getQuantity()) - billTableItem.getDiscount()));
            } else {
                txtBillCost.setText(String.valueOf(Double.parseDouble(txtBillCost.getText()) + (billTableItem.getCostPerItem() * billTableItem.getQuantity()) - billTableItem.getDiscount()));
            }

        }
    }

    private void clearTableFields() {
        cmbItemCode.requestFocus();
        cmbItemCode.setValue(null);
        cmbItemName.setValue(null);
        txtQuanitiy.setText("");
        txtCostPerItem.setText("");
        tvUnit.setText("");
    }

    private BillTableItem createTableItem() {
        String itemCode = selectedItem.getItemCode();
        String itemName = selectedItem.getName();
        String unit = selectedItem.getUnit();
        int quantity = Integer.parseInt(txtQuanitiy.getText());
        double costPerItem = Double.parseDouble(txtCostPerItem.getText());
        boolean percentage = selectedItem.isPercentage();
        double discount = discount(costPerItem) * quantity;
        discount = (costPerItem * quantity) - discount < 0 ? 0 : discount;

        return new BillTableItem(itemCode, itemName, unit, quantity, costPerItem, discount, percentage);
    }

    private double discount(double itemCost) {
        double discount;
        if (selectedItem == null) {
            return 0;
        }
        if (selectedItem.isPercentage()) {
            discount = itemCost * selectedItem.getDiscountAccoringToRank(cmbRank.getValue()) / 100;
        } else {
            discount = selectedItem.getDiscountAccoringToRank(cmbRank.getValue());
        }
        return discount;
    }

    private void goBack() {
        btnPrint.setVisible(false);
        FadeOut fadeOut = new FadeOut(btnCancel.getParent());
        fadeOut.setSpeed(3);
        fadeOut.play();
        clearFields();
        btnCancel.getParent().toBack();
//        fadeOut.getTimeline().setOnFinished(ex -> {
//            ((StackPane) btnCancel.getParent().getParent()).getChildren().remove(btnCancel.getParent());
//        });
        try {
            outGoingController.readRows();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadData() {
        items = ItemQueries.getInstance().getItems();
        TextFields.bindAutoCompletion(cmbItemCode.getEditor(), cmbItemCode.getItems().stream().map(Item::getItemCode).collect(Collectors.toList()));
        cmbItemName.getItems().clear();
        cmbItemName.getItems().addAll(items);
        cmbItemCode.getItems().clear();
        cmbItemCode.getItems().addAll(items);
        admins = AdminQueries.getInstance().getAdmins();
        customers = new HashSet<>(CustomerQueries.getInstance().getAllCustomers());
        TextFields.bindAutoCompletion(txtName, customers.stream().map(Customer::getName).collect(Collectors.toList()));
    }

    public NewOutGoingInvoiceController setOutGoingController(OutGoingController outGoingController) {
        this.outGoingController = outGoingController;
        return this;
    }

    private void clearFields() {
        dpDate.setValue(LocalDate.now());
        txtName.setText("");
        txtAddress.setText("");
        cmbRank.getSelectionModel().select(Integer.valueOf(0));
        cmbItemCode.setValue(null);
        cmbItemName.setValue(null);
        txtQuanitiy.setText("");
        tvUnit.setText("");
        txtCostPerItem.setText("");
        table.getItems().clear();
        txtBillCost.setText("0");
        cmbPrepared.setValue(CurrentAdmin.getInstance().getCurrentAdmin());
        cmbAccepted.setValue(CurrentAdmin.getInstance().getCurrentAdmin());
        cmbchecked.setValue(CurrentAdmin.getInstance().getCurrentAdmin());
        txtVehicleNumber.setText("");
    }

    Alert alert = new Alert(Alert.AlertType.INFORMATION);

    private boolean checkBillProperties() {
        boolean status = true;
        if (table.getItems().size() < 1) {
            alert.setContentText("No items added yet!");
            alert.showAndWait();
            status = false;
        }

        if (txtBillCost.getText().isEmpty()) {
            alert.setContentText("Total bill cost is empty!");
            alert.showAndWait();
            status = false;
        }

        return status;
    }

    private Bill createBill() {

        if (selectedCustomer == null) {
            selectedCustomer = CustomerQueries.getInstance().insertCustomer(new Customer("0", true, "Mr.", txtName.getText(), txtAddress.getText(), "", 0), false);
        }

        Date date = java.sql.Date.valueOf(dpDate.getValue());
        String customerName = txtName.getText();
        String customerAddress = txtAddress.getText();
        int customerRank = cmbRank.getValue();
        String customerId = selectedCustomer.getId();
        List<BillTableItem> tableItems = new ArrayList<>(table.getItems());
        double billCost = Double.parseDouble(txtBillCost.getText());

        String preparedAdminName = cmbPrepared.getValue().getName();
        String preparedAdminId = cmbPrepared.getValue().getId();
        String acceptedAdminName = cmbAccepted.getValue().getName();
        String acceptedAdminId = cmbAccepted.getValue().getId();
        String checkedAdminName = cmbchecked.getValue().getName();
        String checkedAdminId = cmbchecked.getValue().getId();
        String vehicleNumber = txtVehicleNumber.getText();

        return new Bill(goingToUpdate ? currentBill.get_id() : "", date, customerId, customerName, customerAddress, customerRank, tableItems, billCost, preparedAdminName, preparedAdminId, checkedAdminName, checkedAdminId, acceptedAdminName, acceptedAdminId, vehicleNumber);
    }

    public void initToUpdate(Bill bill) {
        btnPrint.setVisible(true);
        this.currentBill = bill;
        this.goingToUpdate = true;
        dpDate.setValue(new java.sql.Date(bill.getDate().getTime()).toLocalDate());
        txtName.setText(bill.getCustomerName());
        txtAddress.setText(bill.getCustomerAddress());
        cmbRank.getSelectionModel().select(Integer.valueOf(bill.getCustomerRank()));
        selectedCustomer = CustomerQueries.getInstance().getCustomer(bill.getCustomerId());
        table.getItems().addAll(bill.getTableItems());
        txtBillCost.setText(String.valueOf(bill.getTotalBillCost()));
        cmbPrepared.setValue(AdminQueries.getInstance().getAdmin(bill.getPreparedAdminId()));
        cmbAccepted.setValue(AdminQueries.getInstance().getAdmin(bill.getAcceptedAdminId()));
        cmbchecked.setValue(AdminQueries.getInstance().getAdmin(bill.getCheckedAdminId()));
        txtVehicleNumber.setText(bill.getVehicleNumber());
    }
}
