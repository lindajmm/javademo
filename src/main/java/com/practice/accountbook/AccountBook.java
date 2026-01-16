package com.practice.accountbook;


import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: Linda
 * @date: 2026/1/15 12:01
 * @description:
 */
public class AccountBook {
    private List<Record> records;
    private int nextId;

    public static final String[] INCOME_CATEGORIES = {"工资","奖金","兼职","投资","其他收入"};
    public static final String[] EXPENSE_CATEGORIES = {"餐饮","交通","购物","娱乐","其他收入"};

    public AccountBook(){
        this.records = new ArrayList<>();
        this.nextId = 1;
    }
    //添加记录
    public void addRecord(Record record){
        record.setId(nextId++);
        records.add(record);
        //按日期倒序排序
//        records.sort((r1,r2) -> r2.getDate().compareTo(r1.getDate()));
        records.sort(Comparator.comparing(Record :: getDate).reversed());
    }
    //删除记录
    public boolean deleteRecord(int id){return records.removeIf(record -> record.getId()==id);}
    //获取所有记录
    public List<Record> getAllRecords() {return new ArrayList<>(records);}
    //获取某月的记录
    public List<Record> getRecordsByMonth(int year, int month){
        return records.stream()
                .filter(record -> {
                    LocalDate date = record.getDate();
                    return date.getYear() == year && date.getMonthValue() ==month;
                })
                .collect(Collectors.toList());
    }
    //获取日期范围内的记录
    public List<Record> getRecordsByDateRange(LocalDate startDate, LocalDate endDate){
        return records.stream()
                .filter(record -> {
                    LocalDate date = record.getDate();
                    return !date.isBefore(startDate) && !date.isAfter(endDate);
                })
                .collect(Collectors.toList());
    }
    //按类别统计
    public Map<String, Double> getCategorySummary(String type, LocalDate startDate, LocalDate endDate){
        Map<String, Double> summary=new HashMap<>();

        List<Record> filteredRecords = getRecordsByDateRange(startDate, endDate).stream()
                .filter(record -> type.equals(record.getType()))
                .collect(Collectors.toList());
        for(Record record : filteredRecords){
            String category=record.getCategory();
            double amount=record.getAmount();
            summary.put(category, summary.getOrDefault(category,0.0)+amount);
        }
        return summary;
    }
    //计算总余额
    public double getBalance(){
        double balance =0;
        for(Record record: records){
            if("income".equals(record.getType())){
                balance+=record.getAmount();
            }else{
                balance-=record.getAmount();
            }
        }
        return balance;
    }

    //按月统计收支
    public Map<YearMonth, Map<String, Double>> getMonthlySummary(){
        TreeMap<YearMonth, Map<String, Double>> monthlySummary = new TreeMap<>(Collections.reverseOrder());
//        TreeMap<YearMonth, Map<String, Double>> monthlySummary = new TreeMap<>();
        for(Record record:records){
            YearMonth yearMonth=YearMonth.from(record.getDate());
            monthlySummary.putIfAbsent(yearMonth,new HashMap<>());
            Map<String, Double> monthData = monthlySummary.get(yearMonth);
            String key = record.getType() + "_amount";
            monthData.put(key, monthData.getOrDefault(key, 0.0)+record.getAmount());

            String countKey=record.getType()+"_count";
            monthData.put(countKey, monthData.getOrDefault(countKey, 0.0)+1);

        }
        return monthlySummary;
    }

    //根据ID查找记录
    public Record findRecordById(int id){
        return records.stream()
                .filter(record -> record.getId()==id)
                .findFirst()
                .orElse(null);
    }

    public int getNextId(){ return nextId;}

    public void setNextId(int nextId){this.nextId = nextId;}

    //设置记录列表（用于从文件加载）
    public void setRecords(List<Record> records){
        this.records = records;
        //重新排序
        /*this.records.sort((r1,r2) -> r2.getDate().compareTo(r1.getDate()));
        records.sort(Comparator.comparing(Record :: getDate).reversed());*/
        this.records.sort(Comparator.comparing(Record :: getDate).reversed());

    }
}

