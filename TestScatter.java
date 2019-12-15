package com.mathpar.NAUKMA.mag19.kulakovskyi;

import mpi.*;

//mpirun --hostfile /home/andrij/hostfile -np 2 java /home/andrij/mpi-dap/src/main/java/com/mathpar/NAUKMA/mag19/kulakovskyi/TestScatter.java 3
//        a = 0
//        a = 1
//        rank = 0
//        myrank = 0 ; 0
//
//        myrank = 0 ; 0
//
//        myrank = 1 ; 1
//
//        myrank = 1 ; 0


public class TestScatter {
    public static void main(String[] args) throws MPIException {
// ініціалізація MPI
        MPI.Init(args);
//визначення номеру процесора
        int myrank = MPI.COMM_WORLD.getRank();
//визначення числа процесорів в групі
        int np = MPI.COMM_WORLD.getSize();
        int n = Integer.parseInt(args[0]);
// оголошуємо масив об'єктів
        int[] a = new int[2];
// запомвнюємо масив на 0 процесорі
        if (myrank == 0) {
            for (int i = 0; i < 2; i++) {
                a[i] = i;
                System.out.println("a = " + a[i] + " ");
            }
            System.out.println("rank = " + myrank);
        }
// оголошуємо масив в який будуть записуватись
// прийняті процесоромелементи
        int[] q = new int[2];
        MPI.COMM_WORLD.barrier();
        MPI.COMM_WORLD.scatter(a, 1, MPI.INT, q, 1, MPI.INT, 0);
// роздруковуємо прийнятий масив і номера процесора
        for (int i = 0; i < q.length; i++)
            System.out.println("myrank = " + myrank
                    + " ; " + q[i] + "\n");
//завершення паралельної частини
        MPI.Finalize();
    }
}
