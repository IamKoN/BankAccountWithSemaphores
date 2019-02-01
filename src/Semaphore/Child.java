package Semaphore;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Child extends Thread implements Runnable {
    int speed, max;
    Bowl account;
    String threadName = "";
    private static Random generator = new Random();

    public Child(Bowl account, String threadName, int speed, int max) {
        this.speed = speed;
        this.max = max;
        this.account = account;
        this.threadName = threadName;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(speed);
                account.withdraw(1 + generator.nextInt(max), threadName);
            }
            catch(InterruptedException ex) {
                //ex.printStackTrace();
                Logger.getLogger(Child.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
