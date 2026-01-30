package com.practice.librarymanagement.dao;

import com.practice.librarymanagement.model.*;
import com.practice.librarymanagement.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Linda
 * @date: 2026/1/27 12:24
 * @description:
 */
public class BookDAO {

    // 添加图书
    public boolean addBook(Book book) throws SQLException {
        String sql = "INSERT INTO books (isbn, title, author, publisher, publish_year, " +
                "category, total_copies, available_copies, price) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getPublisher());
            stmt.setInt(5, book.getPublishYear());
            stmt.setString(6, book.getCategory());
            stmt.setInt(7, book.getTotalCopies());
            stmt.setInt(8, book.getAvailableCopies());
            stmt.setDouble(9, book.getPrice());

            return stmt.executeUpdate() > 0;
        }
    }

    // 搜索图书
    public List<Book> searchBooks(String keyword) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            stmt.setString(3, "%" + keyword + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(extractBook(rs));
            }
        }
        return books;
    }

    // 根据ID获取图书
    public Book getBookById(int id) throws SQLException {
        String sql = "SELECT * FROM books WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractBook(rs);
            }
        }
        return null;
    }

    // 更新图书库存
    public boolean updateBookStock(int bookId, int change) throws SQLException {
        String sql = "UPDATE books SET available_copies = available_copies + ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, change);
            stmt.setInt(2, bookId);

            return stmt.executeUpdate() > 0;
        }
    }

    // 从ResultSet提取Book对象
    private Book extractBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("id"));
        book.setIsbn(rs.getString("isbn"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setPublisher(rs.getString("publisher"));
        book.setPublishYear(rs.getInt("publish_year"));
        book.setCategory(rs.getString("category"));
        book.setTotalCopies(rs.getInt("total_copies"));
        book.setAvailableCopies(rs.getInt("available_copies"));
        book.setPrice(rs.getDouble("price"));
        return book;
    }
}
