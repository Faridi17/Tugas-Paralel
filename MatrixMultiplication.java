import java.util.Arrays;
import java.util.Random;

public class MatrixMultiplication {

    static int rowsA = 1000;
    static int colsA = 300;
    static int rowsB = colsA;
    static int colsB = rowsA;

    static int[][] A = new int[rowsA][colsA];
    static int[][] B = new int[rowsB][colsB];
    static int[][] Result1 = new int[rowsA][colsB];
    static int[][] Result2 = new int[rowsA][colsB];

    public static void main(String[] args) throws InterruptedException {
        Random rand = new Random();

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsA; j++) {
                A[i][j] = rand.nextInt(rowsA * colsA);;
            }
        }

        for (int i = 0; i < rowsB; i++) {
            for (int j = 0; j < colsB; j++) {
                B[i][j] = rand.nextInt(rowsB * colsB);;
            }
        }

//      Sequential
//
        long startMillis = System.currentTimeMillis();
        long startNano = System.nanoTime();

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                int sum = 0;
                for (int k = 0; k < colsA; k++) {
                    sum += A[i][k] * B[k][j];
                }
                Result1[i][j] = sum;
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
        MatrixMultiplicationThread t0 = new MatrixMultiplicationThread(0);
        MatrixMultiplicationThread t1 = new MatrixMultiplicationThread(1);
        MatrixMultiplicationThread t2 = new MatrixMultiplicationThread(2);
        MatrixMultiplicationThread t3 = new MatrixMultiplicationThread(3);

        startMillis = System.currentTimeMillis();
        startNano = System.nanoTime();

        t0.start();
        t1.start();
        t2.start();
        t3.start();

        t0.join();
        t1.join();
        t2.join();
        t3.join();

        endMillis = System.currentTimeMillis();
        endNano = System.nanoTime();
        System.out.println("Parallel Millis: " + (endMillis - startMillis) + "ms");
        System.out.println("Parallel Nano: " + (endNano - startNano) +"ns");
//
//

        System.out.println("Arrays equal: " + Arrays.deepEquals(Result1, Result2));
    }

    static class MatrixMultiplicationThread extends Thread {
        private final int myPart;

        public MatrixMultiplicationThread(int part) {
            this.myPart = part;
        }

        @Override
        public void run() {
            int rowsPerThread = rowsA / 4;
            int start = myPart * rowsPerThread;
            int end = (myPart == 3) ? rowsA : start + rowsPerThread;

            for (int i = start; i < end; i++) {
                for (int j = 0; j < colsB; j++) {
                    int sum = 0;
                    for (int k = 0; k < colsA; k++) {
                        sum += A[i][k] * B[k][j];
                    }
                    Result2[i][j] = sum;
                }
            }
        }
    }
}
