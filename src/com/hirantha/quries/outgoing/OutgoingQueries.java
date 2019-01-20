package com.hirantha.quries.outgoing;

import com.hirantha.models.data.item.BillTableItem;
import com.hirantha.models.data.outgoing.Bill;
import com.hirantha.quries.DBConnection;
import com.hirantha.quries.FDBConnection;
import com.hirantha.quries.admins.AdminQueries;
import com.hirantha.quries.customers.CustomerQueries;
import com.hirantha.quries.items.ItemQueries;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.hirantha.quries.items.ItemQueries.ITEM_ID;

public class OutgoingQueries {

    private String OUTGOINGS_TABLE = "outgoings";

    private static OutgoingQueries instance;

    private OutgoingQueries() {
    }

    public static OutgoingQueries getInstance() {
        if (instance == null) instance = new OutgoingQueries();
        return instance;
    }

    private String F = "* 0.8 ";
    private String OUTGOING_ID = "outgoing_id";
    private String CUSTOMER_ID = "customer_id";
    private String DATE = "date";
    private String OUTGOINGS_ITEMS_TABLE = "outgoing_items";
    private String TOTAL_BILL_COST = "total_bill_cost";
    private String ITEM_QUANTITY = "quantity";
    private String COST_PER_ITEM = "cost_per_items";
    private String DISCOUNT = "discount";
    private String PERCENTAGE = "percentage";
    private String PREPARED_ADMIN_ID = "prepared_admin_id";
    private String ACCEPTED_ADMIN_ID = "accepted_admin_id";
    private String CHECKED_ADMIN_ID = "checked_admin_id";
    private String VEHICLE_NO = "vehicle";


    public Bill insertBill(Bill bill) {

        String query = "INSERT INTO " + OUTGOINGS_TABLE + " VALUES (" +
                "0," +
                bill.getCustomerId() + "," +
                "?," +
                bill.getTotalBillCost() + "," +
                bill.getPreparedAdminId() + "," +
                bill.getAcceptedAdminId() + "," +
                bill.getCheckedAdminId() + "," +
                "'" + bill.getVehicleNumber() + "');";

        try {
            PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setDate(1, new java.sql.Date(bill.getDate().getTime()));
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                bill.set_id(String.valueOf(resultSet.getInt(1)));
            }
            insertTableItems(bill);
        } catch (Exception e) {
            e.printStackTrace();
        }
        insertFBill(bill);
        return bill;
    }

    public void insertFBill(Bill bill) {

        String query = "INSERT INTO " + OUTGOINGS_TABLE + " VALUES (" +
                "0," +
                bill.getCustomerId() + "," +
                "?," +
                bill.getTotalBillCost() + F + "," +
                bill.getPreparedAdminId() + "," +
                bill.getAcceptedAdminId() + "," +
                bill.getCheckedAdminId() + "," +
                "'" + bill.getVehicleNumber() + "');";

        try {
            PreparedStatement preparedStatement = FDBConnection.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setDate(1, new java.sql.Date(bill.getDate().getTime()));
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                bill.set_id(String.valueOf(resultSet.getInt(1)));
            }
            insertFTableItems(bill);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertTableItems(Bill bill) {

        StringBuilder query = new StringBuilder("INSERT INTO " + OUTGOINGS_ITEMS_TABLE + " VALUES ");

        for (int i = 0; i < bill.getTableItems().size(); i++) {
            BillTableItem billTableItem = bill.getTableItems().get(i);
            String comma = i == bill.getTableItems().size() - 1 ? ";" : ",";
            query.append("(")
                    .append(billTableItem.getItemId()).append(",")
                    .append(bill.get_id()).append(",")
                    .append(billTableItem.getQuantity()).append(",")
                    .append(billTableItem.isPercentage()).append(",")
                    .append(billTableItem.getDiscount()).append(",")
                    .append(billTableItem.getCostPerItem()).append(")").append(comma);
        }

        DBConnection.executeQuery(query.toString(), false);
    }

    private void insertFTableItems(Bill bill) {

        StringBuilder query = new StringBuilder("INSERT INTO " + OUTGOINGS_ITEMS_TABLE + " VALUES ");

        for (int i = 0; i < bill.getTableItems().size(); i++) {
            BillTableItem billTableItem = bill.getTableItems().get(i);
            String comma = i == bill.getTableItems().size() - 1 ? ";" : ",";
            query.append("(")
                    .append(billTableItem.getItemId()).append(",")
                    .append(bill.get_id()).append(",")
                    .append(billTableItem.getQuantity()).append(F).append(",")
                    .append(billTableItem.isPercentage()).append(",")
                    .append(billTableItem.getDiscount()).append(",")
                    .append(billTableItem.getCostPerItem()).append(")").append(comma);
        }

        FDBConnection.executeQuery(query.toString(), false);
    }
    public void updateInvoice(Bill bill) {

        StringBuilder query = new StringBuilder("DELETE FROM ")
                .append(OUTGOINGS_ITEMS_TABLE)
                .append(" WHERE ")
                .append(OUTGOING_ID)
                .append("=")
                .append(bill.get_id())
                .append(";");

        DBConnection.executeQuery(query.toString(), false);

        //update items - delete all and insert again
        query = new StringBuilder("INSERT INTO " + OUTGOINGS_ITEMS_TABLE
                + " VALUES ");

        for (int i = 0; i < bill.getTableItems().size(); i++) {
            BillTableItem billTableItem = bill.getTableItems().get(i);
            String comma = i == bill.getTableItems().size() - 1 ? ";" : ",";
            query.append("(")
                    .append(billTableItem.getItemId()).append(",")
                    .append(bill.get_id()).append(",")
                    .append(billTableItem.getQuantity()).append(",")
                    .append(billTableItem.isPercentage()).append(",")
                    .append(billTableItem.getDiscount()).append(",")
                    .append(billTableItem.getCostPerItem()).append(")").append(comma);
        }

        DBConnection.executeQuery(query.toString(), false);
        //item update done

        query = new StringBuilder("UPDATE " + OUTGOINGS_TABLE + " SET ")
                .append(CUSTOMER_ID).append("=").append(bill.getCustomerId()).append(",")
                .append(DATE).append("=?,")
                .append(TOTAL_BILL_COST).append("=").append(bill.getTotalBillCost()).append(",")
                .append(PREPARED_ADMIN_ID).append("=").append(bill.getPreparedAdminId()).append(",")
                .append(ACCEPTED_ADMIN_ID).append("=").append(bill.getAcceptedAdminId()).append(",")
                .append(CHECKED_ADMIN_ID).append("=").append(bill.getCheckedAdminId()).append(",")
                .append(VEHICLE_NO).append("='").append(bill.getVehicleNumber()).append("'")
                .append(" WHERE ").append(OUTGOING_ID).append("=").append(bill.get_id()).append(";");

        try {
            PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query.toString());
            preparedStatement.setDate(1, new java.sql.Date(bill.getDate().getTime()));
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateFInvoice(bill);
    }

    public void updateFInvoice(Bill bill) {

        StringBuilder query = new StringBuilder("DELETE FROM ")
                .append(OUTGOINGS_ITEMS_TABLE)
                .append(" WHERE ")
                .append(OUTGOING_ID)
                .append("=")
                .append(bill.get_id())
                .append(";");

        FDBConnection.executeQuery(query.toString(), false);

        //update items - delete all and insert again
        query = new StringBuilder("INSERT INTO " + OUTGOINGS_ITEMS_TABLE
                + " VALUES ");

        for (int i = 0; i < bill.getTableItems().size(); i++) {
            BillTableItem billTableItem = bill.getTableItems().get(i);
            String comma = i == bill.getTableItems().size() - 1 ? ";" : ",";
            query.append("(")
                    .append(billTableItem.getItemId()).append(",")
                    .append(bill.get_id()).append(",")
                    .append(billTableItem.getQuantity()).append(F).append(",")
                    .append(billTableItem.isPercentage()).append(",")
                    .append(billTableItem.getDiscount()).append(F).append(",")
                    .append(billTableItem.getCostPerItem()).append(")").append(comma);
        }

        FDBConnection.executeQuery(query.toString(), false);
        //item update done

        query = new StringBuilder("UPDATE " + OUTGOINGS_TABLE + " SET ")
                .append(CUSTOMER_ID).append("=").append(bill.getCustomerId()).append(",")
                .append(DATE).append("=?,")
                .append(TOTAL_BILL_COST).append("=").append(bill.getTotalBillCost()).append(F).append(",")
                .append(PREPARED_ADMIN_ID).append("=").append(bill.getPreparedAdminId()).append(",")
                .append(ACCEPTED_ADMIN_ID).append("=").append(bill.getAcceptedAdminId()).append(",")
                .append(CHECKED_ADMIN_ID).append("=").append(bill.getCheckedAdminId()).append(",")
                .append(VEHICLE_NO).append("='").append(bill.getVehicleNumber()).append("'")
                .append(" WHERE ").append(OUTGOING_ID).append("=").append(bill.get_id()).append(";");

        try {
            PreparedStatement preparedStatement = FDBConnection.getConnection().prepareStatement(query.toString());
            preparedStatement.setDate(1, new java.sql.Date(bill.getDate().getTime()));
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<Bill> getBills() {
        List<Bill> bills = new ArrayList<>();


        String query = "SELECT ot.*,atp." + AdminQueries.NAME + " AS pan,ata." + AdminQueries.NAME + " AS aan,atc." + AdminQueries.NAME + " AS can,ct.* FROM " + OUTGOINGS_TABLE + " ot" +
                " INNER JOIN " + AdminQueries.ADMINS_TABLE + " atp ON atp." + AdminQueries.ADMIN_ID + "=ot." + PREPARED_ADMIN_ID +
                " INNER JOIN " + AdminQueries.ADMINS_TABLE + " ata ON ata." + AdminQueries.ADMIN_ID + "=ot." + ACCEPTED_ADMIN_ID +
                " INNER JOIN " + AdminQueries.ADMINS_TABLE + " atc ON atc." + AdminQueries.ADMIN_ID + "=ot." + CHECKED_ADMIN_ID +
                " INNER JOIN " + CustomerQueries.CUSTOMER_TABLE + " ct ON ct." + CUSTOMER_ID + "=ot." + CUSTOMER_ID + " ORDER BY ot." + OUTGOING_ID + " DESC;";

        ResultSet outgoingResultSet = DBConnection.executeQuery(query);

        try {
            if (outgoingResultSet != null) {
                while (outgoingResultSet.next()) {

                    int outgoingId = outgoingResultSet.getInt(OUTGOING_ID);
                    String customerID = outgoingResultSet.getString(CUSTOMER_ID);
                    Date date = outgoingResultSet.getDate(DATE);
                    String customerName = outgoingResultSet.getString(CustomerQueries.NAME);
                    String customerAddress = outgoingResultSet.getString(CustomerQueries.ADDRESS);
                    int customerRank = outgoingResultSet.getInt(CustomerQueries.RANK);

                    double totalBillCost = outgoingResultSet.getDouble(TOTAL_BILL_COST);
                    int preparedAdminId = outgoingResultSet.getInt(PREPARED_ADMIN_ID);
                    int acceptedAdminId = outgoingResultSet.getInt(ACCEPTED_ADMIN_ID);
                    int checkedAdminId = outgoingResultSet.getInt(CHECKED_ADMIN_ID);
                    String preparedAdminName = outgoingResultSet.getString("pan");
                    String acceptedAdminName = outgoingResultSet.getString("aan");
                    String checkedAdminName = outgoingResultSet.getString("can");
                    String vehicle = outgoingResultSet.getString(VEHICLE_NO);

                    List<BillTableItem> items = getBillTableItems(outgoingId);

                    bills.add(new Bill(String.valueOf(outgoingId), date, customerID, customerName, customerAddress, customerRank, items, totalBillCost, preparedAdminName, String.valueOf(preparedAdminId), checkedAdminName, String.valueOf(checkedAdminId), acceptedAdminName, String.valueOf(acceptedAdminId), vehicle));

                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return bills;
    }

    public List<Bill> getFBills() {
        List<Bill> bills = new ArrayList<>();


        String query = "SELECT ot.*,atp." + AdminQueries.NAME + " AS pan,ata." + AdminQueries.NAME + " AS aan,atc." + AdminQueries.NAME + " AS can,ct.* FROM " + OUTGOINGS_TABLE + " ot" +
                " INNER JOIN " + AdminQueries.ADMINS_TABLE + " atp ON atp." + AdminQueries.ADMIN_ID + "=ot." + PREPARED_ADMIN_ID +
                " INNER JOIN " + AdminQueries.ADMINS_TABLE + " ata ON ata." + AdminQueries.ADMIN_ID + "=ot." + ACCEPTED_ADMIN_ID +
                " INNER JOIN " + AdminQueries.ADMINS_TABLE + " atc ON atc." + AdminQueries.ADMIN_ID + "=ot." + CHECKED_ADMIN_ID +
                " INNER JOIN " + CustomerQueries.CUSTOMER_TABLE + " ct ON ct." + CUSTOMER_ID + "=ot." + CUSTOMER_ID + " ORDER BY ot." + OUTGOING_ID + " DESC;";

        ResultSet outgoingResultSet = FDBConnection.executeQuery(query);

        try {
            if (outgoingResultSet != null) {
                while (outgoingResultSet.next()) {

                    int outgoingId = outgoingResultSet.getInt(OUTGOING_ID);
                    String customerID = outgoingResultSet.getString(CUSTOMER_ID);
                    Date date = outgoingResultSet.getDate(DATE);
                    String customerName = outgoingResultSet.getString(CustomerQueries.NAME);
                    String customerAddress = outgoingResultSet.getString(CustomerQueries.ADDRESS);
                    int customerRank = outgoingResultSet.getInt(CustomerQueries.RANK);

                    double totalBillCost = outgoingResultSet.getDouble(TOTAL_BILL_COST);
                    int preparedAdminId = outgoingResultSet.getInt(PREPARED_ADMIN_ID);
                    int acceptedAdminId = outgoingResultSet.getInt(ACCEPTED_ADMIN_ID);
                    int checkedAdminId = outgoingResultSet.getInt(CHECKED_ADMIN_ID);
                    String preparedAdminName = outgoingResultSet.getString("pan");
                    String acceptedAdminName = outgoingResultSet.getString("aan");
                    String checkedAdminName = outgoingResultSet.getString("can");
                    String vehicle = outgoingResultSet.getString(VEHICLE_NO);

                    List<BillTableItem> items = getFBillTableItems(outgoingId);

                    bills.add(new Bill(String.valueOf(outgoingId), date, customerID, customerName, customerAddress, customerRank, items, totalBillCost, preparedAdminName, String.valueOf(preparedAdminId), checkedAdminName, String.valueOf(checkedAdminId), acceptedAdminName, String.valueOf(acceptedAdminId), vehicle));

                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return bills;
    }

    public List<BillTableItem> getBillTableItems(int outgoingId) {
        List<BillTableItem> items = new ArrayList<>();

        String query = "SELECT * FROM " + OUTGOINGS_ITEMS_TABLE + " iit INNER JOIN " + ItemQueries.ITEMS_TABLE + " it" +
                " ON iit." + ItemQueries.ITEM_ID + "=it." + ITEM_ID +
                " WHERE iit." + OUTGOING_ID + "=" + outgoingId + ";";


        ResultSet resultSet = DBConnection.executeQuery(query);
        try {
            if (resultSet != null) {
                while (resultSet.next()) {
                    String itemId = resultSet.getString(ITEM_ID);
                    String name = resultSet.getString(ItemQueries.NAME);
                    String unit = resultSet.getString(ItemQueries.UNIT);
                    int quantity = resultSet.getInt(ITEM_QUANTITY);
                    double costPerItem = resultSet.getInt(COST_PER_ITEM);
                    double discount = resultSet.getInt(DISCOUNT);
                    boolean percentage = resultSet.getBoolean(PERCENTAGE);
                    items.add(new BillTableItem(itemId, name, unit, quantity, costPerItem, discount, percentage));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    public List<BillTableItem> getFBillTableItems(int outgoingId) {
        List<BillTableItem> items = new ArrayList<>();

        String query = "SELECT * FROM " + OUTGOINGS_ITEMS_TABLE + " iit INNER JOIN " + ItemQueries.ITEMS_TABLE + " it" +
                " ON iit." + ItemQueries.ITEM_ID + "=it." + ITEM_ID +
                " WHERE iit." + OUTGOING_ID + "=" + outgoingId + ";";


        ResultSet resultSet = FDBConnection.executeQuery(query);
        try {
            if (resultSet != null) {
                while (resultSet.next()) {
                    String itemId = resultSet.getString(ITEM_ID);
                    String name = resultSet.getString(ItemQueries.NAME);
                    String unit = resultSet.getString(ItemQueries.UNIT);
                    int quantity = resultSet.getInt(ITEM_QUANTITY);
                    double costPerItem = resultSet.getInt(COST_PER_ITEM);
                    double discount = resultSet.getInt(DISCOUNT);
                    boolean percentage = resultSet.getBoolean(PERCENTAGE);
                    items.add(new BillTableItem(itemId, name, unit, quantity, costPerItem, discount, percentage));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }
    public List<BillTableItem> getOutgoingTableItems() {
        List<BillTableItem> items = new ArrayList<>();

        String query = "SELECT * FROM " + OUTGOINGS_ITEMS_TABLE + " iit INNER JOIN " + ItemQueries.ITEMS_TABLE + " it" +
                " ON iit." + ItemQueries.ITEM_ID + "=it." + ITEM_ID + ";";

        ResultSet resultSet = DBConnection.executeQuery(query);
        try {
            if (resultSet != null) {
                while (resultSet.next()) {
                    String itemId = resultSet.getString(ITEM_ID);
                    String name = resultSet.getString(ItemQueries.NAME);
                    String unit = resultSet.getString(ItemQueries.UNIT);
                    int quantity = resultSet.getInt(ITEM_QUANTITY);
                    double costPerItem = resultSet.getInt(COST_PER_ITEM);
                    double discount = resultSet.getInt(DISCOUNT);
                    boolean percentage = resultSet.getBoolean(PERCENTAGE);
                    items.add(new BillTableItem(itemId, name, unit, quantity, costPerItem, discount, percentage));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    public List<BillTableItem> getFOutgoingTableItems() {
        List<BillTableItem> items = new ArrayList<>();

        String query = "SELECT * FROM " + OUTGOINGS_ITEMS_TABLE + " iit INNER JOIN " + ItemQueries.ITEMS_TABLE + " it" +
                " ON iit." + ItemQueries.ITEM_ID + "=it." + ITEM_ID + ";";

        ResultSet resultSet = FDBConnection.executeQuery(query);
        try {
            if (resultSet != null) {
                while (resultSet.next()) {
                    String itemId = resultSet.getString(ITEM_ID);
                    String name = resultSet.getString(ItemQueries.NAME);
                    String unit = resultSet.getString(ItemQueries.UNIT);
                    int quantity = resultSet.getInt(ITEM_QUANTITY);
                    double costPerItem = resultSet.getInt(COST_PER_ITEM);
                    double discount = resultSet.getInt(DISCOUNT);
                    boolean percentage = resultSet.getBoolean(PERCENTAGE);
                    items.add(new BillTableItem(itemId, name, unit, quantity, costPerItem, discount, percentage));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    public void deleteBill(Bill bill) {
          String query = "DELETE FROM " + OUTGOINGS_ITEMS_TABLE + " WHERE " + OUTGOING_ID + "=" + bill.get_id() + ";";
    
        DBConnection.executeQuery(query, false);
        FDBConnection.executeQuery(query, false);
    
        
         query = "DELETE FROM " + OUTGOINGS_TABLE + " WHERE " + OUTGOING_ID + "=" + bill.get_id() + ";";
        DBConnection.executeQuery(query, false);
        FDBConnection.executeQuery(query, false);
    }
}
