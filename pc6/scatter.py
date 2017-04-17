from mpi4py import MPI

comm = MPI.COMM_WORLD
size = comm.Get_size()
rank = comm.Get_rank()
#rank 0 is initialising data
if rank == 0:
 data = [(i+1)**2 for i in range(size)]
else:
 data = None
#rank 0 is scattering that data
data = comm.scatter(data, root=0)
assert data == (rank+1)**2
print "data on rank %d is: "%comm.rank, data

'''
Output:
data on rank 0 is: 1
data on rank 1 is: 4
data on rank 2 is: 9
data on rank 3 is: 16 
'''
