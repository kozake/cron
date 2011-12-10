package task;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SayHello {

    public static void hello(String[] args) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
//        System.out.println("[" + formatter.format(new Date()) + "] Hello Java!");
        System.out.println("[" + formatter.format(new Date()) + "] Hello Java Advent Calendar 2011!");
    }
}
