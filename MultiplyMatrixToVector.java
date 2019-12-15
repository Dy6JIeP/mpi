package com.mathpar.NAUKMA.mag19.kulakovskyi;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.*;
import mpi.MPI;
import mpi.MPIException;

import java.io.IOException;
import java.util.Random;


/*
I’m processor 2
I’m processor 1
I’m processor 3
Matrix A =
[[27, 20, 22, 9 ]
 [9,  17, 3,  27]
 [12, 14, 11, 26]
 [7,  11, 21, 13]]
Vector B = [20, 2, 9, 25]
rank = 0 row = [27, 20, 22, 9]
rank = 1 row = [9, 17, 3, 27]
rank = 1 B = [20, 2, 9, 25]
send result
rank = 2 row = [12, 14, 11, 26]
rank = 2 B = [20, 2, 9, 25]
send result
rank = 3 row = [7, 11, 21, 13]
rank = 3 B = [20, 2, 9, 25]
send result
A * B = [1003, 916, 1017, 676]

 */


public class MultiplyMatrixToVector {
    public static void main(String[] args) throws MPIException,
            IOException, ClassNotFoundException {

        Ring ring = new Ring("Z[x]");
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();
//размер матрицы
        int ord = Integer.parseInt(args[0]);
//количество строк для процессоров c rank > 0
        int k = ord / size;
//количество строк для 0 процессора
        int n = ord - k * (size - 1);
        if (rank == 0) {
            int den = 10000;
            Random rnd = new Random();
            MatrixS A = new MatrixS(ord, ord, den, new int[]{5, 5},
                    rnd, NumberZp32.ONE, ring);
            System.out.println("Matrix A = " + A);
            VectorS B = new VectorS(ord, den, new int[]{5, 5},
                    rnd, ring);
            System.out.println("Vector B = " + B);
//создаем массив с результатом 0 процессора
            Element[] res0 = new Element[n];
            for (int i = 0; i < n; i++) {
                res0[i] = new VectorS(A.M[i]).multiply(B.transpose(ring), ring);
                System.out.println("rank = " + rank + " row = "
                        + Array.toString(A.M[i]));

            }
//рассылка строк процессорам
            for (int j = 1; j < size; j++) {
                for (int z = 0; z < k; z++) {
                    Transport.sendObject(A.M[n + (j - 1) * k + z], j, 100 + j);
                }
//отправляем вектор каждому процессору
                Transport.sendObject(B.V, j, 100 + j);
            }
//массив результата умножения матрицы на вектор
            Element[] result = new Element[ord];
            System.arraycopy(res0, 0, result, 0, n);
//приемка результатов от каждого процессора
            for (int t = 1; t < size; t++) {
                Element[] resRank = (Element[])
                        Transport.recvObject(t, 100 + t);
                System.arraycopy(resRank, 0, result, n +
                        (t - 1) * k, resRank.length);

            }
            System.out.println("A * B = " +
                    new VectorS(result).toString(ring));
        } else {
//программа выполняется на процессоре с рангом = rank
            System.out.println("I’m processor " + rank);
//приемка строк матрицы от 0 процессора
            Element[][] A = new Element[k][ord];
            for (int i = 0; i < k; i++) {
                A[i] = (Element[])
                        Transport.recvObject(0, 100 + rank);
                System.out.println("rank = " + rank + " row = "
                        + Array.toString(A[i]));

            }
//приемка вектора от 0 процессора
            Element[] B = (Element[])
                    Transport.recvObject(0, 100 + rank);
            System.out.println("rank = " + rank + " B = "
                    + Array.toString(B));
//создание массива результата умножения
//строк матрицы на вектор
            Element[] result = new Element[k];
            for (int j = 0; j < A.length; j++) {
                result[j] = new VectorS(A[j]).multiply(
                        new VectorS(B).transpose(ring), ring);

            }

//отсылаем результат 0 процессорy
            Transport.sendObject(result, 0, 100 + rank);
            System.out.println("send result");
        }
        MPI.Finalize();
    }
}