import java.util.*;
import java.io.*;

public class PageRank 
{
   public int path[][] = new int[10][10];
   public double pagerank[] = new double[10];

  

    public void calc(double n)
    {   
	    double init;
	    double c=0;
	    double temp[] = new double[10];
 	    int i,j,u=1,k=1;

 	    init = 1/n;

    for(i=1;i<=n;i++)
      this.pagerank[i]=init;
 

   while(u<=8)
   {
	for(i=1;i<=n;i++)
	{  
		temp[i]=this.pagerank[i];
		this.pagerank[i]=0;

        }

     

 	for(j=1;j<=n;j++)
	     for(i=1;i<=n;i++)
		if(this.path[i][j] == 1)
		 {  k=1;c=0;
		while(k<=n)
		{
		if(this.path[i][k] == 1 )
			c=c+1;
     			k=k+1;
		}

             this.pagerank[j]=this.pagerank[j]+temp[i]*(1/c);   

      }

  if(u==8)
  

   //System.out.printf("\n After "+u+"th Step \n");

     for(i=1;i<=n;i++)

      System.out.printf(" Page Rank of "+i+" is :\t"+this.pagerank[i]+"\n");
//}

   

     u=u+1;

    }

}

 public static void main(String args[])

    {

        int i,j,cost;
	int nodes=4;

        Scanner in = new Scanner(System.in);

        System.out.println("Enter the Number of WebPages \n");

        nodes = in.nextInt();

        PageRank p = new PageRank();

       System.out.println("Enter the Adjacency Matrix with 1->PATH & 0->NO PATH Between two WebPages: \n");

        for(i=1;i<=nodes;i++)

          for(j=1;j<=nodes;j++)

          {

            p.path[i][j]=in.nextInt();

            if(j==i)

              p.path[i][j]=0;

          }
	/*p.path[1][1]=0;
	p.path[1][2]=1;
	p.path[1][3]=1;
	p.path[1][4]=1;
	p.path[1][5]=0;
	p.path[2][1]=0;
	p.path[2][2]=1;
	p.path[2][3]=1;
	p.path[2][4]=1;
	p.path[2][5]=0;
	p.path[3][1]=0;
	p.path[3][2]=0;
	p.path[3][3]=1;
	p.path[3][4]=0;
	p.path[3][5]=1;
	p.path[4][1]=0;
	/*p.path[4][2]=0;
	p.path[4][3]=0;
	p.path[4][4]=0;
	p.path[4][5]=1;
	p.path[5][1]=1;
	p.path[5][2]=0;
	p.path[5][3]=0;
	p.path[5][4]=0;
	p.path[5][5]=0;*/
	 
	 

        p.calc(nodes);     

    }  

 

}
