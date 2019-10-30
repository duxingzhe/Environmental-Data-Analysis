#ifndef TopLogicalSort_h
#define TopLogicalSort_h
#include "Graph.h"
 
template <class T, class E>
void TopLogicalSort(Graphlnk<T, E> &G) {
    int i, w, v;
    int n; // 顶点数
    int *count = new int[DefaultVertices]; // 入度数组
    int top = -1;
    
    // 清零
    for(i = 0; i< DefaultVertices; i++)
        count[i] = 0;
    // 输入顶点和边
    G.inputGraph(count);
    n = G.numberOfVertices(); // 获取图的顶点数
    for(i = 0; i < n; i++) { // 检查网络所有顶点
        if(count[i] == 0) { // 入度为0的顶点进栈
            count[i] = top;
            top = i;
        }
    }
    // 进行拓扑排序，输出n个顶点
    for(i = 0; i < n; i++) {
        if(top == -1) { // 空栈
            cout << "网络中有回路！" << endl;
            return;
        } else {
            v = top;
            top = count[top];
            cout << G.getValue(v) << " "; // 输出入度为0的顶点
            w = G.getFirstNeighbor(v); // 邻接顶点
            while(w != -1) { // 扫描出边表
                if(--count[w] == 0) { // 邻接顶点入度减1，如果入度为0则进栈
                    count[w] = top;
                    top = w;
                }
                w = G.getNextNeighbor(v, w); // 兄弟结点（取顶点v的邻接顶点w的下一邻接顶点）
            }
        }
    }
    cout << endl;
}
 
#endif /* TopLogicalSort_h */
