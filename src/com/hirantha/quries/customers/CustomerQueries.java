package com.hirantha.quries.customers;

import com.google.gson.Gson;
import com.hirantha.database.Connection;
import com.hirantha.database.meta.MetaQueries;
import com.hirantha.models.data.admins.Admin;
import com.hirantha.models.data.customer.Customer;
import com.hirantha.quries.DBConnection;
import com.hirantha.quries.FDBConnection;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerQueries {

    public static String CUSTOMER_TABLE = "customers";

    public static String CUSTOMER_ID = "customer_id";
    public static String NAME = "customer_name";
    public static String ADDRESS = "address";
    public static String TELEPHONE = "telephone";
    public static String TITLE = "title";
    public static String RANK = "rank";
    public static String IMAGE_URL = "image_url";

    private static CustomerQueries instance;

    private CustomerQueries() {
    }

    public static CustomerQueries getInstance() {
        if (instance == null) instance = new CustomerQueries();
        return instance;
    }


    public Customer insertCustomer(Customer customer, boolean flag) {
        String query = "INSERT INTO " + CUSTOMER_TABLE
                + " VALUES ("
                + "0" + ","
                + "'" + customer.getTitle() + "',"
                + "'" + customer.getName() + "',"
                + customer.getRank() + ","
                + "'" + customer.getAddress() + "',"
                + "'" + customer.getImageUrl() + "',"
                + "'" + customer.getTelephone() + "',"
                + flag + ");";

        int id = DBConnection.executeQuery(query, false);
        FDBConnection.executeQuery(query, false);
        customer.setId(String.valueOf(id));
        return customer;
    }

    public void updateCustomer(Customer customer, boolean flag) {

        String query = "UPDATE " + CUSTOMER_TABLE
                + " SET "
                + TITLE + "='" + customer.getTitle() + "', "
                + NAME + "='" + customer.getName() + "', "
                + RANK + "=" + customer.getRank() + ", "
                + ADDRESS + "='" + customer.getAddress() + "', "
                + IMAGE_URL + "='" + customer.getImageUrl() + "', "
                + TELEPHONE + "='" + customer.getTelephone() + "',"
                + "flag=" + flag + " "
                + " WHERE " + CUSTOMER_ID + "=" + String.valueOf(customer.getId()) + ";";


        DBConnection.executeQuery(query, false);
        FDBConnection.executeQuery(query, false);
    }

    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();

        String query = "SELECT * FROM " + CUSTOMER_TABLE;

        ResultSet resultSet = DBConnection.executeQuery(query);
        try {
            if (resultSet != null) {
                while (resultSet.next()) {
                    int customerId = resultSet.getInt(CUSTOMER_ID);
                    String title = resultSet.getString(TITLE);
                    String name = resultSet.getString(NAME);
                    int rank = resultSet.getInt(RANK);
                    String address = resultSet.getString(ADDRESS);
                    String telephone = resultSet.getString(TELEPHONE);
                    customers.add(new Customer(String.valueOf(customerId), title.equals("Mr."), title, name, address, telephone, rank));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public List<Customer> getRankedCustomers() {
        List<Customer> customers = new ArrayList<>();

        String query = "SELECT * FROM " + CUSTOMER_TABLE + " WHERE flag=" + true + ";";

        ResultSet resultSet = DBConnection.executeQuery(query);
        try {
            if (resultSet != null) {
                while (resultSet.next()) {
                    int customerId = resultSet.getInt(CUSTOMER_ID);
                    String title = resultSet.getString(TITLE);
                    String name = resultSet.getString(NAME);
                    int rank = resultSet.getInt(RANK);
                    String address = resultSet.getString(ADDRESS);
                    String telephone = resultSet.getString(TELEPHONE);
                    customers.add(new Customer(String.valueOf(customerId), title.equals("Mr."), title, name, address, telephone, rank));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public void deleteCustomer(Customer customer) {
        String query = "DELETE FROM " + CUSTOMER_TABLE + " WHERE " + CUSTOMER_ID + "=" + customer.getId() + ";";
        DBConnection.executeQuery(query, false);
        FDBConnection.executeQuery(query, false);
    }

    public Customer getCustomer(String id) {
        String query = "SELECT * FROM " + CUSTOMER_TABLE + " WHERE " + CUSTOMER_ID + "=" + id + ";";

        ResultSet resultSet = DBConnection.executeQuery(query);
        try {
            if (resultSet != null) {
                if (resultSet.next()) {
                    int customerId = resultSet.getInt(CUSTOMER_ID);
                    String title = resultSet.getString(TITLE);
                    String name = resultSet.getString(NAME);
                    int rank = resultSet.getInt(RANK);
                    String address = resultSet.getString(ADDRESS);
                    String telephone = resultSet.getString(TELEPHONE);
                    return new Customer(String.valueOf(customerId), title.equals("Mr."), title, name, address, telephone, rank);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
