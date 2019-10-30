#include "TopLogicalSort.h"
 
int main(int argc, const char * argv[]) {
    Graphlnk<char, int> G; // 声明图对象
    
    TopLogicalSort(G); // AOV网络的拓扑排序
    return 0;
}
