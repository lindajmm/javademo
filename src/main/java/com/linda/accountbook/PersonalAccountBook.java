package com.linda.accountbook;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * 个人记账本主程序
 */
public class PersonalAccountBook {
    private AccountBook accountBook;
    private FileHandler fileHandler;
    private ReportGenerator reportGenerator;
    private Scanner scanner;

    public PersonalAccountBook() {
        this.accountBook = new AccountBook();
        this.fileHandler = new FileHandler();
        this.reportGenerator = new ReportGenerator();
        this.scanner = new Scanner(System.in);
    }

    // 主运行方法
    public void run() {
        // 加载数据
        loadData();

        System.out.println("========================================");
        System.out.println("      欢迎使用个人记账本系统");
        System.out.println("========================================");

        boolean running = true;
        while (running) {
            showMainMenu();
            System.out.print("请选择操作 (0-8): ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // 消耗换行符

                switch (choice) {
                    case 1 -> addRecord();
                    case 2 -> viewAllRecords();
                    case 3 -> viewMonthlyReport();
                    case 4 -> viewCategoryStatistics();
                    case 5 -> deleteRecord();
                    case 6 -> searchRecords();
                    case 7 -> systemMaintenance();
                    case 8 -> generateYearlySummary();
                    case 0 -> {
                        running = false;
                        System.out.println("正在退出系统...");
                    }
                    default -> System.out.println("无效的选择，请重新输入");
                }

                if (choice != 0) {

                    System.out.println("\n按回车键继续...");
                    scanner.nextLine();
                }

            } catch (InputMismatchException e) {
                System.out.println("请输入有效的数字！");

                scanner.nextLine(); // 清除无效输入
            }
        }

        // 保存数据并退出
        saveData();
        scanner.close();
        System.out.println("感谢使用个人记账本系统！");
    }

    // 显示主菜单
    private void showMainMenu() {
        System.out.println("\n===== 个人记账本系统 =====");
        System.out.println("1. 添加记账记录");
        System.out.println("2. 查看所有记录");
        System.out.println("3. 月度报表");
        System.out.println("4. 分类统计");
        System.out.println("5. 删除记录");
        System.out.println("6. 搜索记录");
        System.out.println("7. 系统维护");
        System.out.println("8. 年度概要");
        System.out.println("0. 退出系统");
        System.out.println("当前余额: ¥" + String.format("%.2f", accountBook.getBalance()));
        System.out.println("==========================");
    }

    // 添加记录
    private void addRecord() {
        System.out.println("\n--- 添加记账记录 ---");

        try {
            // 选择类型
            System.out.print("请选择类型 (1.收入 / 2.支出): ");
            int typeChoice = scanner.nextInt();
            scanner.nextLine();

            if (typeChoice != 1 && typeChoice != 2) {
                System.out.println("无效的选择");
                return;
            }

            String type = (typeChoice == 1) ? "income" : "expense";

            // 输入金额
            System.out.print("请输入金额: ");
            double amount = scanner.nextDouble();
            scanner.nextLine();

            if (amount <= 0) {
                System.out.println("金额必须大于0");
                return;
            }

            // 选择类别
            System.out.println("\n请选择类别:");
            String[] categories = (typeChoice == 1) ?
                    AccountBook.INCOME_CATEGORIES : AccountBook.EXPENSE_CATEGORIES;

            for (int i = 0; i < categories.length; i++) {
                System.out.printf("%d. %s\n", i + 1, categories[i]);
            }
            System.out.print("请选择 (1-" + categories.length + "): ");

            int categoryChoice = scanner.nextInt();
            scanner.nextLine();

            if (categoryChoice < 1 || categoryChoice > categories.length) {
                System.out.println("无效的选择，使用默认类别");
                categoryChoice = categories.length;
            }

            String category = categories[categoryChoice - 1];

            // 输入日期
            LocalDate date;
            System.out.print("请输入日期 (YYYY-MM-DD，直接回车使用今天): ");
            String dateStr = scanner.nextLine().trim();

            if (dateStr.isEmpty()) {
                date = LocalDate.now();
            } else {
                try {
                    date = LocalDate.parse(dateStr);
                } catch (DateTimeParseException e) {
                    System.out.println("日期格式错误，使用今天日期");
                    date = LocalDate.now();
                }
            }

            // 输入备注
            System.out.print("请输入备注: ");
            String remark = scanner.nextLine();

            // 创建记录
            Record record = new Record(0, date, type, amount, category, remark);
            accountBook.addRecord(record);

            System.out.println("记录添加成功！");
            System.out.println("添加的记录: " + record);

        } catch (InputMismatchException e) {
            System.out.println("输入格式错误！");
            scanner.nextLine();
        }
    }

    // 查看所有记录
    private void viewAllRecords() {
        System.out.println("\n--- 所有记账记录 ---");

        List<Record> allRecords = accountBook.getAllRecords();

        if (allRecords.isEmpty()) {
            System.out.println("暂无记录");
            return;
        }

        System.out.println("ID    日期     类型     金额        类别       备注");
        System.out.println("--------------------------------------------------");

        for (Record record : allRecords) {
            System.out.println(record);
        }

        System.out.println("--------------------------------------------------");
        System.out.println("总计 " + allRecords.size() + " 条记录");
    }

    // 查看月度报表
    private void viewMonthlyReport() {
        System.out.println("\n--- 月度报表 ---");

        try {
            System.out.print("请输入年份 (如: 2024): ");
            int year = scanner.nextInt();

            System.out.print("请输入月份 (1-12): ");
            int month = scanner.nextInt();
            scanner.nextLine();

            if (month < 1 || month > 12) {
                System.out.println("月份无效");
                return;
            }

            reportGenerator.generateMonthlyReport(year, month, accountBook);

        } catch (InputMismatchException e) {
            System.out.println("输入格式错误！");
            scanner.nextLine();
        }
    }

    // 分类统计
    private void viewCategoryStatistics() {
        System.out.println("\n--- 分类统计 ---");

        try {
            System.out.print("请选择统计类型 (1.收入 / 2.支出): ");
            int typeChoice = scanner.nextInt();
            scanner.nextLine();

            String type = (typeChoice == 1) ? "income" : "expense";

            System.out.print("请输入开始日期 (YYYY-MM-DD): ");
            String startStr = scanner.nextLine();

            System.out.print("请输入结束日期 (YYYY-MM-DD): ");
            String endStr = scanner.nextLine();

            LocalDate startDate = LocalDate.parse(startStr);
            LocalDate endDate = LocalDate.parse(endStr);

            if (startDate.isAfter(endDate)) {
                System.out.println("开始日期不能晚于结束日期");
                return;
            }

            Map<String, Double> summary = accountBook.getCategorySummary(type, startDate, endDate);

            System.out.println("\n--- 统计结果 (" + startDate + " 至 " + endDate + ") ---");
            if (summary.isEmpty()) {
                System.out.println("该时间段内没有" + (typeChoice == 1 ? "收入" : "支出") + "记录");
            } else {
                double total = 0;
                for (Map.Entry<String, Double> entry : summary.entrySet()) {
                    System.out.printf("%-10s: ¥%.2f\n", entry.getKey(), entry.getValue());
                    total += entry.getValue();
                }
                System.out.println("-----------------------");
                System.out.printf("总计: ¥%.2f\n", total);
            }

        } catch (DateTimeParseException e) {
            System.out.println("日期格式错误，请使用 YYYY-MM-DD 格式");
        } catch (InputMismatchException e) {
            System.out.println("输入格式错误！");
            scanner.nextLine();
        }
    }

    // 删除记录
    private void deleteRecord() {
        System.out.println("\n--- 删除记录 ---");

        viewAllRecords();

        System.out.print("请输入要删除的记录ID (0取消): ");

        try {
            int id = scanner.nextInt();
            scanner.nextLine();

            if (id == 0) {
                System.out.println("取消删除");
                return;
            }

            Record record = accountBook.findRecordById(id);
            if (record == null) {
                System.out.println("未找到ID为 " + id + " 的记录");
                return;
            }

            System.out.println("找到记录: " + record);
            System.out.print("确定删除吗？ (y/N): ");
            String confirm = scanner.nextLine().toLowerCase();

            if (confirm.equals("y") || confirm.equals("yes")) {
                if (accountBook.deleteRecord(id)) {
                    System.out.println("记录删除成功");
                } else {
                    System.out.println("删除失败");
                }
            } else {
                System.out.println("取消删除");
            }

        } catch (InputMismatchException e) {
            System.out.println("请输入有效的ID");
            scanner.nextLine();
        }
    }

    // 搜索记录
    private void searchRecords() {
        System.out.println("\n--- 搜索记录 ---");
        System.out.println("1. 按类别搜索");
        System.out.println("2. 按日期范围搜索");
        System.out.print("请选择搜索方式: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine();

            List<Record> results = new ArrayList<>();

            switch (choice) {
                case 1 -> {
                    // 按类别搜索
                    System.out.print("请输入类别名称: ");
                    String category = scanner.nextLine();

                    results = accountBook.getAllRecords().stream()
                            .filter(r -> r.getCategory().contains(category))
                            .collect(java.util.stream.Collectors.toList());
                }
                case 2 -> {
                    // 按日期范围搜索
                    System.out.print("请输入开始日期 (YYYY-MM-DD): ");
                    String startStr = scanner.nextLine();

                    System.out.print("请输入结束日期 (YYYY-MM-DD): ");
                    String endStr = scanner.nextLine();

                    LocalDate startDate = LocalDate.parse(startStr);
                    LocalDate endDate = LocalDate.parse(endStr);

                    results = accountBook.getRecordsByDateRange(startDate, endDate);
                }
                default -> {
                    System.out.println("无效的选择");
                    return;
                }
            }

            // 显示搜索结果
            System.out.println("\n--- 搜索结果 ---");
            if (results.isEmpty()) {
                System.out.println("没有找到匹配的记录");
            } else {
                System.out.println("ID    日期     类型     金额        类别       备注");
                System.out.println("--------------------------------------------------");
                for (Record record : results) {
                    System.out.println(record);
                }
                System.out.println("找到 " + results.size() + " 条记录");
            }

        } catch (DateTimeParseException e) {
            System.out.println("日期格式错误");
        } catch (InputMismatchException e) {
            System.out.println("输入格式错误");
            scanner.nextLine();
        }
    }

    // 系统维护
    private void systemMaintenance() {
        System.out.println("\n--- 系统维护 ---");
        System.out.println("1. 备份数据");
        System.out.println("2. 从备份恢复");
        System.out.println("3. 清空所有数据");
        System.out.print("请选择: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    if (fileHandler.backupData()) {
                        System.out.println("备份成功");
                    }
                }
                case 2 -> {
                    System.out.print("确定要从备份恢复吗？当前数据将丢失 (y/N): ");
                    String confirm = scanner.nextLine().toLowerCase();
                    if (confirm.equals("y") || confirm.equals("yes")) {
                        if (fileHandler.restoreFromBackup()) {
                            // 重新加载数据
                            loadData();
                            System.out.println("恢复成功");
                        }
                    } else {
                        System.out.println("取消恢复");
                    }
                }
                case 3 -> {
                    System.out.print("确定要清空所有数据吗？此操作不可恢复 (y/N): ");
                    String confirm = scanner.nextLine().toLowerCase();
                    if (confirm.equals("y") || confirm.equals("yes")) {
                        accountBook = new AccountBook();
                        System.out.println("数据已清空");
                    } else {
                        System.out.println("取消清空");
                    }
                }
                default -> System.out.println("无效的选择");
            }

        } catch (InputMismatchException e) {
            System.out.println("输入格式错误");
            scanner.nextLine();
        }
    }

    // 生成年度概要
    private void generateYearlySummary() {
        reportGenerator.generateYearlySummary(accountBook);
    }

    // 加载数据
    private void loadData() {
        List<Record> records = fileHandler.loadFromFile();
        accountBook.setRecords(records);

        // 设置下一个ID
        if (!records.isEmpty()) {
            int maxId = records.stream()
                    .mapToInt(Record::getId)
                    .max()
                    .orElse(0);
            accountBook.setNextId(maxId + 1);
        }
    }

    // 保存数据
    private void saveData() {
        System.out.println("正在保存数据...");
        fileHandler.saveToFile(accountBook.getAllRecords());
    }

    // 主方法
    public static void main(String[] args) {
        PersonalAccountBook app = new PersonalAccountBook();
        app.run();
    }
}
