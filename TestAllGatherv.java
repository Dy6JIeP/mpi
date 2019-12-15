package com.mathpar.NAUKMA.mag19.kulakovskyi;

import mpi.MPI;
import mpi.MPIException;

//mpirun --hostfile /home/andrij/hostfile -np 3 java /home/andrij/mpi-dap/src/main/java/com/mathpar/NAUKMA/mag19/kulakovskyi/TestAllGather.java 3
//        myrank = 0 :
//        0
//        0
//        0
//        1
//        1
//        1
//        2
//        2
//        2
//
//        myrank = 1 :
//        0
//        0
//        0
//        1
//        1
//        1
//        2
//        2
//        2
//
//        myrank = 2 :
//        0
//        0
//        0
//        1
//        1
//        1
//        2
//        2
//        2

public class TestAllGatherv {
    public static void main(String[] args) throws MPIException,
            InterruptedException {
// ініціалізація MPI
        MPI.Init(args);
//визначення номеру процесора
        int myrank = MPI.COMM_WORLD.getRank();
        int n = Integer.parseInt(args[0]);
        int np = MPI.COMM_WORLD.getSize();
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = i;
        }
        int[] q = new int[n*np];
        MPI.COMM_WORLD.allGatherv(a, n, MPI.INT,
                q, new int[]{n, n}, new int[]{2, 0}, MPI.INT);
        Thread.sleep(60 * myrank);
        System.out.println("myrank " + myrank + " : ");
        for (int i = 0; i < q.length; i++) {
            System.out.println(" " + q[i]);
        }
        System.out.println();
//завершення паралельної частини
        MPI.Finalize();
    }
}