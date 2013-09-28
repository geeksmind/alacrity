package net.geeksmind.alacrity.shieldComm;

/**
 * Author: coderh
 * Date: 9/25/13
 * Time: 11:34 PM
 */
public class ShieldComm {
    public static void syncArduino(OnTaskCompleted listener, String ipAddr) {
        new HttpGetTask(listener).execute(ipAddr);
    }
}
