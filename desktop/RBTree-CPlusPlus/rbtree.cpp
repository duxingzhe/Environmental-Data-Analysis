#include "rbtree"

void LEFT_ROTATE(Tree* &T, Node* x){
	Node* y;
 
	y=x->right;
	x->right=y->left;
	if(y->left!=T->nil)
		y->left->parent=x;
	y->parent=x->parent;
	if(x->parent==T->nil)
		T->root=y;
	else if(x==x->parent->left)
		x->parent->left=y;
	else
		x->parent->right=y;
	y->left=x;
	x->parent=y;
}

//节点右旋
void RIGHT_ROTATE(Tree* &T, Node* y){
	Node *x;
 
	x=y->left;
	y->left=x->right;
	if(x->right!=T->nil)
		x->right->parent=y;
	x->parent=y->parent;
	if(y->parent==T->nil)
		T->root=x;
	else if(y==y->parent->left)
		y->parent->left=x;
	else
		y->parent->right=x;
	x->right=y;
	y->parent=x;
}

//插入节点
void RB_INSERT(Tree* &T,Node* z){
	Node* y=T->nil;
	Node* x=T->root;
	while(x!=T->nil){
		y=x;
		if(z->key < x->key)
			x=x->left;
		else
			x=x->right;
	}
	z->parent=y;
	if(y==T->nil)//插入第一个元素
		T->root=z;
	else if(z->key < y->key)
		y->left=z;
	else
		y->right=z;
	z->left=T->nil;
	z->right=T->nil;
	z->color=RED;
	RB_INSERT_FIXUP(T,z);
}

//红黑树调整
void RB_INSERT_FIXUP(Tree* &T, Node* z){
	Node* y;
 
	while(z->parent->color==RED){
		if(z->parent==z->parent->parent->left){     //z节点父节点为其祖父节点的左孩子
			y=z->parent->parent->right;
			if(y->color==RED){                      //case1
				z->parent->color=BLACK;
				y->color=BLACK;
				z->parent->parent->color=RED;
				z=z->parent->parent;
			}
			else{                                       
				if(z==z->parent->right){            //case2
					z=z->parent;
					LEFT_ROTATE(T,z);
				}
				z->parent->color=BLACK;             //case3
				z->parent->parent->color=RED;
				RIGHT_ROTATE(T,z->parent->parent);
			}
		}
		else{									    //z节点父节点为其祖父节点的右孩子
			y=z->parent->parent->left;
			if(y->color==RED){						//case 1
				z->parent->color=BLACK;
				y->color=BLACK;
				z->parent->parent->color=RED;
				z=z->parent->parent;
			}
			else{
				if(z==z->parent->left){            //case2
					z=z->parent;
					RIGHT_ROTATE(T,z);
				}
				z->parent->color=BLACK;             //case3
				z->parent->parent->color=RED;
				LEFT_ROTATE(T,z->parent->parent);
			}
		}
	}
	T->root->color=BLACK;
}