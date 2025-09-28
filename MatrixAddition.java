import java.util.Arrays;
import java.util.Random;

public class MatrixAddition {

    static int rows = 13;
    static int cols = 31;

    static int[][] A = new int[rows][cols];
    static int[][] B = new int[rows][cols];

    static int[][] sum1 = new int[rows][cols];
    static int[][] sum2 = new int[rows][cols];

    public static void main(String[] args) throws InterruptedException {

        Random rand = new Random();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                A[i][j] = rand.nextInt(rows * cols);
                B[i][j] = rand.nextInt(rows * cols);
            }
        }

//      Sequential
//
        long startMillis = System.currentTimeMillis();
        long startNano = System.nanoTime();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sum1[i][j] = A[i][j] + B[i][j];
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
        MatrixAdditionThread t0 = new MatrixAdditionThread(0);
        MatrixAdditionThread t1 = new MatrixAdditionThread(1);
        MatrixAdditionThread t2 = new MatrixAdditionThread(2);
        MatrixAdditionThread t3 = new MatrixAdditionThread(3);

        endMillis = System.currentTimeMillis();
        endNano = System.nanoTime();

        t0.start();
        t1.start();
        t2.start();
        t3.start();


        t0.join();
        t1.join();
        t2.join();
        t3.join();

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
            int rowsPerThread = rows / 4;
            int start = myPart * rowsPerThread;
            int end = (myPart == 3) ? rows : start + rowsPerThread;

            for (int i = start; i < end; i++) {
                for (int j = 0; j < cols; j++) {
                    sum2[i][j] = A[i][j] + B[i][j];
                }
            }
        }
    }

}