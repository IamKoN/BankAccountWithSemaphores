package Semaphores;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SemaChild extends Thread implements Runnable {

    String threadName;
    private static Random generator = new Random();
    private int accountBalance = 0;
    private boolean occupied = false;
    private Lock accessLock = new ReentrantLock();

    private Condition canRead = accessLock.newCondition();
    private Condition canWrite = accessLock.newCondition();

    public SemaChild(String threadName) {
        this.threadName = threadName;
    }
    public void withdraw(int value, String threadName) {
        accessLock.lock();
        try {
            while (accountBalance - value < 0) {
                System.out.printf("%-40s%-40s%-40s\n","", "Child " + threadName + " withdraws $" + value, "Withdraw Blocked: Insufficient Funds");
                canWrite.await();
            }
            accountBalance -= value;
            System.out.printf("%-40s%-40s%-40s\n","", "Child " + threadName + " withdraws $" + value, "Balance: $" + accountBalance);
            canRead.signal();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally
        {
            accessLock.unlock();
        }
    }
    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(generator.nextInt(5));
                withdraw(1 + generator.nextInt(49), threadName);
            } catch(InterruptedException ex) {
                //ex.printStackTrace();
                Logger.getLogger(SemaChild.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
