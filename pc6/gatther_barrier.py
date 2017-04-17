from mpi4py import MPI
comm = MPI.COMM_WORLD
size = comm.Get_size()
rank = comm.Get_rank()
data = (rank+1)**2
print "before gather, data on \
rank %d is: "%rank, data
comm.Barrier()
data = comm.gather(data, root=0)
if rank == 0:
 for i in range(size):
 assert data[i] == (i+1)**2
else:
 assert data is None
print "data on rank: %d is: "%rank, data

'''
Output:
before gather, data on rank 3 is: 16
before gather, data on rank 0 is: 1
before gather, data on rank 1 is: 4
before gather, data on rank 2 is: 9
data on rank: 1 is: None
data on rank: 3 is: None
data on rank: 2 is: None
data on rank: 0 is: [1, 4, 9, 16]
'''