#include "rbtree.h"

void RBTree::rotate_left(Node * x)   //左旋，用于重构
{
    Node * y = x->right;   //以右子节点为支点旋转

    x->right = y->left;
    if (y->left)
        y->left->parent = x;
    y->parent = x->parent;

    if (x == root())
        root() = y;
    else if (x == x->parent->left)
        x->parent->left = y;
    else
        x->parent->right = y;

    y->left = x;
    x->parent = y;
}

void RBTree::rotate_right(Node * x)   //右旋，用于重构
{
    Node * y = x->left;    //以左子节点为支点旋转

    x->left = y->right;
    if (y->right)
        y->right->parent = x;
    y->parent = x->parent;

    if (x == root())
        root() = y;
    else if (x == x->parent->right)
        x->parent->right = y;
    else
        x->parent->left = y;

    y->right = x;
    x->parent = y;
}

void RBTree::destroy(Node * node)   //树的销毁
{
    if (node == nullptr)
        return;

    destroy(node->left);  //销毁左孩子
    destroy(node->right); //销毁右孩子
    delete node;  //释放节点
}

Node *& RBTree::root()
{
    return header->left;  //head->l=root
}

void RBTree::insert_rebalance(Node * x)  //插入后重构
{
    x->color = red;

    while (x != root() && x->parent->color == red)  //父亲节点是红色时重构
    {
        if (x->parent == x->parent->parent->left)
        {
            Node * y = x->parent->parent->right;

            if (y && y->color == red)           // Case 1 叔叔节点为红色
            {
                x->parent->color = black;
                y->color = black;
                x->parent->parent->color = red;
                x = x->parent->parent;
            }
            else    //叔叔节点黑色
            {
                if (x == x->parent->right)      // Case 2当前节点为其父亲节点右孩子
                {
                    x = x->parent;
                    rotate_left(x);
                }

                x->parent->color = black;       // Case 3当前节点为其父亲节点左孩子
                x->parent->parent->color = red;
                rotate_right(x->parent->parent);
            }
        }
        else  //和上面相同，镜像操作
        {
            Node * y = x->parent->parent->left;

            if (y && y->color == red)
            {
                x->parent->color = black;
                y->color = black;
                x->parent->parent->color = red;
                x = x->parent->parent;
            }
            else
            {
                if (x == x->parent->left)
                {
                    x = x->parent;
                    rotate_right(x);
                }

                x->parent->color = black;
                x->parent->parent->color = red;
                rotate_left(x->parent->parent);
            }
        }
    }

    root()->color = black;  //最后根节点重新着色为黑色
}

void RBTree::erase_rebalance(Node * z)  //删除后重构
{
    Node * y = z;
    Node * x = nullptr;
    Node * x_parent = nullptr;

    if (y->left == nullptr)
        x = y->right;
    else if (y->right == nullptr)
        x = y->left;
    else
    {
        y = y->right;   //找右子树最小节点
        while (y->left)
            y = y->left;
        x = y->right;
    }

    if (y != z)  //y是z的祖先// the third
    {
        z->left->parent = y;
        y->left = z->left;

        if (y != z->right)
        {
            x_parent = y->parent;
            if (x)
                x->parent = y->parent;
            y->parent->left = x;
            y->right = z->right;
            z->right->parent = y;
        }
        else
            x_parent = y;

        if (root() == z)
            root() = y;
        else if (z->parent->left == z)
            z->parent->left = y;
        else
            z->parent->right = y;

        y->parent = z->parent;
        swap(y->color, z->color);
        y = z;
    }
    else
    {
        x_parent = y->parent;
        if (x)
            x->parent = y->parent;

        if (root() == z)
            root() = x;
        else if (z->parent->left == z)
            z->parent->left = x;
        else
            z->parent->right = x;
    }
    //现在，y是想要删除的节点！
    //  x是y的子节点，x必须是空节点



    // 重构的实现
    // .....
    if (y->color == black)
    {
        while (x != root() && (x == nullptr || x->color == black))
        {
            if (x == x_parent->left)
            {
                Node * w = x_parent->right;  // w can not possibly be nullptr!

                if (w->color == red)                                      // Case 1当前结点是黑色，兄弟结点是红色
                {
                    w->color = black;
                    x_parent->color = red;
                    rotate_left(x_parent);
                    w = x_parent->right;
                }

                if ((w->left == nullptr || w->left->color == black) && // Case 2当前结点是黑色，兄弟结点是黑色，两个孩子为空或是黑色
                    (w->right == nullptr || w->right->color == black))
                {
                    w->color = red;
                    x = x_parent;
                    x_parent = x_parent->parent;
                }
                else
                {
                    if (w->right == nullptr || w->right->color == black)//Case 3
                    {                                //当前结点是黑色，兄弟结点是黑色，兄弟结点的左孩子是红色，右孩子为空或是黑色
                        if (w->left)
                            w->left->color = black;
                        w->color = red;
                        rotate_right(w);
                        w = x_parent->right;
                    }

                    w->color = x_parent->color;  // Case 4
                    x_parent->color = black;     //当前结点是黑色，兄弟结点是黑色，兄弟结点的右孩子是红色，左孩子为空或红黑皆可
                    if (w->right)
                        w->right->color = black;
                    rotate_left(x_parent);
                    break;
                }
            }
            else  //和上面相同，镜像操作
            {
                Node * w = x_parent->left;

                if (w->color == red)
                {
                    w->color = black;
                    x_parent->color = red;
                    rotate_right(x_parent);
                    w = x_parent->left;
                }

                if ((w->right == nullptr || w->right->color == black) &&
                    (w->left == nullptr || w->left->color == black))
                {
                    w->color = red;
                    x = x_parent;
                    x_parent = x_parent->parent;
                }
                else
                {
                    if (w->left == nullptr || w->left->color == black)
                    {
                        if (w->right)
                            w->right->color = black;
                        w->color = red;
                        rotate_left(w);
                        w = x_parent->left;
                    }

                    w->color = x_parent->color;
                    x_parent->color = black;
                    if (w->left)
                        w->left->color = black;
                    rotate_right(x_parent);
                    break;
                }
            }
        }  // while (x != root() && (x == nullptr || x->color == black))

        if (x)
            x->color = black;
    }  // if (y->color == black)
}

RBTree::RBTree()
{
    header = new Node(0);
}

RBTree::~RBTree()
{
    destroy(root());
    delete header;
    header = nullptr;
}


Node * RBTree::insert(int key)  //插入
{
    Node * cur = root();
    Node * pre = header;

    while (cur)
    {
        pre = cur;
        if (key < cur->key)
            cur = cur->left;
        else if (key > cur->key)
            cur = cur->right;
        else
            return nullptr;
    }

    cur = new Node(key);
    cur->parent = pre;

    if (pre == header || key < pre->key)
        pre->left = cur;
    else
        pre->right = cur;

    insert_rebalance(cur);  //重构

    return cur;
}

Node * RBTree::find(int key) //查找
{
    Node * z = root();

    while (z)
    {
        if (key < z->key)
            z = z->left;
        else if (key > z->key) //和二叉排序树相同
            z = z->right;
        else
            return z;
    }

    return z;
}

void RBTree::erase(int key)  //删除
{
    Node * z = find(key);

    if (z)
    {
        erase_rebalance(z); //重构
        delete z;
    }
}

void RBTree::doprint(Node *T, int level, QString &str)
{
    if (T == nullptr) return;        //如果指针为空，返回上一层

    doprint(T->left, level + 1, str);    //打印左子树，并将层次加1
    for (int i = 0; i<level; i++)    //按照递归的层次打印空格
    {
        str += "    ";//printf("   ");
    }
    char num[10];

    sprintf(num, "%d", T->key);
    str += QString(num);

    str += (T->color == red) ? "R\n\n" : "B\n\n";
    doprint(T->right, level + 1, str);   //打印右子树，并将层次加1
    str += "\n";
}

void RBTree::print(QString &str)
{

    doprint(this->root(), 1, str);

}
