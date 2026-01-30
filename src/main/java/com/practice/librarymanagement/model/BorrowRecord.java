package com.practice.librarymanagement.model;

import java.time.LocalDate;
/**
 * @author: Linda
 * @date: 2026/1/27 12:20
 * @description:
 */
public class BorrowRecord {
    private int id;
    private int userId;
    private int bookId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status;
    private double fineAmount;

    // 关联对象
    private Book book;
    private User user;

    // 构造方法、getter、setter
    public BorrowRecord() {}

    public BorrowRecord(int userId, int bookId, LocalDate borrowDate, int borrowDays) {
        this.userId = userId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.dueDate = borrowDate.plusDays(borrowDays);
        this.status = "BORROWED";
        this.fineAmount = 0.0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getFineAmount() { return fineAmount; }
    public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    @Override
    public String toString() {
        return String.format("借阅ID: %d | 图书: %s | 读者: %s | 应还日期: %s | 状态: %s",
                id, book.getTitle(), user.getName(), dueDate, status);
    }
}
