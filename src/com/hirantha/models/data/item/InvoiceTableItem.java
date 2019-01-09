package com.hirantha.models.data.item;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Objects;

public class InvoiceTableItem {

    private SimpleStringProperty itemId; // == item code
    private String name;
    private SimpleStringProperty unit;
    private SimpleIntegerProperty quantity;
    private SimpleDoubleProperty costPerItem;
    private SimpleDoubleProperty total;

    public InvoiceTableItem(String itemId, String name, String unit, int quantity, double costPerItem) {
        this.itemId = new SimpleStringProperty(itemId);
//        this.name = new SimpleStringProperty(name);
        this.name = name;
        this.unit = new SimpleStringProperty(unit);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.costPerItem = new SimpleDoubleProperty(costPerItem);
        this.total = new SimpleDoubleProperty(costPerItem * quantity);
    }

    public String getItemId() {
        return itemId.get();
    }

    public SimpleStringProperty itemIdProperty() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId.set(itemId);
    }

    public String getName() {
//        return name.get();
        return name;
    }

//    public SimpleStringProperty nameProperty() {
//        return name;
//    }

//    public void setName(String name) {
//        this.name.set(name);
//    }

    public String getUnit() {
        return unit.get();
    }

    public SimpleStringProperty unitProperty() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit.set(unit);
    }

    public int getQuantity() {
        return quantity.get();
    }

    public SimpleIntegerProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.total.set(this.costPerItem.get() * quantity);
        this.quantity.set(quantity);
    }

    public double getCostPerItem() {
        return costPerItem.get();
    }
    
    public double getTotal() {
        return total.get();
    }
    
    public void setTotal() {
        this.total.set(costPerItem.get() * quantity.get());
    }

    public SimpleDoubleProperty costPerItemProperty() {
        return costPerItem;
    }

    public void setCostPerItem(double costPerItem) {
        this.costPerItem.set(costPerItem);
        setTotal();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvoiceTableItem invoiceTableItem = (InvoiceTableItem) o;
        return Objects.equals(itemId, invoiceTableItem.itemId);
    }

}
