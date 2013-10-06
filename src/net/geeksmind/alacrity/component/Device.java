package net.geeksmind.alacrity.component;

import org.json.JSONException;
import org.json.JSONObject;

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
    private boolean status;

    public void setStatus(boolean status) {
        try {
            jsonObject.put("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }

//    public Device(String type, String name, int pin, boolean status) {
//        this.type = type;
//        this.name = name;
//        this.pin = pin;
//        this.status = status;
//    }

    public Device(JSONObject json) throws JSONException {
        jsonObject = json;
        type = jsonObject.getString("type");
        name = jsonObject.getString("name");
        pin = jsonObject.getInt("pin");
        status = jsonObject.getBoolean("status");
    }

    @Override
    public String toString() {
        return jsonObject.toString();
    }

    public int getPin(){
        return pin;
    }

    public String getName(){
        return name;
    }

    public String getType(){
        return type;
    }

}
