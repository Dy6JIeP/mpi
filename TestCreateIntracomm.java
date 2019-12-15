package com.mathpar.NAUKMA.mag19.kulakovskyi;

import java.util.Random;
import mpi.Intracomm;
import mpi.MPI;
import mpi.MPIException;

//mpirun --hostfile /home/andrij/hostfile -np 2 java /home/andrij/mpi-dap/src/main/java/com/mathpar/NAUKMA/mag19/kulakovskyi/TestCreateIntracomm.java 2
//        a[0]= 0.3497269779418918
//        a[1]= 0.5587433674420038
//        a[0]= 0.3497269779418918
//        a[1]= 0.5587433674420038


public class TestCreateIntracomm {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
//визначаємо нову групу працюючих процесів
        mpi.Group g = MPI.COMM_WORLD.getGroup().incl(new int[]{0, 1});
//створюємо новий комунікатор
        Intracomm COMM_NEW = MPI.COMM_WORLD.create(g);
        int myrank = COMM_NEW.getRank();
        int n = Integer.parseInt(args[0]);
        double[] a = new double[n];
        if (myrank == 0) {
            for (int i = 0; i < n; i++) {
                a[i] = new Random().nextDouble();
                System.out.println("a[" + i + "]= " + a[i]);
            }
        }
        COMM_NEW.barrier();
//застосовуємо до нового комунікатора функцію bcast
        COMM_NEW.bcast(a, a.length, MPI.DOUBLE, 0);
        if (myrank != 0) {
            for (int i = 0; i < n; i++) {
                System.out.println("a[" + i + "]= " + a[i]);
            }
        }
        MPI.Finalize();
    }
}
