package cron;

import it.sauronsoftware.cron4j.Scheduler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public final class Main {

    private Scheduler scheduler;

    public void start() {

        scheduler = new Scheduler();
        scheduler.scheduleFile(new File("conf/crontab.txt"));
        scheduler.start();

        System.out.println("exit on Enter.");
        waitOnEnter();

        scheduler.stop();
    }

    public void waitOnEnter() {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        try {
            reader.readLine();
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
    }

    public static void main(String[] args) {
        new Main().start();
    }
}
