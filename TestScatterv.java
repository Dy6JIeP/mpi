package com.mathpar.NAUKMA.mag19.kulakovskyi;

import mpi.*;

//mpirun --hostfile /home/andrij/hostfile -np 2 java /home/andrij/mpi-dap/src/main/java/com/mathpar/NAUKMA/mag19/kulakovskyi/TestScatterv.java 3
//        myrank = 1; 1
//        myrank = 1; 2
//        myrank = 1; 0
//        myrank = 0; 0
//        myrank = 0; 1
//        myrank = 0; 2


public class TestScatterv {
    public static void main(String[] args)
            throws MPIException {
// ініціалізація MPI
        MPI.Init(args);
//визначення номеру процесора
        int myrank = MPI.COMM_WORLD.getRank();
        int n = Integer.parseInt(args[0]);
//визначення числа процесорів в групі
        int np = MPI.COMM_WORLD.getSize();
// оголошуємо масив цілих чисел
        int[] a = new int[n];
        if (myrank == 0) {
            for (int i = 0; i < a.length; i++)
                a[i] = i;
        }
// оголошуємо масив в який будуть записуватись
// прийняті процесоромелементи
        int[] q = new int[n];
        MPI.COMM_WORLD.barrier();
        MPI.COMM_WORLD.scatterv(a, new int[]{3, 2, 1, 1},
                new int[]{0, 1, 2, 0}, MPI.INT, q, n, MPI.INT, 0);
// роздруковуємо елементи масиву та імена процесора
        for (int i = 0; i < q.length; i++)
            System.out.print("myrank = " + myrank
                    + "; " + q[i] + "\n");
//завершення паралельної частини
        MPI.Finalize();
    }
}
