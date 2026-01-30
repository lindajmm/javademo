package com.practice.librarymanagement.ui;

import com.practice.librarymanagement.model.*;
import com.practice.librarymanagement.model.Book;
import com.practice.librarymanagement.model.BorrowRecord;
import com.practice.librarymanagement.model.User;
import com.practice.librarymanagement.service.LibraryService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author: Linda
 * @date: 2026/1/27 12:28
 * @description:
 */
public class LibraryGUI extends JFrame {
    private LibraryService libraryService;

    // 组件
    private JTabbedPane tabbedPane;
    private JTable bookTable, userTable, borrowTable, overdueTable;
    private JTextField searchField, cardNumberField, bookIdField;
    private JTextArea resultArea;

    public LibraryGUI() {
        libraryService = new LibraryService();
        initUI();
    }

    private void initUI() {
        setTitle("图书馆管理系统");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 创建标签页
        tabbedPane = new JTabbedPane();

        // 图书管理标签页
        tabbedPane.addTab("图书管理", createBookPanel());

        // 读者管理标签页
        tabbedPane.addTab("读者管理", createUserPanel());

        // 借阅管理标签页
        tabbedPane.addTab("借阅管理", createBorrowPanel());

        // 逾期管理标签页
        tabbedPane.addTab("逾期管理", createOverduePanel());

        add(tabbedPane);
    }

    private JPanel createBookPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 搜索区域
        JPanel searchPanel = new JPanel();
        searchField = new JTextField(20);
        JButton searchButton = new JButton("搜索");
        searchButton.addActionListener(e -> searchBooks());

        searchPanel.add(new JLabel("搜索图书:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // 图书表格
        String[] columns = {"ID", "ISBN", "书名", "作者", "出版社", "年份", "分类", "库存", "可借", "价格"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        bookTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(bookTable);

        // 刷新按钮
        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> loadBooks());

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(refreshButton, BorderLayout.SOUTH);

        loadBooks();
        return panel;
    }

    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 用户表格
        String[] columns = {"ID", "借书证号", "姓名", "邮箱", "电话", "注册日期", "借阅上限", "已借数量"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        userTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(userTable);

        // 按钮区域
        JPanel buttonPanel = new JPanel();
        JButton addUserButton = new JButton("添加读者");
        addUserButton.addActionListener(e -> showAddUserDialog());

        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> loadUsers());

        buttonPanel.add(addUserButton);
        buttonPanel.add(refreshButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        loadUsers();
        return panel;
    }

    private JPanel createBorrowPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("借阅/归还操作"));

        // 借阅区域
        cardNumberField = new JTextField();
        bookIdField = new JTextField();

        JButton borrowButton = new JButton("借阅图书");
        borrowButton.addActionListener(e -> borrowBook());

        JButton returnButton = new JButton("归还图书");
        returnButton.addActionListener(e -> returnBook());

        inputPanel.add(new JLabel("借书证号:"));
        inputPanel.add(cardNumberField);
        inputPanel.add(new JLabel("图书ID:"));
        inputPanel.add(bookIdField);
        inputPanel.add(borrowButton);
        inputPanel.add(returnButton);

        // 结果显示区域
        resultArea = new JTextArea(10, 50);
        resultArea.setEditable(false);
        JScrollPane resultScroll = new JScrollPane(resultArea);

        // 查询借阅记录
        JPanel queryPanel = new JPanel();
        JTextField queryCardField = new JTextField(15);
        JButton queryButton = new JButton("查询借阅记录");
        queryButton.addActionListener(e -> queryBorrowRecords(queryCardField.getText()));

        queryPanel.add(new JLabel("借书证号:"));
        queryPanel.add(queryCardField);
        queryPanel.add(queryButton);

        // 借阅记录表格
        String[] columns = {"记录ID", "图书", "读者", "借阅日期", "应还日期", "状态", "逾期费用"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        borrowTable = new JTable(model);
        JScrollPane tableScroll = new JScrollPane(borrowTable);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(queryPanel, BorderLayout.CENTER);
        panel.add(tableScroll, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createOverduePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"记录ID", "图书", "读者", "借阅日期", "应还日期", "逾期天数", "应缴罚金"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        overdueTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(overdueTable);

        JButton refreshButton = new JButton("刷新逾期记录");
        refreshButton.addActionListener(e -> loadOverdueRecords());

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(refreshButton, BorderLayout.SOUTH);

        loadOverdueRecords();
        return panel;
    }

    private void loadBooks() {
        List<Book> books = libraryService.searchBooks("");
        DefaultTableModel model = (DefaultTableModel) bookTable.getModel();
        model.setRowCount(0);

        for (Book book : books) {
            model.addRow(new Object[]{
                    book.getId(),
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getPublishYear(),
                    book.getCategory(),
                    book.getTotalCopies(),
                    book.getAvailableCopies(),
                    String.format("%.2f", book.getPrice())
            });
        }
    }

    private void searchBooks() {
        String keyword = searchField.getText();
        List<Book> books = libraryService.searchBooks(keyword);
        DefaultTableModel model = (DefaultTableModel) bookTable.getModel();
        model.setRowCount(0);

        for (Book book : books) {
            model.addRow(new Object[]{
                    book.getId(),
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getPublishYear(),
                    book.getCategory(),
                    book.getTotalCopies(),
                    book.getAvailableCopies(),
                    String.format("%.2f", book.getPrice())
            });
        }
    }

    private void loadUsers() {
        List<User> users = libraryService.getAllUsers();
        DefaultTableModel model = (DefaultTableModel) userTable.getModel();
        model.setRowCount(0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (User user : users) {
            model.addRow(new Object[]{
                    user.getId(),
                    user.getCardNumber(),
                    user.getName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getRegistrationDate().format(formatter),
                    user.getMaxBorrowLimit(),
                    user.getCurrentBorrowCount()
            });
        }
    }

    private void loadOverdueRecords() {
        List<BorrowRecord> records = libraryService.getOverdueRecords();
        DefaultTableModel model = (DefaultTableModel) overdueTable.getModel();
        model.setRowCount(0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (BorrowRecord record : records) {
            long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(
                    record.getDueDate(), java.time.LocalDate.now());

            model.addRow(new Object[]{
                    record.getId(),
                    record.getBook() != null ? record.getBook().getTitle() : "未知",
                    record.getUser() != null ? record.getUser().getName() : "未知",
                    record.getBorrowDate().format(formatter),
                    record.getDueDate().format(formatter),
                    overdueDays,
                    String.format("%.2f元", record.getFineAmount())
            });
        }
    }

    private void borrowBook() {
        String cardNumber = cardNumberField.getText();
        String bookIdStr = bookIdField.getText();

        if (cardNumber.isEmpty() || bookIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入借书证号和图书ID");
            return;
        }

        try {
            int bookId = Integer.parseInt(bookIdStr);
            libraryService.borrowBook(cardNumber, bookId);
            resultArea.setText("借阅成功！\n借书证号: " + cardNumber + "\n图书ID: " + bookId);
            loadBooks(); // 刷新图书列表
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "借阅失败: " + e.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void returnBook() {
        // 这里需要实现归还逻辑
        JOptionPane.showMessageDialog(this, "归还功能待实现");
    }

    private void queryBorrowRecords(String cardNumber) {
        if (cardNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入借书证号");
            return;
        }

        List<BorrowRecord> records = libraryService.getUserBorrowRecords(cardNumber);
        DefaultTableModel model = (DefaultTableModel) borrowTable.getModel();
        model.setRowCount(0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (BorrowRecord record : records) {
            model.addRow(new Object[]{
                    record.getId(),
                    record.getBook() != null ? record.getBook().getTitle() : "未知",
                    record.getUser() != null ? record.getUser().getName() : "未知",
                    record.getBorrowDate().format(formatter),
                    record.getDueDate().format(formatter),
                    record.getStatus(),
                    String.format("%.2f元", record.getFineAmount())
            });
        }
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog(this, "添加读者", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.setLocationRelativeTo(this);

        JTextField cardField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField addressField = new JTextField();

        dialog.add(new JLabel("借书证号:"));
        dialog.add(cardField);
        dialog.add(new JLabel("姓名:"));
        dialog.add(nameField);
        dialog.add(new JLabel("邮箱:"));
        dialog.add(emailField);
        dialog.add(new JLabel("电话:"));
        dialog.add(phoneField);
        dialog.add(new JLabel("地址:"));
        dialog.add(addressField);

        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");

        saveButton.addActionListener(e -> {
            User user = new User();
            user.setCardNumber(cardField.getText());
            user.setName(nameField.getText());
            user.setEmail(emailField.getText());
            user.setPhone(phoneField.getText());
            user.setAddress(addressField.getText());
            user.setRegistrationDate(java.time.LocalDate.now());
            user.setMaxBorrowLimit(5);

            if (libraryService.addUser(user)) {
                JOptionPane.showMessageDialog(dialog, "添加成功");
                dialog.dispose();
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(dialog, "添加失败",
                        "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(saveButton);
        dialog.add(cancelButton);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LibraryGUI gui = new LibraryGUI();
            gui.setVisible(true);
        });
    }
}
