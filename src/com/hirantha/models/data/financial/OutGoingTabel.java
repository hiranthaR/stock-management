/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hirantha.models.data.financial;

import com.hirantha.models.data.item.BillTableItem;
import com.hirantha.models.data.outgoing.Bill;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Hirantha
 */
public class OutGoingTabel extends Bill{
    
    private SimpleStringProperty dateString;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
    
    public OutGoingTabel(Bill bill) {
        super(bill);
        dateString = new SimpleStringProperty(sdf.format(bill.getDate()));
    }

    public String getDateString() {
        return dateString.get();
    }

    public void setDateString(SimpleStringProperty dateString) {
        this.dateString = dateString;
    }   
}
