package com.selfimp.accountbook;



import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: Linda
 * @date: 2026/1/20 11:05
 * @description:
 */
public class PersonalAccountBook {
    private final AccountBook accountBook;
    private final Scanner scanner;
    private final FileHandler fileHandler;
    private final ReportGenerator reportGenerator;

    private static final String[] INCOME_CATEGORIES = {"工资","理财","兼职","奖金"};
    private static final String[] EXPENSE_CATEGORIES = {"交通","餐饮","娱乐","购物"};

    public PersonalAccountBook() {
        this.accountBook = new AccountBook();
        this.fileHandler = new FileHandler();
        this.reportGenerator = new ReportGenerator();
        this.scanner = new Scanner(System.in);
    }

    public void run(){
        loadData();
        boolean running = true;
        System.out.println("个人账户系统主要功能菜单");
        System.out.println("1 添加记录");
        System.out.println("2 查看所有记录");
        System.out.println("3 查看某月月度报表");
        //用户给出起始日期和结束日期，系统按照细分分类进行汇总，列出 所有子类的总金额，比如交通-100，餐饮-500，等
        System.out.println("4 细分分类统计（按照时间段）");
        System.out.println("5 删除记录");
        System.out.println("6 搜索记录");
        System.out.println("7 系统维护");
        System.out.println("8 年度概要");

        System.out.println("0 退出系统");
        while(running){

            System.out.print("请选择你的操作： ");
            int choice = scanner.nextInt();
            scanner.nextLine();//消耗换行
            switch(choice){
                case 1 -> {
                    addAccountBook();
                    saveData();
                }
                case 2 -> {
                    viewAllRecords();
                }
                case 3 -> {
                    getRecordSearchByYearMonth();
                }
                case 4 -> {
                    getCategoryStatistics();
                }
                case 5 -> {
                    deleteRecord();
                }
                case 6 -> {
                    searchRecord();
                }
                case 7 -> systemMaintenance();
                case 8 -> annualSummary();

                case 0 -> {
                    running = false;
                }

                default -> {
                    System.out.print("无效的选择");
                    return;
                }
            }
        }
        scanner.close();
        System.out.println("欢迎使用个人账户系统！");
    }

    public void addAccountBook(){
        System.out.print("请输入日期(格式为 YYYY-MM-DD): ");
        String dateStr = scanner.nextLine();
        LocalDate date;
        if(dateStr == null || dateStr.trim().isEmpty()){
            System.out.println("Not enter date, it will use today as default date");
            date = LocalDate.now();
        }else{
//            date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("YYYY-MM-DD"));
            date = LocalDate.parse(dateStr);
        }
        System.out.println("收支类型：1 收入, 2 支出");
        System.out.print("请选择收支类型: ");
        int typeValue = scanner.nextInt();
        scanner.nextLine();
        String type= typeValue ==1 ? "收入" : "支出";
        String [] categories = typeValue == 1 ? INCOME_CATEGORIES : EXPENSE_CATEGORIES;
        System.out.println("类别如下： ");
        for(int i=0; i< categories.length; i++){
            System.out.println((i+1) + " " + categories[i]);
        }

        System.out.print("请选择具体的类别： ");
        int categoryValue = scanner.nextInt();
        scanner.nextLine();
        String category = categories[categoryValue-1];

        System.out.print("请输入金额： ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("请输入备注信息： ");
        String remark = scanner.nextLine();

        accountBook.addRecord(new Record(0,date,type, category, amount, remark));
    }

    private void viewAllRecords(){
        List<Record> records = accountBook.getRecords();
        printRecordDetails(records);
    }

    private void printRecordDetails(List<Record> records){
        System.out.printf("%-4s|%-12s|%-4s|%-4s|%-8s|%-12s\n","ID","日期","类型","细分类别","金额","备注");
        for(Record r : records){
            System.out.printf("%-4s|%-12s|%-4s|%-6s|%8.2f|%-12s\n",r.getId(),r.getDate(),r.getType(),r.getCategory(),r.getAmount(),r.getRemark());
        }
    }

    private void getRecordSearchByYearMonth(){
        System.out.print("请输入你需要查找的年份（YYYY）： ");
        int year = scanner.nextInt();
        scanner.nextLine();
        System.out.print("请输入你需要查找的月份（MM）： ");
        int month = scanner.nextInt();
        scanner.nextLine();

        List<Record> results = reportGenerator.recordSearchByYearMonth(accountBook, year, month);
        if(results.isEmpty()){
            System.out.printf("No data in %s-%s\n", year, month);
            return;
        }
        printRecordDetails(results);
    }

    private void getCategoryStatistics(){
        System.out.print("请输入你要查询的开始日期(yyyy-MM-dd): ");
        LocalDate startDate = LocalDate.parse(scanner.nextLine());
        System.out.print("请输入你要查询的结束日期(yyyy-MM-dd): ");
        LocalDate endDate = LocalDate.parse(scanner.nextLine());

        System.out.println("系统所有的细分分类如下：");
        System.out.println("收入的细分分类包括：");
        for(int i=0; i< INCOME_CATEGORIES.length; i++){
            System.out.println((i+1) + " " + INCOME_CATEGORIES[i]);
        }
        System.out.println("支出的细分分类包括：");
        for(int i=0; i< EXPENSE_CATEGORIES.length; i++){
            System.out.println((i+1) + " " + EXPENSE_CATEGORIES[i]);
        }

        List<Record> records = reportGenerator.categoryStatisticsData(accountBook, startDate, endDate);
        //用户给出起始日期和结束日期，系统按照细分分类进行汇总，列出 所有子类的总金额，比如交通-100，餐饮-500，等
        //收入
        Map<String, Double> incomeMap = new HashMap<>();
        for(int i=0; i<INCOME_CATEGORIES.length; i++){
            String category = INCOME_CATEGORIES[i];
            double sum = records.stream().filter(record -> record.getCategory().equals(category))
                    .mapToDouble(Record :: getAmount)
                    .sum();
            incomeMap.put(category,sum);
        }
        //支出
        Map<String, Double> expenseMap = new HashMap<>();
        for(int i=0; i<EXPENSE_CATEGORIES.length; i++){
            String category = EXPENSE_CATEGORIES[i];
            double sum = records.stream().filter(record -> record.getCategory().equals(category))
                    .mapToDouble(Record :: getAmount)
                    .sum();
            expenseMap.put(category,sum);
        }
        Map<String, Map<String, Double>> summaryStatistics = new HashMap<>();
        summaryStatistics.put("收入", incomeMap);
        summaryStatistics.put("支出", expenseMap);
        System.out.printf("%-4s|%-4s|%-8s\n", "类型","细分分类","总金额");
        for(Map.Entry<String, Map<String, Double>> typeStatistics : summaryStatistics.entrySet()){
            for(Map.Entry<String, Double> item : typeStatistics.getValue().entrySet()){
                System.out.printf("%-4s|%-6s|%8.2f\n", typeStatistics.getKey(),item.getKey(),item.getValue());
            }
        }
    }

    private void deleteRecord(){
        System.out.print("请输入你要删除的记录的ID: ");
        int idToBeDeleted = scanner.nextInt();
        scanner.nextLine();
        //先查询是否存在该数据
        List<Record> records = accountBook.getRecords();
        Record record = records.stream().filter(r -> r.getId() == idToBeDeleted)
                .findFirst()
                .orElse(null);
        if(record == null){
            System.out.printf("Can't find any record by this id: %s\n",idToBeDeleted);
            return;
        }
        System.out.println("find the record by id ");
        System.out.printf("%-4s|%-12s|%-4s|%-4s|%-8s|%-12s\n","ID","日期","类型","细分类别","金额","备注");
        System.out.printf("%-4s|%-12s|%-4s|%-6s|%8.2f|%-12s\n",record.getId(),record.getDate(),record.getType(),record.getCategory(),record.getAmount(),record.getRemark());
        System.out.print("are you sure you want to delete it(Y/N): ");
        String confirm = scanner.nextLine();
        if(confirm.equalsIgnoreCase("Y") || confirm.equalsIgnoreCase("yes")){
            records.remove(record);
            System.out.println("删除成功");
        }
        saveData();
    }

    private void searchRecord(){
        System.out.println("1.按照时间范围搜索");
        System.out.println("2.按照细分类别搜索");
        System.out.print("请选择你要搜索的方式: ");
        int searchChoice = scanner.nextInt();
        scanner.nextLine();
        try{
            switch(searchChoice){
                case 1 -> {
                    System.out.print("请输入你要查询的开始日期(yyyy-MM-dd): ");
                    LocalDate startDate = LocalDate.parse(scanner.nextLine());
                    System.out.print("请输入你要查询的结束日期(yyyy-MM-dd): ");
                    LocalDate endDate = LocalDate.parse(scanner.nextLine());
                    List<Record> records = reportGenerator.categoryStatisticsData(accountBook, startDate, endDate);
                    printRecordDetails(records);
                }
                case 2 -> {
                    System.out.print("请输入你要查询细分类别: ");
                    String category = scanner.nextLine();

                    List<Record> collect = accountBook.getRecords().stream()
                            .filter(r -> r.getCategory().equals(category))
                            .collect(Collectors.toList());
                    printRecordDetails(collect);
                }
                default -> {
                    System.out.println("无效的搜索方式！");
                }
            }
        }catch (DateTimeParseException e){
            System.out.println("请确保日期格式是YYYY-MM-DD.");
            System.out.println(e.getMessage());
        }catch(InputMismatchException e){
            System.out.println("输入格式有错误！");
            System.out.println(e.getMessage());
        }
    }

    private void systemMaintenance(){
        System.out.println("1.备份数据");
        System.out.println("2.从备份中恢复数据");
        System.out.print("请选择你的想要的操作: ");
        int choiceType = scanner.nextInt();
        scanner.nextLine();
        switch(choiceType){
            case 1 -> fileHandler.backupData();
            case 2 -> {
               boolean isTrue =  fileHandler.recoverDataFromBackupFile();
               if(isTrue){
                   loadData();
                   System.out.println("从备份文件恢复成功");
               }
            }
            default -> System.out.println("无效的选择！");

        }
    }
    
    private void annualSummary(){
        System.out.print("请输入年份(YYYY): ");
        Integer yearValue = scanner.nextInt();

        List<Record> collect = accountBook.getRecords().stream()
                .filter(record -> record.getDate().getYear() == yearValue)
                .collect(Collectors.toList());

        if(collect.isEmpty()){
            System.out.printf("No data of year %d !", yearValue);
            return;
        }
        Map<YearMonth, Map<String, Double>> yearMonthData = reportGenerator.incomeAndExpenseReport(collect);
        double incomeSum=0.0;
        double expenseSum=0.0;
        System.out.printf("%-8s|%-8s|%-8s\n", "日期", "月度总收入","月总支出");
        for(Map.Entry<YearMonth, Map<String, Double>> entry : yearMonthData.entrySet()){
            YearMonth key = entry.getKey();
            Map<String, Double> value = entry.getValue();
            double income = 0.0;
            double expense = 0.0;

            for(Map.Entry<String, Double> subEntry : value.entrySet()){
//                System.out.printf("%-10s|%-6s|%8.2f\n", key, subEntry.getKey(), subEntry.getValue());
                if(subEntry.getKey().equals("收入")){
                    income = subEntry.getValue();
                    incomeSum += subEntry.getValue();
                }else{
                    expense = subEntry.getValue();
                    expenseSum += subEntry.getValue();
                }
            }
            System.out.printf("%-8s|%-8.2f|%-8.2f\n", key, income,expense);

        }
        System.out.println("===========================================");
        System.out.printf("%-4s年|总收入：%-8.2f|总支出：%-8.2f\n", yearValue, incomeSum,expenseSum);
    }

    private void getAnnualIncomeAndExpenseReport(int year){
        Map<YearMonth, Map<String, Double>> yearMonthMap = reportGenerator.incomeAndExpenseReport(accountBook.getRecords());
//        Map<YearMonth, Map<String, Double>> yearMonthMap = reportGenerator.incomeAndExpenseReport(accountBook);
        if(yearMonthMap == null || yearMonthMap.isEmpty()){
            System.out.println("No data!");
            return;
        }

        System.out.printf("%-10s|%-6s|%-8s\n", "日期", "类型","金额");
        for(Map.Entry<YearMonth, Map<String, Double>> entry : yearMonthMap.entrySet()){
            YearMonth key = entry.getKey();
            Map<String, Double> value = entry.getValue();

            for(Map.Entry<String, Double> subEntry : value.entrySet()){
                System.out.printf("%-10s|%-6s|%8.2f\n", key, subEntry.getKey(), subEntry.getValue());
            }

        }
    }

    private void loadData(){
        List<Record> records = fileHandler.loadFromFile();
        accountBook.setRecords(records) ;

        //AI 给的答案
        if(!records.isEmpty()){
            int maxID = records.stream()
                    .mapToInt(Record::getId)
                    .max()
                    .orElse(0);
            accountBook.setNextId(maxID +1);

        }

      /*  int maxId=0;
        for(Record record : accountBook.getRecords()){
            maxId = Math.max(record.getId(), maxId);
        }
        accountBook.setNextId(maxId +1);*/
    }

    private void saveData(){
        fileHandler.saveDataToFile(accountBook.getRecords());
    }

    public static void main(String[] args) {

    }
}
