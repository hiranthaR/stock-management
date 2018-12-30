package com.hirantha.quries.admins;

import com.hirantha.models.data.admins.Admin;
import com.hirantha.quries.DBConnection;
import com.hirantha.quries.FDBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminQueries {

    public static String ADMINS_TABLE = "admins";

    public static String ADMIN_ID = "admin_id";
    public static String NAME = "name";
    public static String PASSWORD = "password";
    public static String LEVEL = "level";
    public static String USERNAME = "username";

    private static AdminQueries instance;

    private AdminQueries() {
    }

    public static AdminQueries getInstance() {
        if (instance == null) instance = new AdminQueries();
        return instance;
    }


    public Admin insertAdmin(Admin admin) {

        String query = "INSERT INTO " + ADMINS_TABLE
                + " VALUES ("
                + "0" + ","
                + "'" + admin.getName() + "',"
                + "'" + admin.getUsername() + "',"
                + "'" + admin.getPassword() + "',"
                + admin.getLevel() + ");";

        int id = DBConnection.executeQuery(query, false);
        FDBConnection.executeQuery(query, false);
        return null;
        //TODO:make admin id int
    }

    public void updateAdmin(Admin admin) {

        String query = "UPDATE " + ADMINS_TABLE
                + " SET "
                + NAME + "='" + admin.getName() + "', "
                + USERNAME + "='" + admin.getUsername() + "', "
                + PASSWORD + "='" + admin.getPassword() + "', "
                + LEVEL + "=" + admin.getLevel()
                + " WHERE " + ADMIN_ID + "=" + String.valueOf(admin.getId()) + ";";

        DBConnection.executeQuery(query);
        FDBConnection.executeQuery(query);
    }

    public List<Admin> getAdmins() {
        List<Admin> admins = new ArrayList<>();

        String query = "SELECT " + ADMIN_ID + "," + NAME + "," + LEVEL + "," + USERNAME + " FROM " + ADMINS_TABLE;

        ResultSet resultSet = DBConnection.executeQuery(query);
        try {
            if (resultSet != null) {
                while (resultSet.next()) {
                    int id = resultSet.getInt(ADMIN_ID);
                    String name = resultSet.getString(NAME);
                    String username = resultSet.getString(USERNAME);
                    String password = "";
                    int level = resultSet.getInt(LEVEL);
                    admins.add(new Admin(String.valueOf(id), name, username, password, level));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admins;
    }

    public void deleteAdmin(Admin admin) {
        String query = "DELETE FROM " + ADMINS_TABLE + " WHERE " + ADMIN_ID + "=" + admin.getId() + ";";
        DBConnection.executeQuery(query, false);
        FDBConnection.executeQuery(query, false);
    }

    public Admin getAdmin(String adminId) {
        String query = "SELECT " + ADMIN_ID + "," + NAME + "," + LEVEL + "," + USERNAME + " FROM " + ADMINS_TABLE + " WHERE " + ADMIN_ID + "=" + adminId + ";";

        ResultSet resultSet = DBConnection.executeQuery(query);
        try {
            if (resultSet != null) {
                if (resultSet.next()) {
                    int id = resultSet.getInt(ADMIN_ID);
                    String name = resultSet.getString(NAME);
                    String username = resultSet.getString(USERNAME);
                    String password = "";
                    int level = resultSet.getInt(LEVEL);
                    return new Admin(String.valueOf(id), name, username, password, level);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Admin getAdmin(String username, String password) {

        String query = "SELECT " + ADMIN_ID + "," + NAME + "," + LEVEL + "," + USERNAME + " FROM " + ADMINS_TABLE + " WHERE " + PASSWORD + "='" + password + "' AND " + USERNAME + "='" + username + "';";

        ResultSet resultSet = DBConnection.executeQuery(query);
        try {
            if (resultSet != null) {
                if (resultSet.next()) {
                    int id = resultSet.getInt(ADMIN_ID);
                    String name = resultSet.getString(NAME);
                    String uname = resultSet.getString(USERNAME);
                    String pword = "";
                    int level = resultSet.getInt(LEVEL);
                    return new Admin(String.valueOf(id), name, uname, pword, level);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
