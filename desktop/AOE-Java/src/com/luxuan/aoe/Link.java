package com.luxuan.aoe;

public class Link {

	private Node head;  
    private int length;  
      
    /** 
     * ���캯������ʼ������ 
     * @param index 
     */  
  
    public Link(int index) {  
        head = new Node(index, null, 0);  
        length = 0;  
    }  
      
    /** 
     * ���ӽڵ㣬ÿ�ζ����������ͷ�� 
     * @param item  �ڵ��ʶ 
     * @param weight    �����������Ҫ��ʱ�� 
     */  
  
    public void addhead(Object item, int weight) {  
        Node node = new Node(item, null, weight);  
        node.setNext(head.getNext());  
        head.setNext(node);  
        length++;  
    }    
      
    /** 
     * ���ӽڵ㣬ÿ�ζ����������β�� 
     * @param item  �ڵ��ʶ 
     * @param weight    ������������ʱ�� 
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
     * @return �����ͷԪ�� 
     */  
  
    public Node head() {  
        return head;  
    }  
      
    /** 
     * �ҵ�ָ��λ�õ�����Ԫ�� 
     * @param index ָ����λ�� 
     */  
  
    public void find(int index) {  
        if (index < 1 || index > length) {  
            System.out.print(" ��λ�ÿգ�");  
        }  
        Node temp = head;  
        for (int i = 0; i < index; i++) {  
            temp = temp.getNext();  
        }  
        System.out.println("�����е�" + index + "��λ�õ�ֵΪ" + temp.getData());  
    }  
      
    /** 
     * ɾ��ָ��λ�õ�����Ԫ�� 
     * @param index ָ����λ�� 
     */  
  
    public void delindex(int index) {  
        if (index < 1 || index > length) {  
            System.out.print("λ�ò����ڣ�");  
        }  
        Node temp = head;  
        for (int i = 0; i < index - 1; i++) {  
            temp = temp.getNext();  
        }  
        temp.setNext(temp.getNext().getNext());  
        length--;  
  
    }  
      
    /** 
     * ��ӡ�����Ԫ�� 
     */  
  
    public void print() {  
        Node temp = head;  
        while (null != temp.getNext()) {  
            System.out.println(temp.getNext().getData());  
            temp = temp.getNext();  
        }  
        System.out.println("������Ϊ��" + length);  
    } 
}
