
public class BinarySearch {

    static int length = 1_000_000_000;
    static int[] data = new int[length];
    static int search = 4;
    static int index2 = -1;

    static int MAX_THREAD = 4;


    public static void main(String[] args) throws InterruptedException {

        for (int i = 0; i < length; i++) {
            data[i] = i * 2;
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


        BinarySearchThread[] threads = new BinarySearchThread[MAX_THREAD];

        startMillis = System.currentTimeMillis();
        startNano = System.nanoTime();

        for (int i = 0; i < MAX_THREAD; i++) {
            threads[i] = new BinarySearchThread(i);
            threads[i].start();
        }

        for (int i = 0; i < MAX_THREAD; i++) {
            threads[i].join();
        }

        endMillis = System.currentTimeMillis();
        endNano = System.nanoTime();

        System.out.println("Parallel: " + (endMillis - startMillis) + "ms");
        System.out.println("Parallel: " + (endNano - startNano) +"ns");

        

        if (index2 == -1) {
            System.out.println("Element is not present in array");
        } else {
            System.out.println("Element is present at index: " + index2);
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
            int rowsPerThread = length / MAX_THREAD;
            int left = myPart * rowsPerThread;
            int right = (myPart == MAX_THREAD - 1) ? length : left + rowsPerThread;

            while (left <= right) {
                int m = (left + right) / 2;

                if (data[m] == search) {
                    index2 = m;
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
