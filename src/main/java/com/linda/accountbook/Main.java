package com.linda.accountbook;



import java.util.Map;
import java.util.Properties;


/**
 * @author: Linda
 * @date: 2026/1/13 16:12
 * @description:
 */

public class Main {
    public static void main(String[] args) {
        PersonalAccountBook app = new PersonalAccountBook();
        app.run();

//        Map<String, String> tempProps = SystemProps.initProperties();
//        Map<String, String> tempProps = SystemProps.initProperties();
//        String lineSeparator = tempProps.getProperty("line.separator");
      /*  Properties properties = System.getProperties();
        Map<String, String> getenv = System.getenv();
        String s = getenv.get("line.separator");
        Object o = properties.get("line.separator");*/
    }
}