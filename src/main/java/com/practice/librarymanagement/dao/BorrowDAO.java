package com.practice.librarymanagement.dao;

import com.practice.librarymanagement.model.BorrowRecord;
import java.sql.*;
        import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Linda
 * @date: 2026/1/27 12:26
 * @description:
 */
public class BorrowDAO {

    // 借阅图书
    public boolean borrowBook(int userId, int bookId, int borrowDays) throws SQLException {
        String sql = "INSERT INTO borrow_records (user_id, book_id, borrow_date, due_date, status) " +
                "VALUES (?, ?, ?, ?, 'BORROWED')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            stmt.setDate(3, Date.valueOf(LocalDate.now()));
            stmt.setDate(4, Date.valueOf(LocalDate.now().plusDays(borrowDays)));

            return stmt.executeUpdate() > 0;
        }
    }

    // 归还图书
    public boolean returnBook(int recordId) throws SQLException {
        String sql = "UPDATE borrow_records SET return_date = ?, status = 'RETURNED' " +
                "WHERE id = ? AND status = 'BORROWED'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(LocalDate.now()));
            stmt.setInt(2, recordId);

            return stmt.executeUpdate() > 0;
        }
    }

    // 获取读者的借阅记录
    public List<BorrowRecord> getBorrowRecordsByUser(int userId) throws SQLException {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT br.*, b.title, b.isbn, u.name " +
                "FROM borrow_records br " +
                "JOIN books b ON br.book_id = b.id " +
                "JOIN users u ON br.user_id = u.id " +
                "WHERE br.user_id = ? ORDER BY br.borrow_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                records.add(extractBorrowRecord(rs));
            }
        }
        return records;
    }

    // 获取逾期记录
    public List<BorrowRecord> getOverdueRecords() throws SQLException {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT br.*, b.title, b.isbn, u.name " +
                "FROM borrow_records br " +
                "JOIN books b ON br.book_id = b.id " +
                "JOIN users u ON br.user_id = u.id " +
                "WHERE br.status = 'BORROWED' AND br.due_date < CURDATE()";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                records.add(extractBorrowRecord(rs));
            }
        }
        return records;
    }

    // 计算逾期费用（简单规则：每天0.5元）
    public double calculateFine(LocalDate dueDate) {
        LocalDate today = LocalDate.now();
        if (today.isAfter(dueDate)) {
            long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(dueDate, today);
            return daysOverdue * 0.5;
        }
        return 0.0;
    }

    // 从ResultSet提取BorrowRecord对象
    private BorrowRecord extractBorrowRecord(ResultSet rs) throws SQLException {
        BorrowRecord record = new BorrowRecord();
        record.setId(rs.getInt("id"));
        record.setUserId(rs.getInt("user_id"));
        record.setBookId(rs.getInt("book_id"));
        record.setBorrowDate(rs.getDate("borrow_date").toLocalDate());
        record.setDueDate(rs.getDate("due_date").toLocalDate());

        Date returnDate = rs.getDate("return_date");
        if (returnDate != null) {
            record.setReturnDate(returnDate.toLocalDate());
        }

        record.setStatus(rs.getString("status"));
        record.setFineAmount(rs.getDouble("fine_amount"));

        // 计算逾期费用
        if ("BORROWED".equals(record.getStatus())) {
            double fine = calculateFine(record.getDueDate());
            if (fine > 0) {
                record.setFineAmount(fine);
            }
        }

        return record;
    }
}
