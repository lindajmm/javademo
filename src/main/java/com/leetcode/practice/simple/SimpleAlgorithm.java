package com.leetcode.practice.simple;


import java.util.HashMap;
import java.util.Map;

/**
 * @author: Linda
 * @date: 2026/1/14 17:02
 * @description:
 */
public class SimpleAlgorithm {
    public static int[] twoSum(int[] nums, int target) {
        int[] index = new int[2];
        Map<Integer, Integer> mapValue = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int diff = target - nums[i];
            //先判断map里是否包含 target-nums[i],若包含，则找到两个目标数值
            if (mapValue.containsKey(diff)) {
                index[0] = mapValue.get(diff);
                index[1] = i;
                break;
            }
            //把nums[i]， i 存入map
            mapValue.put(nums[i], i);
        }
        return index;
    }

    public static void main(String[] args) {
//        int[] nums = {0,-8, 11,15,2,3,7};
        int[] nums = {2, 5, 5, 11};
//        int[] nums = {3,2,4};
        int target = 10;
        int[] ints = twoSum(nums, target);
        System.out.println(ints[0] + " and " + ints[1]);

    }
}
