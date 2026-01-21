package com.selfimp.accountbook;



import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author: Linda
 * @date: 2026/1/20 11:05
 * @description:
 */
public class PersonalAccountBook {
    private AccountBook accountBook;
    private Scanner scanner;
    private FileHandler fileHandler;

    private static final String[] INCOME_CATEGORIES = {"工资","理财","兼职","奖金"};
    private static final String[] EXPENSE_CATEGORIES = {"交通","餐饮","娱乐","购物"};

    public PersonalAccountBook() {
        this.accountBook = new AccountBook();
        this.fileHandler = new FileHandler();
        this.scanner = new Scanner(System.in);
    }

    public void run(){
        loadData();
        boolean running = true;
        System.out.println("个人账户系统主要功能菜单");

        while(running){
            System.out.println("1 添加记录");
            System.out.println("0 退出系统");
            System.out.print("请选择你的操作： ");
            int choice = scanner.nextInt();
            scanner.nextLine();//消耗换行
            switch(choice){
                case 1 -> {
                    addAccountBook();
                    saveData();
                }

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
