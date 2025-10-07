import java.util.Arrays;
import java.util.Random;

public class MatrixAddition {

    static int rows = 200_000;
    static int cols = 1_000;
    static int MAX_THREAD = 4;

    static short[][] A = new short[rows][cols];
    static short[][] B = new short[rows][cols];

    static short[][] sum1 = new short[rows][cols];
    static short[][] sum2 = new short[rows][cols];

    public static void main(String[] args) throws InterruptedException {

        Random rand = new Random();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                A[i][j] = (short) rand.nextInt(10000);
                B[i][j] = (short) rand.nextInt(10000);
            }
        }

//      Sequential
//
        long startMillis = System.currentTimeMillis();
        long startNano = System.nanoTime();


        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sum1[i][j] = (short) (A[i][j] + B[i][j]);
            }
        }

        long endMillis = System.currentTimeMillis();
        long endNano = System.nanoTime();

        System.out.println("Sequential Millis: " + (endMillis - startMillis) + "ms");
        System.out.println("Sequential Nano: " + (endNano - startNano) +"ns");


//
//

//      Parallel
//

        MatrixAdditionThread[] threads = new MatrixAdditionThread[MAX_THREAD];

        startMillis = System.currentTimeMillis();
        startNano = System.nanoTime();

        for (int i = 0; i < MAX_THREAD; i++) {
            threads[i] = new MatrixAdditionThread(i);
            threads[i].start();
        }

        for (int i = 0; i < MAX_THREAD; i++) {
            threads[i].join();
        }


        endMillis = System.currentTimeMillis();
        endNano = System.nanoTime();

        System.out.println("Parallel Millis: " + (endMillis - startMillis) + "ms");
        System.out.println("Parallel Nano: " + (endNano - startNano) +"ns");

//
//
        System.out.println("Arrays equal: " + Arrays.deepEquals(sum1, sum2));
    }

    static class MatrixAdditionThread extends Thread {
        private final int myPart;

        public MatrixAdditionThread(int inputPart) {
            myPart = inputPart;
        }

        @Override
        public void run() {
            int rowsPerThread = rows / MAX_THREAD;
            int start = myPart * rowsPerThread;
            int end = (myPart == MAX_THREAD -1) ? rows : start + rowsPerThread;

            for (int i = start; i < end; i++) {
                for (int j = 0; j < cols; j++) {
                    sum2[i][j] = (short) (A[i][j] + B[i][j]);
                }
            }
        }
    }

}