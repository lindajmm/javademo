package com.linda.accountbook;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件操作类
 */
public class FileHandler {
    private static final String DATA_FILE = "account_data.txt";
    private static final String BACKUP_FILE = "account_backup.txt";

    // 保存数据到文件
    public boolean saveToFile(List<Record> records) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            // 写入文件头
            writer.write("# 记账本数据文件");
            writer.newLine();
            writer.write("# 格式：ID|日期|类型|金额|类别|备注");
            writer.newLine();

            // 写入数据
            for (Record record : records) {
                writer.write(record.toFileString());
                writer.newLine();
            }

            System.out.println("数据已保存到文件: " + DATA_FILE);
            return true;

        } catch (IOException e) {
            System.out.println("保存数据时出错: " + e.getMessage());
            return false;
        }
    }

    // 从文件加载数据
    public List<Record> loadFromFile() {
        List<Record> records = new ArrayList<>();
        File file = new File(DATA_FILE);

        if (!file.exists()) {
            System.out.println("数据文件不存在，将创建新文件");
            return records;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 跳过注释行和空行
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }

                try {
                    Record record = new Record(line);
                    records.add(record);
                } catch (Exception e) {
                    System.out.println("解析行时出错: " + line);
                }
            }

            System.out.println("从文件加载了 " + records.size() + " 条记录");

        } catch (IOException e) {
            System.out.println("读取文件时出错: " + e.getMessage());
        }

        return records;
    }

    // 备份数据
    public boolean backupData() {
        File source = new File(DATA_FILE);
        File backup = new File(BACKUP_FILE);

        if (!source.exists()) {
            System.out.println("数据文件不存在，无法备份");
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(source));
             BufferedWriter writer = new BufferedWriter(new FileWriter(backup))) {

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }

            System.out.println("数据已备份到: " + BACKUP_FILE);
            return true;

        } catch (IOException e) {
            System.out.println("备份数据时出错: " + e.getMessage());
            return false;
        }
    }

    // 从备份恢复数据
    public boolean restoreFromBackup() {
        File source = new File(BACKUP_FILE);
        File target = new File(DATA_FILE);

        if (!source.exists()) {
            System.out.println("备份文件不存在");
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(source));
             BufferedWriter writer = new BufferedWriter(new FileWriter(target))) {

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }

            System.out.println("已从备份恢复数据");
            return true;

        } catch (IOException e) {
            System.out.println("恢复数据时出错: " + e.getMessage());
            return false;
        }
    }
}
