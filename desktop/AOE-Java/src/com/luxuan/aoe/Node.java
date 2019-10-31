package com.luxuan.aoe;

public class Node {

	private Object data;  
	  
    private Node next;  
      
    private int weight;  
      
    /** 
     * ����һ���ڵ� 
     * @param data  �ڵ�����ݣ�������ʶ����ڵ� 
     * @param next  ��һ���ڵ� 
     * @param weight    �ڵ��Ȩֵ��������ʾ���������Ҫ���ѵ�ʱ�� 
     */  
    Node(Object data, Node next,int weight) {  
        this.data = data;  
        this.next = next;  
        this.weight = weight;  
    }  
      
    /** 
     *  
     * @return �ڵ��ʶ 
     */  
  
    public Object getData() {  
        return data;  
    }  
      
    /** 
     *  
     * @param data �ڵ��ʶ 
     */  
  
    public void setData(Object data) {  
        this.data = data;  
    }  
      
    /** 
     *  
     * @return ��һ����� 
     */  
  
    public Node getNext() {  
        return next;  
    }  
      
    /** 
     *  
     * @param next ��һ����� 
     */  
  
    public void setNext(Node next) {  
        this.next = next;  
    }  
      
    /** 
     *  
     * @return  ���������Ҫ���ѵ�ʱ�� 
     */  
  
    public int getWeight() {  
        return weight;  
    }  
      
    /** 
     *  
     * @param weight ���������Ҫ���ѵ�ʱ�� 
     */  
  
    public void setWeight(int weight) {  
        this.weight = weight;  
    }
}
