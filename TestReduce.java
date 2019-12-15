package com.mathpar.NAUKMA.mag19.kulakovskyi;

import mpi.MPI;
import mpi.MPIException;
//
//mpirun --hostfile /home/andrij/hostfile -np 3 java /home/andrij/mpi-dap/src/main/java/com/mathpar/NAUKMA/mag19/kulakovskyi/TestReduce.java 3
//        0
//        3
//        6

public class TestReduce {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.getRank();
        int n = Integer.parseInt(args[0]);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = i;
        }
        int[] q = new int[n];
        MPI.COMM_WORLD.reduce(a, q, n, MPI.INT, MPI.SUM, 0);
        if (myrank == 0) {
            for (int i = 0; i < q.length; i++) {
                System.out.println(" " + q[i]);
            }
        }
        MPI.Finalize();
    }
}
