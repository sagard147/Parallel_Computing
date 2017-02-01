#include <iostream>
#include <cstring>
#include <omp.h>

using namespace std;


#define N 600



int f(int m, int n) {
  return ((m+1)*(n+1)- 1) % N;
}


int min(int x, int y, int z) {
  if(x < y) {
    return x < z ? x : z;
  }
  return y < z ? y : z;
}



#define UNDEFINED -1


int editDistance(char *x, char *y) {
int i,j,cost;
int M[N+1][N+1];
int X=strlen(x);
int Y=strlen(y);

 	
 	for(i = 0; i <= Y-1; i++) 
	{	
	   M[X][i]=(Y)-(i); 
	}
       
	
	
	
	for(i = 0; i <= X; i++) 
	{
	   M[i][Y]=(X)-(i); 
	}
        



 		for(i = X-1; i>=0; i--) {
    		for(j = Y-1; j>=0; j--) {
     		cost = f(i,j);
       		M[i][j]=min(cost + M[i+1][j],cost + M[i][j+1],(x[i] != y[j])*cost +M[i+1][j+1]);
 		   }
 		 }

return M[0][0];
}

int main(int argc, char **argv) {
  if(argc <= 2) {
    cout << "Usage: " << argv[0] << " str1 str2 ..." << endl;
    return 1;
  }
  for(int i = 1; i < argc; i++) {
    if(strlen(argv[i]) > N) {
      cout << "Maximum string length: " << N << endl;
      return 1;
    }
  }
  
  int total = 0;
  for(int i = 1; i < argc-1; i++) {
    for(int j = i+1; j < argc; j++) {
      total += editDistance(argv[i], argv[j]);
    }
  }
  cout << total << endl;
  return 0;
}
