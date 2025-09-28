import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CyclicBarrier;

class BubbleSort {
    static int length = 20_000;
    static int[] data1 = new int[length];
    static int[] data2 = new int[length];

    public static void main(String args[]) throws InterruptedException {
        Random rand = new Random();

        for (int i = 0; i < length; i++) {
            int val = rand.nextInt(length);
            data1[i] = val;
            data2[i] = val;
        }

//        Sequential
//
        long startMillis = System.currentTimeMillis();
        long startNano = System.nanoTime();

        bubbleSortSequential();

        long endMillis = System.currentTimeMillis();
        long endNano = System.nanoTime();

        System.out.println("Sequential Millis: " + (endMillis - startMillis) + "ms");
        System.out.println("Sequential Nano: " + (endNano - startNano) +"ns");

//
//

//        Parallel
//
        int numThreads = 4;
        CyclicBarrier barrier = new CyclicBarrier(numThreads);

        OddEvenThread t0 = new OddEvenThread(0, barrier);
        OddEvenThread t1 = new OddEvenThread(1, barrier);
        OddEvenThread t2 = new OddEvenThread(2, barrier);
        OddEvenThread t3 = new OddEvenThread(3, barrier);

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

        System.out.println("Arrays equal: " + Arrays.equals(data1, data2));
    }

    static void bubbleSortSequential() {
        boolean swap;
        for (int i = 0; i < length - 1; i++) {
            swap = false;
            for (int j = 0; j < length - i - 1; j++)
                if (data1[j] > data1[j + 1]) {
                    int temp = data1[j];
                    data1[j] = data1[j + 1];
                    data1[j + 1] = temp;
                    swap = true;
                }
            if (!swap) break;
        }
    }

    static class OddEvenThread extends Thread {
        private final int myPart;
        private final CyclicBarrier barrier;
        int start;
        int end = length - 1;

        public OddEvenThread(int part, CyclicBarrier barrier) {
            this.myPart = part;
            this.barrier = barrier;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < length; i++) {

                    if (i % 2 == 0) {
                        start = myPart * 2;
                        for (int j = start; j < end; j += 8) {
                            if (data2[j] > data2[j + 1]) {
                                int temp = data2[j];
                                data2[j] = data2[j + 1];
                                data2[j + 1] = temp;
                            }
                        }
                    }
                    else {
                        start = 1 + myPart * 2;
                        for (int j = start; j < end; j += 8) {
                            if (data2[j] > data2[j + 1]) {
                                int temp = data2[j];
                                data2[j] = data2[j + 1];
                                data2[j + 1] = temp;
                            }
                        }
                    }

                    barrier.await();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
