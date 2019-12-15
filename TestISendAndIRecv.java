package com.mathpar.NAUKMA.mag19.kulakovskyi;

import java.math.BigInteger;
import java.nio.Buffer;
import java.nio.IntBuffer;


import java.util.Random;
import mpi.*;

//mpirun --hostfile /home/andrij/hostfile -np 3 java /home/andrij/mpi-dap/src/main/java/com/mathpar/NAUKMA/mag19/kulakovskyi/TestISendAndIRecv.java 3
//        proc num = 2 масив прийнятий
//        proc num = 1 масив прийнятий
//        proc num = 0  Масив відправлений

public class TestISendAndIRecv {
    public static void main(String[] args) throws MPIException {
//ініціалізація MPI
        MPI.Init(args);
//визначення номеру процесора
        int myrank = MPI.COMM_WORLD.getRank();
//визначення числа процесорів в групі
        int np = MPI.COMM_WORLD.getSize();
//вхідний параметр розмір масиву
        int n = Integer.parseInt(args[0]);
        IntBuffer b = MPI.newIntBuffer(n);
        MPI.COMM_WORLD.barrier();
        if (myrank == 0) {
            for (int i = 0; i < n; i++){
                b.put(new Random().nextInt(10));
            }
            for (int i = 1; i < np; i++) {
                MPI.COMM_WORLD.iSend(b, b.capacity(), MPI.INT, i, 3000);
            }
            System.out.println("proc num = " + myrank +
                    "  Масив відправлений");

        } else {
            MPI.COMM_WORLD.recv(b, b.capacity(), MPI.INT, 0, 3000);
            System.out.println("proc num = " + myrank +
                    " масив прийнятий");
        }
//завершення паралельної частини
        MPI.Finalize();
    }
}
