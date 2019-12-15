package com.mathpar.NAUKMA.mag19.kulakovskyi;

import mpi.MPI;
import mpi.MPIException;

import java.util.Arrays;

//mpirun --hostfile /home/andrij/hostfile -np 3 java /home/andrij/mpi-dap/src/main/java/com/mathpar/NAUKMA/mag19/kulakovskyi/TestAllToAllv.java 3
//        myrank = 2: a = [2, 2, 2]
//        myrank = 1: a = [1, 1, 1]
//        myrank = 0: a = [0, 0, 0]
//        myrank = 1: q = [0, 1, 2]
//        myrank = 0: q = [0, 1, 2]
//        myrank = 2: q = [0, 1, 2]

public class TestAllToAllv {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.getRank();
        int n = Integer.parseInt(args[0]);
        int np = MPI.COMM_WORLD.getSize();
        int[] a = new int[n];
        for (int i = 0; i < n; i++)
            a[i] = myrank;
        System.out.println("myrank = " + myrank + ": a = " + Arrays.toString(a));
        int[] q = new int[n];
        MPI.COMM_WORLD.allToAllv(a, new int[]{1, 1,1,1},
                new int[]{0,1, 2,3}, MPI.INT, q,
                new int[]{1, 1,1,1}, new int[]{0, 1,2,3}, MPI.INT);
        System.out.println("myrank = " + myrank + ": q = " + Arrays.toString(q));
//завершення паралельної частини
        MPI.Finalize();
    }
}