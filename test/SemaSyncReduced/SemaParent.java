package Semaphores;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SemaParent extends Thread implements Runnable{
    int speed, max;
    //AccountBuffer account;
    String threadName = "";
    private static Random generator = new Random();
    private int accountBalance = 0;
    private boolean occupied = false;
    private Lock accessLock = new ReentrantLock();

    private Condition canRead = accessLock.newCondition();
    private Condition canWrite = accessLock.newCondition();

    public SemaParent(String threadName, int speed, int max) {
        this.speed = speed;
        this.max = max;
        //this.account = account;
        this.threadName = threadName;
    }
    public void deposit(int value, String threadName, int speed, int max) {
        int readValue = 0;
        accessLock.lock();
        //if((value <= max))
            accountBalance += value;
        System.out.printf("%-40s%-40s%-40s\n", "Parent " + threadName + " deposits $" + value, "" , "Balance: $" + accountBalance);
        canWrite.signal();
        accessLock.unlock();
    }
    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(speed);
                deposit(1 + generator.nextInt(max), threadName, speed, max);
            } 
            catch(InterruptedException ex) {
                //ex.printStackTrace();
                Logger.getLogger(SemaParent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

