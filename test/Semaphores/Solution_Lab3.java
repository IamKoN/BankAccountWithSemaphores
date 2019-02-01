/*
family consists of two parents and N children.  They all share the same bank account.
The parents make a random amount of money and depositing that money into the bank account.
children take a random amount of money from the account and using it to buy ice cream.
There is no maximum limit to the amount of money that can be put into the account,
however the account balance can never go below zero.

1)Write a Java solution to the above program using a semaphore implementation for synchronization
and mutual exclusion.  Each parent should be a thread.  Each of the N children should be a thread.
2)Write a Java solution using a synchronized class representing the bowl.

features such as a GUI which would allow for a varying number of children, speed up, slow down,
make more money, eat more ice cream, etc.
*/
package Semaphores;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;


public class Solution_Lab3 {
    
    static int children = 0;
    static Semaphore mutex = new Semaphore(1);
    static Semaphore multiplex = new Semaphore(50);
    static Semaphore child = new Semaphore(0);
    static Semaphore parent1 = new Semaphore(0);
    static Semaphore parent2 = new Semaphore(0);
    static Semaphore allAboard = new Semaphore(0);
    
    static int accountBalance = 0;
    static boolean occupied = false;
    static Lock accessLock = new ReentrantLock();
    static Condition canRead = accessLock.newCondition();
    static Condition canWrite = accessLock.newCondition();
    
    public static void printState(String depositString, String withdrawString, String balance) {
        System.out.printf("%-40s%-40s%-40s\n", depositString, withdrawString, balance);
    }
    static class Parent extends Thread implements Runnable{
        int speed, max;
        String parentName = "";
        private static Random generator = new Random();
        public void deposit(int value, String parentName, int speed, int max) {
            int readValue = 0;
            accessLock.lock();
            //if((value <= max))
                accountBalance += value;
            printState("Parent " + parentName + " deposits $" + value, "" , "Balance: $" + accountBalance);
            canWrite.signal();
            accessLock.unlock();
        }    
        public Parent(String parentName, int speed, int max) {
            this.speed = speed;
            this.max = max;
            this.parentName = parentName;
        }
        
        public void run() {
            while(true) {
                try {
                    //Thread.sleep(speed);
                    //deposit(1 + generator.nextInt(max), parentName, speed, max);
                    
                    //bus gets the mutex preventing the later arrived children
                    mutex.acquire();
                    System.out.println(parentName + " : acquired mutex");

                    //check whether there are waiting children
                    if (children > 0) {
                        //allowing children to be boarded
                        parent1.release();
                        System.out.println(parentName + " : released bus");

                        //waiting for the children to be boarded
                        allAboard.acquire();
                        System.out.println(parentName + " : acquired allAboard");

                    }

                    System.out.println(parentName + " : releasing mutex...");
                    //releasing the mutex after boarding the children
                    mutex.release();
                    System.out.println(parentName + " : released");

                    System.out.println(parentName + " : departed");

                } catch (InterruptedException ex) {
                    Logger.getLogger(Solution_Lab3.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    static class Child extends Thread{
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
        String childName = "";
        
        Child(String name) {
            this.childName = name;
        }
        
        public void run() {
            try {
                
                System.out.println(childName + ": acquiring multiplex...");
                //check whether the capacity is satisfied
                multiplex.acquire();
                System.out.println(childName + ": acquired the multiplex");
                
                System.out.println(childName + ": acquiring mutex...");
                //check whether a parent1 has already arrived
                mutex.acquire();
                System.out.println(childName + ": acquired the mutex");
                
                //number of children are get increased after acquiring the mutex
                children++;
                
                System.out.println(childName + ": releasing mutex...");
                mutex.release();
                System.out.println(childName + ": released mutex");
                
                System.out.println(childName + ": acquiring bus...");
                //waiting till a parent1 comes
                parent1.acquire();
                System.out.println(childName + ": acquired bus");
                
                //giving the chance to another rider after acquiring the parent1
                multiplex.release();
                
                System.out.println(childName + ": boarded");
                
                //decreasing the number of waiting children after get boarded
                children--;
                
                if (children == 0) {
                    //releasing allAboard mutex when all are boarded.
                    System.out.println("All the riders are boarded");
                    allAboard.release();
                } else {
                    System.out.println(childName + ": releasing bus...");
                    //give chance to another rider to get boarded
                    parent1.release();
                    System.out.println(childName + ": released bus");
                }                
                
            } catch (InterruptedException ex) {
                Logger.getLogger(Solution_Lab3.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    public static void main(String[] args) {
        ///////////////////////////////////////////////////////////////
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter number of children: ");
        int c = sc.nextInt();
        System.out.println("Enter speed: ");
        int speed = sc.nextInt();
        System.out.println("Enter maximum parent income: ");
        int max = sc.nextInt();
        
        String threadname = "";
        
        ExecutorService app = Executors.newFixedThreadPool(2+c);
        try {
            app.execute(new Parent("1", speed, max));
            app.execute(new Parent("2", speed, max));
            for (int i = 1; i <= c; i++) {
                threadname = Integer.toString(i);
                app.execute(new Child(threadname));
            }
        }
        catch(Exception ex) {ex.printStackTrace();}
        app.shutdown();
        //////////////////////////////////////////////////////////////
        //creating a therad which generates children
        Thread riders_creator = new Thread(new Runnable() {
            Random randomGenerator = new Random();
            int numChildren = 0;
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(30 * 1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Solution_Lab3.class.getName()).log(Level.SEVERE, null, ex);
                    }
                
                    //creating children at random time intervals
                    new Child("Children-" + numChildren).start();
                    numChildren++;
                }
            }
        });
        riders_creator.start();
        
        //starting buses periodically
        int parentID = 0;
        while (true) {
            try {
                Thread.sleep(speed);
            } catch (InterruptedException ex) {
                Logger.getLogger(Solution_Lab3.class.getName()).log(Level.SEVERE, null, ex);
            }
            new Parent("Parent-" + parentID, speed, max).start();
            parentID++;
        }
    }
    
}
