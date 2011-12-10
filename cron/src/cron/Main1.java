package cron;

import it.sauronsoftware.cron4j.Scheduler;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

public final class Main1 {

    private Scheduler scheduler;

    public void start() {

        scheduler = new Scheduler();
        scheduler.scheduleFile(new File("conf/crontab.txt"));

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                try {
                    createTray();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
        });
    }

    private void createTray() throws AWTException, IOException {

        SystemTray tray = SystemTray.getSystemTray();
        tray.add(createTrayIcon());
    }

    private TrayIcon createTrayIcon() throws IOException {

        final Image startImage = ImageIO.read(
                getClass().getResource("/rbs.gif"));
        final Image stopImage = ImageIO.read(
                getClass().getResource("/rbrs.gif"));

        PopupMenu menu = new PopupMenu();
        final TrayIcon trayIcon = new TrayIcon(
                stopImage, "cron(not running)", menu);

        final MenuItem manuOnOff = new MenuItem("start");
        final MenuItem menuExit = new MenuItem("exit");

        manuOnOff.setEnabled(true);
        menuExit.setEnabled(true);

        manuOnOff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isStarted()) {
                    stopSchedule();
                    trayIcon.setImage(stopImage);
                    trayIcon.setToolTip("cron(not running)");
                    manuOnOff.setLabel("start");
                    menuExit.setEnabled(true);
                } else {
                    startSchedule();
                    trayIcon.setImage(startImage);
                    trayIcon.setToolTip("cron(running)");
                    manuOnOff.setLabel("stop");
                    menuExit.setEnabled(false);
                }
            }
        });
        menuExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menu.add(manuOnOff);
        menu.add(menuExit);

        return trayIcon;
    }

    private void startSchedule() {
        scheduler.start();
    }
    private void stopSchedule() {
        scheduler.stop();
    }

    private boolean isStarted() {
        return scheduler.isStarted();
    }

    public static void main(String[] args) {
        new Main1().start();
    }
}
