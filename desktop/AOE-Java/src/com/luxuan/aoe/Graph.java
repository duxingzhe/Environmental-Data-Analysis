package com.luxuan.aoe;

public class Graph {

	private Vertex[] vertexs;// �ڵ����飬����������  
    private Link[] adjTab;// ��ȡͼ����  
    private int pos = -1;  
    private Stack stack;// ջ  
    private Stack restack;// ջ�ı���  
    private Stack backstack;// ջ�ı���  
    private int vertexNum;// �����  
    private Node start;  
    private int edgeCount;// ��¼�ߵĸ���  
    private int kNum;// ��¼�ؼ��������  
  
    private int[] ve;// �¼������緢��ʱ��  
    private int[] vl;// �¼�����ٷ���ʱ��  
    private int[] e;// ������緢��ʱ��  
    private int[] l;// ���������ʱ��  
    private int[] key;// �����洢�ؼ�·��  
  
    /** 
     * ���캯����������ʼ��һ��ͼ 
     *  
     * @param size 
     *            ͼ�Ľ������ 
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
     * ���ӽ�� 
     *  
     * @param obj 
     *            �������� 
     */  
    void add(Object obj) {  
        /** 
         * ����ӵĽ���������С��������Ľ��ռ� 
         */  
        assert pos < vertexs.length;  
        vertexs[++pos] = new Vertex(obj);  
    }  
  
    /** 
     * ��������֮���������ϵ����������ʱ�� 
     *  
     * @param from 
     *            ���������¼� 
     * @param to 
     *            �������¼� 
     * @param weight 
     *            �ӱ��������¼������������¼�֮����Ҫ���ѵ�ʱ�� 
     */  
    void addEdge(int from, int to, int weight) {  
  
        adjTab[from].addtail(to, weight);  
        vertexs[to].in++;  
        edgeCount++;  
    }  
  
    /** 
     * �������������㷨�ж�ͼ���л��Ĵ��� 
     *  
     * @return �Ƿ��л��Ĵ��� 
     */  
  
    boolean topo() {  
  
        int count = 0;// ������¼��������ʱ��û����ȵĽ�����Ƿ�С�ڽڵ���  
  
        /** 
         * ɨ�趥��������Ϊ0�Ķ���ѹջ 
         */  
  
        for (int i = 0; i < vertexNum; i++) {  
            if (vertexs[i].in == 0) {  
                stack.push(i);  
                start = adjTab[i].head();  
            }  
        }  
  
        /** 
         * ���ջ��Ϊ�յĻ��������ѭ�� �˳�ջ��Ԫ�أ�������ۼ�����1��������ĸ����ڽӵ����ȼ�1�����µ����Ϊ0�Ķ�����ջ 
         *  
         */  
  
        while (!stack.isEmpty()) {  
            /** 
             * ����restack��ջ������˳����б��� 
             */  
            restack.push(stack.peek());  
            /** 
             * ȡ��ջ��Ԫ�أ��ۼ�����1 
             */  
            int j = stack.pop();  
            count++;  
            Node p = adjTab[j].head();  
            Node earlyest = p;  
  
            /** 
             * ��¼��ǰ���¼����緢��ʱ�� 
             */  
            int preweight = ve[j];  
  
            /** 
             * ����˽ڵ��йصĵ����ȼ�1������ȼ�Ϊ0������ջ 
             */  
            while (p != null) {  
                int k = ((Integer) p.getData()).intValue();  
                vertexs[k].in--;  
                if (vertexs[k].in == 0)  
                    stack.push(k);  
  
                /** 
                 * ���¼������緢��ʱ��ve[k] 
                 */  
  
                p = p.getNext();  
                if (p != null) {  
  
                    int temp = ((Integer) p.getData()).intValue();  
  
                    /** 
                     * �ж��µõ����¼����緢��ʱ���Ƿ��ԭ�ȵ��¼����緢��ʱ�䳤 ��ʽ��ve[1] = 0; ve[k] = 
                     * max{ve[j]+len<vj,vk>} 
                     */  
                    if (p.getWeight() + preweight > ve[temp]) {  
                        ve[temp] = p.getWeight() + preweight;  
                    }  
  
                }  
            }  
  
        }  
  
        /** 
         * ����õ��Ľڵ�������ԭ�ȵĽ���٣���֤���л�·���� 
         */  
  
        if (count < vertexNum) {  
  
            System.out.println("�л�·���޷��õ��ؼ�·����");  
            return false;  
  
        }  
        return true;  
  
    }  
  
    /** 
     * ������¼����翪ʼʱ�䣬�¼�����ʼʱ�䣬����翪ʼʱ�䣬�����ʼʱ�� 
     */  
  
    public void calculate() {  
  
        int s = 0;// ����e��������緢��ʱ�䣩������  
        int t = 0;// ����l(�����ٷ���ʱ��)������  
  
        /** 
         * ��ʼ��������翪ʼʱ������ٿ�ʼʱ�� 
         */  
        e = new int[edgeCount];  
        l = new int[edgeCount];  
        key = new int[edgeCount];  
  
        /** 
         * ����������������������������ٷ���ʱ�� 
         * ԭ��restack�ﱣ�������������˳�����У�����restack�ｫ���һ���ڵ�ȡ������û���¼����������Ľ�� 
         * ȡ�������Ž�backstack��, 
         * �ٴ�restack��һ��һ��ȡ���ڵ�Ԫ�أ������Ƿ���ѷ���backstack�е�Ԫ�����������������������vl�ļ��㣬����ظ� 
         * ���㹫ʽ��vl[k] = min{vl[j]-len<vk,vj>} 
         */  
        backstack.push(restack.peek());  
        int z = restack.pop();  
        vl[z] = ve[z];  
  
        /** 
         * ����¼ԭ����˳���restack��Ϊ��ʱ�������ѭ�� 
         */  
        while (!restack.isEmpty()) {  
  
            /** 
             * ���Ѿ��Ƚ���Ľ��Ž�backstack�У�Ȼ��restack��ȡ������ֵ��backstack���ֵһһ�Ա� 
             */  
            backstack.push(restack.peek());  
            int q = restack.pop();  
  
            Node vertex = adjTab[q].head();  
  
            for (int k = 0; k < backstack.getCount(); k++) {  
                Node ver = vertex;  
                while (ver.getNext() != null) {  
  
                    /** 
                     *��ѯ����ڵ����һ������Ƿ���backstack��Ľ�㣬�Ӷ�֤�������Ƿ����� 
                     */  
                    if (((Integer) ver.getNext().getData()).intValue() == backstack  
                            .getElement(k)) {  
                        int yuanxian = vl[((Integer) vertex.getData())  
                                .intValue()];  
                        int jiangyao = vl[backstack.getElement(k)]  
                                - ver.getNext().getWeight();  
                        /** 
                         *������֮�������Ļ������½���vlֵ ����vl�Ĺ�ʽ�ǣ���vl[n] = ve[n] vl[k] = 
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
         * �������翪ʼʱ��e[i]��������ʼʱ��l[i] 
         * ���ai���ɻ�<Vk,Vj>��ʾ������AOE�������ʣ�ֻ���¼�vk�����ˣ��ai���ܿ�ʼ 
         * ��Ҳ����˵���ai�����翪ʼʱ��Ӧ�����¼�vk�����緢��ʱ�䡣 �����e[i] = ve[k]; 
         * �ai������ʼʱ����ָ���ڲ��Ƴ��������ڵ�ǰ���� 
         * ��ai���뿪ʼ������ʼʱ�䡣��ai�������<vi,vj>��ʾ����ai������ʼʱ��Ҫ��֤�¼�vj����ٷ���ʱ�䲻�Ϻ� 
         * ��ˣ�Ӧ����l[i] = vl[j] - len<vk,vj> 
         */  
  
        for (int h = 0; h < vertexNum; h++) {  
  
            Node begin = adjTab[h].head();  
            Node backbegin = begin;  
            if (begin != null) {  
                /** 
                 * �鿴���е�ǰ�ڵ����һ����� 
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
     * @return �¼������翪ʼʱ�� 
     */  
    public int[] getVE() {  
        return ve;  
    }  
  
    /** 
     *  
     * @return �¼�����ٿ�ʼʱ�� 
     */  
    public int[] getVl() {  
        return vl;  
    }  
  
    /** 
     * @return ������翪ʼʱ�� 
     */  
    public int[] getE() {  
        return e;  
    }  
  
    /** 
     *  
     * @return �������ʼʱ�� 
     *  
     */  
    public int[] getL() {  
        return l;  
    }  
  
    /** 
     *  
     * @return �ؼ���ĵ� 
     */  
    public int[] getKey() {  
        return key;  
    }  
  
    /** 
     *  
     * @return �ؼ���ĸ��� 
     */  
    public int getKNum() {  
        return kNum;  
    }
}
