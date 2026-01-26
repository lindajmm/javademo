package com.selfimp.accountbook;


import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: Linda
 * @date: 2026/1/22 15:47
 * @description:
 */
public class ReportGenerator {

    public Map<YearMonth, Map<String, Double>> incomeAndExpenseReport(List<Record> records){
//    public Map<YearMonth, Map<String, Double>> incomeAndExpenseReport(AccountBook accountBook){
//        List<Record> records = accountBook.getRecords();
        if(records == null || records.isEmpty()){
            return null;
        }
        TreeMap<YearMonth, Map<String, Double>> treeMap = new TreeMap<>(Collections.reverseOrder());
//        Map<String, Double> map = new HashMap<>();
        for(Record r : records){
            YearMonth yearMonth = YearMonth.of(r.getDate().getYear(), r.getDate().getMonthValue());
           //{{202510:{支出：100, 收入：200}}, {202512:{支出：120, 收入：400}}}
            treeMap.putIfAbsent(yearMonth, new HashMap<>());
            Map<String, Double> map = treeMap.get(yearMonth);
            map.put(r.getType(), map.getOrDefault(r.getType(),0.0)+r.getAmount());
        }
        return treeMap;
    }

    List<Record> recordSearchByYearMonth(AccountBook accountBook, int year, int month){
        List<Record> records = accountBook.getRecords();

        return  records.stream().filter(record -> {
                    LocalDate date = record.getDate();
                    return date.getYear() == year && date.getMonthValue() == month;
                })
                .collect(Collectors.toList());
    }

    List<Record> categoryStatisticsData(AccountBook accountBook, LocalDate startDate, LocalDate endDate){
        List<Record> records = accountBook.getRecords();

     /* 下面是我写的，这样写：date 晚于startDate且早于endDate， 就不会包含 startDate 和 endDate
       return records.stream()
                .filter(record -> {
                    LocalDate date = record.getDate();
                    return date.isAfter(LocalDate.parse(startDate)) && date.isBefore(LocalDate.parse(endDate));
                })
                .collect(Collectors.toList());*/

//下面是gpt 生产的代码，比我的好， 它是不早于startDate且不晚于endDate， 包含startDate 和 endDate， 比较符合过滤要求
        return records.stream()
                .filter(record -> {
                    LocalDate date = record.getDate();
                    return !date.isBefore(startDate) && !date.isAfter(endDate);
                })
                .collect(Collectors.toList());

    }

}
