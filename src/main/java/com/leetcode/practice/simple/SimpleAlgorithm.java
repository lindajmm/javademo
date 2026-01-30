package com.leetcode.practice.simple;


import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author: Linda
 * @date: 2026/1/14 17:02
 * @description:
 */
public class SimpleAlgorithm {

    //input nums={0,1,0,3,12}, output = {1,3,12,0,0}
    public static void moveZeroes(int[] nums) {
        if (nums == null || nums.length <= 1) {
            return;
        }
        int nonZeroIndex = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != 0) {
                if (i != nonZeroIndex) {
                    nums[nonZeroIndex] = nums[i];
                    nums[i] = 0;
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

    public static ListNode getReversedListNode(ListNode head) {
        ListNode pre = null;
        ListNode cur = head;
        while (cur != null) {
            ListNode nextTmp = cur.next;
            cur.next = pre;
            pre = cur;
            cur = nextTmp;
        }
        return pre;
    }

    public static ListNode reverseListNodeWithStack(ListNode head){
        if(head == null || head.next ==null){
            return head;
        }

        ArrayDeque<ListNode> dequeStack = new ArrayDeque<>();
//        Stack<ListNode> stack = new Stack<>();
        ListNode cur = head;
        while(cur != null){
            dequeStack.push(cur);
            cur = cur.next;;
        }
        System.out.println("done for pushing to stack");
        ListNode headNode = dequeStack.pop();
        ListNode tmp = headNode;
        while(!dequeStack.isEmpty()){
            tmp.next = dequeStack.pop();
            tmp = tmp.next;
        }
        tmp.next = null;
        return headNode;

      /*  ListNode newOne = new ListNode(0);
        head = newOne;
        while(!dequeStack.isEmpty()){

            newOne.next = dequeStack.pop();
            newOne = newOne.next;

        }
        return head.next;*/

      /*  while(!dequeStack.isEmpty()){

            head.next = dequeStack.pop();
            head = head.next;

        }
        return newOne.next;*/

    }


//    public static void main(String[] args) {
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

       /* ListNode node = new ListNode(1);
        node.next = new ListNode(1);
        node.next.next = new ListNode(1);
        node.next.next.next = new ListNode(1);
        node.next.next.next.next = new ListNode(1);*/
//        node.next.next.next.next.next = new ListNode(1);
//链表反转- 明天用迭代法和递归法
//        ListNode listNode = reverseNode(node);
//        ListNode listNode = reverseNodeWithIterator(node);
//        ListNode listNode1 = getReversedListNode(node);

//        ListNode listNode = reverseListNodeWithStack(node);
//        ListNode listNode = selfImpReversedList(node);
//        boolean palindrome = isPalindrome(node);
    public static void main(String[] args) {
        /*ListNode node = new ListNode(1);
        ListNode listNode2 = new ListNode(2);
        node.next = listNode2;
        node.next.next = new ListNode(4);
        node.next.next.next = new ListNode(5);
        node.next.next.next.next = listNode2;*/

       /* ListNode node1 = new ListNode(1);
        node1.next = new ListNode(5);
        node1.next.next = new ListNode(10);
        node1.next.next.next = new ListNode(20);
        node1.next.next.next.next = new ListNode(21);

        ListNode node2 = new ListNode(6);
        node2.next = new ListNode(8);
        node2.next.next = new ListNode(11);
        node2.next.next.next = new ListNode(20);*/

//        boolean b = hasCycle(node);
//        ListNode result = mergeTwoLists(node1, node2);
        TreeNode root = new TreeNode(10);
        root.left=new TreeNode(3);
        root.left.left = new TreeNode( 2);
        root.left.right = new TreeNode(4);

        root.right = new TreeNode(15);
        root.right.left = new TreeNode(12);
        root.right.right = new TreeNode(20);

//        List<Integer> integers0 = inorderTraversal(root);
        List<Integer> integers1 = inorderTraversalIterator(root);

        System.out.println("done");
    }

    public static boolean isPalindrome(ListNode head){
        if(head == null || head.next == null){
            return true;
        }

        ListNode fast = head;
        ListNode slow = head;
        while(fast != null && fast.next != null){
            fast = fast.next.next;
            slow = slow.next;
        }
        ListNode reversedRightHalf = selfImpReversedList(slow);
        ListNode p1 = head;
        ListNode p2 = reversedRightHalf;
        while(p2 != null){
            if(p1.val != p2.val){
                return false;
            }
            p1 = p1.next;
            p2 = p2.next;
        }
        return true;
    }

    public static ListNode selfImpReversedList(ListNode head){
        if(head == null || head.next == null){
            return head;
        }
        ListNode cur = head;
        ListNode pre = null;
        while(cur != null){
            ListNode next = cur.next;
            cur.next = pre;
            pre = cur;
            cur = next;
        }
        return pre;
    }
//是否有环
    public static boolean hasCycle(ListNode head) {
        if(head == null || head.next == null){
            return false;
        }
        ListNode slow = head;
        ListNode fast = head;
        while(fast != null && fast.next != null){
            slow = slow.next;
            fast = fast.next.next;
            if(slow == fast){
                return true;
            }
        }
        return false;
    }

    public static ListNode detectCycle(ListNode head) {
        if(head == null || head.next == null){
            return null;
        }
        ListNode slow = head;
        ListNode fast = head;
        while(fast != null && fast.next != null){
            slow = slow.next;
            fast = fast.next.next;
            if(slow == fast){
                fast = head;
//                while(slow != null){//两个问题：1.当fast=head 后，slow 和 fast 还没有开始移动，可能已经相等；2.这里可能导致死循环
                  while(fast != slow){
                    slow = slow.next;
                    fast = fast.next;
                }
                return slow;//因为有环，一定会相应，且再次相遇的点就是环的入口
            }
        }
        return null;
    }

    public static ListNode mergeTwoLists(ListNode list1, ListNode list2) {
       /* if(list1 == null && list2 == null){
            return null;
        }
        if(list1 != null && list2 == null){
            return list1;
        }
        if(list1 == null && list2 != null){
            return list2;
        }*/
        if(list1 == null) return list2;
        if(list2 == null) return list1;

        ListNode result = new ListNode(0);
        ListNode head = result;

        while(list1 != null && list2 != null){
            if(list1.val < list2.val){
               /* result.next = new ListNode(list1.val);
                result = result.next;*/
                //上面是我自己的实现，缺点如下：创建了不必要的对象，浪费空间。但是整体思想是一样的
                result.next = list1;
                list1 = list1.next;
            }else{
            /*    result.next = new ListNode(list2.val);
                result = result.next;*/
                result.next = list2;

                list2 = list2.next;
            }
            result = result.next;
        }

        result.next = (list1 == null) ? list2: list1;

        return head.next;
    }
    //递归法
    public static List<Integer> inorderTraversal(TreeNode root) {
        ArrayList<Integer> result = new ArrayList<>();
        subInorder(root, result);
        return result;
    }

    private static void subInorder(TreeNode node, List<Integer> result) {
        if (node == null) {
            return;
        }
        //遍历左子树
        subInorder(node.left, result);
        //添加根节点
        result.add(node.val);
        //遍历右子树
        subInorder(node.right, result);
    }

    //迭代法
    public static List<Integer> inorderTraversalIterator(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        Deque<TreeNode> queue = new ArrayDeque<>();
//        Stack<TreeNode> stack = new Stack<>();
        TreeNode curr = root;

        while (curr != null || !queue.isEmpty()) {
            // 一直向左走，将所有左节点入栈
            while (curr != null) {
                queue.push(curr);
                curr = curr.left;
            }

            // 弹出栈顶节点并访问
            curr = queue.pop();
            result.add(curr.val);

            // 转向右子树
            curr = curr.right;
        }

        return result;
    }

    public static ListNode reverseNodeWithIterator(ListNode head) {
        ListNode prev = null;
        ListNode current = head;
        while (current != null) {
            ListNode nextTemp = current.next;
            current.next = prev;
            prev = current;
            current = nextTemp;
        }
        return prev;
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
