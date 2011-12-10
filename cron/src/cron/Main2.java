package cron;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.FileChannel;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

public final class Main2 {

    private Object scheduler;

    public void start() {

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

        try {
            File lib = new File("lib");
            File work = new File("work");
            copyDir(lib, work);

            ClassLoader loader = createClassLoader(work);

            Class<?> clazz = loader.loadClass(
                    "it.sauronsoftware.cron4j.Scheduler");
            scheduler = clazz.newInstance();
            Method method = clazz.getMethod("scheduleFile", File.class);
            method.invoke(scheduler, new File("conf/crontab.txt"));
            method = clazz.getMethod("start");
            method.invoke(scheduler);
            System.out.println("scheduler started");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void stopSchedule() {

        if (scheduler == null) {
            return;
        }
        try {
            Method method = scheduler.getClass().getMethod("stop");
            method.invoke(scheduler);
            scheduler = null;
            System.out.println("scheduler stoped");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean isStarted() {

        if (scheduler == null) {
            return false;
        }
        try {
            Method method = scheduler.getClass().getMethod("isStarted");
            return (Boolean) method.invoke(scheduler);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private static ClassLoader createClassLoader(File dir)
            throws MalformedURLException {

        File[] jarFiles = dir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return (pathname.isFile() &&
                        pathname.getName().endsWith(".jar"));
            }
        });
        URL[] urls = new URL[jarFiles.length];
        for (int i = 0; i < jarFiles.length; i++) {
            urls[i] = jarFiles[i].toURI().toURL();
        }
        return new URLClassLoader(urls);
    }

    public static void copyDir(File srcDir, File destDir) throws IOException {
        assert (srcDir.isDirectory());
        assert (destDir.isDirectory());

        File[] files = srcDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                File mkDir = new File(destDir.getAbsolutePath()
                        + File.separator + file.getName());
                if (mkDir.mkdir()) {
                    copyDir(file, mkDir);
                }
            } else {
                copyFile(file, new File(destDir.getAbsolutePath()
                        + File.separator + file.getName()));
            }
        }
    }

    public static void copyFile(File in, File out) throws IOException {
        FileChannel sourceChannel = null;
        FileChannel destinationChannel = null;
        try {
            sourceChannel = new FileInputStream(in).getChannel();
            destinationChannel = new FileOutputStream(out).getChannel();
            sourceChannel.transferTo(0, sourceChannel.size(),
                    destinationChannel);
        } finally {
            if (sourceChannel != null) {
                sourceChannel.close();
            }
            if (destinationChannel != null) {
                destinationChannel.close();
            }
        }
    }

    public static void main(String[] args) {
        new Main2().start();
    }
}
