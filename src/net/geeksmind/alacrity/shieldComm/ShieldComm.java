package net.geeksmind.alacrity.shieldComm;

/**
 *
 * Author: coderh
 * Date: 9/25/13
 * Time: 11:34 PM
 *
 */
public class ShieldComm {

    public static String syncArduino(String url) {
        GetTask syncArduinoTask = (GetTask) new GetTask().execute(url);
        return syncArduinoTask.getMsgRcv();
    }

}
