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

import Semaphores.SemaParent;
import Semaphores.SemaChild;
import Synchronization.AccountBuffer;
import Synchronization.SynchronizedAccountBuffer;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;


public class SemaMain {
    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter number of children: ");
        int c = sc.nextInt();
        System.out.println("Enter speed: ");
        int speed = sc.nextInt();
        System.out.println("Enter maximum parent income: ");
        int max = sc.nextInt();
        
        String threadname = "";
        AccountBuffer account = new SynchronizedAccountBuffer();
        ExecutorService app = Executors.newFixedThreadPool(2+c);
        try {
            app.execute(new SemaParent(account ,"1", speed, max));
            app.execute(new SemaParent(account ,"2", speed, max));
            for (int i = 1; i <= c; i++) {
                threadname = Integer.toString(i);
                app.execute(new SemaChild(account, threadname));
            }
        }
        catch(Exception ex) {ex.printStackTrace();}
        app.shutdown();
    }
}