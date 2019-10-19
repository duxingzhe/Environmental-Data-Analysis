#include <stdio.h>
#include "rbtree.h"

int main()
{
	Tree* T;
	Node* z[MAXSIZE];
	int arr[MAXSIZE]={12,1,9,2,0,11,7,19,4,15, 18, 5, 14, 13, 10, 16, 6, 3, 8, 17};
 
	T=new Tree();
	T->nil=new Node(0);
	T->nil->color=BLACK;
	T->root=T->nil;
	for(int i=0;i<MAXSIZE;i++){
		z[i]=new Node(arr[i]);
		RB_INSERT(T,z[i]);
	}
 
	PreOrder(T->root,T);
	cout << endl;
	InOrder(T->root,T);
	
	return 0;
}