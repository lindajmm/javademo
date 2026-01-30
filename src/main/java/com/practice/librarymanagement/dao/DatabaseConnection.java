package com.practice.librarymanagement.dao;

import java.sql.*;
/**
 * @author: Linda
 * @date: 2026/1/27 12:21
 * @description:
 */
public class DatabaseConnection {
//    private static final String URL = "jdbc:mysql://localhost:3306/library_db";
    private static final String URL = "jdbc:mysql://192.168.5.101:3306/library_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&autoReconnect=true";
    private static final String USER = "hostonly_user";
    private static final String PASSWORD = "123456"; // 修改为你的密码

    private static Connection connection = null;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
