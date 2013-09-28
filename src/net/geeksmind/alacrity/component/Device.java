package net.geeksmind.alacrity.component;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

/**
 * Author: coderh
 * Date: 9/27/13
 * Time: 5:26 PM
 */

public class Device {

    private JSONObject jsonObject;
    private String type;
    private String name;
    private int pin;

    public Device(String tp, String nm, int p) {
        type = tp;
        name = nm;
        pin = p;
    }

    public Device(JSONObject json) throws JSONException {
        jsonObject = json;
        type = jsonObject.getString("type");
        name = jsonObject.getString("name");
        pin = jsonObject.getInt("pin");
    }

    @Override
    public String toString() {
        return jsonObject.toString();
    }

    public int getPin(){
        return pin;
    }

}
