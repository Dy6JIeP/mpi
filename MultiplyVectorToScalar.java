package com.mathpar.NAUKMA.mag19.kulakovskyi;

import com.mathpar.number.*;
import mpi.MPI;
import mpi.MPIException;

import java.io.IOException;
import java.util.Random;


//I'm processor 1I'm processor 3
//
//        I'm processor 2
//        Vector B = [23, 26, 20, 0]
//        rank = 2 B = [20]
//        rank = 1 B = [26]
//        rank = 3 B = [0]
//        send result
//        send result
//        send result
//        B * S = [46, 52, 40, 0]
//

public class MultiplyVectorToScalar {
    public static void main(String[] args) throws MPIException, IOException, ClassNotFoundException {
        Ring ring = new Ring("Z[x]");
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();
        //розмір вектора
        int ord = Integer.parseInt(args[0]);
        //число
        Element s = NumberR64.valueOf(Integer.parseInt(args[1]));
        //кількість елементів для процесорів з rank > 0
        int k = ord / size;
        //кількість елементів вектора для 0 процесора
        int n = ord - k * (size - 1);
        if (rank == 0) {
            int den = 10000;
            Random rnd = new Random();
            VectorS B = new VectorS(ord, den, new int[]{5, 5},
                    rnd, ring);
            System.out.println("Vector B = " + B);
            //створюємо масив з результатом на 0 процесорі
            Element[] res0 = new Element[n];
            for (int i = 0; i < n; i++) {
                res0[i] = B.V[i].multiply(s, ring);
            }
            //розсилка елементів вектора
            for (int j = 1; j < size; j++) {
                Element[] v = new Element[k];
                System.arraycopy(B.V, n + (j - 1) * k, v, 0, k);
                Transport.sendObject(v, j, 100 + j);
            }
            //масив з результатом множення вектора на скаляр
            Element[] result = new Element[ord];
            System.arraycopy(res0, 0, result, 0, n);
            //отримуємо результати від кожного процесора
            for (int t = 1; t < size; t++) {
                Element[] resRank = (Element[])
                        Transport.recvObject(t, 100 + t);
                System.arraycopy(resRank, 0, result, n +
                        (t - 1) * k, resRank.length);
            }
            System.out.println("B * S = " +
                    new VectorS(result).toString(ring));
        } else {
            //програма виконується на процесорі з рангом = rank
            System.out.println("I'm processor " + rank);
            //приймаємо частина вектора  B від 0 процесора
            Element[] B = (Element[])
                    Transport.recvObject(0, 100 + rank);
            System.out.println("rank = " + rank +
                    " B = " + Array.toString(B));
            //створення масиву з результатом множення
            //вектора на скаляр
            Element[] result = new Element[k];
            for (int j = 0; j < B.length; j++) {
                result[j] = B[j].multiply(s, ring);
            }
            //посилаємо результат 0 процесору
            Transport.sendObject(result, 0, 100 + rank);
            System.out.println("send result");
        }
        MPI.Finalize();
    }
}