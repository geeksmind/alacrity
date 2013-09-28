package net.geeksmind.alacrity.console;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import net.geeksmind.alacrity.R;
import net.geeksmind.alacrity.component.Device;
import net.geeksmind.alacrity.shieldComm.OnTaskCompleted;
import net.geeksmind.alacrity.shieldComm.ShieldComm;
import org.json.JSONException;
import org.json.JSONObject;

public class consoleActivity extends Activity {

    // Constants
    private static final String CLEAR_IP = " . . . ";
    private static final String DEFAULT_PREF_IP = "192.168.0.1";
    private static final String PREFS_NAME = "PrefsFile";
    private static final String DEFAULT_IP_ADDR_KEY = "defaultIpAddr";
    private static final String OPENING_IP_ADDR_KEY = "openingIpAddr";

    // GUI Widgets
    private Button clearButton;
    private Button resetButton;
    private Button setAsDefaultButton;
    private Button syncButton;
    private Button emitButton;
    private EditText edtTextIpAddrChunk1;
    private EditText edtTextIpAddrChunk2;
    private EditText edtTextIpAddrChunk3;
    private EditText edtTextIpAddrChunk4;
    private RadioGroup cmdOptionsRadioGroup;
    private LinearLayout layoutIpAddr;

    // Device
    private Device dvc;

    //TODO: process device lists

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        syncButton = (Button) this.findViewById(R.id.buttonSync);
        clearButton = (Button) this.findViewById(R.id.buttonClear);
        resetButton = (Button) this.findViewById(R.id.buttonReset);
        emitButton = (Button) this.findViewById(R.id.buttonEmit);
        setAsDefaultButton = (Button) this.findViewById(R.id.buttonSetAsDefault);
        edtTextIpAddrChunk1 = (EditText) this.findViewById(R.id.editViewIpAddr1);
        edtTextIpAddrChunk2 = (EditText) this.findViewById(R.id.editViewIpAddr2);
        edtTextIpAddrChunk3 = (EditText) this.findViewById(R.id.editViewIpAddr3);
        edtTextIpAddrChunk4 = (EditText) this.findViewById(R.id.editViewIpAddr4);
        cmdOptionsRadioGroup = (RadioGroup) this.findViewById(R.id.radioGroupOptions);
        layoutIpAddr = (LinearLayout) this.findViewById(R.id.linearLayoutIpChunkList);

        addListenersToIpChunks(edtTextIpAddrChunk1, edtTextIpAddrChunk2, edtTextIpAddrChunk3, edtTextIpAddrChunk4);
        setIPAddrToGUI(getPrefIpAddr(OPENING_IP_ADDR_KEY));

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIPAddrToGUI(CLEAR_IP);
                edtTextIpAddrChunk1.requestFocus();
                // show soft keyboard for first IP seg, when cleanButton pushed
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edtTextIpAddrChunk1, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIPAddrToGUI(getPrefIpAddr(DEFAULT_IP_ADDR_KEY));
                edtTextIpAddrChunk1.requestFocus();
                // hide soft keyboard for first IP seg, when resetButton pushed
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtTextIpAddrChunk1.getWindowToken(), 0);
            }
        });

        setAsDefaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipAddr = getIPAddrFromGUI();
                setPrefIpAddr(DEFAULT_IP_ADDR_KEY, ipAddr);
                String msg = "Default IPAddr is set to " + ipAddr;
                showToast(msg);
            }
        });

        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://" + getIPAddrFromGUI();

                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    ShieldComm.syncArduino(new OnTaskCompleted() {
                        @Override
                        public void onTaskCompleted(String res) {
                            try {
                                dvc = new Device(new JSONObject(res));
                                showToast(dvc.toString());
                            } catch (JSONException e) {
                                showToast("JSONObject paring error occurs");
                            }
                        }
                    }, url);
                } else {
                    showToast("No available network");
                }
            }
        });

        emitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkRadioButtonId = cmdOptionsRadioGroup.getCheckedRadioButtonId();
                RadioButton cmdRadioButton = (RadioButton) cmdOptionsRadioGroup.findViewById(checkRadioButtonId);
                int cmdOptionIndex = cmdOptionsRadioGroup.indexOfChild(cmdRadioButton);
//                String msg = "Send to : " + getIPAddrFromGUI() + " , with cmd : " + cmdOptionIndex;
                String msg = getIPAddrFromGUI() + "/?out=" + dvc.getPin() + "&status=" + cmdOptionIndex;
                showToast(msg);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        setPrefIpAddr(OPENING_IP_ADDR_KEY, getIPAddrFromGUI());
    }

    EditText currentEditText = null;

    public void addListenersToIpChunks(EditText... editTexts) {

        for (final EditText editText : editTexts) {

            // ip seg focus shifting
            editText.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            int edtViewIndex = layoutIpAddr.indexOfChild(editText);
                            // shift focus to the next editText which is not the last one
                            if (edtViewIndex < layoutIpAddr.getChildCount() - 1 && editText == currentEditText) {
                                layoutIpAddr.getChildAt(edtViewIndex + 2).requestFocus();
                            }
                            return true;
                    }
                    return false;
                }
            });

            // remove leading zeros when focus changes
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    String ipChunkText = editText.getText().toString();
                    if (!editText.isFocused() && ipChunkText.matches("(0{2}\\d|0\\d|0\\d{2})")) { // when editText lose focus and has leading zeros
                        editText.setText(ipChunkText.replaceFirst("^0+(?!$)", ""));
                    }
                }
            });

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Empty
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    currentEditText = editText;
                    // ipChunk local check, show error when the ipChunk is invalid
                    if (!isIPChunkValid(editText.getText().toString())) {
                        editText.setError("between 0 and 255");
                    } else {
                        editText.setError(null);
                    }

                    // detect GUI errors, e.g. any IpChunk invalid, then disable buttons
                    if (isExistGUIErrors()) {
                        setButtonsEnable(false);
                    } else {
                        setButtonsEnable(true);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Empty
                }
            });
        }
    }

    public void showToast(String msg) {
        Toast toast = Toast.makeText(consoleActivity.this.getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public String getPrefIpAddr(String prefKey) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(prefKey, DEFAULT_PREF_IP);
    }

    public void setPrefIpAddr(String prefKey, String ip) {
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(prefKey, ip);
        // Commit the edits!
        editor.commit();
    }

    public void setButtonsEnable(boolean enable) {
        setAsDefaultButton.setEnabled(enable);
        emitButton.setEnabled(enable);
    }

    public boolean isExistGUIErrors() {
        String[] ipAddrChunkList = getIPAddrChunkListFromView();
        return !isIPChunkListValid(ipAddrChunkList);
    }

    public void setIPAddrToGUI(String ipAddr) {
        String[] ipChunkList = ipAddr.split("\\.");
        edtTextIpAddrChunk1.setText(ipChunkList[0].trim());
        edtTextIpAddrChunk2.setText(ipChunkList[1].trim());
        edtTextIpAddrChunk3.setText(ipChunkList[2].trim());
        edtTextIpAddrChunk4.setText(ipChunkList[3].trim());  // remove spaces for Int parsing
    }

    public String makeString(String[] origin, String delimiter) {
        String retVal = "";
        for (int i = 0; i < origin.length - 1; i++) {
            retVal += origin[i] + delimiter;
        }
        return retVal + origin[origin.length - 1];
    }

    public String getIPAddrFromGUI() {
        String[] ipAddrChunkList = getIPAddrChunkListFromView();
        return makeString(ipAddrChunkList, ".");
    }

    public String[] getIPAddrChunkListFromView() {
        return new String[]{
                edtTextIpAddrChunk1.getText().toString(),
                edtTextIpAddrChunk2.getText().toString(),
                edtTextIpAddrChunk3.getText().toString(),
                edtTextIpAddrChunk4.getText().toString()
        };
    }

    public boolean isIPChunkValid(String chunk) {
        if (chunk.trim().equals(""))
            return false;
        int chunkNumber = Integer.parseInt(chunk);
        return chunkNumber <= 255 && chunkNumber >= 0;
    }

    public boolean isIPChunkListValid(String[] chunkList) {
        for (String chunk : chunkList) {
            if (!isIPChunkValid(chunk)) {
                return false;
            }
        }
        return true;
    }
}