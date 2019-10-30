#ifndef Graph_h
#define Graph_h
 
#include <iostream>
using namespace std;
 
const int DefaultVertices = 30;
 
template <class T, class E>
struct Edge { // 边结点的定义
    int dest; // 边的另一顶点位置
    Edge<T, E> *link; // 下一条边链指针
};
 
template <class T, class E>
struct Vertex { // 顶点的定义
    T data; // 顶点的名字
    Edge<T, E> *adj; // 边链表的头指针
};
 
template <class T, class E>
class Graphlnk {
public:
    const E maxValue = 100000; // 代表无穷大的值（=∞）
    Graphlnk(int sz=DefaultVertices); // 构造函数
    ~Graphlnk(); // 析构函数
    void inputGraph(int count[]); // 建立邻接表表示的图
    void outputGraph(); // 输出图中的所有顶点和边信息
    T getValue(int i); // 取位置为i的顶点中的值
    bool insertVertex(const T& vertex); // 插入顶点
    bool insertEdge(int v1, int v2); // 插入边
    bool removeVertex(int v); // 删除顶点
    bool removeEdge(int v1, int v2); // 删除边
    int getFirstNeighbor(int v); // 取顶点v的第一个邻接顶点
    int getNextNeighbor(int v,int w); // 取顶点v的邻接顶点w的下一邻接顶点
    int getVertexPos(const T vertex); // 给出顶点vertex在图中的位置
    int numberOfVertices(); // 当前顶点数
private:
    int maxVertices; // 图中最大的顶点数
    int numEdges; // 当前边数
    int numVertices; // 当前顶点数
    Vertex<T, E> * nodeTable; // 顶点表(各边链表的头结点)
};

#endif /* Graph_h */