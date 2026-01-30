package com.practice.librarymanagement.model;

import java.time.LocalDate;

/**
 * @author: Linda
 * @date: 2026/1/27 12:19
 * @description:
 */
public class User {
    private int id;
    private String cardNumber;
    private String name;
    private String email;
    private String phone;
    private String address;
    private LocalDate registrationDate;
    private int maxBorrowLimit;
    private int currentBorrowCount;

    // 构造方法、getter、setter
    public User() {}

    public User(String cardNumber, String name, String email) {
        this.cardNumber = cardNumber;
        this.name = name;
        this.email = email;
        this.registrationDate = LocalDate.now();
        this.maxBorrowLimit = 5;
        this.currentBorrowCount = 0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }

    public int getMaxBorrowLimit() { return maxBorrowLimit; }
    public void setMaxBorrowLimit(int maxBorrowLimit) { this.maxBorrowLimit = maxBorrowLimit; }

    public int getCurrentBorrowCount() { return currentBorrowCount; }
    public void setCurrentBorrowCount(int currentBorrowCount) { this.currentBorrowCount = currentBorrowCount; }

    @Override
    public String toString() {
        return String.format("%s (借书证: %s) 已借: %d/%d",
                name, cardNumber, currentBorrowCount, maxBorrowLimit);
    }
}
