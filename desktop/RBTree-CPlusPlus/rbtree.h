#ifndef RBTREE
#define RBTREE

#define RED 0
#define BLACK 1
 
struct Node{
	Node(int key){
		this->key=key;
	}
	int key;
	int color;
	Node* parent;
	Node* left;
	Node* right;
};
 
struct Tree{
	Tree(){
	}
	Node* root;
	Node* nil;
private:
    void rotate_left(Node * x);       //左旋，用于重构
    void rotate_right(Node * x);      //右旋，用于重构
    void insert_rebalance(Node * x);  //插入后重构
    void erase_rebalance(Node * z);   //删除后重构

public:
    Tree();
    ~Tree();
    Node * insert(int key);      //插入
    Node * find(int key);        //查找
    Node *& root();       //得到根节点
    void destroy(Node * node);   //树的销毁
    void erase(int key);         //删除
    void print(QString &);       //树形打印
    void doprint(Node *t, int level, QString &);
};

#endif