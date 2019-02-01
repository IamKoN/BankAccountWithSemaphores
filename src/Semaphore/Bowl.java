package Semaphore;
public interface Bowl {
    public void withdraw(int value, String threadName);
    public void deposit(int value, String threadName);
}
