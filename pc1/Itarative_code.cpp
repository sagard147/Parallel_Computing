#include <iostream>
#include <cstring>
#include <omp.h>

using namespace std;

/* N is the maximum string length */

#define N 600

/* Cost function f(m, n). During
 * testing, we defined our own
 * cost function which will NOT be
 * the same as this function!
 */

int f(int m, int n) {
  return ((m+1)*(n+1)- 1) % N;
}


/* The global variable M[][] is a
 * 2-D table to compute the edit-distance between two strings x and y. 
 * Specifically, M[i][j] is the edit distance between x[i..] and y[j..].
 *  The notation x[i..] denotes the
 * sub-string of x starting from i.
 */
int M[N+1][N+1];
 
/* Helper function to compute the
   minimum of three ints: x, y, z */
 int min(int x, int y, int z) {
  if(x < y) {
    return x < z ? x : z;
  }
  return y < z ? y : z;
}



#define UNDEFINED -1


int editDistance(char *x, char *y) {
int i,j,cost;
//getting the length of 1st string and 2nd string 
int X=strlen(x);
int Y=strlen(y);

 	
 	for(i = 0; i <= Y-1; i++) 
	{	
	// First string is empty
        // where we should isnert all characters of second string
	// so Min. operations = Y-1
	   M[X][i]=(Y)-(i); 
	}
       
	for(i = 0; i <= X; i++) 
	{
	// Second string is empty
        // where we should add all characters of first
	// Min. operations = X-i
	    M[i][Y]=(X)-(i); 
	}
        
		/*
		The above statement means that to reach cell (i,j) wit minimum cost,
		first reach either cell(i-1,j) or cell (i,j-1) in as minimum cost as possible.
		From there, jump to cell (i,j). 
		This brings us to the two important conditions which need to be satisfied for a dynamic programming problem:
			Optimal Sub-structure:- Optimal solution to a problem involves optimal solutions to sub-problems.
			Overlapping Sub-problems:- Subproblems once computed can be stored in a table for further use. 
										This saves the time needed to compute the same sub-problems again and again.
		*/
		for(i = X-1; i>=0; i--) {
    		for(j = Y-1; j>=0; j--) {
     		cost = f(i,j);
       		M[i][j]=min(cost + M[i+1][j],cost + M[i][j+1],(x[i] != y[j])*cost +M[i+1][j+1]);
 		   }
 		 }
/* Return the edit cost distance between
   * x[0..] == x and y[0..] == y.
*/
return M[0][0];
}

//main function call, where we have to pass the strings as the arguments to find the edit distance   
int main(int argc, char **argv) {
  if(argc <= 2) {
    cout << "Usage: " << argv[0] << " str1 str2 ..." << endl;
    return 1;
  }
  
  // checking the length of each strings to allocated length    
  for(int i = 1; i < argc; i++) {
    if(strlen(argv[i]) > N) {
      cout << "Maximum string length: " << N << endl;
      return 1;
    }
  }
  
  int total = 0;
  // for each pair of Strings we are calling editDistance() function to calculate the minimum edit cost.
  for(int i = 1; i < argc-1; i++) {
    for(int j = i+1; j < argc; j++) {
      total += editDistance(argv[i], argv[j]);
    }
  }
  cout << total << endl;
  return 0;
}
