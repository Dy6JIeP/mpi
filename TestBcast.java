package com.mathpar.NAUKMA.mag19.kulakovskyi;

import java.math.BigInteger;
import java.util.Random;
import mpi.*;

//mpirun --hostfile /home/andrij/hostfile -np 3 java /home/andrij/mpi-dap/src/main/java/com/mathpar/NAUKMA/mag19/kulakovskyi/TestBcast.java 3
//        a[0]= 0.10984530836842832
//        a[1]= 0.9980002130998546
//        a[2]= 0.5709069134138705
//        a[0]= 0.10984530836842832
//        a[1]= 0.9980002130998546
//        a[2]= 0.5709069134138705
//        a[0]= 0.10984530836842832
//        a[1]= 0.9980002130998546
//        a[2]= 0.5709069134138705

/**
 * процесор з номером -0 пересилає масив чисел
 * іншим процесорам
 * використовуючи роздачу за бінарним деревом
 */
public class TestBcast {
    public static void main(String[] args)
            throws MPIException {
// ініціалізація MPI
        MPI.Init(args);
//визначення номеру процесора
        int myrank = MPI.COMM_WORLD.getRank();
        int n = Integer.parseInt(args[0]);
        double[] a = new double[n];
        if (myrank == 0) {
            for (int i = 0; i < n; i++) {
                a[i] = new Random().nextDouble();
                System.out.println("a[" + i + "]= " + a[i]);
            }
        }
        MPI.COMM_WORLD.barrier();
// передача даних від 0 процесора іншим
        MPI.COMM_WORLD.bcast(a, a.length, MPI.DOUBLE, 0);
        if (myrank != 0) {
            for (int i = 0; i < n; i++) {
                System.out.println("a[" + i + "]= " + a[i]);
            }
        }
//завершення паралельної частини
        MPI.Finalize();
    }
}