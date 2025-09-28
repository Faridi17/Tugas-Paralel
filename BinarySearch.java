import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class BinarySearch {

    static int length = 100_000_000;
    static int[] data = new int[length];
    static int search = 9_999_000;
    static int[] result = {-1,-1,-1,-1};


    public static void main(String[] args) throws InterruptedException {

        for (int i = 0; i < length; i++) {
            data[i] = i;
        }


        long startMillis = System.currentTimeMillis();
        long startNano = System.nanoTime();

        int index1 = binarySearch();

        long endMillis = System.currentTimeMillis();
        long endNano = System.nanoTime();

        System.out.println("Sequential Millis: " + (endMillis - startMillis) + "ms");
        System.out.println("Sequential Nano: " + (endNano - startNano) +"ns");

        if (index1 == -1)
            System.out.println("Element is not present in array");
        else
            System.out.println("Element is present at index: " + index1);


        BinarySearchThread t0 = new BinarySearchThread(0);
        BinarySearchThread t1 = new BinarySearchThread(1);
        BinarySearchThread t2 = new BinarySearchThread(2);
        BinarySearchThread t3 = new BinarySearchThread(3);

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

        System.out.println("Parallel: " + (endMillis - startMillis) + "ms");
        System.out.println("Parallel: " + (endNano - startNano) +"ns");

        int Index2 = -1;
        for (int i : result) {
            if (i != -1) {
                Index2 = i;
                break;
            }
        }

        if (Index2 == -1) {
            System.out.println("Element is not present in array");
        } else {
            System.out.println("Element is present at index: " + Index2);
        }

    }

    static int binarySearch()
    {
        int left = 0;
        int right = length - 1;
        while (left <= right) {
            int m = (left + right) / 2;

            if (data[m] == search) {
                return m;
            } else if (data[m] > search) {
                right = m - 1;

            } else {
                left = m + 1;
            }
        }

        return -1;
    }

    static class BinarySearchThread extends Thread {
        private final int myPart;

        public BinarySearchThread(int part) {
            this.myPart = part;
        }

        @Override
        public void run() {
            int rowsPerThread = length / 4;
            int left = myPart * rowsPerThread;
            int right = (myPart == 3) ? length : left + rowsPerThread;

            while (left <= right) {
                int m = (left + right) / 2;

                if (data[m] == search) {
                    result[myPart] = m;
                    break;
                } else if (data[m] > search) {
                    right = m - 1;

                } else {
                    left = m + 1;
                }
            }
        }

    }
}
