import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

class BubbleSort {
    static int length = 50_000;
    static int[] data1 = new int[length];
    static int[] data2 = new int[length];
    static int[] data3 = new int[length];

    public static void main(String[] args) throws InterruptedException {
        Random rand = new Random();

        for (int i = 0; i < length; i++) {
            int val = rand.nextInt(length);
            data1[i] = val;
            data2[i] = val;
            data3[i] = val;
        }

//        Sequential Bubble Sort
//
        long startMillis = System.currentTimeMillis();
        long startNano = System.nanoTime();

//        bubbleSort();
        Arrays.parallelSort(data1);

        long endMillis = System.currentTimeMillis();
        long endNano = System.nanoTime();

        System.out.println("Sequential Bubble Sort Millis: " + (endMillis - startMillis) + "ms");
        System.out.println("Sequential Bubble Sort Nano: " + (endNano - startNano) +"ns");

//
//

//        Parallel Odd Even Transposition Sort
//
        int numThreads = 4;
        CyclicBarrier barrier1 = new CyclicBarrier(numThreads);

        OddEvenTranspositionSort toets0 = new OddEvenTranspositionSort(0, barrier1);
        OddEvenTranspositionSort toets1 = new OddEvenTranspositionSort(1, barrier1);
        OddEvenTranspositionSort toets2 = new OddEvenTranspositionSort(2, barrier1);
        OddEvenTranspositionSort toets3 = new OddEvenTranspositionSort(3, barrier1);

        startMillis = System.currentTimeMillis();
        startNano = System.nanoTime();

        toets0.start();
        toets1.start();
        toets2.start();
        toets3.start();

        toets0.join();
        toets1.join();
        toets2.join();
        toets3.join();

        endMillis = System.currentTimeMillis();
        endNano = System.nanoTime();

        System.out.println("Parallel OETS Millis: " + (endMillis - startMillis) + "ms");
        System.out.println("Parallel OETS Nano: " + (endNano - startNano) +"ns");

//
//

//        Parallel Bubble-Merge Sort
//
        CyclicBarrier barrier2 = new CyclicBarrier(numThreads);

        BubbleMergeSort tbms0 = new BubbleMergeSort(0, barrier2);
        BubbleMergeSort tbms1 = new BubbleMergeSort(1, barrier2);
        BubbleMergeSort tbms2 = new BubbleMergeSort(2, barrier2);
        BubbleMergeSort tbms3 = new BubbleMergeSort(3, barrier2);

        startMillis = System.currentTimeMillis();
        startNano = System.nanoTime();

        tbms0.start();
        tbms1.start();
        tbms2.start();
        tbms3.start();

        tbms0.join();
        tbms1.join();
        tbms2.join();
        tbms3.join();

        endMillis = System.currentTimeMillis();
        endNano = System.nanoTime();

        System.out.println("Parallel Bubble-Merge Sort Millis: " + (endMillis - startMillis) + "ms");
        System.out.println("Parallel Bubble-Merge Sort Nano: " + (endNano - startNano) +"ns");

//
//

        boolean equal = Arrays.equals(data1, data2) && Arrays.equals(data1, data3);
        System.out.println("Arrays equal: " + equal);

    }

    static void bubbleSort() {
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

    static class OddEvenTranspositionSort extends Thread {
        private final int myPart;
        private final CyclicBarrier barrier;
        int start;
        int end = length - 1;

        public OddEvenTranspositionSort(int part, CyclicBarrier barrier) {
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
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }

    }

    static class BubbleMergeSort extends Thread {
        private final int myPart;
        private final CyclicBarrier barrier;

        public BubbleMergeSort(int part, CyclicBarrier barrier) {
            this.myPart = part;
            this.barrier = barrier;
        }

        @Override
        public void run() {
            int rowsPerThread = length / 4;
            int start = myPart * rowsPerThread;
            int end = (myPart == 3) ? length : start + rowsPerThread;

            try {
            for (int i = start; i < end - 1; i++) {
                for (int j = start; j < end - 1; j++)
                    if (data3[j] > data3[j + 1]) {
                        int temp = data3[j];
                        data3[j] = data3[j + 1];
                        data3[j + 1] = temp;
                    }
            }
            barrier.await();
            if (myPart == 1) {
                merge(0, end);
            } else if (myPart == 3) {
                merge(rowsPerThread * 2, length);
            }

            barrier.await();

            if (myPart == 0) {
                merge(0, length);
            }


            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }

        }

    }

    private static void merge(int start, int end) {
        int mid = (start + end) / 2;
        int[] temp = new int[end - start];
        int length = temp.length;
        int i = start, j = mid, k = 0;

        while (i < mid && j < end) {
            if (data3[i] <= data3[j]) temp[k++] = data3[i++];
            else temp[k++] = data3[j++];
        }
        while (i < mid) temp[k++] = data3[i++];
        while (j < end) temp[k++] = data3[j++];

        for (k = 0; k < length; k++) data3[start + k] = temp[k];
    }
}
