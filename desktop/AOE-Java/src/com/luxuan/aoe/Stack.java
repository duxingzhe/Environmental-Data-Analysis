package com.luxuan.aoe;

public class Stack {

	private int[] st;  
    private int top;  
    private int count;  
  
    /** 
     * ����һ��ջ 
     *  
     * @param size 
     *            ջ�Ĵ�С 
     */  
    public Stack(int size) {  
        st = new int[size];  
        top = -1;  
        count = 0;  
    }  
  
    /** 
     * Ԫ�ؽ�ջ 
     *  
     * @param j 
     *            Ҫ��ջ��Ԫ�� 
     */  
  
    public void push(int j) {  
        count++;  
        st[++top] = j;  
    }  
  
    /** 
     * Ԫ�س�ջ 
     *  
     * @return ��ջ��Ԫ�� 
     */  
  
    public int pop() {  
  
        return st[top--];  
    }  
  
    /** 
     * ��ѯջ��Ԫ�� 
     *  
     * @return ջ��Ԫ�� 
     */  
  
    public int peek() {  
        return st[top];  
    }  
  
    /** 
     * ��ѯջ�Ƿ�Ϊ�� 
     *  
     * @return ջ�Ƿ�Ϊ�� 
     */  
  
    public boolean isEmpty() {  
        count--;  
        return (top == -1);  
    }  
  
    /** 
     * �鿴ջ�������Ԫ�� 
     */  
  
    public void list() {  
  
        for (int i = 0; i < count; i++) {  
  
            System.out.print(st[i] + "   ");  
  
        }  
        System.out.println();  
    }  
  
    /** 
     * �õ�ջ��һ���ж���Ԫ�� 
     *  
     * @return ջ���Ԫ�ظ��� 
     */  
    public int getCount() {  
        return count;  
    }  
  
    /** 
     * �鿴ջ���Ƿ����ĳ��Ԫ�� 
     *  
     * @param i 
     *            Ҫ��ѯ��Ԫ�� 
     * @return �Ƿ������Ҫ��ѯ��Ԫ�� 
     */  
  
    public boolean isContains(int i) {  
        for (int k = 0; k < st.length; k++) {  
  
            System.out.print("��ʼ�Ƚ�" + i + "��ʱ��result:");  
            list();  
            System.out.println();  
            if (st[k] == i) {  
                return true;  
            }  
        }  
        return false;  
    }  
      
    /** 
     * �õ�ջ���Ԫ�ؼ� 
     * @return ջ���Ԫ�ؼ��� 
     */  
    public int[] getSt(){  
        return st;  
    }  
      
    /** 
     * ����ָ��λ�õ�ջԪ�� 
     * @param i Ҫȡ�õ�ջԪ�����ڵ�λ�� 
     * @return ָ��λ�õ�ջԪ�� 
     */  
    public int getElement(int i){  
        return st[i];  
    }
}
