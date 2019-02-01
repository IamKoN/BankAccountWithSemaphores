package Semaphore;

import java.util.concurrent.locks.*;

public class SynchronizedBowl implements Bowl {
    private int accountBalance = 0;
    private boolean occupied = false;
    private Lock accessLock = new ReentrantLock();
    private Condition canRead = accessLock.newCondition();
    private Condition canWrite = accessLock.newCondition();

    @Override
    public void withdraw(int value, String threadName) {
        accessLock.lock();
        try {
            while (accountBalance - value < 0) {
                printState("", "Child " + threadName + " withdraws $" + value, "Withdraw Blocked: Insufficient Funds");
                canWrite.await();
            }
            accountBalance -= value;
            printState("", "Child " + threadName + " withdraws $" + value, "Balance: $" + accountBalance);
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
    public void deposit(int value, String threadName) {
        accessLock.lock();
        accountBalance += value;
        printState("Parent " + threadName + " deposits $" + value, "" , "Balance: $" + accountBalance);
        canWrite.signal();
        accessLock.unlock();
    }

    public void printState(String depositString, String withdrawString, String balance)
    {
        System.out.printf("%-30s%-30s%-30s\n", depositString, withdrawString, balance);
    }
}
