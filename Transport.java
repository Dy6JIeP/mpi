package com.mathpar.NAUKMA.mag19.kulakovskyi;

import mpi.MPI;
import mpi.MPIException;
import mpi.Status;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Transport {

    /**
     * Метод посылки массива объектов
     *
     * @param a    - массив объектов
     * @param proc - номер процессора
     * @param tag  - идентификатор сообщения
     * @throws MPIException
     * @throws IOException
     */
    public static void sendArrayOfObjects(Object[] a, int proc, int tag)
            throws MPIException, IOException {
        // посылка объекта a[i] процессору с номером proc
        // с тегом tag + i
        for (int i = 0; i < a.length; i++) {
            sendObject(a[i], proc, tag + i);
        }
    }

    /**
     * Метод приемки массива объектов
     *
     * @param proc - номер процессора
     * @param tag  - идентифкатор сообщения
     * @return - полученный массив объектов
     * @throws MPIException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object[] recvArrayOfObjects(int proc, int tag)
            throws MPIException, IOException, ClassNotFoundException {
        Object[] o = new Object[4];
        //прием объекта a[i] от процессора с номером proc
        // с тегом tag + i
        for (int i = 0; i < 4; i++) {
            o[i] = recvObject(proc, tag + i);
        }
        return o;
    }

    /**
     * Метод посылки объекта
     *
     * @param a    - посылаемый объект
     * @param proc - номер процессора которому посылаем объект
     * @param tag  - идентификатор сообщения
     * @throws MPIException
     * @throws IOException
     */
    public static void sendObject(Object a, int proc, int tag)
            throws MPIException, IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(a);
        byte[] tmp = bos.toByteArray();
        MPI.COMM_WORLD.send(tmp, tmp.length, MPI.BYTE, proc, tag);
    }

    /**
     * Метод приемки объекта
     *
     * @param proc - номер процессора от которого получаем объект
     * @param tag  - идентификатор сообщения
     * @return - полученный объект
     * @throws MPIException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object recvObject(int proc, int tag)
            throws MPIException, IOException,
            ClassNotFoundException {

        // команда считывает статус буфера для приема сообщения от процессора proc с тегом tag
        Status st = MPI.COMM_WORLD.probe(proc, tag);
        // статический метод класса Status, который подсчитывает количество элементов в буфере (в данном случае MPI.BYTE)
        int size = st.getCount(MPI.BYTE);
        // создаем байт-массив
        byte[] tmp = new byte[size];
        // recv - блокирующий прием массива из буфера ввода в массив tmp
        MPI.COMM_WORLD.recv(tmp, size, MPI.BYTE, proc, tag);
        Object res = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(tmp);
        ObjectInputStream ois = new ObjectInputStream(bis);
        res = ois.readObject();
        // передаем на выход процедуры полученный объект
        return res;
    }

    /**
     * @param a    - массив объектов
     * @param proc - номер процессора которому посылаем
     *             массив объектов
     * @param tag  - идентификатор сообщения
     * @throws MPIException
     */
    public static void sendObjects(Object[] a, int proc, int tag)
            throws MPIException {
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            ObjectOutputStream oos =
                    new ObjectOutputStream(bos);
            for (int i = 0; i < a.length; i++) {
                oos.writeObject(a[i]);
            }
            bos.toByteArray();
        } catch (Exception ex) {
            Logger.getLogger(Transport.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        byte[] temp = bos.toByteArray();
        ByteBuffer buf = MPI.newByteBuffer(temp.length);
        buf.put(temp);
        MPI.COMM_WORLD.iSend(buf, temp.length, MPI.BYTE, proc, tag);
    }

    /**
     * @param m    - количество элементов принимаемого массива
     * @param proc - номер процессора от которого
     *             принимаем сообщение
     * @param tag  - идентификатор сообщения
     * @return - полученный массив объектов
     * @throws MPIException
     */
    public static Object[] recvObjects(int m, int proc, int tag)
            throws MPIException {
        Status s = MPI.COMM_WORLD.probe(proc, tag);
        int n = s.getCount(MPI.BYTE);
        byte[] arr = new byte[n];
        MPI.COMM_WORLD.recv(arr, n, MPI.BYTE, proc, tag);
        Object[] res = new Object[m];
        try {
            ByteArrayInputStream bis =
                    new ByteArrayInputStream(arr);
            ObjectInputStream ois = new ObjectInputStream(bis);
            // System.out.println("processor number: " + MPI.COMM_WORLD.getRank() + ", length: " + arr.length);
            for (int i = 0; i < m; i++) {
                // System.out.println("processor number: " + MPI.COMM_WORLD.getRank() + ", index: " + i);
                res[i] = (Object) ois.readObject();
            }
        } catch (Exception ex) {
            Logger.getLogger(Transport.class.getName()).
                    log(Level.SEVERE, null, ex);

        }
        return res;
    }
}
