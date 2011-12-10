package cron;

import it.sauronsoftware.cron4j.Scheduler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public final class Main3 {

    private Scheduler scheduler;

    public void start() {

        scheduler = new Scheduler();
        scheduler.scheduleFile(new File("conf/crontab.txt"));

        startCUI();
    }

    public void startCUI() {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        while (true) {
            System.out.print("> ");
            String line = null;
            try {
                line = reader.readLine();
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
            if (line != null && line.toLowerCase().equals("exit")) {
                if (scheduler.isStarted()) {
                    scheduler.stop();
                }
                break;
            } else if (line != null && line.toLowerCase().equals("start")) {
                if (scheduler.isStarted()) {
                    System.out.println("scheduler is already running.");
                } else {
                    scheduler.start();
                    System.out.println("scheduler started.");
                }
            } else if (line != null && line.toLowerCase().equals("stop")) {
                if (scheduler.isStarted()) {
                    scheduler.stop();
                    System.out.println("scheduler stoped.");
                } else {
                    System.out.println("scheduler already stop.");
                }
            } else if (line != null && line.toLowerCase().equals("help")) {
                String help =
                    "exit   exit\n"
                  + "start  start scheduler\n"
                  + "stop   stop scheduler\n"
                  + "help   show help\n"
                  ;
                System.out.println(help);
            }
        }
    }

    public static void main(String[] args) {
        new Main3().start();
    }
}
