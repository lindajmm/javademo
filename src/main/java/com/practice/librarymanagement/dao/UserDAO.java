package com.practice.librarymanagement.dao;


import com.practice.librarymanagement.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 * @author: Linda
 * @date: 2026/1/27 12:25
 * @description:
 */
public class UserDAO {

    // 添加读者
    public boolean addUser(User user) throws SQLException {
        String sql = "INSERT INTO users (card_number, name, email, phone, address, " +
                "registration_date, max_borrow_limit, current_borrow_count) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getCardNumber());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPhone());
            stmt.setString(5, user.getAddress());
            stmt.setDate(6, Date.valueOf(user.getRegistrationDate()));
            stmt.setInt(7, user.getMaxBorrowLimit());
            stmt.setInt(8, user.getCurrentBorrowCount());

            return stmt.executeUpdate() > 0;
        }
    }

    // 根据借书证号查找读者
    public User getUserByCardNumber(String cardNumber) throws SQLException {
        String sql = "SELECT * FROM users WHERE card_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cardNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractUser(rs);
            }
        }
        return null;
    }

    // 更新读者借阅数量
    public boolean updateUserBorrowCount(int userId, int change) throws SQLException {
        String sql = "UPDATE users SET current_borrow_count = current_borrow_count + ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, change);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;
        }
    }

    // 获取所有读者
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(extractUser(rs));
            }
        }
        return users;
    }

    // 从ResultSet提取User对象
    private User extractUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setCardNumber(rs.getString("card_number"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setAddress(rs.getString("address"));
        user.setRegistrationDate(rs.getDate("registration_date").toLocalDate());
        user.setMaxBorrowLimit(rs.getInt("max_borrow_limit"));
        user.setCurrentBorrowCount(rs.getInt("current_borrow_count"));
        return user;
    }
}
