package Semaphore;

import java.util.concurrent.*;

public class SemaphoreBowl implements Bowl {
    private int accountBalance = 0;
    private boolean occupied = false;
    static Semaphore childThread = new Semaphore(0);
    static Semaphore parentThread = new Semaphore(1);
    static Semaphore mutex = new Semaphore(1);

    public void withdraw(int value, String threadName) {
        try {
            //mutex.acquire();
            childThread.acquire();
            if(accountBalance - value < 0) {
                printState("", "Child " + threadName + " withdraws $" + value, "Withdraw Blocked: Insufficient Funds");
                parentThread.release();
                return;
            }
            accountBalance -= value;
            printState("", "Child " + threadName + " withdraws $" + value, "Balance: $" + accountBalance);
            parentThread.release();
            //mutex.release();
        } 
        catch(InterruptedException e) {
            System.out.println("InterruptedException caught");
        }
        //mutex.release();
    }

    public void deposit(int value, String threadName) {
        try {
            //mutex.acquire();
            parentThread.acquire();
            
            accountBalance += value;
            printState("Parent " + threadName + " deposits $" + value, "" , "Balance: $" + accountBalance);
            childThread.release();
            //mutex.release();
        } catch(InterruptedException e) {
            System.out.println("InterruptedException caught");
        }
        //mutex.release();
    }

    public void printState(String depositString, String withdrawString, String balance)
    {
        System.out.printf("%-30s%-30s%-30s\n", depositString, withdrawString, balance);
    }
}
