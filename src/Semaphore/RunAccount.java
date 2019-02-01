package Semaphore;

import java.util.concurrent.*;
import java.util.Scanner;

public class RunAccount {
    public static void main(String[] args)
    {
       
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter '0' for semphore OR '1' for synchronized class: ");
        int sync = sc.nextInt();
        System.out.println("Enter number of children: ");
        int c = sc.nextInt();
         System.out.println("Enter withdrawl speed: ");
        int wSpeed = sc.nextInt();
        System.out.println("Enter maximum withdrawl: ");
        int wMax = sc.nextInt();
        System.out.println("Enter deposit speed: ");
        int dSpeed = sc.nextInt();
        System.out.println("Enter maximum deposit: ");
        int dMax = sc.nextInt();
    
        String threadname = "";
        Bowl account;
        ExecutorService app = Executors.newFixedThreadPool(2+c);
        if (sync == 1) {
            account = new SynchronizedBowl();
        } else {
            account = new SemaphoreBowl();
        } 
        try {
            app.execute(new Parent(account ,"1", dSpeed, dMax));
            app.execute(new Parent(account ,"2", dSpeed, dMax));
            for (int i = 1; i <= c; i++) {
                threadname = Integer.toString(i);
                app.execute(new Child(account, threadname, wSpeed, wMax));
            }
        }
        catch(Exception ex) {ex.printStackTrace();}
        app.shutdown();
    }
}