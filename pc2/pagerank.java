/*
    To calculate page rank using MapReduce.
    Input : Two matrices stored in two separate csv files.
            First matrix is the graph representing the links between the graph.
            Second matrix is the vector.
    
    Implementation: Two mappers class and one reducer class
                Two mappers is to parallelize the process as much as possible.
                 
         
    Output :  Vector containing the page rank.
*/


import java.io.IOException;
import java.io.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;

public class pagerank
{

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException 
	{
		int depth = 3;
		boolean IsEqual = false;
		/*
			The number of times matrix multiplication process has to happen has a threshold value. Here it is 11.
			If the pagerank is obtained before the value reaches threshold, the multiplication process stops and vetor is returned.	
		*/
		while(IsEqual==false || depth<11){
				
			// Create a Configuration object that is used to set other options
			Configuration conf = new Configuration();	
			conf.set("dimension", "4");
			
			 // Create the object representing the job
			Job job = Job.getInstance(conf);		
			
			
			FileSystem fs = FileSystem.get(conf);
			job.setJobName("page rank");
			
			// Set the name of the main class in the job jar file
			job.setJarByClass(pagerank.class);		
			
			//we are setting the type of key,value from mapper class to be text
			job.setMapOutputKeyClass(Text.class); 
			job.setMapOutputValueClass(Text.class);
			
			//we are setting the type of key from reducer class to be text and value to be double
			job.setOutputKeyClass(Text.class);	
			job.setOutputValueClass(DoubleWritable.class);	
					
			// Set the mapper classes for two input files
			job.setMapperClass(Matrix_Mapper.class);	
			job.setMapperClass(Matrix_Mapper1.class);

			// Set the reducer class	
			job.setReducerClass(Matrix_Reducer.class);	
			
			//Set the input and output format for job(worker) to text		
			job.setInputFormatClass(TextInputFormat.class);
			job.setOutputFormatClass(TextOutputFormat.class);
			
			// Set input file paths and output file paths for taking the input and storing the computed output.
			Path input1 = new Path("/usr/local/hadoop/page_rank1/page1.csv");
			Path input2 = new Path("/usr/local/hadoop/page_rank2/page"+(depth-1)+".csv");
			Path output = new Path("/usr/local/hadoop/page_rank2/page"+(depth)+".csv");
					
			if(!fs.exists(input1)) 
			{
				System.err.println("Input file1 doesn't exists");
				return;
			}
								
			if(!fs.exists(input2)) 
			{
				System.err.println("Input file2 doesn't exists");
				return;
			}
			
			if(fs.exists(output)) 
			{
				fs.delete(output, true);
				System.err.println("Output file deleted");
			}
			fs.close();
					
			// Set multiple input paths since the two matrices are stored in two different csv files.
			MultipleInputs.addInputPath(job, input1, TextInputFormat.class,Matrix_Mapper.class);
			MultipleInputs.addInputPath(job, input2, TextInputFormat.class,Matrix_Mapper1.class);
			
			// The output from job is written to the path specified.  
			FileOutputFormat.setOutputPath(job, output);
			
			// Execute the job and wait for it to complete
			job.waitForCompletion(true);	
			FileSystem fs3 =  null;
			FileSystem fs1 = null;
			BufferedReader br = null;
			BufferedReader br1 = null;
			
			try
			{
				Path pt=new Path("/usr/local/hadoop/page_rank2/page"+(depth-1)+".csv");		
				Path pt1 = new Path("/usr/local/hadoop/page_rank2/page"+(depth)+".csv");
				fs = FileSystem.get(new Configuration());
				fs1 = FileSystem.get(new Configuration());
				br=new BufferedReader(new InputStreamReader(fs.open(pt)));
				br1=new BufferedReader(new InputStreamReader(fs1.open(pt)));
				String line;
				String line1;
				line=br.readLine();
				line1=br1.readLine();

				/*
					Check the result of matrix multiplication with the multiplied vector.
					If computed vector is equal to the multiplied vector, then the pagerank is obtained.
					Else pass the vector as matrix 1 and computed vector as matrix 2 and perform multiplication process again.
					This process goes on.
				*/

				while (line != null && line1 != null)
				{
                                        String[] r = line.split(",");
                                        String[] r1 = line1.split(",");
                                        r[3] = r[3].trim();
                                        r1[3] = r1[3].trim();
                                        String l1 = r[0]+","+r[1]+","+r[2]+","+r[3];
                                        String l2 = r1[0]+","+r1[1]+","+r1[2]+","+r1[3];
                                        line.replaceAll(line,l1);
                                        line1.replaceAll(line1,l2);
					if(l1.equals(l2))
					{
						line=br.readLine();
						line1=br1.readLine();
						IsEqual=true;
					}
					else
					{
						IsEqual=false;
						break;
					}
				}
			}
			catch(IOException e)
			{ 
			}
			finally
			{
				if(br!=null)
				{
					try 
					{
						br.close();
					} 
					catch (IOException e) 
					{
								
					}
				}
				if(br1!=null)
				{
					try 
					{
						br1.close();
					} 
					catch (IOException e) 
					{
								
					}
				}	
			}
			depth++;
		}	
	}




	/*	1.Here we are using two mapper methods for computing the result.
		2.Both the mapper methods run in parallel and that is how it takes less time compared to the traditional method of matrix multiplication.

	The Matrix_Mapper class below takes key-value pairs as the input.
	INPUT :
	key : row number for input from 1st csv file	
	      column number for input from 2nd csv file
	      Eg : 1 if 2nd row of 1st csv file
	value : for(i from 0....dimension) take each line
		Eg : For first iteration, for row a,0,0,1
					  for column b,0,0,1

	The output from Matrix_Mapper method is again a key-value pair which is sent to Matrix_Reducer method.
	OUTPUT :
	key :	row number for input from 1st csv file added to the loop variable value
		column number for input from 2nd csv file added to the loop variable value
		Eg : For first iteration value of loop variable is 0.
		     key = 1+0
	value : each line from the input files.
		Eg : for row a,0,0,1
		     for column b,0,0,1
		     Taken according to the computed key value.
	
	*/
	public static class Matrix_Mapper extends Mapper<LongWritable,Text,Text,Text>
    	{
	
		@Override
		protected void map(LongWritable key, Text value,Context context)
							throws IOException, InterruptedException
		{

			String line = value.toString();
			String[] entry = line.split(",");
			String sKey = "";
			String mat = entry[0].trim();
			
			String row, col;
			
			Configuration conf = context.getConfiguration();
			String dimension = conf.get("dimension");
			
			System.out.println("Dimension from Mapper = " + dimension);
			
			int dim = Integer.parseInt(dimension);
			
			
			//The input in 2nd file is present in the format : a,0,0,1
			//So if the mapper has to check if it is receiving the lines from input file 1 or input file 2.
			//Hence the condition....if(mat.matches("a"))
			if(mat.matches("a"))
			{
				for (int i =0; i < dim ; i++) 
				{
					row = entry[1].trim(); 
					sKey = row+i;
					System.out.println(sKey + "-" + value.toString());
					context.write(new Text(sKey),value);
				}
			}
			
			//The input in 2nd file is present in the format : b,0,0,1
			//So if the mapper has to check if it is receiving the lines from input file 1 or input file 2.
			//Hence the condition....if(mat.matches("b"))
			if(mat.matches("b"))
			{
				for (int i =0; i < dim ; i++)
				{
					col = entry[2].trim();
					sKey = i+col;
					System.out.println(sKey + "-" + value.toString());
					context.write(new Text(sKey),value);
				}
			}
			
		}
	}

	

	/*	This is the second mapper method.

	The Matrix_Mapper1 class below takes key-value pairs as the input.
	INPUT :
	key : row number for input from 1st csv file
	      column number for input from 2nd csv file

	value : for(i from 0....dimension) take each line

	The output from Matrix_Mapper method is again a key-value pair which is sent to Matrix_Reducer method.
	OUTPUT :
	key :	row number for input from 1st csv file added to the loop variable value
		column number for input from 2nd csv file added to the loop variable value

	value : each line from the input files.
	
	*/
	public static class Matrix_Mapper1 extends Mapper<LongWritable,Text,Text,Text>
    	{
	
		@Override
		protected void map(LongWritable key, Text value,Context context)
							throws IOException, InterruptedException
		{

			String line = value.toString();
			String[] entry = line.split(",");
			String sKey = "";
			String mat = entry[0].trim();
			
			String row, col;
			
			Configuration conf = context.getConfiguration();
			String dimension = conf.get("dimension");
			
			System.out.println("Dimension from Mapper = " + dimension);
			
			int dim = Integer.parseInt(dimension);
			
			//The input in 1st file is present in the format : a,0,0,1
			//So if the mapper has to check if it is receiving the lines from input file 1 or input file 2.
			//Hence the condition....if(mat.matches("a"))
			if(mat.matches("a"))
			{
				for (int i =0; i < dim ; i++) 
				{
					row = entry[1].trim(); 
					sKey = row+i;
					System.out.println(sKey + "-" + value.toString());
					context.write(new Text(sKey),value);
				}
			}
			
			//The input in 2nd file is present in the format : b,0,0,1
			//So if the mapper has to check if it is receiving the lines from input file 1 or input file 2.
			//Hence the condition....if(mat.matches("b"))
			if(mat.matches("b"))
			{
				for (int i =0; i < dim ; i++)
				{
					col = entry[2].trim(); 
					sKey = i+col;
					System.out.println(sKey + "-" + value.toString());
					context.write(new Text(sKey),value);
				}
			}
			
		}
    	}
	/*
		The parameters to Matrix_Reducer method are the key-value pairs which are outputs from the two Mapper methods.
		Matrix_Reducer is the method where the result of multiplication is computed.
		INPUT :
			key :	row number for input from 1st csv file added to the loop variable value
				column number for input from 2nd csv file added to the loop variable value

			value : each line from the input files.
		OUTPUT :	
			key : line with row number, column number and product computed for each row and column received as parameters.

			value :	total sum computed for each row and column.
			This is written to the output file specified in the path 	
				ie. Path output = new Path("/usr/local/hadoop/page_rank2/page"+(depth)+".csv");
	*/
	
	public static class Matrix_Reducer extends Reducer<Text, Text, Text, DoubleWritable>
    	{
	
			@Override
			protected void reduce(Text key, Iterable<Text> values,Context context)
								throws IOException, InterruptedException 
			{
				
				Configuration conf = context.getConfiguration();
				String dimension = conf.get("dimension");
				
				int dim = Integer.parseInt(dimension);
				
				double[] row = new double[dim];
				double[] col = new double[dim];
				
				/*
					We have to take row 1 from 1st input file and 1st column from 2nd input file.
					That is being done here.
				*/
				for(Text val : values)
				{
					String[] entries = val.toString().split(",");
					if(entries[0].matches("a"))
					{
				
						//To select row number from first input file 
						int index = Integer.parseInt(entries[2].trim());
						//To select the value at that index in the row.
						row[index] = Double.parseDouble(entries[3].trim());
					}
					if(entries[0].matches("b"))
					{
						//To select column number from second input file 
						int index = Integer.parseInt(entries[1].trim());
						//To select the value at that index in the column.
						col[index] = Double.parseDouble(entries[3].trim());
					}
				}
				
				double total = 0;
				//The corresponding values from row and columns are multiplied and added to the total variable.
				for(int i = 0 ; i < 4; i++)
				{
					total += row[i]*col[i];
				}
				System.out.println(key.toString() + "-" + total );
                                String ke=key.toString();
                                String k[] = ke.split("");
                                Text t = new Text("b,"+k[0]+","+k[1]+",");
				//Writing computed values to the output file
				//OUTPUT :
				//	key : t
				//		Eg : b,0,0
				//	value : computed result
				//		Eg : 4
				//So in the output file the key value pair is combined to give b,0,0,4
				context.write(t,new DoubleWritable(Math.round(total * 100.0) / 100.0));
			}
    		}
	}
}
