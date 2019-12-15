package com.mathpar.NAUKMA.mag19.kulakovskyi;

import java.nio.IntBuffer;
import java.math.BigInteger;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Random;
import mpi.*;

//mpirun --hostfile /home/andrij/hostfile -np 3 java /home/andrij/mpi-dap/src/main/java/com/mathpar/NAUKMA/mag19/kulakovskyi/TestAllToAll.java 3
//        myrank = 1: a = [1, 1, 1]
//        myrank = 2: a = [2, 2, 2]
//        myrank = 0: a = [0, 0, 0]
//        myrank = 2: q = [0, 1, 2]
//        myrank = 0: q = [0, 1, 2]
//        myrank = 1: q = [0, 1, 2]

public class TestAllToAll {
    public static void main(String[] args) throws MPIException {
// ініціалізація MPI
        MPI.Init(args);
//визначення номеру процесора
        int myrank = MPI.COMM_WORLD.getRank();
//визначення числа процесорів в групі
        int np = MPI.COMM_WORLD.getSize();
        int n = Integer.parseInt(args[0]);
//створення масиву з 4 елементів
        int [] a = new int[n];
        for(int i = 0; i < n; i++) a[i] = myrank;
        System.out.println("myrank = " + myrank + ": a = " + Arrays.toString(a));
        int[] q = new int[n];
        MPI.COMM_WORLD.allToAll(a, 1, MPI.INT, q, 1, MPI.INT);
        System.out.println("myrank = " + myrank + ": q = " + Arrays.toString(q));
//завершення паралельної частини
        MPI.Finalize();
    }
}