package com.linda.accountbook;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 记账记录实体类
 */
public class Record {
    private int id;
    private LocalDate date;
    private String type;      // income/expense
    private double amount;
    private String category;
    private String remark;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // 构造方法
    public Record() {}

    public Record(int id, LocalDate date, String type, double amount, String category, String remark) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.remark = remark;
    }

    // 用于从文件加载的构造方法
    public Record(String dataLine) {
        String[] parts = dataLine.split("\\|");
        if (parts.length >= 6) {
            this.id = Integer.parseInt(parts[0]);
            this.date = LocalDate.parse(parts[1]);
            this.type = parts[2];
            this.amount = Double.parseDouble(parts[3]);
            this.category = parts[4];
            this.remark = parts[5];
        }
    }

    // getter 和 setter 方法
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    // 转换为字符串（用于文件存储）
    public String toFileString() {
        return String.format("%d|%s|%s|%.2f|%s|%s",
                id, date.format(formatter), type, amount, category, remark);
    }

    // 用于显示
    @Override
    public String toString() {
//        String typeStr = "收入".equals(type) ? "收入" : "支出";
        String typeStr = "income".equals(type) ? "收入" : "支出";
        String formattedDate = date.format(DateTimeFormatter.ofPattern("MM-dd"));
        return String.format("%-4d %-8s %-6s %-10.2f %-8s %-15s",
                id, formattedDate, typeStr, amount, category, remark);
    }
}