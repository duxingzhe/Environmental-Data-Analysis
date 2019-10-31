package com.luxuan.aoe;

public class Stack {

	private int[] st;  
    private int top;  
    private int count;  
  
    /** 
     * 构造一个栈 
     *  
     * @param size 
     *            栈的大小 
     */  
    public Stack(int size) {  
        st = new int[size];  
        top = -1;  
        count = 0;  
    }  
  
    /** 
     * 元素进栈 
     *  
     * @param j 
     *            要进栈的元素 
     */  
  
    public void push(int j) {  
        count++;  
        st[++top] = j;  
    }  
  
    /** 
     * 元素出栈 
     *  
     * @return 出栈的元素 
     */  
  
    public int pop() {  
  
        return st[top--];  
    }  
  
    /** 
     * 查询栈顶元素 
     *  
     * @return 栈顶元素 
     */  
  
    public int peek() {  
        return st[top];  
    }  
  
    /** 
     * 查询栈是否为空 
     *  
     * @return 栈是否为空 
     */  
  
    public boolean isEmpty() {  
        count--;  
        return (top == -1);  
    }  
  
    /** 
     * 查看栈里的所有元素 
     */  
  
    public void list() {  
  
        for (int i = 0; i < count; i++) {  
  
            System.out.print(st[i] + "   ");  
  
        }  
        System.out.println();  
    }  
  
    /** 
     * 得到栈里一共有多少元素 
     *  
     * @return 栈里的元素个数 
     */  
    public int getCount() {  
        return count;  
    }  
  
    /** 
     * 查看栈里是否包含某个元素 
     *  
     * @param i 
     *            要查询的元素 
     * @return 是否包含了要查询的元素 
     */  
  
    public boolean isContains(int i) {  
        for (int k = 0; k < st.length; k++) {  
  
            System.out.print("开始比较" + i + "此时的result:");  
            list();  
            System.out.println();  
            if (st[k] == i) {  
                return true;  
            }  
        }  
        return false;  
    }  
      
    /** 
     * 得到栈里的元素集 
     * @return 栈里的元素集合 
     */  
    public int[] getSt(){  
        return st;  
    }  
      
    /** 
     * 返回指定位置的栈元素 
     * @param i 要取得的栈元素所在的位置 
     * @return 指定位置的栈元素 
     */  
    public int getElement(int i){  
        return st[i];  
    }
}
