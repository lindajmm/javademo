package com.foundation;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: Linda
 * @date: 2026/3/3 11:06
 * @description:
 */
public class LindaStream {
    public static void main(String[] args) {
        List<Student> list = Arrays.asList(
               new Student("1","Tom",90),
               new Student("2","Cat",89),
               new Student("3","Lyne",69),
               new Student("4","Oscar",69)
        );

        List<Student> greaterThanSeventy = list.stream()
                .filter(s -> s.getScore() > 70)
                .collect(Collectors.toList());
        System.out.println(greaterThanSeventy);

        Map<String, String> greaterThanSeventy11 = list.stream()
                .filter(s -> s.getScore() > 70)
                .collect(Collectors.toMap(
                        Student :: getId,
                        Student :: getName
                ));

        System.out.println(greaterThanSeventy11);

        List<Student> students = Arrays.asList(
                new Student("1","Tom", "Male", 90),
                new Student("2","Cat","Female",89),
                new Student("3","Lyne","Female",69),
                new Student("4","Oscar","Male",92),
                new Student("5","Lyne1","Female",76),
                new Student("6","Lyne2","Female",100),
                new Student("7","Lyne3","Female",30),
                new Student("8","Lyne4","Female",56),
                new Student("9","Oscar1","Male",82),
                new Student("10","Oscar2","Male",77),
                new Student("11","Oscar3","Male",99),
                new Student("12","Oscar4","Male",89),
                new Student("13","Oscar5","Male",69),
                new Student("14","Oscar6","Male",59),
                new Student("15","Oscar7","Male",49)
        );
        //分数去重，排序，取前十
        List<Integer> collect111 = students.stream()
                .map(Student::getScore)
                .distinct()
                .sorted(Collections.reverseOrder())
                .limit(10)
                .collect(Collectors.toList());
        System.out.println(collect111);

        Map<String, List<Student>> collect555 = students.stream()
                .collect(Collectors.groupingBy(Student::getGender));

        System.out.println(collect555);
    }
}
