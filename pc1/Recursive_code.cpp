#include <iostream>
#include <cstring>
#include <omp.h>

using namespace std;

/* N is the maximum string length */
#define N 600

/* Cost function f(m, n). During
 * testing, I will define my own
 * cost function which will NOT be
 * the same as this function!
 */

int f(int m, int n) {
  return ((m+1)*(n+1) - 1) % N;
}

/* Helper function to compute the
   minimum of three ints: x, y, z */
int min(int x, int y, int z) {
  if(x < y) {
    return x < z ? x : z;
  }
  return y < z ? y : z;
}

/* The global variable M[][] is a
 * 2-D table to compute the edit-
 * distance between two strings x
 * and y. Specifically, M[i][j] is
 * the edit distance between x[i..]
 * and y[j..]. The table entries
 * are initially undefined (-1).
 *
 * The notation x[i..] denotes the
 * sub-string of x starting from i.
 */
int M[N+1][N+1];
#define UNDEFINED -1

/* Recursive (memoized) edit distance
 * between x[m..] and y[n..].
 */
int recED(char *x, char *y, int m, int n) {
  if(M[m][n] != UNDEFINED) {
    return M[m][n];  /* pre-computed answer */
  }
  if(x[m] == '\0') {
    /* x[m..] is the empty string, so the
     * edit distance = the length of y[n..]
     */
    M[m][n] = strlen(y) - n;
  }
  else if(y[n] == '\0') {
    /* Similarly if y[n..] is empty */
    M[m][n] = strlen(x) - m;
  } else {
    /* There are three options:
     * 1) delete the first letter of x and
     *    edit x[m+1..] to y[n..]
     * 2) insert the first letter of y and
     *    edit x[m..] to y[n+1..]
     * 3) modify x[m] to y[n] and edit
     *    x[m+1..] to y[n+1..]
     * Delete/insert/modify cost f(m,n)
     * units each (if x[m] == y[n], the
     * cost is 0); and we want least cost.
     */
    int cost = f(m, n);
    M[m][n] = min(cost + recED(x, y, m+1, n),
                  cost + recED(x, y, m, n+1),
    (x[m] != y[n])*cost + recED(x, y, m+1, n+1));
  }
  /* return saved answer */
  return M[m][n];
}

int editDistance(char *x, char *y) {
  /* Initialize 2-D table of answers */
  for(int i = 0; i <= strlen(x); i++) {
    for(int j = 0; j <= strlen(y); j++) {
      M[i][j] = UNDEFINED;
    }
  }
  /* Return the edit distance between
   * x[0..] == x and y[0..] == y.
   */
  return recED(x, y, 0, 0);
}

int main(int argc, char **argv) {
  if(argc <= 2) {
    cout << "Usage: " << argv[1] << " str1 str2 ..." << endl;
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
