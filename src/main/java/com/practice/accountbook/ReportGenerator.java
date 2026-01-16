package com.practice.accountbook;


import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

/**
 * @author: Linda
 * @date: 2026/1/15 11:45
 * @description:
 */
public class ReportGenerator {
    //生成月度报表
    public void generateMonthlyReport(int year, int month, AccountBook accountBook){
        System.out.println("\n=================月度报表=============");
        System.out.printf("               %d年%02d月\n",year, month);
        System.out.println("========================================");

        //获取该月记录
        var monthlyRecords = accountBook.getRecordsByMonth(year, month);

        if(monthlyRecords.isEmpty()){
            System.out.println("该月没有记录！");
            return;
        }

        //计算总收入支出
        double totalIncome =0;
        double totalExpense=0;

        System.out.println("\n--- 记录详情 ---");
        System.out.println("ID    日期       类型      金额       类别        备注");
        System.out.println("-----------------------------------------------------");

        for(Record record:monthlyRecords){
            System.out.println(record);
            if("income".equals(record.getType())){
                totalIncome+=record.getAmount();
            }else{
                totalExpense+=record.getAmount();
            }
        }

        System.out.println("\n--- 统计汇总 ---");
        System.out.printf("总收入： ￥%.2f\n", totalIncome);
        System.out.printf("总支出： ￥%.2f\n",totalExpense);
        System.out.printf("月结余： ￥%.2f\n",totalIncome-totalExpense);
        System.out.println("====================================================");
        showCategoryDistribution(year, month, accountBook);
    }

    private void showCategoryDistribution(int year, int month, AccountBook accountBook){
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate=startDate.withDayOfMonth(startDate.lengthOfMonth());

        System.out.println("\n--- 收入类别分布 ---");
        Map<String, Double> incomeSummary=accountBook.getCategorySummary("income", startDate,endDate);
        if(incomeSummary.isEmpty()){
            System.out.println("无收入记录");
        }else{
            for(Map.Entry<String, Double> entry:incomeSummary.entrySet()){
                System.out.printf("%-8s: ￥%.2f\n",entry.getKey(),entry.getValue());
            }
        }

        System.out.println("\n--- 支出类别分布 ---");
        Map<String, Double> expenseSummary=accountBook.getCategorySummary("expense",startDate,endDate);
        if(expenseSummary.isEmpty()){
            System.out.println("无支出记录");
        }else{
            for(Map.Entry<String, Double> entry:expenseSummary.entrySet()){
                System.out.printf("%-8s: ￥%.2f\n",entry.getKey(),entry.getValue());
            }
        }
    }

    //生成年度概要
    public void generateYearlySummary(AccountBook accountBook){
        System.out.println("\n===============年度概要============");

        Map<YearMonth, Map<String, Double>> monthlySummary=accountBook.getMonthlySummary();
        if(monthlySummary.isEmpty()){
            System.out.println("没有记录");
            return;
        }

        System.out.println("月份     收入     支出     结余");
        System.out.println("--------------------------------");
        for(Map.Entry<YearMonth, Map<String, Double>> entry: monthlySummary.entrySet()){
            YearMonth yearMonth=entry.getKey();
            Map<String, Double> monthData = entry.getValue();

            double income=monthData.getOrDefault("income_amount",0.0);
            double expense=monthData.getOrDefault("expense_amount",0.0);
            double balance=income-expense;
            System.out.printf("%s    ￥%8.2f    ￥%8.2f    ￥%8.2f\n",
                    yearMonth.toString(),income,expense,balance);
        }

        System.out.println("----------------------------");
        System.out.printf("总余额： ￥%.2f\n",accountBook.getBalance());
        System.out.println("=============================");
    }
}
