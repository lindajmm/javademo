package com.practice.librarymanagement.service;

import com.practice.librarymanagement.dao.*;
import com.practice.librarymanagement.model.*;
import com.practice.librarymanagement.dao.BookDAO;
import com.practice.librarymanagement.dao.BorrowDAO;
import com.practice.librarymanagement.dao.UserDAO;
import com.practice.librarymanagement.model.Book;
import com.practice.librarymanagement.model.BorrowRecord;
import com.practice.librarymanagement.model.User;

import java.sql.SQLException;
import java.util.List;


/**
 * @author: Linda
 * @date: 2026/1/27 12:27
 * @description:
 */
public class LibraryService {
    private BookDAO bookDAO;
    private UserDAO userDAO;
    private BorrowDAO borrowDAO;

    public LibraryService() {
        this.bookDAO = new BookDAO();
        this.userDAO = new UserDAO();
        this.borrowDAO = new BorrowDAO();
    }

    // 借阅图书
    public boolean borrowBook(String cardNumber, int bookId) throws Exception {
        // 1. 验证读者
        User user = userDAO.getUserByCardNumber(cardNumber);
        if (user == null) {
            throw new Exception("读者不存在");
        }

        // 2. 检查借阅上限
        if (user.getCurrentBorrowCount() >= user.getMaxBorrowLimit()) {
            throw new Exception("借阅数量已达上限");
        }

        // 3. 检查图书库存
        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            throw new Exception("图书不存在");
        }
        if (book.getAvailableCopies() <= 0) {
            throw new Exception("图书已被借完");
        }

        // 4. 执行借阅（需要事务处理）
        try {
            // 更新图书库存
            bookDAO.updateBookStock(bookId, -1);

            // 更新读者借阅数量
            userDAO.updateUserBorrowCount(user.getId(), 1);

            // 创建借阅记录（默认借30天）
            borrowDAO.borrowBook(user.getId(), bookId, 30);

            return true;
        } catch (SQLException e) {
            throw new Exception("借阅失败: " + e.getMessage());
        }
    }

    // 归还图书
    public boolean returnBook(int recordId) throws Exception {
        try {
            // 1. 获取借阅记录
            // 2. 更新图书库存
            // 3. 更新读者借阅数量
            // 4. 更新借阅记录状态
            boolean success = borrowDAO.returnBook(recordId);
            if (success) {
                // 这里需要根据recordId找到bookId和userId
                // 然后更新库存和借阅数量
                // 简化处理：在实际项目中需要更完整的实现
            }
            return success;
        } catch (SQLException e) {
            throw new Exception("归还失败: " + e.getMessage());
        }
    }

    // 搜索图书
    public List<Book> searchBooks(String keyword) {
        try {
            return bookDAO.searchBooks(keyword);
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // 获取所有读者
    public List<User> getAllUsers() {
        try {
            return userDAO.getAllUsers();
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // 获取逾期记录
    public List<BorrowRecord> getOverdueRecords() {
        try {
            return borrowDAO.getOverdueRecords();
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // 添加新读者
    public boolean addUser(User user) {
        try {
            return userDAO.addUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 获取读者借阅记录
    public List<BorrowRecord> getUserBorrowRecords(String cardNumber) {
        try {
            User user = userDAO.getUserByCardNumber(cardNumber);
            if (user != null) {
                return borrowDAO.getBorrowRecordsByUser(user.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return List.of();
    }
}
