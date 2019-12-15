package com.mathpar.NAUKMA.mag19.kulakovskyi;

import mpi.MPI;
import mpi.MPIException;

//mpirun --hostfile /home/andrij/hostfile -np 7 java /home/andrij/mpi-dap/src/main/java/com/mathpar/NAUKMA/mag19/kulakovskyi/HelloWorldParallel.java
//        Proc num 3 Hello World
//        Proc num 1 Hello World
//        Proc num 0 Hello World
//        Proc num 6 Hello World
//        Proc num 4 Hello World
//        Proc num 5 Hello World
//        Proc num 2 Hello World

public class HelloWorldParallel {
    public static void main(String[] args)
            throws MPIException {
        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.getRank();
        System.out.println("Proc num " + myrank + " Hello World");

        MPI.Finalize();
    }
}
