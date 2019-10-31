package com.luxuan.aoe;

public class Node {

	private Object data;  
	  
    private Node next;  
      
    private int weight;  
      
    /** 
     * 构造一个节点 
     * @param data  节点的数据，用来标识这个节点 
     * @param next  下一个节点 
     * @param weight    节点的权值，用来表示任务完成所要花费的时间 
     */  
    Node(Object data, Node next,int weight) {  
        this.data = data;  
        this.next = next;  
        this.weight = weight;  
    }  
      
    /** 
     *  
     * @return 节点标识 
     */  
  
    public Object getData() {  
        return data;  
    }  
      
    /** 
     *  
     * @param data 节点标识 
     */  
  
    public void setData(Object data) {  
        this.data = data;  
    }  
      
    /** 
     *  
     * @return 下一个结点 
     */  
  
    public Node getNext() {  
        return next;  
    }  
      
    /** 
     *  
     * @param next 下一个结点 
     */  
  
    public void setNext(Node next) {  
        this.next = next;  
    }  
      
    /** 
     *  
     * @return  完成任务所要花费的时间 
     */  
  
    public int getWeight() {  
        return weight;  
    }  
      
    /** 
     *  
     * @param weight 完成任务所要花费的时间 
     */  
  
    public void setWeight(int weight) {  
        this.weight = weight;  
    }
}
