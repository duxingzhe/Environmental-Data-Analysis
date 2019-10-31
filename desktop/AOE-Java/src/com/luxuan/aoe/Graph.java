package com.luxuan.aoe;

public class Graph {

	private Vertex[] vertexs;// 节点数组，用来保存结点  
    private Link[] adjTab;// 存取图链表  
    private int pos = -1;  
    private Stack stack;// 栈  
    private Stack restack;// 栈的备份  
    private Stack backstack;// 栈的备份  
    private int vertexNum;// 结点数  
    private Node start;  
    private int edgeCount;// 记录边的个数  
    private int kNum;// 记录关键活动的数量  
  
    private int[] ve;// 事件的最早发生时间  
    private int[] vl;// 事件的最迟发生时间  
    private int[] e;// 活动的最早发生时间  
    private int[] l;// 活动的最晚发生时间  
    private int[] key;// 用来存储关键路径  
  
    /** 
     * 构造函数，用来初始化一个图 
     *  
     * @param size 
     *            图的结点数量 
     */  
  
    public Graph(int size) {  
        vertexNum = size;  
        vertexs = new Vertex[size];  
        adjTab = new Link[size];  
        stack = new Stack(size);  
        restack = new Stack(size);  
        backstack = new Stack(size);  
        for (int i = 0; i < size; i++) {  
            adjTab[i] = new Link(i);  
        }  
        ve = new int[size];  
        vl = new int[size];  
        for (int d = 0; d < size; d++) {  
            vl[d] = -1;  
        }  
        edgeCount = 0;  
    }  
  
    /** 
     * 增加结点 
     *  
     * @param obj 
     *            结点的名称 
     */  
    void add(Object obj) {  
        /** 
         * 所添加的结点个数必须小于所申请的结点空间 
         */  
        assert pos < vertexs.length;  
        vertexs[++pos] = new Vertex(obj);  
    }  
  
    /** 
     * 增加任务之间的依赖关系和任务运行时间 
     *  
     * @param from 
     *            被依赖的事件 
     * @param to 
     *            依赖的事件 
     * @param weight 
     *            从被依赖的事件到到依赖的事件之间所要花费的时间 
     */  
    void addEdge(int from, int to, int weight) {  
  
        adjTab[from].addtail(to, weight);  
        vertexs[to].in++;  
        edgeCount++;  
    }  
  
    /** 
     * 根据拓扑排序算法判断图是有环的存在 
     *  
     * @return 是否有环的存在 
     */  
  
    boolean topo() {  
  
        int count = 0;// 用来记录拓扑排序时，没有入度的结点数是否小于节点数  
  
        /** 
         * 扫描顶点表，将入度为0的顶点压栈 
         */  
  
        for (int i = 0; i < vertexNum; i++) {  
            if (vertexs[i].in == 0) {  
                stack.push(i);  
                start = adjTab[i].head();  
            }  
        }  
  
        /** 
         * 如果栈不为空的话，则进行循环 退出栈顶元素，输出，累加器加1，将顶点的各个邻接点的入度减1，将新的入度为0的顶点入栈 
         *  
         */  
  
        while (!stack.isEmpty()) {  
            /** 
             * 利用restack将栈的拓扑顺序进行备份 
             */  
            restack.push(stack.peek());  
            /** 
             * 取出栈顶元素，累加器加1 
             */  
            int j = stack.pop();  
            count++;  
            Node p = adjTab[j].head();  
            Node earlyest = p;  
  
            /** 
             * 记录当前的事件最早发生时间 
             */  
            int preweight = ve[j];  
  
            /** 
             * 将与此节点有关的点的入度减1，若入度减为0，则入栈 
             */  
            while (p != null) {  
                int k = ((Integer) p.getData()).intValue();  
                vertexs[k].in--;  
                if (vertexs[k].in == 0)  
                    stack.push(k);  
  
                /** 
                 * 求事件的最早发生时间ve[k] 
                 */  
  
                p = p.getNext();  
                if (p != null) {  
  
                    int temp = ((Integer) p.getData()).intValue();  
  
                    /** 
                     * 判断新得到的事件最早发生时间是否比原先的事件最早发生时间长 公式：ve[1] = 0; ve[k] = 
                     * max{ve[j]+len<vj,vk>} 
                     */  
                    if (p.getWeight() + preweight > ve[temp]) {  
                        ve[temp] = p.getWeight() + preweight;  
                    }  
  
                }  
            }  
  
        }  
  
        /** 
         * 如果得到的节点数量比原先的结点少，则证明有回路存在 
         */  
  
        if (count < vertexNum) {  
  
            System.out.println("有回路，无法得到关键路径！");  
            return false;  
  
        }  
        return true;  
  
    }  
  
    /** 
     * 计算出事件最早开始时间，事件最晚开始时间，活动最早开始时间，活动最晚开始时间 
     */  
  
    public void calculate() {  
  
        int s = 0;// 控制e（活动的最早发生时间）的增长  
        int t = 0;// 控制l(活动的最迟发生时间)的增长  
  
        /** 
         * 初始化活动的最早开始时间与最迟开始时间 
         */  
        e = new int[edgeCount];  
        l = new int[edgeCount];  
        key = new int[edgeCount];  
  
        /** 
         * 按逆拓扑有序来求其余各顶点的最迟发生时间 
         * 原理：restack里保存着拓扑排序的顺序数列，将从restack里将最后一个节点取出，即没有事件依赖于它的结点 
         * 取出的数放进backstack中, 
         * 再从restack里一个一个取出节点元素，看其是否跟已放入backstack中的元素相连，若有相连，则进行vl的计算，如此重复 
         * 计算公式：vl[k] = min{vl[j]-len<vk,vj>} 
         */  
        backstack.push(restack.peek());  
        int z = restack.pop();  
        vl[z] = ve[z];  
  
        /** 
         * 当记录原拓扑顺序的restack不为空时，则进行循环 
         */  
        while (!restack.isEmpty()) {  
  
            /** 
             * 将已经比较完的结点放进backstack中，然后将restack里取出来的值与backstack里的值一一对比 
             */  
            backstack.push(restack.peek());  
            int q = restack.pop();  
  
            Node vertex = adjTab[q].head();  
  
            for (int k = 0; k < backstack.getCount(); k++) {  
                Node ver = vertex;  
                while (ver.getNext() != null) {  
  
                    /** 
                     *查询这个节点的下一个结点是否有backstack里的结点，从而证明两者是否相连 
                     */  
                    if (((Integer) ver.getNext().getData()).intValue() == backstack  
                            .getElement(k)) {  
                        int yuanxian = vl[((Integer) vertex.getData())  
                                .intValue()];  
                        int jiangyao = vl[backstack.getElement(k)]  
                                - ver.getNext().getWeight();  
                        /** 
                         *若两点之间相连的话，更新结点的vl值 计算vl的公式是：发vl[n] = ve[n] vl[k] = 
                         * min{vl[j]-len<vk,vj>} 
                         */  
                        if (jiangyao < yuanxian || yuanxian == -1) {  
                            vl[((Integer) vertex.getData()).intValue()] = vl[backstack  
                                    .getElement(k)]  
                                    - ver.getNext().getWeight();  
                        }  
  
                    }  
                    ver = ver.getNext();  
                }  
  
            }  
  
        }  
  
        /** 
         * 求活动的最早开始时间e[i]与活动的最晚开始时间l[i] 
         * 若活动ai是由弧<Vk,Vj>表示，根据AOE网的性质，只有事件vk发生了，活动ai才能开始 
         * 。也就是说，活动ai的最早开始时间应等于事件vk的最早发生时间。 因此有e[i] = ve[k]; 
         * 活动ai的最晚开始时间是指，在不推迟整个工期的前提下 
         * ，ai必须开始的最晚开始时间。若ai由有向边<vi,vj>表示，则ai的最晚开始时间要保证事件vj的最迟发生时间不拖后 
         * 因此，应该有l[i] = vl[j] - len<vk,vj> 
         */  
  
        for (int h = 0; h < vertexNum; h++) {  
  
            Node begin = adjTab[h].head();  
            Node backbegin = begin;  
            if (begin != null) {  
                /** 
                 * 查看所有当前节点的下一个结点 
                 */  
                while (begin.getNext() != null) {  
                    e[s++] = ve[((Integer) backbegin.getData()).intValue()];  
                    l[t++] = vl[((Integer) begin.getNext().getData())  
                            .intValue()]  
                            - begin.getNext().getWeight();  
                    begin = begin.getNext();  
                }  
  
            }  
  
        }  
  
        kNum = 0;  
        for (int w = 0; w < e.length; w++) {  
  
            if (l[w] - e[w] <= 0) {  
                key[kNum++] = w;  
            }  
  
        }  
    }  
  
    /** 
     *  
     * @return 事件的最早开始时间 
     */  
    public int[] getVE() {  
        return ve;  
    }  
  
    /** 
     *  
     * @return 事件的最迟开始时间 
     */  
    public int[] getVl() {  
        return vl;  
    }  
  
    /** 
     * @return 活动的最早开始时间 
     */  
    public int[] getE() {  
        return e;  
    }  
  
    /** 
     *  
     * @return 活动的最晚开始时间 
     *  
     */  
    public int[] getL() {  
        return l;  
    }  
  
    /** 
     *  
     * @return 关键活动的点 
     */  
    public int[] getKey() {  
        return key;  
    }  
  
    /** 
     *  
     * @return 关键活动的个数 
     */  
    public int getKNum() {  
        return kNum;  
    }
}
