package com.hobot.fs;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * 深度优先遍历算法
 * 通过栈来实现遍历
 */
public class Dfs {

    public static void main(String[] args) {
        //构建一颗二叉树
        TreeNode root = new TreeNode(1);

        TreeNode head=new TreeNode(1);
        TreeNode second=new TreeNode(2);
        TreeNode three=new TreeNode(3);
        TreeNode four=new TreeNode(4);
        TreeNode five=new TreeNode(5);
        TreeNode six=new TreeNode(6);
        TreeNode seven=new TreeNode(7);
        head.rightNode=three;
        head.leftNode=second;
        second.rightNode=five;
        second.leftNode=four;
        three.rightNode=seven;
        three.leftNode=six;
        broadFirstSearch(head);
        deepFirstSearch(head);

    }

    /**
     * 广度优先遍历，引入一个队列进行存放
     * @param nodeHead
     */
    public static void broadFirstSearch(TreeNode nodeHead) {

        if(nodeHead == null) {
            return;
        }
        Queue<TreeNode> myQuene = new LinkedList<>();
        ((LinkedList<TreeNode>) myQuene).add(nodeHead);

        while(!myQuene.isEmpty()) {
            TreeNode node = myQuene.poll();
            System.out.print(node.data+" ");
            if(node.leftNode != null) {
                myQuene.add(node.leftNode);
            }
            if(node.rightNode != null) {
                myQuene.add(node.rightNode);
            }

        }

    }
    /**
     * 深度优先遍历，引入一个堆栈进行存放
     */
    public static void deepFirstSearch( TreeNode head) {
        if(head == null ) {
            return;
        }
        Stack<TreeNode> stack = new Stack<>();
        stack.push(head);
        while(!stack.isEmpty()) {
            TreeNode node = stack.pop();
            System.out.print(node.data+" ");
            if(null != node.rightNode) {
                stack.push(node.rightNode);
            }
            if(null != node.leftNode) {
                stack.push(node.leftNode);
            }


        }
    }


}

class TreeNode {
    int data;
    TreeNode leftNode;
    TreeNode rightNode;
    public  TreeNode() {

    }
    public TreeNode(int data) {
        this.data = data;
    }

    public TreeNode(TreeNode leftNode, TreeNode rightNode, int data) {
        this.leftNode = leftNode;
        this.rightNode = rightNode;
        this.data = data;
    }
}
