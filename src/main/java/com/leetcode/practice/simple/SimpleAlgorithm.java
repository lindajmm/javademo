package com.leetcode.practice.simple;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

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
/*

    public static ListNode getIntersectionNode(ListNode headA, ListNode headB) {
       if(headA == null || headB == null){
           return null;
       }
       ListNode PA = headA;
       ListNode PB = headB;
       while(PA != PB){
           PA = (PA == null) ? headB : PA.next;
           PB = (PB == null) ? headA : PB.next;
           */
/*下面这样写，也不对，如果两个链表没有交集，
           下面的写法会造成无限循环（PA,PB永远也不会等于null，因为他们都没有走到null,就被复制给head了），
          PA: 2->6->4->null
          PB: 1->5->null

           PA的循环是：2-6-4-1-5-1-5-1-5-...
           PB的循环是：1-5-2-6-4-2-6-4-2-...

           如果写成
           PA = (PA == null) ? headB : PA.next;
           PB = (PB == null) ? headA : PB.next;

           PA的循环是：2-6-4-null-1-5-null
           PB的循环是：1-5-null-2-6-4-null
           第七次while 判断时， PA, PB 都是null, 说明没有相交的部分，返回null*//*


          */
/* PA = (PA.next == null) ? headB : PA.next;
           PB = (PB.next == null) ? headA : PB.next;*//*


         */
/*下面的代码不对，如果PA 或者PB 到了最后一个节点就接着指向两一个节点的头部
          PA = (PA.next == null) ? PB : PA.next;
           PB = (PB.next == null) ? PA : PB.next;*//*

       }
        return PA;
        */
/*if (headA == null || headB == null) {
            return null;
        }

        ListNode pA = headA;
        ListNode pB = headB;

        // 两个指针遍历两个链表
        // 第一轮遍历结束后，交换路径，使两个指针走过相同的总长度
        // 如果相交，会在相交点相遇；如果不相交，会在null处相遇
        while (pA != pB) {
            // 如果指针A到达末尾，转向链表B
            pA = (pA == null) ? headB : pA.next;
            // 如果指针B到达末尾，转向链表A
            pB = (pB == null) ? headA : pB.next;
        }

        return pA; // 返回相交节点或null*//*

    }
*/




    public static void main(String[] args) {
/*//        int[] nums = {0,-8, 11,15,2,3,7};
        int[] nums = {2, 5, 5, 11};
//        int[] nums = {3,2,4};
        int target = 10;
        int[] ints = twoSum(nums, target);
        System.out.println(ints[0] + " and " + ints[1]);*/
        /*int[] nums={0,1,0,3,12};
        moveZeroes(nums);
        for(int i : nums){
            System.out.println(i);
        }
        System.out.println("done");*/
      /*  ListNode common = new ListNode(8);
        common.next = new ListNode(4);
        common.next.next = new ListNode(5);

        ListNode headA = new ListNode(4);
        headA.next = new ListNode(1);
        headA.next.next = common;

        ListNode headB = new ListNode(5);
        headB.next = new ListNode(6);
        headB.next.next = new ListNode(1);
        headB.next.next.next = common;*/

      /*  ListNode common = new ListNode(2);
        common.next = new ListNode(4);

        ListNode headA = new ListNode(1);
        headA.next = new ListNode(9);
        headA.next.next = new ListNode(1);
        headA.next.next.next = common;

        ListNode headB = new ListNode(3);
        headB.next = common;*/

       /* ListNode headA = new ListNode(2);
        headA.next = new ListNode(6);
        headA.next.next = new ListNode(4);

        ListNode headB = new ListNode(1);
        headB.next = new ListNode(5);

        ListNode intersectionNode = getIntersectionNode(headA, headB);*/

        ListNode node = new ListNode(1);
        node.next=new ListNode(2);
        node.next.next=new ListNode(3);
        node.next.next.next=new ListNode(4);
        node.next.next.next.next=new ListNode(5);
//链表反转- 明天用迭代法和递归法
        ListNode listNode = reverseNode(node);


        System.out.println("done");
    }
    public static ListNode  reverseNode(ListNode node){
        ListNode cur = node;
        ListNode newNode = new ListNode(cur.val);
        while( cur.next != null){
            ListNode listNode = new ListNode(cur.next.val);
            listNode.next = newNode;
            newNode = listNode;
            cur = cur.next;
        }
        return newNode;
    }
}


/*for getIntersectionNode
class ListNode {
    int val;
    ListNode next;
    ListNode(int x) {
        val = x;
        next = null;
    }
}*/
