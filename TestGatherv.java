package com.mathpar.NAUKMA.mag19.kulakovskyi;

import mpi.MPI;
import mpi.MPIException;

//mpirun --hostfile /home/andrij/hostfile -np 2 java /home/andrij/mpi-dap/src/main/java/com/mathpar/NAUKMA/mag19/kulakovskyi/TestGatherv.java 2
//        0
//        0
//        1
//        1

public class TestGatherv {
    public static void main(String[] args) throws MPIException {
// ініціалізація MPI
        MPI.Init(args);
//визначення номеру процесора
        int myrank = MPI.COMM_WORLD.getRank();
//визначення числа процесорів в групі
        int np = MPI.COMM_WORLD.getSize();
        int n = Integer.parseInt(args[0]);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = myrank;
        }
        int[] q = new int[n * np];
        MPI.COMM_WORLD.gatherv(a, n, MPI.INT, q,
                new int[]{n, n}, new int[]{0, 2},
                MPI.INT, np - 1);
        if (myrank == np - 1) {
            for (int i = 0; i < q.length; i++) {
                System.out.println(" " + q[i]);
            }
        }
//завершення паралельної частини
        MPI.Finalize();
    }
}
