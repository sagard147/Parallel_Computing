from mpi4py import MPI

comm = MPI.COMM_WORLD
rank = comm.Get_rank()

#rank 0 is sending data
if rank == 0:
 data = {'a': 7, 'b': 3.14}
 comm.send(data, dest=1, tag=11)
 print "Message sent, data is: ", data
#rank 1 is receiving data
elif rank == 1:
 data = comm.recv(source=0, tag=11)
 print "Message Received, data is: ", data
 
 '''
Output:
Message sent, data is: {'a': 7, 'b': 3.14}
Message Received, data is: {'a': 7, 'b': 3.14}
 '''