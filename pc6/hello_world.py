# helloworld.py

from mpi4py import MPI
import sys

size = MPI.COMM_WORLD.Get_size()
rank = MPI.COMM_WORLD.Get_rank()
name = MPI.Get_processor_name()
print(“Helloworld! I am process \
%d of %d on %s.\n” % (rank, size, name))

'''
Output:
Helloworld! I am process 0 of 4 on Sovereign.
Helloworld! I am process 1 of 4 on Sovereign.
Helloworld! I am process 2 of 4 on Sovereign.
Helloworld! I am process 3 of 4 on Sovereign.
'''