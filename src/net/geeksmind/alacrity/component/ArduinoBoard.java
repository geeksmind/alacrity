package net.geeksmind.alacrity.component;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: coderh
 * Date: 10/1/13
 * Time: 11:24 PM
 */
public class ArduinoBoard {

    private String ipAddr = "";
    private List<Device> deviceList = new ArrayList<Device>();
    private static ArduinoBoard instance = null;

    public void setDevStatusByIndex(int i, boolean stat) {
        deviceList.get(i).setStatus(stat);
    }

    public List<Device> getDeviceList() {
        return deviceList;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public static ArduinoBoard getInstance() {
        if (instance == null)
            instance = new ArduinoBoard();
        return instance;
    }

    public void init(String json) throws JSONException {
        deviceList.clear();
        JSONObject board = new JSONObject(json);
        ipAddr = board.getString("ip");

        JSONArray deviceArray = board.getJSONArray("devices");
        for (int i = 0; i < deviceArray.length(); i++) {
            Log.d("json", "json = " + new Device(deviceArray.getJSONObject(i)).toString());
            deviceList.add(new Device(deviceArray.getJSONObject(i)));
        }
    }

    public String generateURL() {
        String retVal = ipAddr + "/?";
        for (Device dev : deviceList) {
            retVal += "pin=" + dev.getPin() + "&";
            retVal += "status=" + dev.getStatus() + "&";
        }
//        return "http://" + retVal.substring(0, retVal.length() - 1);
        return "http://" + retVal.substring(0, retVal.length() - 1);
    }

    @Override
    public String toString() {
        String deviceListStr = "";
        for (Device dev : deviceList) {
            deviceListStr += dev.toString();
        }
        return "{ \"ip\":\"" + ipAddr + "\",\n" + "[" + deviceListStr + "] }";
    }
}