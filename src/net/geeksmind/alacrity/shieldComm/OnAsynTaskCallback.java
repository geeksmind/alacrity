package net.geeksmind.alacrity.shieldComm;

/**
 * Author: coderh
 * Date: 9/27/13
 * Time: 10:35 AM
 */
public interface OnAsynTaskCallback {
    void onTaskCompleted(String res);
    void onTaskStarted();
}