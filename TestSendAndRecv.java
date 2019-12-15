package com.mathpar.NAUKMA.mag19.kulakovskyi;

import java.util.Random;
import mpi.*;

//mpirun --hostfile /home/andrij/hostfile -np 2 java /home/andrij/mpi-dap/src/main/java/com/mathpar/NAUKMA/mag19/kulakovskyi/TestSendAndRecv.java 3
//        a[0]= 0.8393368866362614
//        a[1]= 0.4983078203773127
//        a[2]= 0.9758572406366175
//        Proc num 0 Масив відправлений
//
//        a[0]= 0.8393368866362614
//        a[1]= 0.4983078203773127
//        a[2]= 0.9758572406366175
//        Proc num 1 масив прийнятий

public class TestSendAndRecv {
    public static void main(String[] args)
            throws MPIException {

//ініціалізація MPI
        MPI.Init(args);
//визначення номеру процесора
        int myrank = MPI.COMM_WORLD.getRank();
//визначення числа процесорів в групі
        int np = MPI.COMM_WORLD.getSize();

//вхідний параметр розмір масиву
        int n = Integer.parseInt(args[0]);
        double[] a = new double[n];
//синхронізація процесорів
        MPI.COMM_WORLD.barrier();
//якщо процесор з номером 0
        if (myrank == 0) {
            for (int i = 0; i < n; i++) {
                a[i] = (new Random()).nextDouble();
                System.out.println("a[" + i + "]= " + a[i]);
            }
//передача 0-процесором елементів
            for (int i = 1; i < np; i++) {
                MPI.COMM_WORLD.send(a, n, MPI.DOUBLE, i, 3000);
            }
            System.out.println("Proc num " + myrank +
                    " Масив відправлений" + "\n");
        } else {
//Прийом і-м процесором повідомлення від
//процесора з номером 0
            MPI.COMM_WORLD.recv(a, n, MPI.DOUBLE, 0, 3000);
            for (int i = 0; i < n; i++) {
                System.out.println("a[" + i + "]= " + a[i]);
            }
            System.out.println("Proc num " + myrank +
                    " масив прийнятий" + "\n");
        }
//завершення паралельної частини
        MPI.Finalize();
    }
}
