package com.mathpar.NAUKMA.mag19.kulakovskyi;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.NumberZp32;
import com.mathpar.number.Ring;
import mpi.MPI;
import mpi.MPIException;

import java.io.IOException;
import java.util.Random;


/*
I'm processor 1
        I'm processor 2
        I'm processor 3
        res =
        [
            [0.78, 1.19]
            [1.17, 1.75]
        ]
        send result
        res =
        [
            [1.32, 1.82]
            [0.47, 0.31]
        ]
        recv 1 to 0
        send result
        recv 2 to 0
        res =
        [
            [1.68, 2.24]
            [0.41, 0.41]
        ]
        send result
        recv 3 to 0
        RES=
        [
            [0.71, 0.85, 0.78, 1.19]
            [1.19, 1.46, 1.17, 1.75]
            [1.32, 1.82, 1.68, 2.24]
            [0.47, 0.31, 0.41, 0.41]
        ]
*/

public class MatrixMul4 {
    static int tag = 0;
    static int mod = 13;

    public static MatrixS mmultiply(MatrixS a, MatrixS b, MatrixS c, MatrixS d, Ring ring) {

        // помножимо a на b, с на d і додамо результати
        return (a.multiply(b, ring)).add(c.multiply(d, ring), ring);
    }

    public static void main(String[] args) throws MPIException, IOException, ClassNotFoundException {
        Ring ring = new Ring("R64[x]");
        //ініціалізація MPI
        MPI.Init(new String[0]);
        // отримання номера вузла
        int rank = MPI.COMM_WORLD.getRank();
        if (rank == 0) {
            // програма виконується на нульовому процесорі
            ring.setMOD32(mod);
            int ord = 4;
            int den = 10000;
            // представник класу випадкового генератора
            Random rnd = new Random();
            // ord = розмір матриці, den = щільність
            MatrixS A = new MatrixS(ord, ord, den,
                    new int[]{5, 5}, rnd, NumberZp32.ONE, ring);
            MatrixS B = new MatrixS(ord, ord, den,
                    new int[]{5, 5}, rnd, NumberZp32.ONE, ring);
            MatrixS[] DD = new MatrixS[4];
            MatrixS CC = null;
            // розбиваємо матрицю A на 4 частини
            MatrixS[] AA = A.split();
            // розбиваємо матрицю В на 4 частини
            MatrixS[] BB = B.split();
            //посилка від нульового процесора масиву Objec процесору 1 з ідентифікатором tag = 1
            Transport.sendArrayOfObjects(new Object[]{AA[0], BB[1],
                    AA[1], BB[3]}, 1, 1);
            //посилка від нульового процесора масиву Objec процесору 2 з ідентифікатором tag = 2
            Transport.sendArrayOfObjects(new Object[]{AA[2], BB[0],
                    AA[3], BB[2]}, 2, 2);
            //посилка від нульового процесора масиву Objec процесору 3 з ідентифікатором tag = 3
            Transport.sendArrayOfObjects(new Object[]{AA[2], BB[1],
                    AA[3], BB[3]}, 3, 3);
            //залишаємо один блок нульового процесору для обробки
            DD[0] = (AA[0].multiply(BB[0], ring)).
                    add(AA[1].multiply(BB[2], ring), ring);
            // приймаємо результат від першого процесора
            DD[1] = (MatrixS) Transport.recvObject(1, 1);
            System.out.println("recv 1 to 0");
            // приймаємо результат від другого процесора
            DD[2] = (MatrixS) Transport.recvObject(2, 2);
            System.out.println("recv 2 to 0");
            // приймаємо результат від третього процесора
            DD[3] = (MatrixS) Transport.recvObject(3, 3);
            System.out.println("recv 3 to 0");
            // процедура складання матриці з блоків DD[i] (i=0,...,3)
            CC = MatrixS.join(DD);
            System.out.println("RES= " + CC.toString());
        } else {
            //програма виконується на процесорі з рангом rank
            System.out.println("I'm processor " + rank);
            ring.setMOD32(mod);
            // отримуємо масив  Object з блоками матриць від нульового процесора
            Object[] n = Transport.recvArrayOfObjects(0, rank);
            MatrixS a = (MatrixS) n[0];
            MatrixS b = (MatrixS) n[1];
            MatrixS c = (MatrixS) n[2];
            MatrixS d = (MatrixS) n[3];
            // перемножуємо i складаємо блоки матриць
            MatrixS res = mmultiply(a, b, c, d, ring);
            // посилаємо результат обчислень від процесора rank нульовому процесору
            System.out.println("res = " + res);
            Transport.sendObject(res, 0, rank);
            // повідомлення на консоль про те, що
            // результат буде посланий
            System.out.println("send result");
        }
        MPI.Finalize();
    }
}