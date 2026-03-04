package com.foundation;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author: Linda
 * @date: 2026/3/3 14:13
 * @description:
 */
public class FunctionInterface {
    public static void main(String[] args) {
        //Supplier -供应者
        Supplier<Double> randomSupplier = () -> Math.random();
        Supplier<Double> randomSupplier1 = Math :: random;
        System.out.println(randomSupplier.get());
        System.out.println(randomSupplier1.get());

        //Consumer - 消费者
        List<Integer> arrayList = new ArrayList<>();
        Consumer<Integer> adder = num -> arrayList.add(num);
        adder.accept(10);
        adder.accept(20);
        System.out.println("列表内容 "+arrayList);
        //组合使用
        Consumer<String> sayHello = s -> System.out.print("Hello ");
        Consumer<String> sayName = s -> System.out.println(s);
        Consumer<String> greet = sayHello.andThen(sayName);
        greet.accept("Java");

        //Function 转换函数，有输入有输出
        //计算字符串长度
        Function<String, Integer> getLength = s -> s.length();
        System.out.println("Java 的长度是 "+ getLength.apply("Java"));
        //组合使用
        Function<String, String> trimFun = s -> s.trim();
        Function<String, Integer> getLen = s -> s.length();
        Function<String, Integer> trimThenLen = trimFun.andThen(getLen);
        System.out.println("去掉前后空格之后，字符串长度为 "+ trimThenLen.apply("  hello world  "));

        //Predicate  - 预言判断
        Predicate<Integer> isPositive = i -> i>0;
        System.out.println("5 > 0: "+isPositive.test(5));
        System.out.println("-5 > 0: "+isPositive.test(-5));

        Predicate<Integer> isEven = i -> i%2==0;
        Predicate<Integer> isGreaterThanTwenty = i -> i>20;

        Predicate<Integer> isEvenAndGreaterThanTwenty = isEven.and(isGreaterThanTwenty);
        System.out.println("is 18 an odd and greater than 20: "+ isEvenAndGreaterThanTwenty.test(18));
        System.out.println("is 24 an odd and greater than 20: "+ isEvenAndGreaterThanTwenty.test(24));

    }
}
