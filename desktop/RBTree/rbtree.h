#ifndef RBTREE_H
#define RBTREE_H

#include <iostream>
#include <algorithm>
#include <QString>

using namespace std;

enum { red = 0, black = 1 };

struct Node         //采用三叉链表表示树的节点
{
    int key;
    bool color;
    Node * parent;
    Node * left;
    Node * right;
    Node(int key = 0)
    {
        this->key = key;
        this->color = red;
        this->parent = this->left = this->right = nullptr;
    }
};

class RBTree
{
private:
    Node * header; //head->l = root
private:
    void rotate_left(Node * x);       //左旋，用于重构
    void rotate_right(Node * x);      //右旋，用于重构
    void insert_rebalance(Node * x);  //插入后重构
    void erase_rebalance(Node * z);   //删除后重构

public:
    RBTree();
    ~RBTree();
    Node * insert(int key);      //插入
    Node * find(int key);        //查找
    Node *& root();       //得到根节点
    void destroy(Node * node);   //树的销毁
    void erase(int key);         //删除
    void print(QString &);       //树形打印
    void doprint(Node *t, int level, QString &);
};

#endif // RBTREE_H
