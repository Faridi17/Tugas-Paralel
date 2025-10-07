import java.util.Arrays;
import java.util.Random;

public class MatrixMultiplication {

    static int rowsA = 1000;
    static int colsA = 300;
    static int rowsB = colsA;
    static int colsB = rowsA;

    static int MAX_THREAD = 4;

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
        MatrixMultiplicationThread[] threads = new MatrixMultiplicationThread[MAX_THREAD];

        startMillis = System.currentTimeMillis();
        startNano = System.nanoTime();

        for (int i = 0; i < MAX_THREAD; i++) {
            threads[i] = new MatrixMultiplicationThread(i);
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

        System.out.println("Arrays equal: " + Arrays.deepEquals(Result1, Result2));
    }

    static class MatrixMultiplicationThread extends Thread {
        private final int myPart;

        public MatrixMultiplicationThread(int part) {
            this.myPart = part;
        }

        @Override
        public void run() {
            int rowsPerThread = rowsA / MAX_THREAD;
            int start = myPart * rowsPerThread;
            int end = (myPart == MAX_THREAD - 1) ? rowsA : start + rowsPerThread;

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
