package com.mathpar.NAUKMA.mag19.kulakovskyi;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.NumberZp32;
import com.mathpar.number.Ring;
import mpi.MPI;
import mpi.MPIException;

import java.io.IOException;
import java.util.Random;

//        I'm processor 1
//        I'm processor 4
//        I'm processor 7I'm processor 6
//
//        I'm processor 2
//        I'm processor 5
//        I'm processor 3
//        A =
//        [[0.08, 0.45, 0.23, 0.05]
//        [0.78, 0.5,  0.5,  0.91]
//        [0.19, 0.67, 0.23, 0.48]
//        [0.03, 0.33, 0.87, 0.74]]
//        B =
//        [[0.72, 0.51, 0.68, 0.23]
//        [0.97, 0.87, 0.39, 0.73]
//        [0.47, 0.36, 0.91, 0.94]
//        [0.85, 0.9,  0.26, 0.79]]
//        RES =
//        [[0.64, 0.56, 0.45, 0.6 ]
//        [2.06, 1.83, 1.42, 1.74]
//        [1.3,  1.19, 0.72, 1.13]
//        [1.39, 1.28, 1.13, 1.66]]


public class MatrixMul8 {
    public static void main(String[] args)
            throws MPIException, IOException,
            ClassNotFoundException {
        Ring ring = new Ring("R64[x]");
// инициализация MPI
        MPI.Init(new String[0]);
// получение номера узла
        int rank = MPI.COMM_WORLD.getRank();
        if (rank == 0) {
            // программа выполняется на нулевом процессоре
            int ord = 4;
            int den = 10000;
// представитель класса случайного генератора
            Random rnd = new Random();
// ord = размер матрицы, den = плотность
            MatrixS A = new MatrixS(ord, ord, den, new int[] {5, 3},
                    rnd, NumberZp32.ONE, ring);
            System.out.println("A = " + A);
            MatrixS B = new MatrixS(ord, ord, den, new int[] {5, 3},
                    rnd, NumberZp32.ONE, ring);
            System.out.println("B = " + B);
            MatrixS D = null;
// разбиваем матрицу A на 4 части
            MatrixS[] AA = A.split();
            System.out.println("Split matrix A");
// разбиваем матрицу B на 4 части
            MatrixS[] BB = B.split();
            System.out.println("Split matrix B");
            int tag = 0;
// посылка от нулевого процессора массива Object процессору rank с идентификатором tag
            System.out.println("Sending objects to proc 1");
            System.out.println(AA[1] + " " + BB[2]);
            Transport.sendObjects(new Object[] {AA[1], BB[2]},
                    1, tag);
            System.out.println("Sending objects to proc 2");
            Transport.sendObjects(new Object[] {AA[0], BB[1]},
                    2, tag);
            System.out.println("Sending objects to proc 3");
            Transport.sendObjects(new Object[] {AA[1], BB[3]},
                    3, tag);
            System.out.println("Sending objects to proc 4");
            Transport.sendObjects(new Object[] {AA[2], BB[0]},
                    4, tag);
            System.out.println("Sending objects to proc 5");
            Transport.sendObjects(new Object[] {AA[3], BB[2]},
                    5, tag);
            System.out.println("Sending objects to proc 6");
            Transport.sendObjects(new Object[] {AA[2], BB[1]},
                    6, tag);
            System.out.println("Sending objects to proc 7");
            Transport.sendObjects(new Object[] {AA[3], BB[3]},
                    7, tag);

            MatrixS[] DD = new MatrixS[4];
// оставляем один блок нулевому процессору для обработки
            DD[0] = (AA[0].multiply(BB[0], ring)).add((MatrixS) Transport.recvObject(1, 3),
                    ring);
            DD[1] = (MatrixS) Transport.recvObject(2, 3);
            DD[2] = (MatrixS) Transport.recvObject(4, 3);
            DD[3] = (MatrixS) Transport.recvObject(6, 3);
            D = MatrixS.join(DD);
            System.out.println("RES = " + D.toString());
        } else {
// программа выполняется на процессоре с рангом rank
            System.out.println("I’m processor " + rank);
            Object[] b = Transport.recvObjects(2, 0, 0);
            System.out.println("Receiving objects from rank 0");
            MatrixS[] a = new MatrixS[b.length];
            for (int i = 0; i < b.length; i++) {
                a[i] = (MatrixS) b[i];
            }
            MatrixS res = a[0].multiply(a[1], ring);
            if (rank % 2 == 0) {
                MatrixS p = res.add((MatrixS) Transport.
                        recvObject(rank + 1, 3), ring);
                System.out.println("Rank % 2: " + rank);
                System.out.println("Send to proc 0: " + p);
                Transport.sendObject(p, 0, 3);
            } else {
                System.out.println("Rank: " + rank + " Send to proc 0: " + res);
                Transport.sendObject(res, rank - 1, 3);
            }
        }
        MPI.Finalize();
    }

}