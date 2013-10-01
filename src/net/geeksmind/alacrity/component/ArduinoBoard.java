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


    public static ArduinoBoard getInstance() {
        if (instance == null)
            instance = new ArduinoBoard();

        return instance;
    }

    public void init(String json) throws JSONException {
        JSONObject board = new JSONObject(json);
        ipAddr = board.getString("ip");
        JSONArray deviceArray = board.getJSONArray("devices");
        for (int i = 0; i < deviceArray.length(); i++) {
            Log.d("json", "json = " + new Device(deviceArray.getJSONObject(i)).toString());
            deviceList.add(new Device(deviceArray.getJSONObject(i)));
        }
        Log.d("json", "dvc list leng = " + deviceList.size());
    }

    @Override
    public String toString() {
        String deviceListStr = "";
        for (Device dev : deviceList) {
            deviceListStr += dev.toString();
        }
        return "{ \"ip\":\"" + ipAddr + "\"," + "[" + deviceListStr + "] }";
    }
}