package net.geeksmind.alacrity.shieldComm;

/**
 * Author: coderh
 * Date: 9/25/13
 * Time: 11:34 PM
 */
public class ShieldComm {
    public static void syncArduino(OnAsynTaskCallback listener, String url) {
        new HttpGetTask(listener).execute(url);
    }

    public static void emitArduino(OnAsynTaskCallback listener, String url) {
        new HttpGetTask(listener).execute(url);
    }
}
