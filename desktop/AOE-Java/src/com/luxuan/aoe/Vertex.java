package com.luxuan.aoe;

public class Vertex {

	public  int in;  
    private Object value;  
    private boolean isVisited;  
    Vertex(Object value){  
        this.value = value;  
    }  
      
    Object value(){  
        return value;  
    }
}
