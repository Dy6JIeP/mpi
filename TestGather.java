package com.mathpar.NAUKMA.mag19.kulakovskyi;

import mpi.*;

// mpirun --hostfile /home/andrij/hostfile -np 3 java /home/andrij/mpi-dap/src/main/java/com/mathpar/NAUKMA/mag19/kulakovskyi/TestGather.java 3
//         0
//         0
//         0
//         1
//         1
//         1
//         2
//         2
//         2

public class TestGather {
    public static void main(String[] args) throws Exception {
// ініціалізація MPI
        MPI.Init(args);
//визначення номеру процесора
        int myrank = MPI.COMM_WORLD.getRank();
//визначення числа процесорів в групі
        int np = MPI.COMM_WORLD.getSize();
        int n = Integer.parseInt(args[0]);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = myrank;
        int[] q = new int[n * np];
        MPI.COMM_WORLD.gather(a, n, MPI.INT, q, n, MPI.INT, np - 1);
        if (myrank == np - 1) {
            for (int i = 0; i < q.length; i++)
                System.out.println(" " + q[i]);
        }
//завершення паралельної частини
        MPI.Finalize();
    }
}
