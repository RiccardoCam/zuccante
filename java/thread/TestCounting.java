// import java.util.Date;
import java.util.Random;
// import java.util.concurrent.TimeUnit;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

/*
 * ##### Synchronizing tasks in a common point ######
 * 
 * A synchronization aid that allows a set of threads to all wait for each other to reach a common barrier point. 
 * CyclicBarriers are useful in programs involving a fixed sized party of threads that must occasionally wait for each other. 
 * The barrier is called cyclic because it can be re-used after the waiting threads are released.
 * 
 */

public class TestCounting {
    public static void main(String[] args) {
        
        final int ROWS = 10000;
        final int NUMBERS = 1000;
        final int SEARCH = 5;
        final int PARTICIPANTS = 5; // the number of searcher
        final int LINES_PARTICIPANT = 2000; // == ROWS/PARTICIPANTS
        
        MatrixMock mock = new MatrixMock(ROWS, NUMBERS, SEARCH);
        Results results = new Results(ROWS);
        Grouper grouper = new Grouper(results); // ... and we can obtain the result
        
        /*
         * CyclicBarrier(int parties, Runnable barrierAction) 
         * Creates a new CyclicBarrier that will trip when the given number of parties (threads) are waiting upon it,
         * and which will execute the given barrier action when the barrier is tripped,
         * performed by the last thread entering the barrier. 
         * 
         */
        
        CyclicBarrier barrier = new CyclicBarrier(PARTICIPANTS, grouper);
        Searcher searchers[] = new Searcher[PARTICIPANTS];
        
        for (int i = 0; i < PARTICIPANTS; i++){
            searchers[i] = new Searcher(i*LINES_PARTICIPANT, (i*LINES_PARTICIPANT)+LINES_PARTICIPANT, mock, results, 5, barrier);
            Thread thread = new Thread(searchers[i]);
            thread.start();
        }
        System.out.printf("Main: The main thread has finished.\n");
    }
}

class MatrixMock {
    
    private int data[][];
    
    public MatrixMock(int size, int length, int number){
        int counter = 0;
        data = new int[size][length];
        Random random = new Random();
        for (int i=0; i<size; i++) {
            for (int j=0; j<length; j++){
                data[i][j] = random.nextInt(10);
                if (data[i][j] == number){
                    counter++;
                }
            }
        }
        // in case put a comment here below
        System.out.printf("Mock: There are %d ocurrences of number in generated data.\n",
          counter, number);
    }
    
    public int[] getRow(int row){
        if ((row >= 0)&&(row < data.length)){
            return data[row];
        }
        return null;
    }
}
    
class Results {
    
    private int data[];
    
    public Results(int size){
        data = new int[size];
    }
    
    public void setData(int position, int value){
        data[position] = value;
    }
    
    public int[] getData(){
        return data;
    }
}

class Searcher implements Runnable {
    
    private int firstRow;
    private int lastRow;
    private MatrixMock mock;
    private Results results;
    private int number;
    
    private final CyclicBarrier barrier;
    
    public Searcher(int firstRow, int lastRow, MatrixMock mock, 
      Results results, int number, CyclicBarrier barrier){
        this.firstRow = firstRow;
        this.lastRow = lastRow;
        this.mock = mock;
        this.results = results;
        this.number = number;
        this.barrier = barrier;
    }
    
    @Override
    public void run() {
        int counter;
        System.out.printf("%s: Processing lines from %d to %d.\n",
          Thread.currentThread().getName(), firstRow, lastRow);
        for (int i = firstRow; i < lastRow; i++){
            int row[] = mock.getRow(i);
            counter = 0;
            for (int j = 0; j < row.length; j++){
                if (row[j] == number){
                    counter++;
                }
            }
            results.setData(i, counter);
        }
        System.out.printf("%s: Lines processed.\n",
          Thread.currentThread().getName());
        try {
            /*
             * 
             * public int await() 
             * throws InterruptedException, BrokenBarrierException
             * 
             * Waits until all parties have invoked await on this barrier.
             * 
             */
            
            barrier.await();
            System.out.printf("### %s, OK ###\n", Thread.currentThread().getName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}

class Grouper implements Runnable {
    
    private Results results;
    
    public Grouper(Results results){
        this.results = results;
    }
    
    @Override
    public void run() {
        int finalResult = 0;
        System.out.printf("Grouper: Processing results...\n");
        int data[] = results.getData();
        for (int number:data){
            finalResult += number;
        }
        System.out.printf("Grouper: Total result: %d.\n",finalResult);
    }
}
