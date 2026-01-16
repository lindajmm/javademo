package com.practice.accountbook;


import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: Linda
 * @date: 2026/1/15 15:04
 * @description: 个人记账本主程序
 */
public class PersonalAccountBook {
    private AccountBook accountBook;
    private FileHandler fileHandler;
    private ReportGenerator reportGenerator;
    private Scanner scanner;

    public PersonalAccountBook(){
        this.accountBook=new AccountBook();
        this.fileHandler=new FileHandler();
        this.reportGenerator=new ReportGenerator();
        this.scanner=new Scanner(System.in);
    }

    public void run(){
        //加载数据
        loadData();

        System.out.println("==============================");
        System.out.println("    欢迎使用个人记账本系统");
        System.out.println("==============================");

        boolean running=true;
        while(running){
            showMainMenu();
            System.out.print("请选择操作(0-8): ");

            try{
                int choice = scanner.nextInt();
                scanner.nextLine();//消耗换行符

                switch(choice){
                    case 1 -> {
                        addRecord();
                        saveDate();
                    }
                    case 2 -> viewAllRecords();
                    case 3 -> viewMonthlyReport();
                    case 4 -> viewCategoryStatistics();
                    case 5 -> deleteRecord();
                    case 6 -> searchRecords();
                    case 7 -> {
                        systemMaintenance();
                        saveDate();
                    }
                    case 8 -> generateYearlySummary();
                    case 0 -> {
                        running = false;
                        System.out.println("正在退出系统...");
                    }
                    default -> System.out.println("无效的选择，请重新输入");
                }
                if(choice !=0){
                    System.out.println("\n 按回车键继续...");
                    scanner.nextLine();
                }
            }catch (InputMismatchException e){
                System.out.println("please enter a valid number!");
                scanner.nextLine();//清楚无效输入
            }
        }
        scanner.close();
        System.out.println("thank you using our personal account book system!");
    }

    //分类统计
    private void viewCategoryStatistics(){
        System.out.println("\n--- 分类统计 ---");
        try{
            System.out.print("请选择统计类型(1. income / 2. expense): ");
            int typeChoice=scanner.nextInt();
            scanner.nextLine();

            String type=(typeChoice==1)? "income" : "expense";
            System.out.print("please input start date(YYYY-MM-DD): ");
            String startStr=scanner.nextLine();
            System.out.print("please input end date(YYYY-MM-DD): ");
            String endStr=scanner.nextLine();

            LocalDate startDate=LocalDate.parse(startStr);
            LocalDate endDate=LocalDate.parse(endStr);

            if(startDate.isAfter(endDate)){
                System.out.println("start date can't be later than end date");
                return;
            }

            Map<String, Double> summary = accountBook.getCategorySummary(type, startDate, endDate);

            System.out.println("\n--- statistics results ("+startDate+" to "+endDate+" ) ---");
            if(summary.isEmpty()){
                System.out.println("no record of "+(typeChoice==1?"incoming":"expensing"));
            }else{
                double total =0;
                for(Map.Entry<String, Double> entry : summary.entrySet()){
                    System.out.printf("%-10s: ￥%.2f\n",entry.getKey(), entry.getValue());
                    total+=entry.getValue();
                }
                System.out.println("-------------------------");
                System.out.printf("总计： ￥%.2f\n", total);
            }
        }catch(DateTimeParseException e){
            System.out.println("date format error, please use the format of YYYY-MM-DD");
        }catch(InputMismatchException e){
            System.out.println("input format error!");
            scanner.nextLine();
        }
    }

    private void deleteRecord(){
        System.out.println("\n--- deleting one record ---");
        viewAllRecords();
        System.out.print("please input the record ID that you want to delete(0 cancel): ");

        try{
            int id=scanner.nextInt();
            scanner.nextLine();
            if(id==0){
                System.out.println("cancel the delete operation");
                return;
            }

            Record record = accountBook.findRecordById(id);
            if(record ==null){
                System.out.println("id: "+ id+ " not found");
                return;
            }
            System.out.println("found the record: "+ record);
            System.out.print("Are you sure you want to delete it? (y/N): ");
            String confirm = scanner.nextLine().toLowerCase();

            if(confirm.equals("y") || confirm.equals("yes")){
                if(accountBook.deleteRecord(id)){
                    System.out.println("The record has been deleted successfully");
                }else{
                    System.out.println("Failed to delete the record");
                }
            }else{
                System.out.println("Cancel the delete operation");
            }
        }catch(InputMismatchException e){
            System.out.println("please input one valid ID");
            scanner.nextLine();
        }
    }

    private void searchRecords(){
        System.out.println("\n--- search records ---");
        System.out.println("1. search by category");
        System.out.println("2. search by date range");
        System.out.print("please select the search method");

        try{
            int choice = scanner.nextInt();
            scanner.nextLine();
            List<Record> results = new ArrayList<>();

            switch(choice){
                case 1 -> {
                    //search by category
                    System.out.print("please input category name: ");
                    String category=scanner.nextLine();
                    accountBook.getAllRecords().stream().
                            filter(r -> r.getCategory().contains(category))
                            .collect(Collectors.toList());
                }
                case 2 -> {
                    System.out.print("please input the start date(YYYY-MM-DD): ");
                    String startStr=scanner.nextLine();

                    System.out.print("please input the end date(YYYY-MM-DD): ");
                    String endStr=scanner.nextLine();

                    LocalDate startDate = LocalDate.parse(startStr);
                    LocalDate endDate = LocalDate.parse(endStr);

                    results=accountBook.getRecordsByDateRange(startDate, endDate);
                }
                default -> {
                    System.out.println("invalid choice");
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

    private void systemMaintenance(){
        System.out.println("\n--- 系统维护 ---");
        System.out.println("1. 备份数据");
        System.out.println("2. 从备份恢复");
        System.out.println("3. 清空所有数据");
        System.out.print("请选择： ");

        try{
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch(choice){
                case 1-> {
                    if(fileHandler.backupData()){
                        System.out.println("备份成功");
                    }
                }
                case 2-> {
                    System.out.print("确定要从备份恢复吗？ 当前数据将丢失 (y/N): ");
                    String confirm = scanner.nextLine().toLowerCase();
                    if(confirm.equals("y") ||confirm.equals("yes")){
                        if(fileHandler.restoreFromBackup()){
                            //重新加载数据 - 从txt文件加载到程序中
                            loadData();
                            System.out.println("恢复成功");
                        }
                    }else{
                        System.out.println("取消恢复");
                    }
                }
                case 3 -> {
                    System.out.print("确定要清空所有数据吗？ 此操作不可恢复 (y/N): ");
                    String confirm =scanner.nextLine().toLowerCase();
                    if(confirm.equals("y") || confirm.equals("yes")){

                        accountBook = new AccountBook();

                        System.out.println("数据已清空");
                    }else{
                        System.out.println("取消清空");
                    }
                }
                default -> System.out.println("无效的选择");
            }
        }catch (InputMismatchException e){
            System.out.println("输入格式错误");
            scanner.nextLine();
        }
    }
//生成年度概要
    private void generateYearlySummary(){
        reportGenerator.generateYearlySummary(accountBook);
    }

    private void viewAllRecords(){
        System.out.println("\n --- all accounting records ---");

        List<Record> allRecords = accountBook.getAllRecords();

        if(allRecords.isEmpty()){
            System.out.println("no records yet");
            return;
        }

        System.out.println("ID    date    type   amount    category    remark");;
        System.out.println("--------------------------------------------------------");

        for(Record record: allRecords){
            System.out.println(record);
        }

        System.out.println("-----------------------------------------------------");
        System.out.println("a total of "+allRecords.size()+" records");
    }

    private void viewMonthlyReport(){
        System.out.println("\n--- 月度报表 ---");

        try{
            System.out.print("请输入年份 (如: 2024): ");
            int year = scanner.nextInt();

            System.out.print("请输入月份 (如: 1-12): ");
            int month=scanner.nextInt();
            scanner.nextLine();//不加这句会怎样？？

            if(month <1 || month >12){
                System.out.println("invalid month");
                return;
            }

            reportGenerator.generateMonthlyReport(year, month, accountBook);
        }catch (InputMismatchException e){
            System.out.println("incorrect input format");
            scanner.nextLine();
        }
    }

    private void addRecord(){
        System.out.println("\n --- 添加记账记录 ---");

        try{
            System.out.print("请选择类型 (1.收入 / 2.支出): ");
            int typeChoice = scanner.nextInt();
            scanner.nextLine();//消耗换行符

            if(typeChoice != 1 && typeChoice !=2){
                System.out.println("无效的选择");
                return;
            }

            String type=(typeChoice ==1) ? "income":"expense";

            //输入金额
            System.out.print("请输入金额： ");
            double amount=scanner.nextDouble();
            scanner.nextLine();

            if(amount <=0){
                System.out.println("金额必须大于0");
                return;
            }

            //选择类别
            System.out.println("\n请选择类别");
            String[] categories=(typeChoice ==1) ?
                    AccountBook.INCOME_CATEGORIES : AccountBook.EXPENSE_CATEGORIES;
            for(int i=0; i< categories.length; i++){
                System.out.printf("%d. %s\n", i+1, categories[i]);
            }
            System.out.print("请选择 (1-"+categories.length +"): ");

            int categoryChoice=scanner.nextInt();
            scanner.nextLine();
            if(categoryChoice <1 || categoryChoice > categories.length){
                System.out.println("invalid choice, will use default category");;
                categoryChoice= categories.length;
            }

            String category=categories[categoryChoice-1];

            //输入日期
            LocalDate date;
            System.out.print("please input date (YYYY-MM-DD, or use today by entering Enter): ");
            String dateStr=scanner.nextLine().trim();

            if(dateStr.isEmpty()){
                date = LocalDate.now();
            } else{
                try{
                    date =LocalDate.parse(dateStr);
                }catch(DateTimeParseException e){
                    System.out.println("wrong date format, will use today as the date");
                    date = LocalDate.now();
                }
            }

            //input remark
            System.out.print("Please input remark: ");
            String remark=scanner.nextLine();

            //create a account book record
            Record record=new Record(0, date,type,amount,category, remark);
            accountBook.addRecord(record);
            System.out.println("record added successfully");
            System.out.println("the record is : "+ record);
        }catch(InputMismatchException e){
            System.out.println("input format is incorrect");
            scanner.nextLine();
        }
    }

    private void saveDate(){
        System.out.println("saving data ...");
        fileHandler.saveToFile(accountBook.getAllRecords());
    }

    private void showMainMenu(){
        System.out.println("\n==== 个人记账本系统 ====");
        System.out.println("1. 添加记账记录");
        System.out.println("2. 查看所有记录");
        System.out.println("3. 月度报表");
        System.out.println("4. 分类统计");
        System.out.println("5. 删除记录");
        System.out.println("6. 搜索记录");
        System.out.println("7. 系统维护");
        System.out.println("8. 年度概要");
        System.out.println("0. 退出系统");
        System.out.println("当前余额： ￥"+ String.format("%.2f", accountBook.getBalance()));
        System.out.println("====================================");
    }

    private void loadData(){
        List<Record> records = fileHandler.loadFromFile();
        accountBook.setRecords(records);

        //设置下一个ID
        if(!records.isEmpty()){
            int maxId=records.stream()
                    .mapToInt(Record::getId)
                    .max()
                    .orElse(0);
            accountBook.setNextId(maxId+1);
        }
    }
}
