package com.foundation;


/**
 * @author: Linda
 * @date: 2026/3/3 11:20
 * @description:
 */
public class Student {
    private String id;
    private String name;
    private String gender;
    private Integer score;

    public Student(String id, String name, String gender, Integer score) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.score = score;
    }
    public Student(String id, String name, Integer score) {
        this.id = id;
        this.name = name;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", score=" + score +
                '}';
    }
}
