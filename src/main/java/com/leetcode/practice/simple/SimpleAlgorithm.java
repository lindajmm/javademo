package com.leetcode.practice.simple;


import java.util.HashMap;
import java.util.Map;

/**
 * @author: Linda
 * @date: 2026/1/14 17:02
 * @description:
 */
public class SimpleAlgorithm {

    //input nums={0,1,0,3,12}, output = {1,3,12,0,0}
    public static void moveZeroes(int[] nums){
        if(nums == null || nums.length <=1){
            return;
        }
        int nonZeroIndex=0;
        for(int i=0; i< nums.length; i++){
            if(nums[i] != 0){
                if(i != nonZeroIndex){
                    nums[nonZeroIndex] = nums[i];
                    nums[i]=0;
                }
                nonZeroIndex++;
            }
        }
        /*for(int k=nonZeroIndex; k< nums.length; k++){
            nums[k] =0;
        }*/
    }

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
/*//        int[] nums = {0,-8, 11,15,2,3,7};
        int[] nums = {2, 5, 5, 11};
//        int[] nums = {3,2,4};
        int target = 10;
        int[] ints = twoSum(nums, target);
        System.out.println(ints[0] + " and " + ints[1]);*/
        int[] nums={0,1,0,3,12};
        moveZeroes(nums);
        for(int i : nums){
            System.out.println(i);
        }
        System.out.println("done");

    }
}
