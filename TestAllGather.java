package com.mathpar.NAUKMA.mag19.kulakovskyi;

import mpi.*;

// mpirun --hostfile /home/andrij/hostfile -np 7 java /home/andrij/mpi-dap/src/main/java/com/mathpar/NAUKMA/mag19/kulakovskyi/TestAllGather.java 3
//         myrank = 0 :
//         0
//         0
//         0
//         1
//         1
//         1
//         2
//         2
//         2
//         3
//         3
//         3
//         4
//         4
//         4
//         5
//         5
//         5
//         6
//         6
//         6
//
//         myrank = 1 :
//         0
//         0
//         0
//         1
//         1
//         1
//         2
//         2
//         2
//         3
//         3
//         3
//         4
//         4
//         4
//         5
//         5
//         5
//         6
//         6
//         6
//
//         myrank = 2 :
//         0
//         0
//         0
//         1
//         1
//         1
//         2
//         2
//         2
//         3
//         3
//         3
//         4
//         4
//         4
//         5
//         5
//         5
//         6
//         6
//         6
//
//         myrank = 3 :
//         0
//         0
//         0
//         1
//         1
//         1
//         2
//         2
//         2
//         3
//         3
//         3
//         4
//         4
//         4
//         5
//         5
//         5
//         6
//         6
//         6
//
//         myrank = 4 :
//         0
//         0
//         0
//         1
//         1
//         1
//         2
//         2
//         2
//         3
//         3
//         3
//         4
//         4
//         4
//         5
//         5
//         5
//         6
//         6
//         6
//
//         myrank = 5 :
//         0
//         0
//         0
//         1
//         1
//         1
//         2
//         2
//         2
//         3
//         3
//         3
//         4
//         4
//         4
//         5
//         5
//         5
//         6
//         6
//         6
//
//         myrank = 6 :
//         0
//         0
//         0
//         1
//         1
//         1
//         2
//         2
//         2
//         3
//         3
//         3
//         4
//         4
//         4
//         5
//         5
//         5
//         6
//         6
//         6


public class TestAllGather {
    public static void main(String[] args)throws Exception {
// ініціалізація MPI
        MPI.Init(args);
//визначення номеру процесора
        Thread t = new Thread();
        int myrank = MPI.COMM_WORLD.getRank();
//визначення числа процесорів в групі
        int np = MPI.COMM_WORLD.getSize();
        int n = Integer.parseInt(args[0]);
        int[] a = new int[n];
        for(int i = 0; i < n; i++) {
            a[i] = myrank;
        }
        int[] q = new int[n * np];
        MPI.COMM_WORLD.allGather(a, n, MPI.INT, q, n, MPI.INT);
        t.sleep(60 * myrank);
        System.out.println("myrank = " + myrank + " : ");
        for(int i = 0; i < q.length; i++) {
            System.out.println(" " + q[i]);
        }
        System.out.println();
//завершення паралельної частини
        MPI.Finalize();
    }
}