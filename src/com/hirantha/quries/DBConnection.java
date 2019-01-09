package com.hirantha.quries;


import java.sql.*;

public class DBConnection {

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
//    private static final String DB_URL = "jdbc:mysql://192.168.8.150:3307/stockdb";
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/stock-db";

    private static final String USERNAME = "root";
    private static final String PASSWORD = "123";
    private static Connection conn = null;

    public static Connection getConnection() {

        if (conn == null) {
            try {
                Class.forName(JDBC_DRIVER);
                conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                System.out.println("Connection Success!");
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
        return conn;
    }

    public static int executeQuery(String query, boolean t) {
//        query = "INSERT INTO admins VALUES (0,'hirantha','hiruu','123',19)";
        int id = -1;
        try {
            PreparedStatement statement = getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                id = resultSet.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public static ResultSet executeQuery(String query) {
        try {
            Statement statement = getConnection().createStatement();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
