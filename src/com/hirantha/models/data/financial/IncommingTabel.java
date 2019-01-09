/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hirantha.models.data.financial;

import com.hirantha.models.data.invoice.Invoice;
import java.text.SimpleDateFormat;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Hirantha
 */
public class IncommingTabel extends Invoice{
    
     private SimpleStringProperty dateString;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
   
    public IncommingTabel(Invoice invoice) {
        super(invoice);
        dateString = new SimpleStringProperty(sdf.format(invoice.getDate()));
    }
    
    public String getDateString() {
        return dateString.get();
    }

    public void setDateString(SimpleStringProperty dateString) {
        this.dateString = dateString;
    } 
    
}
