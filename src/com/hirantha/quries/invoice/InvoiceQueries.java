package com.hirantha.quries.invoice;

import com.hirantha.models.data.invoice.Invoice;
import com.hirantha.models.data.invoice.Supplier;
import com.hirantha.models.data.item.InvoiceTableItem;
import com.hirantha.quries.DBConnection;
import com.hirantha.quries.FDBConnection;
import com.hirantha.quries.admins.AdminQueries;
import com.hirantha.quries.items.ItemQueries;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InvoiceQueries {


    private String INVOICES_TABLE = "invoices";

    private static InvoiceQueries instance;

    private InvoiceQueries() {
    }

    public static InvoiceQueries getInstance() {
        if (instance == null) instance = new InvoiceQueries();
        return instance;
    }

    private String F = "* 0.8 ";
    private String INVOICE_ID = "invoice_id";
    private String DATE = "date";
    private String SUPPLIER_NAME = "supplier_name";
    private String SUPPLIER_ADDRESS = "supplier_address";
    private String SUPPLIER_INVOICE_NUMBER = "supplier_invoice_number";
    private String TOTAL_BILL_COST = "total_bill_cost";
    private String CASH = "cash_flag";
    private String PREPARED_ADMIN_ID = "prepared_admin_id";
    private String ACCEPTED_ADMIN_ID = "accepted_admin_id";
    private String CHECKED_ADMIN_ID = "checked_admin_id";

    private String PREPARED_ADMIN_NAME = "preparedAdminName";
    private String ACCEPTED_ADMIN_NAME = "acceptedAdminName";
    private String CHECKED_ADMIN_NAME = "checkedAdminName";

    private String INVOICE_ITEMS_TABLE = "inovice_items";
    private String ITEM_ID = "item_id";
    private String ITEM_NAME = "name";
    private String ITEM_QUANTITY = "quantity";
    private String COST_PER_ITEM = "cost_per_item";
    private String ITEM_UNIT = "unit";

    private String CHEQUE_TABLE = "cheques";
    private String CHEQUE_NO = "cheque_no";
    private String BANK = "bank";
    private String BRANCH = "branch";
    private String CHEQUE_DATE = "cheque_date";
    private String AMOUNT = "amount";

    public Invoice insertInvoice(Invoice invoice) {

        String query = "INSERT INTO " + INVOICES_TABLE
                + " VALUES ("
                + "0" + ","
                + "?,"
                + "'" + invoice.getName() + "',"
                + "'" + invoice.getAddress() + "',"
                + "'" + invoice.getInvoiceNumber() + "',"
                + invoice.getBillCost() + ","
                + invoice.isCash() + ","
                + invoice.getPreparedAdminId() + ","
                + invoice.getAcceptedAdminId() + ","
                + invoice.getCheckedAdminId() + ");";

        try {
            PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setDate(1, new java.sql.Date(invoice.getDate().getTime()));
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                invoice.set_id(String.valueOf(resultSet.getInt(1)));
            }
            insertTableItems(invoice);
            if (!invoice.isCash())
                insertCheque(invoice);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("exeption");
        }
        insertFInvoice(invoice);
        return invoice;
    }

    public Invoice insertFInvoice(Invoice invoice) {

        String query = "INSERT INTO " + INVOICES_TABLE
                + " VALUES ("
                + "0" + ","
                + "?,"
                + "'" + invoice.getName() + "',"
                + "'" + invoice.getAddress() + "',"
                + "'" + invoice.getInvoiceNumber() + "',"
                + invoice.getBillCost() + F + ","
                + invoice.isCash() + ","
                + invoice.getPreparedAdminId() + ","
                + invoice.getAcceptedAdminId() + ","
                + invoice.getCheckedAdminId() + ");";

        try {
            PreparedStatement preparedStatement = FDBConnection.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setDate(1, new java.sql.Date(invoice.getDate().getTime()));
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                invoice.set_id(String.valueOf(resultSet.getInt(1)));
            }
            insertFTableItems(invoice);
            if (!invoice.isCash())
                insertFCheque(invoice);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("exeption");
        }
        return invoice;
    }


    private void insertTableItems(Invoice invoice) {

        StringBuilder query = new StringBuilder("INSERT INTO " + INVOICE_ITEMS_TABLE + " VALUES ");

        for (int i = 0; i < invoice.getInvoiceTableItems().size(); i++) {
            InvoiceTableItem invoiceTableItem = invoice.getInvoiceTableItems().get(i);
            String comma = i == invoice.getInvoiceTableItems().size() - 1 ? ";" : ",";
            query.append("('")
                    .append(invoiceTableItem.getItemId()).append("',")
                    .append(invoice.get_id()).append(",")
                    .append(invoiceTableItem.getQuantity()).append(",")
                    .append(invoiceTableItem.getCostPerItem()).append(")").append(comma);
        }

        DBConnection.executeQuery(query.toString(), false);
    }

    private void insertCheque(Invoice invoice) {
        String query = "INSERT INTO " + CHEQUE_TABLE + " VALUES ("
                + invoice.get_id() + ","
                + "'" + invoice.getChequeNo()+ "',"
                + "'" + invoice.getBank() + "',"
                + "'" + invoice.getBranch() + "',"
                + invoice.getAmount() + ","
                + "?);";

        try {
            PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
            preparedStatement.setDate(1, new java.sql.Date(invoice.getChequeDate().getTime()));
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("exeption");
        }
    }


    private void insertFTableItems(Invoice invoice) {

        StringBuilder query = new StringBuilder("INSERT INTO " + INVOICE_ITEMS_TABLE + " VALUES ");

        for (int i = 0; i < invoice.getInvoiceTableItems().size(); i++) {
            InvoiceTableItem invoiceTableItem = invoice.getInvoiceTableItems().get(i);
            String comma = i == invoice.getInvoiceTableItems().size() - 1 ? ";" : ",";
            query.append("(")
                    .append("'").append(invoiceTableItem.getItemId()).append("',")
                    .append(invoice.get_id()).append(",")
                    .append(invoiceTableItem.getQuantity()).append(F).append(",")
                    .append(invoiceTableItem.getCostPerItem()).append(")").append(comma);
        }

        FDBConnection.executeQuery(query.toString(), false);
    }

    private void insertFCheque(Invoice invoice) {
        String query = "INSERT INTO " + CHEQUE_TABLE + " VALUES ("
                + invoice.get_id() + ","
                + "'" + invoice.getChequeNo()+ "',"
                + "'" + invoice.getBank() + "',"
                + "'" + invoice.getBranch() + "',"
                + invoice.getAmount() + ","
                + "?);";

        try {
            PreparedStatement preparedStatement = FDBConnection.getConnection().prepareStatement(query);
            preparedStatement.setDate(1, new java.sql.Date(invoice.getChequeDate().getTime()));
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("exeption");
        }
    }

    public void updateFInvoice(Invoice invoice) {

        StringBuilder query = new StringBuilder("DELETE FROM ")
                .append(INVOICE_ITEMS_TABLE)
                .append(" WHERE ")
                .append(INVOICE_ID)
                .append("=")
                .append(invoice.get_id())
                .append(";");

        FDBConnection.executeQuery(query.toString(), false);

        //update items - to avoid deleted items remove all and insert again
        query = new StringBuilder("INSERT INTO " + INVOICE_ITEMS_TABLE
                + " VALUES ");

        for (int i = 0; i < invoice.getInvoiceTableItems().size(); i++) {
            InvoiceTableItem invoiceTableItem = invoice.getInvoiceTableItems().get(i);
            String comma = i == invoice.getInvoiceTableItems().size() - 1 ? ";" : ",";
            query.append("(")
                    .append(invoiceTableItem.getItemId()).append(",")
                    .append(invoice.get_id()).append(",")
                    .append(invoiceTableItem.getQuantity()).append(F).append(",")
                    .append(invoiceTableItem.getCostPerItem()).append(")").append(comma);
        }

        FDBConnection.executeQuery(query.toString(), false);
        //item update done

        //update cheques
        if (!invoice.isCash()) {
            query = new StringBuilder("INSERT INTO " + CHEQUE_TABLE + "(" +
                    INVOICE_ID + "," +
                    CHEQUE_NO + "," +
                    BANK + "," +
                    BRANCH + "," +
                    AMOUNT + "," +
                    CHEQUE_DATE +
                    ") VALUES (")
                    .append(invoice.get_id()).append(",")
                    .append("'").append(invoice.getChequeNo()).append("',")
                    .append("'").append(invoice.getBank()).append("',")
                    .append("'").append(invoice.getBranch()).append("',")
                    .append(invoice.getAmount()).append(",")
                    .append("?) ")
                    .append("ON DUPLICATE KEY UPDATE ")
                    .append(CHEQUE_NO).append("=VALUES(").append(CHEQUE_NO).append("),")
                    .append(BANK).append("=VALUES(").append(BANK).append("),")
                    .append(BRANCH).append("=VALUES(").append(BRANCH).append("),")
                    .append(AMOUNT).append("=VALUES(").append(AMOUNT).append("),")
                    .append(CHEQUE_DATE).append("=VALUES(").append(CHEQUE_DATE).append(");");

            try {
                PreparedStatement preparedStatement = FDBConnection.getConnection().prepareStatement(query.toString());
                preparedStatement.setDate(1, new java.sql.Date(invoice.getChequeDate().getTime()));
                preparedStatement.executeUpdate();

            } catch (Exception e) {
//                e.printStackTrace();
            System.out.println("exeption");
            }
        } else {
            query = new StringBuilder("DELETE FROM ").append(CHEQUE_TABLE).append(" WHERE ").append(INVOICE_ID).append("=").append(invoice.get_id()).append(";");
            FDBConnection.executeQuery(query.toString(), false);
        }
        //cheque update done

        query = new StringBuilder("UPDATE " + INVOICES_TABLE + " SET ")
                .append(DATE).append("=?,")
                .append(SUPPLIER_ADDRESS).append("='").append(invoice.getName()).append("',")
                .append(SUPPLIER_ADDRESS).append("='").append(invoice.getAddress()).append("',")
                .append(SUPPLIER_INVOICE_NUMBER).append("='").append(invoice.getInvoiceNumber()).append("',")
                .append(TOTAL_BILL_COST).append("=").append(invoice.getBillCost()).append(F).append(",")
                .append(CASH).append("=").append(invoice.isCash()).append(",")
                .append(PREPARED_ADMIN_ID).append("=").append(invoice.getPreparedAdminId()).append(",")
                .append(ACCEPTED_ADMIN_ID).append("=").append(invoice.getAcceptedAdminId()).append(",")
                .append(CHECKED_ADMIN_ID).append("=").append(invoice.getCheckedAdminId())
                .append(" WHERE ").append(INVOICE_ID).append("=").append(invoice.get_id()).append(";");

        try {
            PreparedStatement preparedStatement = FDBConnection.getConnection().prepareStatement(query.toString());
            preparedStatement.setDate(1, new java.sql.Date(invoice.getDate().getTime()));
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("exeption");
        }
    }


    public void updateInvoice(Invoice invoice) {

        StringBuilder query = new StringBuilder("DELETE FROM ")
                .append(INVOICE_ITEMS_TABLE)
                .append(" WHERE ")
                .append(INVOICE_ID)
                .append("=")
                .append(invoice.get_id())
                .append(";");

        DBConnection.executeQuery(query.toString(), false);

        //update items - to avoid deleted items remove all and insert again
        query = new StringBuilder("INSERT INTO " + INVOICE_ITEMS_TABLE
                + " VALUES ");

        for (int i = 0; i < invoice.getInvoiceTableItems().size(); i++) {
            InvoiceTableItem invoiceTableItem = invoice.getInvoiceTableItems().get(i);
            String comma = i == invoice.getInvoiceTableItems().size() - 1 ? ";" : ",";
            query.append("(")
                    .append(invoiceTableItem.getItemId()).append(",")
                    .append(invoice.get_id()).append(",")
                    .append(invoiceTableItem.getQuantity()).append(",")
                    .append(invoiceTableItem.getCostPerItem()).append(")").append(comma);
        }

        DBConnection.executeQuery(query.toString(), false);
        //item update done

        //update cheques
        if (!invoice.isCash()) {
            query = new StringBuilder("INSERT INTO " + CHEQUE_TABLE + "(" +
                    INVOICE_ID + "," +
                    CHEQUE_NO + "," +
                    BANK + "," +
                    BRANCH + "," +
                    AMOUNT + "," +
                    CHEQUE_DATE +
                    ") VALUES (")
                    .append(invoice.get_id()).append(",")
                    .append("'").append(invoice.getBank()).append("',")
                    .append("'").append(invoice.getBranch()).append("',")
                    .append(invoice.getAmount()).append(",")
                    .append("?) ")
                    .append("ON DUPLICATE KEY UPDATE ")
                    .append(CHEQUE_NO).append("=VALUES(").append(CHEQUE_NO).append("),")
                    .append(BANK).append("=VALUES(").append(BANK).append("),")
                    .append(BRANCH).append("=VALUES(").append(BRANCH).append("),")
                    .append(AMOUNT).append("=VALUES(").append(AMOUNT).append("),")
                    .append(CHEQUE_DATE).append("=VALUES(").append(CHEQUE_DATE).append(");");

            try {
                PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query.toString());
                preparedStatement.setDate(1, new java.sql.Date(invoice.getChequeDate().getTime()));
                preparedStatement.executeUpdate();

            } catch (Exception e) {
//                e.printStackTrace();
            System.out.println("exeption");
            }
        } else {
            query = new StringBuilder("DELETE FROM ").append(CHEQUE_TABLE).append(" WHERE ").append(INVOICE_ID).append("=").append(invoice.get_id()).append(";");
            DBConnection.executeQuery(query.toString(), false);
        }
        //cheque update done

        query = new StringBuilder("UPDATE " + INVOICES_TABLE + " SET ")
                .append(DATE).append("=?,")
                .append(SUPPLIER_ADDRESS).append("='").append(invoice.getName()).append("',")
                .append(SUPPLIER_ADDRESS).append("='").append(invoice.getAddress()).append("',")
                .append(SUPPLIER_INVOICE_NUMBER).append("='").append(invoice.getInvoiceNumber()).append("',")
                .append(TOTAL_BILL_COST).append("=").append(invoice.getBillCost()).append(",")
                .append(CASH).append("=").append(invoice.isCash()).append(",")
                .append(PREPARED_ADMIN_ID).append("=").append(invoice.getPreparedAdminId()).append(",")
                .append(ACCEPTED_ADMIN_ID).append("=").append(invoice.getAcceptedAdminId()).append(",")
                .append(CHECKED_ADMIN_ID).append("=").append(invoice.getCheckedAdminId())
                .append(" WHERE ").append(INVOICE_ID).append("=").append(invoice.get_id()).append(";");

        try {
            PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query.toString());
            preparedStatement.setDate(1, new java.sql.Date(invoice.getDate().getTime()));
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("exeption");
        }
        updateFInvoice(invoice);
    }

    public List<Supplier> getSuppliers() {

        List<Supplier> suppliers = new ArrayList<>();
        String query = "SELECT DISTINCT " + SUPPLIER_NAME + "," + SUPPLIER_ADDRESS + " FROM " + INVOICES_TABLE + ";";

        ResultSet resultSet = DBConnection.executeQuery(query);
        try {
            if (resultSet != null) {
                while (resultSet.next()) {
                    String supplierName = resultSet.getString(SUPPLIER_NAME);
                    String supplierAddress = resultSet.getString(SUPPLIER_ADDRESS);
                    suppliers.add(new Supplier(supplierName, supplierAddress));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("exeption");
        }
        return suppliers;
    }


    public List<Invoice> getInvoices() {

        List<Invoice> invoices = new ArrayList<>();

        String query = "SELECT it.*,atp." + AdminQueries.NAME + " AS pan,ata." + AdminQueries.NAME + " AS aan,atc." + AdminQueries.NAME + " AS can,ct.* FROM " + INVOICES_TABLE + " it" +
                " INNER JOIN " + AdminQueries.ADMINS_TABLE + " atp ON atp." + AdminQueries.ADMIN_ID + "=it." + PREPARED_ADMIN_ID +
                " INNER JOIN " + AdminQueries.ADMINS_TABLE + " ata ON ata." + AdminQueries.ADMIN_ID + "=it." + ACCEPTED_ADMIN_ID +
                " INNER JOIN " + AdminQueries.ADMINS_TABLE + " atc ON atc." + AdminQueries.ADMIN_ID + "=it." + CHECKED_ADMIN_ID +
                " LEFT OUTER JOIN " + CHEQUE_TABLE + " ct ON ct." + INVOICE_ID + "=it." + INVOICE_ID + " ORDER BY it." + INVOICE_ID + " DESC;";


        ResultSet invoiceResultSet = DBConnection.executeQuery(query);

        try {

            if (invoiceResultSet != null) {
                while (invoiceResultSet.next()) {
                    int invoiceId = invoiceResultSet.getInt(INVOICE_ID);
                    Date date = invoiceResultSet.getDate(DATE);
                    String supplierName = invoiceResultSet.getString(SUPPLIER_NAME);
                    String supplierAddress = invoiceResultSet.getString(SUPPLIER_ADDRESS);
                    String supplierInvoiceNumber = invoiceResultSet.getString(SUPPLIER_INVOICE_NUMBER);
                    double totalBillCost = invoiceResultSet.getDouble(TOTAL_BILL_COST);
                    int preparedAdminId = invoiceResultSet.getInt(PREPARED_ADMIN_ID);
                    int acceptedAdminId = invoiceResultSet.getInt(ACCEPTED_ADMIN_ID);
                    int checkedAdminId = invoiceResultSet.getInt(CHECKED_ADMIN_ID);
                    String preparedAdminName = invoiceResultSet.getString("pan");
                    String acceptedAdminName = invoiceResultSet.getString("aan");
                    String checkedAdminName = invoiceResultSet.getString("can");

                    boolean cash = invoiceResultSet.getBoolean(CASH);

                    String chequeNo = invoiceResultSet.getString(CHEQUE_NO);
                    String bank = invoiceResultSet.getString(BANK);
                    String branch = invoiceResultSet.getString(BRANCH);
                    double amount = invoiceResultSet.getDouble(AMOUNT);
                    Date chequeDate = invoiceResultSet.getDate(CHEQUE_DATE);
                    List<InvoiceTableItem> items = getItems(invoiceId);

                    invoices.add(new Invoice(String.valueOf(invoiceId), date, supplierInvoiceNumber, supplierName, supplierAddress, items, totalBillCost, cash, chequeNo,bank, branch, chequeDate, amount, preparedAdminName, String.valueOf(preparedAdminId), checkedAdminName, String.valueOf(checkedAdminId), acceptedAdminName, String.valueOf(acceptedAdminId)));

                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return invoices;
    }

    public List<Invoice> getFInvoices() {

        List<Invoice> invoices = new ArrayList<>();

        String query = "SELECT it.*,atp." + AdminQueries.NAME + " AS pan,ata." + AdminQueries.NAME + " AS aan,atc." + AdminQueries.NAME + " AS can,ct.* FROM " + INVOICES_TABLE + " it" +
                " INNER JOIN " + AdminQueries.ADMINS_TABLE + " atp ON atp." + AdminQueries.ADMIN_ID + "=it." + PREPARED_ADMIN_ID +
                " INNER JOIN " + AdminQueries.ADMINS_TABLE + " ata ON ata." + AdminQueries.ADMIN_ID + "=it." + ACCEPTED_ADMIN_ID +
                " INNER JOIN " + AdminQueries.ADMINS_TABLE + " atc ON atc." + AdminQueries.ADMIN_ID + "=it." + CHECKED_ADMIN_ID +
                " LEFT OUTER JOIN " + CHEQUE_TABLE + " ct ON ct." + INVOICE_ID + "=it." + INVOICE_ID + " ORDER BY it." + INVOICE_ID + " DESC;";


        ResultSet invoiceResultSet = FDBConnection.executeQuery(query);

        try {

            if (invoiceResultSet != null) {
                while (invoiceResultSet.next()) {
                    int invoiceId = invoiceResultSet.getInt(INVOICE_ID);
                    Date date = invoiceResultSet.getDate(DATE);
                    String supplierName = invoiceResultSet.getString(SUPPLIER_NAME);
                    String supplierAddress = invoiceResultSet.getString(SUPPLIER_ADDRESS);
                    String supplierInvoiceNumber = invoiceResultSet.getString(SUPPLIER_INVOICE_NUMBER);
                    double totalBillCost = invoiceResultSet.getDouble(TOTAL_BILL_COST);
                    int preparedAdminId = invoiceResultSet.getInt(PREPARED_ADMIN_ID);
                    int acceptedAdminId = invoiceResultSet.getInt(ACCEPTED_ADMIN_ID);
                    int checkedAdminId = invoiceResultSet.getInt(CHECKED_ADMIN_ID);
                    String preparedAdminName = invoiceResultSet.getString("pan");
                    String acceptedAdminName = invoiceResultSet.getString("aan");
                    String checkedAdminName = invoiceResultSet.getString("can");

                    boolean cash = invoiceResultSet.getBoolean(CASH);

                    String chequeNo = invoiceResultSet.getString(CHEQUE_NO);
                    String bank = invoiceResultSet.getString(BANK);
                    String branch = invoiceResultSet.getString(BRANCH);
                    double amount = invoiceResultSet.getDouble(AMOUNT);
                    Date chequeDate = invoiceResultSet.getDate(CHEQUE_DATE);
                    List<InvoiceTableItem> items = getFItems(invoiceId);

                    invoices.add(new Invoice(String.valueOf(invoiceId), date, supplierInvoiceNumber, supplierName, supplierAddress, items, totalBillCost, cash, chequeNo ,bank, branch, chequeDate, amount, preparedAdminName, String.valueOf(preparedAdminId), checkedAdminName, String.valueOf(checkedAdminId), acceptedAdminName, String.valueOf(acceptedAdminId)));

                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return invoices;
    }

    public List<InvoiceTableItem> getItems(int invoiceId) {
        List<InvoiceTableItem> items = new ArrayList<>();

        String query = "SELECT * FROM " + INVOICE_ITEMS_TABLE + " iit INNER JOIN " + ItemQueries.ITEMS_TABLE + " it" +
                " ON iit." + ItemQueries.ITEM_ID + "=it." + ITEM_ID +
                " WHERE iit." + INVOICE_ID + "=" + invoiceId + ";";

        ResultSet resultSet = DBConnection.executeQuery(query);
        try {
            if (resultSet != null) {
                while (resultSet.next()) {
                    String itemId = resultSet.getString(ITEM_ID);
                    String name = resultSet.getString(ItemQueries.NAME);
                    String unit = resultSet.getString(ItemQueries.UNIT);
                    int quantity = resultSet.getInt(ITEM_QUANTITY);
                    double costPerItem = resultSet.getInt(COST_PER_ITEM);
                    items.add(new InvoiceTableItem(String.valueOf(itemId), name, unit, quantity, costPerItem));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("exeption");
        }
        return items;
    }

    public List<InvoiceTableItem> getFItems(int invoiceId) {
        List<InvoiceTableItem> items = new ArrayList<>();

        String query = "SELECT * FROM " + INVOICE_ITEMS_TABLE + " iit INNER JOIN " + ItemQueries.ITEMS_TABLE + " it" +
                " ON iit." + ItemQueries.ITEM_ID + "=it." + ITEM_ID +
                " WHERE iit." + INVOICE_ID + "=" + invoiceId + ";";

        ResultSet resultSet = FDBConnection.executeQuery(query);
        try {
            if (resultSet != null) {
                while (resultSet.next()) {
                    String itemId = resultSet.getString(ITEM_ID);
                    String name = resultSet.getString(ItemQueries.NAME);
                    String unit = resultSet.getString(ItemQueries.UNIT);
                    int quantity = resultSet.getInt(ITEM_QUANTITY);
                    double costPerItem = resultSet.getInt(COST_PER_ITEM);
                    items.add(new InvoiceTableItem(String.valueOf(itemId), name, unit, quantity, costPerItem));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("exeption");
        }
        return items;
    }

    //
    public List<InvoiceTableItem> getInvoiceTableItems() {
        List<InvoiceTableItem> items = new ArrayList<>();

        String query = "SELECT * FROM " + INVOICE_ITEMS_TABLE + " iit INNER JOIN " + ItemQueries.ITEMS_TABLE + " it" +
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
                    items.add(new InvoiceTableItem(String.valueOf(itemId), name, unit, quantity, costPerItem));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("exeption");
        }
        return items;
    }

    public List<InvoiceTableItem> getFInvoiceTableItems() {
        List<InvoiceTableItem> items = new ArrayList<>();

        String query = "SELECT * FROM " + INVOICE_ITEMS_TABLE + " iit INNER JOIN " + ItemQueries.ITEMS_TABLE + " it" +
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
                    items.add(new InvoiceTableItem(String.valueOf(itemId), name, unit, quantity, costPerItem));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("exeption");
        }
        return items;
    }

    public void deleteInvoice(Invoice invoice) {
        String query = "DELETE FROM " + INVOICE_ITEMS_TABLE + " WHERE " + INVOICE_ID + "=" + invoice.get_id() + ";";
    
        DBConnection.executeQuery(query, false);
        FDBConnection.executeQuery(query, false);
    
        query = "DELETE FROM " + INVOICES_TABLE + " WHERE " + INVOICE_ID + "=" + invoice.get_id() + ";";
        DBConnection.executeQuery(query, false);
        FDBConnection.executeQuery(query, false);
    }

}
