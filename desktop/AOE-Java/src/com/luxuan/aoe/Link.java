package com.luxuan.aoe;

public class Link {

	private Node head;  
    private int length;  
      
    /** 
     * 构造函数，初始化链表 
     * @param index 
     */  
  
    public Link(int index) {  
        head = new Node(index, null, 0);  
        length = 0;  
    }  
      
    /** 
     * 增加节点，每次都加在链表的头部 
     * @param item  节点标识 
     * @param weight    完成任务所需要的时间 
     */  
  
    public void addhead(Object item, int weight) {  
        Node node = new Node(item, null, weight);  
        node.setNext(head.getNext());  
        head.setNext(node);  
        length++;  
    }    
      
    /** 
     * 增加节点，每次都加在链表的尾部 
     * @param item  节点标识 
     * @param weight    完成任务所需的时间 
     */  
  
    public void addtail(Object item, int weight) {  
        Node node = new Node(item, null, weight);  
        Node temp = head;  
        while (null != temp.getNext()) {  
            temp = temp.getNext();  
        }  
        temp.setNext(node);  
        length++;  
    }  
      
    /** 
     *  
     * @return 链表的头元素 
     */  
  
    public Node head() {  
        return head;  
    }  
      
    /** 
     * 找到指定位置的链表元素 
     * @param index 指定的位置 
     */  
  
    public void find(int index) {  
        if (index < 1 || index > length) {  
            System.out.print(" 此位置空！");  
        }  
        Node temp = head;  
        for (int i = 0; i < index; i++) {  
            temp = temp.getNext();  
        }  
        System.out.println("链表中第" + index + "个位置的值为" + temp.getData());  
    }  
      
    /** 
     * 删除指定位置的链表元素 
     * @param index 指定的位置 
     */  
  
    public void delindex(int index) {  
        if (index < 1 || index > length) {  
            System.out.print("位置不存在！");  
        }  
        Node temp = head;  
        for (int i = 0; i < index - 1; i++) {  
            temp = temp.getNext();  
        }  
        temp.setNext(temp.getNext().getNext());  
        length--;  
  
    }  
      
    /** 
     * 打印链表的元素 
     */  
  
    public void print() {  
        Node temp = head;  
        while (null != temp.getNext()) {  
            System.out.println(temp.getNext().getData());  
            temp = temp.getNext();  
        }  
        System.out.println("链表长度为：" + length);  
    } 
}
