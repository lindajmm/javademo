package com.selfimp.accountbook;


import java.util.ArrayList;
import java.util.List;

/**
 * @author: Linda
 * @date: 2026/1/20 10:57
 * @description:
 */
public class AccountBook {
    private List<Record> records;
    private int nextId;

    public AccountBook() {
        this.records = new ArrayList<>();
        this.nextId = 1;
    }

    public void addRecord(Record record){
       /* //如果nextId > records.size() +1, 说明records 里的数据有过删除操作，导致已有的数据数量+1 少于nextId
        //这种情况，就不需要再重新给nextId 复制
        if(nextId <= records.size() +1){
            nextId = records.size() +1;
        }*/
        record.setId(nextId++);
        records.add(record);
    }



    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    public int getNextId() {
        return nextId;
    }

    public void setNextId(int nextId) {
        this.nextId = nextId;
    }
}
