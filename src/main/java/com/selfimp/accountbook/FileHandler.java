package com.selfimp.accountbook;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Linda
 * @date: 2026/1/20 12:30
 * @description:
 */
public class FileHandler {

    private static final String DATA_FILE="src/main/resources/record_data.txt";
    private static final String DATA_FILE_BAK="src/main/resources/record_data_bak.txt";

    // 1.把文件里的数据加载到内存
     List<Record> loadFromFile(){
//        try(BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))){
//        try(List<String> lines = Files.readAllLines(Paths.get(DATA_FILE))){
         List<Record> records = new ArrayList<>();

        try(BufferedReader reader = Files.newBufferedReader((Paths.get(DATA_FILE)))){
            String line;
            while((line = reader.readLine()) != null){
                if(!line.isEmpty()) {

                    if (!line.startsWith("#")) {
                        String[] splits = line.split("\\|");
                        if (splits.length == 6) {
                            Record record =
                                    new Record(Integer.parseInt(splits[0]), LocalDate.parse(splits[1]), splits[2], splits[3], Double.parseDouble(splits[4]), splits[5]);
                            records.add(record);
                        } else {
                            System.out.println("some items has no data, please check it!");
                            throw new RuntimeException();
                        }

                    }
                }
            }
//            accountBook.setNextId(maxId+1);
        } catch (NoSuchFileException e){
            System.out.println("!file not found" + e.getFile());
        } catch (IOException e) {
//            throw new RuntimeException(e);
            System.out.println("Error occured!!");
        }
        return records;
    }

    //2.把内存的数据全部存到文件里
    void saveDataToFile(List<Record> records){
         try(BufferedWriter writer = Files.newBufferedWriter(Paths.get(DATA_FILE))){
             writer.write("# 这是一个个人记账系统");
             writer.newLine();
             writer.write("# ID | 日期| 类型 | 细分类别  | 金额  |  备注");
             writer.newLine();
//             ArrayList<Record> records = accountBook.getRecords();
             for(Record record : records){
                String recordString = record.getId()+"|"+record.getDate()+"|"+record.getType()+"|"+record.getCategory()+"|"+record.getAmount()+"|"+record.getRemark();
                writer.write(recordString);
                writer.newLine();
             }
         } catch (IOException e) {
             throw new RuntimeException(e);
         }
        System.out.println("saved all data!");
    }

    void backupData(){
         try(BufferedReader reader = Files.newBufferedReader(Paths.get(DATA_FILE));
         BufferedWriter writer = Files.newBufferedWriter(Paths.get(DATA_FILE_BAK))) {
             String line;
             while((line = reader.readLine()) != null){
                 writer.write(line);
                 writer.newLine();
             }
         } catch (IOException e) {
             throw new RuntimeException(e);
         }
    }

    boolean recoverDataFromBackupFile() {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(DATA_FILE_BAK));
             BufferedWriter writer = Files.newBufferedWriter(Paths.get(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
        } catch (NoSuchFileException e) {
            System.err.printf("文件不存在： " + e.getFile());
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
